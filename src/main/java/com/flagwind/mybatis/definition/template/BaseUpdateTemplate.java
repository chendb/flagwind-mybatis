package com.flagwind.mybatis.definition.template;

import com.flagwind.mybatis.common.TemplateContext;
import com.flagwind.mybatis.definition.helper.TemplateSqlHelper;
import com.flagwind.mybatis.definition.helper.ObjectSqlHelper;
import org.apache.ibatis.mapping.MappedStatement;

public class BaseUpdateTemplate extends MapperTemplate {

    public BaseUpdateTemplate(Class<?> mapperClass, TemplateContext mapperResolver) {
        super(mapperClass, mapperResolver);
    }

    /**
     * 通过主键更新全部字段
     *
     * @param ms 映射申明
     */
    public String update(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        StringBuilder sql = new StringBuilder();
        sql.append(TemplateSqlHelper.updateTable(entityClass, tableName(entityClass)));
        sql.append(TemplateSqlHelper.updateSetColumns(entityClass, null, false, false));
        sql.append(TemplateSqlHelper.wherePKColumns(entityClass));
        return sql.toString();
    }

    /**
     * 批量更新部分字段
     *
     * @param ms 映射申明
     * @return
     */
    public String modify(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        StringBuilder sql = new StringBuilder();
        sql.append(TemplateSqlHelper.updateTable(entityClass, tableName(entityClass)));
        sql.append(ObjectSqlHelper.getUpdatePartSetSql("_map"));
        sql.append(ObjectSqlHelper.getWhereSql("_clause", 5));
        return sql.toString();
    }

    /**
     * 通过主键更新不为null的字段
     *
     * @param ms 映射申明
     * @return
     */
    public String updateSelective(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        StringBuilder sql = new StringBuilder();
        sql.append(TemplateSqlHelper.updateTable(entityClass, tableName(entityClass)));
        sql.append(TemplateSqlHelper.updateSetColumns(entityClass, null, true, getConfig().isNotEmpty()));
        sql.append(TemplateSqlHelper.wherePKColumns(entityClass));
        return sql.toString();
    }


}