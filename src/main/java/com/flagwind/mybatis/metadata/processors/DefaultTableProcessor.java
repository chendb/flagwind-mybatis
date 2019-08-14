package com.flagwind.mybatis.metadata.processors;

import com.flagwind.mybatis.code.Style;
import com.flagwind.mybatis.common.Config;
import com.flagwind.mybatis.metadata.EntityTable;
import com.flagwind.mybatis.metadata.TableProcessor;
import com.flagwind.mybatis.utils.NameUtils;
import com.flagwind.persistent.annotation.NameStyle;

import javax.persistence.Table;

public class DefaultTableProcessor implements TableProcessor
{
	@Override
	public void process(EntityTable entityTable, Config config)
	{
		Style style = config.getStyle();
		//style，该注解优先于全局配置
		if(entityTable.getEntityClass().isAnnotationPresent(NameStyle.class))
		{
			NameStyle nameStyle = entityTable.getEntityClass().getAnnotation(NameStyle.class);
			style = nameStyle.value();
		}

		//创建并缓存EntityTable
		if(entityTable.getEntityClass().isAnnotationPresent(Table.class))
		{
			Table table = entityTable.getEntityClass().getAnnotation(Table.class);
			if(!table.name().equals(""))
			{
				entityTable.setTable(table);
			}
		}
		else
		{
			//可以通过style控制
			entityTable.setName(NameUtils.convertByStyle(entityTable.getEntityClass().getSimpleName(), style));
		}

	}
}
