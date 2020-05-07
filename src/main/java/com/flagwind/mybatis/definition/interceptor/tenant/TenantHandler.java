package com.flagwind.mybatis.definition.interceptor.tenant;

import net.sf.jsqlparser.expression.Expression;

/**
 * @author chendb
 * @description: 租户处理器
 * @date 2020-04-15 20:24:07
 */
public interface TenantHandler {
    /**
     * 获取租户 ID 值表达式，支持多个 ID 条件查询
     * <p>
     * 支持自定义表达式，比如：tenant_id in (1,2) @since 2019-8-2
     *
     * @param where 参数 true 表示为 where 条件 false 表示为 insert 或者 select 条件
     * @return 租户 ID 值表达式
     */
    Expression getTenantId(boolean where);

    /**
     * 获取租户字段名
     *
     * @return 租户字段名
     */
    String getTenantIdColumn();

    /**
     * 根据表名判断是否进行过滤
     *
     * @param tableName 表名
     * @return 是否进行过滤, true:表示忽略，false:需要解析多租户字段
     */
    boolean doTableFilter(String tableName);

}
