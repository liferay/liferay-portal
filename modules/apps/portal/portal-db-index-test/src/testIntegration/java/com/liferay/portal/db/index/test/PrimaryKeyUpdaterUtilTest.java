/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.db.index.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.db.index.PrimaryKeyUpdaterUtil;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.sql.Connection;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.BundleTracker;

/**
 * @author Mariano Álvaro Sáiz
 */
@RunWith(Arquillian.class)
public class PrimaryKeyUpdaterUtilTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(
			PrimaryKeyUpdaterUtilTest.class);

		CountDownLatch countDownLatch = new CountDownLatch(1);

		BundleTracker<Bundle> bundleTracker = new BundleTracker<Bundle>(
			bundle.getBundleContext(), Bundle.ACTIVE, null) {

			@Override
			public Bundle addingBundle(Bundle bundle, BundleEvent event) {
				String symbolicName = bundle.getSymbolicName();

				if (symbolicName.equals("com.liferay.portal.lock.service")) {
					countDownLatch.countDown();

					close();
				}

				return null;
			}

		};

		bundleTracker.open();

		countDownLatch.await(10, TimeUnit.SECONDS);
	}

	@Test
	public void testUpdateAllPrimaryKeys() throws Exception {
		DB db = DBManagerUtil.getDB();

		try (Connection connection = DataAccess.getConnection()) {
			db.removePrimaryKey(connection, "Company");
			db.removePrimaryKey(connection, "Lock_");

			PrimaryKeyUpdaterUtil.updateAllPrimaryKeys();

			Assert.assertTrue(
				ArrayUtil.isNotEmpty(
					db.getPrimaryKeyColumnNames(connection, "Company")));
			Assert.assertTrue(
				ArrayUtil.isNotEmpty(
					db.getPrimaryKeyColumnNames(connection, "Lock_")));
		}
	}

}