package com.flagwind.persistent.base;

import com.flagwind.mybatis.definition.template.BaseDeleteTemplate;
import com.flagwind.persistent.model.Clause;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.Param;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;


public interface BaseDeleteRepository <ID extends Serializable>{

    @Transactional
    @DeleteProvider(type = BaseDeleteTemplate.class, method = "dynamicSQL")
    int deleteById(@Param("_key") ID id);


    @Transactional
    @DeleteProvider(type = BaseDeleteTemplate.class, method = "dynamicSQL")
    int delete(@Param("_clause") Clause clause);
}
