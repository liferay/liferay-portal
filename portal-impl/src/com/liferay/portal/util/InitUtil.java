/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.util;

import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.bean.BeanLocatorImpl;
import com.liferay.portal.dao.db.DBManagerImpl;
import com.liferay.portal.dao.init.DBInitUtil;
import com.liferay.portal.dao.jdbc.DataSourceFactoryImpl;
import com.liferay.portal.kernel.bean.BeanLocator;
import com.liferay.portal.kernel.bean.PortalBeanLocatorUtil;
import com.liferay.portal.kernel.configuration.ConfigurationFactoryUtil;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.jdbc.DataSourceFactoryUtil;
import com.liferay.portal.kernel.internal.configuration.ConfigurationFactoryImpl;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.log.SanitizerLogWrapper;
import com.liferay.portal.kernel.log4j.Log4JUtil;
import com.liferay.portal.kernel.security.xml.SecureXMLFactoryProviderUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.InfrastructureUtil;
import com.liferay.portal.kernel.util.JavaDetector;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.OSDetector;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.SystemProperties;
import com.liferay.portal.kernel.util.TimeZoneUtil;
import com.liferay.portal.kernel.xml.UnsecureSAXReaderUtil;
import com.liferay.portal.module.framework.ModuleFrameworkUtil;
import com.liferay.portal.security.xml.SecureXMLFactoryProviderImpl;
import com.liferay.portal.spring.aop.AopConfigurableApplicationContextConfigurator;
import com.liferay.portal.spring.bean.LiferayBeanFactory;
import com.liferay.portal.spring.configurator.ConfigurableApplicationContextConfigurator;
import com.liferay.portal.spring.hibernate.PortalHibernateConfiguration;
import com.liferay.portal.spring.transaction.TransactionManagerFactory;
import com.liferay.portal.xml.SAXReaderImpl;

import java.io.IOException;
import java.io.InputStream;

import java.lang.reflect.Field;

import java.util.List;
import java.util.logging.LogManager;
import java.util.zip.ZipFile;

import javax.sql.DataSource;

import org.apache.commons.lang.time.StopWatch;

import org.hibernate.SessionFactory;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author Brian Wing Shun Chan
 */
public class InitUtil {

	public static synchronized void init() {
		if (_initialized) {
			return;
		}

		try {
			if (!OSDetector.isWindows() && JavaDetector.isJDK8()) {
				Field field = ReflectionUtil.getDeclaredField(
					ZipFile.class, "usemmap");

				if ((boolean)field.get(null)) {
					field.setBoolean(null, false);
				}
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		StopWatch stopWatch = new StopWatch();

		stopWatch.start();

		// Set the default locale used by Liferay. This locale is no longer set
		// at the VM level. See LEP-2584.

		String userLanguage = SystemProperties.get("user.language");
		String userCountry = SystemProperties.get("user.country");
		String userVariant = SystemProperties.get("user.variant");

		LocaleUtil.setDefault(userLanguage, userCountry, userVariant);

		// Set the default time zone used by Liferay. This time zone is no
		// longer set at the VM level. See LEP-2584.

		String userTimeZone = SystemProperties.get("user.timezone");

		TimeZoneUtil.setDefault(userTimeZone);

		// Shared class loader

		Thread currentThread = Thread.currentThread();

		ClassLoader classLoader = currentThread.getContextClassLoader();

		try {
			PortalClassLoaderUtil.setClassLoader(classLoader);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		// JDK logger

		try (InputStream inputStream = InitUtil.class.getResourceAsStream(
				"/logging.properties")) {

			if (inputStream != null) {
				LogManager logManager = LogManager.getLogManager();

				logManager.readConfiguration(inputStream);
			}
		}
		catch (IOException ioException) {
			if (_log.isWarnEnabled()) {
				_log.warn(ioException);
			}
		}

		// Log4J

		if (GetterUtil.getBoolean(
				SystemProperties.get("log4j.configure.on.startup"), true)) {

			Log4JUtil.configureLog4J(InitUtil.class.getClassLoader());
		}

		// Log sanitizer

		SanitizerLogWrapper.init();

		// Configuration factory

		ConfigurationFactoryUtil.setConfigurationFactory(
			new ConfigurationFactoryImpl());

		// Data source factory

		DataSourceFactoryUtil.setDataSourceFactory(new DataSourceFactoryImpl());

		// DB manager

		DBManagerUtil.setDBManager(new DBManagerImpl());

		// File

		FileUtil fileUtil = new FileUtil();

		fileUtil.setFile(new FileImpl());

		// XML

		SecureXMLFactoryProviderUtil secureXMLFactoryProviderUtil =
			new SecureXMLFactoryProviderUtil();

		secureXMLFactoryProviderUtil.setSecureXMLFactoryProvider(
			new SecureXMLFactoryProviderImpl());

		UnsecureSAXReaderUtil unsecureSAXReaderUtil =
			new UnsecureSAXReaderUtil();

		unsecureSAXReaderUtil.setSAXReader(new SAXReaderImpl());

		if (_PRINT_TIME) {
			System.out.println(
				"InitAction takes " + stopWatch.getTime() + " ms");
		}

		_initialized = true;
	}

	public static synchronized void initWithSpring(
		List<String> configLocations, boolean initModuleFramework,
		boolean registerContext, Runnable initFrameworkCallbackRunnable) {

		if (_initialized) {
			return;
		}

		init();

		try {
			if (initModuleFramework) {
				PropsValues.LIFERAY_WEB_PORTAL_CONTEXT_TEMPDIR =
					System.getProperty(SystemProperties.TMP_DIR);

				ModuleFrameworkUtil.createFramework();

				ModuleFrameworkUtil.initFramework();

				if (initFrameworkCallbackRunnable != null) {
					initFrameworkCallbackRunnable.run();
				}
			}

			DBInitUtil.init();

			DataSource dataSource = DBInitUtil.getDataSource();

			InfrastructureUtil.setDataSource(dataSource);

			PortalHibernateConfiguration portalHibernateConfiguration =
				new PortalHibernateConfiguration();

			portalHibernateConfiguration.setDataSource(dataSource);

			portalHibernateConfiguration.afterPropertiesSet();

			SessionFactory sessionFactory =
				portalHibernateConfiguration.getObject();

			InfrastructureUtil.setSessionFactory(sessionFactory);

			InfrastructureUtil.setTransactionManager(
				TransactionManagerFactory.createTransactionManager(
					dataSource, sessionFactory));

			if (initModuleFramework) {
				ModuleFrameworkUtil.startFramework();
			}

			ConfigurableApplicationContext configurableApplicationContext =
				new ClassPathXmlApplicationContext(
					configLocations.toArray(new String[0]), false) {

					@Override
					protected DefaultListableBeanFactory createBeanFactory() {
						return new LiferayBeanFactory(
							getInternalParentBeanFactory());
					}

				};

			ConfigurableApplicationContextConfigurator
				configurableApplicationContextConfigurator =
					new AopConfigurableApplicationContextConfigurator();

			configurableApplicationContextConfigurator.configure(
				configurableApplicationContext);

			configurableApplicationContext.refresh();

			BeanLocator beanLocator = new BeanLocatorImpl(
				PortalClassLoaderUtil.getClassLoader(),
				configurableApplicationContext);

			PortalBeanLocatorUtil.setBeanLocator(beanLocator);

			_appApplicationContext = configurableApplicationContext;

			if (initModuleFramework && registerContext) {
				registerContext();
			}
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}

		_initialized = true;
	}

	public static boolean isInitialized() {
		return _initialized;
	}

	public static void registerContext() {
		if (_appApplicationContext != null) {
			ModuleFrameworkUtil.registerContext(_appApplicationContext);
		}
	}

	private static final boolean _PRINT_TIME = false;

	private static final Log _log = LogFactoryUtil.getLog(InitUtil.class);

	private static ApplicationContext _appApplicationContext;
	private static boolean _initialized;

}