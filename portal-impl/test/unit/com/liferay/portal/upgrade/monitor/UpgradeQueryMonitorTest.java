/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.monitor;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.InfrastructureUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.sql.Connection;
import java.sql.SQLException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Jorge Avalos
 */
public class UpgradeQueryMonitorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@After
	public void tearDown() {
		UpgradeQueryMonitor.stop();
	}

	@Test
	public void testPoll() throws Exception {
		try (MockedStatic<DBManagerUtil> dbManagerUtilMockedStatic =
				Mockito.mockStatic(DBManagerUtil.class);
			LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				UpgradeQueryMonitor.class.getName(), LoggerTestUtil.WARN)) {

			Connection connection = Mockito.mock(Connection.class);

			DataSource dataSource = Mockito.mock(DataSource.class);

			Mockito.when(
				dataSource.getConnection()
			).thenReturn(
				connection
			);

			DB db = Mockito.mock(DB.class);

			dbManagerUtilMockedStatic.when(
				DBManagerUtil::getDB
			).thenReturn(
				db
			);

			DataSource originalDataSource = InfrastructureUtil.getDataSource();

			InfrastructureUtil.setDataSource(dataSource);

			try {
				_testPollWithNoLockedQueries(connection, db, logCapture);
				_testPollWithOneLockedQuery(connection, db, logCapture);
				_testPollWithMultipleLockedQueries(connection, db, logCapture);
				_testPollWithSQLException(connection, db, logCapture);
				_testPollWithSQLExceptionWhileStopping(
					connection, db, logCapture);
			}
			finally {
				InfrastructureUtil.setDataSource(originalDataSource);
			}
		}
	}

	@Test
	public void testPollLongRunning() throws Exception {
		try (MockedStatic<DBManagerUtil> dbManagerUtilMockedStatic =
				Mockito.mockStatic(DBManagerUtil.class);
			LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				UpgradeQueryMonitor.class.getName(), LoggerTestUtil.INFO)) {

			Connection connection = Mockito.mock(Connection.class);

			DataSource dataSource = Mockito.mock(DataSource.class);

			Mockito.when(
				dataSource.getConnection()
			).thenReturn(
				connection
			);

			DB db = Mockito.mock(DB.class);

			Mockito.when(
				db.getLockedQueryInfos(connection)
			).thenReturn(
				Collections.emptyList()
			);

			dbManagerUtilMockedStatic.when(
				DBManagerUtil::getDB
			).thenReturn(
				db
			);

			DataSource originalDataSource = InfrastructureUtil.getDataSource();

			InfrastructureUtil.setDataSource(dataSource);

			try {
				_testPollLongRunningWithNoQueries(connection, db, logCapture);
				_testPollLongRunningWithOneQuery(connection, db, logCapture);
				_testPollLongRunningWithMultipleQueries(
					connection, db, logCapture);
			}
			finally {
				InfrastructureUtil.setDataSource(originalDataSource);
			}
		}
	}

	@Test
	public void testStart() throws Exception {
		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"UPGRADE_QUERY_MONITOR_ENABLED", false)) {

			UpgradeQueryMonitor.start();

			Assert.assertNull(
				ReflectionTestUtil.getFieldValue(
					UpgradeQueryMonitor.class, _SCHEDULED_EXECUTOR_SERVICE));
		}

		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"UPGRADE_QUERY_MONITOR_ENABLED", true)) {

			UpgradeQueryMonitor.start();

			ScheduledExecutorService scheduledExecutorService =
				ReflectionTestUtil.getFieldValue(
					UpgradeQueryMonitor.class, _SCHEDULED_EXECUTOR_SERVICE);

			Assert.assertNotNull(scheduledExecutorService);

			UpgradeQueryMonitor.start();

			Assert.assertSame(
				scheduledExecutorService,
				ReflectionTestUtil.getFieldValue(
					UpgradeQueryMonitor.class, _SCHEDULED_EXECUTOR_SERVICE));
		}
	}

	@Test
	public void testStop() throws Exception {
		UpgradeQueryMonitor.stop();

		Assert.assertNull(
			ReflectionTestUtil.getFieldValue(
				UpgradeQueryMonitor.class, _SCHEDULED_EXECUTOR_SERVICE));

		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"UPGRADE_QUERY_MONITOR_ENABLED", true)) {

			UpgradeQueryMonitor.start();

			ScheduledExecutorService scheduledExecutorService =
				ReflectionTestUtil.getFieldValue(
					UpgradeQueryMonitor.class, _SCHEDULED_EXECUTOR_SERVICE);

			CountDownLatch countDownLatch = new CountDownLatch(1);

			Future<Boolean> future = scheduledExecutorService.submit(
				() -> {
					countDownLatch.countDown();

					Thread.sleep(1000);

					return Thread.interrupted();
				});

			countDownLatch.await();

			UpgradeQueryMonitor.stop();

			Assert.assertFalse(future.get());
			Assert.assertNull(
				ReflectionTestUtil.getFieldValue(
					UpgradeQueryMonitor.class, _SCHEDULED_EXECUTOR_SERVICE));
			Assert.assertTrue(scheduledExecutorService.isTerminated());
		}
	}

	private void _poll(ScheduledExecutorService scheduledExecutorService) {
		ReflectionTestUtil.invoke(
			UpgradeQueryMonitor.class, "_poll",
			new Class<?>[] {ScheduledExecutorService.class},
			scheduledExecutorService);
	}

	private void _testPollLongRunningWithMultipleQueries(
			Connection connection, DB db, LogCapture logCapture)
		throws Exception {

		String id1 = RandomTestUtil.randomString();
		String id2 = RandomTestUtil.randomString();
		String id3 = RandomTestUtil.randomString();
		String query1 = RandomTestUtil.randomString();
		String query2 = RandomTestUtil.randomString();
		String query3 = RandomTestUtil.randomString();
		String schema1 = RandomTestUtil.randomString();
		String schema2 = RandomTestUtil.randomString();
		String schema3 = RandomTestUtil.randomString();

		Mockito.when(
			db.getLongRunningQueryInfos(connection)
		).thenReturn(
			Arrays.asList(
				new DB.QueryInfo(
					630000, id1, query1, schema1,
					RandomTestUtil.randomString()),
				new DB.QueryInfo(
					720000, id2, query2, schema2,
					RandomTestUtil.randomString()),
				new DB.QueryInfo(
					810000, id3, query3, schema3,
					RandomTestUtil.randomString()))
		);

		List<LogEntry> logEntries = logCapture.getLogEntries();

		logEntries.clear();

		_poll(_scheduledExecutorService);

		Assert.assertEquals(logEntries.toString(), 3, logEntries.size());

		LogEntry logEntry1 = logEntries.get(0);

		Assert.assertEquals(
			StringBundler.concat(
				"Long running query \"", query1, "\" with ID ", id1,
				" in schema \"", schema1,
				"\" has been running for 630 seconds"),
			logEntry1.getMessage());

		LogEntry logEntry2 = logEntries.get(1);

		Assert.assertEquals(
			StringBundler.concat(
				"Long running query \"", query2, "\" with ID ", id2,
				" in schema \"", schema2,
				"\" has been running for 720 seconds"),
			logEntry2.getMessage());

		LogEntry logEntry3 = logEntries.get(2);

		Assert.assertEquals(
			StringBundler.concat(
				"Long running query \"", query3, "\" with ID ", id3,
				" in schema \"", schema3,
				"\" has been running for 810 seconds"),
			logEntry3.getMessage());
	}

	private void _testPollLongRunningWithNoQueries(
			Connection connection, DB db, LogCapture logCapture)
		throws Exception {

		Mockito.when(
			db.getLongRunningQueryInfos(connection)
		).thenReturn(
			Collections.emptyList()
		);

		List<LogEntry> logEntries = logCapture.getLogEntries();

		logEntries.clear();

		_poll(_scheduledExecutorService);

		Assert.assertEquals(logEntries.toString(), 0, logEntries.size());
	}

	private void _testPollLongRunningWithOneQuery(
			Connection connection, DB db, LogCapture logCapture)
		throws Exception {

		String id = RandomTestUtil.randomString();
		String query = RandomTestUtil.randomString();
		String schema = RandomTestUtil.randomString();

		Mockito.when(
			db.getLongRunningQueryInfos(connection)
		).thenReturn(
			Collections.singletonList(
				new DB.QueryInfo(
					630000, id, query, schema, RandomTestUtil.randomString()))
		);

		List<LogEntry> logEntries = logCapture.getLogEntries();

		logEntries.clear();

		_poll(_scheduledExecutorService);

		Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

		LogEntry logEntry = logEntries.get(0);

		Assert.assertEquals(
			StringBundler.concat(
				"Long running query \"", query, "\" with ID ", id,
				" in schema \"", schema, "\" has been running for 630 seconds"),
			logEntry.getMessage());
		Assert.assertEquals("INFO", logEntry.getPriority());
	}

	private void _testPollWithMultipleLockedQueries(
			Connection connection, DB db, LogCapture logCapture)
		throws Exception {

		String id1 = RandomTestUtil.randomString();
		String id2 = RandomTestUtil.randomString();
		String id3 = RandomTestUtil.randomString();
		String query1 = RandomTestUtil.randomString();
		String query2 = RandomTestUtil.randomString();
		String query3 = RandomTestUtil.randomString();
		String schema1 = RandomTestUtil.randomString();
		String schema2 = RandomTestUtil.randomString();
		String schema3 = RandomTestUtil.randomString();

		Mockito.when(
			db.getLockedQueryInfos(connection)
		).thenReturn(
			Arrays.asList(
				new DB.QueryInfo(
					300000, id1, query1, schema1,
					RandomTestUtil.randomString()),
				new DB.QueryInfo(
					600000, id2, query2, schema2,
					RandomTestUtil.randomString()),
				new DB.QueryInfo(
					900000, id3, query3, schema3,
					RandomTestUtil.randomString()))
		);

		List<LogEntry> logEntries = logCapture.getLogEntries();

		logEntries.clear();

		_poll(_scheduledExecutorService);

		Assert.assertEquals(logEntries.toString(), 3, logEntries.size());

		LogEntry logEntry1 = logEntries.get(0);

		Assert.assertEquals(
			StringBundler.concat(
				"Locked query \"", query1, "\" with ID ", id1, " in schema \"",
				schema1, "\" has been running for 300 seconds"),
			logEntry1.getMessage());

		LogEntry logEntry2 = logEntries.get(1);

		Assert.assertEquals(
			StringBundler.concat(
				"Locked query \"", query2, "\" with ID ", id2, " in schema \"",
				schema2, "\" has been running for 600 seconds"),
			logEntry2.getMessage());

		LogEntry logEntry3 = logEntries.get(2);

		Assert.assertEquals(
			StringBundler.concat(
				"Locked query \"", query3, "\" with ID ", id3, " in schema \"",
				schema3, "\" has been running for 900 seconds"),
			logEntry3.getMessage());
	}

	private void _testPollWithNoLockedQueries(
			Connection connection, DB db, LogCapture logCapture)
		throws Exception {

		Mockito.when(
			db.getLockedQueryInfos(connection)
		).thenReturn(
			Collections.emptyList()
		);

		List<LogEntry> logEntries = logCapture.getLogEntries();

		logEntries.clear();

		_poll(_scheduledExecutorService);

		Assert.assertEquals(logEntries.toString(), 0, logEntries.size());
	}

	private void _testPollWithOneLockedQuery(
			Connection connection, DB db, LogCapture logCapture)
		throws Exception {

		String id = RandomTestUtil.randomString();
		String query = RandomTestUtil.randomString();
		String schema = RandomTestUtil.randomString();

		Mockito.when(
			db.getLockedQueryInfos(connection)
		).thenReturn(
			Collections.singletonList(
				new DB.QueryInfo(
					30000, id, query, schema, RandomTestUtil.randomString()))
		);

		List<LogEntry> logEntries = logCapture.getLogEntries();

		logEntries.clear();

		_poll(_scheduledExecutorService);

		Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

		LogEntry logEntry = logEntries.get(0);

		Assert.assertEquals(
			StringBundler.concat(
				"Locked query \"", query, "\" with ID ", id, " in schema \"",
				schema, "\" has been running for 30 seconds"),
			logEntry.getMessage());
		Assert.assertEquals("WARN", logEntry.getPriority());
	}

	private void _testPollWithSQLException(
			Connection connection, DB db, LogCapture logCapture)
		throws Exception {

		String message = RandomTestUtil.randomString();

		Mockito.when(
			db.getLockedQueryInfos(connection)
		).thenThrow(
			new SQLException(message)
		);

		List<LogEntry> logEntries = logCapture.getLogEntries();

		logEntries.clear();

		Assert.assertThrows(
			SQLException.class, () -> _poll(_scheduledExecutorService));

		Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

		LogEntry logEntry = logEntries.get(0);

		Assert.assertEquals(
			"Upgrade query monitoring is disabled: " + message,
			logEntry.getMessage());
	}

	private void _testPollWithSQLExceptionWhileStopping(
			Connection connection, DB db, LogCapture logCapture)
		throws Exception {

		Mockito.doThrow(
			new SQLException(RandomTestUtil.randomString())
		).when(
			db
		).getLockedQueryInfos(
			connection
		);

		ScheduledExecutorService scheduledExecutorService =
			Executors.newSingleThreadScheduledExecutor();

		scheduledExecutorService.shutdown();

		List<LogEntry> logEntries = logCapture.getLogEntries();

		logEntries.clear();

		_poll(scheduledExecutorService);

		Assert.assertEquals(logEntries.toString(), 0, logEntries.size());
	}

	private static final String _SCHEDULED_EXECUTOR_SERVICE =
		"_scheduledExecutorService";

	private final ScheduledExecutorService _scheduledExecutorService =
		Mockito.mock(ScheduledExecutorService.class);

}