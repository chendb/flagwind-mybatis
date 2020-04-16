package com.flagwind.mybatis.metadata;

import com.flagwind.mybatis.definition.Config;

public interface TableProcessor
{
	void process(EntityTable entityTable, Config config);
}
