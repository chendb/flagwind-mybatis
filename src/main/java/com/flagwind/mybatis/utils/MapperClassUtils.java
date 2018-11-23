package com.flagwind.mybatis.utils;

import com.flagwind.mybatis.exceptions.MapperException;
import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.cache.decorators.SoftCache;
import org.apache.ibatis.cache.impl.PerpetualCache;
import org.apache.ibatis.mapping.MappedStatement;


public class MapperClassUtils
{

    public static final Cache CLASS_CACHE = new SoftCache(new PerpetualCache("MAPPER_CLASS_CACHE"));

    /**
     * 根据msId获取接口类
     *
     * @param msId
     * @return Mapper类型
     */
    public static Class<?> getMapperClass(String msId) {
        if (!msId.contains(".")) {
            throw new MapperException("当前MappedStatement的id=" + msId + ",不符合MappedStatement的规则!");
        }
        String mapperClassStr = msId.substring(0, msId.lastIndexOf("."));
        //由于一个接口中的每个方法都会进行下面的操作，因此缓存
        Class<?> mapperClass = (Class<?>) CLASS_CACHE.getObject(mapperClassStr);
        if(mapperClass != null){
            return mapperClass;
        }
        ClassLoader[] classLoader = getClassLoaders();

        for (ClassLoader cl : classLoader) {
            if (null != cl) {
                try {
                    mapperClass = Class.forName(mapperClassStr, true, cl);
                    if (mapperClass != null) {
                        break;
                    }
                } catch (ClassNotFoundException e) {
                    // we'll ignore this until all class loaders fail to locate the class
                }
            }
        }
        if (mapperClass == null) {
            throw new MapperException("class loaders failed to locate the class " + mapperClassStr);
        }
        CLASS_CACHE.putObject(mapperClassStr, mapperClass);
        return mapperClass;
    }

    private static ClassLoader[] getClassLoaders() {
        return new ClassLoader[]{Thread.currentThread().getContextClassLoader(), MapperClassUtils.class.getClassLoader()};
    }

    /**
     * 获取执行的方法名
     *
     * @param ms
     * @return 方法名
     */
    public static String getMethodName(MappedStatement ms) {
        return getMethodName(ms.getId());
    }

    /**
     * 获取执行的方法名
     *
     * @param msId
     * @return 方法名
     */
    public static String getMethodName(String msId) {
        return msId.substring(msId.lastIndexOf(".") + 1);
    }

}