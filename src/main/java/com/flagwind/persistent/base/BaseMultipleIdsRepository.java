package com.flagwind.persistent.base;

import com.flagwind.mybatis.definition.template.MultipleIdsTemplate;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

public interface BaseMultipleIdsRepository<E, ID extends Serializable> {
    
    @Transactional
    @DeleteProvider(type = MultipleIdsTemplate.class, method = "dynamicSQL")
    int deleteByIds(@Param("_keys") String keys);

    @SelectProvider(type = MultipleIdsTemplate.class, method = "dynamicSQL")
    List<E> fetchByIds(@Param("_keys") String keys);
}