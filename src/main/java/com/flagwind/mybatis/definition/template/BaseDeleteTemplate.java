package com.flagwind.mybatis.definition.template;


import com.flagwind.mybatis.definition.TemplateContext;
import org.apache.ibatis.mapping.MappedStatement;


/**
 * @author chenabao
 */
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
        return getSqlBuilder(ms).delete(entityClass);
    }

    /**
     * 通过主键删除
     *
     * @param ms 映射申明
     */
    public String deleteById(MappedStatement ms) {
        final Class<?> entityClass = getEntityClass(ms);
        return getSqlBuilder(ms).deleteById(entityClass);
    }
}
