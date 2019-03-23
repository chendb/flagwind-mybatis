package com.flagwind.mybatis.definition.template;

import com.flagwind.mybatis.common.TemplateContext;
import com.flagwind.mybatis.definition.helper.AssociationSqlHelper;
import com.flagwind.mybatis.definition.helper.TemplateSqlHelper;
import com.flagwind.mybatis.definition.helper.ObjectSqlHelper;

import org.apache.ibatis.mapping.MappedStatement;

/**
 * BaseSelectProvider实现类，基础方法实现类
 *
 * @author chendb
 */
public class BaseSelectTemplate extends MapperTemplate {

    public BaseSelectTemplate(Class<?> mapperClass, TemplateContext mapperResolver) {
        super(mapperClass, mapperResolver);
    }

    protected String selectAllColumnsFromTable(Class<?> entityClass) {
        StringBuilder sql = new StringBuilder();
        if (AssociationSqlHelper.hasAssociation(entityClass)) {
            AssociationSqlHelper.registerEntityClass(entityClass, context.getConfig());
            sql.append(AssociationSqlHelper.selectAllColumns(entityClass));

            sql.append(AssociationSqlHelper.fromTable(entityClass, context.getConfig()));
        } else {
            sql.append(TemplateSqlHelper.selectAllColumns(entityClass));
            boolean hasTableAlias = sql.toString().contains(".");
            sql.append(TemplateSqlHelper.fromTable(entityClass, tableName(entityClass,hasTableAlias)));
        }
        return sql.toString();

    }

    /**
     * 根据主键进行关联查询（当没有关联信息时与getById一样）
     *
     * @param ms 映射申明
     */
    public String seekById(MappedStatement ms) {
        final Class<?> entityClass = getEntityClass(ms);
        //将返回值修改为实体类型
        setResultType(ms, entityClass);
        StringBuilder sql = new StringBuilder();
        sql.append(selectAllColumnsFromTable(entityClass));
        if (AssociationSqlHelper.hasAssociation(entityClass)) {
            sql.append(AssociationSqlHelper.wherePKColumn(entityClass.getSimpleName(), entityClass, "_key"));
        } else {
            sql.append(TemplateSqlHelper.wherePKColumn(entityClass, "_key"));
        }
        return sql.toString();
    }

    /**
     * 关联查询（当没有关联信息时与query一样）
     *
     * @param ms 映射申明
     */
    public String seek(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        //修改返回值类型为实体类型
        setResultType(ms, entityClass);
        StringBuilder sql = new StringBuilder();
        sql.append(selectAllColumnsFromTable(entityClass));
        sql.append(ObjectSqlHelper.getWhereSql("_clause", 5));
        return sql.toString();
    }

    /**
     * 查询
     *
     * @param ms 映射申明
     */
    public String query(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        //修改返回值类型为实体类型
        setResultType(ms, entityClass);
        StringBuilder sql = new StringBuilder();
        sql.append(TemplateSqlHelper.selectAllColumns(entityClass));
        boolean hasTableAlias = sql.toString().contains(".");
        sql.append(TemplateSqlHelper.fromTable(entityClass, tableName(entityClass, hasTableAlias)));
        sql.append(ObjectSqlHelper.getWhereSql("_clause", 5));
        sql.append(TemplateSqlHelper.orderByDefault(entityClass));
        return sql.toString();
    }

    /**
     * 查询
     *
     * @param ms 映射申明
     * @return
     */
    public String take(MappedStatement ms) {
        return query(ms);
    }

    public String page(MappedStatement ms) {
        return query(ms);
    }

    /**
     * 根据主键进行查询
     *
     * @param ms 映射申明
     */
    public String getById(MappedStatement ms) {
        final Class<?> entityClass = getEntityClass(ms);
        //将返回值修改为实体类型
        setResultType(ms, entityClass);
        StringBuilder sql = new StringBuilder();
        sql.append(TemplateSqlHelper.selectAllColumns(entityClass));
        boolean hasTableAlias = sql.toString().contains(".");
        sql.append(TemplateSqlHelper.fromTable(entityClass, tableName(entityClass,hasTableAlias)));
        sql.append(TemplateSqlHelper.wherePKColumn(entityClass, "_key"));
        return sql.toString();
    }

    /**
     * 查询总数
     *
     * @param ms 映射申明
     * @return
     */
    public String count(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        StringBuilder sql = new StringBuilder();
        sql.append(TemplateSqlHelper.selectCount(entityClass));
        boolean hasTableAlias = sql.toString().contains(".");
        sql.append(TemplateSqlHelper.fromTable(entityClass, tableName(entityClass,hasTableAlias)));
        sql.append(ObjectSqlHelper.getWhereSql("_clause",5));
        return sql.toString();
    }

    /**
     * 根据主键查询总数
     *
     * @param ms 映射申明
     * @return String
     */
    public String existsById(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        StringBuilder sql = new StringBuilder();
        sql.append(TemplateSqlHelper.selectCountExists(entityClass));
        sql.append(TemplateSqlHelper.fromTable(entityClass, tableName(entityClass)));
        sql.append(TemplateSqlHelper.wherePKColumns(entityClass,null));
        return sql.toString();
    }

    public String exists(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        StringBuilder sql = new StringBuilder();
        sql.append(TemplateSqlHelper.selectCountExists(entityClass));
        sql.append(TemplateSqlHelper.fromTable(entityClass, tableName(entityClass)));
        sql.append(ObjectSqlHelper.getWhereSql("_clause",5));
        return sql.toString();
    }

    /**
     * 查询全部结果
     *
     * @param ms 映射申明
     */
    public String getAll(MappedStatement ms) {
        final Class<?> entityClass = getEntityClass(ms);
        //修改返回值类型为实体类型
        setResultType(ms, entityClass);
        StringBuilder sql = new StringBuilder();
        sql.append(TemplateSqlHelper.selectAllColumns(entityClass));
        boolean hasTableAlias = sql.toString().contains(".");
        sql.append(TemplateSqlHelper.fromTable(entityClass, tableName(entityClass,hasTableAlias)));
        sql.append(TemplateSqlHelper.orderByDefault(entityClass));
        return sql.toString();
    }


    /**
     * 聚合多条件查询
     * @param ms 映射申明
     */
    public String querySelective(MappedStatement ms) {

        StringBuilder sql = new StringBuilder();
        sql.append("select ");
        sql.append(ObjectSqlHelper.getQueryFieldColumnSql());
        sql.append(" from ${_table} ");
        sql.append(ObjectSqlHelper.getWhereSql("_clause", 5));
        sql.append(" ");
        sql.append(ObjectSqlHelper.getQueryFieldGroupBySql());
        sql.append(" ");
        sql.append(ObjectSqlHelper.getSortingSql());
        return sql.toString();
    }
}