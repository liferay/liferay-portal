/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.upgrade.v13_1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeException;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.util.Collections;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jhosseph Gonzalez
 */
@RunWith(Arquillian.class)
public class ObjectDefinitionExternalReferenceCodeUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_userObjectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinitionByClassName(
				TestPropsValues.getCompanyId(), User.class.getName());

		_userObjectDefinition.setExternalReferenceCode(
			RandomTestUtil.randomString());

		_userObjectDefinition =
			_objectDefinitionLocalService.updateObjectDefinition(
				_userObjectDefinition);
	}

	@After
	public void tearDown() throws Exception {
		_userObjectDefinition.setExternalReferenceCode("L_USER");

		_userObjectDefinition =
			_objectDefinitionLocalService.updateObjectDefinition(
				_userObjectDefinition);
	}

	@Test
	public void testUpgrade() throws Exception {
		_objectDefinition =
			ObjectDefinitionTestUtil.addUnmodifiableSystemObjectDefinition(
				null, TestPropsValues.getUserId(), null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				"Test" + ObjectDefinitionTestUtil.getRandomName(), null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectDefinitionConstants.SCOPE_SITE, null, 1,
				Collections.emptyList());

		String externalReferenceCode =
			_objectDefinition.getExternalReferenceCode();

		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator, _CLASS_NAME);

		upgradeProcess.upgrade();

		_multiVMPool.clear();

		_userObjectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinition(
				_userObjectDefinition.getObjectDefinitionId());

		Assert.assertEquals(
			"L_USER", _userObjectDefinition.getExternalReferenceCode());

		_objectDefinition = _objectDefinitionLocalService.fetchObjectDefinition(
			_objectDefinition.getObjectDefinitionId());

		Assert.assertEquals(
			externalReferenceCode,
			_objectDefinition.getExternalReferenceCode());
	}

	@Test
	public void testUpgradeWithDuplicateExternalReferenceCode()
		throws Exception {

		try {
			_objectDefinition =
				ObjectDefinitionTestUtil.addCustomObjectDefinition();

			_objectDefinition.setExternalReferenceCode("L_USER");

			_objectDefinition =
				_objectDefinitionLocalService.updateObjectDefinition(
					_objectDefinition);

			UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
				_upgradeStepRegistrator, _CLASS_NAME);

			Assert.assertThrows(
				UpgradeException.class, upgradeProcess::upgrade);
		}
		finally {
			if (_objectDefinition != null) {
				_objectDefinition.setExternalReferenceCode(
					RandomTestUtil.randomString());

				_objectDefinitionLocalService.updateObjectDefinition(
					_objectDefinition);
			}
		}
	}

	private static final String _CLASS_NAME =
		"com.liferay.object.internal.upgrade.v13_1_0." +
			"ObjectDefinitionExternalReferenceCodeUpgradeProcess";

	@Inject
	private MultiVMPool _multiVMPool;

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject(
		filter = "component.name=com.liferay.object.internal.upgrade.registry.ObjectServiceUpgradeStepRegistrator"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

	private ObjectDefinition _userObjectDefinition;

}