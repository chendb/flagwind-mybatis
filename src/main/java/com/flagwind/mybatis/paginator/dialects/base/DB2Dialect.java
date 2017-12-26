package com.flagwind.mybatis.paginator.dialects.base;

import com.flagwind.mybatis.paginator.dialects.Dialect;
import org.apache.ibatis.mapping.MappedStatement;

import com.flagwind.mybatis.paginator.PageBounds;


public class DB2Dialect extends Dialect {

    public DB2Dialect(MappedStatement mappedStatement, Object parameterObject, PageBounds pageBounds) {
        super(mappedStatement, parameterObject, pageBounds);
    }
	
	private static String getRowNumber(String sql) {
		StringBuffer rownumber = new StringBuffer(50)
			.append("rownumber() over(");

		int orderByIndex = sql.toLowerCase().indexOf("order by");

		if ( orderByIndex>0 && !hasDistinct(sql) ) {
			rownumber.append( sql.substring(orderByIndex) );
		}

		rownumber.append(") as rownumber_,");

		return rownumber.toString();
	}
	
	private static boolean hasDistinct(String sql) {
		return sql.toLowerCase().indexOf("select distinct")>=0;
	}

    protected String getLimitString(String sql, String offsetName,int offset, String limitName, int limit) {
		int startOfSelect = sql.toLowerCase().indexOf("select");

		StringBuffer pagingSelect = new StringBuffer( sql.length()+100 )
					.append( sql.substring(0, startOfSelect) ) 
					.append("select * from ( select ")
					.append( getRowNumber(sql) ); 

		if ( hasDistinct(sql) ) {
			pagingSelect.append(" row_.* from ( ") 
				.append( sql.substring(startOfSelect) )
				.append(" ) as row_");
		}
		else {
			pagingSelect.append( sql.substring( startOfSelect + 6 ) ); 
		}

		pagingSelect.append(" ) as temp_ where rownumber_ ");


		if (offset > 0) {
			pagingSelect.append("between ?+1 and ?");
            setPageParameter(offsetName,offset,Integer.class);
            setPageParameter("__offsetEnd",offset+limit,Integer.class);
		}
		else {
			pagingSelect.append("<= ?");
            setPageParameter(limitName,limit,Integer.class);
		}

		return pagingSelect.toString();
	}
}
