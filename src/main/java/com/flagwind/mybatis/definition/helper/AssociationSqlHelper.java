package com.flagwind.mybatis.definition.helper;

import com.flagwind.commons.StringUtils;
import com.flagwind.mybatis.common.Config;
import com.flagwind.mybatis.exceptions.MapperException;
import com.flagwind.mybatis.metadata.EntityColumn;
import com.flagwind.mybatis.metadata.EntityTable;
import com.flagwind.mybatis.metadata.EntityTableFactory;
import com.flagwind.mybatis.utils.AssociationUtils;
import com.flagwind.reflect.entities.EntityField;

import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.List;
import java.util.Set;

public class AssociationSqlHelper {

    public static void  registerEntityClass(Class<?> entityClass,Config config) {
        EntityTable table = EntityTableFactory.getEntityTable(entityClass);
        if (table.isAssociationRegisted()) {
            return;
        }
        List<EntityField> fields = table.getAssociationFields();
        for (EntityField field : fields) {
            if (field.isAnnotationPresent(OneToOne.class)) {
                OneToOne oneToOne = field.getAnnotation(OneToOne.class);
                EntityTableFactory.register(oneToOne.targetEntity(), config);
            }
            if (field.isAnnotationPresent(OneToMany.class)) {
                OneToMany oneToMany = field.getAnnotation(OneToMany.class);
                EntityTableFactory.register(oneToMany.targetEntity(), config);
            }
        }
        table.setAssociationRegisted(true);
    }

    /**
     * where主键条件(参数为单个值如userReository.getById("123456"))
     * @param entityClass
     * @param keyName
     */
    public static String wherePKColumn(String columnPrefix,Class<?> entityClass,String keyName) {
        StringBuilder sql = new StringBuilder();
        //获取全部列
        Set<EntityColumn> columnList = EntityTableFactory.getPKColumns(entityClass);
        if (columnList.size() == 1) {
            EntityColumn column = columnList.iterator().next();
            sql.append(" where ");
            if (StringUtils.isNotEmpty(columnPrefix) && !column.getColumn().contains(".")) {
                sql.append(columnPrefix).append(".");
            }
            sql.append(column.getColumn());
            sql.append(" = #{" + keyName + "}");
        } else {
            throw new MapperException("实体类[" + entityClass.getCanonicalName() + "]中必须只有一个带有 @Id 注解的字段");
        }
        return sql.toString();
    }


    /**
     * 检查这个实体对应的类型是否有关联关系
     * @param entityClass
     */
    public static boolean hasAssociation(Class<?> entityClass) {
        EntityTable table = EntityTableFactory.getEntityTable(entityClass);
        if (table == null) {
            return false;
        }
        return table.getAssociationFields().size() > 0;
    }

    /**
     * from tableName - 动态表名
     *
     * @param entityClass
     * @param config
     */
    public static String fromTable(Class<?> entityClass, Config config) {
        StringBuilder sql = new StringBuilder();
        sql.append(" FROM ");
        sql.append(tableName(entityClass, config));
        sql.append(" ");
        return sql.toString();
    }

    private static String getTableName(Class<?> entityClass, Config config) {
        EntityTable entityTable = EntityTableFactory.getEntityTable(entityClass);
        String prefix = entityTable.getPrefix();
        if (StringUtils.isEmpty(prefix)) {
            //使用全局配置
            prefix = config.getPrefix();
        }
        if (StringUtils.isNotEmpty(prefix)) {
            return prefix + "." + entityTable.getName();
        }
        return entityTable.getName();
    }

    /**
     * 获取实体的表名以及关联表名
     * @param entityClass
     */
    public static String tableName(Class<?> entityClass,Config config) {
        EntityTable entityTable = EntityTableFactory.getEntityTable(entityClass);
        StringBuilder sb = new StringBuilder();
        String table = getTableName(entityClass, config);
        sb.append(table).append(" ").append(entityClass.getSimpleName());
        List<EntityField> fields = entityTable.getAssociationFields();
        for (EntityField field : fields) {
            if (AssociationUtils.isAssociationField(field)) {
                sb.append(" ").append(getAssociationTable(entityClass.getSimpleName(), field, config));
            }
        }
        return sb.toString();
    }

    public static String getAssociationTable(String masterTableName,EntityField field,Config config){
        StringBuilder sb = new StringBuilder();
        if (field.isAnnotationPresent(OneToOne.class)) {
            OneToOne oneToOne = field.getAnnotation(OneToOne.class);
            JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
            sb.append("left join ").append(getTableName(field.getJavaType(), config)).append("  ").append(field.getName()).append(" on ").append(masterTableName).append(".").append(joinColumn.name()).append("=").append(field.getName()).append(".").append(oneToOne.mappedBy());
        }
        if (field.isAnnotationPresent(OneToMany.class)) {
            OneToMany oneToMany = field.getAnnotation(OneToMany.class);
            JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
            sb.append("left join ").append(getTableName(oneToMany.targetEntity(), config)).append("  ").append(field.getName()).append(" on ").append(masterTableName + "." + joinColumn.name()).append("=").append(field.getName()).append(".").append(oneToMany.mappedBy());
        }
        return sb.toString();
    }

    public static Class<?> getAssociationEntityClass(EntityField field) {
        if (field.isAnnotationPresent(OneToOne.class)) {
            return field.getJavaType();
        }
        if (field.isAnnotationPresent(OneToMany.class)) {
            OneToMany oneToMany = field.getAnnotation(OneToMany.class);
            return oneToMany.targetEntity();
        }
        return null;
    }

    public static String selectAllColumns(Class<?> entityClass) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT ").append(getAllColumns(entityClass)).append(" ");
        return sql.toString();
    }

    public static String getAllColumns(Class<?> entityClass) {
        StringBuilder sql = new StringBuilder();
        sql.append(getSelectColumnsFromMasterTable(entityClass, entityClass.getSimpleName(), null));

        List<EntityField> fields = EntityTableFactory.getEntityTable(entityClass).getAssociationFields();
        for (EntityField field : fields) {
            if (field.isAnnotationPresent(OneToOne.class)) {
                String columnPrefix = field.getName();
                String aliasPrefix = field.getName();
                sql.append(",").append(getSelectColumnsFromAssociationTable(field, columnPrefix, aliasPrefix));
            }
            if (field.isAnnotationPresent(OneToMany.class)) {
                String columnPrefix = field.getName();
                String aliasPrefix = field.getName();
                sql.append(",").append(getSelectColumnsFromAssociationTable(field, columnPrefix, aliasPrefix));
            }
        }
        return sql.toString();
    }

    public static String getSelectColumnsFromAssociationTable(EntityField field,String columnPrefix,String aliasPrefix) {
        Class<?> entityClass = getAssociationEntityClass(field);
        //EntityTable entityTable = EntityTableFactory.getEntityTable(entityClass);
//        if (entityTable.getBaseSelect() != null) {
//            return entityTable.getBaseSelect();
//        }
        if(StringUtils.isNotEmpty(aliasPrefix)) {
            aliasPrefix += "_";
        }
        Set<EntityColumn> columnList = EntityTableFactory.getColumns(entityClass);
        StringBuilder selectBuilder = new StringBuilder();
        for (EntityColumn entityColumn : columnList) {
            if (StringUtils.isNotEmpty(columnPrefix) && !entityColumn.getColumn().contains(".")) {
                selectBuilder.append(columnPrefix).append(".");
            }
            selectBuilder.append(entityColumn.getColumn());
            selectBuilder.append(" as ");
            if(StringUtils.isNotEmpty(aliasPrefix)){
                selectBuilder.append(aliasPrefix);
            }
            selectBuilder.append(entityColumn.getProperty()).append(",");
        }
        return  selectBuilder.substring(0, selectBuilder.length() - 1);
        //entityTable.setBaseSelect(selectBuilder.substring(0, selectBuilder.length() - 1));
        //return entityTable.getBaseSelect();
    }

    public static String getSelectColumnsFromMasterTable(Class<?> entityClass,String columnPrefix,String aliasPrefix) {
        EntityTable entityTable = EntityTableFactory.getEntityTable(entityClass);
        if (entityTable.getBaseSelect() != null) {
            return entityTable.getBaseSelect();
        }
        Set<EntityColumn> columnList = EntityTableFactory.getColumns(entityClass);
        StringBuilder selectBuilder = new StringBuilder();

        if(StringUtils.isNotEmpty(aliasPrefix)) {
            aliasPrefix += "_";
        }

        for (EntityColumn entityColumn : columnList) {
            if (StringUtils.isNotEmpty(columnPrefix) && !entityColumn.getColumn().contains(".")) {
                selectBuilder.append(columnPrefix).append(".");
            }
            selectBuilder.append(entityColumn.getColumn()).append(" as ");
            if(StringUtils.isNotEmpty(aliasPrefix)){
                selectBuilder.append(aliasPrefix);
            }
            selectBuilder.append(entityColumn.getProperty()).append(",");
        }
        entityTable.setBaseSelect(selectBuilder.substring(0, selectBuilder.length() - 1));
        return entityTable.getBaseSelect();
    }

}
