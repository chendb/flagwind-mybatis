
package com.flagwind.mybatis.definition.interceptor.pagination.dialects;


import com.flagwind.mybatis.definition.interceptor.pagination.DialectModel;
import org.apache.commons.lang3.StringUtils;


public class SQLServer2005Dialect implements IDialect {

    private static String getOrderByPart(String sql) {
        String loweredString = sql.toLowerCase();
        int orderByIndex = loweredString.indexOf("order by");
        if (orderByIndex != -1) {
            return sql.substring(orderByIndex);
        } else {
            return "";
        }
    }

    @Override
    public DialectModel buildPaginationSql(String originalSql, long offset, long limit) {
        StringBuilder pagingBuilder = new StringBuilder();
        String orderby = getOrderByPart(originalSql);
        String distinctStr = "";

        String loweredString = originalSql.toLowerCase();
        String sqlPartString = originalSql;
        if (loweredString.trim().startsWith("select")) {
            int index = 6;
            if (loweredString.startsWith("select distinct")) {
                distinctStr = "DISTINCT ";
                index = 15;
            }
            sqlPartString = sqlPartString.substring(index);
        }
        pagingBuilder.append(sqlPartString);

        // if no ORDER BY is specified use fake ORDER BY field to avoid errors
        if (StringUtils.isBlank(orderby)) {
            orderby = "ORDER BY CURRENT_TIMESTAMP";
        }
        long firstParam = offset + 1;
        long secondParam = offset + limit;
        String sql = "WITH selectTemp AS (SELECT " + distinctStr + "TOP 100 PERCENT " +
            " ROW_NUMBER() OVER (" + orderby + ") as __row_number__, " + pagingBuilder +
            ") SELECT * FROM selectTemp WHERE __row_number__ BETWEEN " +
            //FIX#299：原因：mysql中limit 10(offset,size) 是从第10开始（不包含10）,；而这里用的BETWEEN是两边都包含，所以改为offset+1
            firstParam + " AND " + secondParam + " ORDER BY __row_number__";
        return new DialectModel(sql, firstParam, secondParam);
    }
}
