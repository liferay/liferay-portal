/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.internal.upgrade.v4_7_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
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

import org.junit.After;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Feliphe Marinho
 */
@RunWith(Arquillian.class)
public class KaleoDefinitionUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@After
	public void tearDown() throws Exception {
		try (Connection connection = DataAccess.getConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(
				"delete from KaleoDefinition where kaleoDefinitionId = ? or " +
					"kaleoDefinitionId = ?")) {

			preparedStatement.setLong(1, _kaleoDefinitionId1);
			preparedStatement.setLong(2, _kaleoDefinitionId2);

			preparedStatement.executeUpdate();
		}
	}

	@Test
	public void testUpgrade() throws Exception {
		DB db = DBManagerUtil.getDB();

		try (Connection connection = DataAccess.getConnection()) {
			DBInspector dbInspector = new DBInspector(connection);

			if (dbInspector.hasColumn("KaleoDefinition", "system_")) {
				db.alterTableDropColumn(
					connection, "KaleoDefinition", "system_");
			}
		}

		_kaleoDefinitionId1 = RandomTestUtil.randomLong();

		_addKaleoDefinition(_kaleoDefinitionId1, "Change Tone");

		_kaleoDefinitionId2 = RandomTestUtil.randomLong();

		_addKaleoDefinition(_kaleoDefinitionId2, RandomTestUtil.randomString());

		_runUpgrade();

		try (Connection connection = DataAccess.getConnection()) {
			DBInspector dbInspector = new DBInspector(connection);

			Assert.assertTrue(
				dbInspector.hasColumn("KaleoDefinition", "system_"));
		}

		Assert.assertTrue(_isSystem(_kaleoDefinitionId1));
		Assert.assertFalse(_isSystem(_kaleoDefinitionId2));
	}

	private void _addKaleoDefinition(long kaleoDefinitionId, String name)
		throws Exception {

		try (Connection connection = DataAccess.getConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(
				"insert into KaleoDefinition (kaleoDefinitionId, companyId, " +
					"name) values (?, ?, ?)")) {

			preparedStatement.setLong(1, kaleoDefinitionId);
			preparedStatement.setLong(2, RandomTestUtil.randomLong());
			preparedStatement.setString(3, name);

			preparedStatement.executeUpdate();
		}
	}

	private boolean _isSystem(long kaleoDefinitionId) throws Exception {
		try (Connection connection = DataAccess.getConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(
				"select system_ from KaleoDefinition where kaleoDefinitionId " +
					"= ?")) {

			preparedStatement.setLong(1, kaleoDefinitionId);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				Assert.assertTrue(resultSet.next());

				return resultSet.getBoolean("system_");
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
		"com.liferay.portal.workflow.kaleo.internal.upgrade.v4_7_0." +
			"KaleoDefinitionUpgradeProcess";

	private long _kaleoDefinitionId1;
	private long _kaleoDefinitionId2;

	@Inject(
		filter = "component.name=com.liferay.portal.workflow.kaleo.internal.upgrade.registry.KaleoServiceUpgradeStepRegistrator"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}