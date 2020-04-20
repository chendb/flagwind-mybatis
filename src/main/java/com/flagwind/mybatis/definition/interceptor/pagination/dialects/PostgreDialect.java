
package com.flagwind.mybatis.definition.interceptor.pagination.dialects;


import com.flagwind.mybatis.definition.interceptor.pagination.DialectModel;


public class PostgreDialect implements IDialect {

    @Override
    public DialectModel buildPaginationSql(String originalSql, long offset, long limit) {
        String sql = originalSql + " limit " + FIRST_MARK + " offset " + SECOND_MARK;
        return new DialectModel(sql, limit, offset).setConsumerChain();
    }
}
