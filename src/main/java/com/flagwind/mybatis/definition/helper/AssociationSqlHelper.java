package com.flagwind.mybatis.definition.helper;

import com.flagwind.commons.StringUtils;
import com.flagwind.mybatis.definition.Config;
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

    public static void registerEntityClass(Class<?> entityClass, Config config) {
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
     * where主键条件(参数为单个值如userRepository.getById("123456"))
     *
     * @param entityClass
     * @param keyName
     */
    public static String wherePKColumn(Class<?> entityClass, String keyName) {

        String columnPrefix = TemplateSqlHelper.tableAlias(entityClass);
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
            sql.append(" = #{").append(keyName).append("}");
        } else {
            throw new MapperException("实体类[" + entityClass.getCanonicalName() + "]中必须只有一个带有 @Id 注解的字段");
        }
        return sql.toString();
    }


    /**
     * 检查这个实体对应的类型是否有关联关系
     *
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
     * 获取实体的表名以及关联表名
     *
     * @param entityClass
     */
    public static String tableName(Class<?> entityClass, Config config) {
        EntityTable entityTable = EntityTableFactory.getEntityTable(entityClass);
        StringBuilder sb = new StringBuilder();
        String table = TemplateSqlHelper.getTableName(config, entityClass);
        sb.append(table).append(" ").append(TemplateSqlHelper.tableAlias(entityClass));
        List<EntityField> fields = entityTable.getAssociationFields();
        for (EntityField field : fields) {
            if (AssociationUtils.isAssociationField(field)) {
                sb.append(" ").append(getAssociationTable(TemplateSqlHelper.tableAlias(entityClass), field, config));
            }
        }
        return sb.toString();
    }

    public static String getAssociationTable(String masterTableName, EntityField field, Config config) {
        StringBuilder sb = new StringBuilder();
        if (field.isAnnotationPresent(OneToOne.class)) {
            OneToOne oneToOne = field.getAnnotation(OneToOne.class);
            JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
            sb.append("left join ").append(TemplateSqlHelper.getTableName(config, field.getJavaType())).append("  ").append("_").append(field.getName()).append(" on ").append(masterTableName).append(".").append(joinColumn.name()).append("=").append("_").append(field.getName()).append(".").append(oneToOne.mappedBy());
        }
        if (field.isAnnotationPresent(OneToMany.class)) {
            OneToMany oneToMany = field.getAnnotation(OneToMany.class);
            JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
            sb.append("left join ").append(TemplateSqlHelper.getTableName(config, oneToMany.targetEntity())).append("  ").append("_").append(field.getName()).append(" on ").append(masterTableName).append(".").append(joinColumn.name()).append("=").append("_").append(field.getName()).append(".").append(oneToMany.mappedBy());
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


    public static String selectColumnsFromTable(Config config, Class<?> entityClass) {
        String sql = "select " +
                AssociationSqlHelper.columns(entityClass) +
                " FROM " +
                AssociationSqlHelper.tableName(entityClass, config) +
                " ";
        return sql;
    }

    public static String columns(Class<?> entityClass) {
        StringBuilder sql = new StringBuilder();

        sql.append(getSelectColumnsFromMasterTable(entityClass, TemplateSqlHelper.tableAlias(entityClass), null));

        List<EntityField> fields = EntityTableFactory.getEntityTable(entityClass).getAssociationFields();
        for (EntityField field : fields) {
            if (field.isAnnotationPresent(OneToOne.class)) {
                String columnPrefix = "_" + field.getName();
                String aliasPrefix = field.getName();
                sql.append(",").append(getSelectColumnsFromAssociationTable(field, columnPrefix, aliasPrefix));
            }
            if (field.isAnnotationPresent(OneToMany.class)) {
                String columnPrefix = "_" + field.getName();
                String aliasPrefix = field.getName();
                sql.append(",").append(getSelectColumnsFromAssociationTable(field, columnPrefix, aliasPrefix));
            }
        }
        return sql.toString();
    }

    public static String getSelectColumnsFromAssociationTable(EntityField field, String columnPrefix, String aliasPrefix) {
        Class<?> entityClass = getAssociationEntityClass(field);
        if (StringUtils.isNotEmpty(aliasPrefix)) {
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
            if (StringUtils.isNotEmpty(aliasPrefix)) {
                selectBuilder.append(aliasPrefix);
            }
            selectBuilder.append(entityColumn.getProperty()).append(",");
        }
        return selectBuilder.substring(0, selectBuilder.length() - 1);
    }

    public static String getSelectColumnsFromMasterTable(Class<?> entityClass, String columnPrefix, String aliasPrefix) {
        EntityTable entityTable = EntityTableFactory.getEntityTable(entityClass);
        if (entityTable.getBaseSelect() != null) {
            return entityTable.getBaseSelect();
        }
        Set<EntityColumn> columnList = EntityTableFactory.getColumns(entityClass);
        StringBuilder selectBuilder = new StringBuilder();

        if (StringUtils.isNotEmpty(aliasPrefix)) {
            aliasPrefix += "_";
        }

        for (EntityColumn entityColumn : columnList) {
            if (StringUtils.isNotEmpty(columnPrefix) && !entityColumn.getColumn().contains(".")) {
                selectBuilder.append(columnPrefix).append(".");
            }
            selectBuilder.append(entityColumn.getColumn()).append(" as ");
            if (StringUtils.isNotEmpty(aliasPrefix)) {
                selectBuilder.append(aliasPrefix);
            }
            selectBuilder.append(entityColumn.getProperty()).append(",");
        }
        entityTable.setBaseSelect(selectBuilder.substring(0, selectBuilder.length() - 1));
        return entityTable.getBaseSelect();
    }

}
