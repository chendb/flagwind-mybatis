package com.flagwind.mybatis.datasource.single;


import com.flagwind.mybatis.datasource.single.domain.RoleRepository;
import com.flagwind.mybatis.entity.Role;
import com.flagwind.persistent.AggregateType;
import com.flagwind.persistent.QueryField;
import com.flagwind.persistent.model.ClauseOperator;
import com.flagwind.persistent.model.ParameterType;
import com.flagwind.persistent.model.SingleClause;
import com.flagwind.persistent.model.Sorting;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SingleBootstrap.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(value = {"classpath:test/default.properties"})
public class SingleQueryService
{


	@Autowired
	private RoleRepository roleRepository;


	@Test
	public void testGetAll()
	{
		List<Role> menuList = roleRepository.getAll();
		TestCase.assertTrue("查询总数量为：" + menuList.size(), menuList.size() > 0);
	}

	@Test
	public void testQuerySelective()
	{
		SingleClause s = new SingleClause("id", ClauseOperator.LessThanEqual,"length(id)");
		s.setParameterType(ParameterType.Column);
		List<QueryField> fields = new ArrayList<>();
		fields.add(new QueryField(){{
			setColumn("id");
			setAlias("xid");
			setType(AggregateType.Max);
		}});
		Sorting[] sortings = new Sorting[]{Sorting.ascending("xid")};
		List<Map<String,Object>> menuList1 = roleRepository.querySelective("com_role",fields,s,-1,-1,sortings);

		TestCase.assertTrue("查询总数量为：" + menuList1.size(), menuList1.size() > 0);
	}
}
