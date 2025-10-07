/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.upgrade.v10_24_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.util.Collections;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Mario Gomes
 */
@RunWith(Arquillian.class)
public class ObjectDefinitionUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testUpgrade() throws Exception {
		ObjectDefinition customObjectDefinition =
			ObjectDefinitionTestUtil.addCustomObjectDefinition();

		customObjectDefinition.setEnableFormContainer(false);

		customObjectDefinition =
			_objectDefinitionLocalService.updateObjectDefinition(
				customObjectDefinition);

		_assertObjectDefinitionEnableFormContainer(
			false, customObjectDefinition.getObjectDefinitionId());

		ObjectDefinition modifiableSystemObjectDefinition =
			ObjectDefinitionTestUtil.addModifiableSystemObjectDefinition(
				TestPropsValues.getUserId(), null, true,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				"Test" + ObjectDefinitionTestUtil.getRandomName(), null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectDefinitionConstants.SCOPE_SITE, null, 1,
				Collections.emptyList());

		modifiableSystemObjectDefinition.setEnableFormContainer(false);

		modifiableSystemObjectDefinition =
			_objectDefinitionLocalService.updateObjectDefinition(
				modifiableSystemObjectDefinition);

		_assertObjectDefinitionEnableFormContainer(
			false, modifiableSystemObjectDefinition.getObjectDefinitionId());

		ObjectDefinition unmodifiableSystemObjectDefinition =
			ObjectDefinitionTestUtil.addUnmodifiableSystemObjectDefinition(
				null, TestPropsValues.getUserId(), null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				"Test" + ObjectDefinitionTestUtil.getRandomName(), null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectDefinitionConstants.SCOPE_SITE, null, 1,
				Collections.emptyList());

		unmodifiableSystemObjectDefinition.setEnableFormContainer(true);

		unmodifiableSystemObjectDefinition =
			_objectDefinitionLocalService.updateObjectDefinition(
				unmodifiableSystemObjectDefinition);

		_assertObjectDefinitionEnableFormContainer(
			true, unmodifiableSystemObjectDefinition.getObjectDefinitionId());

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME, LoggerTestUtil.OFF)) {

			UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
				_upgradeStepRegistrator, _CLASS_NAME);

			upgradeProcess.upgrade();

			_multiVMPool.clear();
		}

		_assertObjectDefinitionEnableFormContainer(
			true, customObjectDefinition.getObjectDefinitionId());
		_assertObjectDefinitionEnableFormContainer(
			true, modifiableSystemObjectDefinition.getObjectDefinitionId());
		_assertObjectDefinitionEnableFormContainer(
			false, unmodifiableSystemObjectDefinition.getObjectDefinitionId());

		_objectDefinitionLocalService.deleteObjectDefinition(
			customObjectDefinition);
		_objectDefinitionLocalService.deleteObjectDefinition(
			modifiableSystemObjectDefinition);
		_objectDefinitionLocalService.deleteObjectDefinition(
			unmodifiableSystemObjectDefinition);
	}

	private void _assertObjectDefinitionEnableFormContainer(
		boolean expectedEnableFormContainer, long objectDefinitionId) {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinition(
				objectDefinitionId);

		Assert.assertEquals(
			expectedEnableFormContainer,
			objectDefinition.isEnableFormContainer());
	}

	private static final String _CLASS_NAME =
		"com.liferay.object.internal.upgrade.v10_24_0." +
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