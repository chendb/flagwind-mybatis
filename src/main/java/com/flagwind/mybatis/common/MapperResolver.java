package com.flagwind.mybatis.common;

import com.flagwind.mybatis.exceptions.MapperException;
import com.flagwind.mybatis.provider.EmptyProvider;
import com.flagwind.mybatis.provider.MapperTemplate;
import com.flagwind.mybatis.utils.StringUtil;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.builder.annotation.ProviderSqlSource;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MapperResolver {
    /**
     * 缓存skip结果
     */
    private final Map<String, Boolean> msIdSkip = new ConcurrentHashMap<>();

    /**
     * 注册的接口
     */
    private List<Class<?>> registerClass = new ArrayList<>();

    /**
     * 注册的通用Mapper接口
     */
    private Map<Class<?>, MapperTemplate> registerMapper = new ConcurrentHashMap<>();

    /**
     * 缓存msid和MapperTemplate
     */
    private Map<String, MapperTemplate> msIdCache = new ConcurrentHashMap<>();

    /**
     * 通用Mapper配置
     */
    private Config config = new Config();

    /**
     * 默认构造方法
     */
    public MapperResolver() {
    }

    /**
     * 带配置的构造方法
     *
     * @param properties
     */
    public MapperResolver(Properties properties) {
        this();
        setProperties(properties);
    }

    /**
     * 获取通用Mapper配置
     *
     * @return
     */
    public Config getConfig() {
        return config;
    }

    /**
     * 设置通用Mapper配置
     *
     * @param config
     */
    public void setConfig(Config config) {
        this.config = config;
    }

    /**
     * 通过通用Mapper接口获取对应的MapperTemplate
     *
     * @param mapperClass
     * @return
     * @throws Exception
     */
    private MapperTemplate fromMapperClass(Class<?> mapperClass) {
        Method[] methods = mapperClass.getDeclaredMethods();
        Class<?> templateClass = null;
        Class<?> tempClass = null;
        Set<String> methodSet = new HashSet<String>();
        for (Method method : methods) {
            if (method.isAnnotationPresent(SelectProvider.class)) {
                SelectProvider provider = method.getAnnotation(SelectProvider.class);
                tempClass = provider.type();
                methodSet.add(method.getName());
            } else if (method.isAnnotationPresent(InsertProvider.class)) {
                InsertProvider provider = method.getAnnotation(InsertProvider.class);
                tempClass = provider.type();
                methodSet.add(method.getName());
            } else if (method.isAnnotationPresent(DeleteProvider.class)) {
                DeleteProvider provider = method.getAnnotation(DeleteProvider.class);
                tempClass = provider.type();
                methodSet.add(method.getName());
            } else if (method.isAnnotationPresent(UpdateProvider.class)) {
                UpdateProvider provider = method.getAnnotation(UpdateProvider.class);
                tempClass = provider.type();
                methodSet.add(method.getName());
            }
            if (templateClass == null) {
                templateClass = tempClass;
            } else if (templateClass != tempClass) {
                throw new MapperException("一个通用Mapper中只允许存在一个MapperTemplate子类!");
            }
        }
        if (templateClass == null || !MapperTemplate.class.isAssignableFrom(templateClass)) {
            templateClass = EmptyProvider.class;
        }
        MapperTemplate mapperTemplate = null;
        try {
            mapperTemplate = (MapperTemplate) templateClass.getConstructor(Class.class, MapperResolver.class).newInstance(mapperClass, this);
        } catch (Exception e) {
            throw new MapperException("实例化MapperTemplate对象失败:" + e.getMessage());
        }
        //注册方法
        for (String methodName : methodSet) {
            try {
                mapperTemplate.addMethodMap(methodName, templateClass.getMethod(methodName, MappedStatement.class));
            } catch (NoSuchMethodException e) {
                throw new MapperException(templateClass.getCanonicalName() + "中缺少" + methodName + "方法!");
            }
        }
        return mapperTemplate;
    }

    /**
     * 注册通用Mapper接口
     *
     * @param mapperClass
     */
    public void registerMapper(Class<?> mapperClass) {
        if (!registerMapper.containsKey(mapperClass)) {
            registerClass.add(mapperClass);
            registerMapper.put(mapperClass, fromMapperClass(mapperClass));
        }
        //自动注册继承的接口
        Class<?>[] interfaces = mapperClass.getInterfaces();
        if (interfaces != null && interfaces.length > 0) {
            for (Class<?> anInterface : interfaces) {
                registerMapper(anInterface);
            }
        }
    }

    /**
     * 注册通用Mapper接口
     *
     * @param mapperClass
     */
    public void registerMapper(String mapperClass) {
        try {
            registerMapper(Class.forName(mapperClass));
        } catch (ClassNotFoundException e) {
            throw new MapperException("注册通用Mapper[" + mapperClass + "]失败，找不到该通用Mapper!");
        }
    }

    /**
     * 判断当前的接口方法是否需要进行拦截
     *
     * @param msId
     * @return
     */
    public boolean isMapperMethod(String msId) {
        if (msIdSkip.get(msId) != null) {
            return msIdSkip.get(msId);
        }
        for (Map.Entry<Class<?>, MapperTemplate> entry : registerMapper.entrySet()) {
            if (entry.getValue().supportMethod(msId)) {
                msIdSkip.put(msId, true);
                msIdCache.put(msId, entry.getValue());
                return true;
            }
        }
        msIdSkip.put(msId, false);
        return false;
    }

    /**
     * 判断接口是否包含通用接口
     *
     * @param mapperInterface
     * @return
     */
    public boolean isExtendCommonMapper(Class<?> mapperInterface) {
        for (Class<?> mapperClass : registerClass) {
            if (mapperClass.isAssignableFrom(mapperInterface)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 重新设置SqlSource
     * <p/>
     * 执行该方法前必须使用isMapperMethod判断，否则msIdCache会空
     *
     * @param ms
     */
    public void setSqlSource(MappedStatement ms) {
        MapperTemplate mapperTemplate = msIdCache.get(ms.getId());
        try {
            if (mapperTemplate != null) {
                mapperTemplate.setSqlSource(ms);
            }
        } catch (Exception e) {
            throw new MapperException(e);
        }
    }

    /**
     * 配置属性
     *
     * @param properties
     */
    public void setProperties(Properties properties) {
        config.setProperties(properties);
        //注册通用接口
        String mapper = null;
        if (properties != null) {
            mapper = properties.getProperty("mappers");
        }
        if (StringUtil.isNotEmpty(mapper)) {
            String[] mappers = mapper.split(",");
            for (String mapperClass : mappers) {
                if (mapperClass.length() > 0) {
                    registerMapper(mapperClass);
                }
            }
        }
    }

    /**
     * 如果当前注册的接口为空，自动注册默认接口
     */
    public void ifEmptyRegisterDefaultInterface() {
        if (registerClass.size() == 0) {
            registerMapper("tk.mybatis.mapper.common.Mapper");
        }
    }

    /**
     * 配置指定的接口
     *
     * @param configuration
     * @param mapperInterface
     */
    public void processConfiguration(Configuration configuration, Class<?> mapperInterface) {
        String prefix;
        if (mapperInterface != null) {
            prefix = mapperInterface.getCanonicalName();
        } else {
            prefix = "";
        }
        for (Object object : new ArrayList<Object>(configuration.getMappedStatements())) {
            if (object instanceof MappedStatement) {
                MappedStatement ms = (MappedStatement) object;
                if (ms.getId().startsWith(prefix) && isMapperMethod(ms.getId())) {
                    if (ms.getSqlSource() instanceof ProviderSqlSource) {
                        setSqlSource(ms);
                    }
                }
            }
        }
    }

}
