/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.dao.jdbc;

import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.configuration.Filter;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.jndi.JNDIUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.upgrade.util.UpgradeProcessUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.JavaDetector;
import com.liferay.portal.kernel.util.PropertiesUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.ServerDetector;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TextFormatter;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.URLUtil;
import com.liferay.portal.kernel.util.Validator;

import com.zaxxer.hikari.HikariDataSource;

import java.io.Closeable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;

import java.nio.file.Path;
import java.nio.file.Paths;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.TreeMap;

import javax.naming.Context;
import javax.naming.InitialContext;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

import javax.sql.DataSource;

/**
 * @author Brian Wing Shun Chan
 */
public class DataSourceFactoryUtil {

	public static void destroyDataSource(DataSource dataSource)
		throws Exception {

		while (dataSource instanceof DataSourceWrapper) {
			DataSourceWrapper dataSourceWrapper = (DataSourceWrapper)dataSource;

			if (dataSourceWrapper instanceof JNDIDataSourceWrapper) {
				return;
			}

			dataSource = dataSourceWrapper.getWrappedDataSource();
		}

		if (dataSource instanceof Closeable) {
			Closeable closeable = (Closeable)dataSource;

			closeable.close();
		}
	}

	public static DataSource initDataSource(Properties properties)
		throws Exception {

		String jndiName = properties.getProperty("jndi.name");
		String driverClassName = properties.getProperty("driverClassName");

		if (JavaDetector.isIBM() &&
			(Validator.isNotNull(jndiName) ||
			 driverClassName.startsWith("com.mysql.cj"))) {

			// LPS-120753

			if (Validator.isNull(jndiName)) {
				testDatabaseClass(driverClassName);
			}

			try {
				_populateIBMCipherSuites(
					Class.forName("com.mysql.cj.protocol.ExportControlled"));
			}
			catch (ClassNotFoundException classNotFoundException) {
				if (_log.isDebugEnabled()) {
					_log.debug(classNotFoundException);
				}
			}
		}

		if (Validator.isNotNull(jndiName)) {
			try {
				Properties jndiEnvironmentProperties = PropsUtil.getProperties(
					PropsKeys.JNDI_ENVIRONMENT, true);

				Context context = new InitialContext(jndiEnvironmentProperties);

				return new JNDIDataSourceWrapper(
					(DataSource)JNDIUtil.lookup(context, jndiName));
			}
			catch (Exception exception) {
				_log.error("Unable to lookup " + jndiName, exception);

				throw exception;
			}
		}
		else {
			try {
				testDatabaseClass(driverClassName);
			}
			catch (ClassNotFoundException classNotFoundException) {
				_log.error(
					StringBundler.concat(
						"Unable to find the JDBC driver class ",
						driverClassName, " in a JAR in the directory ",
						PropsValues.LIFERAY_SHIELDED_CONTAINER_LIB_PORTAL_DIR));

				throw classNotFoundException;
			}

			_waitForJDBCConnection(properties);
		}

		if (_log.isDebugEnabled()) {
			_log.debug("Data source properties:\n");

			_log.debug(PropertiesUtil.toString(properties));
		}

		DataSource dataSource = initDataSourceHikariCP(properties);

		if (_log.isDebugEnabled()) {
			_log.debug("Created data source " + dataSource.getClass());
		}

		return dataSource;
	}

	public static DataSource initDataSource(
			String driverClassName, String url, String userName,
			String password, String jndiName)
		throws Exception {

		Properties properties = new Properties();

		properties.setProperty("driverClassName", driverClassName);
		properties.setProperty("jndi.name", jndiName);
		properties.setProperty("password", password);
		properties.setProperty("url", url);
		properties.setProperty("username", userName);

		return initDataSource(properties);
	}

	protected static DataSource initDataSourceHikariCP(Properties properties)
		throws Exception {

		HikariDataSource hikariDataSource = new HikariDataSource();

		String connectionPropertiesString = (String)properties.remove(
			"connectionProperties");

		if (connectionPropertiesString != null) {
			Properties connectionProperties = PropertiesUtil.load(
				StringUtil.replace(
					connectionPropertiesString, CharPool.SEMICOLON,
					CharPool.NEW_LINE));

			hikariDataSource.setDataSourceProperties(connectionProperties);
		}

		for (Map.Entry<Object, Object> entry : properties.entrySet()) {
			String key = (String)entry.getKey();

			// Ignore Liferay property

			if (isPropertyLiferay(key)) {
				continue;
			}

			String value = (String)entry.getValue();

			if (StringUtil.equalsIgnoreCase(key, "url")) {
				key = "jdbcUrl";

				value = _rewriteJDBCURL(value);
			}

			// Set HikariCP property

			try {
				_setProperty(hikariDataSource, key, value);
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Property " + key + " is an invalid HikariCP property",
						exception);
				}
			}
		}

		return hikariDataSource;
	}

	protected static boolean isPropertyLiferay(String key) {
		if (StringUtil.equalsIgnoreCase(
				key, "data.source.unavailable.timeout") ||
			StringUtil.equalsIgnoreCase(key, "jndi.name")) {

			return true;
		}

		return false;
	}

	protected static void testDatabaseClass(String driverClassName)
		throws Exception {

		try {
			Class.forName(driverClassName);
		}
		catch (ClassNotFoundException classNotFoundException) {
			if (!ServerDetector.isTomcat()) {
				throw classNotFoundException;
			}

			String url = PropsUtil.get(
				PropsKeys.SETUP_DATABASE_JAR_URL, new Filter(driverClassName));
			String name = PropsUtil.get(
				PropsKeys.SETUP_DATABASE_JAR_NAME, new Filter(driverClassName));
			String sha1 = PropsUtil.get(
				PropsKeys.SETUP_DATABASE_JAR_SHA1, new Filter(driverClassName));

			if (Validator.isNull(url) || Validator.isNull(name) ||
				Validator.isNull(sha1)) {

				throw classNotFoundException;
			}

			ClassLoader classLoader = SystemException.class.getClassLoader();

			if (!(classLoader instanceof URLClassLoader)) {
				_log.error(
					"Unable to install JAR because the system class loader " +
						"is not an instance of URLClassLoader");

				return;
			}

			try {
				_downloadAndInstallJar(
					new URL(url),
					Paths.get(
						PropsValues.LIFERAY_SHIELDED_CONTAINER_LIB_PORTAL_DIR,
						name),
					(URLClassLoader)classLoader, sha1);
			}
			catch (Exception exception) {
				_log.error(
					StringBundler.concat(
						"Unable to download and install ", name, " to ",
						PropsValues.LIFERAY_SHIELDED_CONTAINER_LIB_PORTAL_DIR,
						" from ", url),
					exception);

				throw classNotFoundException;
			}
		}
	}

	private static void _downloadAndInstallJar(
			URL url, Path path, URLClassLoader urlClassLoader, String sha1)
		throws Exception {

		URLUtil.download(url, path, sha1);

		URI uri = path.toUri();

		if (_log.isInfoEnabled()) {
			_log.info(
				StringBundler.concat(
					"Installing ", path, " to ", urlClassLoader));
		}

		_addURLMethod.invoke(urlClassLoader, uri.toURL());

		if (_log.isInfoEnabled()) {
			_log.info(
				StringBundler.concat(
					"Installed ", path, " to ", urlClassLoader));
		}
	}

	private static void _populateIBMCipherSuites(Class<?> clazz) {
		try {
			SSLContext sslContext = SSLContext.getDefault();

			SSLEngine sslEngine = sslContext.createSSLEngine();

			String[] ibmSupportedCipherSuites =
				sslEngine.getSupportedCipherSuites();

			if (ArrayUtil.isEmpty(ibmSupportedCipherSuites)) {
				return;
			}

			Field allowedCiphersField = ReflectionUtil.getDeclaredField(
				clazz, "ALLOWED_CIPHERS");

			List<String> allowedCiphers = (List<String>)allowedCiphersField.get(
				null);

			for (String ibmSupportedCipherSuite : ibmSupportedCipherSuites) {
				if (allowedCiphers.contains(ibmSupportedCipherSuite)) {
					continue;
				}

				allowedCiphers.add(ibmSupportedCipherSuite);
			}
		}
		catch (Exception exception) {
			_log.error(
				"Unable to populate IBM JDK TLS cipher suite into MySQL " +
					"Connector/J's allowed cipher list, consider disabling " +
						"SSL for the connection",
				exception);
		}
	}

	private static String _rewriteJDBCURL(
		Map<String, String> defaultParameters, char parameterDelimiter,
		int searchFrom, boolean trimTrailingDelimiter, String url,
		char urlDelimiter) {

		Map<String, String> existingParameters = new TreeMap<>();

		String baseURL = url;

		int index = url.indexOf(urlDelimiter, searchFrom);

		if (index != -1) {
			baseURL = url.substring(0, index);

			String queryString = url.substring(index + 1);

			if (!queryString.isEmpty()) {
				for (String parameter :
						StringUtil.split(queryString, parameterDelimiter)) {

					if (parameter.isEmpty()) {
						continue;
					}

					String[] parts = StringUtil.split(
						parameter, CharPool.EQUAL);

					if (parts.length == 2) {
						existingParameters.put(parts[0], parts[1]);
					}
					else {
						existingParameters.put(
							parameter, _MALFORMED_PARAMETER_PLACE_HOLDER);
					}
				}
			}
		}

		for (Map.Entry<String, String> entry : defaultParameters.entrySet()) {
			if (existingParameters.containsKey(entry.getKey())) {
				if (_log.isDebugEnabled()) {
					_log.debug("Skipped " + entry.getKey());
				}
			}
			else {
				existingParameters.put(entry.getKey(), entry.getValue());
			}
		}

		String newURL = baseURL;

		if (!existingParameters.isEmpty()) {
			StringBundler sb = new StringBundler();

			sb.append(baseURL);
			sb.append(urlDelimiter);

			for (Map.Entry<String, String> entry :
					existingParameters.entrySet()) {

				sb.append(entry.getKey());

				if (!_MALFORMED_PARAMETER_PLACE_HOLDER.equals(
						entry.getValue())) {

					sb.append(CharPool.EQUAL);
					sb.append(entry.getValue());
				}

				sb.append(parameterDelimiter);
			}

			if (trimTrailingDelimiter) {
				sb.setIndex(sb.index() - 1);
			}

			newURL = sb.toString();
		}

		if (!Objects.equals(url, newURL) && _log.isInfoEnabled()) {
			_log.info(
				StringBundler.concat(
					"Rewrote JDBC URL from ", url, " to ", newURL));
		}

		return newURL;
	}

	private static String _rewriteJDBCURL(
		Map<String, String> defaultParameters, char parameterDelimiter,
		String url, char urlDelimiter) {

		return _rewriteJDBCURL(
			defaultParameters, parameterDelimiter,
			url.indexOf("://") + "://".length(), true, url, urlDelimiter);
	}

	private static String _rewriteJDBCURL(String url) {
		if (url.startsWith("jdbc:db2://")) {
			int index = url.indexOf(
				CharPool.SLASH, url.indexOf("://") + "://".length());

			if (index == -1) {
				return url;
			}

			return _rewriteJDBCURL(
				HashMapBuilder.put(
					"queryTimeoutInterruptProcessingMode", "1"
				).build(),
				CharPool.SEMICOLON, index, false, url, CharPool.COLON);
		}

		if (url.startsWith("jdbc:mariadb://") ||
			url.startsWith("jdbc:mysql://")) {

			return _rewriteJDBCURL(
				HashMapBuilder.put(
					"cachePrepStmts", "true"
				).put(
					"characterEncoding", "UTF-8"
				).put(
					"dontTrackOpenResources", "true"
				).put(
					"holdResultsOpenOverStatementClose", "true"
				).put(
					"prepStmtCacheSize", "1000"
				).put(
					"prepStmtCacheSqlLimit", "2048"
				).put(
					"rewriteBatchedStatements", "true"
				).put(
					"serverTimezone", "GMT"
				).put(
					"useFastDateParsing", "false"
				).put(
					"useLocalSessionState", "true"
				).put(
					"useLocalTransactionState", "true"
				).put(
					"useUnicode", "true"
				).build(),
				CharPool.AMPERSAND, url, CharPool.QUESTION);
		}

		if (url.startsWith("jdbc:postgresql://")) {
			return _rewriteJDBCURL(
				HashMapBuilder.put(
					"reWriteBatchedInserts", "true"
				).build(),
				CharPool.AMPERSAND, url, CharPool.QUESTION);
		}

		if (url.startsWith("jdbc:sqlserver://")) {
			if (!UpgradeProcessUtil.isUpgradeClient()) {
				return url;
			}

			try {
				Driver driver = DriverManager.getDriver(url);

				int majorVersion = driver.getMajorVersion();
				int minorVersion = driver.getMinorVersion();

				if ((majorVersion < 12) ||
					((majorVersion == 12) && (minorVersion < 4))) {

					if (_log.isWarnEnabled()) {
						_log.warn(
							"Update SQL Server driver version to >= 12.4.0 " +
								"to support useBulkCopyForBatchInsert");
					}

					return url;
				}
			}
			catch (SQLException sqlException) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Unable to determine SQL Server driver version",
						sqlException);
				}

				return url;
			}

			return _rewriteJDBCURL(
				HashMapBuilder.put(
					"bulkCopyForBatchInsertTableLock", "true"
				).put(
					"useBulkCopyForBatchInsert", "true"
				).build(),
				CharPool.SEMICOLON, url, CharPool.SEMICOLON);
		}

		return url;
	}

	private static void _setProperty(
			HikariDataSource hikariDataSource, String key, String value)
		throws Exception {

		String keyName = TextFormatter.format(key, TextFormatter.G);

		Method getterMethod = ReflectionUtil.fetchMethod(
			HikariDataSource.class, "get" + keyName, new Class<?>[0]);

		if (getterMethod == null) {
			getterMethod = ReflectionUtil.fetchMethod(
				HikariDataSource.class, "is" + keyName, new Class<?>[0]);
		}

		Class<?> clazz = getterMethod.getReturnType();

		Method setterMethod = ReflectionUtil.fetchMethod(
			HikariDataSource.class, "set" + keyName, new Class<?>[] {clazz});

		setterMethod.invoke(hikariDataSource, _toTypedValue(clazz, value));
	}

	private static Object _toTypedValue(Class<?> clazz, String value) {

		// HikariDataSource setters only take boolean, int, long, and String
		// parameters

		if (clazz == boolean.class) {
			return Boolean.valueOf(value);
		}

		if (clazz == int.class) {
			return Integer.valueOf(value);
		}

		if (clazz == long.class) {
			return Long.valueOf(value);
		}

		return value;
	}

	private static void _waitForJDBCConnection(Properties properties) {
		int maxRetries = PropsValues.RETRY_JDBC_ON_STARTUP_MAX_RETRIES;

		if (maxRetries <= 0) {
			return;
		}

		int delay = PropsValues.RETRY_JDBC_ON_STARTUP_DELAY;

		if (delay < 0) {
			delay = 0;
		}

		String url = properties.getProperty("url");
		String username = properties.getProperty("username");
		String password = properties.getProperty("password");

		int count = maxRetries;

		while (count-- > 0) {
			try (Connection connection = DriverManager.getConnection(
					url, username, password)) {

				if (connection != null) {
					if (_log.isInfoEnabled()) {
						_log.info("Successfully acquired JDBC connection");
					}

					return;
				}
			}
			catch (SQLException sqlException) {
				if (_log.isDebugEnabled()) {
					_log.error(
						"Unable to acquire JDBC connection", sqlException);
				}
			}

			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"At attempt ", maxRetries - count, " of ", maxRetries,
						" in acquiring a JDBC connection after a ", delay,
						" seconds delay"));
			}

			try {
				Thread.sleep(delay * Time.SECOND);
			}
			catch (InterruptedException interruptedException) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Interruptted acquiring a JDBC connection",
						interruptedException);
				}

				break;
			}
		}

		if (_log.isWarnEnabled()) {
			_log.warn(
				"Unable to acquire a direct JDBC connection, proceeding to " +
					"use a data source instead");
		}
	}

	private static final String _MALFORMED_PARAMETER_PLACE_HOLDER =
		"_MALFORMED_PARAMETER_PLACE_HOLDER";

	private static final Log _log = LogFactoryUtil.getLog(
		DataSourceFactoryUtil.class);

	private static final Method _addURLMethod;

	static {
		try {
			_addURLMethod = ReflectionUtil.getDeclaredMethod(
				URLClassLoader.class, "addURL", URL.class);
		}
		catch (Exception exception) {
			throw new ExceptionInInitializerError(exception);
		}
	}

	private static class JNDIDataSourceWrapper extends DataSourceWrapper {

		private JNDIDataSourceWrapper(DataSource dataSource) {
			super(dataSource);
		}

	}

}