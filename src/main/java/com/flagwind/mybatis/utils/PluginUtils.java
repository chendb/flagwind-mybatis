package com.flagwind.mybatis.utils;

import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.springframework.util.StringUtils;

import java.lang.reflect.Proxy;
import java.util.Properties;

/**
 * @author chendb
 * @description: 插件工具类
 * @date 2020-04-15 22:30:00
 */
public final class PluginUtils {
    public static final String DELEGATE_BOUNDSQL_SQL = "delegate.boundSql.sql";

    private PluginUtils() {
        // to do nothing
    }

    /**
     * 获得真正的处理对象,可能多层代理.
     */
    public static <T> T realTarget(Object target) {
        if (Proxy.isProxyClass(target.getClass())) {
            MetaObject metaObject = SystemMetaObject.forObject(target);
            return realTarget(metaObject.getValue("h.target"));
        }
        return (T) target;
    }

    /**
     * 根据 key 获取 Properties 的值
     */
    public static String getProperty(Properties properties, String key) {
        String value = properties.getProperty(key);
        return StringUtils.isEmpty(value) ? null : value;
    }
}