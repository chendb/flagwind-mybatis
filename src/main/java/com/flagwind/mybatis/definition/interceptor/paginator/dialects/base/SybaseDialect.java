package com.flagwind.mybatis.definition.interceptor.paginator.dialects.base;


import com.flagwind.mybatis.definition.interceptor.paginator.dialects.Dialect;
import org.apache.ibatis.mapping.MappedStatement;

import com.flagwind.mybatis.definition.interceptor.paginator.PageBounds;

public class SybaseDialect extends Dialect {

    public SybaseDialect(MappedStatement mappedStatement, Object parameterObject, PageBounds pageBounds) {
        super(mappedStatement, parameterObject, pageBounds);
    }


    protected String getLimitString(String sql, String offsetName,int offset, String limitName, int limit) {
		throw new UnsupportedOperationException( "paged queries not supported" );
	}

}
