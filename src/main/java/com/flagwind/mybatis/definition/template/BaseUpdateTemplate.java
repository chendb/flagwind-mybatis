package com.flagwind.mybatis.definition.template;

import com.flagwind.mybatis.definition.TemplateContext;
import com.flagwind.mybatis.definition.helper.ObjectSqlHelper;
import com.flagwind.mybatis.definition.helper.TemplateSqlHelper;
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
        String sql = TemplateSqlHelper.updateTable(context.getConfig(), entityClass) +
                TemplateSqlHelper.updateSetColumns(entityClass, null, false, false) +
                TemplateSqlHelper.wherePKColumns(entityClass, null);
        return sql;
    }


    public String updateList(MappedStatement ms) {
        final Class<?> entityClass = getEntityClass(ms);
        //开始拼sql

        String sql = "<foreach collection=\"_list\" item=\"record\" separator=\";\" >" +
                TemplateSqlHelper.updateTable(context.getConfig(), entityClass) +
                TemplateSqlHelper.updateSetColumns(entityClass, "record", false, false) +
                TemplateSqlHelper.wherePKColumns(entityClass, "record") +
                "</foreach>";
        return sql;
    }

    /**
     * 批量更新部分字段
     *
     * @param ms 映射申明
     * @return
     */
    public String modify(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        String sql = TemplateSqlHelper.updateTable(context.getConfig(), entityClass) +
                ObjectSqlHelper.getUpdatePartSetSql("_map") +
                ObjectSqlHelper.getWhereSql("_clause", 5);
        return sql;
    }

    /**
     * 通过主键更新不为null的字段
     *
     * @param ms 映射申明
     * @return
     */
    public String updateSelective(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        String sql = TemplateSqlHelper.updateTable(context.getConfig(), entityClass) +
                TemplateSqlHelper.updateSetColumns(entityClass, null, true, getConfig().isNotEmpty()) +
                TemplateSqlHelper.wherePKColumns(entityClass, null);
        return sql;
    }


}