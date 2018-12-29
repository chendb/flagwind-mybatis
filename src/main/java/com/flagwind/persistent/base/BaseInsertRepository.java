package com.flagwind.persistent.base;

import com.flagwind.mybatis.definition.template.BaseInsertTemplate;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.UpdateProvider;

import java.io.Serializable;

public interface BaseInsertRepository<E, ID extends Serializable> {


    @InsertProvider(type = BaseInsertTemplate.class, method = "dynamicSQL")
    <S extends E> void insert(S entity);



    @UpdateProvider(type = BaseInsertTemplate.class, method = "dynamicSQL")
    <S extends E> void insertList(@Param("_list")Iterable<S> entities);
}
