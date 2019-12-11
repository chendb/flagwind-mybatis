package com.flagwind.mybatis.spring.autoconfigure;

import com.flagwind.mybatis.common.Config;
import com.flagwind.mybatis.spring.boot.ClassPathMapperScanner;
import com.flagwind.mybatis.spring.boot.FlagwindCacheDisabler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
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
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.List;

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
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
@Import({ DiscoveryAutoConfiguration.class })
@AutoConfigureBefore(name = "org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration")
public class FlagwindAutoConfiguration /*extends AbstractAutoConfiguration*/
{

	private static final Log LOG = LogFactory.getLog(FlagwindAutoConfiguration.class);

	/*
	public FlagwindAutoConfiguration(MybatisProperties properties, FlagwindProperties flagwindProperties, ObjectProvider<Interceptor[]> interceptorsProvider, ResourceLoader resourceLoader, ObjectProvider<DatabaseIdProvider> databaseIdProvider, ObjectProvider<List<ConfigurationCustomizer>> configurationCustomizersProvider)
	{

		super(properties, flagwindProperties, interceptorsProvider, resourceLoader, databaseIdProvider, configurationCustomizersProvider);
	}

	@PostConstruct
	public void checkConfigFileExists()
	{
		super.checkConfigFileExists();
	}

	@Bean
	@ConditionalOnMissingBean
	public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception
	{
		return super.sqlSessionFactory(dataSource);
	}

	@Bean
	@ConditionalOnMissingBean
	public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory)
	{
		return super.sqlSessionTemplate(sqlSessionFactory);
	}
	*/


	@Configuration
	@ConditionalOnMissingBean(AbstractAutoConfiguration.class)
	@ConditionalOnClass({ SqlSessionFactory.class, SqlSessionFactoryBean.class })
	@ConditionalOnBean(DataSource.class)
	@EnableConfigurationProperties({ MybatisProperties.class, FlagwindProperties.class })
	public static class DatabaseAutoConfiguration extends AbstractAutoConfiguration
	{
		public DatabaseAutoConfiguration(MybatisProperties properties, FlagwindProperties flagwindProperties, ObjectProvider<Interceptor[]> interceptorsProvider, ResourceLoader resourceLoader, ObjectProvider<DatabaseIdProvider> databaseIdProvider, ObjectProvider<List<ConfigurationCustomizer>> configurationCustomizersProvider)
		{
			super(properties, flagwindProperties, interceptorsProvider, resourceLoader, databaseIdProvider, configurationCustomizersProvider);
		}

		@PostConstruct
		public void checkConfigFileExists()
		{
			super.checkConfigFileExists();
		}

		@Bean
		@ConditionalOnMissingBean
		public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception
		{
			return super.sqlSessionFactory(dataSource);
		}

		@Bean
		@ConditionalOnMissingBean
		public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory)
		{
			return super.sqlSessionTemplate(sqlSessionFactory);
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
		public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata,
				BeanDefinitionRegistry registry) {

			LOG.debug("Searching for mappers annotated with @Mapper");

			ClassPathMapperScanner scanner = new ClassPathMapperScanner(registry);
			scanner.setMapperProperties(Config.PREFIX, environment);
			try {
				if (this.resourceLoader != null) {
					scanner.setResourceLoader(this.resourceLoader);
				}

				List<String> packages = AutoConfigurationPackages.get(this.beanFactory);
				if (LOG.isDebugEnabled()) {
					for (String pkg : packages) {
						LOG.debug(String.format("Using auto-configuration base package '%s'", pkg));
					}
				}

				scanner.setAnnotationClass(Mapper.class);
				scanner.registerFilters();
				scanner.doScan(StringUtils.toStringArray(packages));
			} catch (IllegalStateException ex) {
				LOG.debug("Could not determine auto-configuration package, automatic mapper scanning disabled.", ex);
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
	 * {@link org.mybatis.spring.annotation.MapperScan} ultimately ends up creating
	 * instances of {@link MapperFactoryBean}. If
	 * {@link org.mybatis.spring.annotation.MapperScan} is used then this
	 * auto-configuration is not needed. If it is _not_ used, however, then this
	 * will bring in a bean registrar and automatically register components based on
	 * the same component-scanning path as Spring Boot itself.
	 */
	@org.springframework.context.annotation.Configuration
	@Import({ AutoConfiguredMapperScannerRegistrar.class })
	@ConditionalOnMissingBean(MapperFactoryBean.class)
	public static class MapperScannerRegistrarNotFoundConfiguration {

		@PostConstruct
		public void afterPropertiesSet() {
			LOG.debug(String.format("No %s found.", MapperFactoryBean.class.getName()));
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
