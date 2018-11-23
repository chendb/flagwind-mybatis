package com.flagwind.mybatis.reflection.entities;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class EntityTypeHolder
{
	private static final Map<Class<?>, EntityType> CACHE = Collections.synchronizedMap(new WeakHashMap<Class<?>, EntityType>());


	public static EntityType getEntityType(Class<?> clzss)
	{
		if(CACHE.containsKey(clzss))
		{
			return CACHE.get(clzss);
		}
		synchronized(CACHE)
		{

			if(!CACHE.containsKey(clzss))
			{
				EntityType entityType = EntityTypeUtils.getEntityType(clzss);
				CACHE.put(clzss, entityType);
			}
			return CACHE.get(clzss);
		}

	}

	public static List<EntityField> getFields(Class<?> clzss)
	{
		EntityType entityType = getEntityType(clzss);
		return entityType.getFields();
	}


	public static EntityField getField(Class<?> entityClass, String name)
	{
		return getFields(entityClass).stream().filter(g -> g.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
	}
}
