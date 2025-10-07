/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.internal.upgrade.v5_22_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeStep;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Danny Situ
 */
@RunWith(Arquillian.class)
public class CPSpecificationOptionUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		_db = DBManagerUtil.getDB();

		try (Connection connection = DataAccess.getConnection()) {
			DBInspector dbInspector = new DBInspector(connection);

			if (!dbInspector.hasColumn(
					"CPSpecificationOption", "listTypeDefinitionId")) {

				_db.runSQLTemplate(
					"alter table CPSpecificationOption add " +
						"listTypeDefinitionId LONG;",
					true);
			}
		}
	}

	@Test
	public void testUpgrade() throws Exception {
		long cpSpecificationOptionId = RandomTestUtil.randomLong();

		try (Connection connection = DataAccess.getConnection();
			PreparedStatement preparedStatement =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					StringBundler.concat(
						"insert into CPSpecificationOption (",
						"CPSpecificationOptionId, listTypeDefinitionId) ",
						"values (?, ?)"))) {

			preparedStatement.setLong(1, cpSpecificationOptionId);

			long listTypeDefinitionId = RandomTestUtil.randomLong();

			preparedStatement.setLong(2, listTypeDefinitionId);

			preparedStatement.executeUpdate();
		}

		_runUpgrade();

		try (Connection connection = DataAccess.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				"select count(*) from CPSOListTypeDefinitionRel where " +
					"cpSpecificationOptionId = ?")) {

			preparedStatement.setLong(1, cpSpecificationOptionId);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					int count = resultSet.getInt(1);

					Assert.assertEquals(1, count);
				}
			}
		}
	}

	private void _runUpgrade() throws Exception {
		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator, _CLASS_NAME);

		UpgradeStep[] upgradeSteps = upgradeProcess.getUpgradeSteps();

		for (UpgradeStep upgradeStep : upgradeSteps) {
			UpgradeProcess innerUpgradeProcess = (UpgradeProcess)upgradeStep;

			innerUpgradeProcess.upgrade();
		}

		EntityCacheUtil.clearCache();
	}

	private static final String _CLASS_NAME =
		"com.liferay.commerce.product.internal.upgrade.v5_22_0." +
			"CPSpecificationOptionUpgradeProcess";

	private static DB _db;

	@Inject(
		filter = "(&(component.name=com.liferay.commerce.product.internal.upgrade.registry.CommerceProductServiceUpgradeStepRegistrator))"
	)
	private static UpgradeStepRegistrator _upgradeStepRegistrator;

}