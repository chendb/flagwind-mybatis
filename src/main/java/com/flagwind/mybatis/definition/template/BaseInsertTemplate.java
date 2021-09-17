package com.flagwind.mybatis.definition.template;

import com.flagwind.mybatis.definition.TemplateContext;
import org.apache.ibatis.mapping.MappedStatement;

public class BaseInsertTemplate extends MapperTemplate {

    public BaseInsertTemplate(Class<?> mapperClass, TemplateContext mapperResolver) {
        super(mapperClass, mapperResolver);
    }


    /**
     * 插入全部,这段代码比较复杂，这里举个例子
     * CountryU生成的insert方法结构如下：
     * <pre>
     * &lt;bind name="countryname_bind" value='@java.util.UUID@randomUUID().toString().execute("-", "")'/&gt;
     * INSERT INTO country_u(id,countryname,countrycode) VALUES
     * &lt;trim prefix="(" suffix=")" suffixOverrides=","&gt;
     * &lt;if test="id != null"&gt;#{id,javaType=java.lang.Integer},&lt;/if&gt;
     * &lt;if test="id == null"&gt;#{id,javaType=java.lang.Integer},&lt;/if&gt;
     * &lt;if test="countryname != null"&gt;#{countryname,javaType=java.lang.String},&lt;/if&gt;
     * &lt;if test="countryname == null"&gt;#{countryname_bind,javaType=java.lang.String},&lt;/if&gt;
     * &lt;if test="countrycode != null"&gt;#{countrycode,javaType=java.lang.String},&lt;/if&gt;
     * &lt;if test="countrycode == null"&gt;#{countrycode,javaType=java.lang.String},&lt;/if&gt;
     * &lt;/trim&gt;
     * </pre>
     *
     * @param ms 映射申明
     * @return
     */
    public String insert(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        return getSqlBuilder(ms).insert(ms, entityClass);
    }


    /**
     * 批量插入
     *
     * @param ms 映射申明
     */
    public String insertList(MappedStatement ms) {
        final Class<?> entityClass = getEntityClass(ms);
        return getSqlBuilder(ms).insertList(entityClass);
    }

    /**
     * 插入，主键id，自增
     *
     * @param ms 映射申明
     */
    public String insertUseGeneratedKeys(MappedStatement ms) {
        final Class<?> entityClass = getEntityClass(ms);
        return getSqlBuilder(ms).insertUseGeneratedKeys(entityClass);
    }
}