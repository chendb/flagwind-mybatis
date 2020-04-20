package com.flagwind.mybatis.metadata.processors;

import com.flagwind.commons.StringUtils;
import com.flagwind.mybatis.code.DatabaseType;
import com.flagwind.mybatis.exceptions.MapperException;
import com.flagwind.mybatis.metadata.FunctionProcessor;

/**
 * 取时间中的年yyyy
 */
public class YearFunctionProcessor implements FunctionProcessor {

	@Override
	public String process(String arguments, String alias, DatabaseType databaseType) {
		String suffix = (StringUtils.isEmpty(alias) ? "" : (" as " + alias));
		switch (databaseType) {
			case Oracle:
				return "to_char("+arguments+",'yyyy')" + suffix;
			case MySQL:
				return "year("+arguments+")" + suffix;
			default:
				throw new MapperException("该函数没有针对" + databaseType + "类型数据库实现");
		}
	}

	public static void main(String[] args1) {
		String arguments = "timestramp";
		System.out.println("MySQL:"+(new YearFunctionProcessor()).process(arguments,null,DatabaseType.MySQL));
        System.out.println("Oracle:"+(new YearFunctionProcessor()).process(arguments,null,DatabaseType.Oracle));
    }
}
