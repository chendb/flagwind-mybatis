package com.flagwind.mybatis.provider;

import com.flagwind.mybatis.common.MapperResolver;

public class EmptyProvider extends MapperTemplate {

    public EmptyProvider(Class<?> mapperClass, MapperResolver mapperResolver) {
        super(mapperClass, mapperResolver);
    }

    @Override
    public boolean supportMethod(String msId) {
        return false;
    }
}
