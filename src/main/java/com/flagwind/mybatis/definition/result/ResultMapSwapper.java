package com.flagwind.mybatis.definition.result;

import com.flagwind.commons.StringUtils;
import com.flagwind.lang.CodeType;
import com.flagwind.mybatis.code.Style;
import com.flagwind.mybatis.handlers.CodeTypeHandler;
import com.flagwind.mybatis.metadata.EntityTableUtils;
import com.flagwind.mybatis.utils.AssociationUtils;
import com.flagwind.persistent.ColumnTypeEntry;
import com.flagwind.reflect.EntityTypeHolder;
import com.flagwind.reflect.SimpleTypeUtils;
import com.flagwind.reflect.entities.EntityField;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.mapping.ResultFlag;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.reflection.MetaClass;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandler;

import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author chendb
 */
public class ResultMapSwapper {

    private final Configuration configuration;

    /**
     * Result Maps collection,key : id
     */
    private final ConcurrentHashMap<String, ResultMap> resultMaps = new ConcurrentHashMap<>();

    public ResultMapSwapper(Configuration configuration) {
        this.configuration = configuration;
    }

    public ResultMap reloadResultMap(String resource, String id, Class<?> type, Style style) {
        synchronized (resultMaps) {
            if (!resultMaps.containsKey(id)) {
                resultMaps.put(id, resolveResultMap(null, resource, id, type, style));

            }
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
        ResultMapping.Builder builder = new ResultMapping.Builder(configuration, field.getName());


        builder.column(getColumn(field, style, columnPrefix));


        ColumnTypeEntry columnTypeEntry = EntityTableUtils.getColumnTypeEntry(field);


        builder.jdbcType(columnTypeEntry.getJdbcType());


        // 注册关联结果


        String nestedResultMap = id + ".association[" + field.getJavaType().getSimpleName() + "]";
        String childColumnPrefix = columnPrefix;
        if (StringUtils.isEmpty(childColumnPrefix)) {
            childColumnPrefix = field.getName() + "_";
        } else {
            childColumnPrefix += "_" + field.getName() + "_";
        }
        registerResultMap(resolveResultMap(childColumnPrefix, resource, nestedResultMap, field.getJavaType(), style));

        builder.nestedResultMapId(assistant.applyCurrentNamespace(nestedResultMap, true));


        builder.flags(getResultFlags(field));


        builder.typeHandler(getTypeHandler(field, field.getJavaType(), columnTypeEntry));

        builder.javaType(this.resolveResultJavaType(type, field.getName(), field.getJavaType()));

        return builder.build();
    }

    private String getColumn(EntityField field, Style style, String columnPrefix) {
        String column = EntityTableUtils.getColumnName(field, style);

        if (field.isAnnotationPresent(JoinColumn.class)) {
            JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
            column = joinColumn.name();
        }

        return (StringUtils.isEmpty(columnPrefix) ? "" : columnPrefix) + column;
    }


    public ResultMapping resolveOneToManyResultMapping(String columnPrefix, EntityField field, MapperBuilderAssistant assistant, String resource, String id, Class<?> type, Style style) {


        if (!field.isAnnotationPresent(OneToMany.class)) {
            return null;
        }
        OneToMany oneToMany = field.getAnnotation(OneToMany.class);


        ResultMapping.Builder builder = new ResultMapping.Builder(configuration, field.getName());

        builder.column(getColumn(field, style, columnPrefix));

        ColumnTypeEntry columnTypeEntry = EntityTableUtils.getColumnTypeEntry(field);

        builder.jdbcType(columnTypeEntry.getJdbcType());
        builder.foreignColumn(oneToMany.mappedBy());


        String nestedResultMap = id + ".association[" + oneToMany.targetEntity().getSimpleName() + "]";
        String childColumnPrefix = columnPrefix;
        if (StringUtils.isEmpty(childColumnPrefix)) {
            childColumnPrefix = field.getName() + "_";
        } else {
            childColumnPrefix += "_" + field.getName() + "_";
        }
        registerResultMap(resolveResultMap(childColumnPrefix, resource, nestedResultMap, oneToMany.targetEntity(), style));
        builder.nestedResultMapId(assistant.applyCurrentNamespace(nestedResultMap, true));


        builder.flags(getResultFlags(field));


        builder.typeHandler(getTypeHandler(field, field.getJavaType(), columnTypeEntry));


        builder.javaType(this.resolveResultJavaType(type, field.getName(), field.getJavaType()));


        return builder.build();
    }

    private List<ResultFlag> getResultFlags(EntityField field) {
        List<ResultFlag> flags = new ArrayList<>();
        if (field.isAnnotationPresent(Id.class)) {
            flags.add(ResultFlag.ID);
        }
        return flags;
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
                if (StringUtils.isNotEmpty(id) && id.split("association").length > 1) {
                    continue;
                }
                resultMappings.add(resolveOneToOneResultMapping(columnPrefix, field, assistant, resource, id, type, style));
                continue;
            }
            if (field.isAnnotationPresent(OneToMany.class)) {
                if (StringUtils.isNotEmpty(id) && id.split("association").length > 1) {
                    continue;
                }
                resultMappings.add(resolveOneToManyResultMapping(columnPrefix, field, assistant, resource, id, type, style));

                continue;
            }


            ResultMapping.Builder builder = new ResultMapping.Builder(configuration, field.getName());


            String col = getColumn(field, style, columnPrefix);
            builder.column(col);


            ColumnTypeEntry columnTypeEntry = EntityTableUtils.getColumnTypeEntry(field);


            builder.jdbcType(columnTypeEntry.getJdbcType());


            String nestedResultMap = null;
            if (AssociationUtils.isAssociationField(field)) {

                // OneToOne
                nestedResultMap = id + ".association[" + field.getJavaType().getSimpleName() + "]";

                registerResultMap(resolveResultMap(columnPrefix, resource, nestedResultMap, field.getJavaType(), style));
            }

            builder.nestedResultMapId(assistant.applyCurrentNamespace(nestedResultMap, true));


            builder.flags(getResultFlags(field));

            TypeHandler<?> typeHandler = getTypeHandler(field, field.getJavaType(), columnTypeEntry);

            builder.typeHandler(typeHandler);


            Class<?> javaTypeClass = this.resolveResultJavaType(type, field.getName(), field.getJavaType());

            builder.javaType(javaTypeClass);

            List<ResultMapping> composites = this.parseCompositeColumnName(col);
            builder.composites(composites);


            resultMappings.add(builder.build());
        }
        return resultMappings;
    }

    private TypeHandler<?> getTypeHandler(EntityField field, Class<?> javaType, ColumnTypeEntry columnTypeEntry) {
        TypeHandler<?> typeHandler;

        if (columnTypeEntry.getTypeHandler() == null) {

            if (CodeType.class.isAssignableFrom(javaType)) {
                typeHandler = new CodeTypeHandler(field.getJavaType());
            } else {
                typeHandler = configuration.getTypeHandlerRegistry().getTypeHandler(javaType, columnTypeEntry.getJdbcType());
            }

//            if (typeHandler == null && CodeType.class.isAssignableFrom(javaType)) {
//                typeHandler = new CodeTypeHandler(field.getJavaType());
//            }

        } else {
            typeHandler = this.resolveTypeHandler(javaType, columnTypeEntry.getTypeHandler());
        }
        return typeHandler;
    }

    // region result mapping assistant method

    protected TypeHandler<?> resolveTypeHandler(Class<?> javaType, Class<? extends TypeHandler<?>> typeHandlerType) {
        if (typeHandlerType == null) {
            return null;
        } else {
            TypeHandler<?> handler = this.configuration.getTypeHandlerRegistry().getMappingTypeHandler(typeHandlerType);
            if (handler == null) {
                handler = this.configuration.getTypeHandlerRegistry().getInstance(javaType, typeHandlerType);
            }

            return handler;
        }
    }

    private List<ResultMapping> parseCompositeColumnName(String columnName) {
        List<ResultMapping> composites = new ArrayList<>();
        if (columnName != null && (columnName.indexOf(61) > -1 || columnName.indexOf(44) > -1)) {
            StringTokenizer parser = new StringTokenizer(columnName, "{}=, ", false);

            while (parser.hasMoreTokens()) {
                String property = parser.nextToken();
                String column = parser.nextToken();
                ResultMapping complexResultMapping = (new org.apache.ibatis.mapping.ResultMapping.Builder(this.configuration, property, column, this.configuration.getTypeHandlerRegistry().getUnknownTypeHandler())).build();
                composites.add(complexResultMapping);
            }
        }

        return composites;
    }

    private Class<?> resolveResultJavaType(Class<?> resultType, String property, Class<?> javaType) {
        if (javaType == null && property != null) {
            try {
                MetaClass metaResultType = MetaClass.forClass(resultType, this.configuration.getReflectorFactory());
                javaType = metaResultType.getSetterType(property);
            } catch (Exception ignored) {
            }
        }

        if (javaType == null) {
            javaType = Object.class;
        }

        return javaType;
    }


    // endregion

}
