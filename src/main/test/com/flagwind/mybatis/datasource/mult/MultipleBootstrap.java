package com.flagwind.mybatis.datasource.mult;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@AutoConfigureBefore(name = "com.flagwind.mybatis.spring.autoconfigure.FlagwindAutoConfiguration")
public class MultipleBootstrap
{
	public static void main(String[] args)
	{
		//org.elasticsearch.transport.netty4.Netty4InternalESLogger
		SpringApplication.run(MultipleBootstrap.class, args);
	}
}