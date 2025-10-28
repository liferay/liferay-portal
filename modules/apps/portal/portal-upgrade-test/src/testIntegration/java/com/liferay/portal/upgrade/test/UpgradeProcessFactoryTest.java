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
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.kernel.upgrade.UpgradeStep;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.sql.Connection;

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
 * @author Luis Ortiz
 */
@RunWith(Arquillian.class)
public class UpgradeProcessFactoryTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

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
		_companyLocalService.forEachCompany(
			company -> _db.runSQL(
				StringBundler.concat(
					"create table ", _TABLE_NAME_1,
					" (id LONG not null primary key, notNilColumn VARCHAR(75) ",
					"not null, nilColumn VARCHAR(75) null, typeBlob BLOB, ",
					"typeBoolean BOOLEAN,typeDate DATE null, typeDouble ",
					"DOUBLE, typeInteger INTEGER, typeLong LONG null, ",
					"typeSBlob SBLOB, typeString STRING null, typeText TEXT ",
					"null, typeVarchar VARCHAR(75) null)")));
	}

	@After
	public void tearDown() throws Exception {
		_companyLocalService.forEachCompany(
			company -> _db.runSQL(
				"DROP_TABLE_IF_EXISTS(" + _TABLE_NAME_1 + ")"));
	}

	@Test
	public void testAddColumn() throws Exception {
		UpgradeProcess upgradeProcess = UpgradeProcessFactory.addColumns(
			_TABLE_NAME_1, "newColumn LONG default 0 NOT NULL");

		upgradeProcess.upgrade();

		Assert.assertTrue(
			_dbInspector.hasColumnType(
				_TABLE_NAME_1, "newColumn", "LONG default 0 NOT NULL"));
	}

	@Test
	public void testAlterColumnName() throws Exception {
		UpgradeProcess upgradeProcess = UpgradeProcessFactory.alterColumnName(
			_TABLE_NAME_1, "typeLong", "newTypeLong LONG null");

		upgradeProcess.upgrade();

		Assert.assertTrue(
			_dbInspector.hasColumnType(
				_TABLE_NAME_1, "newTypeLong", "LONG null"));
		Assert.assertFalse(_dbInspector.hasColumn(_TABLE_NAME_1, "typeLong"));
	}

	@Test
	public void testAlterColumnType() throws Exception {
		UpgradeProcess upgradeProcess = UpgradeProcessFactory.alterColumnType(
			_TABLE_NAME_1, "typeText", "VARCHAR(250) null");

		upgradeProcess.upgrade();

		Assert.assertTrue(
			_dbInspector.hasColumnType(
				_TABLE_NAME_1, "typeText", "VARCHAR(250) null"));
		Assert.assertFalse(
			_dbInspector.hasColumnType(_TABLE_NAME_1, "typeText", "TEXT null"));
	}

	@Test
	public void testDropColumn() throws Exception {
		UpgradeProcess upgradeProcess = UpgradeProcessFactory.dropColumns(
			_TABLE_NAME_1, "typeDate");

		upgradeProcess.upgrade();

		Assert.assertFalse(_dbInspector.hasColumn(_TABLE_NAME_1, "typeDate"));
	}

	@Test
	public void testDropTables() throws Exception {
		_db.runSQL(
			"create table " + _TABLE_NAME_2 +
				" (id LONG not null primary key)");

		UpgradeProcess upgradeProcess = UpgradeProcessFactory.dropTables(
			_TABLE_NAME_1, _TABLE_NAME_2);

		upgradeProcess.upgrade();

		Assert.assertFalse(_dbInspector.hasTable(_TABLE_NAME_1));
		Assert.assertFalse(_dbInspector.hasTable(_TABLE_NAME_2));
	}

	@Test
	public void testUtilOnPostUpgradeSteps() throws Exception {
		UpgradeProcess upgradeProcess = new UpgradeProcess() {

			@Override
			protected void doUpgrade() throws Exception {
			}

			@Override
			protected UpgradeStep[] getPostUpgradeSteps() {
				return new UpgradeStep[] {
					UpgradeProcessFactory.addColumns(
						_TABLE_NAME_1, "newColumn LONG null"),
					UpgradeProcessFactory.dropColumns(_TABLE_NAME_1, "typeDate")
				};
			}

		};

		UpgradeStep[] upgradeSteps = upgradeProcess.getUpgradeSteps();

		for (UpgradeStep upgradeStep : upgradeSteps) {
			UpgradeProcess innerUpgradeProcess = (UpgradeProcess)upgradeStep;

			innerUpgradeProcess.upgrade();
		}

		Assert.assertTrue(
			_dbInspector.hasColumnType(
				_TABLE_NAME_1, "newColumn", "LONG null"));
		Assert.assertFalse(_dbInspector.hasColumn(_TABLE_NAME_1, "typeDate"));
	}

	@Test
	public void testUtilOnPreAndPostUpgradeSteps() throws Exception {
		UpgradeProcess upgradeProcess = new UpgradeProcess() {

			@Override
			protected void doUpgrade() throws Exception {
			}

			@Override
			protected UpgradeStep[] getPostUpgradeSteps() {
				return new UpgradeStep[] {
					UpgradeProcessFactory.alterColumnName(
						_TABLE_NAME_1, "newColumn",
						"newColumnModified LONG null")
				};
			}

			@Override
			protected UpgradeStep[] getPreUpgradeSteps() {
				return new UpgradeStep[] {
					UpgradeProcessFactory.addColumns(
						_TABLE_NAME_1, "newColumn LONG null"),
					UpgradeProcessFactory.dropColumns(_TABLE_NAME_1, "typeDate")
				};
			}

		};

		UpgradeStep[] upgradeSteps = upgradeProcess.getUpgradeSteps();

		for (UpgradeStep upgradeStep : upgradeSteps) {
			UpgradeProcess innerUpgradeProcess = (UpgradeProcess)upgradeStep;

			innerUpgradeProcess.upgrade();
		}

		Assert.assertTrue(
			_dbInspector.hasColumnType(
				_TABLE_NAME_1, "newColumnModified", "LONG null"));
		Assert.assertFalse(_dbInspector.hasColumn(_TABLE_NAME_1, "typeDate"));
	}

	@Test
	public void testUtilOnPreUpgradeSteps() throws Exception {
		UpgradeProcess upgradeProcess = new UpgradeProcess() {

			@Override
			protected void doUpgrade() throws Exception {
			}

			@Override
			protected UpgradeStep[] getPreUpgradeSteps() {
				return new UpgradeStep[] {
					UpgradeProcessFactory.addColumns(
						_TABLE_NAME_1, "newColumn LONG null"),
					UpgradeProcessFactory.dropColumns(_TABLE_NAME_1, "typeDate")
				};
			}

		};

		UpgradeStep[] upgradeSteps = upgradeProcess.getUpgradeSteps();

		for (UpgradeStep upgradeStep : upgradeSteps) {
			UpgradeProcess innerUpgradeProcess = (UpgradeProcess)upgradeStep;

			innerUpgradeProcess.upgrade();
		}

		Assert.assertTrue(
			_dbInspector.hasColumnType(
				_TABLE_NAME_1, "newColumn", "LONG null"));
		Assert.assertFalse(_dbInspector.hasColumn(_TABLE_NAME_1, "typeDate"));
	}

	private static final String _TABLE_NAME_1 = "UpgradeProcessFactoryTest1";

	private static final String _TABLE_NAME_2 = "UpgradeProcessFactoryTest2";

	@Inject
	private static CompanyLocalService _companyLocalService;

	private static Connection _connection;
	private static DB _db;
	private static DBInspector _dbInspector;

}