package com.flagwind.mybatis.definition.interceptor;

import com.flagwind.mybatis.code.DatabaseType;
import com.flagwind.mybatis.definition.MybatisDefaultParameterHandler;
import com.flagwind.mybatis.definition.interceptor.pagination.DialectFactory;
import com.flagwind.mybatis.definition.interceptor.pagination.DialectModel;
import com.flagwind.mybatis.definition.interceptor.pagination.PageBounds;
import com.flagwind.mybatis.definition.interceptor.pagination.dialects.IDialect;
import com.flagwind.mybatis.definition.parser.AbstractSqlParserHandler;
import com.flagwind.mybatis.definition.parser.SqlInfo;
import com.flagwind.mybatis.exceptions.MapperException;
import com.flagwind.mybatis.utils.CollectionUtils;
import com.flagwind.mybatis.utils.JdbcUtils;
import com.flagwind.mybatis.utils.PluginUtils;
import com.flagwind.mybatis.utils.SqlParserUtils;
import com.flagwind.persistent.model.Paging;
import com.flagwind.persistent.model.Sorting;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.RowBounds;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

/**
 * @author chendb
 * @description: 分页插件
 * @date 2020-04-15 22:48:09
 */
@Setter
@Accessors(chain = true)
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class PaginationInterceptor extends AbstractSqlParserHandler implements Interceptor {

    protected static final Log logger = LogFactory.getLog(PaginationInterceptor.class);



    /**
     * 数据库类型
     *
     * @since 3.3.1
     */
    private DatabaseType dbType;
    /**
     * 方言实现类
     *
     * @since 3.3.1
     */
    private IDialect dialect;


    /**
     * 查询SQL拼接Order By
     *
     * @param originalSql 需要拼接的SQL
     * @param sorts       page对象
     * @return ignore
     */
    public static String concatOrderBy(String originalSql, List<Sorting> sorts) {
        if (!CollectionUtils.isEmpty(sorts)) {
            try {

                Select selectStatement = (Select) CCJSqlParserUtil.parse(originalSql);
                if (selectStatement.getSelectBody() instanceof PlainSelect) {
                    PlainSelect plainSelect = (PlainSelect) selectStatement.getSelectBody();
                    List<OrderByElement> orderByElements = plainSelect.getOrderByElements();
                    List<OrderByElement> orderByElementsReturn = addOrderByElements(sorts, orderByElements);
                    plainSelect.setOrderByElements(orderByElementsReturn);
                    return plainSelect.toString();
                } else if (selectStatement.getSelectBody() instanceof SetOperationList) {
                    SetOperationList setOperationList = (SetOperationList) selectStatement.getSelectBody();
                    List<OrderByElement> orderByElements = setOperationList.getOrderByElements();
                    List<OrderByElement> orderByElementsReturn = addOrderByElements(sorts, orderByElements);
                    setOperationList.setOrderByElements(orderByElementsReturn);
                    return setOperationList.toString();
                } else if (selectStatement.getSelectBody() instanceof WithItem) {
                    // todo: don't known how to resole
                    return originalSql;
                } else {
                    return originalSql;
                }

            } catch (JSQLParserException e) {
                logger.warn("failed to concat orderBy from IPage, exception=" + e.getMessage());
            }
        }
        return originalSql;
    }

    private static List<OrderByElement> addOrderByElements(List<Sorting> sorts, List<OrderByElement> orderByElements) {
        orderByElements = Optional.ofNullable(orderByElements).orElse(new ArrayList<>());

        for (Sorting sorting : sorts) {
            for (String field : sorting.getFields()) {
                OrderByElement element = new OrderByElement();
                element.setExpression(new Column(field));
                element.setAsc(sorting.getMode() == Sorting.SortingMode.Ascending);
                element.setAscDescPresent(true);
                orderByElements.add(element);
            }
        }
        return orderByElements;
    }

    /**
     * Physical Page Interceptor for all the queries with parameter {@link RowBounds}
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = PluginUtils.realTarget(invocation.getTarget());
        MetaObject metaObject = SystemMetaObject.forObject(statementHandler);


        // SQL 解析
        this.sqlParser(metaObject);


        // 先判断是不是SELECT操作  (2019-04-10 00:37:31 跳过存储过程)
        MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("delegate.mappedStatement");


//     MapperTemplate mapperTemplate = templateContext.getMapperTemplateByMsid(mappedStatement.getId());

        if (SqlCommandType.SELECT != mappedStatement.getSqlCommandType()
                || StatementType.CALLABLE == mappedStatement.getStatementType()) {
            return invocation.proceed();
        }

        // 针对定义了rowBounds，做为mapper接口方法的参数
        BoundSql boundSql = (BoundSql) metaObject.getValue("delegate.boundSql");
        Object paramObj = boundSql.getParameterObject();


        if (!(paramObj instanceof HashMap)) {
            return invocation.proceed();
        }

        final HashMap parameter = (HashMap) paramObj;


        Paging paging = (Paging) parameter.getOrDefault("_paging", null);
        PageBounds pageBounds = null != paging ? getPageBoundsByPaging(paging, parameter) : getPageBoundsByRowIndex(parameter);


        if (null == pageBounds) {
            return invocation.proceed();
        }


        String originalSql = boundSql.getSql();
        Connection connection = (Connection) invocation.getArgs()[0];

        if (pageBounds.isContainsTotalCount()) {
            SqlInfo sqlInfo = SqlParserUtils.getOptimizeCountSql(false, null, originalSql);
            this.queryTotal(sqlInfo.getSql(), mappedStatement, boundSql, paging, connection);
//            assert paging != null;
//            if (paging.getTotalCount() <= 0) {
//                return null;
//            }
        }
        DatabaseType dbType = Optional.ofNullable(this.dbType).orElse(JdbcUtils.getDbType(connection.getMetaData().getURL()));
        IDialect dialect = Optional.ofNullable(this.dialect).orElse(DialectFactory.getDialect(dbType));
        String buildSql = concatOrderBy(originalSql, pageBounds.getOrders());
        DialectModel model = dialect.buildPaginationSql(buildSql, pageBounds.getOffset(), pageBounds.getLimit());
        Configuration configuration = mappedStatement.getConfiguration();
        List<ParameterMapping> mappings = new ArrayList<>(boundSql.getParameterMappings());
        Map<String, Object> additionalParameters = (Map<String, Object>) metaObject.getValue("delegate.boundSql.additionalParameters");
        model.consumers(mappings, configuration, additionalParameters);
        metaObject.setValue("delegate.boundSql.sql", model.getDialectSql());
        metaObject.setValue("delegate.boundSql.parameterMappings", mappings);
        return invocation.proceed();
    }

    protected PageBounds getPageBoundsByRowIndex(HashMap parameter) {
        Integer startIndex = (Integer) parameter.getOrDefault("_startIndex", -1);
        Integer endIndex = (Integer) parameter.getOrDefault("_endIndex", -1);
        if (startIndex >= 0 && endIndex >= 0) {
            PageBounds pageBounds = new PageBounds(startIndex, endIndex);
            pageBounds.setContainsTotalCount(false);
            Sorting[] sorts = (Sorting[]) parameter.getOrDefault("_sorts", null);
            if (sorts != null) {
                pageBounds.setOrders(Arrays.asList(sorts));
            }
            return pageBounds;
        }
        return null;
    }

    protected PageBounds getPageBoundsByPaging(Paging paging, HashMap parameter) {

        if (paging != null) {
            PageBounds pageBounds = new PageBounds(paging.getPageIndex().intValue(), paging.getPageSize().intValue());
            Sorting[] sorts = (Sorting[]) parameter.getOrDefault("_sorts", null);
            if (sorts != null) {
                pageBounds.setOrders(Arrays.asList(sorts));
            }
            return pageBounds;

        }
        return null;
    }


    /**
     * 查询总记录条数
     *
     * @param sql             count sql
     * @param mappedStatement MappedStatement
     * @param boundSql        BoundSql
     * @param page            IPage
     * @param connection      Connection
     */
    protected void queryTotal(String sql, MappedStatement mappedStatement, BoundSql boundSql, Paging page, Connection connection) {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            DefaultParameterHandler parameterHandler = new MybatisDefaultParameterHandler(mappedStatement, boundSql.getParameterObject(), boundSql);
            parameterHandler.setParameters(statement);
            long total = 0;
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    total = resultSet.getLong(1);
                }
            }
            page.setTotalCount(total);
        } catch (Exception e) {
            throw new MapperException(String.format("Error: Method queryTotal execution error of sql : \n %s \n", sql), e);
        }
    }


    @Override
    public Object plugin(Object target) {
        if (target instanceof StatementHandler) {
            return Plugin.wrap(target, this);
        }
        return target;
    }

    @Override
    public void setProperties(Properties prop) {
//        String dialectType = prop.getProperty("dialectType");
//        String dialectClazz = prop.getProperty("dialectClazz");
//        if (StringUtils.isNotBlank(dialectType)) {
//            setDialectType(dialectType);
//        }
//        if (StringUtils.isNotBlank(dialectClazz)) {
//            setDialectClazz(dialectClazz);
//        }
    }


}