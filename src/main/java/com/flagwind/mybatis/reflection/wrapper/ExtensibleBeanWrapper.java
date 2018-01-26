package com.flagwind.mybatis.reflection.wrapper;

import com.flagwind.mybatis.helpers.FieldHelper;
import com.flagwind.mybatis.meta.EntityField;
import com.flagwind.mybatis.utils.StringUtil;
import com.flagwind.persistent.AssociativeProvider;
import com.flagwind.persistent.DiscoveryFactory;
import com.flagwind.persistent.ExtensibleObject;
import com.flagwind.persistent.annotation.Associative;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.property.PropertyTokenizer;
import org.apache.ibatis.reflection.wrapper.BeanWrapper;

import java.util.Arrays;

public class ExtensibleBeanWrapper extends BeanWrapper {
    private Log LOG = LogFactory.getLog(ExtensibleObject.class);
    public ExtensibleObject extensibleObject;
    private MetaObject metaObject;
    public ExtensibleBeanWrapper(MetaObject metaObject, Object object) {
        super(metaObject, object);
        this.metaObject=metaObject;
        this.extensibleObject = (ExtensibleObject) object;
    }

    @Override
    public void set(PropertyTokenizer prop, Object value) {
        EntityField field = FieldHelper.getField(this.extensibleObject.getClass(), prop.getName());
        if (field != null && field.isAnnotationPresent(Associative.class)) {
            Associative associative = field.getAnnotation(Associative.class);
            AssociativeProvider provider = DiscoveryFactory.instance().resolve(associative.source());
            if (provider != null) {
                if (!this.extensibleObject.contains(associative.name())) {
                    String extras = associative.extras();
                    if (StringUtil.isNotEmpty(extras)) {
                        Object obj = provider.associate(Arrays.asList(value, extras).toArray());
                        this.extensibleObject.set(associative.name(), obj);
                    } else {
                        Object obj = provider.associate(value);
                        this.extensibleObject.set(associative.name(), obj);
                    }
                }
            } else {
                LOG.warn(String.format("没有发现属性%s的Associative定义%s %s", prop.getName(), associative.source(), associative.name()));
            }

        }
        super.set(prop, value);
    }
}
