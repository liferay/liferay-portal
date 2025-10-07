/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.db.migration.importer;

import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.io.unsync.UnsyncBufferedReader;
import com.liferay.portal.kernel.io.unsync.UnsyncStringReader;
import com.liferay.portal.kernel.module.framework.ThrowableCollector;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.tools.db.migration.importer.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.tools.db.migration.importer.jdbc.DataSourceFactoryUtil;

import java.io.File;
import java.io.FileFilter;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

/**
 * @author Mariano Álvaro Sáiz
 */
public class DBMigrationImportProcess {

	public DBMigrationImportProcess(
			String path, String sourceJDBCURL, String sourcePassword,
			String sourceUser, String targetJDBCURL, String targetPassword,
			String targetUser)
		throws Exception {

		_path = path;
		_sourceJDBCURL = sourceJDBCURL;
		_sourcePassword = sourcePassword;
		_sourceUser = sourceUser;
		_targetJDBCURL = targetJDBCURL;
		_targetPassword = targetPassword;
		_targetUser = targetUser;

		_sourceDataSource = DataSourceFactoryUtil.initDataSource(
			sourceJDBCURL, sourcePassword, sourceUser);

		_targetDataSource = DataSourceFactoryUtil.initDataSource(
			_targetJDBCURL, _targetPassword, _targetUser);
	}

	public String getDataSourceInfos() {
		StringBundler sb = new StringBundler(_dataSourceInfos.size() * 2);

		for (String dataSourceInfo : _dataSourceInfos) {
			sb.append(dataSourceInfo);
			sb.append(StringPool.NEW_LINE);
		}

		return sb.toString();
	}

	public String getReleaseInfo() throws Exception {
		StringBundler sb = new StringBundler(6);

		try (Connection connection = _sourceDataSource.getConnection();
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(
				"select buildDate, buildNumber, schemaVersion from Release_ " +
					"where servletContextName = 'portal'")) {

			resultSet.next();

			sb.append("Portal build date: ");

			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				DateUtil.ISO_8601_PATTERN);

			sb.append(simpleDateFormat.format(resultSet.getDate("buildDate")));

			sb.append("\nPortal build number: ");
			sb.append(resultSet.getLong("buildNumber"));
			sb.append("\nPortal schema version: ");
			sb.append(resultSet.getString("schemaVersion"));
		}

		return sb.toString();
	}

	public void run() throws Exception {
		_validateSQLFiles();

		_createTables();

		_copyTables();

		_createIndexes();

		_executorService.shutdownNow();

		_executorService.awaitTermination(10, TimeUnit.SECONDS);
	}

	private String _asymmetricDifference(Set<String> set1, Set<String> set2) {
		return StringUtil.merge(
			SetUtil.asymmetricDifference(set1, set2),
			StringPool.COMMA_AND_SPACE);
	}

	private void _copyTables() throws Exception {
		AutoBatchPreparedStatementUtil.start();

		Set<Future<?>> futures = Collections.newSetFromMap(
			new ConcurrentHashMap<>());
		ThrowableCollector throwableCollector = new ThrowableCollector();

		futures.add(
			_executorService.submit(
				() -> {
					try {
						new DBCopyTablesProcess(
							_sourceDataSource, _targetDataSource
						).run();

						_dataSourceInfos.add(
							0,
							_getDataSourceInfo(
								null, _sourceDataSource, _targetDataSource));
					}
					catch (Exception exception) {
						throwableCollector.collect(exception);
					}

					return null;
				}));

		for (String partitionName : _partitionNames) {
			futures.add(
				_executorService.submit(
					() -> {
						try {
							DataSource sourceDataSource =
								DataSourceFactoryUtil.initDataSource(
									_sourceJDBCURL, _sourcePassword,
									_sourceUser, partitionName);
							DataSource targetDataSource =
								DataSourceFactoryUtil.initDataSource(
									_targetJDBCURL, _targetPassword,
									_targetUser, partitionName);

							new DBCopyTablesProcess(
								sourceDataSource, targetDataSource
							).run();

							_dataSourceInfos.add(
								_getDataSourceInfo(
									partitionName, sourceDataSource,
									targetDataSource));
						}
						catch (Exception exception) {
							throwableCollector.collect(exception);
						}

						return null;
					}));
		}

		for (Future<?> future : futures) {
			future.get();
		}

		AutoBatchPreparedStatementUtil.stop();

		Throwable throwable = throwableCollector.getThrowable();

		if (throwable != null) {
			ReflectionUtil.throwException(throwable);
		}
	}

	private void _createIndexes() throws Exception {
		_executeFilesSQL("indexes.sql");
	}

	private void _createTables() throws Exception {
		_runSQLTemplate(
			_targetDataSource, _readFile(new File(_path, "tables.sql")));

		_executeFilesSQL("_tables.sql");
	}

	private void _executeFilesSQL(String suffix) throws Exception {
		File[] files = _listFiles(suffix);

		StringBundler sb = new StringBundler();

		int count = 0;

		for (File file : files) {
			sb.append(_readFile(file));

			if ((++count % _COMPANY_BATCH_SIZE) == 0) {
				_runSQLTemplate(_targetDataSource, sb.toString());

				sb.setIndex(0);
			}
		}

		if (sb.index() > 0) {
			_runSQLTemplate(_targetDataSource, sb.toString());
		}
	}

	private String _getDataSourceInfo(
			String partitionName, DataSource sourceDataSource,
			DataSource targetDataSource)
		throws Exception {

		if (Validator.isNull(partitionName)) {
			partitionName = "Default";
		}

		Set<String> sourceTableNames = _getNames(sourceDataSource, "TABLE");
		Set<String> sourceViewNames = _getNames(sourceDataSource, "VIEW");
		Set<String> targetTableNames = _getNames(targetDataSource, "TABLE");
		Set<String> targetViewNames = _getNames(targetDataSource, "VIEW");

		return StringUtil.merge(
			new Object[] {
				partitionName + " partition source tables: " +
					sourceTableNames.size(),
				partitionName + " partition source views: " +
					sourceViewNames.size(),
				partitionName + " partition target tables: " +
					targetTableNames.size(),
				partitionName + " partition target views: " +
					targetViewNames.size(),
				partitionName + " partition missing source tables: " +
					_asymmetricDifference(targetTableNames, sourceTableNames),
				partitionName + " partition missing source views: " +
					_asymmetricDifference(targetViewNames, sourceViewNames),
				partitionName + " partition missing target tables: " +
					_asymmetricDifference(sourceTableNames, targetTableNames),
				partitionName + " partition missing target views: " +
					_asymmetricDifference(sourceViewNames, targetViewNames),
				StringPool.NEW_LINE
			},
			StringPool.NEW_LINE);
	}

	private Set<String> _getNames(DataSource dataSource, String type)
		throws Exception {

		Set<String> names = new HashSet<>();

		try (Connection connection = dataSource.getConnection()) {
			DatabaseMetaData databaseMetaData = connection.getMetaData();

			try (ResultSet resultSet = databaseMetaData.getTables(
					connection.getCatalog(), connection.getSchema(), null,
					new String[] {type})) {

				while (resultSet.next()) {
					names.add(
						StringUtil.toLowerCase(
							resultSet.getString("TABLE_NAME")));
				}
			}
		}

		return names;
	}

	private File[] _listFiles(String suffix) {
		File dir = new File(_path);

		return dir.listFiles(
			new FileFilter() {

				@Override
				public boolean accept(File file) {
					if (file.isDirectory()) {
						return false;
					}

					return StringUtil.endsWith(file.getName(), suffix);
				}

			});
	}

	private void _preprocessSQLTemplate(String template) throws Exception {
		template = StringUtil.trim(template);

		if ((template == null) || template.isEmpty()) {
			return;
		}

		if (!template.endsWith(StringPool.SEMICOLON)) {
			template += StringPool.SEMICOLON;
		}

		try (UnsyncBufferedReader unsyncBufferedReader =
				new UnsyncBufferedReader(new UnsyncStringReader(template))) {

			StringBundler sb = new StringBundler();

			String line = null;

			while ((line = unsyncBufferedReader.readLine()) != null) {
				if (line.isEmpty() || line.startsWith("##")) {
					continue;
				}

				sb.append(line);
				sb.append(StringPool.NEW_LINE);

				if (line.endsWith(";")) {
					String sql = sb.toString();

					sb.setIndex(0);

					if (StringUtil.startsWith(sql, "create or replace rule")) {
						_syncFinalSQLs.add(sql);
					}
					else if (StringUtil.startsWith(
								sql, "create schema if not exists")) {

						String[] parts = StringUtil.split(sql, CharPool.SPACE);

						String partitionName = StringUtil.removeChar(
							parts[5], CharPool.SEMICOLON);

						_partitionNames.add(partitionName);

						_syncInitialSQLs.add(sql);
					}
					else {
						_asyncSQLs.add(sql);
					}
				}
			}
		}
	}

	private String _readFile(File file) throws Exception {
		return new String(
			Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
	}

	private void _runSQLTemplate(DataSource dataSource, String template)
		throws Exception {

		if (Validator.isNull(template)) {
			return;
		}

		_preprocessSQLTemplate(template);

		for (String sql : _syncInitialSQLs) {
			try (Connection connection = dataSource.getConnection();
				Statement statement = connection.createStatement()) {

				statement.executeUpdate(sql);
			}
		}

		_syncInitialSQLs.clear();

		List<Future<?>> futures = new ArrayList<>();
		ThrowableCollector throwableCollector = new ThrowableCollector();

		for (String sql : _asyncSQLs) {
			futures.add(
				_executorService.submit(
					() -> {
						try (Connection connection = dataSource.getConnection();
							Statement statement =
								connection.createStatement()) {

							statement.executeUpdate(sql);
						}
						catch (Exception exception) {
							throwableCollector.collect(exception);
						}
					}));
		}

		_asyncSQLs.clear();

		for (Future<?> future : futures) {
			future.get();
		}

		Throwable throwable = throwableCollector.getThrowable();

		if (throwable != null) {
			ReflectionUtil.throwException(throwable);
		}

		for (String sql : _syncFinalSQLs) {
			try (Connection connection = dataSource.getConnection();
				Statement statement = connection.createStatement()) {

				statement.executeUpdate(sql);
			}
		}

		_syncFinalSQLs.clear();
	}

	private void _validateSQLFiles() {
		if (!Files.exists(Paths.get(_path, "indexes.sql"))) {
			throw new IllegalStateException(
				"Missing " + _path + "/indexes.sql");
		}

		if (!Files.exists(Paths.get(_path, "tables.sql"))) {
			throw new IllegalStateException("Missing " + _path + "/tables.sql");
		}
	}

	private static final int _COMPANY_BATCH_SIZE = 5;

	private final List<String> _asyncSQLs = new ArrayList<>();
	private final List<String> _dataSourceInfos = Collections.synchronizedList(
		new ArrayList<>());
	private final ExecutorService _executorService =
		Executors.newFixedThreadPool(5);
	private final List<String> _partitionNames = new ArrayList<>();
	private final String _path;
	private final DataSource _sourceDataSource;
	private final String _sourceJDBCURL;
	private final String _sourcePassword;
	private final String _sourceUser;
	private final List<String> _syncFinalSQLs = new ArrayList<>();
	private final List<String> _syncInitialSQLs = new ArrayList<>();
	private final DataSource _targetDataSource;
	private final String _targetJDBCURL;
	private final String _targetPassword;
	private final String _targetUser;

}