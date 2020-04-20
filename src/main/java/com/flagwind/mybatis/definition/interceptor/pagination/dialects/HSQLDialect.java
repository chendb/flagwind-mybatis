
package com.flagwind.mybatis.definition.interceptor.pagination.dialects;

import com.flagwind.mybatis.definition.interceptor.pagination.DialectModel;

/**
 * HSQL 数据库分页语句组装实现
 *
 * @author hubin
 * @since 2016-01-23
 */
public class HSQLDialect implements IDialect {

    @Override
    public DialectModel buildPaginationSql(String originalSql, long offset, long limit) {
        String sql = originalSql + " limit " + FIRST_MARK + "," + SECOND_MARK;
        return new DialectModel(sql, offset, limit).setConsumerChain();
    }
}
