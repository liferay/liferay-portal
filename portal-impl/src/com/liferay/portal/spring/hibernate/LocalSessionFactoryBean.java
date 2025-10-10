/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.spring.hibernate;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import javax.sql.DataSource;
import org.hibernate.Interceptor;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.model.naming.ImplicitNamingStrategy;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
import org.hibernate.cache.spi.RegionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.hibernate.integrator.spi.Integrator;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.InfrastructureProxy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.lang.Nullable;

public class LocalSessionFactoryBean extends HibernateExceptionTranslator
	implements FactoryBean<SessionFactory>, ResourceLoaderAware, BeanFactoryAware, InitializingBean, SmartInitializingSingleton, DisposableBean {
	@Nullable
	private DataSource dataSource;
	@Nullable
	private Resource[] configLocations;
	@Nullable
	private String[] mappingResources;
	@Nullable
	private Resource[] mappingLocations;
	@Nullable
	private Resource[] cacheableMappingLocations;
	@Nullable
	private Resource[] mappingJarLocations;
	@Nullable
	private Resource[] mappingDirectoryLocations;
	@Nullable
	private Interceptor entityInterceptor;
	@Nullable
	private ImplicitNamingStrategy implicitNamingStrategy;
	@Nullable
	private PhysicalNamingStrategy physicalNamingStrategy;
	@Nullable
	private Object jtaTransactionManager;
	@Nullable
	private RegionFactory cacheRegionFactory;
	@Nullable
	private MultiTenantConnectionProvider multiTenantConnectionProvider;
	@Nullable
	private CurrentTenantIdentifierResolver currentTenantIdentifierResolver;
	@Nullable
	private Properties hibernateProperties;
	@Nullable
	private TypeFilter[] entityTypeFilters;
	@Nullable
	private Class<?>[] annotatedClasses;
	@Nullable
	private String[] annotatedPackages;
	@Nullable
	private String[] packagesToScan;
	@Nullable
	private AsyncTaskExecutor bootstrapExecutor;
	@Nullable
	private Integrator[] hibernateIntegrators;
	private boolean metadataSourcesAccessed = false;
	@Nullable
	private MetadataSources metadataSources;
	@Nullable
	private ResourcePatternResolver resourcePatternResolver;
	@Nullable
	private ConfigurableListableBeanFactory beanFactory;
	@Nullable
	private Configuration configuration;
	@Nullable
	private SessionFactory sessionFactory;

	public LocalSessionFactoryBean() {
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setConfigLocation(Resource configLocation) {
		this.configLocations = new Resource[]{configLocation};
	}

	public void setConfigLocations(Resource... configLocations) {
		this.configLocations = configLocations;
	}

	public void setMappingResources(String... mappingResources) {
		this.mappingResources = mappingResources;
	}

	public void setMappingLocations(Resource... mappingLocations) {
		this.mappingLocations = mappingLocations;
	}

	public void setCacheableMappingLocations(Resource... cacheableMappingLocations) {
		this.cacheableMappingLocations = cacheableMappingLocations;
	}

	public void setMappingJarLocations(Resource... mappingJarLocations) {
		this.mappingJarLocations = mappingJarLocations;
	}

	public void setMappingDirectoryLocations(Resource... mappingDirectoryLocations) {
		this.mappingDirectoryLocations = mappingDirectoryLocations;
	}

	public void setEntityInterceptor(Interceptor entityInterceptor) {
		this.entityInterceptor = entityInterceptor;
	}

	public void setImplicitNamingStrategy(ImplicitNamingStrategy implicitNamingStrategy) {
		this.implicitNamingStrategy = implicitNamingStrategy;
	}

	public void setPhysicalNamingStrategy(PhysicalNamingStrategy physicalNamingStrategy) {
		this.physicalNamingStrategy = physicalNamingStrategy;
	}

	public void setJtaTransactionManager(Object jtaTransactionManager) {
		this.jtaTransactionManager = jtaTransactionManager;
	}

	public void setCacheRegionFactory(RegionFactory cacheRegionFactory) {
		this.cacheRegionFactory = cacheRegionFactory;
	}

	public void setMultiTenantConnectionProvider(MultiTenantConnectionProvider multiTenantConnectionProvider) {
		this.multiTenantConnectionProvider = multiTenantConnectionProvider;
	}

	public void setCurrentTenantIdentifierResolver(CurrentTenantIdentifierResolver currentTenantIdentifierResolver) {
		this.currentTenantIdentifierResolver = currentTenantIdentifierResolver;
	}

	public void setHibernateProperties(Properties hibernateProperties) {
		this.hibernateProperties = hibernateProperties;
	}

	public Properties getHibernateProperties() {
		if (this.hibernateProperties == null) {
			this.hibernateProperties = new Properties();
		}

		return this.hibernateProperties;
	}

	public void setEntityTypeFilters(TypeFilter... entityTypeFilters) {
		this.entityTypeFilters = entityTypeFilters;
	}

	public void setAnnotatedClasses(Class<?>... annotatedClasses) {
		this.annotatedClasses = annotatedClasses;
	}

	public void setAnnotatedPackages(String... annotatedPackages) {
		this.annotatedPackages = annotatedPackages;
	}

	public void setPackagesToScan(String... packagesToScan) {
		this.packagesToScan = packagesToScan;
	}

	public void setBootstrapExecutor(AsyncTaskExecutor bootstrapExecutor) {
		this.bootstrapExecutor = bootstrapExecutor;
	}

	public void setHibernateIntegrators(Integrator... hibernateIntegrators) {
		this.hibernateIntegrators = hibernateIntegrators;
	}

	public void setMetadataSources(MetadataSources metadataSources) {
		this.metadataSourcesAccessed = true;
		this.metadataSources = metadataSources;
	}

	public MetadataSources getMetadataSources() {
		this.metadataSourcesAccessed = true;
		if (this.metadataSources == null) {
			BootstrapServiceRegistryBuilder builder = new BootstrapServiceRegistryBuilder();
			if (this.resourcePatternResolver != null) {
				builder = builder.applyClassLoader(this.resourcePatternResolver.getClassLoader());
			}

			if (this.hibernateIntegrators != null) {
				Integrator[] var2 = this.hibernateIntegrators;
				int var3 = var2.length;

				for(int var4 = 0; var4 < var3; ++var4) {
					Integrator integrator = var2[var4];
					builder = builder.applyIntegrator(integrator);
				}
			}

			this.metadataSources = new MetadataSources(builder.build());
		}

		return this.metadataSources;
	}

	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourcePatternResolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
	}

	public ResourceLoader getResourceLoader() {
		if (this.resourcePatternResolver == null) {
			this.resourcePatternResolver = new PathMatchingResourcePatternResolver();
		}

		return this.resourcePatternResolver;
	}

	public void setBeanFactory(BeanFactory beanFactory) {
		if (beanFactory instanceof ConfigurableListableBeanFactory clbf) {
			this.beanFactory = clbf;
		}

	}

	public void afterPropertiesSet() throws IOException {
		if (this.metadataSources != null && !this.metadataSourcesAccessed) {
			this.metadataSources = null;
		}

		LocalSessionFactoryBuilder
			sfb = new LocalSessionFactoryBuilder(this.dataSource, this.getResourceLoader(), this.getMetadataSources());
		Resource[] var2;
		int var3;
		int var4;
		Resource resource;
		if (this.configLocations != null) {
			var2 = this.configLocations;
			var3 = var2.length;

			for(var4 = 0; var4 < var3; ++var4) {
				resource = var2[var4];
				sfb.configure(resource.getURL());
			}
		}

		if (this.mappingResources != null) {
			String[] var7 = this.mappingResources;
			var3 = var7.length;

			for(var4 = 0; var4 < var3; ++var4) {
				String mapping = var7[var4];
				Resource mr = new ClassPathResource(mapping.trim(), this.getResourceLoader().getClassLoader());
				sfb.addInputStream(mr.getInputStream());
			}
		}

		if (this.mappingLocations != null) {
			var2 = this.mappingLocations;
			var3 = var2.length;

			for(var4 = 0; var4 < var3; ++var4) {
				resource = var2[var4];
				sfb.addInputStream(resource.getInputStream());
			}
		}

		if (this.cacheableMappingLocations != null) {
			var2 = this.cacheableMappingLocations;
			var3 = var2.length;

			for(var4 = 0; var4 < var3; ++var4) {
				resource = var2[var4];
				sfb.addCacheableFile(resource.getFile());
			}
		}

		if (this.mappingJarLocations != null) {
			var2 = this.mappingJarLocations;
			var3 = var2.length;

			for(var4 = 0; var4 < var3; ++var4) {
				resource = var2[var4];
				sfb.addJar(resource.getFile());
			}
		}

		if (this.mappingDirectoryLocations != null) {
			var2 = this.mappingDirectoryLocations;
			var3 = var2.length;

			for(var4 = 0; var4 < var3; ++var4) {
				resource = var2[var4];
				File file = resource.getFile();
				if (!file.isDirectory()) {
					throw new IllegalArgumentException("Mapping directory location [" + String.valueOf(resource) + "] does not denote a directory");
				}

				sfb.addDirectory(file);
			}
		}

		if (this.entityInterceptor != null) {
			sfb.setInterceptor(this.entityInterceptor);
		}

		if (this.implicitNamingStrategy != null) {
			sfb.setImplicitNamingStrategy(this.implicitNamingStrategy);
		}

		if (this.physicalNamingStrategy != null) {
			sfb.setPhysicalNamingStrategy(this.physicalNamingStrategy);
		}

		if (this.jtaTransactionManager != null) {
			sfb.setJtaTransactionManager(this.jtaTransactionManager);
		}

		if (this.beanFactory != null) {
			sfb.setBeanContainer(this.beanFactory);
		}

		if (this.cacheRegionFactory != null) {
			sfb.setCacheRegionFactory(this.cacheRegionFactory);
		}

		if (this.multiTenantConnectionProvider != null) {
			sfb.setMultiTenantConnectionProvider(this.multiTenantConnectionProvider);
		}

		if (this.currentTenantIdentifierResolver != null) {
			sfb.setCurrentTenantIdentifierResolver(this.currentTenantIdentifierResolver);
		}

		if (this.hibernateProperties != null) {
			sfb.addProperties(this.hibernateProperties);
		}

		if (this.entityTypeFilters != null) {
			sfb.setEntityTypeFilters(this.entityTypeFilters);
		}

		if (this.annotatedClasses != null) {
			sfb.addAnnotatedClasses(this.annotatedClasses);
		}

		if (this.annotatedPackages != null) {
			sfb.addPackages(this.annotatedPackages);
		}

		if (this.packagesToScan != null) {
			sfb.scanPackages(this.packagesToScan);
		}

		this.configuration = sfb;
		this.sessionFactory = this.buildSessionFactory(sfb);
	}

	public void afterSingletonsInstantiated() {
		SessionFactory var2 = this.sessionFactory;
		if (var2 instanceof InfrastructureProxy proxy) {
			proxy.getWrappedObject();
		}

	}

	protected SessionFactory buildSessionFactory(LocalSessionFactoryBuilder sfb) {
		return this.bootstrapExecutor != null ? sfb.buildSessionFactory(this.bootstrapExecutor) : sfb.buildSessionFactory();
	}

	public final Configuration getConfiguration() {
		if (this.configuration == null) {
			throw new IllegalStateException("Configuration not initialized yet");
		} else {
			return this.configuration;
		}
	}

	@Nullable
	public SessionFactory getObject() {
		return this.sessionFactory;
	}

	public Class<?> getObjectType() {
		return this.sessionFactory != null ? this.sessionFactory.getClass() : SessionFactory.class;
	}

	public boolean isSingleton() {
		return true;
	}

	public void destroy() {
		if (this.sessionFactory != null) {
			this.sessionFactory.close();
		}

	}
}

