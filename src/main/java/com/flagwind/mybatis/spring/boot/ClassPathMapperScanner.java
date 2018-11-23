package com.flagwind.mybatis.spring.boot;

import com.flagwind.mybatis.common.Config;
import com.flagwind.mybatis.common.TemplateContext;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.env.Environment;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Properties;
import java.util.Set;

/**
 * A {@link ClassPathBeanDefinitionScanner} that registers Mappers by
 * {@code basePackage}, {@code annotationClass}, or {@code markerInterface}. If
 * an {@code annotationClass} and/or {@code markerInterface} is specified, only
 * the specified types will be searched (searching for all interfaces will be
 * disabled).
 * <p>
 * This functionality was previously a private class of
 * {@link MapperScannerConfigurer}, but was broken out in version 1.2.0.
 *
 * @author Hunter Presnall
 * @author Eduardo Macarron
 * @see MapperFactoryBean
 * @since 1.2.0
 */
public class ClassPathMapperScanner extends ClassPathBeanDefinitionScanner {

	private boolean addToConfig = true;

	private SqlSessionFactory sqlSessionFactory;

	private SqlSessionTemplate sqlSessionTemplate;

	private String sqlSessionTemplateBeanName;

	private String sqlSessionFactoryBeanName;

	private Class<? extends Annotation> annotationClass;

	private Class<?> markerInterface;

	public TemplateContext getTemplateContext()
	{
		return templateContext;
	}

	public void setTemplateContext(TemplateContext templateContext)
	{
		this.templateContext = templateContext;
	}

	private TemplateContext templateContext;

	private String templateContextBeanName;

	private MapperFactoryBean<?> mapperFactoryBean = new MapperFactoryBean<>();

	public ClassPathMapperScanner(BeanDefinitionRegistry registry) {
		super(registry, false);
	}



	/**
	 * Configures parent scanner to search for the right interfaces. It can search
	 * for all interfaces or just for those that extends a markerInterface or/and
	 * those annotated with the annotationClass
	 */
	public void registerFilters() {
		boolean acceptAllInterfaces = true;

		// if specified, use the given annotation and / or marker interface
		if (this.annotationClass != null) {
			addIncludeFilter(new AnnotationTypeFilter(this.annotationClass));
			acceptAllInterfaces = false;
		}

		// override AssignableTypeFilter to ignore matches on the actual marker interface
		if (this.markerInterface != null) {
			addIncludeFilter(new AssignableTypeFilter(this.markerInterface) {
				@Override
				protected boolean matchClassName(String className) {
					return false;
				}
			});
			acceptAllInterfaces = false;
		}

		if (acceptAllInterfaces) {
			// default include filter that accepts all classes
			addIncludeFilter((metadataReader, metadataReaderFactory) -> true);
		}

		// exclude package-info.java
		addExcludeFilter((metadataReader, metadataReaderFactory) ->
		{
			String className = metadataReader.getClassMetadata().getClassName();
			return className.endsWith("package-info");
		});
	}

	/**
	 * Calls the parent search that will search and register all the candidates.
	 * Then the registered objects are post processed to set them as
	 * MapperFactoryBeans
	 */
	@Override
	public Set<BeanDefinitionHolder> doScan(String... basePackages) {
		Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);

		if (beanDefinitions.isEmpty()) {
			logger.warn("No MyBatis mapper was found in '" + Arrays.toString(basePackages) + "' package. Please check your configuration.");
		} else {
			processBeanDefinitions(beanDefinitions);
		}

		return beanDefinitions;
	}

	private void processBeanDefinitions(Set<BeanDefinitionHolder> beanDefinitions) {
		GenericBeanDefinition definition;
		for (BeanDefinitionHolder holder : beanDefinitions) {
			definition = (GenericBeanDefinition) holder.getBeanDefinition();

			if (logger.isDebugEnabled()) {
				logger.debug("Creating MapperFactoryBean with name '" + holder.getBeanName()
						+ "' and '" + definition.getBeanClassName() + "' mapperInterface");
			}

			// the mapper interface is the original class of the bean
			// but, the actual class of the bean is MapperFactoryBean
			definition.getConstructorArgumentValues().addGenericArgumentValue(definition.getBeanClassName()); // issue #59
			definition.setBeanClass(this.mapperFactoryBean.getClass());
			//设置通用 Mapper
			if(StringUtils.hasText(this.templateContextBeanName)){
				definition.getPropertyValues().add("templateContext", new RuntimeBeanReference(this.templateContextBeanName));
			} else {
				//不做任何配置的时候使用默认方式
				if(this.templateContext == null){
					this.templateContext = new TemplateContext();
				}
				definition.getPropertyValues().add("templateContext", this.templateContext);
			}

			definition.getPropertyValues().add("addToConfig", this.addToConfig);

			boolean explicitFactoryUsed = false;
			if (StringUtils.hasText(this.sqlSessionFactoryBeanName)) {
				definition.getPropertyValues().add("sqlSessionFactory", new RuntimeBeanReference(this.sqlSessionFactoryBeanName));
				explicitFactoryUsed = true;
			} else if (this.sqlSessionFactory != null) {
				definition.getPropertyValues().add("sqlSessionFactory", this.sqlSessionFactory);
				explicitFactoryUsed = true;
			}

			if (StringUtils.hasText(this.sqlSessionTemplateBeanName)) {
				if (explicitFactoryUsed) {
					logger.warn("Cannot use both: sqlSessionTemplate and sqlSessionFactory together. sqlSessionFactory is ignored.");
				}
				definition.getPropertyValues().add("sqlSessionTemplate", new RuntimeBeanReference(this.sqlSessionTemplateBeanName));
				explicitFactoryUsed = true;
			} else if (this.sqlSessionTemplate != null) {
				if (explicitFactoryUsed) {
					logger.warn("Cannot use both: sqlSessionTemplate and sqlSessionFactory together. sqlSessionFactory is ignored.");
				}
				definition.getPropertyValues().add("sqlSessionTemplate", this.sqlSessionTemplate);
				explicitFactoryUsed = true;
			}

			if (!explicitFactoryUsed) {
				if (logger.isDebugEnabled()) {
					logger.debug("Enabling autowire by type for MapperFactoryBean with name '" + holder.getBeanName() + "'.");
				}
				definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
		return beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean checkCandidate(String beanName, BeanDefinition beanDefinition) {
		if (super.checkCandidate(beanName, beanDefinition)) {
			return true;
		} else {
			logger.warn("Skipping MapperFactoryBean with name '" + beanName
					+ "' and '" + beanDefinition.getBeanClassName() + "' mapperInterface"
					+ ". Bean already defined with the same name!");
			return false;
		}
	}


	public void setAddToConfig(boolean addToConfig) {
		this.addToConfig = addToConfig;
	}

	public void setAnnotationClass(Class<? extends Annotation> annotationClass) {
		this.annotationClass = annotationClass;
	}

	/**
	 * 配置通用 Mapper
	 *
	 * @param config
	 */
	public void setConfig(Config config) {
		if (templateContext == null) {
			templateContext = new TemplateContext();
		}
		templateContext.setConfig(config);
	}

	public void setMapperFactoryBean(MapperFactoryBean<?> mapperFactoryBean) {
		this.mapperFactoryBean = mapperFactoryBean != null ? mapperFactoryBean : new MapperFactoryBean<>();
	}

	public void setTemplateContextBeanName(String templateContextBeanName) {
		this.templateContextBeanName = templateContextBeanName;
	}

	/**
	 * 从环境变量中获取 mapper 配置信息
	 *
	 * @param environment
	 */
	public void setMapperProperties(String perfix,Environment environment)
	{
//		if(StringUtils.isEmpty(perfix)){
//			perfix = Config.PREFIX;
//		}
		Config config = SpringBootBindUtil.bind(environment, Config.class, perfix);
		if(config == null)
		{
			config = new Config();
		}
		if(templateContext == null)
		{
			templateContext = new TemplateContext();
		}
		templateContext.setConfig(config);
	}

	/**
	 * 从 properties 数组获取 mapper 配置信息
	 *
	 * @param properties
	 */
	public void setMapperProperties(String prefix,String[] properties) {
		if (templateContext == null) {
			templateContext = new TemplateContext();
		}
		Properties props = new Properties();
		for (String property : properties) {
			property = property.trim();
			int index = property.indexOf("=");
			if(index < 0){
				continue;
			}
			props.put(property.substring(0, index).trim(), property.substring(index + 1).trim());
		}
		templateContext.setProperties(prefix,props);
	}

	public void setMarkerInterface(Class<?> markerInterface) {
		this.markerInterface = markerInterface;
	}

	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
		this.sqlSessionFactory = sqlSessionFactory;
	}

	public void setSqlSessionFactoryBeanName(String sqlSessionFactoryBeanName) {
		this.sqlSessionFactoryBeanName = sqlSessionFactoryBeanName;
	}

	public void setSqlSessionTemplate(SqlSessionTemplate sqlSessionTemplate) {
		this.sqlSessionTemplate = sqlSessionTemplate;
	}

	public void setSqlSessionTemplateBeanName(String sqlSessionTemplateBeanName) {
		this.sqlSessionTemplateBeanName = sqlSessionTemplateBeanName;
	}
}
