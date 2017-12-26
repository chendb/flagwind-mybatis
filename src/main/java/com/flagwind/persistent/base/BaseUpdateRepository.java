package com.flagwind.persistent.base;

import com.flagwind.mybatis.provider.base.BaseUpdateProvider;
import com.flagwind.persistent.model.Clause;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.UpdateProvider;
import java.util.HashMap;
import java.util.List;

public interface BaseUpdateRepository<E> {

    @UpdateProvider(type = BaseUpdateProvider.class, method = "dynamicSQL")
    public <S extends E> void update(S entity);


    public <S extends E> void updateList(@Param("_list")List<S> entities);


    @UpdateProvider(type = BaseUpdateProvider.class, method = "dynamicSQL")
    public <S extends E> void updatePart(@Param("_map") HashMap<String, Object> map, @Param("_clause")Clause clause);

}
