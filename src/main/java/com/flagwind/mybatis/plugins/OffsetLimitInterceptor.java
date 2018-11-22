package com.flagwind.mybatis.plugins;

import java.lang.reflect.Constructor;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.flagwind.mybatis.code.DialectType;
import com.flagwind.mybatis.plugins.paginator.PageBounds;
import com.flagwind.mybatis.plugins.paginator.helpers.PropertiesHelper;
import com.flagwind.mybatis.plugins.paginator.helpers.SQLHelper;
import com.flagwind.persistent.model.Paging;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.MappedStatement.Builder;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import com.flagwind.mybatis.plugins.paginator.dialects.base.DB2Dialect;
import com.flagwind.mybatis.plugins.paginator.dialects.Dialect;
import com.flagwind.mybatis.plugins.paginator.dialects.base.H2Dialect;
import com.flagwind.mybatis.plugins.paginator.dialects.base.HSQLDialect;
import com.flagwind.mybatis.plugins.paginator.dialects.base.MySQLDialect;
import com.flagwind.mybatis.plugins.paginator.dialects.base.OracleDialect;
import com.flagwind.mybatis.plugins.paginator.dialects.base.PostgreSQLDialect;
import com.flagwind.mybatis.plugins.paginator.dialects.base.SQLServer2005Dialect;
import com.flagwind.mybatis.plugins.paginator.dialects.base.SQLServerDialect;
import com.flagwind.mybatis.plugins.paginator.dialects.base.SybaseDialect;
import com.flagwind.persistent.model.Sorting;

/**
 * 为MyBatis提供基于方言(Dialect)的分页查询的插件 将拦截Executor.query()方法实现分页方言的插入.
 * @author hbche
 */

@Intercepts({
        @Signature(type = Executor.class,
                method = "query",
                args = { MappedStatement.class, Object.class,RowBounds.class, ResultHandler.class })
})
public class OffsetLimitInterceptor implements Interceptor {
    private static Log LOG = LogFactory.getLog(OffsetLimitInterceptor.class);
    private final static int MAPPED_STATEMENT_INDEX = 0;
    private final static int PARAMETER_INDEX = 1;
    private final static int ROWBOUNDS_INDEX = 2;
    // private static int RESULT_HANDLER_INDEX = 3;

    private static ExecutorService Pool;
    // String dialectClass;
    String dialect;
    private boolean asyncTotalCount = false;




    public void setDialect(String dialect) {
        this.dialect = dialect;
    }



    @Override
    public Object intercept(final Invocation invocation) throws Throwable {
        final Executor executor = (Executor) invocation.getTarget();
        final Object[] queryArgs = invocation.getArgs();
        final MappedStatement ms = (MappedStatement) queryArgs[MAPPED_STATEMENT_INDEX];
        Object parameterObject =  queryArgs[PARAMETER_INDEX];
        if(!(parameterObject instanceof HashMap)){
          return invocation.proceed();
        }
        final HashMap parameter = (HashMap) parameterObject;
        final RowBounds rowBounds = (RowBounds) queryArgs[ROWBOUNDS_INDEX];
        PageBounds pageBounds = new PageBounds(rowBounds);

        // if (parameter == null) {
        //     return invocation.proceed();
        // }

        Paging paging = (Paging) parameter.getOrDefault("_paging", null);

        if (paging != null) {
            pageBounds = new PageBounds(paging.getPageIndex().intValue(), paging.getPageSize().intValue());
            queryArgs[ROWBOUNDS_INDEX] = pageBounds;
        } else {

            Integer startIndex = (Integer) parameter.getOrDefault("_startIndex", -1);
            Integer endIndex = (Integer) parameter.getOrDefault("_endIndex", -1);
            if (startIndex >= 0 && endIndex >= 0) {

                pageBounds = new PageBounds(startIndex, endIndex);
                pageBounds.setContainsTotalCount(false);
                queryArgs[ROWBOUNDS_INDEX] = pageBounds;
            }

        }

        Sorting[] sortings = (Sorting[]) parameter.getOrDefault("_sorts", null);
        if (sortings != null) {
            pageBounds.setOrders(Arrays.asList(sortings));
            queryArgs[ROWBOUNDS_INDEX] = pageBounds;
        }

        if (pageBounds.getOffset() == RowBounds.NO_ROW_OFFSET && pageBounds.getLimit() == RowBounds.NO_ROW_LIMIT
                && pageBounds.getOrders().isEmpty()) {
            return invocation.proceed();
        }

        final Dialect dialect;
        Class clazz = getDialect();
        try {
            Constructor constructor = clazz.getConstructor(MappedStatement.class, Object.class, PageBounds.class);
            dialect = (Dialect) constructor.newInstance(new Object[]{ms, parameter, pageBounds});
        } catch (Exception e) {
            throw new ClassNotFoundException("Cannot create dialect instance: " + clazz, e);
        }

        final BoundSql boundSql = ms.getBoundSql(parameter);

        queryArgs[MAPPED_STATEMENT_INDEX] = copyFromNewSql(ms, boundSql, dialect.getPageSQL(),
                dialect.getParameterMappings(), dialect.getParameterObject());
        queryArgs[PARAMETER_INDEX] = dialect.getParameterObject();
        queryArgs[ROWBOUNDS_INDEX] = new RowBounds(RowBounds.NO_ROW_OFFSET, RowBounds.NO_ROW_LIMIT);

        if (pageBounds.isContainsTotalCount()) {
            int count = getCount(ms, executor, parameter, boundSql, dialect);
            assert paging != null;
            paging.setTotalCount((long) count);
        }

        return invocation.proceed();

    }

    private Class<?> getDialect() {

        if (dialect.equalsIgnoreCase(DialectType.DB2.name())) {
            return DB2Dialect.class;
        }
        if (dialect.equalsIgnoreCase(DialectType.H2.name())) {
            return H2Dialect.class;
        }
        if (dialect.equalsIgnoreCase(DialectType.HSQL.name())) {
            return HSQLDialect.class;
        }
        if (dialect.equalsIgnoreCase(DialectType.MySQL.name())) {
            return MySQLDialect.class;
        }
        if (dialect.equalsIgnoreCase(DialectType.Oralce.name())) {
            return OracleDialect.class;
        }
        if (dialect.equalsIgnoreCase(DialectType.PostgreSQL.name())) {
            return PostgreSQLDialect.class;
        }
        if (dialect.equalsIgnoreCase(DialectType.SQLServer2005.name())) {
            return SQLServer2005Dialect.class;
        }
        if (dialect.equalsIgnoreCase(DialectType.SQLServer.name())) {
            return SQLServerDialect.class;
        }
        if (dialect.equalsIgnoreCase(DialectType.Sybase.name())) {
            return SybaseDialect.class;
        }
        return HSQLDialect.class;
    }

    private int getCount(MappedStatement ms, Executor executor, HashMap<?, ?> parameter, BoundSql boundSql,
            Dialect dialect) throws SQLException {
        Integer count;
        Cache cache = ms.getCache();
        if (cache != null && ms.isUseCache() && ms.getConfiguration().isCacheEnabled()) {
            CacheKey cacheKey = executor.createCacheKey(ms, parameter, new PageBounds(), copyFromBoundSql(ms, boundSql,
                    dialect.getCountSQL(), boundSql.getParameterMappings(), boundSql.getParameterObject()));
            count = (Integer) cache.getObject(cacheKey);
            if (count == null) {
                count = SQLHelper.getCount(ms, executor.getTransaction(), parameter, boundSql, dialect);
                cache.putObject(cacheKey, count);
            }
        }
        else {
            count = SQLHelper.getCount(ms, executor.getTransaction(), parameter, boundSql, dialect);
        }
        return count;
    }

    private MappedStatement copyFromNewSql(MappedStatement ms, BoundSql boundSql, String sql,
            List<ParameterMapping> parameterMappings, Object parameter) {
        BoundSql newBoundSql = copyFromBoundSql(ms, boundSql, sql, parameterMappings, parameter);
        return copyFromMappedStatement(ms, new BoundSqlSqlSource(newBoundSql));
    }

    private BoundSql copyFromBoundSql(MappedStatement ms, BoundSql boundSql, String sql,
            List<ParameterMapping> parameterMappings, Object parameter) {
        BoundSql newBoundSql = new BoundSql(ms.getConfiguration(), sql, parameterMappings, parameter);
        for (ParameterMapping mapping : boundSql.getParameterMappings()) {
            String prop = mapping.getProperty();
            if (boundSql.hasAdditionalParameter(prop)) {
                newBoundSql.setAdditionalParameter(prop, boundSql.getAdditionalParameter(prop));
            }
        }
        return newBoundSql;
    }


    private MappedStatement copyFromMappedStatement(MappedStatement ms, SqlSource newSqlSource) {
        Builder builder = new Builder(ms.getConfiguration(), ms.getId(), newSqlSource, ms.getSqlCommandType());

        builder.resource(ms.getResource());
        builder.fetchSize(ms.getFetchSize());
        builder.statementType(ms.getStatementType());
        builder.keyGenerator(ms.getKeyGenerator());
        if (ms.getKeyProperties() != null && ms.getKeyProperties().length != 0) {
            StringBuilder keyProperties = new StringBuilder();
            for (String keyProperty : ms.getKeyProperties()) {
                keyProperties.append(keyProperty).append(",");
            }
            keyProperties.delete(keyProperties.length() - 1, keyProperties.length());
            builder.keyProperty(keyProperties.toString());
        }

        // setStatementTimeout()
        builder.timeout(ms.getTimeout());

        // setStatementResultMap()
        builder.parameterMap(ms.getParameterMap());

        // setStatementResultMap()
        builder.resultMaps(ms.getResultMaps());
        builder.resultSetType(ms.getResultSetType());

        // setStatementCache()
        builder.cache(ms.getCache());
        builder.flushCacheRequired(ms.isFlushCacheRequired());
        builder.useCache(ms.isUseCache());

        return builder.build();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties)
    {
        if(properties == null)
        {
            return;
        }

        PropertiesHelper propertiesHelper = new PropertiesHelper(properties);

        setDialect(DialectType.parse(propertiesHelper.getRequiredString("dialect")).name());

        setAsyncTotalCount(propertiesHelper.getBoolean("async-total-count", false));

        setPoolMaxSize(propertiesHelper.getInt("pool-max-size", 0));

    }

    public static class BoundSqlSqlSource implements SqlSource {
        BoundSql boundSql;

        public BoundSqlSqlSource(BoundSql boundSql) {
            this.boundSql = boundSql;
        }

        @Override
        public BoundSql getBoundSql(Object parameterObject) {
            return boundSql;
        }
    }

    public void setAsyncTotalCount(boolean asyncTotalCount) {
        LOG.debug(String.format("asyncTotalCount: {} ", asyncTotalCount));
        this.asyncTotalCount = asyncTotalCount;
    }

    public void setPoolMaxSize(int poolMaxSize) {

        if (poolMaxSize > 0) {
            LOG.debug(String.format("poolMaxSize: {} ", poolMaxSize));
            Pool = Executors.newFixedThreadPool(poolMaxSize);
        }
        else {
            Pool = Executors.newCachedThreadPool();
        }

    }
}
