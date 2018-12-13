package com.flagwind.mybatis.spring.autoconfigure;

		import com.flagwind.persistent.Discovery;
		import com.flagwind.persistent.DiscoveryFactory;
		import org.springframework.beans.BeansException;
		import org.springframework.beans.factory.BeanFactory;
		import org.springframework.beans.factory.BeanFactoryAware;


public class DiscoveryAutoConfiguration implements BeanFactoryAware
{
	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException
	{
		DiscoveryFactory.instance().initialize(new Discovery()
		{
			@Override
			public <T> T discover(String name)
			{
				return (T) beanFactory.getBean(name);
			}

			@Override
			public <T> T discover(Class<?> serviceType)
			{
				return (T) beanFactory.getBean(serviceType.getSimpleName());
			}
		});
	}
}
