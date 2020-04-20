
package com.flagwind.mybatis.definition.interceptor.pagination;


import com.flagwind.mybatis.code.DatabaseType;
import com.flagwind.mybatis.definition.interceptor.pagination.dialects.DialectRegistry;
import com.flagwind.mybatis.definition.interceptor.pagination.dialects.IDialect;
import com.flagwind.mybatis.exceptions.MapperException;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;


public class DialectFactory {

    private static final DialectRegistry DIALECT_REGISTRY = new DialectRegistry();

    /**
     * 自定义方言缓存
     */
    private static final Map<String, IDialect> DIALECT_CACHE = new ConcurrentHashMap<>();


    private static <T> T newInstance(String className) {
        try {
            Class tClass = Class.forName(className);
            Constructor constructor = tClass.getConstructor();
            return (T) constructor.newInstance(new Object[]{});
        } catch (Exception e) {
            throw new RuntimeException("Cannot create instance: " + className, e);
        }
    }

    /**
     * 获取实现方言
     *
     * @param dialectClazz 方言全类名
     * @return 方言实现对象
     * @since 3.3.1
     */
    public static IDialect getDialect(String dialectClazz) {
        return DIALECT_CACHE.computeIfAbsent(dialectClazz, DialectFactory::newInstance);
    }

    public static IDialect getDialect(DatabaseType dbType) {
        return Optional.ofNullable(DIALECT_REGISTRY.getDialect(dbType))
                .orElseThrow(() -> new MapperException(String.format("%s database not supported.", dbType.getDb())));
    }
}
