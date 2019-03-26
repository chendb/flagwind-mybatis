package com.flagwind.mybatis.metadata.processors;

import com.flagwind.mybatis.code.DialectType;
import com.flagwind.mybatis.exceptions.MapperException;
import com.flagwind.mybatis.metadata.FunctionProcessor;
import com.flagwind.mybatis.utils.StringUtil;

/**
 * 取时间中的年yyyy
 */
public class YearFunctionProcessor implements FunctionProcessor {

	@Override
	public String process(String arguments, String alias, DialectType dialectType) {
		String suffix = (StringUtil.isEmpty(alias) ? "" : (" as " + alias));
		switch (dialectType) {
			case Oracle:
				return "to_char("+arguments+",'yyyy')" + suffix;
			case MySQL:
				return "year("+arguments+")" + suffix;
			default:
				throw new MapperException("该函数没有针对" + dialectType + "类型数据库实现");
		}
	}

	public static void main(String[] args1) {
		String arguments = "timestramp";
		System.out.println("MySQL:"+(new YearFunctionProcessor()).process(arguments,null,DialectType.MySQL));;
		System.out.println("Oracle:"+(new YearFunctionProcessor()).process(arguments,null,DialectType.Oracle));;
	}
}
