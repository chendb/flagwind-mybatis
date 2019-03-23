package com.flagwind.persistent;

import com.flagwind.mybatis.code.DialectType;
import com.flagwind.mybatis.common.TemplateContext;
import com.flagwind.mybatis.metadata.FunctionProcessor;
import com.flagwind.mybatis.metadata.processors.DateFormatFunctionProcessor;
import com.flagwind.mybatis.metadata.processors.DateFunctionProcessor;
import com.flagwind.mybatis.metadata.processors.DayFunctionProcessor;
import com.flagwind.mybatis.metadata.processors.DecodeFunctionProcessor;
import com.flagwind.mybatis.metadata.processors.HourFunctionProcessor;
import com.flagwind.mybatis.metadata.processors.LengthFunctionProcessor;
import com.flagwind.mybatis.metadata.processors.MinuteFunctionProcessor;
import com.flagwind.mybatis.metadata.processors.MonthFunctionProcessor;
import com.flagwind.mybatis.metadata.processors.NowFunctionProcessor;
import com.flagwind.mybatis.metadata.processors.SecondFunctionProcessor;
import com.flagwind.mybatis.metadata.processors.TimeFunctionProcessor;
import com.flagwind.mybatis.metadata.processors.YearFunctionProcessor;

import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Functions {

	private static String FUNCTION_PATTERN = "(?<method>(?<=@)(\\S+)(?=\\())|(?<argument>(?<=\\()(\\S*)(?=\\)))|(?<alias>(?<= ?as )(\\S+))";

	private static final Map<String, FunctionProcessor> CACHE_FUNCTION_PROCESSOR = Collections
			.synchronizedMap(new WeakHashMap<String, FunctionProcessor>());

	static {
		CACHE_FUNCTION_PROCESSOR.put("now", new NowFunctionProcessor());
		CACHE_FUNCTION_PROCESSOR.put("length", new LengthFunctionProcessor());
		CACHE_FUNCTION_PROCESSOR.put("decode", new DecodeFunctionProcessor());
		// 根据自定义格式显示时间
		CACHE_FUNCTION_PROCESSOR.put("date.format",new DateFormatFunctionProcessor());
		// 显示yyy-mm-dd
		CACHE_FUNCTION_PROCESSOR.put("get.date",new DateFunctionProcessor());
		// 显示yyyy
		CACHE_FUNCTION_PROCESSOR.put("get.year",new YearFunctionProcessor());
		// 显示month
		CACHE_FUNCTION_PROCESSOR.put("get.month",new MonthFunctionProcessor());
		// 显示day
		CACHE_FUNCTION_PROCESSOR.put("get.day",new DayFunctionProcessor());
		// 显示hh24:mi:ss
		CACHE_FUNCTION_PROCESSOR.put("get.time",new TimeFunctionProcessor());
		// 显示hh24
		CACHE_FUNCTION_PROCESSOR.put("get.hour",new HourFunctionProcessor());
		// 显示mi
		CACHE_FUNCTION_PROCESSOR.put("get.minute",new MinuteFunctionProcessor());
		// 显示ss
		CACHE_FUNCTION_PROCESSOR.put("get.second",new SecondFunctionProcessor());
	}


	public static String invoke(String express) {
		if (!express.contains("@")) {
			return express;
		}
		DialectType dialectType = DialectType.parse(TemplateContext.config().getDialect());
		Matcher mat = Pattern.compile(FUNCTION_PATTERN).matcher(express);
		if(mat.groupCount()<=0){
			return express;
		}
 
		String method = mat.find() ? mat.group("method") : null;
		if(StringUtils.isEmpty(method)){
			return express;
		}
		String argument = mat.find() ? mat.group("argument") : null;
		String alias = mat.find() ? mat.group("alias") : null;
		return CACHE_FUNCTION_PROCESSOR.get(method).process(argument,alias, dialectType);

	}

	public static void main(String[] args) {
		System.out.println(invoke("@now() as time"));
	}
}
