/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.upgrade.v10_20_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
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

		Assert.assertNotNull(
			_objectFieldLocalService.getObjectField(
				modifiableSystemObjectDefinition.getObjectDefinitionId(),
				"displayDate"));
		Assert.assertNotNull(
			_objectFieldLocalService.getObjectField(
				modifiableSystemObjectDefinition.getObjectDefinitionId(),
				"expirationDate"));
		Assert.assertNotNull(
			_objectFieldLocalService.getObjectField(
				modifiableSystemObjectDefinition.getObjectDefinitionId(),
				"reviewDate"));
		Assert.assertNotNull(
			_objectFieldLocalService.getObjectField(
				objectDefinition.getObjectDefinitionId(), "displayDate"));
		Assert.assertNotNull(
			_objectFieldLocalService.getObjectField(
				objectDefinition.getObjectDefinitionId(), "expirationDate"));
		Assert.assertNotNull(
			_objectFieldLocalService.getObjectField(
				objectDefinition.getObjectDefinitionId(), "reviewDate"));
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

	private static final String _CLASS_NAME =
		"com.liferay.object.internal.upgrade.v10_20_0." +
			"ObjectFieldUpgradeProcess";

	@Inject(
		filter = "component.name=com.liferay.object.internal.upgrade.registry.ObjectServiceUpgradeStepRegistrator"
	)
	private static UpgradeStepRegistrator _upgradeStepRegistrator;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectFieldLocalService _objectFieldLocalService;

}