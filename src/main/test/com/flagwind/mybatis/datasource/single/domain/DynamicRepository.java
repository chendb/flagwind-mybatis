package com.flagwind.mybatis.datasource.single.domain;

import com.flagwind.mybatis.entity.Role;
import com.flagwind.mybatis.scripting.KnownEntityType;
import com.flagwind.mybatis.scripting.RepositoryDriver;
import com.flagwind.persistent.base.BaseDynamicRepository;
import com.flagwind.persistent.model.Clause;
import org.apache.ibatis.annotations.Lang;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author chendb
 * @description:
 * @date 2020-04-28 18:11:51
 */
@KnownEntityType({Role.class})
public interface DynamicRepository extends BaseDynamicRepository {

    @Lang(RepositoryDriver.class)

    @Select({"<script>", "SELECT * FROM com_role WHERE ${@clause()}", "</script>"})
//    @Select({"'<script>'+", "'SELECT * FROM '+TABLE(type)", "+'</script>'"})
    List<Role> selectRole(@Param("_clause") Clause clause);
}
