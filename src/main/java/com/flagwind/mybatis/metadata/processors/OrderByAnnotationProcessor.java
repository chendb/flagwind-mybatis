package com.flagwind.mybatis.metadata.processors;

import com.flagwind.mybatis.code.Style;
import com.flagwind.mybatis.reflection.entities.EntityField;
import com.flagwind.mybatis.metadata.ColumnProcessor;
import com.flagwind.mybatis.metadata.EntityColumn;

import javax.persistence.OrderBy;

public class OrderByAnnotationProcessor implements ColumnProcessor
{
	@Override
	public void process(EntityColumn entityColumn, EntityField field, Style style)
	{
		if (field.isAnnotationPresent(OrderBy.class)) {
			OrderBy orderBy = field.getAnnotation(OrderBy.class);
			if (orderBy.value().equals("")) {
				entityColumn.setOrderBy("ASC");
			} else {
				entityColumn.setOrderBy(orderBy.value());
			}
		}
	}
	
}