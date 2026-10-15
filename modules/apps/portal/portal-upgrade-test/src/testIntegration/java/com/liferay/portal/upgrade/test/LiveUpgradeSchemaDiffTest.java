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
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.live.LiveUpgradeSchemaDiff;

import java.sql.Connection;

import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Assert;
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
public class LiveUpgradeSchemaDiffTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		_connection = DataAccess.getConnection();

		_db = DBManagerUtil.getDB();

		_db.runSQL(
			StringBundler.concat(
				"create table ", _TABLE_NAME,
				" (id LONG not null primary key, name VARCHAR(128) not null, ",
				"description VARCHAR(255) null)"));

		_dbInspector = new DBInspector(_connection);
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		_db.runSQL("DROP_TABLE_IF_EXISTS(" + _TABLE_NAME + ")");

		DataAccess.cleanUp(_connection);
	}

	@Before
	public void setUp() throws Exception {
		_liveUpgradeSchemaDiff = new LiveUpgradeSchemaDiff(
			_connection, _TABLE_NAME);
	}

	@Test
	public void testRecordAddColumns() throws Exception {
		_liveUpgradeSchemaDiff.recordAddColumns(
			"title VARCHAR(128) null", "version LONG default 1 not null");

		_checkSchemaDiff(
			HashMapBuilder.put(
				"description", "description"
			).put(
				"id", "id"
			).put(
				"name", "name"
			).build(),
			HashMapBuilder.put(
				"version", "1"
			).build());
	}

	@Test
	public void testRecordAlterColumnName() throws Exception {
		_liveUpgradeSchemaDiff.recordAlterColumnName("name", "title");

		_checkSchemaDiff(
			HashMapBuilder.put(
				"description", "description"
			).put(
				"id", "id"
			).put(
				"name", "title"
			).build(),
			new HashMap<>());
	}

	@Test
	public void testRecordAlterColumnType() throws Exception {
		_liveUpgradeSchemaDiff.recordAlterColumnType(
			"name", "VARCHAR(255) null");
		_liveUpgradeSchemaDiff.recordAlterColumnType(
			"id", "LONG default 1000 not null");
		_liveUpgradeSchemaDiff.recordAlterColumnType(
			"description", "VARCHAR(255) default 'test test' not null");

		_checkSchemaDiff(
			HashMapBuilder.put(
				"description", "description"
			).put(
				"id", "id"
			).put(
				"name", "name"
			).build(),
			HashMapBuilder.put(
				"description", "'test test'"
			).put(
				"id", "1000"
			).build());
	}

	@Test
	public void testRecordDropColumns() throws Exception {
		_liveUpgradeSchemaDiff.recordDropColumns("name");

		_checkSchemaDiff(
			HashMapBuilder.put(
				"description", "description"
			).put(
				"id", "id"
			).build(),
			new HashMap<>());
	}

	private void _checkSchemaDiff(
			Map<String, String> expectedColumnNamesMap,
			Map<String, String> expectedDefaultValuesMap)
		throws Exception {

		Map<String, String> actualColumnNamesMap =
			_liveUpgradeSchemaDiff.getResultColumnNamesMap();

		Assert.assertEquals(
			actualColumnNamesMap.toString(), expectedColumnNamesMap.size(),
			actualColumnNamesMap.size());

		for (Map.Entry<String, String> entry :
				expectedColumnNamesMap.entrySet()) {

			String expectedOldColumnName = _dbInspector.normalizeName(
				entry.getKey());

			Assert.assertTrue(
				actualColumnNamesMap.containsKey(expectedOldColumnName));
			Assert.assertEquals(
				_dbInspector.normalizeName(entry.getValue()),
				actualColumnNamesMap.get(expectedOldColumnName));
		}

		Map<String, String> actualDefaultValuesMap =
			_liveUpgradeSchemaDiff.getResultDefaultValuesMap();

		Assert.assertEquals(
			actualDefaultValuesMap.toString(), expectedDefaultValuesMap.size(),
			actualDefaultValuesMap.size());

		for (Map.Entry<String, String> entry :
				expectedDefaultValuesMap.entrySet()) {

			String columnName = _dbInspector.normalizeName(entry.getKey());

			Assert.assertTrue(actualDefaultValuesMap.containsKey(columnName));
			Assert.assertEquals(
				entry.getValue(), actualDefaultValuesMap.get(columnName));
		}
	}

	private static final String _TABLE_NAME = "LiveUpgradeSchemaTest";

	private static Connection _connection;
	private static DB _db;
	private static DBInspector _dbInspector;

	private LiveUpgradeSchemaDiff _liveUpgradeSchemaDiff;

}