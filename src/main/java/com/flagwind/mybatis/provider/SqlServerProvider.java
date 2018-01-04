package com.flagwind.mybatis.provider;

import com.flagwind.mybatis.helpers.SqlHelper;
import com.flagwind.mybatis.common.MapperResolver;
import org.apache.ibatis.mapping.MappedStatement;

public class SqlServerProvider extends MapperTemplate {

    public SqlServerProvider(Class<?> mapperClass, MapperResolver mapperResolver) {
        super(mapperClass, mapperResolver);
    }

    /**
     * 插入
     *
     * @param ms
     */
    public String insert(MappedStatement ms) {
        final Class<?> entityClass = getEntityClass(ms);
        StringBuilder sql = new StringBuilder();
        sql.append(SqlHelper.insertIntoTable(entityClass, tableName(entityClass)));
        sql.append(SqlHelper.insertColumns(entityClass, true, false, false));
        sql.append(SqlHelper.insertValuesColumns(entityClass, true, false, false));
        return sql.toString();
    }

    /**
     * 插入不为null的字段
     *
     * @param ms
     * @return
     */
    public String insertSelective(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        StringBuilder sql = new StringBuilder();
        sql.append(SqlHelper.insertIntoTable(entityClass, tableName(entityClass)));
        sql.append(SqlHelper.insertColumns(entityClass, true, true, isNotEmpty()));
        sql.append(SqlHelper.insertValuesColumns(entityClass, true, true, isNotEmpty()));
        return sql.toString();
    }
}