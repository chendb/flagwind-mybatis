package com.flagwind.mybatis.spring.boot;

import com.flagwind.mybatis.common.MapperResolver;

public class MapperFactoryBean<T> extends org.mybatis.spring.mapper.MapperFactoryBean<T> {

    private MapperResolver mapperResolver;

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

    public void setMapperResolver(MapperResolver mapperResolver) {
        this.mapperResolver = mapperResolver;
    }
}
