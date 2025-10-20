/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.jdbc.util;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.jdbc.DataSourceFactoryUtil;
import com.liferay.portal.kernel.servlet.PortalSessionContext;
import com.liferay.portal.kernel.servlet.PortalSessionThreadLocal;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.spring.hibernate.DialectDetector;
import com.liferay.portal.spring.hibernate.SpringHibernateThreadLocalUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.util.FastDateFormatFactoryImpl;
import com.liferay.portal.util.FileImpl;

import java.io.File;

import java.util.List;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpSession;

/**
 * @author Tina Tian
 */
public class DynamicDataSourceTest {

	@ClassRule
	@Rule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		FastDateFormatFactoryUtil fastDateFormatFactoryUtil =
			new FastDateFormatFactoryUtil();

		fastDateFormatFactoryUtil.setFastDateFormatFactory(
			new FastDateFormatFactoryImpl());

		FileUtil fileUtil = new FileUtil();

		fileUtil.setFile(new FileImpl());

		_currentTransactionReadOnly = ReflectionTestUtil.getFieldValue(
			SpringHibernateThreadLocalUtil.class,
			"_currentTransactionReadOnly");

		_dbManagerUtilMockedStatic.when(
			() -> DBManagerUtil.getDBType(Mockito.any())
		).thenReturn(
			null
		);

		_dialectDetectorMockedStatic.when(
			() -> DialectDetector.getDialect(Mockito.any())
		).thenReturn(
			null
		);

		_tempDir = FileUtil.createTempFolder();

		DataSource readDataSource = DataSourceFactoryUtil.initDataSource(
			"org.hsqldb.jdbc.JDBCDriver",
			"jdbc:hsqldb:" + _tempDir.getAbsolutePath() + "/lportal-read;",
			"sa", StringPool.BLANK, StringPool.BLANK);
		DataSource writeDataSource = DataSourceFactoryUtil.initDataSource(
			"org.hsqldb.jdbc.JDBCDriver",
			"jdbc:hsqldb:" + _tempDir.getAbsolutePath() + "/lportal-write;",
			"sa", StringPool.BLANK, StringPool.BLANK);

		_dynamicDataSource = new DynamicDataSource(
			readDataSource, writeDataSource);

		_writeDynamicDataSource = ReflectionTestUtil.getFieldValue(
			DynamicDataSource.class, "_writeDynamicDataSource");
	}

	@After
	public void tearDown() {
		_currentTransactionReadOnly.remove();

		_dbManagerUtilMockedStatic.close();
		_dialectDetectorMockedStatic.close();

		FileUtil.deltree(_tempDir);

		_writeDynamicDataSource.remove();
	}

	@Test
	public void testGetDataSource() {
		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"JDBC_READ_DATA_SOURCE_UNAVAILABLE_TIMEOUT", 0)) {

			_testGetDataSource(
				false, _dynamicDataSource.getWriteDataSource(),
				List.of("Returning write data source"), false);
			_testGetDataSource(
				false, _dynamicDataSource.getWriteDataSource(),
				List.of("Returning write data source"), true);
			_testGetDataSource(
				true, _dynamicDataSource.getReadDataSource(),
				List.of("Returning read data source"), false);
			_testGetDataSource(
				true, _dynamicDataSource.getWriteDataSource(),
				List.of("Returning write data source"), true);
		}
	}

	@Test
	public void testGetDataSourceWithTimeout() {
		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"JDBC_READ_DATA_SOURCE_UNAVAILABLE_TIMEOUT",
					Long.MAX_VALUE)) {

			_testGetDataSource(
				false, _dynamicDataSource.getWriteDataSource(),
				List.of(
					"No context HTTP session exists, skip setting the write " +
						"data source's last used date",
					"Returning write data source"),
				false);
			_testGetDataSource(
				false, _dynamicDataSource.getWriteDataSource(),
				List.of(
					"No context HTTP session exists, skip setting the write " +
						"data source's last used date",
					"Returning write data source"),
				true);
			_testGetDataSource(
				true, _dynamicDataSource.getReadDataSource(),
				List.of(
					"No context HTTP session exists, skip getting the write " +
						"data source's last used date",
					"Returning read data source"),
				false);
			_testGetDataSource(
				true, _dynamicDataSource.getWriteDataSource(),
				List.of("Returning write data source"), true);
		}
	}

	@Test
	public void testGetDataSourceWithTimeoutAndHttpSession() {
		String sessionId = RandomTestUtil.randomString();

		PortalSessionContext.put(sessionId, new MockHttpSession());

		ThreadLocal<String> sessionIdThreadLocal =
			ReflectionTestUtil.getFieldValue(
				PortalSessionThreadLocal.class, "_sessionId");

		sessionIdThreadLocal.set(sessionId);

		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"JDBC_READ_DATA_SOURCE_UNAVAILABLE_TIMEOUT",
					Long.MAX_VALUE)) {

			_testGetDataSource(
				false, _dynamicDataSource.getWriteDataSource(),
				List.of("Returning write data source"), false);
			_testGetDataSource(
				false, _dynamicDataSource.getWriteDataSource(),
				List.of("Returning write data source"), true);
			_testGetDataSource(
				true, _dynamicDataSource.getWriteDataSource(),
				List.of("Returning write data source"), false);
			_testGetDataSource(
				true, _dynamicDataSource.getWriteDataSource(),
				List.of("Returning write data source"), true);
		}
		finally {
			sessionIdThreadLocal.remove();
		}
	}

	private void _testGetDataSource(
		boolean currentTransactionReadOnly, DataSource expectedDataSource,
		List<String> expectedLogMessages, boolean writeDataSource) {

		_writeDynamicDataSource.set(writeDataSource);

		if (currentTransactionReadOnly) {
			_currentTransactionReadOnly.set(true);
		}
		else {
			_currentTransactionReadOnly.remove();
		}

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.dao.jdbc.util.DynamicDataSource",
				LoggerTestUtil.TRACE)) {

			Assert.assertSame(
				expectedDataSource,
				ReflectionTestUtil.invoke(
					_dynamicDataSource, "_getDataSource", new Class<?>[0]));

			List<LogEntry> logEntries = logCapture.getLogEntries();

			if (expectedLogMessages == null) {
				Assert.assertTrue(logEntries.toString(), logEntries.isEmpty());
			}
			else {
				Assert.assertEquals(
					logEntries.toString(), expectedLogMessages.size(),
					logEntries.size());

				for (int i = 0; i < expectedLogMessages.size(); i++) {
					LogEntry logEntry = logEntries.get(i);

					Assert.assertEquals(
						expectedLogMessages.get(i), logEntry.getMessage());
				}
			}
		}
	}

	private ThreadLocal<Boolean> _currentTransactionReadOnly;
	private final MockedStatic<DBManagerUtil> _dbManagerUtilMockedStatic =
		Mockito.mockStatic(DBManagerUtil.class);
	private final MockedStatic<DialectDetector> _dialectDetectorMockedStatic =
		Mockito.mockStatic(DialectDetector.class);
	private DynamicDataSource _dynamicDataSource;
	private File _tempDir;
	private ThreadLocal<Boolean> _writeDynamicDataSource;

}