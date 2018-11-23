package com.flagwind.mybatis.reflection.entities;


import com.flagwind.mybatis.exceptions.MapperException;

import javax.persistence.Entity;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.*;
import java.util.*;

/**
 * POJO属性解析工具类
 */
public class EntityTypeUtils
{

    public static EntityType getEntityType(Class<?> entityClass)
    {
        EntityType entityType = new EntityType(entityClass);
        entityType.getFields().addAll(getFields(entityClass));
        return entityType;
    }

    /**
     * 获取全部的Field
     * @param entityClass
     * @return
     */

    public static List<EntityField> getFields(Class<?> entityClass)
    {
        List<EntityField> fields = _getFields(entityClass, null, null);
        List<EntityField> properties = getProperties(entityClass);
        Set<EntityField> usedSet = new HashSet<EntityField>();
        for(EntityField field : fields)
        {
            for(EntityField property : properties)
            {
                if(!usedSet.contains(property) && field.getName().equals(property.getName()))
                {
                    //泛型的情况下通过属性可以得到实际的类型
                    field.setJavaType(property.getJavaType());
                    break;
                }
            }
        }
        return fields;
    }

    public static PropertyDescriptor getPropertyDescriptor(Class<?> clazz, String propertyName)
    {
        StringBuffer sb = new StringBuffer();//构建一个可变字符串用来构建方法名称
        Method setMethod;
        Method getMethod;
        PropertyDescriptor pd = null;
        try
        {
            Field f = clazz.getDeclaredField(propertyName);//根据字段名来获取字段
            if(f != null)
            {
                //构建方法的后缀
                String methodEnd = propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);

                sb.append("get").append(methodEnd);//构建get方法
                //构建get 方法
                getMethod = clazz.getDeclaredMethod(sb.toString());

                if(getMethod == null)
                {
                    sb.delete(0, sb.length());//清空整个可变字符串
                    sb.append("is").append(methodEnd);//构建get方法
                    getMethod = clazz.getDeclaredMethod(sb.toString());
                }

                sb.delete(0, sb.length());//清空整个可变字符串
                sb.append("set").append(methodEnd);//构建set方法
                setMethod = clazz.getDeclaredMethod(sb.toString(), f.getType());

                //构建一个属性描述器 把对应属性 propertyName 的 get 和 set 方法保存到属性描述器中
                pd = new PropertyDescriptor(propertyName, getMethod, setMethod);
            }
        }
        catch(Exception ex)
        {
            //ex.printStackTrace();
        }

        return pd;
    }

    /**
     * 获取全部的Field，仅仅通过Field获取
     * @param entityClass
     * @param fieldList
     * @param level
     * @return
     */
    private static List<EntityField> _getFields(Class<?> entityClass, List<EntityField> fieldList, Integer level)
    {
        if(fieldList == null)
        {
            fieldList = new ArrayList<>();
        }
        if(level == null)
        {
            level = 0;
        }
        if(entityClass.equals(Object.class))
        {
            return fieldList;
        }
        Field[] fields = entityClass.getDeclaredFields();
        int index = 0;
        for(int i = 0; i < fields.length; i++)
        {
            Field field = fields[i];
            //排除静态字段，解决bug#2
            if(!Modifier.isStatic(field.getModifiers()))
            {
                if(level.intValue() != 0)
                {
                    //将父类的字段放在前面
                    fieldList.add(index, new EntityField(field, getPropertyDescriptor(entityClass, field.getName())));
                    index++;
                }
                else
                {
                    fieldList.add(new EntityField(field, getPropertyDescriptor(entityClass, field.getName())));
                }
            }
        }
        Class<?> superClass = entityClass.getSuperclass();
        if(superClass != null && !superClass.equals(Object.class) && (superClass.isAnnotationPresent(Entity.class) || (!Map.class.isAssignableFrom(superClass) && !Collection.class.isAssignableFrom(superClass))))
        {
            return _getFields(entityClass.getSuperclass(), fieldList, ++level);
        }
        return fieldList;
    }

    /**
     * 通过方法获取属性
     * @param entityClass
     * @return
     */
    private static List<EntityField> getProperties(Class<?> entityClass)
    {
        List<EntityField> entityFields = new ArrayList<>();
        BeanInfo beanInfo;
        try
        {
            beanInfo = Introspector.getBeanInfo(entityClass);
        }
        catch(IntrospectionException e)
        {
            throw new MapperException(e);
        }
        PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
        for(PropertyDescriptor desc : descriptors)
        {
            String propertyName = "class";
            if(!propertyName.equals(desc.getName()))
            {
                entityFields.add(new EntityField(null, desc));
            }
        }
        return entityFields;
    }

}
