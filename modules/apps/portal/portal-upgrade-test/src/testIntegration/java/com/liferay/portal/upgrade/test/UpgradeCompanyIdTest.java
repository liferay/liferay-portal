/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.v7_0_0.UpgradeCompanyId;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.After;
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
public class UpgradeCompanyIdTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws SQLException {
		_connection = DataAccess.getConnection();

		_dbInspector = new DBInspector(_connection);
	}

	@Before
	public void setUp() throws Exception {
		_companyLocalService.forEachCompany(
			company -> {
				_upgradeProcess.runSQL(
					StringBundler.concat(
						"create table ", _MAPPING_TABLE_NAME, " (",
						_COLUMN_NAME, " LONG not null primary key);"));

				_upgradeProcess.runSQL(
					StringBundler.concat(
						"insert into ", _MAPPING_TABLE_NAME, " (", _COLUMN_NAME,
						") values (", _COLUMN_VALUE, ");"));

				_upgradeProcess.runSQL(
					StringBundler.concat(
						"create table ", _TABLE_NAME, " (", _COLUMN_NAME,
						" LONG not null primary key, companyId LONG);"));

				_upgradeProcess.runSQL(
					StringBundler.concat(
						"insert into ", _TABLE_NAME, " (", _COLUMN_NAME,
						", companyId) values (", _COLUMN_VALUE,
						", (select max(companyId) from Company));"));
			});
	}

	@After
	public void tearDown() throws Exception {
		_companyLocalService.forEachCompany(
			company -> {
				_upgradeProcess.runSQL("drop table " + _MAPPING_TABLE_NAME);
				_upgradeProcess.runSQL("drop table " + _TABLE_NAME);
			});
	}

	@Test
	public void testUpgradeNullColumn() throws Exception {
		_upgradeProcess.upgrade();

		Assert.assertTrue(
			_dbInspector.hasColumnType(
				_MAPPING_TABLE_NAME, "companyId", "LONG NOT NULL"));
	}

	@Test
	public void testUpgradeNullValues() throws Exception {
		Company company = CompanyTestUtil.addCompany();

		_upgradeProcess.runSQL(
			StringBundler.concat(
				"insert into ", _MAPPING_TABLE_NAME, " (", _COLUMN_NAME,
				") values (", _COLUMN_VALUE - 1, ");"));

		try {
			_upgradeProcess.upgrade();

			Assert.fail();
		}
		catch (Exception exception) {
			Assert.assertNotNull(exception);
		}

		_companyLocalService.deleteCompany(company);
	}

	private static final String _COLUMN_NAME = "id";

	private static final int _COLUMN_VALUE = 99999;

	private static final String _MAPPING_TABLE_NAME =
		"UpgradeCompanyIdTestMapping";

	private static final String _TABLE_NAME = "UpgradeCompanyIdTest";

	@Inject
	private static CompanyLocalService _companyLocalService;

	private static Connection _connection;
	private static DBInspector _dbInspector;

	private final UpgradeCompanyIdCustom _upgradeProcess =
		new UpgradeCompanyIdCustom();

	private class UpgradeCompanyIdCustom extends UpgradeCompanyId {

		@Override
		protected TableUpdater[] getTableUpdaters() {
			return new TableUpdater[] {
				new CompanyIdNotNullTableUpdater(
					_MAPPING_TABLE_NAME, _TABLE_NAME, _COLUMN_NAME)
			};
		}

	}

}