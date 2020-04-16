package com.flagwind.mybatis.definition;


import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeException;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

/**
 * @author chendb
 * @description: Mybatis参数处理
 * @date 2020-04-16 13:59:59
 */

public class MybatisDefaultParameterHandler extends DefaultParameterHandler {

    private final TypeHandlerRegistry typeHandlerRegistry;
    private final MappedStatement mappedStatement;
    private final Object parameterObject;
    private final BoundSql boundSql;
    private final Configuration configuration;

    public MybatisDefaultParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql) {
        super(mappedStatement, processParameter(mappedStatement, parameterObject), boundSql);
        this.mappedStatement = mappedStatement;
        this.configuration = mappedStatement.getConfiguration();
        this.typeHandlerRegistry = mappedStatement.getConfiguration().getTypeHandlerRegistry();
        this.parameterObject = parameterObject;
        this.boundSql = boundSql;
    }

    protected static Object processParameter(MappedStatement ms, Object parameterObject) {
        /* 只处理插入或更新操作 */
        if (parameterObject != null
                && (SqlCommandType.INSERT == ms.getSqlCommandType() || SqlCommandType.UPDATE == ms.getSqlCommandType())) {
            //检查 parameterObject
            return parameterObject;
        }
        return parameterObject;
    }


//
//    /**
//     * 处理参数
//     *
//     * @param ms              MappedStatement
//     * @param parameterObject 插入数据库对象
//     * @return ignore
//     */
//    protected static Object processParameter(MappedStatement ms, Object parameterObject) {
//        /* 只处理插入或更新操作 */
//        if (parameterObject != null
//                && (SqlCommandType.INSERT == ms.getSqlCommandType() || SqlCommandType.UPDATE == ms.getSqlCommandType())) {
//            //检查 parameterObject
//            if (ReflectionKit.isPrimitiveOrWrapper(parameterObject.getClass())
//                    || parameterObject.getClass() == String.class) {
//                return parameterObject;
//            }
//            Collection<Object> parameters = getParameters(parameterObject);
//            if (null != parameters) {
//                // 感觉这里可以稍微优化一下，理论上都是同一个.
//                parameters.forEach(obj -> process(ms, obj));
//            } else {
//                process(ms, parameterObject);
//            }
//        }
//        return parameterObject;
//    }
//
//    private static void process(MappedStatement ms, Object parameterObject) {
//        if (parameterObject != null) {
//            TableInfo tableInfo = null;
//            Object entity = parameterObject;
//            if (parameterObject instanceof Map) {
//                Map<?, ?> map = (Map<?, ?>) parameterObject;
//                if (map.containsKey(Constants.ENTITY)) {
//                    Object et = map.get(Constants.ENTITY);
//                    if (et != null) {
//                        entity = et;
//                        tableInfo = TableInfoHelper.getTableInfo(entity.getClass());
//                    }
//                }
//            } else {
//                tableInfo = TableInfoHelper.getTableInfo(parameterObject.getClass());
//            }
//            if (tableInfo != null) {
//                //到这里就应该转换到实体参数对象了,因为填充和ID处理都是争对实体对象处理的,不用传递原参数对象下去.
//                MetaObject metaObject = ms.getConfiguration().newMetaObject(entity);
//                if (SqlCommandType.INSERT == ms.getSqlCommandType()) {
//                    populateKeys(tableInfo, metaObject, entity);
//                    insertFill(metaObject, tableInfo);
//                } else {
//                    updateFill(metaObject, tableInfo);
//                }
//            }
//        }
//    }
//
//
//    /**
//     * 处理正常批量插入逻辑
//     * <p>
//     * org.apache.ibatis.session.defaults.DefaultSqlSession$StrictMap 该类方法
//     * wrapCollection 实现 StrictMap 封装逻辑
//     * </p>
//     *
//     * @param parameter 插入数据库对象
//     * @return
//     */
//    @SuppressWarnings({"rawtypes", "unchecked"})
//    protected static Collection<Object> getParameters(Object parameter) {
//        Collection<Object> parameters = null;
//        if (parameter instanceof Collection) {
//            parameters = (Collection) parameter;
//        } else if (parameter instanceof Map) {
//            Map parameterMap = (Map) parameter;
//            if (parameterMap.containsKey("collection")) {
//                parameters = (Collection) parameterMap.get("collection");
//            } else if (parameterMap.containsKey("list")) {
//                parameters = (List) parameterMap.get("list");
//            } else if (parameterMap.containsKey("array")) {
//                parameters = Arrays.asList((Object[]) parameterMap.get("array"));
//            }
//        }
//        return parameters;
//    }
//
//    /**
//     * 填充主键
//     *
//     * @param tableInfo  数据库表反射信息
//     * @param metaObject 元数据对象
//     * @param entity     实体信息
//     */
//    protected static void populateKeys(TableInfo tableInfo, MetaObject metaObject, Object entity) {
//        final IdType idType = tableInfo.getIdType();
//        final String keyProperty = tableInfo.getKeyProperty();
//        if (StringUtils.isNotBlank(keyProperty) && null != idType && idType.getKey() >= 3) {
//            final IdentifierGenerator identifierGenerator = GlobalConfigUtils.getGlobalConfig(tableInfo.getConfiguration()).getIdentifierGenerator();
//            Object idValue = metaObject.getValue(keyProperty);
//            if (StringUtils.checkValNull(idValue)) {
//                if (idType.getKey() == IdType.ASSIGN_ID.getKey()) {
//                    if (Number.class.isAssignableFrom(tableInfo.getKeyType())) {
//                        metaObject.setValue(keyProperty, identifierGenerator.nextId(entity));
//                    } else {
//                        metaObject.setValue(keyProperty, identifierGenerator.nextId(entity).toString());
//                    }
//                } else if (idType.getKey() == IdType.ASSIGN_UUID.getKey()) {
//                    metaObject.setValue(keyProperty, identifierGenerator.nextUUID(entity));
//                }
//            }
//        }
//    }
//
//    protected static void insertFill(MetaObject metaObject, TableInfo tableInfo) {
//        GlobalConfigUtils.getMetaObjectHandler(tableInfo.getConfiguration()).ifPresent(metaObjectHandler -> {
//            if (metaObjectHandler.openInsertFill()) {
//                if (tableInfo.isWithInsertFill()) {
//                    metaObjectHandler.insertFill(metaObject);
//                } else {
//                    // 兼容旧操作 id类型为input或none的要用填充器处理一下
//                    if (metaObjectHandler.compatibleFillId()) {
//                        String keyProperty = tableInfo.getKeyProperty();
//                        if (StringUtils.isNotBlank(keyProperty)) {
//                            Object value = metaObject.getValue(keyProperty);
//                            if (value == null && (IdType.NONE == tableInfo.getIdType() || IdType.INPUT == tableInfo.getIdType())) {
//                                metaObjectHandler.insertFill(metaObject);
//                            }
//                        }
//                    }
//                }
//            }
//        });
//    }
//
//    protected static void updateFill(MetaObject metaObject, TableInfo tableInfo) {
//        GlobalConfigUtils.getMetaObjectHandler(tableInfo.getConfiguration()).ifPresent(metaObjectHandler -> {
//            if (metaObjectHandler.openUpdateFill() && tableInfo.isWithUpdateFill()) {
//                metaObjectHandler.updateFill(metaObject);
//            }
//        });
//    }

    @Override
    public void setParameters(PreparedStatement ps) {
        ErrorContext.instance().activity("setting parameters").object(mappedStatement.getParameterMap().getId());
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        if (parameterMappings != null) {
            for (int i = 0; i < parameterMappings.size(); i++) {
                ParameterMapping parameterMapping = parameterMappings.get(i);
                if (parameterMapping.getMode() != ParameterMode.OUT) {
                    Object value;
                    String propertyName = parameterMapping.getProperty();
                    if (boundSql.hasAdditionalParameter(propertyName)) { // issue #448 ask first for additional params
                        value = boundSql.getAdditionalParameter(propertyName);
                    } else if (parameterObject == null) {
                        value = null;
                    } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                        value = parameterObject;
                    } else {
                        MetaObject metaObject = configuration.newMetaObject(parameterObject);
                        value = metaObject.getValue(propertyName);
                    }
                    TypeHandler typeHandler = parameterMapping.getTypeHandler();
                    JdbcType jdbcType = parameterMapping.getJdbcType();
                    if (value == null && jdbcType == null) {
                        jdbcType = configuration.getJdbcTypeForNull();
                    }
                    try {
                        typeHandler.setParameter(ps, i + 1, value, jdbcType);
                    } catch (TypeException | SQLException e) {
                        throw new TypeException("Could not set parameters for mapping: " + parameterMapping + ". Cause: " + e, e);
                    }
                }
            }
        }
    }
}
