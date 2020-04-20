
package com.flagwind.mybatis.definition.interceptor.pagination.dialects;

import com.flagwind.mybatis.definition.interceptor.pagination.DialectModel;

/**
 * H2 数据库分页方言
 *
 * @author hubin
 * @since 2016-11-10
 */
public class H2Dialect implements IDialect {

    @Override
    public DialectModel buildPaginationSql(String originalSql, long offset, long limit) {
        String sql = originalSql + " limit " + FIRST_MARK;
        boolean existOffset = false;
        if (offset > 0) {
            existOffset = true;
            sql += (" offset " + SECOND_MARK);
        }
        DialectModel model = new DialectModel(sql, limit, offset);
        return existOffset ? model.setConsumerChain() : model.setConsumer(true);
    }
}
