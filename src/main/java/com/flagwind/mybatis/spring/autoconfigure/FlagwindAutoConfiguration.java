package com.flagwind.mybatis.spring.autoconfigure;


import com.flagwind.mybatis.common.Config;
import com.flagwind.mybatis.definition.interceptor.OffsetLimitInterceptor;
import com.flagwind.mybatis.spring.MybatisSqlSessionFactoryBean;
import com.flagwind.mybatis.spring.boot.ClassPathMapperScanner;
import com.flagwind.mybatis.spring.boot.FlagwindCacheDisabler;
import com.flagwind.persistent.Discovery;
import com.flagwind.persistent.DiscoveryFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;


import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

/**
 * {@link EnableAutoConfiguration Auto-Configuration} for Mybatis. Contributes a
 * {@link SqlSessionFactory} and a {@link SqlSessionTemplate}.
 *
 * If {@link org.mybatis.spring.annotation.MapperScan} is used, or a
 * configuration file is specified as a property, those will be considered,
 * otherwise this auto-configuration will attempt to register mappers based on
 * the interface definitions in or under the root auto-configuration package.
 *
 * @author Eddú Meléndez
 * @author Josh Long
 * @author Kazuki Shimizu
 * @author Eduardo Macarrón
 */
@org.springframework.context.annotation.Configuration
@ConditionalOnClass({SqlSessionFactory.class, SqlSessionFactoryBean.class})
@ConditionalOnBean(DataSource.class)
@EnableConfigurationProperties({MybatisProperties.class,FlagwindProperties.class})
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
@AutoConfigureBefore(name = "org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration")
public class FlagwindAutoConfiguration
{

	private static final Log logger = LogFactory.getLog(FlagwindAutoConfiguration.class);

	private final MybatisProperties properties;

	private final FlagwindProperties flagwindProperties;

	private final Interceptor[] interceptors;

	private final ResourceLoader resourceLoader;

	private final DatabaseIdProvider databaseIdProvider;

	private final List<ConfigurationCustomizer> configurationCustomizers;

	public FlagwindAutoConfiguration(MybatisProperties properties,
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

	@PostConstruct
	public void checkConfigFileExists() {
		if (this.properties.isCheckConfigLocation() && StringUtils.hasText(this.properties.getConfigLocation())) {
			Resource resource = this.resourceLoader.getResource(this.properties.getConfigLocation());
			Assert.state(resource.exists(), "Cannot find config location: " + resource
					+ " (please add config file or check your Mybatis configuration)");
		}
	}

	@Bean
	@ConditionalOnMissingBean
	public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception
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

		return factory.getObject();
	}

	@Bean
	@ConditionalOnMissingBean
	public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
		ExecutorType executorType = this.properties.getExecutorType();
		if (executorType != null) {
			return new SqlSessionTemplate(sqlSessionFactory, executorType);
		} else {
			return new SqlSessionTemplate(sqlSessionFactory);
		}
	}

	/**
	 * This will just scan the same base package as Spring Boot does. If you want
	 * more power, you can explicitly use
	 * {@link org.mybatis.spring.annotation.MapperScan} but this will get typed
	 * mappers working correctly, out-of-the-box, similar to using Spring Data JPA
	 * repositories.
	 */
	public static class AutoConfiguredMapperScannerRegistrar
			implements BeanFactoryAware, ImportBeanDefinitionRegistrar, ResourceLoaderAware, EnvironmentAware {

		private BeanFactory beanFactory;

		private ResourceLoader resourceLoader;

		private Environment environment;

		@Override
		public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

			logger.debug("Searching for mappers annotated with @Mapper");

			ClassPathMapperScanner scanner = new ClassPathMapperScanner(registry);
			scanner.setMapperProperties(Config.PREFIX,environment);
			try {
				if (this.resourceLoader != null) {
					scanner.setResourceLoader(this.resourceLoader);
				}

				List<String> packages = AutoConfigurationPackages.get(this.beanFactory);
				if (logger.isDebugEnabled()) {
					for (String pkg : packages) {
						logger.debug(String.format("Using auto-configuration base package '%s'", pkg));
					}
				}

				scanner.setAnnotationClass(Mapper.class);
				scanner.registerFilters();
				scanner.doScan(StringUtils.toStringArray(packages));
			} catch (IllegalStateException ex) {
				logger.debug("Could not determine auto-configuration package, automatic mapper scanning disabled.", ex);
			}
		}

		@Override
		public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
			this.beanFactory = beanFactory;
		}

		@Override
		public void setEnvironment(Environment environment) {
			this.environment = environment;
		}

		@Override
		public void setResourceLoader(ResourceLoader resourceLoader) {
			this.resourceLoader = resourceLoader;
		}
	}

	/**
	 * {@link org.mybatis.spring.annotation.MapperScan} ultimately ends up
	 * creating instances of {@link MapperFactoryBean}. If
	 * {@link org.mybatis.spring.annotation.MapperScan} is used then this
	 * auto-configuration is not needed. If it is _not_ used, however, then this
	 * will bring in a bean registrar and automatically register components based
	 * on the same component-scanning path as Spring Boot itself.
	 */
	@org.springframework.context.annotation.Configuration
	@Import({AutoConfiguredMapperScannerRegistrar.class})
	@ConditionalOnMissingBean(MapperFactoryBean.class)
	public static class MapperScannerRegistrarNotFoundConfiguration {

		@PostConstruct
		public void afterPropertiesSet() {
			logger.debug(String.format("No %s found.", MapperFactoryBean.class.getName()));
		}
	}

	/**
	 * Support Devtools Restart.
	 */
	@org.springframework.context.annotation.Configuration
	@ConditionalOnProperty(prefix = "spring.devtools.restart", name = "enabled", matchIfMissing = true)
	static class RestartConfiguration {

		@Bean
		public FlagwindCacheDisabler mapperCacheDisabler() {
			return new FlagwindCacheDisabler();
		}

	}
}
