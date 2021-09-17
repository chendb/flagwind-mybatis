package com.flagwind.mybatis.definition.template;

import com.flagwind.mybatis.definition.TemplateContext;
import org.apache.ibatis.mapping.MappedStatement;

/**
 * BaseSelectProvider实现类，基础方法实现类
 *
 * @author chendb
 */
public class BaseSelectTemplate extends MapperTemplate {

    public BaseSelectTemplate(Class<?> mapperClass, TemplateContext mapperResolver) {
        super(mapperClass, mapperResolver);
    }


    /**
     * 根据主键进行关联查询（当没有关联信息时与getById一样）
     *
     * @param ms 映射申明
     */
    public String seekById(MappedStatement ms) {
        final Class<?> entityClass = getEntityClass(ms);
        setAssociationResultType(ms,entityClass);
        return getSqlBuilder(ms).seekById(entityClass);
    }

    /**
     * 关联查询（当没有关联信息时与query一样）
     *
     * @param ms 映射申明
     */
    public String seek(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        setAssociationResultType(ms,entityClass);
        return getSqlBuilder(ms).seek(entityClass);
    }

    /**
     * 查询
     *
     * @param ms 映射申明
     */
    public String query(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        setGeneralResultType(ms,entityClass);
        return getSqlBuilder(ms).query(entityClass);
    }

    /**
     * 查询
     *
     * @param ms 映射申明
     * @return
     */
    public String take(MappedStatement ms) {
        return query(ms);
    }


    /**
     * 根据主键进行查询
     *
     * @param ms 映射申明
     */
    public String getById(MappedStatement ms) {
        final Class<?> entityClass = getEntityClass(ms);
        // 将返回值修改为实体类型
        setGeneralResultType(ms,entityClass);
        return getSqlBuilder(ms).getById(entityClass);
    }

    /**
     * 查询总数
     *
     * @param ms 映射申明
     * @return
     */
    public String count(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        return getSqlBuilder(ms).count(entityClass);
    }


    /**
     * 查询全部结果
     *
     * @param ms 映射申明
     */
    public String getAll(MappedStatement ms) {
        final Class<?> entityClass = getEntityClass(ms);
        setGeneralResultType(ms,entityClass);
        return getSqlBuilder(ms).getAll(entityClass);
    }

}