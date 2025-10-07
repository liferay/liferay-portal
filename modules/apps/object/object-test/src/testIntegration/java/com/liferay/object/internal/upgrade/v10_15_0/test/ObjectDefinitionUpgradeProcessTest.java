/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.upgrade.v10_15_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.friendly.url.configuration.manager.FriendlyURLSeparatorConfigurationManager;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Carolina Barbosa
 */
@RunWith(Arquillian.class)
public class ObjectDefinitionUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testUpgrade() throws Exception {
		ObjectDefinition objectDefinition1 = _addCustomObjectDefinition(null);
		ObjectDefinition objectDefinition2 = _addCustomObjectDefinition("able");

		JSONObject originalFriendlyURLSeparatorsJSONObject =
			_friendlyURLSeparatorConfigurationManager.
				getFriendlyURLSeparatorsJSONObject(
					TestPropsValues.getCompanyId());

		try {
			_friendlyURLSeparatorConfigurationManager.
				updateFriendlyURLSeparatorCompanyConfiguration(
					TestPropsValues.getCompanyId(),
					JSONUtil.put(
						ObjectEntry.class.getName(), "/test/"
					).toString());

			_upgrade();
		}
		finally {
			_friendlyURLSeparatorConfigurationManager.
				updateFriendlyURLSeparatorCompanyConfiguration(
					TestPropsValues.getCompanyId(),
					originalFriendlyURLSeparatorsJSONObject.toString());
		}

		_assertObjectDefinitionFriendlyURLSeparator(
			"test", objectDefinition1.getObjectDefinitionId());
		_assertObjectDefinitionFriendlyURLSeparator(
			"able", objectDefinition2.getObjectDefinitionId());

		objectDefinition1.setFriendlyURLSeparator(null);

		objectDefinition1 =
			_objectDefinitionLocalService.updateObjectDefinition(
				objectDefinition1);

		_upgrade();

		_assertObjectDefinitionFriendlyURLSeparator(
			"l", objectDefinition1.getObjectDefinitionId());
		_assertObjectDefinitionFriendlyURLSeparator(
			"able", objectDefinition2.getObjectDefinitionId());
	}

	private ObjectDefinition _addCustomObjectDefinition(
			String friendlyURLSeparator)
		throws Exception {

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.addCustomObjectDefinition();

		objectDefinition.setFriendlyURLSeparator(friendlyURLSeparator);

		return _objectDefinitionLocalService.updateObjectDefinition(
			objectDefinition);
	}

	private void _assertObjectDefinitionFriendlyURLSeparator(
		String expectedFriendlyURLSeparator, long objectDefinitionId) {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinition(
				objectDefinitionId);

		Assert.assertEquals(
			expectedFriendlyURLSeparator,
			objectDefinition.getFriendlyURLSeparator());
	}

	private void _upgrade() throws Exception {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME, LoggerTestUtil.OFF)) {

			UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
				_upgradeStepRegistrator, _CLASS_NAME);

			upgradeProcess.upgrade();

			_multiVMPool.clear();
		}
	}

	private static final String _CLASS_NAME =
		"com.liferay.object.internal.upgrade.v10_15_0." +
			"ObjectDefinitionUpgradeProcess";

	@Inject(
		filter = "component.name=com.liferay.object.internal.upgrade.registry.ObjectServiceUpgradeStepRegistrator"
	)
	private static UpgradeStepRegistrator _upgradeStepRegistrator;

	@Inject
	private FriendlyURLSeparatorConfigurationManager
		_friendlyURLSeparatorConfigurationManager;

	@Inject
	private MultiVMPool _multiVMPool;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

}