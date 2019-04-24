package com.flagwind.mybatis.definition.result.swapper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.flagwind.mybatis.code.Style;
import com.flagwind.mybatis.metadata.EntityTableUtils;
import com.flagwind.mybatis.utils.AssociationUtils;
import com.flagwind.mybatis.utils.StringUtil;
import com.flagwind.mybatis.utils.TypeUtils;
import com.flagwind.persistent.ColumnTypeEntry;
import com.flagwind.reflect.EntityTypeHolder;
import com.flagwind.reflect.SimpleTypeUtils;
import com.flagwind.reflect.entities.EntityField;

import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.mapping.ResultFlag;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

/**
 * @author chendb
 */
public class ResultMapSwapper {

    private Configuration configuration;

    /**
     * Result Maps collection,key : id
     */
    private ConcurrentHashMap<String, ResultMap> resultMaps = new ConcurrentHashMap<>();

    public ResultMapSwapper(Configuration configuration) {
        this.configuration = configuration;
    }

    public ResultMap reloadResultMap(String resource, String id, Class<?> type, Style style) {
        if (!resultMaps.containsKey(id)) {
            resultMaps.put(id, resolveResultMap(null, resource, id, type, style));

        }
        return resultMaps.get(id);
    }

    public void registerResultMap(ResultMap resultMap) {
        configuration.addResultMap(resultMap);
    }


    public ResultMap resolveResultMap(String columnPrefix, String resource, String id, Class<?> type, Style style) {
        List<ResultMapping> resultMappings = resolveResultMappings(columnPrefix, resource, id, type, style);
        return new ResultMap.Builder(configuration, id, type, resultMappings).build();
    }

    public ResultMapping resolveOneToOneResultMapping(String columnPrefix, EntityField field, MapperBuilderAssistant assistant, String resource, String id, Class<?> type, Style style) {

        if (!field.isAnnotationPresent(OneToOne.class)) {
            return null;
        }

        String property = field.getName();
        String column = EntityTableUtils.getColumnName(field, style);
        Class<?> javaType = field.getJavaType();

        if (field.isAnnotationPresent(JoinColumn.class)) {
            JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
            column = joinColumn.name();
        }
        ColumnTypeEntry columnTypeEntry = EntityTableUtils.getColumnTypeEntry(field);

       Class<? extends TypeHandler<?>> typeHandlerClass = columnTypeEntry.getTypeHandler();
       JdbcType jdbcType = columnTypeEntry.getJdbcType();

        // 注册关联结果
        String nestedSelect = null;

        String nestedResultMap = id + ".association[" + javaType.getSimpleName() + "]";

        String childColumnPrefix = columnPrefix;
        if (StringUtil.isEmpty(childColumnPrefix)) {
            childColumnPrefix = field.getName() + "_";
        } else {
            childColumnPrefix += "_" + field.getName() + "_";
        }
        registerResultMap(resolveResultMap(childColumnPrefix, resource, nestedResultMap, javaType, style));


        String notNullColumn = null;
        //String columnPrefix = null;


        String resultSet = null;
        String foreignColumn = null;


        List<ResultFlag> flags = new ArrayList<>();
        if (field.isAnnotationPresent(Id.class)) {
            flags.add(ResultFlag.ID);
        }

        boolean lazy = false;

        if(typeHandlerClass==null) {
            TypeHandler<?> typeHandler = configuration.getTypeHandlerRegistry().getTypeHandler(javaType, jdbcType);
            if (typeHandler != null) {
                typeHandlerClass = TypeUtils.castTo( typeHandler.getClass());
            }
        }

        ResultMapping resultMapping = assistant.buildResultMapping(
                type,
                property,
                (StringUtil.isEmpty(columnPrefix) ? "" : columnPrefix) + column,
                javaType,
                jdbcType,
                nestedSelect,
                nestedResultMap,
                notNullColumn,
                null,
                typeHandlerClass, flags, resultSet, foreignColumn, lazy);
        return resultMapping;
    }


    public ResultMapping resolveOneToManyResultMapping(String columnPrefix, EntityField field, MapperBuilderAssistant assistant, String resource, String id, Class<?> type, Style style) {


        if (!field.isAnnotationPresent(OneToMany.class)) {
            return null;
        }
        OneToMany oneToMany = field.getAnnotation(OneToMany.class);

        String property = field.getName();
        String column = EntityTableUtils.getColumnName(field, style);
        Class<?> javaType = field.getJavaType();

        if (field.isAnnotationPresent(JoinColumn.class)) {
            JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
            column = joinColumn.name();
        }
        ColumnTypeEntry columnTypeEntry = EntityTableUtils.getColumnTypeEntry(field);

        Class<? extends TypeHandler<?>> typeHandlerClass = columnTypeEntry.getTypeHandler();
        JdbcType jdbcType = columnTypeEntry.getJdbcType();

        // 注册关联结果
        String nestedSelect = null;

        String nestedResultMap = id + ".association[" + oneToMany.targetEntity().getSimpleName() + "]";

        String childColumnPrefix = columnPrefix;
        if (StringUtil.isEmpty(childColumnPrefix)) {
            childColumnPrefix = field.getName() + "_";
        } else {
            childColumnPrefix += "_" + field.getName() + "_";
        }
        registerResultMap(resolveResultMap(childColumnPrefix, resource, nestedResultMap, oneToMany.targetEntity(), style));


        String notNullColumn = null;
//        String columnPrefix = field.getName();
//        if(StringUtil.isNotEmpty(id)) {
//            columnPrefix = id +"_"+ columnPrefix;
//        }

        String resultSet = null;
        String foreignColumn = oneToMany.mappedBy();


        List<ResultFlag> flags = new ArrayList<>();
        if (field.isAnnotationPresent(Id.class)) {
            flags.add(ResultFlag.ID);
        }

        boolean lazy = false;

        if(typeHandlerClass==null) {
            TypeHandler<?> typeHandler = configuration.getTypeHandlerRegistry().getTypeHandler(javaType, jdbcType);
            if (typeHandler != null) {
                typeHandlerClass = TypeUtils.castTo( typeHandler.getClass());
            }
        }

        ResultMapping resultMapping = assistant.buildResultMapping(
                type,
                property,

                (StringUtil.isEmpty(columnPrefix) ? "" : columnPrefix) + column,

                javaType,
                jdbcType,
                nestedSelect,
                nestedResultMap,
                notNullColumn,

                null,

                typeHandlerClass, flags, resultSet, foreignColumn, lazy);
        return resultMapping;
    }


    public List<ResultMapping> resolveResultMappings(String columnPrefix, String resource, String id, Class<?> type, Style style) {

        List<ResultMapping> resultMappings = new ArrayList<>();

        MapperBuilderAssistant assistant = new MapperBuilderAssistant(configuration, resource);


        List<EntityField> fields = EntityTypeHolder.getFields(type);


        for (EntityField field : fields) {
            // java field name

            // 若为非简单类型，同时没有关联注解，则跳过

            if ((!SimpleTypeUtils.isSimpleType(field.getJavaType()))
                    && (!AssociationUtils.isAssociationField(field))) {
                continue;
            }

            if (field.isAnnotationPresent(OneToOne.class)) {
                if (StringUtil.isNotEmpty(id) && id.split("association").length > 1) {
                    continue;
                }
                resultMappings.add(resolveOneToOneResultMapping(columnPrefix, field, assistant, resource, id, type, style));
                continue;
            }
            if (field.isAnnotationPresent(OneToMany.class)) {
                if (StringUtil.isNotEmpty(id) && id.split("association").length > 1) {
                    continue;
                }
                resultMappings.add(resolveOneToManyResultMapping(columnPrefix, field, assistant, resource, id, type, style));

                continue;
            }

            String property = field.getName();
            // sql column name


            String column = EntityTableUtils.getColumnName(field, style);

            Class<?> javaType = field.getJavaType();

            //resultMap is not need jdbcType


            ColumnTypeEntry columnTypeEntry = EntityTableUtils.getColumnTypeEntry(field);


            JdbcType jdbcType = columnTypeEntry.getJdbcType();

            String nestedSelect = null;
            String nestedResultMap = null;
            if (AssociationUtils.isAssociationField(field)) {
                if (field.isAnnotationPresent(JoinColumn.class)) {
                    JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
                    column = joinColumn.name();
                }
                // OneToOne
                nestedResultMap = id + ".association[" + javaType.getSimpleName() + "]";

                registerResultMap(resolveResultMap(columnPrefix, resource, nestedResultMap, javaType, style));
            }
            String notNullColumn = null;
            // String columnPrefix = null;

            String resultSet = null;
            String foreignColumn = null;

            List<ResultFlag> flags = new ArrayList<>();
            if (field.isAnnotationPresent(Id.class)) {
                flags.add(ResultFlag.ID);
            }
            // lazy or eager

            boolean lazy = false;
            // enum

            Class<? extends TypeHandler<?>> typeHandlerClass = columnTypeEntry.getTypeHandler();

            if(typeHandlerClass==null) {
                TypeHandler<?> typeHandler = configuration.getTypeHandlerRegistry().getTypeHandler(javaType, jdbcType);
                if (typeHandler != null) {
                    typeHandlerClass = TypeUtils.castTo( typeHandler.getClass());
                }
            }


            ResultMapping resultMapping = assistant.buildResultMapping(
                    type,
                    property,
                    (StringUtil.isEmpty(columnPrefix) ? "" : columnPrefix) + column,
                    javaType,
                    jdbcType,
                    nestedSelect,
                    nestedResultMap,
                    notNullColumn,
                    null,
                    typeHandlerClass, flags, resultSet, foreignColumn, lazy);
            resultMappings.add(resultMapping);
        }
        return resultMappings;

    }


}
