/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v7_0_3.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.service.ReleaseLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.AssumeTestRule;
import com.liferay.portal.kernel.util.ReleaseInfo;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.v7_0_3.UpgradeOracle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.junit.After;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alberto Chaparro
 */
@RunWith(Arquillian.class)
public class UpgradeOracleTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new AssumeTestRule("assume"), new LiferayIntegrationTestRule());

	public static void assume() {
		_db = DBManagerUtil.getDB();

		Assume.assumeTrue(_db.getDBType() == DBType.ORACLE);
	}

	@Before
	public void setUp() throws Exception {
		_upgradeOracle = new UpgradeOracle();

		_db.runSQL(
			StringBundler.concat(
				"alter table ", _TABLE_NAME, " modify ", _FIELD_NAME,
				" varchar2(300 BYTE)"));
	}

	@After
	public void tearDown() throws Exception {
		_db.runSQL(
			StringBundler.concat(
				"alter table ", _TABLE_NAME, " modify ", _FIELD_NAME,
				" varchar2(75 CHAR)"));

		Release release = _releaseLocalService.fetchRelease("portal");

		release.setBuildNumber(ReleaseInfo.getBuildNumber());

		_releaseLocalService.updateRelease(release);
	}

	@Test
	public void testUpgradeAlterVarchar2ColumnsToChar() throws Exception {
		_upgradeOracle.upgrade();

		Assert.assertEquals("C", getCharUsed(_TABLE_NAME, _FIELD_NAME));
	}

	protected String getCharUsed(String tableName, String columnName)
		throws Exception {

		try (Connection connection = DataAccess.getConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(
				"select char_used from user_tab_columns where table_name = ? " +
					"and column_name = ?")) {

			preparedStatement.setString(1, tableName);
			preparedStatement.setString(2, columnName);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				resultSet.next();

				return resultSet.getString("char_used");
			}
		}
	}

	private static final String _FIELD_NAME = "WEBID";

	private static final String _TABLE_NAME = "COMPANY";

	private static DB _db;

	@Inject
	private ReleaseLocalService _releaseLocalService;

	private UpgradeOracle _upgradeOracle;

}