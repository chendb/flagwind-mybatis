package com.flagwind.mybatis.spring;

import com.flagwind.mybatis.common.MapperResolver;
import com.flagwind.mybatis.utils.StringUtil;
import com.flagwind.persistent.AbstractRepository;
import com.flagwind.persistent.Discovery;
import com.flagwind.persistent.DiscoveryFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;

import java.util.Properties;

public class MapperScannerConfigurer extends org.mybatis.spring.mapper.MapperScannerConfigurer {
    private MapperResolver mapperResolver = new MapperResolver();


    public void setDiscovery(Discovery discovery) {
        this.discovery = discovery;
        DiscoveryFactory.instance().initialize(discovery);
    }

    private Discovery discovery;

    @Override
    public void setMarkerInterface(Class<?> superClass) {
        super.setMarkerInterface(superClass);
        if (AbstractRepository.class.isAssignableFrom(superClass)) {
            mapperResolver.registerMapper(superClass);
        }
    }

    public MapperResolver getMapperResolver() {
        return mapperResolver;
    }

    public void setMapperResolver(MapperResolver mapperResolver) {
        this.mapperResolver = mapperResolver;
    }

    /**
     * 属性注入
     *
     * @param properties
     */
    public void setProperties(Properties properties) {
        mapperResolver.setProperties(properties);
    }

    /**
     * 注册完成后，对MapperFactoryBean的类进行特殊处理
     *
     * @param registry
     */
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) {
        super.postProcessBeanDefinitionRegistry(registry);

        //如果没有注册过接口，就注册默认的Mapper接口
        this.mapperResolver.ifEmptyRegisterDefaultInterface();
        String[] names = registry.getBeanDefinitionNames();
        GenericBeanDefinition definition;
        for (String name : names) {
            BeanDefinition beanDefinition = registry.getBeanDefinition(name);
            if (beanDefinition instanceof GenericBeanDefinition) {
                definition = (GenericBeanDefinition) beanDefinition;
                if (StringUtil.isNotEmpty(definition.getBeanClassName())
                        && definition.getBeanClassName().equals("org.mybatis.spring.mapper.MapperFactoryBean")) {
                    definition.setBeanClass(MapperFactoryBean.class);
                    definition.getPropertyValues().add("mapperResolver", this.mapperResolver);
                }
            }
        }
    }
}
