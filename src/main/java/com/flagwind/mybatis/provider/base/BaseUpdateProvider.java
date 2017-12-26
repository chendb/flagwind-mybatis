package com.flagwind.mybatis.provider.base;

import com.flagwind.mybatis.entity.MapperHelper;
import com.flagwind.mybatis.provider.MapperTemplate;
import com.flagwind.mybatis.utils.ClauseUtils;
import com.flagwind.mybatis.helpers.SqlHelper;
import org.apache.ibatis.mapping.MappedStatement;

public class BaseUpdateProvider extends MapperTemplate {

    public BaseUpdateProvider(Class<?> mapperClass, MapperHelper mapperHelper) {
        super(mapperClass, mapperHelper);
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

    public String updatePart(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        StringBuilder sql = new StringBuilder();
        sql.append(SqlHelper.updateTable(entityClass, tableName(entityClass)));
        sql.append(ClauseUtils.getUpdatePartSetSql("_map"));
        sql.append(ClauseUtils.getWhereSql("_clause", 5));
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