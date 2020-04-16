package com.flagwind.mybatis.metadata.processors;

import com.flagwind.commons.StringUtils;
import com.flagwind.mybatis.code.Style;
import com.flagwind.mybatis.metadata.ColumnProcessor;
import com.flagwind.mybatis.metadata.EntityColumn;
import com.flagwind.mybatis.metadata.EntityTableUtils;
import com.flagwind.persistent.ColumnTypeEntry;
import com.flagwind.persistent.annotation.ColumnType;
import com.flagwind.reflect.entities.EntityField;

;

public class ColumnTypeAnnotationProcessor implements ColumnProcessor
{

	@Override
	public void process(EntityColumn entityColumn, EntityField field, Style style)
	{
		if (field.isAnnotationPresent(ColumnType.class)) {

			ColumnTypeEntry columnTypeEntry = EntityTableUtils.getColumnTypeEntry(field);

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
