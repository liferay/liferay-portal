/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.concurrent.DCLSingleton;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.events.StartupHelperUtil;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.model.ReleaseConstants;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ReleaseInfo;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.tools.DBUpgrader;
import com.liferay.portal.upgrade.PortalUpgradeProcess;
import com.liferay.portal.upgrade.data.cleanup.DataCleanupPreupgradeProcessSuite;
import com.liferay.portal.verify.VerifyProcess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Luis Ortiz
 */
@RunWith(Arquillian.class)
public class DBUpgraderTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		_connection = DataAccess.getConnection();

		_currentBuildNumber = PortalUpgradeProcess.getCurrentBuildNumber(
			_connection);
		_currentState = PortalUpgradeProcess.getCurrentState(_connection);

		_moduleServiceLifecyclePortalInitialized =
			ReflectionTestUtil.getAndSetFieldValue(
				DBUpgrader.class, "moduleServiceLifecyclePortalInitialized",
				"test");
		_moduleServiceLifecyclePortletsInitialized =
			ReflectionTestUtil.getAndSetFieldValue(
				DBUpgrader.class, "moduleServiceLifecyclePortletsInitialized",
				"test");

		_upgrading = StartupHelperUtil.isUpgrading();
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		DataAccess.cleanUp(_connection);

		ReflectionTestUtil.setFieldValue(
			DBUpgrader.class, "moduleServiceLifecyclePortalInitialized",
			_moduleServiceLifecyclePortalInitialized);
		ReflectionTestUtil.setFieldValue(
			DBUpgrader.class, "moduleServiceLifecyclePortletsInitialized",
			_moduleServiceLifecyclePortletsInitialized);

		StartupHelperUtil.setUpgrading(_upgrading);
	}

	@After
	public void tearDown() throws Exception {
		_updatePortalRelease(_currentBuildNumber, _currentState);
	}

	@Test
	public void testDisablePreupgradeDataCleanup() throws Exception {
		String currentSchemaVersion;

		try (Connection connection = DataAccess.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				"select schemaVersion from Release_ where releaseId = " +
					ReleaseConstants.DEFAULT_ID);
			ResultSet resultSet = preparedStatement.executeQuery()) {

			resultSet.next();

			currentSchemaVersion = resultSet.getString(1);
		}

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				LoggingTimer.class.getName(), LoggerTestUtil.INFO);
			SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"UPGRADE_DATABASE_PREUPGRADE_VERIFY_ENABLED", false)) {

			_updatePortalSchemaVersion(currentSchemaVersion + ".0");

			DBUpgrader.upgradePortal();

			List<LogEntry> logEntries = logCapture.getLogEntries();

			LogEntry logEntry = logEntries.get(0);

			String logMessage = logEntry.getMessage();

			Assert.assertTrue(
				logMessage.contains(
					"Starting " +
						DataCleanupPreupgradeProcessSuite.class.getName()));
		}
		finally {
			_updatePortalSchemaVersion(currentSchemaVersion);
		}

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				LoggingTimer.class.getName(), LoggerTestUtil.INFO);
			SafeCloseable safeCloseable1 =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"UPGRADE_DATABASE_PREUPGRADE_DATA_CLEANUP_ENABLED", false);
			SafeCloseable safeCloseable2 =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"UPGRADE_DATABASE_PREUPGRADE_VERIFY_ENABLED", false)) {

			_updatePortalSchemaVersion(currentSchemaVersion + ".0");

			DBUpgrader.upgradePortal();

			List<String> messages = logCapture.getMessages();

			for (String message : messages) {
				Assert.assertFalse(
					message.contains(
						"Starting " +
							DataCleanupPreupgradeProcessSuite.class.getName()));
			}
		}
		finally {
			_updatePortalSchemaVersion(currentSchemaVersion);
		}
	}

	@Test
	public void testDisablePreupgradeVerification() throws Exception {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				VerifyProcess.class.getName(), LoggerTestUtil.INFO)) {

			DBUpgrader.upgradePortal();

			List<LogEntry> logEntries = logCapture.getLogEntries();

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				"Verifying com.liferay.portal.verify." +
					"PreupgradeVerifyProcessSuite",
				logEntry.getMessage());
		}

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				VerifyProcess.class.getName(), LoggerTestUtil.INFO);
			SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"UPGRADE_DATABASE_PREUPGRADE_VERIFY_ENABLED", false)) {

			DBUpgrader.upgradePortal();

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 0, logEntries.size());
		}
	}

	@Test
	public void testUpgrade() throws Exception {
		_updatePortalRelease(
			ReleaseInfo.RELEASE_7_1_0_BUILD_NUMBER,
			ReleaseConstants.STATE_GOOD);

		try {
			StartupHelperUtil.setUpgrading(true);

			DBUpgrader.upgradePortal();
		}
		finally {
			StartupHelperUtil.setUpgrading(false);
		}
	}

	@Test
	public void testUpgradeModuleIndexes() throws Exception {
		DB db = DBManagerUtil.getDB();

		db.runSQL("create index IX_TEST on Lock_ (createDate)");

		Boolean newRelease = ReflectionTestUtil.getAndSetFieldValue(
			StartupHelperUtil.class, "_newRelease", false);

		String upgradeDatabaseAutoRun = PropsUtil.get(
			PropsKeys.UPGRADE_DATABASE_AUTO_RUN);

		try {
			PropsUtil.set(PropsKeys.UPGRADE_DATABASE_AUTO_RUN, "false");

			StartupHelperUtil.setUpgrading(true);

			DBUpgrader.upgradeModules(
				() -> {
				});

			DBInspector dbInspector = new DBInspector(_connection);

			Assert.assertTrue(dbInspector.hasIndex("Lock_", "IX_TEST"));

			PropsUtil.set(PropsKeys.UPGRADE_DATABASE_AUTO_RUN, "true");

			DBUpgrader.upgradeModules(
				() -> {
				});

			Assert.assertTrue(dbInspector.hasIndex("Lock_", "IX_TEST"));

			ReflectionTestUtil.setFieldValue(
				StartupHelperUtil.class, "_newRelease", true);

			DBUpgrader.upgradeModules(
				() -> {
				});

			Assert.assertFalse(dbInspector.hasIndex("Lock_", "IX_TEST"));
		}
		finally {
			PropsUtil.set(
				PropsKeys.UPGRADE_DATABASE_AUTO_RUN, upgradeDatabaseAutoRun);

			ReflectionTestUtil.setFieldValue(
				StartupHelperUtil.class, "_newRelease", newRelease);

			StartupHelperUtil.setUpgrading(false);
		}
	}

	@Test
	public void testUpgradeWithFailureDoesNotSupportRetry() throws Exception {
		_updatePortalRelease(
			ReleaseInfo.RELEASE_6_2_0_BUILD_NUMBER,
			ReleaseConstants.STATE_UPGRADE_FAILURE);

		try {
			StartupHelperUtil.setUpgrading(true);

			DBUpgrader.upgradePortal();

			Assert.fail();
		}
		catch (IllegalStateException illegalStateException) {
		}
		finally {
			StartupHelperUtil.setUpgrading(false);
		}
	}

	@Test
	public void testUpgradeWithFailureSupportsRetry() throws Exception {
		_updatePortalRelease(
			ReleaseInfo.RELEASE_7_1_0_BUILD_NUMBER,
			ReleaseConstants.STATE_UPGRADE_FAILURE);

		try {
			StartupHelperUtil.setUpgrading(true);

			DBUpgrader.upgradePortal();
		}
		finally {
			StartupHelperUtil.setUpgrading(false);
		}
	}

	private void _updatePortalRelease(int buildNumber, int state)
		throws Exception {

		try (Connection connection = DataAccess.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				"update Release_ set buildNumber = ?, state_ = ? where " +
					"releaseId = ?")) {

			preparedStatement.setInt(1, buildNumber);
			preparedStatement.setInt(2, state);
			preparedStatement.setLong(3, ReleaseConstants.DEFAULT_ID);

			preparedStatement.executeUpdate();
		}

		DCLSingleton<?> dclSingleton = ReflectionTestUtil.getFieldValue(
			PortalUpgradeProcess.class, "_currentPortalReleaseDTODCLSingleton");

		dclSingleton.destroy(null);
	}

	private void _updatePortalSchemaVersion(String schemaVersion)
		throws Exception {

		try (Connection connection = DataAccess.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				"update Release_ set schemaVersion = ? where releaseId = ?")) {

			preparedStatement.setString(1, schemaVersion);
			preparedStatement.setLong(2, ReleaseConstants.DEFAULT_ID);

			preparedStatement.executeUpdate();
		}

		DCLSingleton<?> dclSingleton = ReflectionTestUtil.getFieldValue(
			PortalUpgradeProcess.class, "_currentPortalReleaseDTODCLSingleton");

		dclSingleton.destroy(null);
	}

	private static Connection _connection;
	private static int _currentBuildNumber;
	private static int _currentState;
	private static String _moduleServiceLifecyclePortalInitialized;
	private static String _moduleServiceLifecyclePortletsInitialized;
	private static boolean _upgrading;

}