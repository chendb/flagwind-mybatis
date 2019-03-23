package com.flagwind.mybatis.metadata.processors;

import com.flagwind.mybatis.code.DialectType;
import com.flagwind.mybatis.exceptions.MapperException;
import com.flagwind.mybatis.metadata.FunctionProcessor;
import com.flagwind.mybatis.utils.StringUtil;

/**
 * 取时间中的年 yyyy-mm-dd hh24:mi:ss
 */
public class DateFormatFunctionProcessor implements FunctionProcessor {

	@Override
	public String process(String arguments, String alias, DialectType dialectType) {
		String suffix = (StringUtil.isEmpty(alias) ? "" : (" as " + alias));
		String column = arguments.split(",")[0];
		String format = arguments.split(",")[1];
		switch (dialectType) {
		case Oracle:
			format = format.replaceAll("%Y","yyyy").replaceAll("%y","yy").replaceAll("%m","mm")
					.replaceAll("%d","dd").replaceAll("%H","hh24").replaceAll("%h","hh").replaceAll("%i","mi")
					.replaceAll("ss", "%s");
			return "to_char(" + column + "," + format + ")" + suffix;
		case MySQL:
			format = format.replaceAll("yyyy", "%Y").replaceAll("yy", "%y").replaceAll("mm", "%m")
					.replaceAll("dd", "%d").replaceAll("hh24", "%H").replaceAll("hh", "%h").replaceAll("mi", "%i")
					.replaceAll("ss", "%s");
			return "date_format(" + column + "," + format + ")" + suffix;
		default:
			throw new MapperException("该函数没有针对" + dialectType + "类型数据库实现");
		}
	}
}
