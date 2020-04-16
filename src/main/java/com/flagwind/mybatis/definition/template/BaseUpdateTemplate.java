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
        StringBuilder sql = new StringBuilder();
        sql.append(TemplateSqlHelper.updateTable(context.getConfig(), entityClass));
        sql.append(TemplateSqlHelper.updateSetColumns(entityClass, null, false, false));
        sql.append(TemplateSqlHelper.wherePKColumns(entityClass,null));
        return sql.toString();
    }


    public String updateList(MappedStatement ms) {
        final Class<?> entityClass = getEntityClass(ms);
        //开始拼sql
        StringBuilder sql = new StringBuilder();

        sql.append("<foreach collection=\"_list\" item=\"record\" separator=\";\" >");

        sql.append(TemplateSqlHelper.updateTable(context.getConfig(), entityClass));
        sql.append(TemplateSqlHelper.updateSetColumns(entityClass, "record", false, false));
        sql.append(TemplateSqlHelper.wherePKColumns(entityClass,"record"));

        sql.append("</foreach>");
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
        sql.append(TemplateSqlHelper.updateTable(context.getConfig(), entityClass));
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
        sql.append(TemplateSqlHelper.updateTable(context.getConfig(), entityClass));
        sql.append(TemplateSqlHelper.updateSetColumns(entityClass, null, true, getConfig().isNotEmpty()));
        sql.append(TemplateSqlHelper.wherePKColumns(entityClass,null));
        return sql.toString();
    }


}