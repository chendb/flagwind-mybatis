package com.flagwind.mybatis.metadata.processors;

import com.flagwind.commons.StringUtils;
import com.flagwind.mybatis.code.Style;
import com.flagwind.mybatis.metadata.ColumnProcessor;
import com.flagwind.mybatis.metadata.EntityColumn;
import com.flagwind.persistent.AggregateEntry;
import com.flagwind.persistent.annotation.Aggregate;
import com.flagwind.reflect.entities.EntityField;

public class AggregateAnnotationProcessor implements ColumnProcessor
{
	@Override
	public void process(EntityColumn entityColumn, EntityField field, Style style)
	{
		if (field.isAnnotationPresent(Aggregate.class)) {
			Aggregate aggregate = field.getAnnotation(Aggregate.class);
			String column = StringUtils.isEmpty(aggregate.column()) ? field.getName() : aggregate.column();
			entityColumn.setAggregate(new AggregateEntry(aggregate.type(), field.getName(), column));
		}
	}
}