package com.flagwind.mybatis.metadata.processors;

import com.flagwind.commons.StringUtils;
import com.flagwind.mybatis.code.Style;
import com.flagwind.mybatis.metadata.ColumnProcessor;
import com.flagwind.mybatis.metadata.EntityColumn;
import com.flagwind.mybatis.metadata.EntityTableUtils;
import com.flagwind.persistent.ColumnTypeEntry;
import com.flagwind.persistent.annotation.ColumnType;
import com.flagwind.reflect.entities.EntityField;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.UnknownTypeHandler;

;

public class ColumnTypeAnnotationProcessor implements ColumnProcessor
{

	@Override
	public void process(EntityColumn entityColumn, EntityField field, Style style)
	{
		if (field.isAnnotationPresent(ColumnType.class)) {

			ColumnTypeEntry columnTypeEntry = new ColumnTypeEntry();
			if (field.isAnnotationPresent(ColumnType.class)) {
				ColumnType columnType = field.getAnnotation(ColumnType.class);

				if (columnType.jdbcType() != JdbcType.UNDEFINED) {
					columnTypeEntry.setJdbcType(columnType.jdbcType());
				}
				if (columnType.typeHandler() != UnknownTypeHandler.class) {
					columnTypeEntry.setTypeHandler(columnType.typeHandler());
				}

			}
			if (columnTypeEntry.getJdbcType() == JdbcType.UNDEFINED ||columnTypeEntry.getJdbcType() == null) {
				columnTypeEntry.setJdbcType(EntityTableUtils.formJavaType(field.getJavaType()));
			}


			if (columnTypeEntry.getJdbcType() != null) {
				entityColumn.setJdbcType(columnTypeEntry.getJdbcType());
			}
			if (columnTypeEntry.getTypeHandler() != null) {
				entityColumn.setTypeHandler(columnTypeEntry.getTypeHandler());
			}
			if(StringUtils.isNotEmpty(entityColumn.getColumn())){
				entityColumn.setColumn(columnTypeEntry.getColumn());
			}
		}
	}
}
