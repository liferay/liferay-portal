/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.data.cleanup.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.concurrent.DCLSingleton;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.db.partition.DBPartition;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.model.ReleaseConstants;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.upgrade.data.cleanup.DataCleanupPreupgradeException;
import com.liferay.portal.kernel.upgrade.data.cleanup.DataCleanupPreupgradeProcess;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.PortalUpgradeProcess;
import com.liferay.portal.upgrade.data.cleanup.DataCleanupPreupgradeProcessSuite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Luis Ortiz
 */
@RunWith(Arquillian.class)
public class DataCleanupPreupgradeProcessSuiteTest
	extends DataCleanupPreupgradeProcessSuite {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		try (Connection connection = DataAccess.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				"select schemaVersion from Release_ where releaseId = " +
					ReleaseConstants.DEFAULT_ID);
			ResultSet resultSet = preparedStatement.executeQuery()) {

			resultSet.next();

			_currentPortalSchemaVersion = resultSet.getString(1);

			_updatePortalSchemaVersion(_currentPortalSchemaVersion + ".0");
		}

		if (DBPartition.isPartitionEnabled()) {
			long[] companyIds = PortalInstancePool.getCompanyIds();

			_companiesCount = companyIds.length;
		}
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		_updatePortalSchemaVersion(_currentPortalSchemaVersion);
	}

	@Before
	public void setUp() {
		_originalDataCleanupPreupgradeProcessesMap =
			ReflectionTestUtil.getFieldValue(
				this, "_dataCleanupPreupgradeProcessesMap");
	}

	@After
	public void tearDown() {
		ReflectionTestUtil.setFieldValue(
			this, "_dataCleanupPreupgradeProcessesMap",
			_originalDataCleanupPreupgradeProcessesMap);
	}

	@Test
	public void testDataCleanupPreupgradeProcessesSuiteWithBlacklist()
		throws Exception {

		String className =
			BlacklistedDataCleanupPreupgradeTestProcess.class.getName();

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				DataCleanupPreupgradeProcessSuite.class.getName(),
				LoggerTestUtil.INFO);
			SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"UPGRADE_DATABASE_PREUPGRADE_DATA_CLEANUP_BLACKLIST",
					new String[] {className})) {

			ReflectionTestUtil.setFieldValue(
				this, "_dataCleanupPreupgradeProcessesMap",
				HashMapBuilder.
					<DataCleanupPreupgradeProcess,
					 List<DataCleanupPreupgradeProcess>>put(
						new BlacklistedDataCleanupPreupgradeTestProcess(
							() -> _cleanupMessages.add(_SUCCESS_MESSAGE_1)),
						DataCleanupPreupgradeProcess.dependsOn()
					).put(
						new DataCleanupPreupgradeTestProcess(
							() -> _cleanupMessages.add(_SUCCESS_MESSAGE_2)),
						DataCleanupPreupgradeProcess.dependsOn()
					).build());

			cleanUp();

			Assert.assertEquals(
				_cleanupMessages.toString(), _companiesCount,
				_cleanupMessages.size());

			Assert.assertFalse(_cleanupMessages.contains(_SUCCESS_MESSAGE_1));
			Assert.assertTrue(_cleanupMessages.contains(_SUCCESS_MESSAGE_2));

			List<LogEntry> logEntries = new ArrayList<>();

			for (LogEntry logEntry : logCapture.getLogEntries()) {
				String message = logEntry.getMessage();

				if (message.contains("Skipping blacklisted")) {
					logEntries.add(logEntry);
				}
			}

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				"Skipping blacklisted data cleanup process: " + className,
				logEntry.getMessage());
		}
	}

	@Test
	public void testDataCleanupPreupgradeProcessesSuiteWithFailures() {
		DataCleanupPreupgradeProcess failureDataCleanupPreupgradeProcess =
			_createDataCleanupPreupgradeProcess(
				() -> {
					throw new Exception(_EXCEPTION_MESSAGE);
				});
		DataCleanupPreupgradeProcess successDataCleanupPreupgradeProcess =
			_createDataCleanupPreupgradeProcess(
				() -> _cleanupMessages.add(_SUCCESS_MESSAGE_1));

		ReflectionTestUtil.setFieldValue(
			this, "_dataCleanupPreupgradeProcessesMap",
			HashMapBuilder.
				<DataCleanupPreupgradeProcess,
				 List<DataCleanupPreupgradeProcess>>put(
					successDataCleanupPreupgradeProcess,
					DataCleanupPreupgradeProcess.dependsOn()
				).put(
					failureDataCleanupPreupgradeProcess,
					DataCleanupPreupgradeProcess.dependsOn(
						successDataCleanupPreupgradeProcess)
				).put(
					_createDataCleanupPreupgradeProcess(
						() -> _cleanupMessages.add(_SUCCESS_MESSAGE_2)),
					DataCleanupPreupgradeProcess.dependsOn(
						failureDataCleanupPreupgradeProcess)
				).build());

		try {
			cleanUp();

			Assert.fail();
		}
		catch (Exception exception) {
			Assert.assertTrue(
				exception instanceof DataCleanupPreupgradeException);

			String message = exception.getMessage();

			Assert.assertTrue(message.contains(_EXCEPTION_MESSAGE));
		}

		Assert.assertEquals(
			_cleanupMessages.toString(), _companiesCount,
			_cleanupMessages.size());

		Assert.assertFalse(_cleanupMessages.contains(_SUCCESS_MESSAGE_2));
		Assert.assertTrue(_cleanupMessages.contains(_SUCCESS_MESSAGE_1));
	}

	@Test
	public void testDataCleanupPreupgradeProcessesSuiteWithMultipleData()
		throws Exception {

		ReflectionTestUtil.setFieldValue(
			this, "_dataCleanupPreupgradeProcessesMap",
			HashMapBuilder.
				<DataCleanupPreupgradeProcess,
				 List<DataCleanupPreupgradeProcess>>put(
					_createDataCleanupPreupgradeProcess(
						() -> _cleanupMessages.add(_SUCCESS_MESSAGE_1)),
					DataCleanupPreupgradeProcess.dependsOn()
				).put(
					_createDataCleanupPreupgradeProcess(
						() -> _cleanupMessages.add(_SUCCESS_MESSAGE_2)),
					DataCleanupPreupgradeProcess.dependsOn()
				).build());

		cleanUp();

		Assert.assertEquals(
			_cleanupMessages.toString(), 2 * _companiesCount,
			_cleanupMessages.size());

		Assert.assertTrue(_cleanupMessages.contains(_SUCCESS_MESSAGE_1));
		Assert.assertTrue(_cleanupMessages.contains(_SUCCESS_MESSAGE_2));
	}

	private static void _updatePortalSchemaVersion(String schemaVersion)
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

	private DataCleanupPreupgradeProcess _createDataCleanupPreupgradeProcess(
		UnsafeRunnable<Exception> unsafeRunnable) {

		return new DataCleanupPreupgradeProcess() {

			@Override
			protected void doUpgrade() throws Exception {
				unsafeRunnable.run();
			}

		};
	}

	private static final String _EXCEPTION_MESSAGE =
		RandomTestUtil.randomString();

	private static final String _SUCCESS_MESSAGE_1 =
		RandomTestUtil.randomString();

	private static final String _SUCCESS_MESSAGE_2 =
		RandomTestUtil.randomString();

	private static int _companiesCount = 1;
	private static String _currentPortalSchemaVersion;

	private final List<String> _cleanupMessages = new ArrayList<>();
	private Map
		<DataCleanupPreupgradeProcess, List<DataCleanupPreupgradeProcess>>
			_originalDataCleanupPreupgradeProcessesMap;

	private static class BlacklistedDataCleanupPreupgradeTestProcess
		extends DataCleanupPreupgradeTestProcess {

		public BlacklistedDataCleanupPreupgradeTestProcess(
			UnsafeRunnable<Exception> unsafeRunnable) {

			super(unsafeRunnable);
		}

	}

	private static class DataCleanupPreupgradeTestProcess
		extends DataCleanupPreupgradeProcess {

		public DataCleanupPreupgradeTestProcess(
			UnsafeRunnable<Exception> unsafeRunnable) {

			_unsafeRunnable = unsafeRunnable;
		}

		@Override
		protected void doUpgrade() throws Exception {
			_unsafeRunnable.run();
		}

		private final UnsafeRunnable<Exception> _unsafeRunnable;

	}

}