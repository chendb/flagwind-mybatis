package com.flagwind.mybatis.metadata.processors;

import com.flagwind.commons.StringUtils;
import com.flagwind.mybatis.code.DatabaseType;
import com.flagwind.mybatis.exceptions.MapperException;
import com.flagwind.mybatis.metadata.FunctionProcessor;

/**
 * 格式如下：yyyy-MM-dd HH:mm:ss
 */
public class DateFormatFunctionProcessor implements FunctionProcessor {

	@Override
	public String process(String arguments, String alias, DatabaseType databaseType) {
		String suffix = (StringUtils.isEmpty(alias) ? "" : (" as " + alias));
		String column = arguments.split(",")[0];
		String format = arguments.split(",")[1];
		switch (databaseType) {
		case Oracle:
		case DM:
			format = format.replaceAll("MM","mm").replace("mm", "mi");
			return "to_char(" + column + "," + format + ")" + suffix;
		case MySQL:
			format = format.replaceAll("yyyy", "%Y").replaceAll("yy", "%y").replaceAll("MM", "%m")
						   .replaceAll("dd", "%d").replaceAll("HH", "%H").replaceAll("hh", "%h")
						   .replaceAll("mm", "%i").replaceAll("ss", "%s");
			return "date_format(" + column + "," + format + ")" + suffix;
		default:
			throw new MapperException("该函数没有针对" + databaseType + "类型数据库实现");
		}
	}

	
	public static void main(String[] args1) {
		String arguments = "timestamp,'yyyy-MM-dd'";
		System.out.println("MySQL:"+(new DateFormatFunctionProcessor()).process(arguments,null,DatabaseType.MySQL));
		System.out.println("Oracle:"+(new DateFormatFunctionProcessor()).process(arguments,null,DatabaseType.Oracle));
	}
}
