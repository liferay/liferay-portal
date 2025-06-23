/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.internal.report;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.events.StartupHelperUtil;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ReleaseConstants;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.upgrade.ReleaseManager;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.recorder.UpgradeSQLRecorder;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.EnvPropertiesUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ReleaseInfo;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.tools.DBUpgrader;
import com.liferay.portal.upgrade.PortalUpgradeProcess;
import com.liferay.portal.upgrade.internal.recorder.UpgradeRecorder;
import com.liferay.portal.util.PropsUtil;
import com.liferay.portal.util.PropsValues;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

import java.net.URI;

import java.nio.file.Files;
import java.nio.file.Paths;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;
import org.apache.felix.cm.PersistenceManager;
import org.apache.logging.log4j.ThreadContext;

/**
 * @author Sam Ziemer
 */
public class UpgradeReport {

	public UpgradeReport() {
		_initialBuildNumber = _getBuildNumber();

		if (StartupHelperUtil.isNewRelease()) {
			_initialTableCounts = _getTableCounts();
		}
	}

	public void generateReport(UpgradeRecorder upgradeRecorder) {
		if (StringUtil.equals(upgradeRecorder.getType(), "no upgrade")) {
			if (_log.isInfoEnabled()) {
				_log.info(
					"Upgrade report was not generated because no upgrade " +
						"processes were executed");
			}

			return;
		}

		if (_log.isInfoEnabled()) {
			_log.info("Starting upgrade report generation");
		}

		_executionDateString = _getExecutionDateString();
		_executionTimeString =
			(DBUpgrader.getUpgradeTime() / Time.SECOND) + " seconds";
		_rootDir = _getRootDir();

		Map<String, Object> reportData = _getReportData(upgradeRecorder);

		Map<String, Object> reportDataDiagnostics = _getReportDataDiagnostics(
			upgradeRecorder);

		_printToLogContext(reportData);

		_writeToFile(reportData, "upgrade_report.txt");

		_printToLogContext(reportDataDiagnostics);

		_writeToFile(reportDataDiagnostics, "upgrade_report_diagnostics.txt");
	}

	private int _getBuildNumber() {
		try (Connection connection = DataAccess.getConnection()) {
			return PortalUpgradeProcess.getCurrentBuildNumber(connection);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to get build number", exception);
			}
		}

		return 0;
	}

	private String _getExecutionDateString() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
			"EEE, MMM dd, yyyy hh:mm:ss z");

		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

		Calendar calendar = Calendar.getInstance();

		return simpleDateFormat.format(calendar.getTime());
	}

	private List<MessagesPrinter> _getMessagesPrinters(
		boolean includeOccurrences, Map<String, Map<String, Integer>> map1) {

		List<MessagesPrinter> messagesPrinters = new ArrayList<>();

		List<Map.Entry<String, Map<String, Integer>>> list = new ArrayList<>();

		list.addAll(map1.entrySet());

		ListUtil.sort(
			list,
			Collections.reverseOrder(
				Map.Entry.comparingByValue(
					Comparator.comparingInt(Map::size))));

		for (Map.Entry<String, Map<String, Integer>> entry1 : list) {
			MessagesPrinter messagesPrinter = new MessagesPrinter(
				entry1.getKey());

			messagesPrinters.add(messagesPrinter);

			Map<String, Integer> map2 = entry1.getValue();

			for (Map.Entry<String, Integer> entry2 : map2.entrySet()) {
				messagesPrinter.addMessagePrinter(
					entry2.getKey(),
					includeOccurrences ? entry2.getValue() : null);
			}
		}

		return messagesPrinters;
	}

	private Set<String> _getPropertiesFilePathStrings() {
		Set<String> propertiesFilePathStrings = new TreeSet<>();

		for (String loadedSource : PropsUtil.getLoadedSources()) {
			try {
				URI uri = new URI(loadedSource);

				if (StringUtil.equals("file", uri.getScheme())) {
					String propertiesFilePathString = String.valueOf(
						Paths.get(uri));

					if (FileUtil.exists(propertiesFilePathString)) {
						propertiesFilePathStrings.add(propertiesFilePathString);
					}
				}
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(exception);
				}
			}
		}

		return propertiesFilePathStrings;
	}

	private Map<String, Object> _getReportData(
		UpgradeRecorder upgradeRecorder) {

		Set<String> propertiesFilePathStrings = _getPropertiesFilePathStrings();

		return LinkedHashMapBuilder.<String, Object>put(
			"execution.date", _executionDateString
		).put(
			"execution.time", _executionTimeString
		).put(
			"result", upgradeRecorder.getResult()
		).put(
			"status",
			() -> {
				ReleaseManager releaseManager = _releaseManagerSnapshot.get();

				if (releaseManager == null) {
					if (upgradeRecorder.isPreupgradeVerifyFailure()) {
						return "No changes have been made to the system";
					}

					return "Upgrade failed to complete";
				}

				String statusMessage = releaseManager.getStatusMessage(false);

				if (statusMessage.isEmpty()) {
					return "There are no pending upgrades";
				}

				return statusMessage;
			}
		).put(
			"type", upgradeRecorder.getType()
		).put(
			"portal",
			LinkedHashMapBuilder.put(
				"initial.build.number",
				(_initialBuildNumber != 0) ?
					String.valueOf(_initialBuildNumber) : "Unable to determine"
			).put(
				"initial.schema.version",
				() -> {
					String initialSchemaVersion =
						upgradeRecorder.getInitialSchemaVersion(
							ReleaseConstants.DEFAULT_SERVLET_CONTEXT_NAME);

					if ((initialSchemaVersion != null) &&
						!initialSchemaVersion.equals("0.0.0")) {

						return initialSchemaVersion;
					}

					return "Unable to determine";
				}
			).put(
				"final.build.number",
				() -> {
					int buildNumber = _getBuildNumber();

					if (buildNumber != 0) {
						return String.valueOf(buildNumber);
					}

					return "Unable to determine";
				}
			).put(
				"final.schema.version",
				() -> {
					String schemaVersion =
						upgradeRecorder.getFinalSchemaVersion(
							ReleaseConstants.DEFAULT_SERVLET_CONTEXT_NAME);

					if (schemaVersion != null) {
						return schemaVersion;
					}

					return "Unable to determine";
				}
			).put(
				"expected.build.number",
				() -> {
					int buildNumber = ReleaseInfo.getBuildNumber();

					if (buildNumber != 0) {
						return String.valueOf(buildNumber);
					}

					return "Unable to determine";
				}
			).put(
				"expected.schema.version",
				() -> {
					String schemaVersion = String.valueOf(
						PortalUpgradeProcess.getLatestSchemaVersion());

					if (schemaVersion != null) {
						return schemaVersion;
					}

					return "Unable to determine";
				}
			).build()
		).put(
			"database.version",
			() -> {
				DB db = DBManagerUtil.getDB();

				return StringBundler.concat(
					db.getDBType(), StringPool.SPACE, db.getMajorVersion(),
					StringPool.PERIOD, db.getMinorVersion());
			}
		).put(
			"document.library",
			LinkedHashMapBuilder.put(
				"root.directory", (_rootDir != null) ? _rootDir : "Undefined"
			).put(
				"storage.implementation", PropsValues.DL_STORE_IMPL
			).put(
				"storage.size",
				() -> {
					if (PropsValues.UPGRADE_REPORT_DL_STORAGE_SIZE_TIMEOUT ==
							0) {

						return "Disabled";
					}

					if (!StringUtil.endsWith(
							PropsValues.DL_STORE_IMPL, "FileSystemStore")) {

						return "Check externally";
					}

					if (_rootDir == null) {
						return "Unable to determine. Document library " +
							"\"rootDir\" was not set";
					}

					_dlSize = 0;

					try {
						_dlSizeThread.start();
						_dlSizeThread.join(
							PropsValues.UPGRADE_REPORT_DL_STORAGE_SIZE_TIMEOUT *
								Time.SECOND);
					}
					catch (Exception exception) {
						_log.error(
							"Unable to determine the document library size",
							exception);

						return "Unable to determine";
					}

					if (_dlSizeThread.isAlive()) {
						if (_log.isInfoEnabled()) {
							_log.info(
								"Unable to determine the document library " +
									"size. Increase the timeout or check it " +
										"manually.");
						}

						return "Unable to determine";
					}

					return LanguageUtil.formatStorageSize(
						_dlSize, LocaleUtil.US);
				}
			).build()
		).put(
			"liferay.home", PropsValues.LIFERAY_HOME
		).put(
			"jvm.arguments",
			() -> {
				List<String> jvmArguments = new ArrayList<>();

				String[] keywords = {
					"password", "secret", "securitycredential"
				};

				RuntimeMXBean runtimeMXBean =
					ManagementFactory.getRuntimeMXBean();

				for (String inputArgument : runtimeMXBean.getInputArguments()) {
					if (!inputArgument.startsWith("-D") ||
						!inputArgument.contains(StringPool.EQUAL)) {

						jvmArguments.add(inputArgument);

						continue;
					}

					String keyValueString = inputArgument.substring(2);

					String[] keyValue = keyValueString.split(
						StringPool.EQUAL, 2);

					String key = keyValue[0];
					String value = keyValue[1];

					for (String keyword : keywords) {
						if (StringUtil.containsIgnoreCase(
								key, keyword, StringPool.BLANK)) {

							value = StringPool.EIGHT_STARS;

							break;
						}
					}

					jvmArguments.add(
						StringBundler.concat(
							"-D", key, StringPool.EQUAL, value));
				}

				return ListUtil.sort(jvmArguments);
			}
		).put(
			"properties",
			() -> {
				Map<String, String> propertiesMap = new TreeMap<>();

				for (String propertiesFilePathString :
						propertiesFilePathStrings) {

					Properties properties = new Properties();

					try (InputStream inputStream = new FileInputStream(
							propertiesFilePathString)) {

						properties.load(inputStream);
					}
					catch (IOException ioException) {
						if (_log.isWarnEnabled()) {
							_log.warn(
								"Unable to load properties file from: " +
									propertiesFilePathString,
								ioException);
						}

						continue;
					}

					for (String key : properties.stringPropertyNames()) {
						propertiesMap.put(key, PropsUtil.get(key));
					}
				}

				String envPrefix = "LIFERAY_";

				Map<String, String> env = System.getenv();

				for (Map.Entry<String, String> entry : env.entrySet()) {
					String key = entry.getKey();

					if (!key.startsWith(envPrefix)) {
						continue;
					}

					propertiesMap.put(
						EnvPropertiesUtil.decode(
							StringUtil.toLowerCase(
								key.substring(envPrefix.length()))),
						entry.getValue());
				}

				List<PropertyPrinter> propertyPrinters = new ArrayList<>();

				for (Map.Entry<String, String> entry :
						propertiesMap.entrySet()) {

					propertyPrinters.add(
						new PropertyPrinter(entry.getKey(), entry.getValue()));
				}

				return propertyPrinters;
			}
		).put(
			"properties.files", propertiesFilePathStrings
		).put(
			"tables.initial.final.rows",
			() -> {
				Map<String, Integer> finalTableCounts = _getTableCounts();

				if ((finalTableCounts == null) ||
					(_initialTableCounts == null)) {

					return null;
				}

				List<String> tableNames = new ArrayList<>();

				tableNames.addAll(_initialTableCounts.keySet());
				tableNames.addAll(finalTableCounts.keySet());

				ListUtil.distinct(
					tableNames,
					(tableName1, tableName2) -> {
						int initialTableCount1 =
							_initialTableCounts.getOrDefault(tableName1, 0);
						int initialTableCount2 =
							_initialTableCounts.getOrDefault(tableName2, 0);

						if (initialTableCount1 != initialTableCount2) {
							return initialTableCount2 - initialTableCount1;
						}

						return tableName1.compareTo(tableName2);
					});

				return TransformUtil.transform(
					tableNames,
					tableName -> {
						int finalTableCount = finalTableCounts.getOrDefault(
							tableName, -1);
						int initialTableCount =
							_initialTableCounts.getOrDefault(tableName, -1);

						if ((finalTableCount <= 0) &&
							(initialTableCount <= 0)) {

							return null;
						}

						return new TablePrinter(
							(finalTableCount >= 0) ?
								String.valueOf(finalTableCount) :
									StringPool.DASH,
							(initialTableCount >= 0) ?
								String.valueOf(initialTableCount) :
									StringPool.DASH,
							tableName);
					});
			}
		).build();
	}

	private Map<String, Object> _getReportDataDiagnostics(
		UpgradeRecorder upgradeRecorder) {

		return LinkedHashMapBuilder.<String, Object>put(
			"execution.date", _executionDateString
		).put(
			"execution.time", _executionTimeString
		).put(
			"data.clean.up",
			_getMessagesPrinters(
				false, upgradeRecorder.getDataCleanUpMessages())
		).put(
			"errors",
			_getMessagesPrinters(true, upgradeRecorder.getErrorMessages())
		).put(
			"failed.sqls", UpgradeSQLRecorder.getFailedSQLs()
		).put(
			"longest.upgrade.processes",
			() -> {
				Map<String, ArrayList<String>> eventMessages =
					upgradeRecorder.getUpgradeProcessMessages();

				List<String> messages = eventMessages.get(
					UpgradeProcess.class.getName());

				if (ListUtil.isEmpty(messages)) {
					return new ArrayList<>();
				}

				Map<String, Long> upgradeProcessDurations = new HashMap<>();

				for (String message : messages) {
					String[] parts = StringUtil.split(
						message, StringPool.SPACE);

					String upgradeProcessClassName = parts[3];

					if (upgradeProcessClassName.equals(
							PortalUpgradeProcess.class.getName())) {

						continue;
					}

					long duration = GetterUtil.getLong(parts[parts.length - 2]);

					if (duration >=
							PropsValues.
								UPGRADE_REPORT_UPGRADE_PROCESS_THRESHOLD) {

						upgradeProcessDurations.put(
							upgradeProcessClassName, duration);
					}
				}

				List<RunningUpgradeProcess> longestRunningUpgradeProcesses =
					new ArrayList<>();

				int count = 0;

				for (Map.Entry<String, Long> entry :
						ListUtil.sort(
							new ArrayList<>(upgradeProcessDurations.entrySet()),
							Collections.reverseOrder(
								Map.Entry.comparingByValue(Long::compare)))) {

					longestRunningUpgradeProcesses.add(
						new RunningUpgradeProcess(
							String.valueOf(entry.getValue()), entry.getKey()));

					count++;

					if (count >= _LONGEST_UPGRADE_PROCESSES_COUNT) {
						break;
					}
				}

				return longestRunningUpgradeProcesses;
			}
		).put(
			"longest.running.sqls",
			() -> {
				List<UpgradeSQLRecorder.RunningSQL> runningSQLs =
					new ArrayList<>(UpgradeSQLRecorder.getRunningSQLs());

				runningSQLs.sort(
					(entry1, entry2) -> Long.compare(
						entry2.getDuration(), entry1.getDuration()));

				return ListUtil.subList(
					runningSQLs, 0,
					Math.min(_LONGEST_RUNNING_SQLS_COUNT, runningSQLs.size()));
			}
		).put(
			"warnings",
			_getMessagesPrinters(true, upgradeRecorder.getWarningMessages())
		).build();
	}

	private File _getReportFile(String reportFileName) {
		File reportsDir = null;

		if (!Validator.isBlank(PropsValues.UPGRADE_REPORT_DIR)) {
			reportsDir = new File(PropsValues.UPGRADE_REPORT_DIR);

			if ((!reportsDir.exists() && !reportsDir.mkdir()) ||
				!Files.isWritable(reportsDir.toPath())) {

				reportsDir = null;

				if (_log.isWarnEnabled()) {
					_log.warn(
						"Unable to generate the upgrade report at " +
							PropsValues.UPGRADE_REPORT_DIR);
				}
			}
		}

		if (reportsDir == null) {
			if (DBUpgrader.isUpgradeClient()) {
				reportsDir = new File(".", "reports");
			}
			else {
				reportsDir = new File(PropsValues.LIFERAY_HOME, "reports");
			}

			if (!reportsDir.exists()) {
				reportsDir.mkdirs();
			}
		}

		File reportFile = new File(reportsDir, reportFileName);

		if (reportFile.exists()) {
			reportFile.renameTo(
				new File(
					reportsDir,
					reportFileName + "." + reportFile.lastModified()));

			reportFile = new File(reportsDir, reportFileName);
		}

		return reportFile;
	}

	private String _getReportHeader(String key) {
		if (key.equals("longest.running.sqls")) {
			return String.format(
				"Top %d longest running SQLs above %d milliseconds",
				_LONGEST_RUNNING_SQLS_COUNT,
				PropsValues.UPGRADE_REPORT_SQL_STATEMENT_THRESHOLD);
		}

		if (key.equals("longest.upgrade.processes")) {
			return String.format(
				"Top %d longest upgrade processes above %d milliseconds",
				_LONGEST_UPGRADE_PROCESSES_COUNT,
				PropsValues.UPGRADE_REPORT_UPGRADE_PROCESS_THRESHOLD);
		}

		if (key.startsWith("tables.")) {
			return String.format(
				TablePrinter.FORMAT, "Table Name", "Initial Rows",
				"Final Rows");
		}

		return StringUtil.replace(
			StringUtil.upperCaseFirstLetter(key), '.', ' ');
	}

	private String _getReportLine(String key, Object value) {
		return StringBundler.concat(
			_getReportHeader(key), StringPool.COLON, StringPool.SPACE, value);
	}

	private String _getRootDir() {
		String rootDir = null;

		try {
			PersistenceManager persistenceManager =
				_persistenceManagerSnapshot.get();

			String dlStoreConfigurationPid = StringPool.BLANK;

			if (StringUtil.equals(
					PropsValues.DL_STORE_IMPL,
					"com.liferay.portal.store.file.system." +
						"AdvancedFileSystemStore")) {

				dlStoreConfigurationPid =
					_CONFIGURATION_PID_ADVANCED_FILE_SYSTEM_STORE;
			}
			else if (StringUtil.equals(
						PropsValues.DL_STORE_IMPL,
						"com.liferay.portal.store.file.system." +
							"FileSystemStore")) {

				dlStoreConfigurationPid = _CONFIGURATION_PID_FILE_SYSTEM_STORE;
			}

			Dictionary<String, String> configurations = persistenceManager.load(
				dlStoreConfigurationPid);

			if (configurations != null) {
				rootDir = configurations.get("rootDir");
			}

			if (rootDir == null) {
				rootDir = PropsValues.LIFERAY_HOME + "/data/document_library";
			}

			return rootDir;
		}
		catch (IOException ioException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to get document library store root dir",
					ioException);
			}
		}

		return null;
	}

	private Map<String, Integer> _getTableCounts() {
		try (Connection connection = DataAccess.getConnection()) {
			DatabaseMetaData databaseMetaData = connection.getMetaData();

			DBInspector dbInspector = new DBInspector(connection);

			try (ResultSet resultSet1 = databaseMetaData.getTables(
					dbInspector.getCatalog(), dbInspector.getSchema(), null,
					new String[] {"TABLE"})) {

				Map<String, Integer> tableCounts = new HashMap<>();

				while (resultSet1.next()) {
					String tableName = resultSet1.getString("TABLE_NAME");

					try (PreparedStatement preparedStatement =
							connection.prepareStatement(
								"select count(*) from " + tableName);
						ResultSet resultSet2 =
							preparedStatement.executeQuery()) {

						if (resultSet2.next()) {
							tableCounts.put(tableName, resultSet2.getInt(1));
						}
					}
					catch (SQLException sqlException) {
						if (_log.isWarnEnabled()) {
							_log.warn(
								"Unable to retrieve data from " + tableName,
								sqlException);
						}
					}
				}

				return tableCounts;
			}
		}
		catch (SQLException sqlException) {
			if (_log.isDebugEnabled()) {
				_log.debug(sqlException);
			}

			return null;
		}
	}

	private void _printToLogContext(Map.Entry<String, Object> entry1) {
		Object value = entry1.getValue();

		if (value == null) {
			return;
		}

		String key = "upgrade.report." + entry1.getKey();

		if (value instanceof List<?>) {
			StringBundler sb = new StringBundler(StringPool.OPEN_BRACKET);

			List<?> list = (List<?>)value;

			for (Object object : list) {
				if (object instanceof UpgradeSQLRecorder.FailedSQL) {
					UpgradeSQLRecorder.FailedSQL failedSQL =
						(UpgradeSQLRecorder.FailedSQL)object;

					sb.append(failedSQL.getSQL());

					sb.append(StringPool.COLON);
					sb.append(failedSQL.getMessage());
				}
				else if (object instanceof UpgradeSQLRecorder.RunningSQL) {
					UpgradeSQLRecorder.RunningSQL runningSQL =
						(UpgradeSQLRecorder.RunningSQL)object;

					sb.append(runningSQL.getUpgradeProcessClassName());

					sb.append(StringPool.COLON);
					sb.append(runningSQL.getSQL());
					sb.append(StringPool.COLON);
					sb.append(runningSQL.getDuration());
					sb.append(" ms");
				}
				else {
					sb.append(String.valueOf(object));
				}

				sb.append(StringPool.COMMA_AND_SPACE);
			}

			if (sb.length() > 1) {
				sb.setIndex(sb.index() - 1);
			}

			sb.append(StringPool.CLOSE_BRACKET);

			ThreadContext.put(key, sb.toString());
		}
		else if (value instanceof Map<?, ?>) {
			Map<?, ?> map = (Map<?, ?>)value;

			for (Map.Entry<?, ?> entry2 : map.entrySet()) {
				ThreadContext.put(
					key + StringPool.PERIOD + entry2.getKey(),
					String.valueOf(entry2.getValue()));
			}
		}
		else {
			ThreadContext.put(key, String.valueOf(value));
		}
	}

	private void _printToLogContext(Map<String, Object> reportData) {
		if (!PropsValues.UPGRADE_LOG_CONTEXT_ENABLED) {
			return;
		}

		_logContext = true;

		try {
			for (Map.Entry<String, Object> entry1 : reportData.entrySet()) {
				_printToLogContext(entry1);
			}
		}
		finally {
			_logContext = false;
		}
	}

	private void _writeToFile(
		Map<String, Object> reportData, String reportFileName) {

		StringBundler sb = new StringBundler();

		for (Map.Entry<String, Object> entry1 : reportData.entrySet()) {
			Object value = entry1.getValue();

			if (value == null) {
				continue;
			}

			String key = entry1.getKey();

			if (value instanceof Collection<?>) {
				String reportHeader = _getReportHeader(key);

				sb.append(reportHeader);

				Collection<Object> objects = (Collection<Object>)value;

				if (objects.isEmpty()) {
					sb.append(": Nothing registered\n");
				}
				else {
					sb.append(StringPool.NEW_LINE);
					sb.append(
						ListUtil.toString(
							Collections.nCopies(
								reportHeader.length(), StringPool.MINUS),
							StringPool.NULL, StringPool.BLANK));
					sb.append(StringPool.NEW_LINE);

					for (Object object : (Collection<Object>)value) {
						sb.append(object.toString());
						sb.append(StringPool.NEW_LINE);
					}
				}
			}
			else if (value instanceof Map<?, ?>) {
				Map<?, ?> map = (Map<?, ?>)value;

				for (Map.Entry<?, ?> entry2 : map.entrySet()) {
					sb.append(
						_getReportLine(
							key + StringPool.PERIOD + entry2.getKey(),
							entry2.getValue()));
					sb.append(StringPool.NEW_LINE);
				}
			}
			else {
				sb.append(_getReportLine(key, value));
				sb.append(StringPool.NEW_LINE);
			}

			sb.append(StringPool.NEW_LINE);
		}

		File reportFile = null;

		try {
			reportFile = _getReportFile(reportFileName);

			FileUtil.write(
				reportFile,
				StringUtil.merge(
					new String[] {sb.toString()},
					StringPool.NEW_LINE + StringPool.NEW_LINE));

			if (_log.isInfoEnabled()) {
				_log.info(
					"Upgrade report generated in " +
						reportFile.getAbsolutePath());
			}
		}
		catch (IOException ioException) {
			_log.error(
				"Unable to generate the upgrade report in " +
					reportFile.getAbsolutePath(),
				ioException);
		}
		finally {
			if (PropsValues.UPGRADE_LOG_CONTEXT_ENABLED) {
				ThreadContext.clearMap();
			}
		}
	}

	private static final String _CONFIGURATION_PID_ADVANCED_FILE_SYSTEM_STORE =
		"com.liferay.portal.store.file.system.configuration." +
			"AdvancedFileSystemStoreConfiguration";

	private static final String _CONFIGURATION_PID_FILE_SYSTEM_STORE =
		"com.liferay.portal.store.file.system.configuration." +
			"FileSystemStoreConfiguration";

	private static final int _LONGEST_RUNNING_SQLS_COUNT = 20;

	private static final int _LONGEST_UPGRADE_PROCESSES_COUNT = 20;

	private static final Log _log = LogFactoryUtil.getLog(UpgradeReport.class);

	private static boolean _logContext;
	private static final Snapshot<PersistenceManager>
		_persistenceManagerSnapshot = new Snapshot<>(
			UpgradeReport.class, PersistenceManager.class);
	private static final Snapshot<ReleaseManager> _releaseManagerSnapshot =
		new Snapshot<>(UpgradeReport.class, ReleaseManager.class);

	private double _dlSize;
	private final Thread _dlSizeThread = new DLSizeThread();
	private String _executionDateString;
	private String _executionTimeString;
	private final int _initialBuildNumber;
	private Map<String, Integer> _initialTableCounts;
	private String _rootDir;

	private class DLSizeThread extends Thread {

		@Override
		public void run() {
			_dlSize = FileUtils.sizeOfDirectory(new File(_rootDir));
		}

	}

	private class MessagesPrinter {

		public MessagesPrinter(String className) {
			_className = className;
		}

		public void addMessagePrinter(String message, Integer occurrences) {
			_messagePrinters.add(new MessagePrinter(message, occurrences));
		}

		@Override
		public String toString() {
			if (_logContext) {
				return _className + StringPool.COLON +
					_messagePrinters.toString();
			}

			StringBundler sb = new StringBundler();

			sb.append("Class name: ");
			sb.append(_className);
			sb.append(StringPool.NEW_LINE);

			for (MessagePrinter messagePrinter : _messagePrinters) {
				sb.append(StringPool.TAB);
				sb.append(messagePrinter.toString());
				sb.append(StringPool.NEW_LINE);
			}

			return sb.toString();
		}

		private final String _className;
		private final List<MessagePrinter> _messagePrinters = new ArrayList<>();

		private class MessagePrinter {

			public MessagePrinter(String message, Integer occurrences) {
				_message = message;
				_occurrences = occurrences;
			}

			@Override
			public String toString() {
				if (_occurrences != null) {
					if (_logContext) {
						return _occurrences + StringPool.COLON + _message;
					}

					return StringBundler.concat(
						_occurrences, " occurrences of the following event: ",
						_message);
				}

				return _message;
			}

			private final String _message;
			private final Integer _occurrences;

		}

	}

	private class PropertyPrinter {

		public PropertyPrinter(String key, String value) {
			_key = key;

			if (ArrayUtil.contains(
					PropsValues.ADMIN_OBFUSCATED_PROPERTIES, key)) {

				_value = StringPool.EIGHT_STARS;
			}
			else {
				_value = value;
			}
		}

		public String toString() {
			return _key + StringPool.EQUAL + _value;
		}

		private final String _key;
		private final String _value;

	}

	private class RunningUpgradeProcess {

		public RunningUpgradeProcess(
			String timeDescription, String upgradeProcessClassName) {

			_timeDescription = timeDescription;
			_upgradeProcessClassName = upgradeProcessClassName;
		}

		@Override
		public String toString() {
			if (_logContext) {
				return StringBundler.concat(
					_upgradeProcessClassName, StringPool.COLON,
					_timeDescription, " ms");
			}

			return StringBundler.concat(
				_upgradeProcessClassName, " took ", _timeDescription,
				" ms to complete\n");
		}

		private final String _timeDescription;
		private final String _upgradeProcessClassName;

	}

	private class TablePrinter {

		public static final String FORMAT = "%-30s %20s %20s";

		public TablePrinter(
			String finalTableCount, String initialTableCount,
			String tableName) {

			_finalTableCount = finalTableCount;
			_initialTableCount = initialTableCount;
			_tableName = tableName;
		}

		@Override
		public String toString() {
			if (_logContext) {
				return StringBundler.concat(
					_tableName, StringPool.COLON, _initialTableCount,
					StringPool.COLON, _finalTableCount);
			}

			return String.format(
				FORMAT, _tableName, _initialTableCount, _finalTableCount);
		}

		private final String _finalTableCount;
		private final String _initialTableCount;
		private final String _tableName;

	}

}