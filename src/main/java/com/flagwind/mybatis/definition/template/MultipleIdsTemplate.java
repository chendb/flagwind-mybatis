package com.flagwind.mybatis.definition.template;

import com.flagwind.mybatis.definition.TemplateContext;
import org.apache.ibatis.mapping.MappedStatement;

/**
 * @author chendb
 */
public class MultipleIdsTemplate extends MapperTemplate {

    public MultipleIdsTemplate(Class<?> mapperClass, TemplateContext mapperResolver) {
        super(mapperClass, mapperResolver);
    }

    /**
     * 根据主键字符串进行删除，类中只有存在一个带有@Id注解的字段
     *
     * @param ms 映射申明
     */
    public String deleteByIds(MappedStatement ms) {
        final Class<?> entityClass = getEntityClass(ms);
        return getSqlBuilder(ms).deleteByIds(entityClass);
    }

    /**
     * 根据主键字符串进行查询，类中只有存在一个带有@Id注解的字段
     *
     * @param ms 映射申明
     */
    public String selectByIds(MappedStatement ms) {
        final Class<?> entityClass = getEntityClass(ms);

        //将返回值修改为实体类型
        setResultType(ms, entityClass);
        return getSqlBuilder(ms).selectByIds(entityClass);
    }
}