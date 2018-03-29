package com.flagwind.mybatis.provider.base;


import com.flagwind.mybatis.common.MapperResolver;
import com.flagwind.mybatis.helpers.SqlHelper;
import com.flagwind.mybatis.provider.MapperTemplate;
import com.flagwind.mybatis.utils.TemplateSqlUtils;

import org.apache.ibatis.mapping.MappedStatement;


public class BaseDeleteProvider extends MapperTemplate {

    public BaseDeleteProvider(Class<?> mapperClass, MapperResolver mapperResolver) {
        super(mapperClass, mapperResolver);
    }

    /**
     * 通过条件删除
     *
     * @param ms
     * @return
     */
    public String delete(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        StringBuilder sql = new StringBuilder();
        sql.append(SqlHelper.deleteFromTable(entityClass, tableName(entityClass)));
        sql.append(TemplateSqlUtils.getWhereSql("_clause", 5));
        return sql.toString();
    }

    /**
     * 通过主键删除
     *
     * @param ms
     */
    public String deleteById(MappedStatement ms) {
        final Class<?> entityClass = getEntityClass(ms);
        StringBuilder sql = new StringBuilder();
        sql.append(SqlHelper.deleteFromTable(entityClass, tableName(entityClass)));
        sql.append(SqlHelper.wherePKColumn(entityClass, "_key"));
        return sql.toString();
    }
}
