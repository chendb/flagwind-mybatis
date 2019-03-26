package com.flagwind.mybatis.metadata.processors;

import com.flagwind.mybatis.code.DialectType;
import com.flagwind.mybatis.exceptions.MapperException;
import com.flagwind.mybatis.metadata.FunctionProcessor;
import org.apache.commons.lang3.StringUtils;

/**
 * 使用示例：@left(name,2);
 */
public class LeftFunctionProcessor implements FunctionProcessor
{

	@Override
	public String process(String arguments, String alias, DialectType dialectType) {
		String suffix = (StringUtils.isEmpty(alias) ? "" : (" as " + alias));
		String[] args = arguments.split("[,:]");
		switch (dialectType) {
			case Oracle:
			{
				return "substr(" + args[0]+ ",1,"+args[1]+")" + suffix;
			}
			case MySQL:
			{
				return "left(" + args[0]+ ","+args[1]+")" + suffix;
			}
			default:
				throw new MapperException("该函数没有针对" + dialectType + "类型数据库实现");
		}
	}

	public static void main(String[] args1) {
		String arguments = "id,2";
		System.out.println((new LeftFunctionProcessor()).process(arguments,null,DialectType.MySQL));;
		System.out.println((new LeftFunctionProcessor()).process(arguments,null,DialectType.Oracle));;
	}

}