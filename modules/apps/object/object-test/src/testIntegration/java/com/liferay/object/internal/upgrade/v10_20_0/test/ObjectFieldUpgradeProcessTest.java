/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.upgrade.v10_20_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.field.setting.util.ObjectFieldSettingUtil;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.Collections;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jhosseph Gonzalez
 */
@RunWith(Arquillian.class)
public class ObjectFieldUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testUpgrade() throws Exception {
		ObjectDefinition modifiableSystemObjectDefinition =
			ObjectDefinitionTestUtil.publishSystemObjectDefinition();

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				ObjectDefinitionTestUtil.getRandomName(),
				Collections.singletonList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING,
						RandomTestUtil.randomString(), StringUtil.randomId())),
				ObjectDefinitionConstants.SCOPE_COMPANY,
				TestPropsValues.getUserId());

		DB db = DBManagerUtil.getDB();

		db.runSQL(
			StringBundler.concat(
				"delete from ObjectField where objectDefinitionId in (",
				modifiableSystemObjectDefinition.getObjectDefinitionId(), ", ",
				objectDefinition.getObjectDefinitionId(),
				") and name in ('displayDate', 'expirationDate', ",
				"'reviewDate')"));

		EntityCacheUtil.clearCache();

		Assert.assertNull(
			_objectFieldLocalService.fetchObjectField(
				modifiableSystemObjectDefinition.getObjectDefinitionId(),
				"displayDate"));
		Assert.assertNull(
			_objectFieldLocalService.fetchObjectField(
				modifiableSystemObjectDefinition.getObjectDefinitionId(),
				"expirationDate"));
		Assert.assertNull(
			_objectFieldLocalService.fetchObjectField(
				modifiableSystemObjectDefinition.getObjectDefinitionId(),
				"reviewDate"));
		Assert.assertNull(
			_objectFieldLocalService.fetchObjectField(
				objectDefinition.getObjectDefinitionId(), "displayDate"));
		Assert.assertNull(
			_objectFieldLocalService.fetchObjectField(
				objectDefinition.getObjectDefinitionId(), "expirationDate"));
		Assert.assertNull(
			_objectFieldLocalService.fetchObjectField(
				objectDefinition.getObjectDefinitionId(), "reviewDate"));

		ObjectDefinition userObjectDefinition =
			_objectDefinitionLocalService.getObjectDefinition(
				TestPropsValues.getCompanyId(), "User");

		Assert.assertNull(
			_objectFieldLocalService.fetchObjectField(
				userObjectDefinition.getObjectDefinitionId(), "displayDate"));
		Assert.assertNull(
			_objectFieldLocalService.fetchObjectField(
				userObjectDefinition.getObjectDefinitionId(),
				"expirationDate"));
		Assert.assertNull(
			_objectFieldLocalService.fetchObjectField(
				userObjectDefinition.getObjectDefinitionId(), "reviewDate"));

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME, LoggerTestUtil.OFF)) {

			UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
				_upgradeStepRegistrator, _CLASS_NAME);

			upgradeProcess.upgrade();

			EntityCacheUtil.clearCache();
		}

		_assertObjectField(
			modifiableSystemObjectDefinition.getObjectDefinitionId(),
			"displayDate");
		_assertObjectField(
			modifiableSystemObjectDefinition.getObjectDefinitionId(),
			"expirationDate");
		_assertObjectField(
			modifiableSystemObjectDefinition.getObjectDefinitionId(),
			"reviewDate");
		_assertObjectField(
			objectDefinition.getObjectDefinitionId(), "displayDate");
		_assertObjectField(
			objectDefinition.getObjectDefinitionId(), "expirationDate");
		_assertObjectField(
			objectDefinition.getObjectDefinitionId(), "reviewDate");
		Assert.assertNull(
			_objectFieldLocalService.fetchObjectField(
				userObjectDefinition.getObjectDefinitionId(), "displayDate"));
		Assert.assertNull(
			_objectFieldLocalService.fetchObjectField(
				userObjectDefinition.getObjectDefinitionId(),
				"expirationDate"));
		Assert.assertNull(
			_objectFieldLocalService.fetchObjectField(
				userObjectDefinition.getObjectDefinitionId(), "reviewDate"));
	}

	private void _assertObjectField(long objectDefinitionId, String name)
		throws Exception {

		ObjectField objectField = _objectFieldLocalService.getObjectField(
			objectDefinitionId, name);

		Assert.assertEquals(
			ObjectFieldConstants.BUSINESS_TYPE_DATE_TIME,
			objectField.getBusinessType());
		Assert.assertEquals(
			ObjectFieldConstants.DB_TYPE_DATE_TIME, objectField.getDBType());
		Assert.assertFalse(objectField.isIndexed());
		Assert.assertTrue(objectField.isSystem());
		Assert.assertEquals(
			ObjectFieldSettingConstants.VALUE_CONVERT_TO_UTC,
			ObjectFieldSettingUtil.getValue(
				ObjectFieldSettingConstants.NAME_TIME_STORAGE, objectField));
		Assert.assertEquals(
			ObjectFieldConstants.READ_ONLY_FALSE,
			_getColumnValue("readOnly", objectField.getObjectFieldId()));
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

	private static final String _CLASS_NAME =
		"com.liferay.object.internal.upgrade.v10_20_0." +
			"ObjectFieldUpgradeProcess";

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectFieldLocalService _objectFieldLocalService;

	@Inject(
		filter = "component.name=com.liferay.object.internal.upgrade.registry.ObjectServiceUpgradeStepRegistrator"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}