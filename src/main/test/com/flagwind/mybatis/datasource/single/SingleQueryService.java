package com.flagwind.mybatis.datasource.single;



import com.flagwind.mybatis.datasource.single.domain.RoleRepository;
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
}
