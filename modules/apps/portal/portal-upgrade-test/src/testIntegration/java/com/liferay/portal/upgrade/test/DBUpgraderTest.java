/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.osgi.util.BundleUtil;
import com.liferay.petra.concurrent.DCLSingleton;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.events.StartupHelperUtil;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.encryptor.EncryptorUtil;
import com.liferay.portal.kernel.model.ReleaseConstants;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ReleaseInfo;
import com.liferay.portal.kernel.version.Version;
import com.liferay.portal.lpkg.deployer.LPKGDeployer;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.tools.DBUpgrader;
import com.liferay.portal.upgrade.PortalUpgradeProcess;
import com.liferay.portal.upgrade.data.cleanup.DataCleanupPreupgradeProcessSuite;
import com.liferay.portal.verify.VerifyProcess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.component.runtime.ServiceComponentRuntime;
import org.osgi.service.component.runtime.dto.ComponentDescriptionDTO;
import org.osgi.util.promise.Promise;

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
	public void testCheckReleaseStateWithInterruptedUpgrade() throws Exception {
		_updatePortalRelease(
			ReleaseInfo.RELEASE_6_2_0_BUILD_NUMBER,
			ReleaseConstants.STATE_IN_PROGRESS);

		DBUpgrader.checkReleaseState();
	}

	@Test
	public void testCheckReleaseStateWithInterruptedUpgradeDoesNotSupportRetry()
		throws Exception {

		_updatePortalRelease(
			ReleaseInfo.RELEASE_6_2_0_BUILD_NUMBER,
			ReleaseConstants.STATE_IN_PROGRESS);

		try {
			_startUpgrade();

			DBUpgrader.checkReleaseState();

			Assert.fail();
		}
		catch (IllegalStateException illegalStateException) {
		}
		finally {
			_stopUpgrade();
		}
	}

	@Test
	public void testDisablePreupgradeDataCleanup() throws Exception {
		String currentSchemaVersion;

		try (Connection connection = DataAccess.getConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(
				"select schemaVersion from Release_ where releaseId = ?")) {

			preparedStatement.setLong(1, ReleaseConstants.DEFAULT_ID);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				resultSet.next();

				currentSchemaVersion = resultSet.getString("schemaVersion");
			}
		}

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				LoggingTimer.class.getName(), LoggerTestUtil.INFO);
			SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"UPGRADE_DATABASE_PREUPGRADE_VERIFY_ENABLED", false)) {

			_updatePortalSchemaVersion(Integer.MAX_VALUE + ".0.0");

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

			_updatePortalSchemaVersion(Integer.MAX_VALUE + ".0.0");

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
	public void testUpdateCompanyKey() throws Exception {
		Map<Long, String> originalKeys = new HashMap<>();

		try (PreparedStatement preparedStatement = _connection.prepareStatement(
				"select companyId, key_ from CompanyInfo");

			ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				originalKeys.put(
					resultSet.getLong("companyId"),
					resultSet.getString("key_"));
			}
		}

		try (PreparedStatement preparedStatement = _connection.prepareStatement(
				"update CompanyInfo set key_ = null")) {

			preparedStatement.executeUpdate();
		}

		try {
			ReflectionTestUtil.invoke(
				DBUpgrader.class, "_updateCompanyKey", new Class<?>[0]);

			try (PreparedStatement preparedStatement =
					_connection.prepareStatement(
						"select companyId, key_ from CompanyInfo");

				ResultSet resultSet = preparedStatement.executeQuery()) {

				while (resultSet.next()) {
					long companyId = resultSet.getLong("companyId");
					String key = resultSet.getString("key_");

					Assert.assertNotEquals(originalKeys.get(companyId), key);
					Assert.assertNotNull(EncryptorUtil.deserializeKey(key));
				}
			}
		}
		finally {
			String sql = "update CompanyInfo set key_ = ? where companyId = ?";

			try (PreparedStatement preparedStatement =
					AutoBatchPreparedStatementUtil.autoBatch(
						_connection, sql)) {

				for (Map.Entry<Long, String> entry : originalKeys.entrySet()) {
					preparedStatement.setString(1, entry.getValue());
					preparedStatement.setLong(2, entry.getKey());

					preparedStatement.addBatch();
				}

				preparedStatement.executeBatch();
			}
		}
	}

	@Test
	public void testUpgrade() throws Exception {
		_updatePortalRelease(
			ReleaseInfo.RELEASE_7_1_0_BUILD_NUMBER,
			ReleaseConstants.STATE_GOOD);

		try {
			_startUpgrade();

			DBUpgrader.upgradePortal();
		}
		finally {
			_stopUpgrade();
		}
	}

	@Test
	public void testUpgradeModuleDoesNotGenerateUpgradeReportWhenDataCleanupModuleIsAvailable()
		throws Exception {

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.upgrade.internal.report.UpgradeReport",
				LoggerTestUtil.INFO)) {

			_startUpgrade();

			DBUpgrader.upgradeModules();

			List<String> messages = logCapture.getMessages();

			Assert.assertTrue(messages.isEmpty());
		}
		finally {
			_stopUpgrade();
		}
	}

	@Test
	public void testUpgradeModuleGeneratesUpgradeReportWhenDataCleanupModuleIsNotAvailable()
		throws Exception {

		Bundle bundle = _uninstallBundle(_getBundle());

		try (LogCapture logCapture1 = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.upgrade.internal.report.UpgradeReport",
				LoggerTestUtil.INFO);
			LogCapture logCapture2 = LoggerTestUtil.configureLog4JLogger(
				DBUpgrader.class.getName(), LoggerTestUtil.WARN)) {

			_startUpgrade();

			DBUpgrader.upgradeModules();

			List<String> messages = logCapture1.getMessages();

			Assert.assertFalse(messages.isEmpty());

			messages = logCapture2.getMessages();

			Assert.assertTrue(
				messages.toString(),
				messages.contains(
					_CLASS_NAME +
						" did not activate successfully. The verify process " +
							"will not be executed."));
		}
		finally {
			_stopUpgrade();

			if (bundle != null) {
				_installBundle(bundle);
			}
		}
	}

	@Test
	public void testUpgradeModuleGeneratesUpgradeReportWhenPostUpgradeDataCleanupVerifyProcessServiceIsNotAvailable()
		throws Exception {

		ComponentDescriptionDTO componentDescriptionDTO =
			_serviceComponentRuntime.getComponentDescriptionDTO(
				_getBundle(), _CLASS_NAME);

		Promise<Void> voidPromise = _serviceComponentRuntime.disableComponent(
			componentDescriptionDTO);

		voidPromise.getValue();

		try (LogCapture logCapture1 = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.upgrade.internal.report.UpgradeReport",
				LoggerTestUtil.INFO);
			LogCapture logCapture2 = LoggerTestUtil.configureLog4JLogger(
				DBUpgrader.class.getName(), LoggerTestUtil.WARN)) {

			_startUpgrade();

			DBUpgrader.upgradeModules();

			List<String> messages = logCapture1.getMessages();

			Assert.assertFalse(messages.isEmpty());

			messages = logCapture2.getMessages();

			Assert.assertTrue(
				messages.toString(),
				messages.contains(
					_CLASS_NAME +
						" did not activate successfully. The verify process " +
							"will not be executed."));
		}
		finally {
			_stopUpgrade();

			voidPromise = _serviceComponentRuntime.enableComponent(
				componentDescriptionDTO);

			voidPromise.getValue();
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

			_startUpgrade();

			DBInspector dbInspector = new DBInspector(_connection);

			Assert.assertTrue(dbInspector.hasIndex("Lock_", "IX_TEST"));

			PropsUtil.set(PropsKeys.UPGRADE_DATABASE_AUTO_RUN, "true");

			DBUpgrader.upgradeModules();

			Assert.assertTrue(dbInspector.hasIndex("Lock_", "IX_TEST"));

			ReflectionTestUtil.setFieldValue(
				StartupHelperUtil.class, "_newRelease", true);

			DBUpgrader.upgradeModules();

			Assert.assertFalse(dbInspector.hasIndex("Lock_", "IX_TEST"));
		}
		finally {
			PropsUtil.set(
				PropsKeys.UPGRADE_DATABASE_AUTO_RUN, upgradeDatabaseAutoRun);

			ReflectionTestUtil.setFieldValue(
				StartupHelperUtil.class, "_newRelease", newRelease);

			_stopUpgrade();
		}
	}

	@Test
	public void testUpgradeWithFailureDoesNotSupportRetry() throws Exception {
		_updatePortalRelease(
			ReleaseInfo.RELEASE_6_2_0_BUILD_NUMBER,
			ReleaseConstants.STATE_UPGRADE_FAILURE);

		try {
			_startUpgrade();

			DBUpgrader.upgradePortal();

			Assert.fail();
		}
		catch (IllegalStateException illegalStateException) {
		}
		finally {
			_stopUpgrade();
		}
	}

	@Test
	public void testUpgradeWithFailureSupportsRetry() throws Exception {
		_updatePortalRelease(
			ReleaseInfo.RELEASE_7_1_0_BUILD_NUMBER,
			ReleaseConstants.STATE_UPGRADE_FAILURE);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				DBUpgrader.class.getName(), LoggerTestUtil.WARN)) {

			Version schemaVersion =
				PortalUpgradeProcess.getCurrentSchemaVersion(_connection);

			_startUpgrade();

			DBUpgrader.upgradePortal();

			List<String> messages = logCapture.getMessages();

			Assert.assertTrue(
				messages.contains(
					StringBundler.concat(
						"Retrying the upgrade of ",
						ReleaseConstants.DEFAULT_SERVLET_CONTEXT_NAME,
						" from schema version ", schemaVersion,
						" after a failure")));
		}
		finally {
			_stopUpgrade();
		}
	}

	@Test
	public void testUpgradeWithInterruption() throws Exception {
		_updatePortalRelease(
			ReleaseInfo.RELEASE_7_1_0_BUILD_NUMBER,
			ReleaseConstants.STATE_IN_PROGRESS);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				DBUpgrader.class.getName(), LoggerTestUtil.WARN)) {

			Version schemaVersion =
				PortalUpgradeProcess.getCurrentSchemaVersion(_connection);

			_startUpgrade();

			DBUpgrader.upgradePortal();

			List<String> messages = logCapture.getMessages();

			Assert.assertTrue(
				messages.contains(
					StringBundler.concat(
						"Resuming the upgrade of ",
						ReleaseConstants.DEFAULT_SERVLET_CONTEXT_NAME,
						" from schema version ", schemaVersion,
						" after an interruption")));
			Assert.assertFalse(
				messages.contains(
					StringBundler.concat(
						"Retrying the upgrade of ",
						ReleaseConstants.DEFAULT_SERVLET_CONTEXT_NAME,
						" from schema version ", schemaVersion,
						" after a failure")));
		}
		finally {
			_stopUpgrade();
		}

		Assert.assertEquals(
			ReleaseConstants.STATE_GOOD,
			PortalUpgradeProcess.getCurrentState(_connection));
	}

	@Test
	public void testUpgradeWithInterruptionAfterLastUpgradeProcess()
		throws Exception {

		_updatePortalRelease(
			ReleaseInfo.getParentBuildNumber(),
			ReleaseConstants.STATE_IN_PROGRESS);

		try {
			_startUpgrade();

			DBUpgrader.upgradePortal();
		}
		finally {
			_stopUpgrade();
		}

		Assert.assertEquals(
			ReleaseConstants.STATE_GOOD,
			PortalUpgradeProcess.getCurrentState(_connection));
	}

	private Bundle _getBundle() throws Exception {
		Bundle currentBundle = FrameworkUtil.getBundle(DBUpgraderTest.class);

		BundleContext bundleContext = currentBundle.getBundleContext();

		for (Bundle bundle : bundleContext.getBundles()) {
			if (Objects.equals(
					bundle.getSymbolicName(),
					"com.liferay.data.cleanup.impl")) {

				return bundle;
			}
		}

		return null;
	}

	private void _installBundle(Bundle bundle) throws Exception {
		Bundle currentBundle = FrameworkUtil.getBundle(DBUpgraderTest.class);

		BundleContext bundleContext = currentBundle.getBundleContext();

		BundleUtil.installBundle(
			bundleContext, _lpkgDeployer, bundle.getLocation(), 1);

		BundleUtil.refreshBundles(
			bundleContext, Collections.singletonList(bundle));
	}

	private void _startUpgrade() {
		StartupHelperUtil.setUpgrading(true);

		DBUpgrader.startUpgradeLogAppender();
	}

	private void _stopUpgrade() {
		StartupHelperUtil.setUpgrading(false);

		DBUpgrader.stopUpgradeLogAppender();
	}

	private Bundle _uninstallBundle(Bundle bundle) throws Exception {
		if ((bundle != null) && (bundle.getState() == Bundle.ACTIVE)) {
			bundle.uninstall();

			Bundle currentBundle = FrameworkUtil.getBundle(
				DBUpgraderTest.class);

			BundleUtil.refreshBundles(
				currentBundle.getBundleContext(),
				Collections.singletonList(bundle));
		}

		return bundle;
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

	private static final String _CLASS_NAME =
		"com.liferay.data.cleanup.internal.verify." +
			"PostUpgradeDataCleanupVerifyProcess";

	private static Connection _connection;
	private static int _currentBuildNumber;
	private static int _currentState;
	private static String _moduleServiceLifecyclePortalInitialized;
	private static String _moduleServiceLifecyclePortletsInitialized;
	private static boolean _upgrading;

	@Inject
	private LPKGDeployer _lpkgDeployer;

	@Inject
	private ServiceComponentRuntime _serviceComponentRuntime;

}