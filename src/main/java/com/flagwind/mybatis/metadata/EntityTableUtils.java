package com.flagwind.mybatis.metadata;

import com.flagwind.mybatis.code.Style;
import com.flagwind.mybatis.reflection.entities.EntityField;
import com.flagwind.mybatis.utils.StringUtil;
import com.flagwind.persistent.ColumnTypeEntry;
import com.flagwind.persistent.annotation.ColumnType;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.UnknownTypeHandler;

import javax.persistence.Column;
import java.sql.Timestamp;

public class EntityTableUtils
{
	public static JdbcType formJavaType(Class<?> javaType)
	{
		if(javaType == null)
		{
			return JdbcType.UNDEFINED;
		}
		if(javaType.isAssignableFrom(String.class))
		{
			return JdbcType.VARCHAR;
		}
		if(javaType.isAssignableFrom(Integer.class) || javaType.isAssignableFrom(int.class))
		{
			return JdbcType.INTEGER;
		}
		if(javaType.isAssignableFrom(Number.class))
		{
			return JdbcType.NUMERIC;
		}
		if(javaType.isAssignableFrom(Long.class) || javaType.isAssignableFrom(long.class))
		{
			return JdbcType.NUMERIC;
		}
		if(javaType.isAssignableFrom(Double.class) || javaType.isAssignableFrom(double.class))
		{
			return JdbcType.NUMERIC;
		}
		if(javaType.isAssignableFrom(Boolean.class) || javaType.isAssignableFrom(boolean.class))
		{
			return JdbcType.TINYINT;
		}
		if(javaType.isAssignableFrom(Float.class) || javaType.isAssignableFrom(float.class))
		{
			return JdbcType.FLOAT;
		}

		if(javaType.isAssignableFrom(Timestamp.class))
		{
			return JdbcType.TIMESTAMP;
		}

		if(javaType.isAssignableFrom(java.sql.Time.class))
		{
			return JdbcType.TIME;
		}

		if(javaType.isAssignableFrom(java.sql.Date.class) || javaType.isAssignableFrom(java.util.Date.class))
		{
			return JdbcType.DATE;
		}

		if(javaType.isAssignableFrom(byte[].class))
		{
			return JdbcType.BINARY;
		}
		if(javaType.isEnum())
		{
			return JdbcType.VARCHAR;
		}
		return JdbcType.UNDEFINED;
	}

	public static ColumnTypeEntry getColumnTypeEntry(EntityField field){
		ColumnTypeEntry entry = new ColumnTypeEntry();
		if (field.isAnnotationPresent(ColumnType.class)) {
			ColumnType columnType = field.getAnnotation(ColumnType.class);

			if (columnType.jdbcType() != JdbcType.UNDEFINED) {
				entry.setJdbcType(columnType.jdbcType());
			}
			if (columnType.typeHandler() != UnknownTypeHandler.class) {
				entry.setTypeHandler(columnType.typeHandler());
			}

		}
		if (entry.getJdbcType() == JdbcType.UNDEFINED ||entry.getJdbcType() == null) {
			entry.setJdbcType(formJavaType(field.getJavaType()));
		}
		return entry;
	}

	public static String getColumnName(EntityField field, Style style)
	{
		if(field.isAnnotationPresent(Column.class))
		{
			Column column = field.getAnnotation(Column.class);
			if(StringUtil.isNotEmpty(column.name()))
			{
				return column.name();
			}
		}
		return StringUtil.convertByStyle(field.getName(), style);

	}


}
