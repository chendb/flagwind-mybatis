package com.flagwind.mybatis.definition.result;

import com.flagwind.mybatis.reflection.entities.EntityField;
import com.flagwind.mybatis.reflection.entities.EntityTypeHolder;
import com.flagwind.persistent.AssociativeEntry;
import com.flagwind.persistent.AssociativeProvider;
import com.flagwind.persistent.DiscoveryFactory;
import com.flagwind.persistent.ExtensibleObject;
import com.flagwind.persistent.annotation.Associative;
import com.flagwind.persistent.annotation.Associatives;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.property.PropertyTokenizer;
import org.apache.ibatis.reflection.wrapper.BeanWrapper;

public class ExtensibleBeanWrapper extends BeanWrapper
{
	private Log LOG = LogFactory.getLog(ExtensibleObject.class);
	private ExtensibleObject extensibleObject;

	public ExtensibleBeanWrapper(MetaObject metaObject, Object object)
	{
		super(metaObject, object);
		this.extensibleObject = (ExtensibleObject) object;
	}

	@Override
	public void set(PropertyTokenizer prop, Object value)
	{
		EntityField field = EntityTypeHolder.getField(this.extensibleObject.getClass(), prop.getName());
		if(field == null)
		{
			super.set(prop, value);
			return;
		}

		if(field.isAnnotationPresent(Associatives.class))
		{
			Associatives associatives = field.getAnnotation(Associatives.class);
			for(Associative associative : associatives.value())
			{
				setAssociativeField(associative, prop, value);
			}

		}
		if(field.isAnnotationPresent(Associative.class))
		{
			Associative associative = field.getAnnotation(Associative.class);
			setAssociativeField(associative, prop, value);
		}
		super.set(prop, value);
	}

	private void setAssociativeField(Associative associative, PropertyTokenizer prop, Object value)
	{
		AssociativeProvider provider = DiscoveryFactory.instance().resolve(associative.source());
		if(provider != null)
		{
			if(!this.extensibleObject.contains(associative.name()))
			{
				AssociativeEntry entry = new AssociativeEntry(associative);
				entry.excute(extensibleObject, value);
			}
		}
		else
		{
			LOG.warn(String.format("没有发现属性%s的Associative定义%s %s", prop.getName(), associative.source(), associative.name()));
		}
	}
}
