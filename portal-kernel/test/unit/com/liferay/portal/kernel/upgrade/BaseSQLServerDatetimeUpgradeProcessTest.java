/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.upgrade;

import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Types;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Jorge Avalos
 */
public class BaseSQLServerDatetimeUpgradeProcessTest {

	@Test
	public void testUpgradeTableAlreadyTargetType() throws Exception {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				BaseSQLServerDatetimeUpgradeProcess.class.getName(),
				LoggerTestUtil.WARN)) {

			logCapture.resetPriority(LoggerTestUtil.INFO);

			_upgradeTable(_createConnection(true, "datetime2", 6));

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals("INFO", logEntry.getPriority());
			Assert.assertEquals(
				"Column publishDate in table Layout already is datetime2(6)",
				logEntry.getMessage());
		}
	}

	@Test
	public void testUpgradeTableColumnAbsent() throws Exception {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				BaseSQLServerDatetimeUpgradeProcess.class.getName(),
				LoggerTestUtil.WARN)) {

			logCapture.resetPriority(LoggerTestUtil.INFO);

			_upgradeTable(
				_createConnection(
					false, RandomTestUtil.randomString(),
					RandomTestUtil.randomInt()));

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals("ERROR", logEntry.getPriority());
			Assert.assertEquals(
				"Column publishDate does not exist in table Layout",
				logEntry.getMessage());
		}
	}

	public static class TestTable {

		public static final Map<String, Integer> TABLE_COLUMNS_MAP =
			HashMapBuilder.put(
				"publishDate", Types.TIMESTAMP
			).build();

		public static final String TABLE_NAME = "Layout";

	}

	private Connection _createConnection(
			boolean columnPresent, String typeName, int decimalDigits)
		throws Exception {

		Connection connection = Mockito.mock(Connection.class);

		DatabaseMetaData databaseMetaData = Mockito.mock(
			DatabaseMetaData.class);

		Mockito.when(
			connection.getMetaData()
		).thenReturn(
			databaseMetaData
		);

		ResultSet tableResultSet = Mockito.mock(ResultSet.class);

		Mockito.when(
			databaseMetaData.getTables(
				Mockito.any(), Mockito.any(), Mockito.anyString(),
				Mockito.any())
		).thenReturn(
			tableResultSet
		);

		Mockito.when(
			tableResultSet.next()
		).thenReturn(
			true
		);

		ResultSet columnResultSet = Mockito.mock(ResultSet.class);

		Mockito.when(
			databaseMetaData.getColumns(
				Mockito.isNull(), Mockito.isNull(), Mockito.anyString(),
				Mockito.anyString())
		).thenReturn(
			columnResultSet
		);

		Mockito.when(
			columnResultSet.next()
		).thenReturn(
			columnPresent
		);

		Mockito.when(
			columnResultSet.getString("TYPE_NAME")
		).thenReturn(
			typeName
		);

		Mockito.when(
			columnResultSet.getInt("DECIMAL_DIGITS")
		).thenReturn(
			decimalDigits
		);

		return connection;
	}

	private void _upgradeTable(Connection connection) {
		BaseSQLServerDatetimeUpgradeProcess
			baseSQLServerDatetimeUpgradeProcess =
				new BaseSQLServerDatetimeUpgradeProcess(
					new Class<?>[] {TestTable.class});

		ReflectionTestUtil.setFieldValue(
			baseSQLServerDatetimeUpgradeProcess, "connection", connection);

		ReflectionTestUtil.invoke(
			baseSQLServerDatetimeUpgradeProcess, "_upgradeTable",
			new Class<?>[] {Class.class}, TestTable.class);
	}

}