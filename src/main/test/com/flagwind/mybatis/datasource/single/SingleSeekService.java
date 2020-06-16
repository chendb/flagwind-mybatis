package com.flagwind.mybatis.datasource.single;


import com.flagwind.mybatis.datasource.single.domain.FocusItemRepository;
import com.flagwind.mybatis.entity.FocusItem;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SingleBootstrap.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(value = {"classpath:test/default.properties"})
public class SingleSeekService {


	@Autowired
	private FocusItemRepository focusItemRepository;


//	@Test
//	public void testSeek() {
//		List<FocusItem> menuList = focusItemRepository.seek(null);
//		TestCase.assertTrue("查询总数量为：" + menuList.size(), menuList.size() > 0);
//	}

	@Test
	public void  testSeekById(){
		FocusItem focusItem = focusItemRepository.seekById("3532680e-9fe7-4c18-adbd-35bfe86b1eb7");
		TestCase.assertTrue("查询总数量为：" , focusItem!=null);
	}

	@Test
	public void  testGetById(){
		FocusItem focusItem = focusItemRepository.getById("3532680e-9fe7-4c18-adbd-35bfe86b1eb7");
		TestCase.assertTrue("查询总数量为：" , focusItem!=null);

	}

//	@Test
//	public void testQuery() {
//		List<FocusItem> menuList = focusItemRepository.query(null);
//		TestCase.assertTrue("查询总数量为：" + menuList.size(), menuList.size() > 0);
//	}

}
