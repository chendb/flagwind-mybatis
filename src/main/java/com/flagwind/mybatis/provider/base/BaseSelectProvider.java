package com.flagwind.mybatis.provider.base;

import com.flagwind.mybatis.common.MapperResolver;
import com.flagwind.mybatis.helpers.AssociationSqlHelper;
import com.flagwind.mybatis.helpers.SqlHelper;
import com.flagwind.mybatis.provider.MapperTemplate;
import com.flagwind.mybatis.utils.TemplateSqlUtils;

import org.apache.ibatis.mapping.MappedStatement;

/**
 * BaseSelectProvider实现类，基础方法实现类
 *
 * @author chendb
 */
public class BaseSelectProvider extends MapperTemplate {

    public BaseSelectProvider(Class<?> mapperClass, MapperResolver mapperResolver) {
        super(mapperClass, mapperResolver);
    }

    protected String selectAllColumnsFromTable(Class<?> entityClass) {
        StringBuilder sql = new StringBuilder();
        if (AssociationSqlHelper.hasAssociation(entityClass)) {
            AssociationSqlHelper.registerEntityClass(entityClass, mapperResolver.getConfig());
            sql.append(AssociationSqlHelper.selectAllColumns(entityClass));

            sql.append(AssociationSqlHelper.fromTable(entityClass, mapperResolver.getConfig()));
        } else {
            sql.append(SqlHelper.selectAllColumns(entityClass));
            boolean hasTableAlias = sql.toString().contains(".");
            sql.append(SqlHelper.fromTable(entityClass, tableName(entityClass,hasTableAlias)));
        }
        return sql.toString();

    }

    /**
     * 根据主键进行关联查询（当没有关联信息时与getById一样）
     *
     * @param ms
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
            sql.append(SqlHelper.wherePKColumn(entityClass, "_key"));
        }
        return sql.toString();
    }

    /**
     * 关联查询（当没有关联信息时与query一样）
     *
     * @param ms
     * @return
     */
    public String seek(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        //修改返回值类型为实体类型
        setResultType(ms, entityClass);
        StringBuilder sql = new StringBuilder();
        sql.append(selectAllColumnsFromTable(entityClass));
        sql.append(TemplateSqlUtils.getWhereSql("_clause", 5));
        return sql.toString();
    }

    /**
     * 查询
     *
     * @param ms
     * @return
     */
    public String query(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        //修改返回值类型为实体类型
        setResultType(ms, entityClass);
        StringBuilder sql = new StringBuilder();
        sql.append(SqlHelper.selectAllColumns(entityClass));
        boolean hasTableAlias = sql.toString().contains(".");
        sql.append(SqlHelper.fromTable(entityClass, tableName(entityClass, hasTableAlias)));
        sql.append(TemplateSqlUtils.getWhereSql("_clause", 5));
        sql.append(SqlHelper.orderByDefault(entityClass));
        return sql.toString();
    }

    /**
     * 查询
     *
     * @param ms
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
     * @param ms
     */
    public String getById(MappedStatement ms) {
        final Class<?> entityClass = getEntityClass(ms);
        //将返回值修改为实体类型
        setResultType(ms, entityClass);
        StringBuilder sql = new StringBuilder();
        sql.append(SqlHelper.selectAllColumns(entityClass));
        boolean hasTableAlias = sql.toString().contains(".");
        sql.append(SqlHelper.fromTable(entityClass, tableName(entityClass,hasTableAlias)));
        sql.append(SqlHelper.wherePKColumn(entityClass, "_key"));
        return sql.toString();
    }

    /**
     * 查询总数
     *
     * @param ms
     * @return
     */
    public String count(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        StringBuilder sql = new StringBuilder();
        sql.append(SqlHelper.selectCount(entityClass));
        boolean hasTableAlias = sql.toString().contains(".");
        sql.append(SqlHelper.fromTable(entityClass, tableName(entityClass,hasTableAlias)));
        sql.append(TemplateSqlUtils.getWhereSql("_clause",5));
        return sql.toString();
    }

    /**
     * 根据主键查询总数
     *
     * @param ms
     * @return String
     */
    public String existsById(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        StringBuilder sql = new StringBuilder();
        sql.append(SqlHelper.selectCountExists(entityClass));
        sql.append(SqlHelper.fromTable(entityClass, tableName(entityClass)));
        sql.append(SqlHelper.wherePKColumns(entityClass));
        return sql.toString();
    }

    public String exists(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        StringBuilder sql = new StringBuilder();
        sql.append(SqlHelper.selectCountExists(entityClass));
        sql.append(SqlHelper.fromTable(entityClass, tableName(entityClass)));
        sql.append(TemplateSqlUtils.getWhereSql("_clause",5));
        return sql.toString();
    }

    /**
     * 查询全部结果
     *
     * @param ms
     * @return
     */
    public String getAll(MappedStatement ms) {
        final Class<?> entityClass = getEntityClass(ms);
        //修改返回值类型为实体类型
        setResultType(ms, entityClass);
        StringBuilder sql = new StringBuilder();
        sql.append(SqlHelper.selectAllColumns(entityClass));
        boolean hasTableAlias = sql.toString().contains(".");
        sql.append(SqlHelper.fromTable(entityClass, tableName(entityClass,hasTableAlias)));
        sql.append(SqlHelper.orderByDefault(entityClass));
        return sql.toString();
    }


    /**
     * 聚合多条件查询
     * @param ms
     * @return
     */
    public String querySelective(MappedStatement ms) {

        StringBuilder sql = new StringBuilder();
        sql.append("select ");
        sql.append(TemplateSqlUtils.getQueryFieldColumnSql());
        sql.append(" from ${_table} ");
        sql.append(TemplateSqlUtils.getWhereSql("_clause", 5));
        sql.append(" ");
        sql.append(TemplateSqlUtils.getQueryFieldGroupBySql());
        sql.append(" ");
        sql.append(TemplateSqlUtils.getSortingSql());
        return sql.toString();
    }
}