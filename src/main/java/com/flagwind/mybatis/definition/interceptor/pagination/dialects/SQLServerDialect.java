
package com.flagwind.mybatis.definition.interceptor.pagination.dialects;

import com.flagwind.mybatis.definition.interceptor.pagination.DialectModel;


public class SQLServerDialect implements IDialect {

    @Override
    public DialectModel buildPaginationSql(String originalSql, long offset, long limit) {
        String sql = originalSql + " OFFSET " + FIRST_MARK + " ROWS FETCH NEXT " + SECOND_MARK + " ROWS ONLY";
        return new DialectModel(sql, offset, limit).setConsumerChain();
    }
}
