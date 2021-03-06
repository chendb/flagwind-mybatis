package com.flagwind.persistent;

import com.flagwind.mybatis.code.DialectType;
import com.flagwind.mybatis.common.TemplateContext;
import com.flagwind.mybatis.metadata.FunctionProcessor;
import com.flagwind.mybatis.metadata.processors.DateFormatFunctionProcessor;
import com.flagwind.mybatis.metadata.processors.DateFunctionProcessor;
import com.flagwind.mybatis.metadata.processors.DayFunctionProcessor;
import com.flagwind.mybatis.metadata.processors.DecodeFunctionProcessor;
import com.flagwind.mybatis.metadata.processors.HourFunctionProcessor;
import com.flagwind.mybatis.metadata.processors.LeftFunctionProcessor;
import com.flagwind.mybatis.metadata.processors.LengthFunctionProcessor;
import com.flagwind.mybatis.metadata.processors.MinuteFunctionProcessor;
import com.flagwind.mybatis.metadata.processors.MonthFunctionProcessor;
import com.flagwind.mybatis.metadata.processors.NowFunctionProcessor;
import com.flagwind.mybatis.metadata.processors.RightFunctionProcessor;
import com.flagwind.mybatis.metadata.processors.SecondFunctionProcessor;
import com.flagwind.mybatis.metadata.processors.SubstringFunctionProcessor;
import com.flagwind.mybatis.metadata.processors.TimeFunctionProcessor;
import com.flagwind.mybatis.metadata.processors.YearFunctionProcessor;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

public class Functions {

	private static String FUNCTION_PATTERN = "(?<method>(?<=@)(\\S+)(?=\\())|(?<argument>(?<=\\()(\\S*)(?=\\)))|(?<alias>(?<= ?as )(\\S+))";

	private static final Map<String, FunctionProcessor> CACHE_FUNCTION_PROCESSOR = Collections
			.synchronizedMap(new WeakHashMap<String, FunctionProcessor>());

	static {
		// @now()
		CACHE_FUNCTION_PROCESSOR.put("now", new NowFunctionProcessor());
		// @length(name)
		CACHE_FUNCTION_PROCESSOR.put("length", new LengthFunctionProcessor());
		// @decode(status,1:'在线',2:'离线','未知')
		CACHE_FUNCTION_PROCESSOR.put("decode", new DecodeFunctionProcessor());
		// @dt_format(timestamp,'yyyy-MM-dd HH:mm:ss')
		CACHE_FUNCTION_PROCESSOR.put("dt_format", new DateFormatFunctionProcessor());
		// @dt_date(timestamp) -> yyyy-MM-dd
		CACHE_FUNCTION_PROCESSOR.put("dt_date", new DateFunctionProcessor());
		// @year(timestamp) -> yyyy
		CACHE_FUNCTION_PROCESSOR.put("year", new YearFunctionProcessor());
		// @month(timestamp) -> MM
		CACHE_FUNCTION_PROCESSOR.put("month", new MonthFunctionProcessor());
		// @day(timestamp) -> dd
		CACHE_FUNCTION_PROCESSOR.put("day", new DayFunctionProcessor());
		// @dt_time(timestamp) -> HH:mm:ss
		CACHE_FUNCTION_PROCESSOR.put("dt_time", new TimeFunctionProcessor());
		// @hour(timestamp) -> HH
		CACHE_FUNCTION_PROCESSOR.put("hour", new HourFunctionProcessor());
		// @minute(timestamp) -> mm
		CACHE_FUNCTION_PROCESSOR.put("minute", new MinuteFunctionProcessor());
		// @second(timestamp) -> ss
		CACHE_FUNCTION_PROCESSOR.put("second", new SecondFunctionProcessor());
		// @substring(regionId,1,2) 或 @substring(regionId,1)
		CACHE_FUNCTION_PROCESSOR.put("substring", new SubstringFunctionProcessor());
        // @left(name,2)
		CACHE_FUNCTION_PROCESSOR.put("left", new LeftFunctionProcessor());
		// @right(name,2)
		CACHE_FUNCTION_PROCESSOR.put("right", new RightFunctionProcessor());
	}

	private static String parseNestFunction(String express) {
		StringBuilder sb = new StringBuilder();
		StringBuilder func = new StringBuilder();
		char[] chars = express.toCharArray();
		for (char s : chars) {
			if ('@' == s) {
				sb.append(func.toString());
				func = new StringBuilder().append('@');
			} else {
				if (func.length() > 0) {
					func.append(s);
				} else {
					sb.append(s);
				}
			}

			if (')' == s && func.length() > 0) {
				String sql = parse(func.toString());
				sb.append(sql).append(" ");
				func = new StringBuilder();
			}
		}

		if (sb.indexOf("@") >= 0) {
			return parse(sb.toString());
		} else {
			return sb.toString();
		}
	}

	private static String parseAtomFunction(DialectType dialectType, String express) {
		String[] attr = express.split("@|\\(|\\)|as");
		String method = attr[1].trim();
		String argument = attr[2].trim();
		String alias = "";
		if (attr.length == 5) {
			alias = attr[4];
		}
		String sql = CACHE_FUNCTION_PROCESSOR.get(method).process(argument, alias, dialectType);
		sql = sql.replace("(", "&{").replace(")", "}&");
		return sql;
	}

	/**
	 * 增加函数
	 * @param name 函数名称
	 * @param processor 函数的实现
	 */
	public static void add(String name,FunctionProcessor processor) {
		if (CACHE_FUNCTION_PROCESSOR.containsKey(name)) {
			throw new RuntimeException("该函数名已被占用");
		}
		CACHE_FUNCTION_PROCESSOR.put(name, processor);
	}

	public static String parse(String express) {
		int count = express.split("@").length - 1;
		if (count <= 0) {
			return express;
		}
		if (count > 1) {
			return parseNestFunction(express);
		}

		DialectType dialectType = DialectType.parse(TemplateContext.config().getDialect());
		return parseAtomFunction(dialectType, express);
	}

	public static String invoke(String express) {
		 return parse(express).replaceAll("\\&\\{","(").replaceAll("\\}\\&",")");
	}

	public static void main(String[] args) {
		System.out.println(invoke("@decode(state,1:10,2:20) as time"));
	}
}
