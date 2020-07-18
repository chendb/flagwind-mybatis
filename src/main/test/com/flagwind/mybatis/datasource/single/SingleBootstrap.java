package com.flagwind.mybatis.datasource.single;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@AutoConfigureBefore(name = "com.flagwind.mybatis.spring.autoconfigure.FlagwindAutoConfiguration")
public class SingleBootstrap {




    public static void main(String[] args) {
        //org.elasticsearch.transport.netty4.Netty4InternalESLogger
        SpringApplication.run(SingleBootstrap.class, args);
    }
}