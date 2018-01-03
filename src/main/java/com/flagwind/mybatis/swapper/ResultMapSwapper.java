package com.flagwind.mybatis.swapper;

import com.flagwind.mybatis.code.Style;
import com.flagwind.mybatis.helpers.ColumnHelper;
import com.flagwind.mybatis.helpers.FieldHelper;
import com.flagwind.mybatis.meta.EntityField;
import com.flagwind.mybatis.utils.AssociationUtils;
import com.flagwind.mybatis.utils.SimpleTypeUtils;
import com.flagwind.mybatis.utils.StringUtil;
import com.flagwind.persistent.annotation.ColumnType;
import org.apache.commons.lang3.tuple.MutableTriple;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.mapping.ResultFlag;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ResultMapSwapper {

    private Configuration configuration;

    /**

     * Result Maps collection,key : id

     */
    private ConcurrentHashMap<String, ResultMap> resultMaps = new ConcurrentHashMap<>();

    public ResultMapSwapper(Configuration configuration){
        this.configuration = configuration;
    }

    public ResultMap reloadResultMap(String resource, String id, Class<?> type, Style style) {
        if (!resultMaps.containsKey(id)) {
            resultMaps.put(id, resolveResultMap(resource, id, type,style));
        }
        return resultMaps.get(id);
    }

    public void registerResultMap(ResultMap resultMap) {
        configuration.addResultMap(resultMap);
    }

    public ResultMap resolveResultMap(String resource, String id, Class<?> type, Style style) {
        List<ResultMapping> resultMappings = resolveResultMappings(resource, id, type, style);
        return new ResultMap.Builder(configuration, id, type, resultMappings).build();
    }

    public ResultMapping resolveOneToOneResultMapping(EntityField field,MapperBuilderAssistant assistant,String resource, String id, Class<?> type, Style style) {
        if (!field.isAnnotationPresent(OneToOne.class)) {
            return null;
        }

        String property = ColumnHelper.getPropertyName(field);
        String column = ColumnHelper.getColumnName(field, style).left;
        Class<?> javaType = field.getJavaType();

        if (field.isAnnotationPresent(JoinColumn.class)) {
            JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
            column = joinColumn.name();
        }
        MutableTriple<ColumnType, JdbcType, Class<? extends TypeHandler<?>>> columnTypeTriple = ColumnHelper.getColumnType(field);

        Class<? extends TypeHandler<?>> typeHandlerClass = columnTypeTriple.right;
        JdbcType jdbcType = columnTypeTriple.middle;

        // 注册关联结果
        String nestedSelect = null;
        String nestedResultMap =  id + ".association[" + javaType.getSimpleName() + "]";
        registerResultMap(resolveResultMap(resource, nestedResultMap, javaType, style));


        String notNullColumn = null;
        String columnPrefix = null;
        String resultSet = null;
        String foreignColumn = null;


        List<ResultFlag> flags = new ArrayList<>();
        if (field.isAnnotationPresent(Id.class)) {
            flags.add(ResultFlag.ID);
        }

        boolean lazy = false;

        ResultMapping resultMapping = assistant.buildResultMapping(
                type,
                property,
                column,
                javaType,
                jdbcType,
                nestedSelect,
                nestedResultMap,
                notNullColumn,
                columnPrefix,
                typeHandlerClass, flags, resultSet, foreignColumn, lazy);
        return resultMapping;
    }

    public ResultMapping resolveOneToManyResultMapping(EntityField field,MapperBuilderAssistant assistant,String resource, String id, Class<?> type, Style style) {
        if (!field.isAnnotationPresent(OneToMany.class)) {
            return null;
        }
        OneToMany oneToMany = field.getAnnotation(OneToMany.class);

        String property = ColumnHelper.getPropertyName(field);
        String column = ColumnHelper.getColumnName(field, style).left;
        Class<?> javaType = field.getJavaType();

        if (field.isAnnotationPresent(JoinColumn.class)) {
            JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
            column = joinColumn.name();
        }
        MutableTriple<ColumnType, JdbcType, Class<? extends TypeHandler<?>>> columnTypeTriple = ColumnHelper.getColumnType(field);

        Class<? extends TypeHandler<?>> typeHandlerClass = columnTypeTriple.right;
        JdbcType jdbcType = columnTypeTriple.middle;

        // 注册关联结果
        String nestedSelect = null;
        String nestedResultMap =  id + ".association[" + oneToMany.targetEntity().getSimpleName() + "]";
        registerResultMap(resolveResultMap(resource, nestedResultMap, oneToMany.targetEntity(), style));


        String notNullColumn = null;
        String columnPrefix = null;
        String resultSet = null;
        String foreignColumn = oneToMany.mappedBy();


        List<ResultFlag> flags = new ArrayList<>();
        if (field.isAnnotationPresent(Id.class)) {
            flags.add(ResultFlag.ID);
        }

        boolean lazy = false;

        ResultMapping resultMapping = assistant.buildResultMapping(
                type,
                property,
                column,
                javaType,
                jdbcType,
                nestedSelect,
                nestedResultMap,
                notNullColumn,
                columnPrefix,
                typeHandlerClass, flags, resultSet, foreignColumn, lazy);
        return resultMapping;
    }

    public List<ResultMapping> resolveResultMappings(String resource, String id, Class<?> type, Style style) {
        List<ResultMapping> resultMappings = new ArrayList<>();

        MapperBuilderAssistant assistant = new MapperBuilderAssistant(configuration, resource);

        List<EntityField> fields =  FieldHelper.getFields(type);

        for (EntityField field : fields) {
            // java field name

            // 若为非简单类型，同时没有关联注解，则跳过
            if((!SimpleTypeUtils.isSimpleType(field.getJavaType()))
                    &&(!AssociationUtils.isAssociationField(field))) {
                continue;
            }

            if(field.isAnnotationPresent(OneToOne.class)) {
                if (StringUtil.isNotEmpty(id) && id.split("association").length > 2) {
                    continue;
                }
                resultMappings.add(resolveOneToOneResultMapping(field, assistant, resource, id, type, style));
                continue;
            }
            if(field.isAnnotationPresent(OneToMany.class)) {
                if (StringUtil.isNotEmpty(id) && id.split("association").length > 2) {
                    continue;
                }
                resultMappings.add(resolveOneToManyResultMapping(field, assistant, resource, id, type, style));
                continue;
            }

            String property = ColumnHelper.getPropertyName(field);
            // sql column name

            String column = ColumnHelper.getColumnName(field,style).left;
            Class<?> javaType = field.getJavaType();

            //resultMap is not need jdbcType

            MutableTriple<ColumnType,JdbcType ,Class<? extends TypeHandler<?>>> columnTypeTriple = ColumnHelper.getColumnType(field);

            JdbcType jdbcType = columnTypeTriple.middle;

            String nestedSelect = null;
            String nestedResultMap = null;
            if (AssociationUtils.isAssociationField(field)) {
                if (field.isAnnotationPresent(JoinColumn.class)) {
                    JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
                    column = joinColumn.name();
                }
                // OneToOne
                nestedResultMap = id + ".association[" + javaType.getSimpleName() + "]";
                registerResultMap(resolveResultMap(resource, nestedResultMap, javaType, style));
            }

            String notNullColumn = null;
            String columnPrefix = null;
            String resultSet = null;
            String foreignColumn = null;

            List<ResultFlag> flags = new ArrayList<>();
            if (field.isAnnotationPresent(Id.class)) {
                flags.add(ResultFlag.ID);
            }
            // lazy or eager

            boolean lazy = false;
            // enum

            Class<? extends TypeHandler<?>> typeHandlerClass = columnTypeTriple.right;

            ResultMapping resultMapping = assistant.buildResultMapping(type, property, column,
                    javaType, jdbcType, nestedSelect, nestedResultMap, notNullColumn, columnPrefix,
                    typeHandlerClass, flags, resultSet, foreignColumn, lazy);
            resultMappings.add(resultMapping);
        }
        return resultMappings;

    }


}
