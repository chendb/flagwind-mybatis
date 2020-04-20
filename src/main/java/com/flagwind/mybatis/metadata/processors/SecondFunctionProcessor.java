package com.flagwind.mybatis.metadata.processors;

import com.flagwind.commons.StringUtils;
import com.flagwind.mybatis.code.DatabaseType;
import com.flagwind.mybatis.exceptions.MapperException;
import com.flagwind.mybatis.metadata.FunctionProcessor;

/**
 * 取时间的秒ss
 */
public class SecondFunctionProcessor implements FunctionProcessor {

	@Override
	public String process(String arguments, String alias, DatabaseType databaseType) {
		String suffix = (StringUtils.isEmpty(alias) ? "" : (" as " + alias));
		switch (databaseType) {
		case Oracle:
			return "to_char(" + arguments + ",'ss')" + suffix;
		case MySQL:
			return "date_format(" + arguments + ",'%s')" + suffix;
		default:
			throw new MapperException("该函数没有针对" + databaseType + "类型数据库实现");
		}
	}

	public static void main(String[] args1) {
		String arguments = "timestamp";
		System.out.println("MySQL:"+(new SecondFunctionProcessor()).process(arguments,null,DatabaseType.MySQL));
        System.out.println("Oracle:"+(new SecondFunctionProcessor()).process(arguments,null,DatabaseType.Oracle));
    }
}
