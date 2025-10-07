/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.upgrade.v10_0_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Paulo Albuquerque
 */
@RunWith(Arquillian.class)
public class ObjectDefinitionUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testUpgrade() throws Exception {
		ObjectDefinition objectDefinition1 =
			_addModifiableSystemObjectDefinition();

		_assertObjectDefinitionPKObjectFieldPrefix(
			_objectDefinitionLocalService.publishSystemObjectDefinition(
				TestPropsValues.getUserId(),
				objectDefinition1.getObjectDefinitionId()),
			"c_");

		ObjectDefinition objectDefinition2 =
			_addModifiableSystemObjectDefinition();

		_assertObjectDefinitionPKObjectFieldPrefix(objectDefinition2, "c_");

		String pkObjectFieldDBColumnName = StringUtil.randomId();
		String pkObjectFieldName = StringUtil.randomId();

		ObjectDefinition objectDefinition3 =
			ObjectDefinitionTestUtil.addModifiableSystemObjectDefinition(
				TestPropsValues.getUserId(), null, true,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				"FDSEntry", pkObjectFieldDBColumnName, pkObjectFieldName,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectDefinitionConstants.SCOPE_COMPANY, null, 1,
				List.of(
					new TextObjectFieldBuilder(
					).userId(
						TestPropsValues.getUserId()
					).labelMap(
						RandomTestUtil.randomLocaleStringMap()
					).name(
						"a" + RandomTestUtil.randomString()
					).build()));

		objectDefinition3 =
			_objectDefinitionLocalService.publishSystemObjectDefinition(
				TestPropsValues.getUserId(),
				objectDefinition3.getObjectDefinitionId());

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME, LoggerTestUtil.OFF)) {

			UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
				_upgradeStepRegistrator, _CLASS_NAME);

			upgradeProcess.upgrade();

			_multiVMPool.clear();
		}

		_assertObjectDefinitionPKObjectFieldPrefix(
			_objectDefinitionLocalService.getObjectDefinition(
				objectDefinition1.getObjectDefinitionId()),
			"l_");
		_assertObjectDefinitionPKObjectFieldPrefix(
			_objectDefinitionLocalService.getObjectDefinition(
				objectDefinition2.getObjectDefinitionId()),
			"l_");

		Assert.assertEquals(
			pkObjectFieldDBColumnName,
			objectDefinition3.getPKObjectFieldDBColumnName());
		Assert.assertEquals(
			pkObjectFieldName, objectDefinition3.getPKObjectFieldName());
	}

	private ObjectDefinition _addModifiableSystemObjectDefinition()
		throws Exception {

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.addModifiableSystemObjectDefinition(
				TestPropsValues.getUserId(), null, false,
				RandomTestUtil.randomLocaleStringMap(),
				"Test" + RandomTestUtil.randomString(), null, null,
				RandomTestUtil.randomLocaleStringMap(),
				ObjectDefinitionConstants.SCOPE_SITE, null, 1,
				Collections.singletonList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING,
						RandomTestUtil.randomString(), StringUtil.randomId())));

		objectDefinition.setPKObjectFieldDBColumnName(
			StringUtil.replaceFirst(
				objectDefinition.getPKObjectFieldDBColumnName(), "l_", "c_"));
		objectDefinition.setPKObjectFieldName(
			StringUtil.replaceFirst(
				objectDefinition.getPKObjectFieldName(), "l_", "c_"));

		return _objectDefinitionLocalService.updateObjectDefinition(
			objectDefinition);
	}

	private void _assertObjectDefinitionPKObjectFieldPrefix(
		ObjectDefinition objectDefinition, String prefix) {

		Assert.assertTrue(
			StringUtil.startsWith(
				objectDefinition.getPKObjectFieldDBColumnName(), prefix));
		Assert.assertTrue(
			StringUtil.startsWith(
				objectDefinition.getPKObjectFieldName(), prefix));
	}

	private static final String _CLASS_NAME =
		"com.liferay.object.internal.upgrade.v10_0_0." +
			"ObjectDefinitionUpgradeProcess";

	@Inject(
		filter = "component.name=com.liferay.object.internal.upgrade.registry.ObjectServiceUpgradeStepRegistrator"
	)
	private static UpgradeStepRegistrator _upgradeStepRegistrator;

	@Inject
	private MultiVMPool _multiVMPool;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

}