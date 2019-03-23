package com.flagwind.mybatis.metadata.processors;

import com.flagwind.mybatis.code.DialectType;
import com.flagwind.mybatis.exceptions.MapperException;
import com.flagwind.mybatis.metadata.FunctionProcessor;
import com.flagwind.mybatis.utils.StringUtil;

/**
 * 当前时间函数
 */
public class NowFunctionProcessor implements FunctionProcessor {

	@Override
	public String process(String arguments, String alias, DialectType dialectType) {
		String suffix = (StringUtil.isEmpty(alias) ? "" : (" as " + alias));
		switch (dialectType) {
			case Oracle:
				return "sysdate()" + suffix;
			case MySQL:
				return "now()" + suffix;
			default:
				throw new MapperException("该函数没有针对" + dialectType + "类型数据库实现");
		}
	}
}
