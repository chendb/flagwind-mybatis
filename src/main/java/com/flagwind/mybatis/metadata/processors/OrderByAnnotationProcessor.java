package com.flagwind.mybatis.metadata.processors;

import com.flagwind.mybatis.code.Style;
import com.flagwind.mybatis.metadata.ColumnProcessor;
import com.flagwind.mybatis.metadata.EntityColumn;
import com.flagwind.reflect.entities.EntityField;

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