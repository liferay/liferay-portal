/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.db.index;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.db.DBResourceUtil;
import com.liferay.portal.kernel.dependency.manager.DependencyManagerSyncUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Mariano Álvaro Sáiz
 */
public class IndexUpdaterUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetTableIndexesSQLMap() {
		String tablesSQL =
			"create table TestTable1 (field1 INT, field2 INT);\n\n" +
				"create table TestTable2 (field1 INT, field2 INT);";
		String indexesSQL = "create index IX_TEST1 on TestTable1 (field2);";

		Map<String, String> tableIndexesSQLMap = ReflectionTestUtil.invoke(
			IndexUpdaterUtil.class, "_getTableIndexesSQLMap",
			new Class<?>[] {String.class, String.class}, tablesSQL, indexesSQL);

		Assert.assertEquals(
			tableIndexesSQLMap.toString(), 2, tableIndexesSQLMap.size());
		Assert.assertEquals(tableIndexesSQLMap.get("TestTable1"), indexesSQL);
		Assert.assertEquals(
			tableIndexesSQLMap.get("TestTable2"), StringPool.BLANK);
	}

	@Test
	public void testUpdateAllIndexes() throws Exception {
		try (MockedStatic<DBResourceUtil> dbResourceUtilMockedStatic =
				Mockito.mockStatic(DBResourceUtil.class);
			MockedStatic<DependencyManagerSyncUtil>
				dependencyManagerSyncUtilMockedStatic = Mockito.mockStatic(
					DependencyManagerSyncUtil.class)) {

			IndexUpdaterUtil.updateAllIndexes();

			ArgumentCaptor<Callable<?>> callableArgumentCaptor =
				ArgumentCaptor.forClass(Callable.class);

			dependencyManagerSyncUtilMockedStatic.verify(
				() -> DependencyManagerSyncUtil.registerSyncCallable(
					callableArgumentCaptor.capture()));

			ArgumentCaptor<FutureTask<?>> futureTaskArgumentCaptor =
				ArgumentCaptor.forClass(FutureTask.class);

			dependencyManagerSyncUtilMockedStatic.verify(
				() -> DependencyManagerSyncUtil.registerSyncFutureTask(
					futureTaskArgumentCaptor.capture(), Mockito.anyString()));

			FutureTask<?> callableFutureTask = new FutureTask<>(
				callableArgumentCaptor.getValue());

			Thread thread = new Thread(callableFutureTask);

			thread.start();

			try {
				callableFutureTask.get(1, TimeUnit.SECONDS);

				Assert.fail();
			}
			catch (TimeoutException timeoutException) {
			}

			try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
					IndexUpdaterUtil.class.getName(), LoggerTestUtil.ERROR)) {

				FutureTask<?> futureTask = futureTaskArgumentCaptor.getValue();

				futureTask.cancel(true);

				callableFutureTask.get(10, TimeUnit.SECONDS);

				List<LogEntry> logEntries = logCapture.getLogEntries();

				Assert.assertEquals(
					logEntries.toString(), 1, logEntries.size());
			}
		}
	}

}