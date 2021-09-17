package com.flagwind.mybatis.definition.template;

import com.flagwind.mybatis.definition.TemplateContext;
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
    public String dynamicSelective(MappedStatement ms) {
        return getSqlBuilder(ms).dynamicSelective();
    }


    public String dynamicQuery(MappedStatement ms) {
        return getSqlBuilder(ms).dynamicQuery();
    }

}

