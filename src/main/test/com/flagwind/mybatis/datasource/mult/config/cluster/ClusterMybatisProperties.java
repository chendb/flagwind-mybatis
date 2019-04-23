package com.flagwind.mybatis.datasource.mult.config.cluster;

import com.flagwind.mybatis.spring.autoconfigure.MybatisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = MybatisProperties.MYBATIS_PREFIX+".cluster")
public class ClusterMybatisProperties extends MybatisProperties
{
}
