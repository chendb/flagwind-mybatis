package com.flagwind.mybatis.spring.autoconfigure;

import com.flagwind.associative.AssociativeProviderFactory;
import com.flagwind.associative.AssociativeProviderFactory.Discovery;

import org.springframework.beans.BeansException;
		import org.springframework.beans.factory.BeanFactory;
		import org.springframework.beans.factory.BeanFactoryAware;


public class DiscoveryAutoConfiguration implements BeanFactoryAware
{
	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException
	{
		AssociativeProviderFactory.setInstance(new AssociativeProviderFactory(new Discovery(){

			@Override
			public <T> T discover(String name) {
				return (T) beanFactory.getBean(name);
			}

			@Override
			public <T> T discover(Class<?> serviceType) {
				return (T) beanFactory.getBean(serviceType.getSimpleName());
			}
			
		}));
	}
}
