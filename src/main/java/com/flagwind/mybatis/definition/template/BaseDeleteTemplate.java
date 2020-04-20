package com.flagwind.mybatis.definition.template;


import com.flagwind.mybatis.definition.TemplateContext;
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
        String sql = TemplateSqlHelper.deleteFromTable(context.getConfig(), entityClass) +
                ObjectSqlHelper.getWhereSql("_clause", 5);
        return sql;
    }

    /**
     * 通过主键删除
     *
     * @param ms 映射申明
     */
    public String deleteById(MappedStatement ms) {
        final Class<?> entityClass = getEntityClass(ms);
        String sql = TemplateSqlHelper.deleteFromTable(context.getConfig(), entityClass) +
                TemplateSqlHelper.wherePKColumn(entityClass, "_key");
        return sql;
    }
}
