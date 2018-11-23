package com.flagwind.mybatis.definition.template;

import com.flagwind.mybatis.common.TemplateContext;

public class EmptyTemplate extends MapperTemplate {

    public EmptyTemplate(Class<?> mapperClass, TemplateContext mapperResolver) {
        super(mapperClass, mapperResolver);
    }

    @Override
    public boolean supportMethod(String msId) {
        return false;
    }
}
