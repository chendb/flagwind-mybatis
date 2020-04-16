package com.flagwind.mybatis.spring.boot;

import org.springframework.core.env.Environment;

import java.lang.reflect.Method;

/**
 * @author liuzh
 * @since 1.2.1
 */
public abstract class SpringBootBindUtils
{

	private static final IBind BIND;

	static
	{
		BIND = new SpringBoot2Bind();

	}

	public static <T> T bind(Environment environment, Class<T> targetClass, String prefix)
	{
		return BIND.bind(environment, targetClass, prefix);
	}

	public interface IBind
	{
		<T> T bind(Environment environment, Class<T> targetClass, String prefix);
	}

	/**
	 * 使用 Spring Boot 2.x 方式绑定
	 */
	public static class SpringBoot2Bind implements IBind
	{
		@Override
		public <T> T bind(Environment environment, Class<T> targetClass, String prefix)
		{
			try
			{
				Class<?> bindClass = Class.forName("org.springframework.boot.context.properties.bind.Binder");
				Method getMethod = bindClass.getDeclaredMethod("get", Environment.class);
				Method bindMethod = bindClass.getDeclaredMethod("bind", String.class, Class.class);
				Object bind = getMethod.invoke(null, environment);
				Object bindResult = bindMethod.invoke(bind, prefix, targetClass);
				Method resultGetMethod = bindResult.getClass().getDeclaredMethod("get");
				Method isBoundMethod = bindResult.getClass().getDeclaredMethod("isBound");
				if((Boolean) isBoundMethod.invoke(bindResult))
				{
					return (T) resultGetMethod.invoke(bindResult);
				}
				return null;
			}
			catch(Exception e)
			{
				throw new RuntimeException(e);
			}
		}
	}

}


