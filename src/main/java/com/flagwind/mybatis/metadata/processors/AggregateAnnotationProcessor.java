package com.flagwind.mybatis.metadata.processors;

import com.flagwind.mybatis.code.Style;
import com.flagwind.mybatis.reflection.entities.EntityField;
import com.flagwind.mybatis.metadata.ColumnProcessor;
import com.flagwind.mybatis.metadata.EntityColumn;
import com.flagwind.mybatis.utils.StringUtil;
import com.flagwind.persistent.AggregateEntry;
import com.flagwind.persistent.annotation.Aggregate;

public class AggregateAnnotationProcessor implements ColumnProcessor
{
	@Override
	public void process(EntityColumn entityColumn, EntityField field, Style style)
	{
		if (field.isAnnotationPresent(Aggregate.class)) {
			Aggregate aggregate = field.getAnnotation(Aggregate.class);
			String column = aggregate.column();
			column = StringUtil.isEmpty(column) ? field.getName() : column;
			entityColumn.setAggregate(new AggregateEntry(aggregate.type(), field.getName(), column));
		}
	}
}