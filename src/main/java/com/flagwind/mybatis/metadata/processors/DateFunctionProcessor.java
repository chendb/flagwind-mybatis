package com.flagwind.mybatis.metadata.processors;

import com.flagwind.commons.StringUtils;
import com.flagwind.mybatis.code.DialectType;
import com.flagwind.mybatis.exceptions.MapperException;
import com.flagwind.mybatis.metadata.FunctionProcessor;

/**
 * 取时间的年月日（格式：yyyy-mm-dd）
 */
public class DateFunctionProcessor implements FunctionProcessor {

	@Override
	public String process(String arguments, String alias, DialectType dialectType) {
		String suffix = (StringUtils.isEmpty(alias) ? "" : (" as " + alias));
		switch (dialectType) {
		case Oracle:
			return "to_char(" + arguments + ",'yyyy-mm-dd')" + suffix;
		case MySQL:
			return "date_format(" + arguments + ",'%Y-%m-%d')" + suffix;
		default:
			throw new MapperException("该函数没有针对" + dialectType + "类型数据库实现");
		}
	}

	
	public static void main(String[] args1) {
		String arguments = "timestramp";
		System.out.println("MySQL:"+(new DateFunctionProcessor()).process(arguments,null,DialectType.MySQL));;
		System.out.println("Oracle:"+(new DateFunctionProcessor()).process(arguments,null,DialectType.Oracle));;
	}
}
