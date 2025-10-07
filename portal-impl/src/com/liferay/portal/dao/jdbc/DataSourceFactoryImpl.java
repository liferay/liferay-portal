/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.jdbc;

import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.dao.jdbc.util.DataSourceWrapper;
import com.liferay.portal.kernel.configuration.Filter;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.dao.jdbc.DataSourceFactory;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.jndi.JNDIUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.JavaDetector;
import com.liferay.portal.kernel.util.PropertiesUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.ServerDetector;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.spring.hibernate.DialectDetector;
import com.liferay.portal.util.JarUtil;

import com.zaxxer.hikari.HikariDataSource;

import java.io.Closeable;

import java.lang.reflect.Field;

import java.net.URL;
import java.net.URLClassLoader;

import java.nio.file.Paths;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Arrays;
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

import jodd.bean.BeanUtil;

/**
 * @author Brian Wing Shun Chan
 * @author Shuyang Zhou
 */
public class DataSourceFactoryImpl implements DataSourceFactory {

	@Override
	public void destroyDataSource(DataSource dataSource) throws Exception {
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

	@Override
	public DataSource initDataSource(Properties properties) throws Exception {
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

		DBType dbType = DBManagerUtil.getDBType(
			DialectDetector.getDialect(dataSource));

		if (dbType == DBType.SQLSERVER) {
			_checkSQLServer(dataSource);
		}

		return dataSource;
	}

	@Override
	public DataSource initDataSource(
			String driverClassName, String url, String userName,
			String password, String jndiName)
		throws Exception {

		Properties properties = new Properties();

		properties.setProperty("driverClassName", driverClassName);
		properties.setProperty("url", url);
		properties.setProperty("username", userName);
		properties.setProperty("password", password);
		properties.setProperty("jndi.name", jndiName);

		return initDataSource(properties);
	}

	protected DataSource initDataSourceHikariCP(Properties properties)
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
				BeanUtil.pojo.setProperty(hikariDataSource, key, value);
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

	protected boolean isPropertyLiferay(String key) {
		if (StringUtil.equalsIgnoreCase(
				key, "data.source.unavailable.timeout") ||
			StringUtil.equalsIgnoreCase(key, "jndi.name")) {

			return true;
		}

		return false;
	}

	protected void testDatabaseClass(String driverClassName) throws Exception {
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
				JarUtil.downloadAndInstallJar(
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

	private void _checkSQLServer(DataSource dataSource) {
		try (Connection connection = dataSource.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				"select name, is_read_committed_snapshot_on from " +
					"sys.databases where name = db_name()");
			ResultSet resultSet = preparedStatement.executeQuery()) {

			if (!resultSet.next() ||
				resultSet.getBoolean("is_read_committed_snapshot_on") ||
				!_log.isWarnEnabled()) {

				return;
			}

			String name = resultSet.getString("name");

			_log.warn(
				StringBundler.concat(
					"SQL Server may have deadlocks because ",
					"\"read_committed_snapshot\" is disabled for database \"",
					name, "\". To enable, execute: alter database ", name,
					" set read_committed_snapshot on"));
		}
		catch (Exception exception) {
			_log.error("Unable to check SQL Server", exception);
		}
	}

	private void _populateIBMCipherSuites(Class<?> clazz) {
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
				if (!allowedCiphers.contains(ibmSupportedCipherSuite)) {
					allowedCiphers.add(ibmSupportedCipherSuite);
				}
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

	private String _rewriteJDBCURL(String url) {
		if (!url.startsWith("jdbc:mariadb://") &&
			!url.startsWith("jdbc:mysql://")) {

			return url;
		}

		Map<String, String> existingParameterValues = new TreeMap<>();

		int index = url.indexOf(CharPool.QUESTION);

		if (index != -1) {
			String queryString = url.substring(index + 1);

			for (String parameterString :
					StringUtil.split(queryString, CharPool.AMPERSAND)) {

				String[] parameter = StringUtil.split(
					parameterString, CharPool.EQUAL);

				if (parameter.length == 2) {
					existingParameterValues.put(parameter[0], parameter[1]);
				}
				else {
					existingParameterValues.put(
						parameterString, _MALFORMED_PARAMETER_PLACE_HOLDER);
				}
			}
		}

		for (String[] parameter : _MYSQL_DEFAULT_PARAMETERS) {
			if (existingParameterValues.containsKey(parameter[0])) {
				if (_log.isDebugEnabled()) {
					_log.debug("Skipped " + Arrays.toString(parameter));
				}
			}
			else {
				existingParameterValues.put(parameter[0], parameter[1]);
			}
		}

		StringBundler sb = new StringBundler(
			(existingParameterValues.size() * 4) + 2);

		if (index == -1) {
			sb.append(url);
			sb.append(CharPool.QUESTION);
		}
		else {
			sb.append(url.substring(0, index + 1));
		}

		for (Map.Entry<String, String> entry :
				existingParameterValues.entrySet()) {

			sb.append(entry.getKey());

			String value = entry.getValue();

			if (!_MALFORMED_PARAMETER_PLACE_HOLDER.equals(value)) {
				sb.append(CharPool.EQUAL);
				sb.append(value);
			}

			sb.append(CharPool.AMPERSAND);
		}

		if (!existingParameterValues.isEmpty()) {
			sb.setIndex(sb.index() - 1);
		}

		String newURL = sb.toString();

		if (!Objects.equals(url, newURL) && _log.isInfoEnabled()) {
			_log.info(
				StringBundler.concat(
					"Rewrite JDBC URL from ", url, " to ", newURL));
		}

		return newURL;
	}

	private void _waitForJDBCConnection(Properties properties) {
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

	private static final String[][] _MYSQL_DEFAULT_PARAMETERS = {
		{"cachePrepStmts", "true"}, {"characterEncoding", "UTF-8"},
		{"dontTrackOpenResources", "true"},
		{"holdResultsOpenOverStatementClose", "true"},
		{"prepStmtCacheSize", "1000"}, {"prepStmtCacheSqlLimit", "2048"},
		{"rewriteBatchedStatements", "true"}, {"serverTimezone", "GMT"},
		{"useFastDateParsing", "false"}, {"useLocalSessionState", "true"},
		{"useLocalTransactionState", "true"}, {"useUnicode", "true"}
	};

	private static final Log _log = LogFactoryUtil.getLog(
		DataSourceFactoryImpl.class);

	private static class JNDIDataSourceWrapper extends DataSourceWrapper {

		private JNDIDataSourceWrapper(DataSource dataSource) {
			super(dataSource);
		}

	}

}