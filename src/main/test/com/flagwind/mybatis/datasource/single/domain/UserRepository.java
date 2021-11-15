package com.flagwind.mybatis.datasource.single.domain;

import com.flagwind.mybatis.entity.User;
import com.flagwind.persistent.AbstractRepository;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface UserRepository extends AbstractRepository<User,String> {

    @Select("select t.* from com_user t   where t.departmentId = #{departmentId} ")
    List<User> departmentId(@Param("departmentId") String departmentId);

}
