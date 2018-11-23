package com.flagwind.mybatis.spring.boot;

import com.flagwind.mybatis.common.TemplateContext;

public class MapperFactoryBean<T> extends org.mybatis.spring.mapper.MapperFactoryBean<T> {

    private TemplateContext mapperResolver;

    public MapperFactoryBean() {
    }

    public MapperFactoryBean(Class<T> mapperInterface) {
        super(mapperInterface);
    }


    @Override
    protected void checkDaoConfig() {
        super.checkDaoConfig();
        if (mapperResolver.isExtendCommonMapper(getObjectType())) {
            mapperResolver.processConfiguration(getSqlSession().getConfiguration(), getObjectType());
        }
    }

    public void setMapperResolver(TemplateContext mapperResolver) {
        this.mapperResolver = mapperResolver;
    }
}
