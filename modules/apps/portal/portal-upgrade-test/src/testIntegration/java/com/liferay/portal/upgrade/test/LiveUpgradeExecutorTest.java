/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.AssumeTestRule;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.live.LiveUpgradeExecutor;
import com.liferay.portal.upgrade.live.LiveUpgradeProcessFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Kevin Lee
 */
@RunWith(Arquillian.class)
public class LiveUpgradeExecutorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new AssumeTestRule("assume"), new LiferayIntegrationTestRule());

	public static void assume() {
		Assume.assumeFalse(PropsValues.DATABASE_PARTITION_ENABLED);
	}

	@BeforeClass
	public static void setUpClass() throws Exception {
		_connection = DataAccess.getConnection();

		_dbInspector = new DBInspector(_connection);

		_db = DBManagerUtil.getDB();
	}

	@AfterClass
	public static void tearDownClass() {
		DataAccess.cleanUp(_connection);
	}

	@Before
	public void setUp() throws Exception {
		_db.runSQL(
			StringBundler.concat(
				"create table ", _TABLE_NAME,
				" (id LONG not null primary key, name VARCHAR(128) not null, ",
				"description VARCHAR(255) null)"));
		_db.runSQL(
			StringBundler.concat(
				"insert into ", _TABLE_NAME,
				" (id, name) values (1, 'test_a')"));
		_db.runSQL(
			StringBundler.concat(
				"insert into ", _TABLE_NAME,
				" (id, name) values (2, 'test_b')"));
	}

	@After
	public void tearDown() throws Exception {
		_db.runSQL("DROP_TABLE_IF_EXISTS(" + _TABLE_NAME + ")");
		_db.runSQL("DROP_TABLE_IF_EXISTS(" + _getArchiveTableName() + ")");
		_db.runSQL("DROP_TABLE_IF_EXISTS(" + _getTempTableName() + ")");
	}

	@Test
	public void testAddColumns() throws Exception {
		_liveUpgradeExecutor.upgrade(
			_TABLE_NAME,
			LiveUpgradeProcessFactory.addColumns(
				"content SBLOB", "version LONG default 1 not null"));

		Assert.assertTrue(_dbInspector.hasColumn(_TABLE_NAME, "content"));
		Assert.assertTrue(_dbInspector.hasColumn(_TABLE_NAME, "version"));

		try (PreparedStatement preparedStatement = _connection.prepareStatement(
				"select * from " + _TABLE_NAME + " order by id asc");
			ResultSet resultSet = preparedStatement.executeQuery()) {

			Assert.assertTrue(resultSet.next());

			Assert.assertEquals(1, resultSet.getLong("id"));
			Assert.assertEquals(1, resultSet.getLong("version"));

			Assert.assertTrue(resultSet.next());

			Assert.assertEquals(2, resultSet.getLong("id"));
			Assert.assertEquals(1, resultSet.getLong("version"));

			Assert.assertFalse(resultSet.next());
		}
	}

	@Test
	public void testAlterColumnName() throws Exception {
		_liveUpgradeExecutor.upgrade(
			_TABLE_NAME,
			LiveUpgradeProcessFactory.alterColumnName(
				"name", "title VARCHAR(128) not null"));

		Assert.assertFalse(_dbInspector.hasColumn(_TABLE_NAME, "name"));
		Assert.assertTrue(_dbInspector.hasColumn(_TABLE_NAME, "title"));

		_checkData("title");
	}

	@Test
	public void testAlterColumnType() throws Exception {
		_liveUpgradeExecutor.upgrade(
			_TABLE_NAME,
			LiveUpgradeProcessFactory.alterColumnType(
				"name", "VARCHAR(255) null"),
			LiveUpgradeProcessFactory.alterColumnType(
				"description", "VARCHAR(255) default 'test' not null"));

		Assert.assertTrue(
			_dbInspector.hasColumnType(
				_TABLE_NAME, "name", "VARCHAR(255) null"));
		Assert.assertTrue(
			_dbInspector.hasColumnType(
				_TABLE_NAME, "description",
				"VARCHAR(255) default 'test' not null"));

		try (PreparedStatement preparedStatement = _connection.prepareStatement(
				"select * from " + _TABLE_NAME + " order by id asc");
			ResultSet resultSet = preparedStatement.executeQuery()) {

			Assert.assertTrue(resultSet.next());

			Assert.assertEquals(1, resultSet.getLong("id"));
			Assert.assertEquals("test_a", resultSet.getString("name"));
			Assert.assertEquals("test", resultSet.getString("description"));

			Assert.assertTrue(resultSet.next());

			Assert.assertEquals(2, resultSet.getLong("id"));
			Assert.assertEquals("test_b", resultSet.getString("name"));
			Assert.assertEquals("test", resultSet.getString("description"));

			Assert.assertFalse(resultSet.next());
		}
	}

	@Test
	public void testDropColumns() throws Exception {
		_liveUpgradeExecutor.upgrade(
			_TABLE_NAME, LiveUpgradeProcessFactory.dropColumns("name"));

		Assert.assertFalse(_dbInspector.hasColumn(_TABLE_NAME, "name"));
	}

	@Test
	public void testEmptyUpgrade() throws Exception {
		try {
			_liveUpgradeExecutor.upgrade(_TABLE_NAME);

			Assert.fail();
		}
		catch (IllegalArgumentException illegalArgumentException) {
			Assert.assertEquals(
				"At least one live upgrade process is required",
				illegalArgumentException.getMessage());
		}
	}

	@Test
	public void testMultipleUpgrades() throws Exception {
		_liveUpgradeExecutor.upgrade(
			_TABLE_NAME,
			LiveUpgradeProcessFactory.addColumns(
				"content SBLOB", "version LONG null"),
			LiveUpgradeProcessFactory.alterColumnName(
				"name", "title VARCHAR(128) not null"),
			LiveUpgradeProcessFactory.alterColumnType(
				"title", "VARCHAR(255) null"),
			LiveUpgradeProcessFactory.dropColumns("content"));

		Assert.assertFalse(_dbInspector.hasColumn(_TABLE_NAME, "content"));
		Assert.assertTrue(_dbInspector.hasColumn(_TABLE_NAME, "id"));
		Assert.assertFalse(_dbInspector.hasColumn(_TABLE_NAME, "name"));
		Assert.assertTrue(_dbInspector.hasColumn(_TABLE_NAME, "title"));
		Assert.assertTrue(_dbInspector.hasColumn(_TABLE_NAME, "version"));

		Assert.assertTrue(
			_dbInspector.hasColumnType(
				_TABLE_NAME, "title", "VARCHAR(255) null"));

		try (PreparedStatement preparedStatement = _connection.prepareStatement(
				"select * from " + _TABLE_NAME + " order by id asc");
			ResultSet resultSet = preparedStatement.executeQuery()) {

			Assert.assertTrue(resultSet.next());

			Assert.assertEquals(1, resultSet.getLong("id"));
			Assert.assertEquals("test_a", resultSet.getString("title"));
			Assert.assertEquals(0, resultSet.getLong("version"));

			Assert.assertTrue(resultSet.next());

			Assert.assertEquals(2, resultSet.getLong("id"));
			Assert.assertEquals("test_b", resultSet.getString("title"));
			Assert.assertEquals(0, resultSet.getLong("version"));

			Assert.assertFalse(resultSet.next());
		}
	}

	private void _checkData(String columnName) throws Exception {
		try (PreparedStatement preparedStatement = _connection.prepareStatement(
				"select * from " + _TABLE_NAME + " order by id asc");
			ResultSet resultSet = preparedStatement.executeQuery()) {

			Assert.assertTrue(resultSet.next());

			Assert.assertEquals("test_a", resultSet.getString(columnName));
			Assert.assertEquals(1, resultSet.getLong("id"));

			Assert.assertTrue(resultSet.next());

			Assert.assertEquals("test_b", resultSet.getString(columnName));
			Assert.assertEquals(2, resultSet.getLong("id"));

			Assert.assertFalse(resultSet.next());
		}
	}

	private String _getArchiveTableName() {
		return ReflectionTestUtil.invoke(
			_liveUpgradeExecutor, "_getArchiveTableName",
			new Class<?>[] {String.class}, _TABLE_NAME);
	}

	private String _getTempTableName() {
		return ReflectionTestUtil.invoke(
			_liveUpgradeExecutor, "_getTempTableName",
			new Class<?>[] {String.class}, _TABLE_NAME);
	}

	private static final String _TABLE_NAME = "LiveUpgradeTest";

	private static Connection _connection;
	private static DB _db;
	private static DBInspector _dbInspector;

	@Inject
	private LiveUpgradeExecutor _liveUpgradeExecutor;

}