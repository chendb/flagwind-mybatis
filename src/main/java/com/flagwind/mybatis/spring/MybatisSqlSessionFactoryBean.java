package com.flagwind.mybatis.spring;

import com.flagwind.mybatis.utils.PackageUtils;
import org.springframework.util.StringUtils;

public class MybatisSqlSessionFactoryBean extends org.mybatis.spring.SqlSessionFactoryBean
{

	@Override
	public void setTypeAliasesPackage(String typeAliasesPackage)
	{

		super.setTypeAliasesPackage(standardPackage(typeAliasesPackage));
	}

	@Override
	public void setTypeHandlersPackage(String typeHandlersPackage)
	{
		super.setTypeHandlersPackage(standardPackage(typeHandlersPackage));
	}


	private String standardPackage(String packages)
	{
		StringBuilder builder = new StringBuilder();
		if(StringUtils.hasLength(packages) && packages.contains("*"))
		{
			String[] array = StringUtils.tokenizeToStringArray(packages, ",; \t\n");
			for(String one : array)
			{
				if(one.contains("*"))
				{
					this.appendArrayToBuilder(builder, PackageUtils.convertTypeAliasesPackage(one));
				}
				else
				{
					builder.append(one).append(",");
				}
			}
			builder.deleteCharAt(builder.length() - 1);
		}
		else
		{
			builder.append(packages);
		}
		return builder.toString();
	}

	private void appendArrayToBuilder(StringBuilder builder, String[] array)
	{
		if(builder != null && array != null && array.length > 0)
		{
			for(String item : array)
			{
				builder.append(item).append(",");
			}
		}
	}
}