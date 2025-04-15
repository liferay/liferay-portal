/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.spring.hibernate;

import com.liferay.petra.io.Deserializer;
import com.liferay.petra.io.Serializer;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.lang.ThreadContextClassLoaderUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.portal.internal.change.tracking.hibernate.CTSQLInterceptor;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.util.PropsUtil;
import com.liferay.portal.util.PropsValues;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.lang.reflect.Field;

import java.net.URL;
import java.net.URLConnection;

import java.nio.ByteBuffer;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.jaxb.Origin;
import org.hibernate.boot.jaxb.SourceType;
import org.hibernate.boot.jaxb.internal.InputStreamXmlSource;
import org.hibernate.boot.jaxb.spi.Binding;
import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
import org.hibernate.boot.spi.XmlMappingBinderAccess;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.metamodel.spi.MetamodelImplementor;
import org.hibernate.type.spi.TypeConfiguration;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.hibernate5.LocalSessionFactoryBuilder;

/**
 * @author Brian Wing Shun Chan
 * @author Marcellus Tavares
 * @author Shuyang Zhou
 * @author Tomas Polesovsky
 */
public class PortalHibernateConfiguration extends LocalSessionFactoryBean {

	@Override
	public void afterPropertiesSet() throws IOException {
		Dialect dialect = DialectDetector.getDialect(_dataSource);

		if (DBManagerUtil.getDBType(dialect) == DBType.ORACLE) {

			// This must be done before the instantiating Configuration to
			// ensure that org.hibernate.cfg.Environment's static init block can
			// see it

			System.setProperty(
				PropsKeys.HIBERNATE_JDBC_USE_STREAMS_FOR_BINARY, "true");
		}

		Properties properties = PropsUtil.getProperties();

		properties.remove("hibernate.cache.region.factory_class");

		properties.setProperty(
			"hibernate.allow_update_outside_transaction", "true");
		properties.setProperty("hibernate.cache.use_query_cache", "false");
		properties.setProperty(
			"hibernate.cache.use_second_level_cache", "false");
		properties.setProperty(
			"hibernate.current_session_context_class",
			PortalCurrentSessionContext.class.getName());

		if (Validator.isNull(PropsValues.HIBERNATE_DIALECT)) {
			Class<?> clazz = dialect.getClass();

			properties.setProperty("hibernate.dialect", clazz.getName());
		}

		properties.setProperty(
			"hibernate.query.sql.jdbc_style_params_base", "true");
		properties.setProperty("jakarta.persistence.validation.mode", "none");

		setHibernateProperties(properties);

		BootstrapServiceRegistryBuilder bootstrapServiceRegistryBuilder =
			new BootstrapServiceRegistryBuilder();

		bootstrapServiceRegistryBuilder.applyClassLoader(
			getConfigurationClassLoader());

		bootstrapServiceRegistryBuilder.applyIntegrator(
			GlobalEventListenerIntegrator.INSTANCE);

		if (_mvccEnabled) {
			bootstrapServiceRegistryBuilder.applyIntegrator(
				new CTModelIntegrator());
			bootstrapServiceRegistryBuilder.applyIntegrator(
				MVCCEventListenerIntegrator.INSTANCE);

			setEntityInterceptor(new CTSQLInterceptor());
		}

		setMetadataSources(
			new MetadataSources(bootstrapServiceRegistryBuilder.build()));

		super.afterPropertiesSet();
	}

	public void setConfigurationResources(String[] configurationResources) {
		_configurationResources = configurationResources;
	}

	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);

		_dataSource = dataSource;
	}

	public void setMvccEnabled(boolean mvccEnabled) {
		_mvccEnabled = mvccEnabled;
	}

	@Override
	protected SessionFactory buildSessionFactory(
			LocalSessionFactoryBuilder localSessionFactoryBuilder)
		throws HibernateException {

		try {
			String[] resources = getConfigurationResources();

			for (String resource : resources) {
				try {
					readResource(localSessionFactoryBuilder, resource);
				}
				catch (Exception exception) {
					if (_log.isWarnEnabled()) {
						_log.warn(exception);
					}
				}
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		SessionFactory sessionFactory = super.buildSessionFactory(
			localSessionFactoryBuilder);

		SessionFactoryImplementor sessionFactoryImplementor =
			(SessionFactoryImplementor)sessionFactory;

		MetamodelImplementor metamodelImplementor =
			sessionFactoryImplementor.getMetamodel();

		TypeConfiguration typeConfiguration =
			metamodelImplementor.getTypeConfiguration();

		try {
			_META_MODEL_FIELD.set(
				sessionFactory,
				ProxyUtil.newDelegateProxyInstance(
					MetamodelImplementor.class.getClassLoader(),
					MetamodelImplementor.class,
					new SessionFactoryDelegate(
						typeConfiguration.getImportMap()),
					metamodelImplementor));
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to inject optimized query plan cache", exception);
			}
		}

		return sessionFactory;
	}

	protected ClassLoader getConfigurationClassLoader() {
		Class<?> clazz = getClass();

		return clazz.getClassLoader();
	}

	protected String[] getConfigurationResources() {
		if (_configurationResources == null) {
			return PropsUtil.getArray(PropsKeys.HIBERNATE_CONFIGS);
		}

		return _configurationResources;
	}

	protected void readResource(Configuration configuration, String resource)
		throws Exception {

		ClassLoader classLoader = getConfigurationClassLoader();

		if (resource.startsWith("classpath*:")) {
			String name = resource.substring("classpath*:".length());

			Enumeration<URL> enumeration = classLoader.getResources(name);

			if (_log.isDebugEnabled() && !enumeration.hasMoreElements()) {
				_log.debug("No resources found for " + name);
			}

			while (enumeration.hasMoreElements()) {
				URL url = enumeration.nextElement();

				readResource(configuration, url);
			}
		}
		else {
			readResource(configuration, classLoader.getResource(resource));
		}
	}

	protected void readResource(Configuration configuration, URL url)
		throws Exception {

		if (url == null) {
			return;
		}

		try (SafeCloseable safeCloseable = ThreadContextClassLoaderUtil.swap(
				PortalHibernateConfiguration.class.getClassLoader())) {

			configuration.addXmlMapping(_loadBinding(configuration, url));
		}
	}

	private File _getCacheFile(URL url) {
		long bundleId = 0;

		if (Objects.equals(url.getProtocol(), "bundleresource")) {
			String host = url.getHost();

			int index = host.indexOf(CharPool.PERIOD);

			if (index != -1) {
				bundleId = GetterUtil.getLong(host.substring(0, index));
			}
		}

		Bundle bundle = _bundleContext.getBundle(bundleId);

		return bundle.getDataFile(
			StringUtil.replace(
				StringUtil.replace(
					url.getPath(), PropsValues.LIFERAY_HOME, "_liferay_home_"),
				new char[] {
					CharPool.COLON, CharPool.EXCLAMATION, CharPool.SLASH
				},
				new char[] {
					CharPool.UNDERLINE, CharPool.UNDERLINE, CharPool.UNDERLINE
				}));
	}

	private Binding<?> _loadBinding(Configuration configuration, URL url)
		throws Exception {

		URLConnection urlConnection = url.openConnection();

		File cacheFile = null;

		long lastModifiedTime = 0;

		if (PropsValues.HIBERNATE_HBM_JAXB_CACHE) {
			cacheFile = _getCacheFile(url);

			lastModifiedTime = urlConnection.getLastModified();

			try {
				if (cacheFile.exists() &&
					(cacheFile.lastModified() == lastModifiedTime)) {

					Deserializer deserializer = new Deserializer(
						ByteBuffer.wrap(FileUtil.getBytes(cacheFile)));

					Binding<?> binding = deserializer.readObject();

					InputStream inputStream = urlConnection.getInputStream();

					inputStream.close();

					return binding;
				}
			}
			catch (Exception exception) {
				_log.error("Unable to load " + url, exception);
			}
		}

		XmlMappingBinderAccess xmlMappingBinderAccess =
			configuration.getXmlMappingBinderAccess();

		Binding<?> binding = InputStreamXmlSource.doBind(
			xmlMappingBinderAccess.getMappingBinder(),
			urlConnection.getInputStream(),
			new Origin(SourceType.URL, url.toExternalForm()), true);

		if (PropsValues.HIBERNATE_HBM_JAXB_CACHE) {
			Serializer serializer = new Serializer();

			serializer.writeObject(binding);

			try (OutputStream outputStream = new FileOutputStream(cacheFile)) {
				serializer.writeTo(outputStream);
			}

			cacheFile.setLastModified(lastModifiedTime);
		}

		return binding;
	}

	private static final Field _META_MODEL_FIELD;

	private static final Log _log = LogFactoryUtil.getLog(
		PortalHibernateConfiguration.class);

	private static final BundleContext _bundleContext;

	static {
		_bundleContext = SystemBundleUtil.getBundleContext();

		try {
			_META_MODEL_FIELD = ReflectionUtil.getDeclaredField(
				SessionFactoryImpl.class, "metamodel");
		}
		catch (Exception exception) {
			throw new ExceptionInInitializerError(exception);
		}
	}

	private String[] _configurationResources;
	private DataSource _dataSource;
	private boolean _mvccEnabled = true;

	private static class SessionFactoryDelegate {

		public String getImportedClassName(String className) {
			return _imports.get(className);
		}

		private SessionFactoryDelegate(Map<String, String> imports) {
			_imports = new HashMap<>(imports);
		}

		private final Map<String, String> _imports;

	}

}