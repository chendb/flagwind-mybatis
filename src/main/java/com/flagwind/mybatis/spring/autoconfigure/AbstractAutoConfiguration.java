package com.flagwind.mybatis.spring.autoconfigure;


import com.flagwind.mybatis.definition.interceptor.OffsetLimitInterceptor;
import com.flagwind.mybatis.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.List;

/**
 * 为方便多数据源扩展应用，提出一个抽象基础类，便于不同数据源配置扩展
 */
public class AbstractAutoConfiguration
{


	protected final MybatisProperties properties;

	protected final FlagwindProperties flagwindProperties;

	protected final Interceptor[] interceptors;

	protected final ResourceLoader resourceLoader;

	protected final DatabaseIdProvider databaseIdProvider;

	protected final List<ConfigurationCustomizer> configurationCustomizers;

	public AbstractAutoConfiguration(MybatisProperties properties,
									 FlagwindProperties flagwindProperties,
									 ObjectProvider<Interceptor[]> interceptorsProvider,
									 ResourceLoader resourceLoader,
									 ObjectProvider<DatabaseIdProvider> databaseIdProvider,
									 ObjectProvider<List<ConfigurationCustomizer>> configurationCustomizersProvider) {
		this.properties = properties;
		this.flagwindProperties = flagwindProperties;
		this.interceptors = interceptorsProvider.getIfAvailable();
		this.resourceLoader = resourceLoader;
		this.databaseIdProvider = databaseIdProvider.getIfAvailable();
		this.configurationCustomizers = configurationCustomizersProvider.getIfAvailable();
	}


	public void checkConfigFileExists() {
		if (this.properties.isCheckConfigLocation() && StringUtils.hasText(this.properties.getConfigLocation())) {
			Resource resource = this.resourceLoader.getResource(this.properties.getConfigLocation());
			Assert.state(resource.exists(), "Cannot find config location: " + resource
					+ " (please add config file or check your Mybatis configuration)");
		}
	}

	public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception
	{
		return sqlSessionFactoryBean(dataSource).getObject();
	}

	public MybatisSqlSessionFactoryBean sqlSessionFactoryBean(DataSource dataSource) throws Exception
	{
		MybatisSqlSessionFactoryBean factory = new MybatisSqlSessionFactoryBean();
		factory.setDataSource(dataSource);
		factory.setVfs(SpringBootVFS.class);
		OffsetLimitInterceptor offsetLimitInterceptor = new OffsetLimitInterceptor();
		{

			offsetLimitInterceptor.setDialect(flagwindProperties.getDialect());
			FlagwindProperties.Paginator paginator = flagwindProperties.getPaginator();
			if(paginator == null)
			{
				paginator = new FlagwindProperties.Paginator();
			}
			offsetLimitInterceptor.setAsyncTotalCount(paginator.isAsyncTotalCount());
			offsetLimitInterceptor.setPoolMaxSize(paginator.getPoolMaxSize());
		}

		factory.setPlugins(new Interceptor[]{offsetLimitInterceptor});
		if(StringUtils.hasText(this.properties.getConfigLocation()))
		{
			factory.setConfigLocation(this.resourceLoader.getResource(this.properties.getConfigLocation()));
		}
		Configuration configuration = this.properties.getConfiguration();
		if(configuration == null && !StringUtils.hasText(this.properties.getConfigLocation()))
		{
			configuration = new Configuration();
		}
		if(configuration != null && !CollectionUtils.isEmpty(this.configurationCustomizers))
		{
			for(ConfigurationCustomizer customizer : this.configurationCustomizers)
			{
				customizer.customize(configuration);
			}
		}
		//configuration.getTypeHandlerRegistry().register(CodeType.class, CodeTypeHandler.class);
		factory.setConfiguration(configuration);
		if(this.properties.getConfigurationProperties() != null)
		{
			factory.setConfigurationProperties(this.properties.getConfigurationProperties());
		}
		if(!ObjectUtils.isEmpty(this.interceptors))
		{
			factory.setPlugins(this.interceptors);
		}
		if(this.databaseIdProvider != null)
		{
			factory.setDatabaseIdProvider(this.databaseIdProvider);
		}
		if(StringUtils.hasLength(this.properties.getTypeAliasesPackage()))
		{
			factory.setTypeAliasesPackage(this.properties.getTypeAliasesPackage());
		}
		if(StringUtils.hasLength(this.properties.getTypeHandlersPackage()))
		{
			factory.setTypeHandlersPackage(this.properties.getTypeHandlersPackage());
		}
		if(!ObjectUtils.isEmpty(this.properties.resolveMapperLocations()))
		{
			factory.setMapperLocations(this.properties.resolveMapperLocations());
		}



		return factory;
	}

	public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
		ExecutorType executorType = this.properties.getExecutorType();
		if (executorType != null) {
			return new SqlSessionTemplate(sqlSessionFactory, executorType);
		} else {
			return new SqlSessionTemplate(sqlSessionFactory);
		}
	}

}
