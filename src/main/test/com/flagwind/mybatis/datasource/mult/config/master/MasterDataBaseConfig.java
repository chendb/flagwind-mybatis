package com.flagwind.mybatis.datasource.mult.config.master;


import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.flagwind.mybatis.definition.interceptor.PaginationInterceptor;
import com.flagwind.mybatis.spring.autoconfigure.AbstractAutoConfiguration;
import com.flagwind.mybatis.spring.autoconfigure.ConfigurationCustomizer;
import com.flagwind.mybatis.spring.autoconfigure.FlagwindProperties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.List;


@Configuration
@EnableConfigurationProperties({MasterMybatisProperties.class, FlagwindProperties.class})
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
@AutoConfigureBefore(name = "org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration")
@com.flagwind.mybatis.spring.annotation.MapperScan(
		basePackages = MasterDataBaseConfig.PACKAGE,
		sqlSessionTemplateRef = "masterSqlSessionTemplate",
		sqlSessionFactoryRef = "masterSqlSessionFactory",
		prefix = "flagwind.mybatis.master")
public class MasterDataBaseConfig extends AbstractAutoConfiguration
{
	// 精确到 master 目录，以便跟其他数据源隔离
	static final String PACKAGE = "com.flagwind.mybatis.datasource.mult.domain.master";

	private static final Log LOG = LogFactory.getLog(MasterDataBaseConfig.class);



	public MasterDataBaseConfig(MasterMybatisProperties properties, FlagwindProperties flagwindProperties, ObjectProvider<Interceptor[]> interceptorsProvider, ResourceLoader resourceLoader, ObjectProvider<DatabaseIdProvider> databaseIdProvider, ObjectProvider<List<ConfigurationCustomizer>> configurationCustomizersProvider)
	{
		super(properties, flagwindProperties, interceptorsProvider, resourceLoader, databaseIdProvider, configurationCustomizersProvider);
	}

	@Bean(name = "masterDataSource")
	@Primary
	@ConfigurationProperties(prefix = "spring.datasource.druid.master")
	public DataSource dataSource(Environment environment)
	{
		return DruidDataSourceBuilder.create().build();

	}

	@ConditionalOnMissingBean
	@Bean(name = "masterTransactionManager")
	public PlatformTransactionManager transactionManager(@Qualifier("masterDataSource") DataSource dataSource) {
		DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
		transactionManager.setDataSource(dataSource);
		return transactionManager;
	}

	@Bean(name = "masterSqlSessionFactory")
	public SqlSessionFactory sqlSessionFactory(@Qualifier("masterDataSource") DataSource dataSource, PaginationInterceptor paginationInterceptor) throws Exception
	{
	 return super.sqlSessionFactory(dataSource);
	}

	@Bean(name = "masterSqlSessionTemplate")
	public SqlSessionTemplate sqlSessionTemplate(@Qualifier("masterSqlSessionFactory")  SqlSessionFactory sqlSessionFactory)
	{
		return  super.sqlSessionTemplate(sqlSessionFactory);
	}
}
