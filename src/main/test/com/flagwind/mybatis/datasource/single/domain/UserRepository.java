package com.flagwind.mybatis.datasource.single.domain;

import com.flagwind.mybatis.entity.User;
import com.flagwind.persistent.AbstractRepository;
import com.flagwind.persistent.model.Clause;
import com.flagwind.persistent.model.Paging;
import com.flagwind.persistent.model.Sorting;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface UserRepository extends AbstractRepository<User,String> {

    @Select("select t.* from com_user t   where ${@} ")
    List<User> departmentId(@Param("departmentId") String departmentId);


    @Select({"<script>", "select t.* from com_user t where ${@clause()} ", "</script>"})
    List<User> querys(@Param("_clause") Clause clause, @Param("_paging") Paging page, @Param("_sorts") Sorting[] sorts);

}
