package com.flagwind.mybatis.spring.boot;

import com.flagwind.mybatis.common.TemplateContext;

public class MapperFactoryBean<T> extends org.mybatis.spring.mapper.MapperFactoryBean<T> {

    private TemplateContext templateContext;

    public MapperFactoryBean() {
    }

    public MapperFactoryBean(Class<T> mapperInterface) {
        super(mapperInterface);
    }


    @Override
    protected void checkDaoConfig() {
        super.checkDaoConfig();
        if (templateContext.isExtendCommonMapper(getObjectType())) {
            templateContext.processConfiguration(getSqlSession().getConfiguration(), getObjectType());
        }
    }

    public void setTemplateContext(TemplateContext templateContext) {
        this.templateContext = templateContext;
    }
}
