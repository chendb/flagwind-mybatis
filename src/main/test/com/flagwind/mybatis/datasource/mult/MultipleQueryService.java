package com.flagwind.mybatis.datasource.mult;

import com.flagwind.mybatis.datasource.mult.domain.cluster.AppMenuRepository;
import com.flagwind.mybatis.datasource.mult.domain.master.TestRoleRepository;
import com.flagwind.mybatis.entity.Role;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest( classes = {MultipleBootstrap.class},webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(value = {"classpath:test/master.properties", "classpath:test/cluster.properties"})
public class MultipleQueryService
{
	@Autowired
	private AppMenuRepository appMenuRepository;

	@Autowired
	private TestRoleRepository roleRepository;
//
//
	@Test
	public void testGetAllOnCluster()
	{
		List<Role> menuList = roleRepository.getAll();
 		TestCase.assertTrue("查询总数量为：" + menuList.size(), menuList.size() > 0);
	}
}
