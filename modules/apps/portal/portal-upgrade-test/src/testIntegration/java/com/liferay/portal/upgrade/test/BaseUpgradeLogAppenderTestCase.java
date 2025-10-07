/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.test;

import com.liferay.petra.concurrent.DCLSingleton;
import com.liferay.petra.io.unsync.UnsyncStringWriter;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.db.partition.util.DBPartitionUtil;
import com.liferay.portal.events.StartupHelperUtil;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DuplicateUniqueFinderRowsCleaner;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.db.partition.DBPartition;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.model.ReleaseConstants;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ReleaseLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.upgrade.DeleteDuplicateUniqueFinderRowsUpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeException;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.kernel.upgrade.data.cleanup.DataCleanupPreupgradeProcess;
import com.liferay.portal.kernel.upgrade.data.cleanup.util.OrphanReferencesDataCleanupUtil;
import com.liferay.portal.kernel.upgrade.recorder.UpgradeSQLRecorder;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.ReleaseInfo;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.version.Version;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.tools.DBUpgrader;
import com.liferay.portal.upgrade.PortalUpgradeProcess;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Field;

import java.net.URI;

import java.sql.Connection;
import java.sql.PreparedStatement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.appender.WriterAppender;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.message.SimpleMessage;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author Sam Ziemer
 */
public abstract class BaseUpgradeLogAppenderTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@AfterClass
	public static void tearDownClass() throws Exception {
		DBPartitionUtil.forEachCompanyId(
			companyId -> {
				_db.runSQL("DROP_TABLE_IF_EXISTS(UpgradeReportTable1)");
				_db.runSQL("DROP_TABLE_IF_EXISTS(UpgradeReportTable2)");
			});

		ReflectionTestUtil.setFieldValue(
			DBUpgrader.class, "_upgradeClient", _originalUpgradeClient);
		ReflectionTestUtil.setFieldValue(
			PropsValues.class, "UPGRADE_LOG_CONTEXT_ENABLED",
			_originalUpgradeLogContextEnabled);

		_restoreRelease();
	}

	@Before
	public void setUp() throws Exception {
		PatternLayout.Builder builder = PatternLayout.newBuilder();

		builder.withPattern("%level - %m%n %X");

		_logContextAppender = WriterAppender.createAppender(
			builder.build(), null, _unsyncStringWriter,
			"logContextWriterAppender", false, false);

		_logContextAppender.start();

		ReflectionTestUtil.setFieldValue(
			StartupHelperUtil.class, "_newRelease", true);

		_updatePortalRelease(
			new Version(1, 0, 0), ReleaseInfo.RELEASE_7_1_0_BUILD_NUMBER);

		_upgradeReportLogger = (Logger)LogManager.getLogger(
			"com.liferay.portal.upgrade.internal.report.UpgradeReport");

		_upgradeReportLogger.addAppender(_logContextAppender);
	}

	@After
	public void tearDown() throws Exception {
		_appender.stop();

		File reportsDir = null;

		if (_upgradeReportDir.isEmpty()) {
			reportsDir = new File(getFilePath(), "reports");
		}
		else {
			reportsDir = new File(_upgradeReportDir);

			_upgradeReportDir = "";
		}

		if (reportsDir.exists()) {
			File reportFile = new File(reportsDir, "upgrade_report.txt");

			if (reportFile.exists()) {
				reportFile.delete();
			}

			reportFile = new File(reportsDir, "upgrade_report_diagnostics.txt");

			if (reportFile.exists()) {
				reportFile.delete();
			}

			reportsDir.delete();
		}

		_upgradeReportLogger.removeAppender(_logContextAppender);

		_logContextAppender.stop();
	}

	@Test
	public void testDatabaseTablesAreSorted() throws Exception {
		_appender.start();

		_appender.stop();

		if (_reportContent == null) {
			_reportContent = _getReportContent();
		}

		_assertTablesAreSortedByInitialRows(
			_logContextTablesInitialFinalRowsPattern.matcher(
				_getLogContextValue(
					"upgrade.report.tables.initial.final.rows")));
		_assertTablesAreSortedByInitialRows(_pattern.matcher(_reportContent));
	}

	@Test
	public void testDatabaseTablesCounts() throws Exception {
		_db.runSQL("insert into UpgradeReportTable2 (id_) values (1)");

		_appender.start();

		_db.runSQL("insert into UpgradeReportTable1 (id_) values (1)");

		_db.runSQL("delete from UpgradeReportTable2 where id_ = 1");

		_appender.stop();

		if (_reportContent == null) {
			_reportContent = _getReportContent();
		}

		Matcher matcher = _pattern.matcher(_reportContent);

		boolean table1Exists = false;
		boolean table2Exists = false;

		while (matcher.find()) {
			String tableName = matcher.group(1);

			int initialTableCount = GetterUtil.getInteger(matcher.group(2), -1);
			int finalTableCount = GetterUtil.getInteger(matcher.group(3), -1);

			if (StringUtil.equalsIgnoreCase(tableName, "UpgradeReportTable1")) {
				table1Exists = true;

				Assert.assertEquals(0, initialTableCount);
				Assert.assertEquals(1, finalTableCount);
			}
			else if (StringUtil.equalsIgnoreCase(
						tableName, "UpgradeReportTable2")) {

				table2Exists = true;

				Assert.assertEquals(1, initialTableCount);
				Assert.assertEquals(0, finalTableCount);
			}
			else {
				Assert.assertTrue(
					(initialTableCount > 0) || (finalTableCount > 0));
			}
		}

		Assert.assertTrue(table1Exists && table2Exists);

		_assertLogContext(
			"upgrade.report.tables.initial.final.rows",
			"UpgradeReportTable1:0:1");
		_assertLogContext(
			"upgrade.report.tables.initial.final.rows",
			"UpgradeReportTable2:1:0");
	}

	@Test
	public void testDatabaseTablesEmpty() throws Exception {
		ReflectionTestUtil.setFieldValue(
			StartupHelperUtil.class, "_newRelease", false);

		_appender.start();

		_appender.stop();

		String content = _getReportContent();

		Assert.assertFalse(content.contains("Table Name"));
	}

	@Test
	public void testDataCleanupMessages() throws Exception {
		Thread currentThread = Thread.currentThread();

		long randomCompanyId = RandomTestUtil.nextLong();

		try (AutoCloseable autoCloseable =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					PortalClassLoaderUtil.class, "_classLoader",
					currentThread.getContextClassLoader());
			Connection connection = DataAccess.getConnection()) {

			_db.runSQL(
				StringBundler.concat(
					"insert into Portlet (mvccVersion, id_, companyId, ",
					"portletId, active_) values (0, ",
					RandomTestUtil.nextLong(), ", ", randomCompanyId, ", '",
					RandomTestUtil.randomString(), "', [$FALSE$])"));

			_appender.start();

			UpgradeProcess upgradeProcess =
				new TestDataCleanupPreupgradeProcess();

			upgradeProcess.upgrade();

			upgradeProcess =
				new TestDeleteDuplicateUniqueFinderRowsUpgradeProcess(
					RandomTestUtil.randomString(),
					new String[] {RandomTestUtil.randomString()},
					RandomTestUtil.randomString());

			upgradeProcess.upgrade();

			OrphanReferencesDataCleanupUtil.cleanUpTable(
				connection, null, "companyId", "Portlet",
				new String[] {"companyId"}, "Company");

			_appender.stop();

			DBInspector dbInspector = new DBInspector(connection);

			_assertLogContextDiagnostics(
				"upgrade.report.data.clean.up", _CLEANUP_INFO_MESSAGE);
			_assertLogContextDiagnostics(
				"upgrade.report.data.clean.up", _CLEANUP_WARNING_MESSAGE);
			_assertLogContextDiagnostics(
				"upgrade.report.data.clean.up",
				_DELETE_DUPLICATES_FINDER_WARNING_MESSAGE);
			_assertLogContextDiagnostics(
				"upgrade.report.data.clean.up",
				StringBundler.concat(
					"Table ", dbInspector.normalizeName("Portlet"),
					", 1 row deleted because ",
					dbInspector.normalizeName("companyId"), StringPool.SPACE,
					randomCompanyId, " was not found in column ",
					dbInspector.normalizeName("companyId"), " from table ",
					dbInspector.normalizeName("Company")));
		}
		finally {
			_db.runSQL(
				"delete from Portlet where companyId = " + randomCompanyId);
		}
	}

	@Test
	public void testDLAndLiferayHome() throws Exception {
		_appender.start();

		_appender.stop();

		_assertLogContext(
			"upgrade.report.document.library.root.directory",
			PropsValues.LIFERAY_HOME + "/data/document_library");
		_assertLogContext(
			"upgrade.report.document.library.storage.implementation",
			PropsValues.DL_STORE_IMPL);
		_assertLogContext(
			"upgrade.report.liferay.home", PropsValues.LIFERAY_HOME);

		_assertReport(
			"Document library root directory: " + PropsValues.LIFERAY_HOME +
				"/data/document_library");
		_assertReport(
			"Document library storage implementation: " +
				PropsValues.DL_STORE_IMPL);
		_assertReport("Liferay home: " + PropsValues.LIFERAY_HOME);
	}

	@Test
	public void testDLStorageSizeAfterTimeout() throws Exception {
		_appender.start();

		try (SafeCloseable safeCloseable =
				_setUpgradeReportDLStorageSizeTimeout(1)) {

			Object upgradeReport = ReflectionTestUtil.getFieldValue(
				_appender, "_upgradeReport");

			ReflectionTestUtil.setFieldValue(
				upgradeReport, "_dlSizeThread",
				new Thread() {

					@Override
					public void run() {
						try {
							sleep(5 * Time.SECOND);
						}
						catch (InterruptedException interruptedException) {
							throw new RuntimeException(interruptedException);
						}
					}

				});

			_appender.stop();

			Assert.assertTrue(
				_getLogContent().contains(
					"INFO - Unable to determine the document library size. " +
						"Increase the timeout or check it manually."));
			_assertLogContext(
				"upgrade.report.document.library.storage.size",
				"Unable to determine");
			_assertReport("Document library storage size: Unable to determine");
		}
	}

	@Test
	public void testDLStorageSizeDisabled() throws Exception {
		_appender.start();

		try (SafeCloseable safeCloseable =
				_setUpgradeReportDLStorageSizeTimeout(0)) {

			_appender.stop();

			_assertLogContext(
				"upgrade.report.document.library.storage.size", "Disabled");
			_assertReport("Document library storage size: Disabled");
		}
	}

	@Test
	public void testDLStorageSizeInGb() throws Exception {
		_appender.start();

		Object upgradeReport = ReflectionTestUtil.getFieldValue(
			_appender, "_upgradeReport");

		ReflectionTestUtil.setFieldValue(
			upgradeReport, "_dlSizeThread",
			new Thread() {

				@Override
				public void run() {
					ReflectionTestUtil.setFieldValue(
						upgradeReport, "_dlSize", 1073742000);
				}

			});

		_appender.stop();

		String size = LanguageUtil.formatStorageSize(1073742000, LocaleUtil.US);

		_assertLogContext("upgrade.report.document.library.storage.size", size);
		_assertReport("Document library storage size: " + size);
	}

	@Test
	public void testDLStorageSizeInMb() throws Exception {
		_appender.start();

		Object upgradeReport = ReflectionTestUtil.getFieldValue(
			_appender, "_upgradeReport");

		ReflectionTestUtil.setFieldValue(
			upgradeReport, "_dlSizeThread",
			new Thread() {

				@Override
				public void run() {
					ReflectionTestUtil.setFieldValue(
						upgradeReport, "_dlSize", 1048576);
				}

			});

		_appender.stop();

		String size = LanguageUtil.formatStorageSize(1048576, LocaleUtil.US);

		_assertLogContext("upgrade.report.document.library.storage.size", size);
		_assertReport("Document library storage size: " + size);
	}

	@Test
	public void testFailedSQLStatements() throws Exception {
		_appender.start();

		UpgradeProcess upgradeProcess = UpgradeProcessFactory.runSQL(
			"update NonexistingTable");

		try {
			upgradeProcess.upgrade();
		}
		catch (UpgradeException upgradeException) {
		}

		_appender.stop();

		_assertLogContextDiagnostics(
			"upgrade.report.failed.sqls", "update NonexistingTable;");
		_assertReportDiagnostics("SQL: update NonexistingTable;");
	}

	@Test
	public void testJVMArguments() throws Exception {
		_appender.start();

		_appender.stop();

		RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();

		List<String> inputArguments = runtimeMXBean.getInputArguments();

		_assertLogContext(
			"upgrade.report.jvm.arguments", inputArguments.get(0));

		_assertReport(inputArguments.get(0));
	}

	@Test
	public void testLogEvents() throws Exception {
		_appender.start();

		LogEvent logEvent = Log4jLogEvent.newBuilder(
		).setLoggerName(
			"Warn"
		).setLevel(
			Level.WARN
		).setMessage(
			new SimpleMessage("Warning")
		).build();

		_appender.append(logEvent);
		_appender.append(logEvent);

		Log log = LogFactoryUtil.getLog(UpgradeProcess.class);

		log.info(
			"Completed upgrade process com.liferay.portal.UpgradeTest in " +
				PropsValues.UPGRADE_REPORT_UPGRADE_PROCESS_THRESHOLD + " ms");

		_appender.stop();

		_assertLogContextDiagnostics(
			"upgrade.report.longest.upgrade.processes",
			"com.liferay.portal.UpgradeTest:" +
				PropsValues.UPGRADE_REPORT_UPGRADE_PROCESS_THRESHOLD + " ms");
		_assertLogContextDiagnostics("upgrade.report.warnings", "2:Warning");
		_assertReportDiagnostics(
			"2 occurrences of the following event: Warning");
		_assertReportDiagnostics(
			"com.liferay.portal.UpgradeTest took " +
				PropsValues.UPGRADE_REPORT_UPGRADE_PROCESS_THRESHOLD +
					" ms to complete");
	}

	@Test
	public void testLongestRunningSQLsThreshold() throws Exception {
		String aboveThresholdSQL = null;

		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"UPGRADE_REPORT_SQL_STATEMENT_THRESHOLD", 0L)) {

			_appender.start();

			aboveThresholdSQL =
				"insert into UpgradeReportTable1 (id_) values (2)";

			UpgradeProcess aboveThresholdUpgradeProcess =
				UpgradeProcessFactory.runSQL(aboveThresholdSQL);

			aboveThresholdUpgradeProcess.upgrade();
		}

		String belowThresholdSQL = null;

		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"UPGRADE_REPORT_SQL_STATEMENT_THRESHOLD", 60000L)) {

			belowThresholdSQL = "delete from UpgradeReportTable1 where id_ = 2";

			UpgradeProcess belowThresholdUpgradeProcess =
				UpgradeProcessFactory.runSQL(belowThresholdSQL);

			belowThresholdUpgradeProcess.upgrade();

			_appender.stop();
		}

		String longestRunningSQLs = _getLogContextValueDiagnostics(
			"upgrade.report.longest.running.sqls");

		Assert.assertFalse(longestRunningSQLs.contains(belowThresholdSQL));
		Assert.assertTrue(longestRunningSQLs.contains(aboveThresholdSQL));
	}

	@Test
	public void testLongestUpgradeProcessesThresholdSorted() throws Exception {
		_appender.start();

		Log log = LogFactoryUtil.getLog(UpgradeProcess.class);

		String fasterUpgradeProcessClassName =
			"com.liferay.portal.FasterUpgradeTest";

		log.info(
			StringBundler.concat(
				"Completed upgrade process ", fasterUpgradeProcessClassName,
				" in ", PropsValues.UPGRADE_REPORT_UPGRADE_PROCESS_THRESHOLD,
				" ms"));

		String slowerUpgradeProcessClassName =
			"com.liferay.portal.SlowerUpgradeTest";

		log.info(
			StringBundler.concat(
				"Completed upgrade process ", slowerUpgradeProcessClassName,
				" in ",
				PropsValues.UPGRADE_REPORT_UPGRADE_PROCESS_THRESHOLD + 1,
				" ms"));

		String belowThresholdUpgradeProcessName =
			"com.liferay.portal.BelowThresholdUpgradeTest";

		log.info(
			StringBundler.concat(
				"Completed upgrade process ", belowThresholdUpgradeProcessName,
				" in ",
				PropsValues.UPGRADE_REPORT_UPGRADE_PROCESS_THRESHOLD - 1,
				" ms"));

		_appender.stop();

		String longestUpgradeProcessesValue = _getLogContextValueDiagnostics(
			"upgrade.report.longest.upgrade.processes");

		Assert.assertFalse(
			longestUpgradeProcessesValue.contains(
				belowThresholdUpgradeProcessName));

		int index1 = longestUpgradeProcessesValue.indexOf(
			slowerUpgradeProcessClassName);
		int index2 = longestUpgradeProcessesValue.indexOf(
			fasterUpgradeProcessClassName);

		Assert.assertTrue(index1 < index2);
	}

	@Test
	public void testModuleUpgrades() throws Exception {
		String bundleSymbolicName = "com.liferay.asset.service";

		Release release = _releaseLocalService.fetchRelease(bundleSymbolicName);

		String currentSchemaVersion = release.getSchemaVersion();

		release.setSchemaVersion("0.0.1");

		_releaseLocalService.updateRelease(release);

		_appender.start();

		_appender.stop();

		release = _releaseLocalService.fetchRelease(bundleSymbolicName);

		release.setSchemaVersion(currentSchemaVersion);

		_releaseLocalService.updateRelease(release);

		_assertLogContext(
			"upgrade.report.status",
			StringBundler.concat(
				"There are upgrade processes available for ",
				bundleSymbolicName, " from 0.0.1 to ", currentSchemaVersion));
		_assertReport(
			StringBundler.concat(
				"There are upgrade processes available for ",
				bundleSymbolicName, " from 0.0.1 to ", currentSchemaVersion));
	}

	@Test
	public void testNoLogEvents() throws Exception {
		_appender.start();

		Object upgradeReport = ReflectionTestUtil.getFieldValue(
			_appender, "_upgradeReport");

		_appender.stop();

		_assertLogContextDiagnostics("upgrade.report.errors", "[]");
		_assertLogContextDiagnostics("upgrade.report.failed.sqls", "[]");
		_assertLogContextDiagnostics(
			"upgrade.report.longest.running.sqls", "[]");
		_assertLogContextDiagnostics(
			"upgrade.report.longest.upgrade.processes", "[]");
		_assertLogContextDiagnostics("upgrade.report.warnings", "[]");
		_assertReportDiagnostics("Errors: Nothing registered");
		_assertReportDiagnostics("Failed sqls: Nothing registered");
		_assertReportDiagnostics(
			String.format(
				"Top %d longest running SQLs above %d milliseconds: Nothing " +
					"registered",
				ReflectionTestUtil.getFieldValue(
					upgradeReport, "_LONGEST_RUNNING_SQLS_COUNT"),
				PropsValues.UPGRADE_REPORT_SQL_STATEMENT_THRESHOLD));
		_assertReportDiagnostics(
			String.format(
				"Top %d longest upgrade processes above %d milliseconds: " +
					"Nothing registered",
				ReflectionTestUtil.getFieldValue(
					upgradeReport, "_LONGEST_UPGRADE_PROCESSES_COUNT"),
				PropsValues.UPGRADE_REPORT_UPGRADE_PROCESS_THRESHOLD));

		_assertReportDiagnostics("Warnings: Nothing registered");
	}

	@Test
	public void testNoUpgrade() throws Exception {
		_restoreRelease();

		_appender.start();

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.upgrade.internal.report.UpgradeReport",
				LoggerTestUtil.INFO)) {

			_appender.stop();

			String logEntries = String.valueOf(logCapture.getLogEntries());

			Assert.assertTrue(
				logEntries.contains(
					"Upgrade report was not generated because no upgrade " +
						"processes were executed"));
		}

		File file = new File(
			new File(getFilePath(), "reports"), "upgrade_report.txt");

		Assert.assertTrue(!file.exists());

		file = new File(
			new File(getFilePath(), "reports"),
			"upgrade_report_diagnostics.txt");

		Assert.assertTrue(!file.exists());
	}

	@Test
	public void testPropertiesEnvVariable() throws Exception {
		_setEnv(
			"LIFERAY_MY_PERIOD_ENVIRONMENT_PERIOD_PROPERTY",
			"my environment property value");

		_appender.start();

		_appender.stop();

		_assertLogContext(
			"upgrade.report.properties",
			"my.environment.property=my environment property value");

		_assertReport("my.environment.property=my environment property value");
	}

	@Test
	public void testPropertiesFile() throws Exception {
		List<String> loadedSources = PropsUtil.getLoadedSources();

		File file = temporaryFolder.newFile("test.properties");

		URI uri = file.toURI();

		String loadedSource = "file:" + uri.getPath();

		loadedSources.add(loadedSource);

		Properties properties = new Properties();

		properties.setProperty("my.property", "my property value");

		PropsUtil.set("my.property", "my property value");

		try (Writer writer = new FileWriter(file)) {
			properties.store(writer, null);

			_appender.start();

			_appender.stop();

			_assertLogContext(
				"upgrade.report.properties.files", file.getAbsolutePath());
			_assertLogContext(
				"upgrade.report.properties", "my.property=my property value");
			_assertReport(file.getAbsolutePath());
			_assertReport("my.property=my property value");
		}
		finally {
			loadedSources.remove(loadedSource);
		}
	}

	@Test
	public void testRenameUpgradeReport() throws Exception {
		_appender.start();

		_appender.stop();

		_assertRenameUpgradeReport("upgrade_report.txt");
		_assertRenameUpgradeReport("upgrade_report_diagnostics.txt");
	}

	@Test
	public void testSchemaVersion() throws Exception {
		_appender.start();

		_restoreRelease();

		_appender.stop();

		_assertLogContext(
			"upgrade.report.portal.expected.build.number",
			String.valueOf(ReleaseInfo.getBuildNumber()));

		Version latestSchemaVersion =
			PortalUpgradeProcess.getLatestSchemaVersion();

		_assertLogContext(
			"upgrade.report.portal.expected.schema.version",
			latestSchemaVersion.toString());

		_assertLogContext(
			"upgrade.report.portal.final.build.number",
			String.valueOf(ReleaseInfo.getBuildNumber()));
		_assertLogContext(
			"upgrade.report.portal.final.schema.version",
			latestSchemaVersion.toString());
		_assertLogContext("upgrade.report.portal.initial.build.number", "7100");
		_assertLogContext(
			"upgrade.report.portal.initial.schema.version", "1.0.0");
		_assertReport(
			StringBundler.concat(
				"Portal initial build number: 7100\n",
				"Portal initial schema version: 1.0.0\n",
				"Portal final build number: ", ReleaseInfo.getBuildNumber(),
				"\nPortal final schema version: ", latestSchemaVersion,
				"\nPortal expected build number: ",
				ReleaseInfo.getBuildNumber(),
				"\nPortal expected schema version: ", latestSchemaVersion,
				StringPool.NEW_LINE));
	}

	@Test
	public void testSQLStatementsWithClassNameAndDuration() throws Exception {
		String sql1 = "insert into UpgradeReportTable1 (id_) values (2)";

		UpgradeProcess upgradeProcess1 = UpgradeProcessFactory.runSQL(sql1);

		Class<?> upgradeProcess1Class = upgradeProcess1.getClass();

		List<String> upgradeProcess1ClassNames = new CopyOnWriteArrayList<>();

		String sql2 = "delete from UpgradeReportTable1 where id_ = 2";

		UpgradeProcess upgradeProcess2 = UpgradeProcessFactory.runSQL(sql2);

		Class<?> upgradeProcess2Class = upgradeProcess2.getClass();

		List<String> upgradeProcess2ClassNames = new CopyOnWriteArrayList<>();

		DBPartitionUtil.forEachCompanyId(
			companyId -> {
				if (DBPartition.isPartitionEnabled()) {
					upgradeProcess1ClassNames.add(
						upgradeProcess1Class.getName() + StringPool.AT +
							CompanyThreadLocal.getCompanyId());

					upgradeProcess2ClassNames.add(
						upgradeProcess2Class.getName() + StringPool.AT +
							CompanyThreadLocal.getCompanyId());
				}
				else {
					upgradeProcess1ClassNames.add(
						upgradeProcess1Class.getName());

					upgradeProcess2ClassNames.add(
						upgradeProcess2Class.getName());
				}
			});

		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"UPGRADE_REPORT_SQL_STATEMENT_THRESHOLD", 0L)) {

			_appender.start();

			upgradeProcess1.upgrade();

			upgradeProcess2.upgrade();

			_appender.stop();

			for (String upgradeProcessClassName : upgradeProcess1ClassNames) {
				_assertLogContextDiagnostics(
					"upgrade.report.longest.running.sqls",
					String.format("%s:%s", upgradeProcessClassName, sql1));
				_assertReportDiagnostics(
					String.format(
						"Upgrade Process: %s\nSQL: %s", upgradeProcessClassName,
						sql1));
			}

			for (String upgradeProcessClassName : upgradeProcess2ClassNames) {
				_assertLogContextDiagnostics(
					"upgrade.report.longest.running.sqls",
					String.format("%s:%s", upgradeProcessClassName, sql2));
				_assertReportDiagnostics(
					String.format(
						"Upgrade Process: %s\nSQL: %s", upgradeProcessClassName,
						sql2));
			}

			Set<UpgradeSQLRecorder.RunningSQL> runningSQLs =
				UpgradeSQLRecorder.getRunningSQLs();

			for (UpgradeSQLRecorder.RunningSQL runningSQL : runningSQLs) {
				long duration = runningSQL.getDuration();

				String sql = runningSQL.getSQL();

				String upgradeProcessClassName =
					runningSQL.getUpgradeProcessClassName();

				_assertLogContextDiagnostics(
					"upgrade.report.longest.running.sqls",
					String.format(
						"%s:%s:%d ms", upgradeProcessClassName, sql, duration));
				_assertReportDiagnostics(
					String.format(
						"Upgrade Process: %s\nSQL: %s\nDuration: %d ms",
						upgradeProcessClassName, sql, duration));
			}
		}
	}

	@Test
	public void testUpgradeReportDirectory() throws Exception {
		String originalUpgradeReportDir =
			ReflectionTestUtil.getAndSetFieldValue(
				PropsValues.class, "UPGRADE_REPORT_DIR", "./test_reports");

		try {
			_upgradeReportDir = PropsValues.UPGRADE_REPORT_DIR;

			_appender.start();

			try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
					"com.liferay.portal.upgrade.internal.report.UpgradeReport",
					LoggerTestUtil.INFO)) {

				_appender.stop();

				_assertUpgradeReportDirectory(logCapture, "upgrade_report.txt");
				_assertUpgradeReportDirectory(
					logCapture, "upgrade_report_diagnostics.txt");
			}
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				PropsValues.class, "UPGRADE_REPORT_DIR",
				originalUpgradeReportDir);
		}
	}

	@Test
	public void testUpgradeReportDirectoryWriteProtected() throws Exception {
		File reportDir = new File("/");

		Assume.assumeFalse(reportDir.canWrite());

		String originalUpgradeReportDir =
			ReflectionTestUtil.getAndSetFieldValue(
				PropsValues.class, "UPGRADE_REPORT_DIR", "/");

		try {
			_upgradeReportDir = PropsValues.UPGRADE_REPORT_DIR;

			_appender.start();

			try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
					"com.liferay.portal.upgrade.internal.report.UpgradeReport",
					LoggerTestUtil.ALL)) {

				_appender.stop();

				_assertUpgradeReportDirectoryWriteProtected(
					logCapture, reportDir, "upgrade_report.txt");
				_assertUpgradeReportDirectoryWriteProtected(
					logCapture, reportDir, "upgrade_report_diagnostics.txt");
			}
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				PropsValues.class, "UPGRADE_REPORT_DIR",
				originalUpgradeReportDir);
		}
	}

	@Test
	public void testUpgradeReportDisabled() throws Exception {
		boolean originalUpgradeReportEnabled =
			ReflectionTestUtil.getAndSetFieldValue(
				PropsValues.class, "UPGRADE_REPORT_ENABLED", false);

		try {
			_appender.start();

			_appender.stop();

			File reportFile = _getReportFile("upgrade_report.txt");

			Assert.assertFalse(reportFile.exists());

			reportFile = _getReportFile("upgrade_report_diagnostics.txt");

			Assert.assertFalse(reportFile.exists());
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				PropsValues.class, "UPGRADE_REPORT_ENABLED",
				originalUpgradeReportEnabled);
		}
	}

	@Rule
	public final TemporaryFolder temporaryFolder = new TemporaryFolder();

	protected static void setUpClass(boolean upgradeClient) throws Exception {
		_db = DBManagerUtil.getDB();

		DBPartitionUtil.forEachCompanyId(
			companyId -> {
				_db.runSQL(
					"create table UpgradeReportTable1 (id_ LONG not null " +
						"primary key)");
				_db.runSQL(
					"create table UpgradeReportTable2 (id_ LONG not null " +
						"primary key)");
			});

		_originalNewRelease = ReflectionTestUtil.getFieldValue(
			StartupHelperUtil.class, "_newRelease");

		_originalUpgradeClient = ReflectionTestUtil.getAndSetFieldValue(
			DBUpgrader.class, "_upgradeClient", upgradeClient);

		_originalUpgradeLogContextEnabled =
			ReflectionTestUtil.getAndSetFieldValue(
				PropsValues.class, "UPGRADE_LOG_CONTEXT_ENABLED", true);
	}

	protected abstract String getFilePath();

	private static void _restoreRelease() throws Exception {
		ReflectionTestUtil.setFieldValue(
			StartupHelperUtil.class, "_newRelease", _originalNewRelease);

		_updatePortalRelease(
			PortalUpgradeProcess.getLatestSchemaVersion(),
			ReleaseInfo.getBuildNumber());
	}

	private static void _updatePortalRelease(
			Version schemaVersion, int buildNumber)
		throws Exception {

		try (Connection connection = DataAccess.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				"update Release_ set schemaVersion = ?, buildNumber = ? " +
					"where releaseId = ?")) {

			preparedStatement.setString(1, schemaVersion.toString());
			preparedStatement.setInt(2, buildNumber);
			preparedStatement.setLong(3, ReleaseConstants.DEFAULT_ID);

			preparedStatement.executeUpdate();
		}

		DCLSingleton<?> dclSingleton = ReflectionTestUtil.getFieldValue(
			PortalUpgradeProcess.class, "_currentPortalReleaseDTODCLSingleton");

		dclSingleton.destroy(null);
	}

	private void _assertLogContext(String key, String text) {
		Assert.assertTrue(
			StringUtil.containsIgnoreCase(
				_getLogContextValue(key), text, StringPool.BLANK));
	}

	private void _assertLogContextDiagnostics(String key, String text) {
		Assert.assertTrue(
			StringUtil.containsIgnoreCase(
				_getLogContextValueDiagnostics(key), text, StringPool.BLANK));
	}

	private void _assertRenameUpgradeReport(String reportFileName)
		throws Exception {

		File reportFile = _getReportFile(reportFileName);

		Assert.assertTrue(reportFile.exists());

		long reportFile1LastModified = reportFile.lastModified();

		_appender.start();

		_appender.stop();

		reportFile = _getReportFile(
			reportFileName + "." + reportFile1LastModified);

		Assert.assertTrue(reportFile.exists());

		reportFile = _getReportFile(reportFileName);

		Assert.assertTrue(reportFile.exists());
		Assert.assertTrue(reportFile.lastModified() != reportFile1LastModified);
	}

	private void _assertReport(String testString) throws Exception {
		if (_reportContent == null) {
			_reportContent = _getReportContent();
		}

		Assert.assertTrue(_reportContent.contains(testString));
	}

	private void _assertReportDiagnostics(String testString) throws Exception {
		if (_diagnosticsReportContent == null) {
			_diagnosticsReportContent = _getReportContentDiagnostics();
		}

		Assert.assertTrue(_diagnosticsReportContent.contains(testString));
	}

	private void _assertTablesAreSortedByInitialRows(Matcher matcher) {
		int previousInitialTableCount = Integer.MAX_VALUE;
		String previousTableName = null;

		while (matcher.find()) {
			int initialTableCount = GetterUtil.getInteger(matcher.group(2), -1);

			String tableName = matcher.group(1);

			if (initialTableCount == previousInitialTableCount) {
				Assert.assertTrue(previousTableName.compareTo(tableName) < 0);
			}
			else {
				Assert.assertTrue(
					initialTableCount < previousInitialTableCount);
			}

			previousInitialTableCount = initialTableCount;
			previousTableName = tableName;
		}
	}

	private void _assertUpgradeReportDirectory(
			LogCapture logCapture, String reportFileName)
		throws Exception {

		File reportFile = _getReportFile(reportFileName);

		Assert.assertTrue(reportFile.exists());

		URI uri = reportFile.toURI();

		String path = uri.getPath();

		Assert.assertTrue(path.contains(_upgradeReportDir));

		String logEntries = String.valueOf(logCapture.getLogEntries());

		Assert.assertTrue(
			logEntries.contains(
				"Upgrade report generated in " + reportFile.getAbsolutePath()));
	}

	private void _assertUpgradeReportDirectoryWriteProtected(
			LogCapture logCapture, File reportDir, String reportFileName)
		throws Exception {

		File reportFile = new File(reportDir, reportFileName);

		Assert.assertFalse(reportFile.exists());

		String logEntries = String.valueOf(logCapture.getLogEntries());

		Assert.assertTrue(
			logEntries.contains("Unable to generate the upgrade report at /"));

		_upgradeReportDir = "";

		reportFile = _getReportFile(reportFileName);

		Assert.assertTrue(reportFile.exists());
		Assert.assertTrue(
			logEntries.contains(
				"Upgrade report generated in " + reportFile.getAbsolutePath()));
	}

	private String _getLogContent() {
		return _unsyncStringWriter.toString();
	}

	private String _getLogContextValue(String key) {
		return _getLogContextValue(key, "upgrade_report.txt");
	}

	private String _getLogContextValue(String key, String reportFileName) {
		File file = new File(
			new File(getFilePath(), "reports"), reportFileName);

		Pattern pattern = Pattern.compile(
			"(?s)INFO - Upgrade report generated in " +
				Pattern.quote(file.getAbsolutePath()) + "\\s*\\{(.+?)\\}");

		int index = _getLogContent().indexOf(
			"INFO - Upgrade report generated in " + file.getAbsolutePath());

		String substringLogContextContent = _getLogContent().substring(index);

		Matcher matcher = pattern.matcher(substringLogContextContent);

		Assert.assertTrue(matcher.matches());

		Map<String, String> logContextValues = _getLogContextValues(
			matcher.group(1));

		Assert.assertTrue(logContextValues.containsKey(key));

		return logContextValues.get(key);
	}

	private String _getLogContextValueDiagnostics(String key) {
		return _getLogContextValue(key, "upgrade_report_diagnostics.txt");
	}

	private Map<String, String> _getLogContextValues(String logContextContent) {
		HashMap<String, String> values = new HashMap<>();

		String[] keys = logContextContent.split(",\\s*(?=upgrade.report.)");

		for (String key : keys) {
			String[] keyParts = key.split("=", 2);

			values.put(keyParts[0], keyParts[1]);
		}

		return values;
	}

	private String _getReportContent() throws Exception {
		File reportFile = _getReportFile("upgrade_report.txt");

		Assert.assertTrue(reportFile.exists());

		return FileUtil.read(reportFile);
	}

	private String _getReportContentDiagnostics() throws Exception {
		File diagnosticsReportFile = _getReportFile(
			"upgrade_report_diagnostics.txt");

		Assert.assertTrue(diagnosticsReportFile.exists());

		return FileUtil.read(diagnosticsReportFile);
	}

	private File _getReportFile(String reportFileName) throws Exception {
		File reportsDir = null;

		if (Validator.isBlank(_upgradeReportDir)) {
			reportsDir = new File(getFilePath(), "reports");
		}
		else {
			reportsDir = new File(_upgradeReportDir);
		}

		if (PropsValues.UPGRADE_REPORT_ENABLED) {
			Assert.assertTrue(reportsDir.exists());
		}

		return new File(reportsDir, reportFileName);
	}

	private void _setEnv(String key, String value) throws Exception {
		Map<String, String> env = System.getenv();

		Class<?> clazz = env.getClass();

		Field field = clazz.getDeclaredField("m");

		field.setAccessible(true);

		@SuppressWarnings("unchecked")
		Map<String, String> writableEnv = (Map<String, String>)field.get(env);

		writableEnv.put(key, value);
	}

	private SafeCloseable _setUpgradeReportDLStorageSizeTimeout(long timeout) {
		long originalUpgradeReportDLStorageSizeTimeout =
			ReflectionTestUtil.getAndSetFieldValue(
				PropsValues.class, "UPGRADE_REPORT_DL_STORAGE_SIZE_TIMEOUT",
				timeout);

		return () -> ReflectionTestUtil.getAndSetFieldValue(
			PropsValues.class, "UPGRADE_REPORT_DL_STORAGE_SIZE_TIMEOUT",
			originalUpgradeReportDLStorageSizeTimeout);
	}

	private static final String _CLEANUP_INFO_MESSAGE =
		RandomTestUtil.randomString();

	private static final String _CLEANUP_WARNING_MESSAGE =
		RandomTestUtil.randomString();

	private static final String _DELETE_DUPLICATES_FINDER_WARNING_MESSAGE =
		RandomTestUtil.randomString();

	private static DB _db;
	private static Appender _logContextAppender;
	private static final Pattern _logContextTablesInitialFinalRowsPattern =
		Pattern.compile("(\\w+_?):(\\d+|-):(\\d+|-)");
	private static boolean _originalNewRelease;
	private static boolean _originalUpgradeClient;
	private static boolean _originalUpgradeLogContextEnabled;
	private static final Pattern _pattern = Pattern.compile(
		"(\\w+_?)\\s+(\\d+|-)\\s+(\\d+|-)\n");

	@Inject(
		filter = "component.name=com.liferay.portal.upgrade.internal.recorder.UpgradeRecorder",
		type = Inject.NoType.class
	)
	private static Object _upgradeRecorder;

	private static Logger _upgradeReportLogger;

	@Inject(filter = "appender.name=UpgradeLogAppender")
	private Appender _appender;

	private String _diagnosticsReportContent;

	@Inject
	private ReleaseLocalService _releaseLocalService;

	private String _reportContent;
	private final UnsyncStringWriter _unsyncStringWriter =
		new UnsyncStringWriter();
	private String _upgradeReportDir = "";

	private class TestDataCleanupPreupgradeProcess
		extends DataCleanupPreupgradeProcess {

		@Override
		protected void doUpgrade() throws Exception {
			LogEvent logEvent = Log4jLogEvent.newBuilder(
			).setLoggerName(
				TestDataCleanupPreupgradeProcess.class.getName()
			).setLevel(
				Level.WARN
			).setMessage(
				new SimpleMessage(_CLEANUP_WARNING_MESSAGE)
			).build();

			_appender.append(logEvent);

			logEvent = Log4jLogEvent.newBuilder(
			).setLoggerName(
				TestDataCleanupPreupgradeProcess.class.getName()
			).setLevel(
				Level.INFO
			).setMessage(
				new SimpleMessage(_CLEANUP_INFO_MESSAGE)
			).build();

			_appender.append(logEvent);
		}

	}

	private class TestDeleteDuplicateUniqueFinderRowsUpgradeProcess
		extends DeleteDuplicateUniqueFinderRowsUpgradeProcess {

		public TestDeleteDuplicateUniqueFinderRowsUpgradeProcess(
			String tableName, String[] columnNames, String orderByClause) {

			super(tableName, columnNames, orderByClause);
		}

		@Override
		protected void doUpgrade() throws Exception {
			LogEvent logEvent = Log4jLogEvent.newBuilder(
			).setLoggerName(
				DuplicateUniqueFinderRowsCleaner.class.getName()
			).setLevel(
				Level.WARN
			).setMessage(
				new SimpleMessage(_DELETE_DUPLICATES_FINDER_WARNING_MESSAGE)
			).build();

			_appender.append(logEvent);
		}

	}

}