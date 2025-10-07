/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.db.index.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.db.DBResourceUtil;
import com.liferay.portal.db.index.IndexUpdaterUtil;
import com.liferay.portal.events.StartupHelperUtil;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DuplicateUniqueFinderRowsCleaner;
import com.liferay.portal.kernel.dao.db.IndexMetadata;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.List;
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
	public void testUpdateAllIndexesUpdatesPrimaryKeys() throws Exception {
		_db.removePrimaryKey(_connection, _moduleTableIndexName);
		_db.removePrimaryKey(_connection, _portalTableIndexName);

		IndexUpdaterUtil.updateAllIndexes();

		Assert.assertTrue(
			ArrayUtil.isNotEmpty(
				_db.getPrimaryKeyColumnNames(
					_connection, _moduleTableIndexName)));
		Assert.assertTrue(
			ArrayUtil.isNotEmpty(
				_db.getPrimaryKeyColumnNames(
					_connection, _portalTableIndexName)));
	}

	@Test
	public void testUpdateIndexes() throws Exception {
		_dropIndex(_moduleTableIndexName, _moduleIndexName);

		IndexUpdaterUtil.updateIndexes(_moduleBundle);

		Assert.assertTrue(
			_dbInspector.hasIndex(_moduleTableIndexName, _moduleIndexName));
	}

	@Test
	public void testUpdateIndexRetry() throws Exception {
		_db.runSQL(
			StringBundler.concat(
				"create table TestTable (id1 INTEGER, id2 INTEGER, column1 ",
				"INTEGER, column2 INTEGER, column3 VARCHAR(255), column4 ",
				"VARCHAR(255), primary key (id1, id2))"));

		try {
			_db.runSQL("insert into TestTable values(1, 2, 3, 4, '5', '6')");
			_db.runSQL("insert into TestTable values(7, 9, 10, 11, '5', '6')");

			boolean upgrading = StartupHelperUtil.isUpgrading();

			StartupHelperUtil.setUpgrading(true);

			try (AutoCloseable autoCloseable =
					() -> StartupHelperUtil.setUpgrading(upgrading);
				LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
					DuplicateUniqueFinderRowsCleaner.class.getName(),
					LoggerTestUtil.WARN)) {

				ReflectionTestUtil.invoke(
					IndexUpdaterUtil.class, "_updateIndexes",
					new Class<?>[] {String.class, String.class}, "TestTable",
					"create unique index IX_TestTable on TestTable(column3" +
						"[$COLUMN_LENGTH:255$], column4[$COLUMN_LENGTH:255$])");

				List<LogEntry> logEntries = logCapture.getLogEntries();

				Assert.assertEquals(
					logEntries.toString(), 1, logEntries.size());

				LogEntry logEntry = logEntries.get(0);

				Assert.assertTrue(
					StringUtil.startsWith(
						logEntry.getMessage(),
						"Deleted row from table TestTable due to duplicate " +
							"values in finder columns column3, column4"));
			}

			try (Connection connection = DataAccess.getConnection();
				PreparedStatement preparedStatement =
					connection.prepareStatement(
						"select count(*) from TestTable")) {

				try (ResultSet resultSet = preparedStatement.executeQuery()) {
					Assert.assertTrue(resultSet.next());

					Assert.assertEquals(1, resultSet.getInt(1));
				}

				List<IndexMetadata> indexMetadatas = _db.getIndexMetadatas(
					connection, "TestTable", "column3", true);

				Assert.assertEquals(
					indexMetadatas.toString(), 1, indexMetadatas.size());
			}
		}
		finally {
			_db.runSQL("DROP_TABLE_IF_EXISTS(TestTable)");
		}
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