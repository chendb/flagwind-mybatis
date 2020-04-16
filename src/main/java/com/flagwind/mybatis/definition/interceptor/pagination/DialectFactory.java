/*
 * Copyright (c) 2011-2020, baomidou (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.flagwind.mybatis.definition.interceptor.pagination;


import com.flagwind.mybatis.code.DatabaseType;
import com.flagwind.mybatis.definition.interceptor.pagination.dialects.DialectRegistry;
import com.flagwind.mybatis.definition.interceptor.pagination.dialects.IDialect;
import com.flagwind.mybatis.exceptions.MapperException;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 分页方言工厂类
 *
 * @author hubin
 * @since 2016-01-23
 */
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
