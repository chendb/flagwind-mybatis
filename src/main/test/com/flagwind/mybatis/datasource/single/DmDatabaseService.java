package com.flagwind.mybatis.datasource.single;


import com.flagwind.mybatis.datasource.single.domain.DynamicRepository;
import com.flagwind.mybatis.datasource.single.domain.RoleRepository;
import com.flagwind.mybatis.entity.Role;
import com.flagwind.persistent.AggregateType;
import com.flagwind.persistent.QueryField;
import com.flagwind.persistent.model.*;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Timestamp;
import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SingleBootstrap.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(value = {"classpath:test/dm.properties"})
public class DmDatabaseService {


    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private DynamicRepository dynamicRepository;

    private Random random = new Random();


    @Test
    public void testInsert() {
        Role role = new Role();
        role.setId("test_tenant_" + UUID.randomUUID().toString().substring(0, 10));
        role.setName("test-角色" + random.nextInt());
        role.setCreateTime(new Timestamp(System.currentTimeMillis()));
        roleRepository.insert(role);
    }

    @Test
    public void testInsertList() {
        List<Role> roles = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Role role = new Role();
            role.setId("test_tenant_" + i + "_" + UUID.randomUUID().toString().substring(0, 10));
            role.setName("test-角色" + i + "_" + random.nextInt());
            role.setCreateTime(new Timestamp(System.currentTimeMillis()));
            roles.add(role);
        }

        roleRepository.insertList(roles);
    }

    @Test
    public void testUpdate() {

        List<Role> roles = roleRepository.query(SingleClause.like("id", "test%"), null, null);
        if (roles != null && roles.size() > 0) {
            Role role = roles.get(0);
            role.setDescription("测试更新");
            roleRepository.update(role);
        }
    }

    @Test
    public void deleteById() {

        Role role = roleRepository.query(SingleClause.like("id", "test_tenant_%"), null, null).get(0);
        roleRepository.deleteById(role.getId());
    }


    @Test
    public void testPage() {
        Paging paging = new Paging(1L, 10L);
        List<Role> roles = roleRepository.query(SingleClause.equal("disabled", 0), paging, new Sorting[]{Sorting.ascending("createTime")});
        TestCase.assertTrue("查询总数量为：" + roles.size(), roles.size() > 0);
    }

    @Test
    public void testSeek() {
        List<Role> menuList = roleRepository.seek(SingleClause.equal("disabled", 0), null, new Sorting[]{Sorting.ascending("createTime")});
        TestCase.assertTrue("查询总数量为：" + menuList.size(), menuList.size() > 0);
    }


    @Test
    public void testQuery() {
        List<Role> menuList = roleRepository.query(SingleClause.equal("disabled", 0), null, new Sorting[]{Sorting.ascending("createTime")});
        TestCase.assertTrue("查询总数量为：" + menuList.size(), menuList.size() > 0);
    }

    @Test
    public void testGetAll() {
        List<Role> menuList = roleRepository.getAll();
        TestCase.assertTrue("查询总数量为：" + menuList.size(), menuList.size() > 0);
    }

    @Test
    public void testQuerySelective() {
        SingleClause s = new SingleClause("length(id)", ClauseOperator.LessThanEqual, 3);
        s.setParameterType(ParameterType.Column);
        List<QueryField> fields = new ArrayList<>();
        fields.add(new QueryField() {{
            setColumn("id");
            setAlias("xid");
            setType(AggregateType.Max);
        }});
        Sorting[] sorts = new Sorting[]{Sorting.ascending("xid")};
        List<Map<String, Object>> menuList1 = dynamicRepository.dynamicSelective("com_role", fields, s, -1, -1, sorts);

        TestCase.assertTrue("查询总数量为：" + menuList1.size(), menuList1.size() > 0);
    }

    @Test
    public void testDelete() {
        int count = roleRepository.delete(SingleClause.like("id", "test_tenant_%"));
        TestCase.assertTrue("查询总数量为：" + count, count > 0);
    }
}
