package com.flagwind.mybatis.metadata.processors;

import com.flagwind.mybatis.code.DialectType;
import com.flagwind.mybatis.exceptions.MapperException;
import com.flagwind.mybatis.metadata.FunctionProcessor;
import com.flagwind.mybatis.utils.StringUtil;

/**
 * 取时间的月份
 */
public class MonthFunctionProcessor implements FunctionProcessor {

	@Override
	public String process(String arguments, String alias, DialectType dialectType) {
		String suffix = (StringUtil.isEmpty(alias) ? "" : (" as " + alias));
		switch (dialectType) {
			case Oracle:
				return "to_char("+arguments+",'mm')" + suffix;
			case MySQL:
				return "month("+arguments+")" + suffix;
			default:
				throw new MapperException("该函数没有针对" + dialectType + "类型数据库实现");
		}
	}

	public static void main(String[] args1) {
		String arguments = "timestramp";
		System.out.println("MySQL:"+(new MonthFunctionProcessor()).process(arguments,null,DialectType.MySQL));;
		System.out.println("Oracle:"+(new MonthFunctionProcessor()).process(arguments,null,DialectType.Oracle));;
	}
}
