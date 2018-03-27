package com.flagwind.persistent.base;

import com.flagwind.mybatis.provider.MultipleIdsProvider;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

public interface BaseMultipleIdsRepository<E, ID extends Serializable> {
    
    @Transactional
    @DeleteProvider(type = MultipleIdsProvider.class, method = "dynamicSQL")
    int deleteByIds(@Param("_keys") String keys);

    @SelectProvider(type = MultipleIdsProvider.class, method = "dynamicSQL")
    List<E> fetchByIds(@Param("_keys") String keys);
}