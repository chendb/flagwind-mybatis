package com.flagwind.mybatis.datasource.single.config;


import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@com.flagwind.mybatis.spring.annotation.MapperScan(basePackages = "com.flagwind.mybatis.datasource.single.domain")
public class DatabaseConfig
{
	@Bean
	public DataSource dataSource()
	{
		return DruidDataSourceBuilder.create().build();
	}
}
