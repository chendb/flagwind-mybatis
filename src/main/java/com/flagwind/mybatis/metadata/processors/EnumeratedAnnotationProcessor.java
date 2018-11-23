package com.flagwind.mybatis.metadata.processors;

import com.flagwind.mybatis.code.Style;
import com.flagwind.mybatis.reflection.entities.EntityField;
import com.flagwind.mybatis.metadata.ColumnProcessor;
import com.flagwind.mybatis.metadata.EntityColumn;
import com.flagwind.mybatis.utils.TypeUtils;
import org.apache.ibatis.type.EnumOrdinalTypeHandler;
import org.apache.ibatis.type.EnumTypeHandler;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

public class EnumeratedAnnotationProcessor implements ColumnProcessor
{
	@Override
	public void process(EntityColumn entityColumn, EntityField field, Style style)
	{
		if (field.getJavaType().isEnum())
		{

			if(field.isAnnotationPresent(Enumerated.class))
			{
				// 获取注解对象
				Enumerated enumerated = field.getAnnotation(Enumerated.class);
				// 设置了value属性
				if(enumerated.value() == EnumType.ORDINAL)
				{
					EnumOrdinalTypeHandler typeHandler = new EnumOrdinalTypeHandler(field.getJavaType());
					entityColumn.setTypeHandler(TypeUtils.castTo(typeHandler.getClass()));
				}
				else
				{
					EnumTypeHandler typeHandler = new EnumTypeHandler(field.getJavaType());
					entityColumn.setTypeHandler(TypeUtils.castTo(typeHandler.getClass()));
				}
			}

		}

	}
}