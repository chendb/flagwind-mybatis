package com.flagwind.mybatis.definition.template;

import com.flagwind.mybatis.definition.TemplateContext;
import com.flagwind.mybatis.definition.helper.AssociationSqlHelper;
import com.flagwind.mybatis.definition.helper.ObjectSqlHelper;
import com.flagwind.mybatis.definition.helper.TemplateSqlHelper;
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

    protected String selectColumnsFromTable(Class<?> entityClass) {
        StringBuilder sql = new StringBuilder();
        if (AssociationSqlHelper.hasAssociation(entityClass)) {
            AssociationSqlHelper.registerEntityClass(entityClass, context.getConfig());

            sql.append(AssociationSqlHelper.selectColumnsFromTable(context.getConfig(), entityClass));

        } else {

            sql.append(TemplateSqlHelper.selectColumnsFromTable(context.getConfig(), entityClass));


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
        sql.append(selectColumnsFromTable(entityClass));
        if (AssociationSqlHelper.hasAssociation(entityClass)) {
            sql.append(AssociationSqlHelper.wherePKColumn(entityClass, "_key"));
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
        String sql = selectColumnsFromTable(entityClass) +
                ObjectSqlHelper.getWhereSql("_clause", 5) +
                ObjectSqlHelper.getSortingSql();
        return sql;
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

        String sql = TemplateSqlHelper.selectColumnsFromTable(context.getConfig(), entityClass) +
                ObjectSqlHelper.getWhereSql("_clause", 5) +
                ObjectSqlHelper.getSortingSql();
        return sql;
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


    /**
     * 根据主键进行查询
     *
     * @param ms 映射申明
     */
    public String getById(MappedStatement ms) {
        final Class<?> entityClass = getEntityClass(ms);
        //将返回值修改为实体类型
        setResultType(ms, entityClass);

        String sql = TemplateSqlHelper.selectColumnsFromTable(context.getConfig(), entityClass) +
                TemplateSqlHelper.wherePKColumn(entityClass, "_key");
        return sql;
    }

    /**
     * 查询总数
     *
     * @param ms 映射申明
     * @return
     */
    public String count(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        String sql = TemplateSqlHelper.selectCountFromTable(context.getConfig(), entityClass) +
                ObjectSqlHelper.getWhereSql("_clause", 5);
        return sql;
    }

    /**
     * 根据主键查询总数
     *
     * @param ms 映射申明
     * @return String
     */
    public String existsById(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        String sql = TemplateSqlHelper.selectCountExistsFromTable(context.getConfig(), entityClass) +
                TemplateSqlHelper.wherePKColumns(entityClass, null);
        return sql;
    }

    public String exists(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        String sql = TemplateSqlHelper.selectCountExistsFromTable(context.getConfig(), entityClass) +
                ObjectSqlHelper.getWhereSql("_clause", 5);
        return sql;
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
        String sql = TemplateSqlHelper.selectColumnsFromTable(context.getConfig(), entityClass) +
                ObjectSqlHelper.getSortingSql();

        return sql;
    }

}