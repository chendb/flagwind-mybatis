package com.flagwind.mybatis.metadata;

import com.flagwind.mybatis.code.Style;
import com.flagwind.mybatis.reflection.entities.EntityField;

public interface ColumnProcessor
{
	void process(EntityColumn column, EntityField field, Style style);
}
