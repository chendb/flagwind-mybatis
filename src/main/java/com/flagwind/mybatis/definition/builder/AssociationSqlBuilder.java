package com.flagwind.mybatis.definition.builder;

import com.flagwind.commons.StringUtils;
import com.flagwind.mybatis.definition.Config;
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

public  class AssociationSqlBuilder {
    private Config config;

    public AssociationSqlBuilder(Config config) {
        this.config = config;
    }

    public void registerEntityClass(Class<?> entityClass) {
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
     * 获取实体的表名以及关联表名
     *
     * @param entityClass
     */
    private String tableName(Class<?> entityClass) {
        EntityTable entityTable = EntityTableFactory.getEntityTable(entityClass);
        StringBuilder sb = new StringBuilder();
        String table = BaseSqlBuilder.getTableName(config, entityClass);
        sb.append(table).append(" ").append(BaseSqlBuilder.getTableAlias(entityClass));
        List<EntityField> fields = entityTable.getAssociationFields();
        for (EntityField field : fields) {
            if (AssociationUtils.isAssociationField(field)) {
                sb.append(" ").append(getAssociationTable(BaseSqlBuilder.getTableAlias(entityClass), field, config));
            }
        }
        return sb.toString();
    }

    private String getAssociationTable(String masterTableName, EntityField field, Config config) {
        StringBuilder sb = new StringBuilder();
        if (field.isAnnotationPresent(OneToOne.class)) {
            OneToOne oneToOne = field.getAnnotation(OneToOne.class);
            JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
            sb.append("left join ").append(BaseSqlBuilder.getTableName(config, field.getJavaType())).append("  ")
                    .append("_").append(field.getName()).append(" on ")
                    .append(masterTableName).append(".")
                    .append(BaseSqlBuilder.getColumnName(config, joinColumn.name()))
                    .append("=").append("_").append(field.getName()).append(".")
                    .append(BaseSqlBuilder.getColumnName(config, oneToOne.mappedBy()));
        }
        if (field.isAnnotationPresent(OneToMany.class)) {
            OneToMany oneToMany = field.getAnnotation(OneToMany.class);
            JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
            sb.append("left join ").append(BaseSqlBuilder.getTableName(config, oneToMany.targetEntity()))
                    .append("  ").append("_").append(field.getName()).append(" on ")
                    .append(masterTableName).append(".")
                    .append(BaseSqlBuilder.getColumnName(config, joinColumn.name()))
                    .append("=").append("_").append(field.getName()).append(".")
                    .append(BaseSqlBuilder.getColumnName(config, oneToMany.mappedBy()));
        }
        return sb.toString();
    }

    private Class<?> getAssociationEntityClass(EntityField field) {
        if (field.isAnnotationPresent(OneToOne.class)) {
            return field.getJavaType();
        }
        if (field.isAnnotationPresent(OneToMany.class)) {
            OneToMany oneToMany = field.getAnnotation(OneToMany.class);
            return oneToMany.targetEntity();
        }
        return null;
    }


    public String selectColumnsFromTable(Class<?> entityClass) {
        String sql = "select " +
                this.columns(entityClass) +
                " FROM " +
                this.tableName(entityClass) +
                " ";
        return sql;
    }

    private String columns(Class<?> entityClass) {
        StringBuilder sql = new StringBuilder();
        Set<EntityColumn> columnList = EntityTableFactory.getColumns(entityClass);
        sql.append(BaseSqlBuilder.columns(config, columnList, BaseSqlBuilder.getTableAlias(entityClass), null));

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

    /**
     * 生成选择的列SQL，格式如下：_user.name as user_name,_user.id as user_id
     *
     * @param field        列字段
     * @param columnPrefix 列前缀
     * @param aliasPrefix  别名前缀
     * @return
     */
    private String getSelectColumnsFromAssociationTable(EntityField field, String columnPrefix, String aliasPrefix) {
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
            selectBuilder.append(BaseSqlBuilder.getColumnName(config,entityColumn));
            selectBuilder.append(" as ");
            if (StringUtils.isNotEmpty(aliasPrefix)) {
                selectBuilder.append(aliasPrefix);
            }
            selectBuilder.append(entityColumn.getProperty()).append(",");
        }
        return selectBuilder.substring(0, selectBuilder.length() - 1);
    }

}
