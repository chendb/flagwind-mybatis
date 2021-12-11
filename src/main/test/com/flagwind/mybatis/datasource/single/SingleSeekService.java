package com.flagwind.mybatis.datasource.single;


import com.flagwind.mybatis.datasource.single.domain.FocusItemRepository;
import com.flagwind.mybatis.entity.FocusItem;
import com.flagwind.mybatis.entity.codes.FocusType;
import com.flagwind.persistent.model.SingleClause;
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
public class SingleSeekService {


	@Autowired
	private FocusItemRepository focusItemRepository;


//	@Test
//	public void testSeek() {
//		List<FocusItem> menuList = focusItemRepository.seek(null);
//		TestCase.assertTrue("查询总数量为：" + menuList.size(), menuList.size() > 0);
//	}

	@Test
	public void  testSeekById() {
		FocusItem focusItem = focusItemRepository.seekById("39c9ea6f-2882-45df-9dc0-865504cd9a96");
		TestCase.assertTrue("查询总数量为：", focusItem != null);
	}

	@Test
	public void  testGetById(){
		FocusItem focusItem = focusItemRepository.getById("01a79f2f-6c5b-48b9-86ff-051ac5421c3a");
		TestCase.assertTrue("查询总数量为：" , focusItem!=null);
	}

	@Test
	public void  testQuery(){
		FocusType focusType = new FocusType("1");
		List<FocusItem> focusItems = focusItemRepository.query(SingleClause.equal("id",focusType),null,null);
		TestCase.assertTrue("查询总数量为：" , focusItems!=null);

	}

//	@Test
//	public void testQuery() {
//		List<FocusItem> menuList = focusItemRepository.query(null);
//		TestCase.assertTrue("查询总数量为：" + menuList.size(), menuList.size() > 0);
//	}

}
