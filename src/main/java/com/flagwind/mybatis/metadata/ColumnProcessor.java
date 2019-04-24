package com.flagwind.mybatis.metadata;

import com.flagwind.mybatis.code.Style;
import com.flagwind.reflect.entities.EntityField;


public interface ColumnProcessor
{
	void process(EntityColumn column, EntityField field, Style style);
}
