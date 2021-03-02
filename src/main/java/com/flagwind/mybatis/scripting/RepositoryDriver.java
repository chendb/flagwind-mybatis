package com.flagwind.mybatis.scripting;

import com.flagwind.mybatis.FlagwindConfiguration;
import com.flagwind.mybatis.FlagwindMapperRegistry;
import com.flagwind.mybatis.scripting.method.*;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author chendb
 * @description:
 * @date 2021-02-28 09:51:08
 */
public class RepositoryDriver extends XMLLanguageDriver implements LanguageDriver {

    private Map<String,XmlScriptMethod> methods = new HashMap<>();

    public RepositoryDriver() {
        super();
        supportScriptMethod(new ClauseScriptMethod());
        supportScriptMethod(new ColumnsScriptMethod());
        supportScriptMethod(new IdScriptMethod());
        supportScriptMethod(new InScriptMethod());
        supportScriptMethod(new SetsScriptMethod());
        supportScriptMethod(new SortsScriptMethod());
        supportScriptMethod(new TableScriptMethod());
        supportScriptMethod(new ValuesScriptMethod());
    }

    public void supportScriptMethod(XmlScriptMethod scriptMethod){
        methods.put(scriptMethod.pattern(),scriptMethod);
    }

    @Override
    public SqlSource createSqlSource(Configuration configuration, String script, Class<?> parameterType) {

        FlagwindConfiguration flagwindConfiguration = (FlagwindConfiguration) configuration;
        //获取当前mapper
        Class<?> mapperClass = FlagwindMapperRegistry.getCurrentMapper();

        if (mapperClass == null) {
            throw new RuntimeException("解析SQL出错");
        }
        //处理SQL
        if (mapperClass != null) {
            List<Class> entityClasses = new ArrayList<>();

            if (mapperClass.isAnnotationPresent(KnownEntityType.class)) {
                entityClasses.addAll(Arrays.stream(mapperClass.getAnnotation(KnownEntityType.class).value()).collect(Collectors.toList()));
            }

            Class<?>[] generics = getMapperGenerics(mapperClass);
            if (generics != null && generics.length > 0) {
                entityClasses.add(0, generics[0]);
            }

            for (XmlScriptMethod method : this.methods.values()) {
                if (method.matches(script)) {
                    script = method.execute(flagwindConfiguration, script, entityClasses);
                }
            }

        }

        return super.createSqlSource(configuration, script, parameterType);
    }

    /**
     * 获取泛型
     *
     * @param mapperClass
     * @return
     */
    private Class<?>[] getMapperGenerics(Class<?> mapperClass) {

        Type[] types = mapperClass.getGenericInterfaces();
        for (Type type : types) {
            if (type instanceof ParameterizedType) {
                ParameterizedType t = (ParameterizedType) type;
                if (t.getRawType() == mapperClass || mapperClass.isAssignableFrom((Class<?>) t.getRawType())) {
                    return Arrays.stream(t.getActualTypeArguments()).map(s -> (Class<?>) s).toArray(Class[]::new);
                }
            }
        }
        return null;
    }


}
