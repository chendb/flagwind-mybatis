package com.flagwind.mybatis.metadata;

		import com.flagwind.mybatis.code.DialectType;
		import com.flagwind.mybatis.common.Config;

/**
 * 自定义函数处理接口
 */
public interface FunctionProcessor
{
	String process(String arguments,String alias,DialectType dialectType);
}
