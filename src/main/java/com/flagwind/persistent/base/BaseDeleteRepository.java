package com.flagwind.persistent.base;

import com.flagwind.mybatis.provider.base.BaseDeleteProvider;
import com.flagwind.persistent.model.Clause;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;


public interface BaseDeleteRepository <ID extends Serializable>{

    @DeleteProvider(type = BaseDeleteProvider.class, method = "dynamicSQL")
    int deleteById(@Param("_key") ID id);


    @DeleteProvider(type = BaseDeleteProvider.class, method = "dynamicSQL")
    int delete(@Param("_clause") Clause clause);
}
