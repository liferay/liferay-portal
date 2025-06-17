/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.spring.context;

import com.liferay.petra.executor.PortalExecutorManager;
import com.liferay.petra.lang.ClassLoaderPool;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.bean.BeanLocatorImpl;
import com.liferay.portal.dao.init.DBInitUtil;
import com.liferay.portal.db.partition.util.DBPartitionUtil;
import com.liferay.portal.deploy.hot.CustomJspBagRegistryUtil;
import com.liferay.portal.deploy.hot.ServiceWrapperRegistry;
import com.liferay.portal.events.StartupHelperUtil;
import com.liferay.portal.kernel.bean.PortalBeanLocatorUtil;
import com.liferay.portal.kernel.cache.thread.local.ThreadLocalCacheManager;
import com.liferay.portal.kernel.concurrent.SystemExecutorServiceUtil;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.deploy.hot.HotDeployUtil;
import com.liferay.portal.kernel.exception.LoggedExceptionInInitializerError;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.MessageBus;
import com.liferay.portal.kernel.module.util.ServiceLatch;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.scheduler.SchedulerEngineHelper;
import com.liferay.portal.kernel.service.ClassNameLocalServiceUtil;
import com.liferay.portal.kernel.servlet.DirectServletRegistryUtil;
import com.liferay.portal.kernel.servlet.ServletContextClassLoaderPool;
import com.liferay.portal.kernel.servlet.ServletContextPool;
import com.liferay.portal.kernel.util.ClearThreadLocalUtil;
import com.liferay.portal.kernel.util.ClearTimerThreadUtil;
import com.liferay.portal.kernel.util.InfrastructureUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ModuleFrameworkPropsValues;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.SystemProperties;
import com.liferay.portal.log4j.Log4JUtil;
import com.liferay.portal.module.framework.ModuleFrameworkUtil;
import com.liferay.portal.spring.aop.AopConfigurableApplicationContextConfigurator;
import com.liferay.portal.spring.aop.DynamicProxyCreator;
import com.liferay.portal.spring.configurator.ConfigurableApplicationContextConfigurator;
import com.liferay.portal.spring.hibernate.PortalHibernateConfiguration;
import com.liferay.portal.spring.override.OverrideBeanDefinitionRegistryPostProcessor;
import com.liferay.portal.spring.transaction.TransactionManagerFactory;
import com.liferay.portal.tools.DBUpgrader;
import com.liferay.portal.util.InitUtil;
import com.liferay.portal.util.PortalClassPathUtil;
import com.liferay.portal.util.PropsUtil;
import com.liferay.portal.util.PropsValues;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;

import java.beans.PropertyDescriptor;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;

import org.springframework.beans.CachedIntrospectionResults;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.datasource.DelegatingDataSource;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.ContextLoaderListener;

/**
 * @author Michael Young
 * @author Shuyang Zhou
 * @author Raymond Augé
 */
public class PortalContextLoaderListener extends ContextLoaderListener {

	public static String getPortalServletContextName() {
		return _portalServletContextName;
	}

	public static String getPortalServletContextPath() {
		return _portalServletContextPath;
	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		ApplicationContext applicationContext =
			ContextLoader.getCurrentWebApplicationContext();

		ModuleFrameworkUtil.unregisterContext(applicationContext);

		ThreadLocalCacheManager.destroy();

		if (_serviceWrapperRegistry != null) {
			_serviceWrapperRegistry.close();
		}

		try {
			DirectServletRegistryUtil.clearServlets();
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		try {
			HotDeployUtil.reset();
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		if (DBManagerUtil.getDBType() == DBType.HYPERSONIC) {
			try (Connection connection = DataAccess.getConnection();
				Statement statement = connection.createStatement()) {

				statement.executeUpdate("SHUTDOWN");
			}
			catch (Exception exception) {
				_log.error(exception);
			}
		}

		DataSource dataSource = (DataSource)PortalBeanLocatorUtil.locate(
			"liferayDataSource");

		super.contextDestroyed(servletContextEvent);

		SessionFactory sessionFactory =
			(SessionFactory)InfrastructureUtil.getSessionFactory();

		sessionFactory.close();

		closeDataSource(dataSource);

		_cleanUpJDBCDrivers();

		try {
			ModuleFrameworkUtil.stopFramework(
				PropsValues.MODULE_FRAMEWORK_STOP_WAIT_TIMEOUT);
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		ClassLoaderPool.unregister(_portalServletContextName);
		ServletContextClassLoaderPool.unregister(_portalServletContextName);

		try {
			ClearThreadLocalUtil.clearThreadLocal();
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		try {
			ClearTimerThreadUtil.clearTimerThread();
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		Log4JUtil.shutdownLog4J();

		try {
			SystemExecutorServiceUtil.shutdown();
		}
		catch (InterruptedException interruptedException) {
			_log.error(interruptedException);
		}
	}

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		try {
			_contextInitialized(servletContextEvent);
		}
		catch (Exception exception) {
			_log.error(exception);

			throw new RuntimeException(exception);
		}
	}

	protected void clearFilteredPropertyDescriptorsCache(
		AutowireCapableBeanFactory autowireCapableBeanFactory) {

		try {
			Map<Class<?>, PropertyDescriptor[]>
				filteredPropertyDescriptorsCache =
					(Map<Class<?>, PropertyDescriptor[]>)
						_FILTERED_PROPERTY_DESCRIPTORS_CACHE_FIELD.get(
							autowireCapableBeanFactory);

			filteredPropertyDescriptorsCache.clear();
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	protected void closeDataSource(DataSource dataSource) {
		if (dataSource instanceof DelegatingDataSource) {
			DelegatingDataSource delegatingDataSource =
				(DelegatingDataSource)dataSource;

			dataSource = delegatingDataSource.getTargetDataSource();
		}

		if (dataSource instanceof Closeable) {
			try {
				Closeable closeable = (Closeable)dataSource;

				closeable.close();
			}
			catch (IOException ioException) {
				_log.error(ioException);
			}
		}
	}

	@Override
	protected void customizeContext(
		ServletContext servletContext,
		ConfigurableWebApplicationContext configurableWebApplicationContext) {

		ConfigurableApplicationContextConfigurator
			configurableApplicationContextConfigurator =
				new AopConfigurableApplicationContextConfigurator();

		configurableApplicationContextConfigurator.configure(
			configurableWebApplicationContext);

		Properties properties = PropsUtil.getProperties("spring.bean.", true);

		if (!properties.isEmpty()) {
			configurableWebApplicationContext.addBeanFactoryPostProcessor(
				new OverrideBeanDefinitionRegistryPostProcessor(properties));
		}
	}

	private void _cleanUpJDBCDrivers() {
		Enumeration<Driver> enumeration = DriverManager.getDrivers();

		while (enumeration.hasMoreElements()) {
			Driver driver = enumeration.nextElement();

			Class<?> driverClass = driver.getClass();

			if (PortalClassLoaderUtil.isPortalClassLoader(
					driverClass.getClassLoader())) {

				try {
					DriverManager.deregisterDriver(driver);
				}
				catch (SQLException sqlException) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							"Unable to deregister driver " + driver,
							sqlException);
					}
				}
			}
		}

		DBType dbType = DBManagerUtil.getDBType();

		if (dbType == DBType.MYSQL) {
			try {
				Class<?> clazz = Class.forName(
					"com.mysql.cj.jdbc.AbandonedConnectionCleanupThread");

				Method method = clazz.getMethod("checkedShutdown");

				method.invoke(null);
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn("Unable to cleanly shut down MySQL", exception);
				}
			}
		}
	}

	private void _contextInitialized(ServletContextEvent servletContextEvent)
		throws Exception {

		Class.forName(SystemProperties.class.getName());

		ServletContext servletContext = servletContextEvent.getServletContext();

		PortalClassPathUtil.initializeClassPaths(servletContext);

		InitUtil.init();

		// Log JVM arguments after Log4j is initialized

		_logJVMArguments();

		_portalServletContextName = servletContext.getServletContextName();

		if (_portalServletContextName == null) {
			_portalServletContextName = StringPool.BLANK;
		}

		_portalServletContextPath = servletContext.getContextPath();

		File tempDir = (File)servletContext.getAttribute(
			JavaConstants.JAKARTA_SERVLET_CONTEXT_TEMPDIR);

		PropsValues.LIFERAY_WEB_PORTAL_CONTEXT_TEMPDIR =
			tempDir.getAbsolutePath();

		Path tempDirPath = Paths.get(System.getProperty("java.io.tmpdir"));

		if (!Files.exists(tempDirPath)) {
			try {
				Files.createDirectories(tempDirPath);
			}
			catch (IOException ioException) {
				_log.error("Unable to create " + tempDirPath, ioException);
			}
		}

		ModuleFrameworkUtil.createFramework();

		ExecutorService executorService =
			SystemExecutorServiceUtil.getExecutorService();

		Future<Future<?>> future1 = executorService.submit(
			() -> {
				DBInitUtil.init();

				DataSource dataSource = DBInitUtil.getDataSource();

				InfrastructureUtil.setDataSource(dataSource);

				return executorService.submit(
					() -> {
						PortalHibernateConfiguration
							portalHibernateConfiguration =
								new PortalHibernateConfiguration();

						portalHibernateConfiguration.setDataSource(dataSource);

						portalHibernateConfiguration.afterPropertiesSet();

						SessionFactory sessionFactory =
							portalHibernateConfiguration.getObject();

						InfrastructureUtil.setSessionFactory(sessionFactory);

						InfrastructureUtil.setTransactionManager(
							TransactionManagerFactory.createTransactionManager(
								dataSource, sessionFactory));

						return null;
					});
			});

		ModuleFrameworkUtil.initFramework();

		Future<?> future2 = future1.get();

		future2.get();

		ClassLoader portalClassLoader = PortalClassLoaderUtil.getClassLoader();

		ClassLoaderPool.register(_portalServletContextName, portalClassLoader);
		ServletContextClassLoaderPool.register(
			_portalServletContextName, portalClassLoader);

		ServiceLatch serviceLatch = SystemBundleUtil.newServiceLatch();

		serviceLatch.waitFor(MessageBus.class);
		serviceLatch.waitFor(PortalExecutorManager.class);
		serviceLatch.waitFor(SchedulerEngineHelper.class);

		serviceLatch.openOn(
			() -> _serviceWrapperRegistry = new ServiceWrapperRegistry());

		FutureTask<Void> springInitTask = null;

		if (ModuleFrameworkPropsValues.
				MODULE_FRAMEWORK_CONCURRENT_STARTUP_ENABLED) {

			springInitTask = new FutureTask<>(
				() -> {
					super.contextInitialized(servletContextEvent);

					return null;
				});

			executorService.submit(
				SystemExecutorServiceUtil.renameThread(
					springInitTask, "Portal Spring Init Thread"));
		}

		ModuleFrameworkUtil.startFramework();

		if (springInitTask == null) {
			super.contextInitialized(servletContextEvent);
		}
		else {
			springInitTask.get();
		}

		ServletContextPool.put(_portalServletContextName, servletContext);

		ApplicationContext applicationContext =
			ContextLoader.getCurrentWebApplicationContext();

		BeanLocatorImpl beanLocatorImpl = new BeanLocatorImpl(
			portalClassLoader, applicationContext);

		PortalBeanLocatorUtil.setBeanLocator(beanLocatorImpl);

		ClassLoader classLoader = portalClassLoader;

		while (classLoader != null) {
			CachedIntrospectionResults.clearClassLoader(classLoader);

			classLoader = classLoader.getParent();
		}

		clearFilteredPropertyDescriptorsCache(
			applicationContext.getAutowireCapableBeanFactory());

		DynamicProxyCreator dynamicProxyCreator =
			DynamicProxyCreator.getDynamicProxyCreator();

		dynamicProxyCreator.clear();

		boolean upgradeDatabaseAutoRun =
			DBUpgrader.isUpgradeDatabaseAutoRunEnabled();

		if (upgradeDatabaseAutoRun) {
			StartupHelperUtil.setUpgrading(true);

			try {
				DBUpgrader.upgradePortal();
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		ModuleFrameworkUtil.registerContext(applicationContext);

		CustomJspBagRegistryUtil.getCustomJspBags();

		if (!upgradeDatabaseAutoRun) {

			// Check class names

			if (_log.isDebugEnabled()) {
				_log.debug("Check class names");
			}

			try {
				DBPartitionUtil.forEachCompanyId(
					companyId -> ClassNameLocalServiceUtil.checkClassNames());
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}
	}

	private void _logJVMArguments() {
		if (!_log.isInfoEnabled()) {
			return;
		}

		RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();

		List<String> inputArguments = runtimeMXBean.getInputArguments();

		StringBundler sb = new StringBundler(inputArguments.size() * 2);

		sb.append("JVM arguments: ");

		for (String inputArgument : inputArguments) {
			sb.append(inputArgument);
			sb.append(StringPool.SPACE);
		}

		if (!inputArguments.isEmpty()) {
			sb.setIndex(sb.index() - 1);
		}

		_log.info(sb.toString());
	}

	private static final Field _FILTERED_PROPERTY_DESCRIPTORS_CACHE_FIELD;

	private static final Log _log = LogFactoryUtil.getLog(
		PortalContextLoaderListener.class);

	private static String _portalServletContextName = StringPool.BLANK;
	private static String _portalServletContextPath = StringPool.SLASH;

	static {
		try {
			_FILTERED_PROPERTY_DESCRIPTORS_CACHE_FIELD =
				ReflectionUtil.getDeclaredField(
					AbstractAutowireCapableBeanFactory.class,
					"filteredPropertyDescriptorsCache");
		}
		catch (Exception exception) {
			throw new LoggedExceptionInInitializerError(exception);
		}
	}

	private ServiceWrapperRegistry _serviceWrapperRegistry;

}