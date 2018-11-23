package com.flagwind.mybatis.metadata.processors;

import com.flagwind.mybatis.code.Style;
import com.flagwind.mybatis.reflection.entities.EntityField;
import com.flagwind.mybatis.metadata.ColumnProcessor;
import com.flagwind.mybatis.metadata.EntityColumn;

import javax.persistence.Id;

public class IdAnnotationProcessor implements ColumnProcessor
{
	@Override
	public void process(EntityColumn entityColumn, EntityField field, Style style)
	{
		if (field.isAnnotationPresent(Id.class)) {
			entityColumn.setId(true);
		}

	}
}
