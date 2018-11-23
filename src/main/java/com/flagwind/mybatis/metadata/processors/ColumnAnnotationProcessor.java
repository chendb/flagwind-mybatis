package com.flagwind.mybatis.metadata.processors;

import com.flagwind.mybatis.code.Style;
import com.flagwind.mybatis.reflection.entities.EntityField;
import com.flagwind.mybatis.metadata.ColumnProcessor;
import com.flagwind.mybatis.metadata.EntityColumn;
import com.flagwind.mybatis.utils.StringUtil;

import javax.persistence.Column;

public class ColumnAnnotationProcessor implements ColumnProcessor
{
	@Override
	public void process(EntityColumn entityColumn, EntityField field, Style style)
	{
		if (field.isAnnotationPresent(Column.class)) {
			Column column = field.getAnnotation(Column.class);
			entityColumn.setUpdatable(column.updatable());
			entityColumn.setInsertable(column.insertable());
			if(StringUtil.isNotEmpty(column.name())){
				entityColumn.setColumn(column.name());
			}
		}

	}
}
