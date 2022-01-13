package com.flagwind.mybatis.definition.builder;

import com.flagwind.commons.StringUtils;
import com.flagwind.mybatis.code.DatabaseType;
import com.flagwind.mybatis.definition.Config;
import com.flagwind.mybatis.metadata.EntityTable;
import com.flagwind.mybatis.metadata.EntityTableFactory;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.ibatis.mapping.MappedStatement;

public class CompositeSqlBuilder {

    private Config config;

    private ObjectSqlBuilder objectSqlBuilder;

    private AssociationSqlBuilder associationSqlBuilder;

    private TemplateSqlBuilder templateSqlBuilder;

    public ObjectSqlBuilder getObjectSqlBuilder() {
        return objectSqlBuilder;
    }

    public void setObjectSqlBuilder(ObjectSqlBuilder objectSqlBuilder) {
        this.objectSqlBuilder = objectSqlBuilder;
    }

    public AssociationSqlBuilder getAssociationSqlBuilder() {
        return associationSqlBuilder;
    }

    public void setAssociationSqlBuilder(AssociationSqlBuilder associationSqlBuilder) {
        this.associationSqlBuilder = associationSqlBuilder;
    }

    public TemplateSqlBuilder getTemplateSqlBuilder() {
        return templateSqlBuilder;
    }

    public void setTemplateSqlBuilder(TemplateSqlBuilder templateSqlBuilder) {
        this.templateSqlBuilder = templateSqlBuilder;
    }

    public CompositeSqlBuilder(Config config) {
        this.config = config;
        this.objectSqlBuilder = new ObjectSqlBuilder(config);
        this.associationSqlBuilder = new AssociationSqlBuilder(config);
        this.templateSqlBuilder = new TemplateSqlBuilder(config);
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

    protected String selectColumnsFromTable(Class<?> entityClass, boolean hasAssociation) {
        StringBuilder sql = new StringBuilder();
        if (hasAssociation) {
            associationSqlBuilder.registerEntityClass(entityClass);
            sql.append(associationSqlBuilder.selectColumnsFromTable(entityClass));
        } else {
            sql.append(templateSqlBuilder.selectColumnsFromTable(entityClass));
        }
        return sql.toString();
    }

    public String delete(Class<?> entityClass) {
        String sql = templateSqlBuilder.deleteFromTable(entityClass) +
                objectSqlBuilder.getWhereSql("_clause");
        return sql;
    }

    public String deleteById(Class<?> entityClass) {
        String sql = templateSqlBuilder.deleteFromTable(entityClass) + templateSqlBuilder.wherePKColumn(entityClass, "_key");
        return sql;
    }

    public String dynamicSelective() {

        String sql = "select " +
                objectSqlBuilder.getQueryFieldColumnSql() +
                " from ${_table} " +
                objectSqlBuilder.getWhereSql("_clause") +
                " " +
                objectSqlBuilder.getQueryFieldGroupBySql() +
                " ";
        return sql;
    }


    public String dynamicQuery() {
        String sql = "select * from ${_table} " +
                objectSqlBuilder.getWhereSql("_clause") +
                objectSqlBuilder.getSortingSql();
        return sql;
    }

    public String insert(MappedStatement ms, Class<?> entityClass) {
        StringBuilder sql = new StringBuilder();
        //先处理cache或bind节点
        MutablePair<String, Boolean> pair = templateSqlBuilder.getSequenceKeyMapping(entityClass, ms);

        // Identity列只能有一个
        // Boolean hasIdentityKey = pair.right;
        if (StringUtils.isNotEmpty(pair.left)) {
            sql.append(pair.left);
        }
        sql.append(templateSqlBuilder.insertIntoTable(entityClass));
        sql.append(templateSqlBuilder.insertColumns(entityClass, false));
        sql.append(templateSqlBuilder.insertValues(entityClass));
        return sql.toString();
    }

    /**
     * 批量插入
     */
    public String insertList(Class<?> entityClass) {
        StringBuilder sql = new StringBuilder();
        sql.append(templateSqlBuilder.insertIntoTable(entityClass));
        sql.append(templateSqlBuilder.insertColumns(entityClass, false));
        sql.append("  ");
        DatabaseType databaseType = DatabaseType.parse(config.getDatabase());
        if (DatabaseType.DM == databaseType || DatabaseType.Oscar == databaseType) {
            sql.append("<foreach collection=\"_list\" item=\"record\" separator=\"UNION ALL\" >");
            sql.append(" select ");

            //获取全部列
            sql.append(templateSqlBuilder.getInsertValueSql(entityClass, "record"));

            sql.append(" from dual ");
            sql.append("</foreach>");
        } else {
            sql.append(" values ");
            sql.append("<foreach collection=\"_list\" item=\"record\" separator=\",\" >");
            sql.append("<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");

            //获取全部列
            sql.append(templateSqlBuilder.getInsertValueSql(entityClass, "record"));

            sql.append("</trim>");
            sql.append("</foreach>");
        }
        return sql.toString();
    }


    /**
     * 插入，主键id，自增
     */
    public String insertUseGeneratedKeys(Class<?> entityClass) {

        String sql = templateSqlBuilder.insertIntoTable(entityClass) +
                templateSqlBuilder.insertColumns(entityClass, true) +
                templateSqlBuilder.insertValuesColumns(entityClass, true, false, false);
        return sql;
    }

    public String seekById(Class<?> entityClass) {
        StringBuilder sql = new StringBuilder();
        boolean hasAssociation = hasAssociation(entityClass);
        sql.append(selectColumnsFromTable(entityClass, hasAssociation));
        sql.append(templateSqlBuilder.wherePKColumn(hasAssociation ? TemplateSqlBuilder.getTableAlias(entityClass) : null, entityClass, "_key"));
        return sql.toString();
    }

    public String seek(Class<?> entityClass) {
        String sql = selectColumnsFromTable(entityClass, true) +
                objectSqlBuilder.getWhereSql("_clause") +
                objectSqlBuilder.getSortingSql();
        return sql;
    }

    public String query(Class<?> entityClass) {
        String sql = selectColumnsFromTable(entityClass, false) +
                objectSqlBuilder.getWhereSql("_clause") +
                objectSqlBuilder.getSortingSql();
        return sql;
    }

    public String getById(Class<?> entityClass) {

        String sql = selectColumnsFromTable(entityClass, false) +
                templateSqlBuilder.wherePKColumn(entityClass, "_key");
        return sql;
    }

    public String getAll(Class<?> entityClass) {
        String sql = selectColumnsFromTable(entityClass, false) +
                objectSqlBuilder.getSortingSql();

        return sql;
    }

    public String count(Class<?> entityClass) {
        String sql = templateSqlBuilder.selectCountFromTable(entityClass) +
                objectSqlBuilder.getWhereSql("_clause");
        return sql;
    }

    public String update(Class<?> entityClass) {
        String sql = templateSqlBuilder.updateTable(entityClass) +
                templateSqlBuilder.updateSetColumns(entityClass, null, false, false) +
                templateSqlBuilder.wherePKColumns(entityClass, null);
        return sql;
    }

    public String updateList(Class<?> entityClass) {
        //开始拼sql
        String sql = "<foreach collection=\"_list\" item=\"record\" separator=\";\" >" +
                templateSqlBuilder.updateTable(entityClass) +
                templateSqlBuilder.updateSetColumns(entityClass, "record", false, false) +
                templateSqlBuilder.wherePKColumns(entityClass, "record") +
                "</foreach>";
        return sql;
    }

    public String modify(Class<?> entityClass) {
        String sql = templateSqlBuilder.updateTable(entityClass) +
                objectSqlBuilder.getUpdatePartSetSql("_map") +
                objectSqlBuilder.getWhereSql("_clause");
        return sql;
    }

    public String updateSelective(Class<?> entityClass) {
        String sql = templateSqlBuilder.updateTable(entityClass) +
                templateSqlBuilder.updateSetColumns(entityClass, null, true, config.isNotEmpty()) +
                templateSqlBuilder.wherePKColumns(entityClass, null);
        return sql;
    }

    public String deleteByIds(Class<?> entityClass) {
        StringBuilder sql = new StringBuilder();
        sql.append(templateSqlBuilder.deleteFromTable(entityClass));
        sql.append(templateSqlBuilder.whereKeyInList(entityClass));
        return sql.toString();
    }

    public String selectByIds(Class<?> entityClass) {
        StringBuilder sql = new StringBuilder();
        sql.append(templateSqlBuilder.selectColumnsFromTable(entityClass));
        sql.append(templateSqlBuilder.whereKeyInList(entityClass));
        return sql.toString();
    }

}
