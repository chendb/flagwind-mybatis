package com.flagwind.mybatis.metadata.processors;

import com.flagwind.commons.StringUtils;
import com.flagwind.mybatis.code.DialectType;
import com.flagwind.mybatis.exceptions.MapperException;
import com.flagwind.mybatis.metadata.FunctionProcessor;

/**
 * 取时间中的小时hh24
 */
public class HourFunctionProcessor implements FunctionProcessor {

	@Override
	public String process(String arguments, String alias, DialectType dialectType) {
		String suffix = (StringUtils.isEmpty(alias) ? "" : (" as " + alias));
		switch (dialectType) {
		case Oracle:
			return "to_char(" + arguments + ",'hh24')" + suffix;
		case MySQL:
			return "date_format(" + arguments + ",'%H')" + suffix;
		default:
			throw new MapperException("该函数没有针对" + dialectType + "类型数据库实现");
		}
	}

	public static void main(String[] args1) {
		String arguments = "timestramp";
		System.out.println("MySQL:"+(new HourFunctionProcessor()).process(arguments,null,DialectType.MySQL));;
		System.out.println("Oracle:"+(new HourFunctionProcessor()).process(arguments,null,DialectType.Oracle));;
	}
}
