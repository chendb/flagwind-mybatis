package com.flagwind.mybatis.metadata.processors;

import com.flagwind.commons.StringUtils;
import com.flagwind.mybatis.code.DatabaseType;
import com.flagwind.mybatis.exceptions.MapperException;
import com.flagwind.mybatis.metadata.FunctionProcessor;

/**
 * 当前时间函数
 */
public class NowFunctionProcessor implements FunctionProcessor {

	@Override
	public String process(String arguments, String alias, DatabaseType databaseType) {
		String suffix = (StringUtils.isEmpty(alias) ? "" : (" as " + alias));
		switch (databaseType) {
			case Oracle:
			case DM:
				return "sysdate()" + suffix;
			case MySQL:
				return "now()" + suffix;
			default:
				throw new MapperException("该函数没有针对" + databaseType + "类型数据库实现");
		}
	}

	public static void main(String[] args1) {
		String arguments = "";
		System.out.println("MySQL:"+(new NowFunctionProcessor()).process(arguments,null,DatabaseType.MySQL));
        System.out.println("Oracle:"+(new NowFunctionProcessor()).process(arguments,null,DatabaseType.Oracle));
    }
}
