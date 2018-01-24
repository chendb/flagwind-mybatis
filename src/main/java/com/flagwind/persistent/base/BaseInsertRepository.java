package com.flagwind.persistent.base;

import com.flagwind.mybatis.provider.base.BaseInsertProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.UpdateProvider;


import java.io.Serializable;

public interface BaseInsertRepository<E, ID extends Serializable> {

    @InsertProvider(type = BaseInsertProvider.class, method = "dynamicSQL")
    <S extends E> void insert(S entity);


    @UpdateProvider(type = BaseInsertProvider.class, method = "dynamicSQL")
    <S extends E> void insertList(@Param("_list")Iterable<S> entities);
}
