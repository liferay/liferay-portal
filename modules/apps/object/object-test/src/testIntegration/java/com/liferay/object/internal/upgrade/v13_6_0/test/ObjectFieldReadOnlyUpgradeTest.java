/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.upgrade.v13_6_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.version.Version;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Adolfo Pérez
 */
@RunWith(Arquillian.class)
public class ObjectFieldReadOnlyUpgradeTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testUpgrade() throws Exception {
		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition();

		ObjectField createDateObjectField =
			_objectFieldLocalService.getObjectField(
				objectDefinition.getObjectDefinitionId(), "createDate");
		ObjectField displayDateObjectField =
			_objectFieldLocalService.getObjectField(
				objectDefinition.getObjectDefinitionId(), "displayDate");

		DB db = DBManagerUtil.getDB();

		db.runSQL(
			"update ObjectField set readOnly = '1' where objectFieldId = " +
				displayDateObjectField.getObjectFieldId());

		Assert.assertEquals(
			"1",
			_getColumnValue(
				"readOnly", displayDateObjectField.getObjectFieldId()));

		for (UpgradeProcess upgradeProcess :
				UpgradeTestUtil.getUpgradeSteps(
					_upgradeStepRegistrator, new Version(13, 6, 0))) {

			upgradeProcess.upgrade();
		}

		Assert.assertEquals(
			ObjectFieldConstants.READ_ONLY_FALSE,
			_getColumnValue(
				"readOnly", displayDateObjectField.getObjectFieldId()));
		Assert.assertEquals(
			ObjectFieldConstants.READ_ONLY_TRUE,
			_getColumnValue(
				"readOnly", createDateObjectField.getObjectFieldId()));
	}

	private String _getColumnValue(String columnName, long objectFieldId)
		throws Exception {

		try (Connection connection = DataAccess.getConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"select ", columnName,
					" from ObjectField where objectFieldId = ?"))) {

			preparedStatement.setLong(1, objectFieldId);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				Assert.assertTrue(resultSet.next());

				return resultSet.getString(columnName);
			}
		}
	}

	@Inject
	private ObjectFieldLocalService _objectFieldLocalService;

	@Inject(
		filter = "component.name=com.liferay.object.internal.upgrade.registry.ObjectServiceUpgradeStepRegistrator"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}