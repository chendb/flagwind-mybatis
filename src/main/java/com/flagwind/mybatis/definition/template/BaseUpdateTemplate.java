package com.flagwind.mybatis.definition.template;

import com.flagwind.mybatis.definition.TemplateContext;
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
        return getSqlBuilder(ms).update(entityClass);
    }


    public String updateList(MappedStatement ms) {
        final Class<?> entityClass = getEntityClass(ms);
        //开始拼sql
        return getSqlBuilder(ms).updateList(entityClass);
    }

    /**
     * 批量更新部分字段
     *
     * @param ms 映射申明
     * @return
     */
    public String modify(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        return getSqlBuilder(ms).modify(entityClass);
    }

    /**
     * 通过主键更新不为null的字段
     *
     * @param ms 映射申明
     * @return
     */
    public String updateSelective(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        return getSqlBuilder(ms).updateSelective(entityClass);
    }


}