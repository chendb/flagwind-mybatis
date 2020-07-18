package com.flagwind.mybatis.metadata.processors;

import com.flagwind.mybatis.code.DatabaseType;
import com.flagwind.mybatis.exceptions.MapperException;
import com.flagwind.mybatis.metadata.FunctionProcessor;
import org.apache.commons.lang3.StringUtils;

/**
 * 使用示例：@decode(status,1:0,2:3,4:9,8);
 */
public class SubstringFunctionProcessor implements FunctionProcessor
{

	@Override
	public String process(String arguments, String alias, DatabaseType databaseType) {
		String suffix = (StringUtils.isEmpty(alias) ? "" : (" as " + alias));
		String[] args = arguments.split("[,:]");
		switch (databaseType) {
			case Oracle:
			case DM:
			{
				return "substr(" + StringUtils.join(args,",") + ")" + suffix;
			}
			case MySQL:
			{
				return "substring(" + StringUtils.join(args,",") + ")" + suffix;
			}
			default:
				throw new MapperException("该函数没有针对" + databaseType + "类型数据库实现");
		}
	}
 

	public static void main(String[] args1) {
		String arguments = "'dddddd',1,2";
		System.out.println("MySQL:"+(new SubstringFunctionProcessor()).process(arguments,null,DatabaseType.MySQL));
        System.out.println("Oracle:"+(new SubstringFunctionProcessor()).process(arguments,null,DatabaseType.Oracle));
    }

}