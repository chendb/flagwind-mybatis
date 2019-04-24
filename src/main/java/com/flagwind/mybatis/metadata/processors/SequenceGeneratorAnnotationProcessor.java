package com.flagwind.mybatis.metadata.processors;

import com.flagwind.mybatis.code.Style;
import com.flagwind.mybatis.exceptions.MapperException;
import com.flagwind.mybatis.metadata.ColumnProcessor;
import com.flagwind.mybatis.metadata.EntityColumn;
import com.flagwind.reflect.entities.EntityField;

import javax.persistence.SequenceGenerator;

public class SequenceGeneratorAnnotationProcessor implements ColumnProcessor
{
	@Override
	public void process(EntityColumn entityColumn, EntityField field, Style style)
	{
		if (field.isAnnotationPresent(SequenceGenerator.class)) {
			SequenceGenerator sequenceGenerator = field.getAnnotation(SequenceGenerator.class);
			if (sequenceGenerator.sequenceName().equals("")) {
				throw new MapperException(entityColumn.getTable().getEntityClass() + "字段" + field.getName() + "的注解@SequenceGenerator未指定sequenceName!");
			}
			entityColumn.setSequenceName(sequenceGenerator.sequenceName());
		}
	}
}