package com.liferay.portal.spring.hibernate;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.MappedSuperclass;
import jakarta.transaction.TransactionManager;
import jakarta.transaction.TransactionSynchronizationRegistry;
import jakarta.transaction.UserTransaction;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import javax.sql.DataSource;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
import org.hibernate.cache.spi.RegionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.resource.jdbc.spi.PhysicalConnectionHandlingMode;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.InfrastructureProxy;
import org.springframework.core.SpringProperties;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.ClassFormatException;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.lang.Nullable;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

public class LocalSessionFactoryBuilder extends Configuration {
	private static final String RESOURCE_PATTERN = "/**/*.class";
	private static final String PACKAGE_INFO_SUFFIX = ".package-info";
	private static final TypeFilter[] DEFAULT_ENTITY_TYPE_FILTERS = new TypeFilter[]{new AnnotationTypeFilter(Entity.class, false), new AnnotationTypeFilter(Embeddable.class, false), new AnnotationTypeFilter(MappedSuperclass.class, false)};
	private static final TypeFilter CONVERTER_TYPE_FILTER = new AnnotationTypeFilter(Converter.class, false);
	private static final String IGNORE_CLASSFORMAT_PROPERTY_NAME = "spring.classformat.ignore";
	private static final boolean shouldIgnoreClassFormatException = SpringProperties.getFlag("spring.classformat.ignore");
	private final ResourcePatternResolver resourcePatternResolver;
	private TypeFilter[] entityTypeFilters;

	public LocalSessionFactoryBuilder(@Nullable DataSource dataSource) {
		this(dataSource, (ResourceLoader)(new PathMatchingResourcePatternResolver()));
	}

	public LocalSessionFactoryBuilder(@Nullable DataSource dataSource, ClassLoader classLoader) {
		this(dataSource, (ResourceLoader)(new PathMatchingResourcePatternResolver(classLoader)));
	}

	public LocalSessionFactoryBuilder(@Nullable DataSource dataSource, ResourceLoader resourceLoader) {
		this(dataSource, resourceLoader, new MetadataSources((new BootstrapServiceRegistryBuilder()).applyClassLoader(resourceLoader.getClassLoader()).build()));
	}

	public LocalSessionFactoryBuilder(@Nullable DataSource dataSource, ResourceLoader resourceLoader, MetadataSources metadataSources) {
		super(metadataSources);
		this.entityTypeFilters = DEFAULT_ENTITY_TYPE_FILTERS;
		this.getProperties().put("hibernate.current_session_context_class", SpringSessionContext.class.getName());
		if (dataSource != null) {
			this.getProperties().put("hibernate.connection.datasource", dataSource);
		}

		this.getProperties().put("hibernate.connection.handling_mode", PhysicalConnectionHandlingMode.DELAYED_ACQUISITION_AND_HOLD);
		this.getProperties().put("hibernate.classLoaders", Collections.singleton(resourceLoader.getClassLoader()));
		this.resourcePatternResolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
	}

	public LocalSessionFactoryBuilder setJtaTransactionManager(Object jtaTransactionManager) {
		Assert.notNull(jtaTransactionManager, "Transaction manager reference must not be null");
		if (jtaTransactionManager instanceof JtaTransactionManager springJtaTm) {
			boolean webspherePresent = ClassUtils.isPresent("com.ibm.wsspi.uow.UOWManager", this.getClass().getClassLoader());
			if (webspherePresent) {
				this.getProperties().put("hibernate.transaction.jta.platform", "org.hibernate.engine.transaction.jta.platform.internal.WebSphereExtendedJtaPlatform");
			} else {
				if (springJtaTm.getTransactionManager() == null) {
					throw new IllegalArgumentException("Can only apply JtaTransactionManager which has a TransactionManager reference set");
				}

				this.getProperties().put("hibernate.transaction.jta.platform", new ConfigurableJtaPlatform(springJtaTm.getTransactionManager(), springJtaTm.getUserTransaction(), springJtaTm.getTransactionSynchronizationRegistry()));
			}
		} else {
			if (!(jtaTransactionManager instanceof TransactionManager)) {
				throw new IllegalArgumentException("Unknown transaction manager type: " + jtaTransactionManager.getClass().getName());
			}

			TransactionManager jtaTm = (TransactionManager)jtaTransactionManager;
			this.getProperties().put("hibernate.transaction.jta.platform", new ConfigurableJtaPlatform(jtaTm, (UserTransaction)null, (TransactionSynchronizationRegistry)null));
		}

		this.getProperties().put("hibernate.transaction.coordinator_class", "jta");
		this.getProperties().put("hibernate.connection.handling_mode", PhysicalConnectionHandlingMode.DELAYED_ACQUISITION_AND_RELEASE_AFTER_STATEMENT);
		return this;
	}

	public LocalSessionFactoryBuilder setBeanContainer(ConfigurableListableBeanFactory beanFactory) {
		this.getProperties().put("hibernate.resource.beans.container", new SpringBeanContainer(beanFactory));
		return this;
	}

	public LocalSessionFactoryBuilder setCacheRegionFactory(RegionFactory cacheRegionFactory) {
		this.getProperties().put("hibernate.cache.region.factory_class", cacheRegionFactory);
		return this;
	}

	public LocalSessionFactoryBuilder setMultiTenantConnectionProvider(MultiTenantConnectionProvider multiTenantConnectionProvider) {
		this.getProperties().put("hibernate.multi_tenant_connection_provider", multiTenantConnectionProvider);
		return this;
	}

	public void setCurrentTenantIdentifierResolver(CurrentTenantIdentifierResolver currentTenantIdentifierResolver) {
		this.getProperties().put("hibernate.tenant_identifier_resolver", currentTenantIdentifierResolver);
		super.setCurrentTenantIdentifierResolver(currentTenantIdentifierResolver);
	}

	public LocalSessionFactoryBuilder setEntityTypeFilters(TypeFilter... entityTypeFilters) {
		this.entityTypeFilters = entityTypeFilters;
		return this;
	}

	public LocalSessionFactoryBuilder addAnnotatedClasses(Class<?>... annotatedClasses) {
		Class[] var2 = annotatedClasses;
		int var3 = annotatedClasses.length;

		for(int var4 = 0; var4 < var3; ++var4) {
			Class<?> annotatedClass = var2[var4];
			this.addAnnotatedClass(annotatedClass);
		}

		return this;
	}

	public LocalSessionFactoryBuilder addPackages(String... annotatedPackages) {
		String[] var2 = annotatedPackages;
		int var3 = annotatedPackages.length;

		for(int var4 = 0; var4 < var3; ++var4) {
			String annotatedPackage = var2[var4];
			this.addPackage(annotatedPackage);
		}

		return this;
	}

	public LocalSessionFactoryBuilder scanPackages(String... packagesToScan) throws HibernateException {
		Set<String> entityClassNames = new TreeSet();
		Set<String> converterClassNames = new TreeSet();
		Set<String> packageNames = new TreeSet();

		try {
			String[] var5 = packagesToScan;
			int var6 = packagesToScan.length;

			for(int var7 = 0; var7 < var6; ++var7) {
				String pkg = var5[var7];
				String pattern = "classpath*:" + ClassUtils.convertClassNameToResourcePath(pkg) + "/**/*.class";
				Resource[] resources = this.resourcePatternResolver.getResources(pattern);
				MetadataReaderFactory readerFactory = new CachingMetadataReaderFactory(this.resourcePatternResolver);
				Resource[] var12 = resources;
				int var13 = resources.length;

				for(int var14 = 0; var14 < var13; ++var14) {
					Resource resource = var12[var14];

					try {
						MetadataReader reader = readerFactory.getMetadataReader(resource);
						String className = reader.getClassMetadata().getClassName();
						if (this.matchesEntityTypeFilter(reader, readerFactory)) {
							entityClassNames.add(className);
						} else if (CONVERTER_TYPE_FILTER.match(reader, readerFactory)) {
							converterClassNames.add(className);
						} else if (className.endsWith(".package-info")) {
							packageNames.add(className.substring(0, className.length() - ".package-info".length()));
						}
					} catch (FileNotFoundException var19) {
					} catch (ClassFormatException var20) {
						if (!shouldIgnoreClassFormatException) {
							throw new MappingException("Incompatible class format in " + String.valueOf(resource), var20);
						}
					} catch (Throwable var21) {
						throw new MappingException("Failed to read candidate component class: " + String.valueOf(resource), var21);
					}
				}
			}
		} catch (IOException var22) {
			throw new MappingException("Failed to scan classpath for unlisted classes", var22);
		}

		try {
			ClassLoader cl = this.resourcePatternResolver.getClassLoader();
			Iterator var24 = entityClassNames.iterator();

			String packageName;
			while(var24.hasNext()) {
				packageName = (String)var24.next();
				this.addAnnotatedClass(ClassUtils.forName(packageName, cl));
			}

			var24 = converterClassNames.iterator();

			while(var24.hasNext()) {
				packageName = (String)var24.next();
				this.addAttributeConverter((Class<? extends AttributeConverter<?, ?>>)ClassUtils.forName(packageName, cl));
			}

			var24 = packageNames.iterator();

			while(var24.hasNext()) {
				packageName = (String)var24.next();
				this.addPackage(packageName);
			}

			return this;
		} catch (ClassNotFoundException var18) {
			throw new MappingException("Failed to load annotated classes from classpath", var18);
		}
	}

	private boolean matchesEntityTypeFilter(MetadataReader reader, MetadataReaderFactory readerFactory) throws IOException {
		TypeFilter[] var3 = this.entityTypeFilters;
		int var4 = var3.length;

		for(int var5 = 0; var5 < var4; ++var5) {
			TypeFilter filter = var3[var5];
			if (filter.match(reader, readerFactory)) {
				return true;
			}
		}

		return false;
	}

	public SessionFactory buildSessionFactory(AsyncTaskExecutor bootstrapExecutor) {
		Assert.notNull(bootstrapExecutor, "AsyncTaskExecutor must not be null");
		return (SessionFactory)Proxy.newProxyInstance(this.resourcePatternResolver.getClassLoader(), new Class[]{SessionFactoryImplementor.class, InfrastructureProxy.class}, new LocalSessionFactoryBuilder.BootstrapSessionFactoryInvocationHandler(bootstrapExecutor));
	}

	private class BootstrapSessionFactoryInvocationHandler implements InvocationHandler {
		private final Future<SessionFactory> sessionFactoryFuture;

		public BootstrapSessionFactoryInvocationHandler(AsyncTaskExecutor bootstrapExecutor) {
			this.sessionFactoryFuture = bootstrapExecutor.submit(
				(Callable<SessionFactory>)LocalSessionFactoryBuilder.this::buildSessionFactory);
		}

		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			Object var10000;
			switch (method.getName()) {
				case "equals":
					Boolean var11 = proxy == args[0];
					var10000 = var11;
					break;
				case "hashCode":
					Integer var10 = System.identityHashCode(proxy);
					var10000 = var10;
					break;
				case "getProperties":
					Properties var9 = LocalSessionFactoryBuilder.this.getProperties();
					var10000 = var9;
					break;
				case "getWrappedObject":
					SessionFactory var6 = this.getSessionFactory();
					var10000 = var6;
					break;
				default:
					Object var12;
					try {
						var12 = method.invoke(this.getSessionFactory(), args);
					} catch (InvocationTargetException var8) {
						throw var8.getTargetException();
					}

					var10000 = var12;
			}

			return var10000;
		}

		private SessionFactory getSessionFactory() {
			try {
				return (SessionFactory)this.sessionFactoryFuture.get();
			} catch (InterruptedException var4) {
				Thread.currentThread().interrupt();
				throw new IllegalStateException("Interrupted during initialization of Hibernate SessionFactory", var4);
			} catch (ExecutionException var5) {
				Throwable cause = var5.getCause();
				if (cause instanceof HibernateException hibernateException) {
					throw hibernateException;
				} else {
					throw new IllegalStateException("Failed to asynchronously initialize Hibernate SessionFactory: " + var5.getMessage(), cause);
				}
			}
		}
	}
}

