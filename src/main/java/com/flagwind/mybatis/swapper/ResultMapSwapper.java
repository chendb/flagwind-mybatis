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

/**
 * @author chendb
 */
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
<<<<<<< HEAD
            resultMaps.put(id, resolveResultMap(null,resource, id, type,style));
=======
            resultMaps.put(id, resolveResultMap(resource, id, type,style));
>>>>>>> b3035d9660c4df4b8e3799eba2eda828bdc7778d
        }
        return resultMaps.get(id);
    }

    public void registerResultMap(ResultMap resultMap) {
        configuration.addResultMap(resultMap);
    }

<<<<<<< HEAD
    public ResultMap resolveResultMap( String columnPrefix,String resource, String id, Class<?> type, Style style) {
        List<ResultMapping> resultMappings = resolveResultMappings(columnPrefix, resource, id, type, style);
        return new ResultMap.Builder(configuration, id, type, resultMappings).build();
    }

    public ResultMapping resolveOneToOneResultMapping(String columnPrefix,EntityField field,MapperBuilderAssistant assistant,String resource, String id, Class<?> type, Style style) {
=======
    public ResultMap resolveResultMap(String resource, String id, Class<?> type, Style style) {
        List<ResultMapping> resultMappings = resolveResultMappings(resource, id, type, style);
        return new ResultMap.Builder(configuration, id, type, resultMappings).build();
    }

    public ResultMapping resolveOneToOneResultMapping(EntityField field,MapperBuilderAssistant assistant,String resource, String id, Class<?> type, Style style) {
>>>>>>> b3035d9660c4df4b8e3799eba2eda828bdc7778d
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
<<<<<<< HEAD
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


=======
        String nestedResultMap =  id + ".association[" + javaType.getSimpleName() + "]";
        registerResultMap(resolveResultMap(resource, nestedResultMap, javaType, style));


        String notNullColumn = null;
        String columnPrefix = null;
>>>>>>> b3035d9660c4df4b8e3799eba2eda828bdc7778d
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
<<<<<<< HEAD
                (StringUtil.isEmpty(columnPrefix) ? "" : columnPrefix) + column,
=======
                column,
>>>>>>> b3035d9660c4df4b8e3799eba2eda828bdc7778d
                javaType,
                jdbcType,
                nestedSelect,
                nestedResultMap,
                notNullColumn,
<<<<<<< HEAD
                null,
=======
                columnPrefix,
>>>>>>> b3035d9660c4df4b8e3799eba2eda828bdc7778d
                typeHandlerClass, flags, resultSet, foreignColumn, lazy);
        return resultMapping;
    }

<<<<<<< HEAD
    public ResultMapping resolveOneToManyResultMapping( String columnPrefix,EntityField field,MapperBuilderAssistant assistant,String resource, String id, Class<?> type, Style style) {
=======
    public ResultMapping resolveOneToManyResultMapping(EntityField field,MapperBuilderAssistant assistant,String resource, String id, Class<?> type, Style style) {
>>>>>>> b3035d9660c4df4b8e3799eba2eda828bdc7778d
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
<<<<<<< HEAD
        String nestedResultMap = id + ".association[" + oneToMany.targetEntity().getSimpleName() + "]";

        String childColumnPrefix = columnPrefix;
        if (StringUtil.isEmpty(childColumnPrefix)) {
            childColumnPrefix = field.getName() + "_";
        } else {
            childColumnPrefix += "_" + field.getName() + "_";
        }
        registerResultMap(resolveResultMap(childColumnPrefix,resource, nestedResultMap, oneToMany.targetEntity(), style));


        String notNullColumn = null;
//        String columnPrefix = field.getName();
//        if(StringUtil.isNotEmpty(id)) {
//            columnPrefix = id +"_"+ columnPrefix;
//        }
=======
        String nestedResultMap =  id + ".association[" + oneToMany.targetEntity().getSimpleName() + "]";
        registerResultMap(resolveResultMap(resource, nestedResultMap, oneToMany.targetEntity(), style));


        String notNullColumn = null;
        String columnPrefix = null;
>>>>>>> b3035d9660c4df4b8e3799eba2eda828bdc7778d
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
<<<<<<< HEAD
                (StringUtil.isEmpty(columnPrefix) ? "" : columnPrefix) + column,
=======
                column,
>>>>>>> b3035d9660c4df4b8e3799eba2eda828bdc7778d
                javaType,
                jdbcType,
                nestedSelect,
                nestedResultMap,
                notNullColumn,
<<<<<<< HEAD
                null,
=======
                columnPrefix,
>>>>>>> b3035d9660c4df4b8e3799eba2eda828bdc7778d
                typeHandlerClass, flags, resultSet, foreignColumn, lazy);
        return resultMapping;
    }

<<<<<<< HEAD
    public List<ResultMapping> resolveResultMappings( String columnPrefix,String resource, String id, Class<?> type, Style style) {
=======
    public List<ResultMapping> resolveResultMappings(String resource, String id, Class<?> type, Style style) {
>>>>>>> b3035d9660c4df4b8e3799eba2eda828bdc7778d
        List<ResultMapping> resultMappings = new ArrayList<>();

        MapperBuilderAssistant assistant = new MapperBuilderAssistant(configuration, resource);

<<<<<<< HEAD
        List<EntityField> fields = FieldHelper.getFields(type);
=======
        List<EntityField> fields =  FieldHelper.getFields(type);
>>>>>>> b3035d9660c4df4b8e3799eba2eda828bdc7778d

        for (EntityField field : fields) {
            // java field name

            // 若为非简单类型，同时没有关联注解，则跳过
<<<<<<< HEAD
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
=======
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
>>>>>>> b3035d9660c4df4b8e3799eba2eda828bdc7778d
                continue;
            }

            String property = ColumnHelper.getPropertyName(field);
            // sql column name

<<<<<<< HEAD
            String column = ColumnHelper.getColumnName(field, style).left;
=======
            String column = ColumnHelper.getColumnName(field,style).left;
>>>>>>> b3035d9660c4df4b8e3799eba2eda828bdc7778d
            Class<?> javaType = field.getJavaType();

            //resultMap is not need jdbcType

<<<<<<< HEAD
            MutableTriple<ColumnType, JdbcType, Class<? extends TypeHandler<?>>> columnTypeTriple = ColumnHelper.getColumnType(field);
=======
            MutableTriple<ColumnType,JdbcType ,Class<? extends TypeHandler<?>>> columnTypeTriple = ColumnHelper.getColumnType(field);
>>>>>>> b3035d9660c4df4b8e3799eba2eda828bdc7778d

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
<<<<<<< HEAD
                registerResultMap(resolveResultMap(columnPrefix, resource, nestedResultMap, javaType, style));
            }

            String notNullColumn = null;
            // String columnPrefix = null;
=======
                registerResultMap(resolveResultMap(resource, nestedResultMap, javaType, style));
            }

            String notNullColumn = null;
            String columnPrefix = null;
>>>>>>> b3035d9660c4df4b8e3799eba2eda828bdc7778d
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

<<<<<<< HEAD
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
=======
            ResultMapping resultMapping = assistant.buildResultMapping(type, property, column,
                    javaType, jdbcType, nestedSelect, nestedResultMap, notNullColumn, columnPrefix,
>>>>>>> b3035d9660c4df4b8e3799eba2eda828bdc7778d
                    typeHandlerClass, flags, resultSet, foreignColumn, lazy);
            resultMappings.add(resultMapping);
        }
        return resultMappings;

    }


}
