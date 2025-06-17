/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.db.index.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.db.DBResourceUtil;
import com.liferay.portal.db.index.IndexUpdaterUtil;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.sql.Connection;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
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
 * @author Alberto Chaparro
 */
@RunWith(Arquillian.class)
public class IndexUpdaterUtilTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(IndexUpdaterUtilTest.class);

		CountDownLatch countDownLatch = new CountDownLatch(1);

		BundleTracker<Bundle> bundleTracker = new BundleTracker<Bundle>(
			bundle.getBundleContext(), Bundle.ACTIVE, null) {

			@Override
			public Bundle addingBundle(Bundle bundle, BundleEvent event) {
				String symbolicName = bundle.getSymbolicName();

				if (symbolicName.equals("com.liferay.portal.lock.service")) {
					_moduleBundle = bundle;

					countDownLatch.countDown();

					close();
				}

				return null;
			}

		};

		bundleTracker.open();

		_connection = DataAccess.getConnection();

		_db = DBManagerUtil.getDB();

		_dbInspector = new DBInspector(_connection);

		countDownLatch.await(10, TimeUnit.SECONDS);

		_initIndexNames();
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		DataAccess.cleanUp(_connection);

		ReflectionTestUtil.invoke(
			IndexUpdaterUtil.class, "_clearProcessedServletContextNames", null,
			null);
	}

	@Before
	public void setUp() {
		ReflectionTestUtil.invoke(
			IndexUpdaterUtil.class, "_clearProcessedServletContextNames", null,
			null);
	}

	@Test
	public void testUpdateAllIndexes() throws Exception {
		_dropIndex(_moduleTableIndexName, _moduleIndexName);
		_dropIndex(_portalTableIndexName, _portalIndexName);

		IndexUpdaterUtil.updateAllIndexes();

		Assert.assertTrue(
			_dbInspector.hasIndex(_moduleTableIndexName, _moduleIndexName));
		Assert.assertTrue(
			_dbInspector.hasIndex(_portalTableIndexName, _portalIndexName));
	}

	@Test
	public void testUpdateAllIndexesAfterUpdateIndexes() throws Exception {
		IndexUpdaterUtil.updateIndexes(_moduleBundle);

		_dropIndex(_moduleTableIndexName, _moduleIndexName);

		IndexUpdaterUtil.updateAllIndexes();

		Assert.assertFalse(
			_dbInspector.hasIndex(_moduleTableIndexName, _moduleIndexName));

		IndexUpdaterUtil.updateIndexes(_moduleBundle);

		Assert.assertTrue(
			_dbInspector.hasIndex(_moduleTableIndexName, _moduleIndexName));
	}

	@Test
	public void testUpdateAllIndexesAfterUpdatePortalIndexes()
		throws Exception {

		IndexUpdaterUtil.updatePortalIndexes();

		_dropIndex(_portalTableIndexName, _portalIndexName);

		IndexUpdaterUtil.updateAllIndexes();

		Assert.assertFalse(
			_dbInspector.hasIndex(_portalTableIndexName, _portalIndexName));

		IndexUpdaterUtil.updatePortalIndexes();

		Assert.assertTrue(
			_dbInspector.hasIndex(_portalTableIndexName, _portalIndexName));
	}

	@Test
	public void testUpdateIndexes() throws Exception {
		_dropIndex(_moduleTableIndexName, _moduleIndexName);

		IndexUpdaterUtil.updateIndexes(_moduleBundle);

		Assert.assertTrue(
			_dbInspector.hasIndex(_moduleTableIndexName, _moduleIndexName));
	}

	@Test
	public void testUpdatePortalIndexes() throws Exception {
		_dropIndex(_portalTableIndexName, _portalIndexName);

		IndexUpdaterUtil.updatePortalIndexes();

		Assert.assertTrue(
			_dbInspector.hasIndex(_portalTableIndexName, _portalIndexName));
	}

	private static String _getIndexName(String indexesSQL) {
		return indexesSQL.substring(
			indexesSQL.indexOf("IX_"), indexesSQL.indexOf(" on"));
	}

	private static String _getTableIndexName(String indexesSQL) {
		return indexesSQL.substring(
			indexesSQL.indexOf("on ") + 3, indexesSQL.indexOf(" ("));
	}

	private static void _initIndexNames() throws Exception {
		String moduleIndexesSQL = DBResourceUtil.getModuleIndexesSQL(
			_moduleBundle);

		String portalIndexesSQL = DBResourceUtil.getPortalIndexesSQL();

		_moduleIndexName = _getIndexName(moduleIndexesSQL);
		_moduleTableIndexName = _getTableIndexName(moduleIndexesSQL);

		_portalIndexName = _getIndexName(portalIndexesSQL);
		_portalTableIndexName = _getTableIndexName(portalIndexesSQL);
	}

	private void _dropIndex(String tableName, String indexName)
		throws Exception {

		_db.runSQL(
			StringBundler.concat("drop index ", indexName, " on ", tableName));
	}

	private static Connection _connection;
	private static DB _db;
	private static DBInspector _dbInspector;
	private static Bundle _moduleBundle;
	private static String _moduleIndexName;
	private static String _moduleTableIndexName;
	private static String _portalIndexName;
	private static String _portalTableIndexName;

}