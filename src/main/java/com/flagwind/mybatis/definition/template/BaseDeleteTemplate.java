package com.flagwind.mybatis.definition.template;


import com.flagwind.mybatis.common.TemplateContext;
import com.flagwind.mybatis.definition.helper.TemplateSqlHelper;
import com.flagwind.mybatis.definition.helper.ObjectSqlHelper;

import org.apache.ibatis.mapping.MappedStatement;


public class BaseDeleteTemplate extends MapperTemplate {

    public BaseDeleteTemplate(Class<?> mapperClass, TemplateContext mapperResolver) {
        super(mapperClass, mapperResolver);
    }

    /**
     * 通过条件删除
     *
     * @param ms 映射申明
     */
    public String delete(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        StringBuilder sql = new StringBuilder();
        sql.append(TemplateSqlHelper.deleteFromTable(entityClass, tableName(entityClass)));
        sql.append(ObjectSqlHelper.getWhereSql("_clause", 5));
        return sql.toString();
    }

    /**
     * 通过主键删除
     *
     * @param ms 映射申明
     */
    public String deleteById(MappedStatement ms) {
        final Class<?> entityClass = getEntityClass(ms);
        StringBuilder sql = new StringBuilder();
        sql.append(TemplateSqlHelper.deleteFromTable(entityClass, tableName(entityClass)));
        sql.append(TemplateSqlHelper.wherePKColumn(entityClass, "_key"));
        return sql.toString();
    }
}
