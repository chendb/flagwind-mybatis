package com.flagwind.mybatis.datasource.single;


import com.flagwind.mybatis.datasource.single.domain.UserRepository;
import com.flagwind.mybatis.entity.User;
import com.flagwind.persistent.model.*;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SingleBootstrap.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(value = {"classpath:test/default.properties"})
public class SingleTenantService {


    @Autowired
    private UserRepository userRepository;




    @Test
    public void testInsert() {
        User entity = new User();
        entity.setId("test_tenant_" + UUID.randomUUID().toString().substring(0, 10));
        entity.setUsername("test_tenant_" + UUID.randomUUID().toString().substring(0, 10));
        entity.setPassword("test_tenant_" + UUID.randomUUID().toString().substring(0, 10));
        entity.setName("租房新增");
        entity.setCreateTime(new Timestamp(System.currentTimeMillis()));
        userRepository.insert(entity);
    }

    @Test
    public void testDelete(){
     int count = userRepository.delete(SingleClause.like("id", "test_tenant_%"));
        TestCase.assertTrue("查询总数量为：" + count, count > 0);
    }


    @Test
    public void testPage() {
        Paging paging = new Paging(1L, 10L);
        List<User> menuList = userRepository.query(SingleClause.like("id", "test_tenant_%"), paging, new Sorting[]{Sorting.ascending("createTime")});
        TestCase.assertTrue("查询总数量为：" + menuList.size(), menuList.size() > 0);
    }


    @Test
    public void testQuery() {
        List<User> menuList = userRepository.query(SingleClause.like("id", "test_tenant_%"), null, new Sorting[]{Sorting.ascending("createTime")});
        TestCase.assertTrue("查询总数量为：" + menuList.size(), menuList.size() > 0);
    }

    @Test
    public void testChildQuery() {
        ChildClause childClause = ChildClause.include("id", "id", "com_role_member");
        childClause.add(SingleClause.greaterThanEqual("createTime", "1920-08-08"));
        childClause.add(CombineClause.and(
                SingleClause.greaterThanEqual("createTime", "2020-08-08"),
                SingleClause.equal("disabled", "0")
        ));
        long count = userRepository.count(childClause);
        TestCase.assertTrue("查询总数量为：" + count, count > 0);
    }

    @Test
    public void testGetAll() {
        List<User> menuList = userRepository.getAll();
        TestCase.assertTrue("查询总数量为：" + menuList.size(), menuList.size() > 0);
    }


}
