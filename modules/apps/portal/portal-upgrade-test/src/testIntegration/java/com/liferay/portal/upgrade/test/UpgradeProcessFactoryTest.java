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
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.sql.Connection;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

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
			company -> {
				_db.runSQL("DROP_TABLE_IF_EXISTS(" + _TABLE_NAME_1 + ")");
				_db.runSQL("DROP_TABLE_IF_EXISTS(" + _TABLE_NAME_2 + ")");
			});
	}

	@Test
	public void testAddColumn() throws Exception {
		UpgradeProcess upgradeProcess = UpgradeProcessFactory.addColumns(
			_TABLE_NAME_1, "newColumn LONG default 0 NOT NULL");

		_assertUpgrade(
			UpgradeProcessFactoryTest.class,
			StringBundler.concat(
				"Modifying table ", _TABLE_NAME_1,
				" to add the columns [newColumn LONG default 0 NOT NULL]"),
			upgradeProcess);

		Assert.assertTrue(
			_dbInspector.hasColumnType(
				_TABLE_NAME_1, "newColumn", "LONG default 0 NOT NULL"));
	}

	@Test
	public void testAlterColumnName() throws Exception {
		UpgradeProcess upgradeProcess = UpgradeProcessFactory.alterColumnName(
			_TABLE_NAME_1, "typeLong", "newTypeLong LONG null");

		_assertUpgrade(
			UpgradeProcessFactoryTest.class,
			StringBundler.concat(
				"Modifying table ", _TABLE_NAME_1,
				" to alter the name of the column typeLong to newTypeLong ",
				"LONG null"),
			upgradeProcess);

		Assert.assertTrue(
			_dbInspector.hasColumnType(
				_TABLE_NAME_1, "newTypeLong", "LONG null"));
		Assert.assertFalse(_dbInspector.hasColumn(_TABLE_NAME_1, "typeLong"));
	}

	@Test
	public void testAlterColumnType() throws Exception {
		UpgradeProcess upgradeProcess = UpgradeProcessFactory.alterColumnType(
			_TABLE_NAME_1, "typeText", "VARCHAR(250) null");

		_assertUpgrade(
			UpgradeProcessFactoryTest.class,
			StringBundler.concat(
				"Modifying table ", _TABLE_NAME_1,
				" to alter the type of the column typeText to VARCHAR(250) ",
				"null"),
			upgradeProcess);

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

		_assertUpgrade(
			UpgradeProcessFactoryTest.class,
			StringBundler.concat(
				"Modifying table ", _TABLE_NAME_1,
				" to drop the columns [typeDate]"),
			upgradeProcess);

		Assert.assertFalse(_dbInspector.hasColumn(_TABLE_NAME_1, "typeDate"));
	}

	@Test
	public void testDropTables() throws Exception {
		_db.runSQL(
			"create table " + _TABLE_NAME_2 +
				" (id LONG not null primary key)");

		UpgradeProcess upgradeProcess = UpgradeProcessFactory.dropTables(
			_TABLE_NAME_1, _TABLE_NAME_2);

		_assertUpgrade(
			UpgradeProcessFactoryTest.class,
			StringBundler.concat(
				"Modifying schema to drop tables [", _TABLE_NAME_1, ", ",
				_TABLE_NAME_2, "]"),
			upgradeProcess);

		Assert.assertFalse(_dbInspector.hasTable(_TABLE_NAME_1));
		Assert.assertFalse(_dbInspector.hasTable(_TABLE_NAME_2));
	}

	@Test
	public void testRunSQL() throws Exception {
		UpgradeProcess upgradeProcess = UpgradeProcessFactory.runSQL(
			"create table " + _TABLE_NAME_2 +
				" (id LONG not null primary key)");

		_assertUpgrade(
			UpgradeProcessFactoryTest.class,
			"Modifying schema to run SQL updates", upgradeProcess);

		Assert.assertTrue(_dbInspector.hasTable(_TABLE_NAME_2));
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

		Assert.assertEquals(
			Arrays.toString(upgradeSteps), 3, upgradeSteps.length);

		upgradeProcess.upgrade();

		_assertUpgrade(
			upgradeProcess.getClass(),
			StringBundler.concat(
				"Modifying table ", _TABLE_NAME_1,
				" to add the columns [newColumn LONG null]"),
			(UpgradeProcess)upgradeSteps[1]);
		_assertUpgrade(
			upgradeProcess.getClass(),
			StringBundler.concat(
				"Modifying table ", _TABLE_NAME_1,
				" to drop the columns [typeDate]"),
			(UpgradeProcess)upgradeSteps[2]);

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

		Assert.assertEquals(
			Arrays.toString(upgradeSteps), 4, upgradeSteps.length);

		_assertUpgrade(
			upgradeProcess.getClass(),
			StringBundler.concat(
				"Modifying table ", _TABLE_NAME_1,
				" to add the columns [newColumn LONG null]"),
			(UpgradeProcess)upgradeSteps[0]);
		_assertUpgrade(
			upgradeProcess.getClass(),
			StringBundler.concat(
				"Modifying table ", _TABLE_NAME_1,
				" to drop the columns [typeDate]"),
			(UpgradeProcess)upgradeSteps[1]);

		upgradeProcess.upgrade();

		_assertUpgrade(
			upgradeProcess.getClass(),
			StringBundler.concat(
				"Modifying table ", _TABLE_NAME_1,
				" to alter the name of the column newColumn to ",
				"newColumnModified LONG null"),
			(UpgradeProcess)upgradeSteps[3]);

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

		Assert.assertEquals(
			Arrays.toString(upgradeSteps), 3, upgradeSteps.length);

		_assertUpgrade(
			upgradeProcess.getClass(),
			StringBundler.concat(
				"Modifying table ", _TABLE_NAME_1,
				" to add the columns [newColumn LONG null]"),
			(UpgradeProcess)upgradeSteps[0]);
		_assertUpgrade(
			upgradeProcess.getClass(),
			StringBundler.concat(
				"Modifying table ", _TABLE_NAME_1,
				" to drop the columns [typeDate]"),
			(UpgradeProcess)upgradeSteps[1]);

		upgradeProcess.upgrade();

		Assert.assertTrue(
			_dbInspector.hasColumnType(
				_TABLE_NAME_1, "newColumn", "LONG null"));
		Assert.assertFalse(_dbInspector.hasColumn(_TABLE_NAME_1, "typeDate"));
	}

	private void _assertUpgrade(
			Class<?> callerClass, String expectedMessage,
			UpgradeProcess upgradeProcess)
		throws Exception {

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				UpgradeProcess.class.getName(), LoggerTestUtil.INFO)) {

			upgradeProcess.upgrade();

			List<String> messages = logCapture.getMessages();

			String message = messages.get(0);

			Assert.assertTrue(
				message,
				message.matches(
					StringBundler.concat(
						"Upgrading ", Pattern.quote(callerClass.getName()),
						":\\d+ - ", Pattern.quote(expectedMessage), ".*")));
		}
	}

	private static final String _TABLE_NAME_1 = "UpgradeProcessFactoryTest1";

	private static final String _TABLE_NAME_2 = "UpgradeProcessFactoryTest2";

	private static Connection _connection;
	private static DB _db;
	private static DBInspector _dbInspector;

	@Inject
	private CompanyLocalService _companyLocalService;

}