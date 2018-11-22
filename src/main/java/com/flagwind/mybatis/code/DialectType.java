package com.flagwind.mybatis.code;

public enum DialectType
{
	Cloudscape,Derby,DB2_MF,DB2,H2,HSQL,Informix,MySQL,Oralce,PostgreSQL,SQLServer2005,SQLServer,Sybase;

	public static DialectType parse(String dialect)
	{
		if(dialect.toLowerCase().contains("cloudscape"))
		{
			return DialectType.Cloudscape;
		}
		else if(dialect.toLowerCase().contains("derby"))
		{
			return DialectType.Derby;
		}
		else if(dialect.toLowerCase().contains("db2_mf"))
		{
			return DialectType.DB2_MF;
		}
		else if(dialect.toLowerCase().contains("db2"))
		{
			return DialectType.DB2;
		}
		else if(dialect.toLowerCase().contains("h2"))
		{
			return DialectType.H2;
		}
		else if(dialect.toLowerCase().contains("hsql"))
		{
			return DialectType.HSQL;
		}
		else if(dialect.toLowerCase().contains("informix"))
		{
			return DialectType.Informix;
		}
		else if(dialect.toLowerCase().contains("mysql"))
		{
			return DialectType.MySQL;
		}
		else if(dialect.toLowerCase().contains("oracle"))
		{
			return DialectType.Oralce;
		}
		else if(dialect.toLowerCase().contains("postgresql"))
		{
			return DialectType.PostgreSQL;
		}
		else if(dialect.toLowerCase().contains("sqlserver2005"))
		{
			return DialectType.SQLServer2005;
		}
		else if(dialect.toLowerCase().contains("sqlserver"))
		{
			return DialectType.SQLServer;
		}
		else if(dialect.toLowerCase().contains("sybase"))
		{
			return DialectType.Sybase;
		}
		else
		{
			return DialectType.Oralce;
		}
	}
}
