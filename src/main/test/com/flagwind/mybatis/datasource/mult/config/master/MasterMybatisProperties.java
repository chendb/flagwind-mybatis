package com.flagwind.mybatis.datasource.mult.config.master;

import com.flagwind.mybatis.spring.autoconfigure.MybatisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = MybatisProperties.MYBATIS_PREFIX+".master")
public class MasterMybatisProperties extends MybatisProperties
{
}
