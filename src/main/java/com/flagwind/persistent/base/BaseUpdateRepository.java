package com.flagwind.persistent.base;

import com.flagwind.mybatis.provider.base.BaseUpdateProvider;
import com.flagwind.persistent.model.Clause;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.UpdateProvider;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;

public interface BaseUpdateRepository<E> {

    /**
     * 单条更新
     * @param entity 实体
     * @param <S> 实体类型
     */
    @Transactional
    @UpdateProvider(type = BaseUpdateProvider.class, method = "dynamicSQL")
    <S extends E> void update(S entity);


    /**
     * 批量更新
     * @param entities 实体集
     * @param <S> 实体类型
     */
    @Transactional
    <S extends E> void updateList(@Param("_list")List<S> entities);


    @Transactional
    @UpdateProvider(type = BaseUpdateProvider.class, method = "dynamicSQL")
    void modify(@Param("_map") HashMap<String, Object> map, @Param("_clause")Clause clause);

}
