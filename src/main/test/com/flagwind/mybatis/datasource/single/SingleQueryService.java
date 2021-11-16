package com.flagwind.mybatis.datasource.single;


import com.flagwind.mybatis.datasource.single.domain.DynamicRepository;
import com.flagwind.mybatis.datasource.single.domain.RoleRepository;
import com.flagwind.mybatis.datasource.single.domain.UserRepository;
import com.flagwind.mybatis.entity.Role;
import com.flagwind.mybatis.entity.User;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SingleBootstrap.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(value = {"classpath:test/default.properties"})
public class SingleQueryService {


    @Autowired
    private RoleRepository roleRepository;



    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DynamicRepository dynamicRepository;


//    @Test
//    public void testDynamicQuery1() {
//        List<Role> roles = dynamicRepository.queryDDFF("com_role",SingleClause.equal("disabled", 0));
//        TestCase.assertTrue("查询总数量为：" + roles.size(), roles.size() > 0);
//    }

    @Test
    public void testDynamicDriver() {
        List<Role> roles = dynamicRepository.selectRole( SingleClause.equal("disabled", 0));
        TestCase.assertTrue("查询总数量为：" + roles.size(), roles.size() > 0);
    }

    @Test
    public void testDynamicQuery() {
        List<Map<String, Object>> roles = dynamicRepository.dynamicQuery("com_role", SingleClause.equal("disabled", 0), Paging.build(1L,10L), null);
        TestCase.assertTrue("查询总数量为：" + roles.size(), roles.size() > 0);
    }


    @Test
    public void testInsert() {
        Role role = new Role();
        role.setId("test_tenant_" + UUID.randomUUID().toString().substring(0, 10));
        role.setName("租房新增");
        role.setCreateTime(new Timestamp(System.currentTimeMillis()));
        roleRepository.insert(role);
    }

    @Test
    public void testUser() {
        List<User> menuList = userRepository.departmentId("1");
        TestCase.assertTrue("查询总数量为：" + menuList.size(), menuList.size() > 0);
    }

    @Test
    public void testCase() {


        List<QueryField> fields = new ArrayList<>();
        fields.add(new QueryField() {{
            setColumn("@decode(sex,0:'女士',1:'男士','未知') ");
            setAlias("sex");
            setType(AggregateType.Max);
        }});
        Sorting[] sorts = new Sorting[]{Sorting.ascending("sex")};
        List<Map<String, Object>> menuList1 = dynamicRepository.dynamicSelective("com_user", fields, null, -1, -1, sorts);

        TestCase.assertTrue("查询总数量为：" + menuList1.size(), menuList1.size() > 0);
    }


    @Test
    public void testPage() {
        Paging paging = new Paging(1L, 10L);
        List<Role> menuList = roleRepository.query(SingleClause.equal("disabled", 0), paging,
                new Sorting[]{Sorting.ascending("createTime"),Sorting.ascending("disabled")});
        TestCase.assertTrue("查询总数量为：" + menuList.size(), menuList.size() > 0);
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
    public void testCount() {
        ChildClause childClause = ChildClause.include("instanceId", "id", "insta_case");
        childClause.add(SingleClause.greaterThanEqual("createTime", "2020-08-08"));
        childClause.add(CombineClause.and(
                SingleClause.greaterThanEqual("archiveTime", "2020-08-08"),
                SingleClause.equal("status", "5")
        ));
        long count = roleRepository.count(childClause);
        TestCase.assertTrue("查询总数量为：" + count, count > 0);
    }

    @Test
    public void testGetAll() {
        List<Role> menuList = roleRepository.getAll();
        TestCase.assertTrue("查询总数量为：" + menuList.size(), menuList.size() > 0);
    }

    @Test
    public void testQuerySelective() {
        SingleClause s = new SingleClause("id", ClauseOperator.LessThanEqual, "length(id)");
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

}
