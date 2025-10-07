/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.db.partition.DBPartition;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.AssumeTestRule;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.upgrade.DummyUpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeException;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.sql.Connection;
import java.sql.DatabaseMetaData;

import java.util.concurrent.FutureTask;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Luis Ortiz
 */
@RunWith(Arquillian.class)
public class UpgradeProcessDBPartitionTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new AssumeTestRule("assume"), new LiferayIntegrationTestRule());

	public static void assume() {
		Assume.assumeTrue(DBPartition.isPartitionEnabled());

		DB db = DBManagerUtil.getDB();

		Assume.assumeTrue(db.isSupportsDBPartition());
	}

	@Test
	public void testUpgradeWithDatabasePartitionDisabled()
		throws UpgradeException {

		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"DATABASE_PARTITION_ENABLED", false)) {

			UpgradeProcess upgradeProcess =
				new AssertConnectionUpgradeProcess();

			upgradeProcess.upgrade();
		}
	}

	@Test
	public void testUpgradeWithDatabasePartitionEnabled()
		throws UpgradeException {

		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"DATABASE_PARTITION_ENABLED", true)) {

			UpgradeProcess upgradeProcess =
				new AssertConnectionUpgradeProcess();

			upgradeProcess.upgrade();
		}
	}

	private class AssertConnectionUpgradeProcess extends DummyUpgradeProcess {

		@Override
		protected void process(UnsafeConsumer<Long, Exception> unsafeConsumer)
			throws Exception {

			if (DBPartition.isPartitionEnabled()) {
				Assert.assertNotSame(_getConnection(), _getConnection());
			}
			else {
				Assert.assertSame(_getConnection(), _getConnection());
			}
		}

		private Connection _getConnection() throws Exception {
			FutureTask<Connection> futureTask = new FutureTask<>(
				() -> {
					DatabaseMetaData databaseMetaData =
						connection.getMetaData();

					return databaseMetaData.getConnection();
				});

			Thread thread = new Thread(futureTask);

			thread.start();

			return futureTask.get();
		}

	}

}