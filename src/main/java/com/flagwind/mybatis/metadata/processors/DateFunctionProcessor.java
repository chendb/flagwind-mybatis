package com.flagwind.mybatis.metadata.processors;

import com.flagwind.mybatis.code.DialectType;
import com.flagwind.mybatis.exceptions.MapperException;
import com.flagwind.mybatis.metadata.FunctionProcessor;
import com.flagwind.mybatis.utils.StringUtil;

/**
 * 取时间的年月日（格式：yyyy-mm-dd）
 */
public class DateFunctionProcessor implements FunctionProcessor {

	@Override
	public String process(String arguments, String alias, DialectType dialectType) {
		String suffix = (StringUtil.isEmpty(alias) ? "" : (" as " + alias));
		switch (dialectType) {
		case Oracle:
			return "to_char(" + arguments + ",'yyyy-mm-dd')" + suffix;
		case MySQL:
			return "date_format(" + arguments + ",'%Y-%m-%d')" + suffix;
		default:
			throw new MapperException("该函数没有针对" + dialectType + "类型数据库实现");
		}
	}
}
