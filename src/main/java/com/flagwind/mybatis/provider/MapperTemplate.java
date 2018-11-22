package com.flagwind.mybatis.provider;

import static com.flagwind.mybatis.utils.MsUtil.getMapperClass;
import static com.flagwind.mybatis.utils.MsUtil.getMethodName;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.flagwind.mybatis.common.Config;
import com.flagwind.mybatis.common.MapperResolver;
import com.flagwind.mybatis.exceptions.MapperException;
import com.flagwind.mybatis.helpers.AssociationSqlHelper;
import com.flagwind.mybatis.helpers.EntityHelper;
import com.flagwind.mybatis.meta.EntityColumn;
import com.flagwind.mybatis.meta.EntityTable;
import com.flagwind.mybatis.reflection.swapper.ResultMapSwapper;
import com.flagwind.mybatis.reflection.swapper.ResultMapSwapperHolder;
import com.flagwind.mybatis.utils.StringUtil;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.scripting.xmltags.DynamicSqlSource;
import org.apache.ibatis.scripting.xmltags.SqlNode;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;

public abstract class MapperTemplate {
    private static final XMLLanguageDriver languageDriver = new XMLLanguageDriver();
    private Map<String, Method> methodMap = new ConcurrentHashMap<String, Method>();
    private Map<String, Class<?>> entityClassMap = new ConcurrentHashMap<String, Class<?>>();
    protected Class<?> mapperClass;
    protected MapperResolver mapperResolver;

    public MapperTemplate(Class<?> mapperClass, MapperResolver mapperResolver) {
        this.mapperClass = mapperClass;
        this.mapperResolver = mapperResolver;
    }

    /**
     * 该方法仅仅用来初始化ProviderSqlSource
     *
     * @param record
     * @return
     */
    public String dynamicSQL(Object record) {
        return "dynamicSQL";
    }

 
    public String getDialect(){
		return mapperResolver.getConfig().getDialect();
	}

    /**
     * 添加映射方法
     *
     * @param methodName
     * @param method
     */
    public void addMethodMap(String methodName, Method method) {
        methodMap.put(methodName, method);
    }

    public Config getConfig(){
        return mapperResolver.getConfig();
    }

    /**
     * 获取IDENTITY值的表达式
     *
     * @param column
     * @return
     */
    public String getIdentity(EntityColumn column) {
        return MessageFormat.format(getConfig().getIdentity(), column.getSequenceName(), column.getColumn(), column.getProperty(), column.getTable().getName());
    }
 

    /**
     * 是否支持该通用方法
     *
     * @param msId
     * @return
     */
    public boolean supportMethod(String msId) {
        Class<?> mapperClass = getMapperClass(msId);
        if (mapperClass != null && this.mapperClass.isAssignableFrom(mapperClass)) {
            String methodName = getMethodName(msId);
            return methodMap.get(methodName) != null;
        }
        return false;
    }

    /**
     * 设置返回值类型 - 为了让typeHandler在select时有效，改为设置resultMap
     *
     * @param ms
     * @param entityClass
     */
    protected void setResultType(MappedStatement ms, Class<?> entityClass) {

        EntityTable entityTable = EntityHelper.getEntityTable(entityClass);
        if (AssociationSqlHelper.hasAssociation(entityClass)) {
            ResultMapSwapper swapper = ResultMapSwapperHolder.getSwapper(ms.getConfiguration());
            ResultMap newResultMap = swapper.reloadResultMap(ms.getResource(), entityClass.getSimpleName(), entityTable.getEntityClass(), mapperResolver.getConfig().getStyle());
            List<ResultMap> newResultMaps = new ArrayList<>();
            newResultMaps.add(newResultMap);
            MetaObject metaObject = SystemMetaObject.forObject(ms);
            metaObject.setValue("resultMaps", Collections.unmodifiableList(newResultMaps));
        } else {
            List<ResultMap> resultMaps = new ArrayList<>();
            resultMaps.add(entityTable.getResultMap(ms.getConfiguration()));
            MetaObject metaObject = SystemMetaObject.forObject(ms);
            metaObject.setValue("resultMaps", Collections.unmodifiableList(resultMaps));
        }
    }

    /**
     * 重新设置SqlSource
     *
     * @param ms
     * @param sqlSource
     */
    protected void setSqlSource(MappedStatement ms, SqlSource sqlSource) {
        MetaObject msObject = SystemMetaObject.forObject(ms);
        msObject.setValue("sqlSource", sqlSource);
    }

    /**
     * 重新设置SqlSource
     *
     * @param ms
     * @throws java.lang.reflect.InvocationTargetException
     * @throws IllegalAccessException
     */
    public void setSqlSource(MappedStatement ms)
    {
        if (this.mapperClass == getMapperClass(ms.getId())) {
            throw new MapperException("请不要配置或扫描通用Mapper接口类：" + this.mapperClass);
        }
        Method method = methodMap.get(getMethodName(ms));
        try {
            //第一种，直接操作ms，不需要返回值
            if (method.getReturnType() == Void.TYPE) {
                method.invoke(this, ms);
            }
            //第二种，返回SqlNode
            else if (SqlNode.class.isAssignableFrom(method.getReturnType())) {
                SqlNode sqlNode = (SqlNode) method.invoke(this, ms);
                DynamicSqlSource dynamicSqlSource = new DynamicSqlSource(ms.getConfiguration(), sqlNode);
                setSqlSource(ms, dynamicSqlSource);
            }
            //第三种，返回xml形式的sql字符串
            else if (String.class.equals(method.getReturnType())) {
                String xmlSql = (String) method.invoke(this, ms);
                SqlSource sqlSource = createSqlSource(ms, xmlSql);
                //替换原有的SqlSource
                setSqlSource(ms, sqlSource);
            } else {
                throw new MapperException("自定义Mapper方法返回类型错误,可选的返回类型为void,SqlNode,String三种!");
            }
        } catch (IllegalAccessException e) {
            throw new MapperException(e);
        } catch (InvocationTargetException e) {
            throw new MapperException(e.getTargetException() != null ? e.getTargetException() : e);
        }
    }

    /**
     * 通过xmlSql创建sqlSource
     *
     * @param ms
     * @param xmlSql
     * @return
     */
    public SqlSource createSqlSource(MappedStatement ms, String xmlSql) {
        return languageDriver.createSqlSource(ms.getConfiguration(), "<script>\n\t" + xmlSql + "</script>", null);
    }

    /**
     * 获取返回值类型 - 实体类型
     *
     * @param ms
     * @return
     */
    public Class<?> getEntityClass(MappedStatement ms) {
        String msId = ms.getId();
        if (entityClassMap.containsKey(msId)) {
            return entityClassMap.get(msId);
        } else {
            Class<?> mapperClass = getMapperClass(msId);
            Type[] types = mapperClass.getGenericInterfaces();
            for (Type type : types) {
                if (type instanceof ParameterizedType) {
                    ParameterizedType t = (ParameterizedType) type;
                    if (t.getRawType() == this.mapperClass || this.mapperClass.isAssignableFrom((Class<?>) t.getRawType())) {
                        Class<?> returnType = (Class<?>) t.getActualTypeArguments()[0];
                        //获取该类型后，第一次对该类型进行初始化
                        EntityHelper.initEntityNameMap(returnType, mapperResolver.getConfig());
                        entityClassMap.put(msId, returnType);
                        return returnType;
                    }
                }
            }
        }
        throw new MapperException("无法获取 " + msId + " 方法的泛型信息!");
    }

    /**
     * 根据对象生成主键映射
     *
     * @param ms
     * @return
     * @deprecated 4.x版本会移除该方法
     */
    @Deprecated
    protected List<ParameterMapping> getPrimaryKeyParameterMappings(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        Set<EntityColumn> entityColumns = EntityHelper.getPKColumns(entityClass);
        List<ParameterMapping> parameterMappings = new ArrayList<ParameterMapping>();
        for (EntityColumn column : entityColumns) {
            ParameterMapping.Builder builder = new ParameterMapping.Builder(ms.getConfiguration(), column.getProperty(), column.getJavaType());
            builder.mode(ParameterMode.IN);
            parameterMappings.add(builder.build());
        }
        return parameterMappings;
    }

    /**
     * 获取序列下个值的表达式
     *
     * @param column
     * @return
     */
    protected String getSeqNextVal(EntityColumn column) {
        return MessageFormat.format(mapperResolver.getConfig().getSequenceFormat(), column.getSequenceName(), column.getColumn(), column.getProperty(), column.getTable().getName());
    }

    /**
     * 获取实体类的表名
     *
     * @param entityClass
     * @return
     */
    private String getTableName(Class<?> entityClass) {
        EntityTable entityTable = EntityHelper.getEntityTable(entityClass);
        String prefix = entityTable.getPrefix();
        if (StringUtil.isEmpty(prefix)) {
            //使用全局配置
            prefix = mapperResolver.getConfig().getPrefix();
        }
        if (StringUtil.isNotEmpty(prefix)) {
            return prefix + "." + entityTable.getName();
        }
        return entityTable.getName();
    }

    /**
     * 获取实体的表名
     * @param entityClass
     * @return
     */
    protected String tableName(Class<?> entityClass) {
       return tableName(entityClass,false);
    }

    /**
     * 获取实体的表名
     * @param entityClass 实体类型
     * @param addDefultAlias 是否增加默认别名
     * @return
     */
    protected String tableName(Class<?> entityClass,boolean addDefultAlias) {
        StringBuilder sb = new StringBuilder();
        String table = getTableName(entityClass);
        sb.append(table);
        if (addDefultAlias) {
            sb.append(" ").append(entityClass.getSimpleName());
        }
        return sb.toString();
    }


}