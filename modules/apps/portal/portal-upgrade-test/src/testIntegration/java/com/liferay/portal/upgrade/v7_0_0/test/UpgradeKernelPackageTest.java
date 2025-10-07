/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v7_0_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.dao.orm.common.SQLTransformer;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.dao.orm.WildcardMode;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.v7_0_0.UpgradeKernelPackage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Preston Crary
 */
@RunWith(Arquillian.class)
public class UpgradeKernelPackageTest extends UpgradeKernelPackage {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		_db = DBManagerUtil.getDB();

		_db.runSQL(
			"create table UpgradeKernelPackageTest (" +
				"id LONG not null primary key, data VARCHAR(40) null, " +
					"textData VARCHAR(255) null)");
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		_db.runSQL("drop table UpgradeKernelPackageTest");
	}

	@Before
	public void setUp() throws Exception {
		connection = DataAccess.getConnection();
	}

	@After
	public void tearDown() {
		DataAccess.cleanUp(connection);

		connection = null;
	}

	@Test
	public void testUpgrade() throws Exception {

		// For code coverage

		doUpgrade();

		// Check that the table and column combination is correct

		DBInspector dbInspector = new DBInspector(connection);

		_assertTableAndColumn(dbInspector, "ClassName_", "value");
		_assertTableAndColumn(dbInspector, "Counter", "name");
		_assertTableAndColumn(dbInspector, "Lock_", "className");
		_assertTableAndColumn(dbInspector, "ResourceAction", "name");
		_assertTableAndColumn(dbInspector, "ResourcePermission", "name");
		_assertTableAndColumn(dbInspector, "ListType", "type_");
		_assertTableAndColumn(
			dbInspector, "UserNotificationEvent", "payload",
			"userNotificationEventId");
	}

	@Test
	public void testUpgradeTable() throws Exception {
		try {

			// Test WildcardMode.LEADING

			_insert(1, _PREFIX_CLASS_NAME_OLD, "");
			_insert(2, _POSTFIX_CLASS_NAME_OLD, "");
			_insert(3, _PREFIX_POSTFIX_CLASS_NAME_OLD, "");

			upgradeTable(
				"UpgradeKernelPackageTest", "data", _TEST_CLASS_NAMES,
				WildcardMode.LEADING);

			_assertData(1, "data", _PREFIX_CLASS_NAME_NEW);
			_assertData(2, "data", _POSTFIX_CLASS_NAME_OLD);
			_assertData(3, "data", _PREFIX_POSTFIX_CLASS_NAME_OLD);

			// Test WildcardMode.TRAILING

			_insert(4, _PREFIX_CLASS_NAME_OLD, "");
			_insert(5, _POSTFIX_CLASS_NAME_OLD, "");
			_insert(6, _PREFIX_POSTFIX_CLASS_NAME_OLD, "");

			upgradeTable(
				"UpgradeKernelPackageTest", "data", _TEST_CLASS_NAMES,
				WildcardMode.TRAILING);

			_assertData(4, "data", _PREFIX_CLASS_NAME_OLD);
			_assertData(5, "data", _POSTFIX_CLASS_NAME_NEW);
			_assertData(6, "data", _PREFIX_POSTFIX_CLASS_NAME_OLD);

			// Test WildcardMode.SURROUND

			_insert(7, _PREFIX_CLASS_NAME_OLD, "");
			_insert(8, _POSTFIX_CLASS_NAME_OLD, "");
			_insert(9, _PREFIX_POSTFIX_CLASS_NAME_OLD, "");

			upgradeTable(
				"UpgradeKernelPackageTest", "data", _TEST_CLASS_NAMES,
				WildcardMode.SURROUND);

			_assertData(7, "data", _PREFIX_CLASS_NAME_NEW);
			_assertData(8, "data", _POSTFIX_CLASS_NAME_NEW);
			_assertData(9, "data", _PREFIX_POSTFIX_CLASS_NAME_NEW);

			// Test preventDuplicates

			runSQL("delete from UpgradeKernelPackageTest");

			_insert(10, _PREFIX_POSTFIX_CLASS_NAME_OLD, "");
			_insert(11, _PREFIX_POSTFIX_CLASS_NAME_NEW, "");
			_insert(12, _PREFIX_POSTFIX_CLASS_NAME_NEW, "uniqueTextData");

			try {
				upgradeTable(
					"UpgradeKernelPackageTest", "data", _TEST_CLASS_NAMES,
					WildcardMode.SURROUND, true);
			}
			catch (Exception exception) {
				Assert.assertEquals(
					"UpgradeKernelPackageTest has no unique index including " +
						"data column",
					exception.getMessage());
			}

			_db.runSQL(
				"create unique index IX_TEMP on UpgradeKernelPackageTest " +
					"(data, textData)");

			try {
				upgradeTable(
					"UpgradeKernelPackageTest", "data", _TEST_CLASS_NAMES,
					WildcardMode.SURROUND, true);

				_assertData(10, "data", _PREFIX_POSTFIX_CLASS_NAME_NEW);
				_assertData(11, "data", null);
				_assertData(12, "data", _PREFIX_POSTFIX_CLASS_NAME_NEW);
				_assertData(12, "textData", "uniqueTextData");
			}
			finally {
				_db.runSQL("drop index IX_TEMP on UpgradeKernelPackageTest");
			}
		}
		finally {
			runSQL("delete from UpgradeKernelPackageTest");
		}
	}

	@Override
	protected String[][] getClassNames() {
		return new String[0][0];
	}

	@Override
	protected String[][] getResourceNames() {
		return new String[0][0];
	}

	private void _assertData(long id, String columnName, String expectedValue)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				SQLTransformer.transform(
					StringBundler.concat(
						"select ", columnName,
						" from UpgradeKernelPackageTest where id =", id)));
			ResultSet resultSet = preparedStatement.executeQuery()) {

			if (expectedValue == null) {
				Assert.assertFalse(
					"Entry with id " + id + "should not exsit",
					resultSet.next());
			}
			else {
				Assert.assertTrue(
					"Entry with id " + id + " should exist", resultSet.next());

				Assert.assertEquals(
					expectedValue, resultSet.getString(columnName));
			}
		}
	}

	private void _assertTableAndColumn(
			DBInspector dbInspector, String tableName, String... columnNames)
		throws Exception {

		Assert.assertTrue(
			StringBundler.concat("Table \"", tableName, "\" does not exist"),
			dbInspector.hasTable(tableName));

		for (String columnName : columnNames) {
			Assert.assertTrue(
				StringBundler.concat(
					"Table \"", tableName, "\" does not have column \"",
					columnName, "\""),
				dbInspector.hasColumn(tableName, columnName));
		}
	}

	private void _insert(long id, String data, String textData)
		throws Exception {

		runSQL(
			StringBundler.concat(
				"insert into UpgradeKernelPackageTest values(", id, ", '", data,
				"', '", textData, "')"));
	}

	private static final String _CLASS_NAME_NEW = "UPDATED_CLASS_NAME";

	private static final String _CLASS_NAME_OLD = "ORIGINAL_CLASS_NAME";

	private static final String _POSTFIX_CLASS_NAME_NEW =
		_CLASS_NAME_NEW + "_POSTFIX";

	private static final String _POSTFIX_CLASS_NAME_OLD =
		_CLASS_NAME_OLD + "_POSTFIX";

	private static final String _PREFIX_CLASS_NAME_NEW =
		"PREFIX_" + _CLASS_NAME_NEW;

	private static final String _PREFIX_CLASS_NAME_OLD =
		"PREFIX_" + _CLASS_NAME_OLD;

	private static final String _PREFIX_POSTFIX_CLASS_NAME_NEW =
		"PREFIX_" + _CLASS_NAME_NEW + "_POSTFIX";

	private static final String _PREFIX_POSTFIX_CLASS_NAME_OLD =
		"PREFIX_" + _CLASS_NAME_OLD + "_POSTFIX";

	private static final String[][] _TEST_CLASS_NAMES = {
		{_CLASS_NAME_OLD, _CLASS_NAME_NEW}
	};

	private static DB _db;

}