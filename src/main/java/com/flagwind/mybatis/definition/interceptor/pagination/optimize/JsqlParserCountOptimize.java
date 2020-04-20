
package com.flagwind.mybatis.definition.interceptor.pagination.optimize;


import com.flagwind.mybatis.definition.parser.ISqlParser;
import com.flagwind.mybatis.definition.parser.SqlInfo;
import com.flagwind.mybatis.utils.CollectionUtils;
import com.flagwind.mybatis.utils.SqlParserUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.*;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.reflection.MetaObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JsqlParser Count Optimize
 *
 * @author hubin
 * @since 2017-06-20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JsqlParserCountOptimize implements ISqlParser {
    private static final List<SelectItem> COUNT_SELECT_ITEM = countSelectItem();
    private final Log logger = LogFactory.getLog(JsqlParserCountOptimize.class);

    private boolean optimizeJoin = false;

    /**
     * 获取jsqlparser中count的SelectItem
     */
    private static List<SelectItem> countSelectItem() {
        Function function = new Function();
        function.setName("COUNT");
        List<Expression> expressions = new ArrayList<>();
        LongValue longValue = new LongValue(1);
        ExpressionList expressionList = new ExpressionList();
        expressions.add(longValue);
        expressionList.setExpressions(expressions);
        function.setParameters(expressionList);
        List<SelectItem> selectItems = new ArrayList<>();
        SelectExpressionItem selectExpressionItem = new SelectExpressionItem(function);
        selectItems.add(selectExpressionItem);
        return selectItems;
    }


    @Override
    public SqlInfo parser(MetaObject metaObject, String sql) {
        if (logger.isDebugEnabled()) {
            logger.debug("JsqlParserCountOptimize sql=" + sql);
        }
        SqlInfo sqlInfo = SqlInfo.newInstance();
        try {
            Select selectStatement = (Select) CCJSqlParserUtil.parse(sql);
            PlainSelect plainSelect = (PlainSelect) selectStatement.getSelectBody();
            Distinct distinct = plainSelect.getDistinct();
            GroupByElement groupBy = plainSelect.getGroupBy();
            List<OrderByElement> orderBy = plainSelect.getOrderByElements();

            // 添加包含groupBy 不去除orderBy
            if (null == groupBy && CollectionUtils.isNotEmpty(orderBy)) {
                plainSelect.setOrderByElements(null);
                sqlInfo.setOrderBy(false);
            }
            //#95 Github, selectItems contains #{} ${}, which will be translated to ?, and it may be in a function: power(#{myInt},2)
            for (SelectItem item : plainSelect.getSelectItems()) {
                if (item.toString().contains("?")) {
                    return sqlInfo.setSql(SqlParserUtils.getOriginalCountSql(selectStatement.toString()));
                }
            }
            // 包含 distinct、groupBy不优化
            if (distinct != null || null != groupBy) {
                return sqlInfo.setSql(SqlParserUtils.getOriginalCountSql(selectStatement.toString()));
            }
            // 包含 join 连表,进行判断是否移除 join 连表
            List<Join> joins = plainSelect.getJoins();
            if (optimizeJoin && CollectionUtils.isNotEmpty(joins)) {
                boolean canRemoveJoin = true;
                String whereS = Optional.ofNullable(plainSelect.getWhere()).map(Expression::toString).orElse("");
                for (Join join : joins) {
                    if (!join.isLeft()) {
                        canRemoveJoin = false;
                        break;
                    }
                    Table table = (Table) join.getRightItem();
                    String str = Optional.ofNullable(table.getAlias()).map(Alias::getName).orElse(table.getName()) + ",";
                    String onExpressionS = join.getOnExpression().toString();
                    /* 如果 join 里包含 ?(代表有入参) 或者 where 条件里包含使用 join 的表的字段作条件,就不移除 join */
                    if (onExpressionS.contains("?") || whereS.contains(str)) {
                        canRemoveJoin = false;
                        break;
                    }
                }
                if (canRemoveJoin) {
                    plainSelect.setJoins(null);
                }
            }
            // 优化 SQL
            plainSelect.setSelectItems(COUNT_SELECT_ITEM);
            return sqlInfo.setSql(selectStatement.toString());
        } catch (Throwable e) {
            // 无法优化使用原 SQL
            return sqlInfo.setSql(SqlParserUtils.getOriginalCountSql(sql));
        }
    }
}
