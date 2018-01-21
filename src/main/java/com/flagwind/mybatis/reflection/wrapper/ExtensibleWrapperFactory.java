package com.flagwind.mybatis.reflection.wrapper;

import com.flagwind.persistent.ExtensibleObject;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.wrapper.ObjectWrapper;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;

public class ExtensibleWrapperFactory implements ObjectWrapperFactory {
    @Override
    public boolean hasWrapperFor(Object object) {
        return object != null && object instanceof ExtensibleObject;
    }

    @Override
    public ObjectWrapper getWrapperFor(MetaObject metaObject, Object object) {
        return new ExtensibleBeanWrapper(metaObject,object);
    }
}
