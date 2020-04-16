package com.flagwind.mybatis.datasource.mult;

import com.flagwind.mybatis.definition.interceptor.PaginationInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@AutoConfigureBefore(name = "com.flagwind.mybatis.spring.autoconfigure.FlagwindAutoConfiguration")
public class MultipleBootstrap
{
	@Bean
	public PaginationInterceptor paginationInterceptor(){
		return new PaginationInterceptor();
	}

	public static void main(String[] args)
	{
		//org.elasticsearch.transport.netty4.Netty4InternalESLogger
		SpringApplication.run(MultipleBootstrap.class, args);
	}
}