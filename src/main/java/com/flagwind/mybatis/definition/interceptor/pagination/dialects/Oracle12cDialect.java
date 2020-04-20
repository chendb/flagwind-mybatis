
package com.flagwind.mybatis.definition.interceptor.pagination.dialects;

import com.flagwind.mybatis.definition.interceptor.pagination.DialectModel;

/**
 * Oracle 新版数据库分页语句组装实现
 *
 * @author chendb
 */
public class Oracle12cDialect implements IDialect {

    @Override
    public DialectModel buildPaginationSql(String originalSql, long offset, long limit) {
        String sql = originalSql + " OFFSET " + FIRST_MARK + " ROWS FETCH NEXT " + SECOND_MARK + " ROWS ONLY ";
        return new DialectModel(sql, offset, limit).setConsumerChain();
    }
}
