package com.flagwind.mybatis.spring.autoconfigure;

import com.flagwind.persistent.AssociativeProviderFactory;
import com.flagwind.persistent.AssociativeProviderFactory.Discovery;
import com.flagwind.mybatis.utils.TypeUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

public class DiscoveryAutoConfiguration implements BeanFactoryAware {
	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		AssociativeProviderFactory.setInstance(new AssociativeProviderFactory(new Discovery() {

			@Override
			public <T> T discover(String name) {
				return TypeUtils.castTo(beanFactory.getBean(name));
			}

			@Override
			public <T> T discover(Class<?> serviceType) {
				return TypeUtils.castTo(beanFactory.getBean(serviceType.getSimpleName()));
			}

		}));
	}
}
