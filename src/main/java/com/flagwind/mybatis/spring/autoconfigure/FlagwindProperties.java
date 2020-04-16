package com.flagwind.mybatis.spring.autoconfigure;

import com.flagwind.mybatis.definition.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;


/**
 * 这个类存在的主要目的是方便 IDE 自动提示 mapper. 开头的配置
 *
 * @author chendb
 * @since 2017/1/2.
 */
@ConfigurationProperties(prefix = FlagwindProperties.PREFIX)
public class FlagwindProperties extends Config {

}
