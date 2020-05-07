package com.flagwind.mybatis.definition.template;

import com.flagwind.mybatis.definition.TemplateContext;
import com.flagwind.mybatis.definition.helper.ObjectSqlHelper;
import org.apache.ibatis.mapping.MappedStatement;

/**
 * BaseSelectProvider实现类，基础方法实现类
 *
 * @author chendb
 */
public class BaseDynamicTemplate extends MapperTemplate {

    public BaseDynamicTemplate(Class<?> mapperClass, TemplateContext mapperResolver) {
        super(mapperClass, mapperResolver);
    }

    /**
     * 聚合多条件查询
     *
     * @param ms 映射申明
     */
    public String dynamicQuery(MappedStatement ms) {

        String sql = "select " +
                ObjectSqlHelper.getQueryFieldColumnSql() +
                " from ${_table} " +
                ObjectSqlHelper.getWhereSql("_clause", 5) +
                " " +
                ObjectSqlHelper.getQueryFieldGroupBySql() +
                " ";
        return sql;
    }
}

