
package com.flagwind.mybatis.definition.interceptor.pagination.dialects;

import com.flagwind.mybatis.definition.interceptor.pagination.DialectModel;

/**
 * MySQL 数据库分页语句组装实现
 *
 * @author chendb
 */
public class MySqlDialect implements IDialect {

    @Override
    public DialectModel buildPaginationSql(String originalSql, long offset, long limit) {
        String sql = originalSql + " LIMIT " + FIRST_MARK + "," + SECOND_MARK;
        return new DialectModel(sql, offset, limit).setConsumerChain();
    }
}
