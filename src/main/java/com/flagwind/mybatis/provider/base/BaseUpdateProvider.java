package com.flagwind.mybatis.provider.base;

import com.flagwind.mybatis.common.MapperResolver;
import com.flagwind.mybatis.provider.MapperTemplate;
import com.flagwind.mybatis.utils.TemplateSqlUtils;
import com.flagwind.mybatis.helpers.SqlHelper;
import org.apache.ibatis.mapping.MappedStatement;

public class BaseUpdateProvider extends MapperTemplate {

    public BaseUpdateProvider(Class<?> mapperClass, MapperResolver mapperResolver) {
        super(mapperClass, mapperResolver);
    }

    /**
     * 通过主键更新全部字段
     *
     * @param ms
     */
    public String update(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        StringBuilder sql = new StringBuilder();
        sql.append(SqlHelper.updateTable(entityClass, tableName(entityClass)));
        sql.append(SqlHelper.updateSetColumns(entityClass, null, false, false));
        sql.append(SqlHelper.wherePKColumns(entityClass));
        return sql.toString();
    }

    /**
     * 批量更新部分字段
     *
     * @param ms
     * @return
     */
    public String modify(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        StringBuilder sql = new StringBuilder();
        sql.append(SqlHelper.updateTable(entityClass, tableName(entityClass)));
        sql.append(TemplateSqlUtils.getUpdatePartSetSql("_map"));
        sql.append(TemplateSqlUtils.getWhereSql("_clause", 5));
        return sql.toString();
    }

    /**
     * 通过主键更新不为null的字段
     *
     * @param ms
     * @return
     */
    public String updateSelective(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        StringBuilder sql = new StringBuilder();
        sql.append(SqlHelper.updateTable(entityClass, tableName(entityClass)));
        sql.append(SqlHelper.updateSetColumns(entityClass, null, true, isNotEmpty()));
        sql.append(SqlHelper.wherePKColumns(entityClass));
        return sql.toString();
    }


}