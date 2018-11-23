package com.flagwind.mybatis.definition.interceptor.paginator.dialects.base;


import com.flagwind.mybatis.definition.interceptor.paginator.dialects.Dialect;
import org.apache.ibatis.mapping.MappedStatement;

import com.flagwind.mybatis.definition.interceptor.paginator.PageBounds;


public class SQLServerDialect extends Dialect {

    public SQLServerDialect(MappedStatement mappedStatement, Object parameterObject, PageBounds pageBounds) {
        super(mappedStatement, parameterObject, pageBounds);
    }

	
	static int getAfterSelectInsertPoint(String sql) {
		int selectIndex = sql.toLowerCase().indexOf( "select" );
		final int selectDistinctIndex = sql.toLowerCase().indexOf( "select distinct" );
		return selectIndex + ( selectDistinctIndex == selectIndex ? 15 : 6 );
	}


    protected String getLimitString(String sql, String offsetName,int offset, String limitName, int limit) {
		if ( offset > 0 ) {
			throw new UnsupportedOperationException( "sql server has no offset" );
		}
//		if(limitPlaceholder != null) {
//			throw new UnsupportedOperationException(" sql server not support variable limit");
//		}
        setPageParameter(limitName, limit, Integer.class);
		return new StringBuffer( sql.length() + 8 )
				.append( sql )
				.insert( getAfterSelectInsertPoint( sql ), " top " + limit )
				.toString();
	}
	
	// TODO add Dialect.supportsVariableLimit() for sqlserver 
//	public boolean supportsVariableLimit() {
//		return false;
//	}

}
