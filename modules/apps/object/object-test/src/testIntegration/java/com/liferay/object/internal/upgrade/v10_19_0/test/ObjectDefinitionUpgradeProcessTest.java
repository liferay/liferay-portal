/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.upgrade.v10_19_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.constants.ObjectDefinitionSettingConstants;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
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
 * @author Feliphe Marinho
 */
@RunWith(Arquillian.class)
public class ObjectDefinitionUpgradeProcessTest {

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
					"ObjectDefinition", "rootObjectDefinitionId")) {

				_db.runSQLTemplate(
					"alter table ObjectDefinition add rootObjectDefinitionId " +
						"LONG;",
					true);
			}
		}
	}

	@Test
	public void testUpgrade() throws Exception {
		int objectDefinitionId = RandomTestUtil.randomInt();
		int rootObjectDefinitionId = RandomTestUtil.randomInt();

		_db.runSQL(
			StringBundler.concat(
				"insert into ObjectDefinition (objectDefinitionId, ",
				"rootObjectDefinitionId) values (", objectDefinitionId, ", ",
				rootObjectDefinitionId, ")"));

		_runUpgrade();

		try (Connection connection = DataAccess.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				"select name, value from ObjectDefinitionSetting where " +
					"objectDefinitionId = ?")) {

			preparedStatement.setLong(1, objectDefinitionId);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					Assert.assertEquals(
						ObjectDefinitionSettingConstants.
							NAME_ROOT_OBJECT_DEFINITION_IDS,
						resultSet.getString("name"));
					Assert.assertEquals(
						String.valueOf(rootObjectDefinitionId),
						resultSet.getString("value"));
				}

				DBInspector dbInspector = new DBInspector(connection);

				Assert.assertFalse(
					dbInspector.hasColumn(
						"ObjectDefinition", "rootObjectDefinitionId"));
			}
		}
	}

	private void _runUpgrade() throws Exception {
		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator, _CLASS_NAME);

		for (UpgradeStep upgradeStep : upgradeProcess.getUpgradeSteps()) {
			UpgradeProcess innerUpgradeProcess = (UpgradeProcess)upgradeStep;

			innerUpgradeProcess.upgrade();
		}

		EntityCacheUtil.clearCache();
	}

	private static final String _CLASS_NAME =
		"com.liferay.object.internal.upgrade.v10_19_0." +
			"ObjectDefinitionUpgradeProcess";

	private static DB _db;

	@Inject(
		filter = "component.name=com.liferay.object.internal.upgrade.registry.ObjectServiceUpgradeStepRegistrator"
	)
	private static UpgradeStepRegistrator _upgradeStepRegistrator;

}