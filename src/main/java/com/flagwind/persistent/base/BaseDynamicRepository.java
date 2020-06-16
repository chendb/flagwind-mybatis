package com.flagwind.persistent.base;

import com.flagwind.mybatis.definition.template.BaseDynamicTemplate;
import com.flagwind.persistent.QueryField;
import com.flagwind.persistent.model.Clause;
import com.flagwind.persistent.model.Paging;
import com.flagwind.persistent.model.Sorting;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;
import java.util.Map;

/**
 * 查询操作
 * @author chendb
 */
public interface BaseDynamicRepository {

    /**
     * 选择性查询
     * @param table 表名
     * @param fields 要查询的字段
     * @param clause 条件
     * @param sorts 排序条件
     */
    @SelectProvider(type = BaseDynamicTemplate.class, method = "dynamicSQL")
    List<Map<String,Object>> dynamicSelective(@Param("_table") String table, @Param("_fields") List<QueryField> fields, @Param("_clause") Clause clause, @Param("_startIndex") Integer startIndex,
                                            @Param("_endIndex") Integer endIndex, @Param("_sorts") Sorting[] sorts);


//    @SelectProvider(type = BaseDynamicTemplate.class, method = "dynamicSQL")
//    <T> List<T> dynamicSeek(@Param("_clause") Clause clause, @Param("_paging") Paging page, @Param("_sorts") Sorting[] sorts);

    @SelectProvider(type = BaseDynamicTemplate.class, method = "dynamicSQL")
    <T> List<T> dynamicQuery(@Param("_clause") Clause clause, @Param("_paging") Paging page, @Param("_sorts") Sorting[] sorts);

}
