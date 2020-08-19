package com.flagwind.mybatis.tenant;

import com.flagwind.mybatis.definition.interceptor.tenant.TenantSqlParser;

/**
 * @author chendb
 * @description:
 * @date 2020-08-18 14:17:16
 */
public class AnnotationTenantSqlParser extends TenantSqlParser {
    public AnnotationTenantSqlParser() {
        super(new AnnotationTenantHandler());
    }
}

