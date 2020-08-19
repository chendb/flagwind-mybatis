package com.flagwind.mybatis.datasource.single.config;


import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.flagwind.mybatis.definition.interceptor.PaginationInterceptor;
import com.flagwind.mybatis.tenant.AnnotationTenantSqlParser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Arrays;

@Configuration
@com.flagwind.mybatis.spring.annotation.MapperScan(basePackages = "com.flagwind.mybatis.datasource.single.domain")
public class DatabaseConfig
{

	@Bean
	public PaginationInterceptor paginationInterceptor() {
		PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
		paginationInterceptor.setSqlParserList(Arrays.asList(new AnnotationTenantSqlParser()));
		return paginationInterceptor;
	}

	@Bean
	public DataSource dataSource()
	{
		return DruidDataSourceBuilder.create().build();
	}
}
