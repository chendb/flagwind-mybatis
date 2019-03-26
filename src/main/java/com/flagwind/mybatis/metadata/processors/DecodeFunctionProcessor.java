package com.flagwind.mybatis.metadata.processors;

import com.flagwind.mybatis.code.DialectType;
import com.flagwind.mybatis.exceptions.MapperException;
import com.flagwind.mybatis.metadata.FunctionProcessor;
import org.apache.commons.lang3.StringUtils;

/**
 * 使用示例：@decode(status,1:'在线',2:'离线','未知');
 */
public class DecodeFunctionProcessor implements FunctionProcessor
{

	@Override
	public String process(String arguments, String alias, DialectType dialectType) {
		String suffix = (StringUtils.isEmpty(alias) ? "" : (" as " + alias));

		switch (dialectType) {
			case Oracle:
			{
				String[] args = arguments.split("[,:]");
				return "decode(" + StringUtils.join(args,",") + ")" + suffix;
			}
			case MySQL:
			{
				String[] args = arguments.split(",");
				StringBuilder sb = new StringBuilder();
				String column = args[0];
				String when = " when " + column + " = ";
				sb.append(" case ").append(column);
				for(int i = 1; i <= args.length - 1; i++)
				{
					String[] kv = args[i].split(":");
					if(kv.length == 2)
					{
						sb.append(when).append(kv[0]).append(" then ").append(kv[1]);
					}
					else
					{
						sb.append(" else ").append(kv[0]);
					}
				}
				sb.append(" end ");

				return "(" + sb.toString() + ")" + suffix;
			}
			default:
				throw new MapperException("该函数没有针对" + dialectType + "类型数据库实现");
		}
	}
 
	public static void main(String[] args1) {
		String arguments = "status,1:'在线',2:'离线','未知'";
		System.out.println("MySQL:"+(new DecodeFunctionProcessor()).process(arguments,null,DialectType.MySQL));;
		System.out.println("Oracle:"+(new DecodeFunctionProcessor()).process(arguments,null,DialectType.Oracle));;
	}

}