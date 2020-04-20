
package com.flagwind.mybatis.definition.interceptor.pagination.dialects;

import com.flagwind.mybatis.definition.interceptor.pagination.DialectModel;

/**
 * 数据库 分页语句组装接口
 *
 * @author hubin
 * @since 2016-01-23
 */
public interface IDialect {
    /**
     * 这俩没什么特殊意义
     * 只是为了实现类方便使用,以及区分分页 sql 的参数
     */
    String FIRST_MARK = "?";
    String SECOND_MARK = "?";

    /**
     * 组装分页语句
     *
     * @param originalSql 原始语句
     * @param offset      偏移量
     * @param limit       界限
     * @return 分页模型
     */
    DialectModel buildPaginationSql(String originalSql, long offset, long limit);
}
