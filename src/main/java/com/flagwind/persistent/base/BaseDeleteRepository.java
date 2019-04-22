package com.flagwind.persistent.base;

import java.io.Serializable;

import com.flagwind.mybatis.definition.template.BaseDeleteTemplate;
import com.flagwind.persistent.model.Clause;

import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.Param;


public interface BaseDeleteRepository <ID extends Serializable>{


    @DeleteProvider(type = BaseDeleteTemplate.class, method = "dynamicSQL")
    int deleteById(@Param("_key") ID id);



    @DeleteProvider(type = BaseDeleteTemplate.class, method = "dynamicSQL")
    int delete(@Param("_clause") Clause clause);
}
