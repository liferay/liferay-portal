/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.upgrade.v13_2_0.test;

import com.liferay.account.model.AccountEntry;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.system.SystemObjectDefinitionManager;
import com.liferay.object.system.SystemObjectDefinitionManagerRegistry;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeStep;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import java.util.Collection;
import java.util.Collections;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Carolina Barbosa
 */
@RunWith(Arquillian.class)
public class ObjectDefinitionExternalReferenceCodeUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator, _CLASS_NAME);

		_systemObjectDefinitionManagerRegistry =
			ReflectionTestUtil.getAndSetFieldValue(
				_upgradeProcess, "_systemObjectDefinitionManagerRegistry",
				new SystemObjectDefinitionManagerRegistry() {

					@Override
					public SystemObjectDefinitionManager
						getSystemObjectDefinitionManager(String name) {

						return null;
					}

					@Override
					public Collection<SystemObjectDefinitionManager>
						getSystemObjectDefinitionManagers() {

						return Collections.emptyList();
					}

				});
	}

	@After
	public void tearDown() throws Exception {
		ReflectionTestUtil.setFieldValue(
			_upgradeProcess, "_systemObjectDefinitionManagerRegistry",
			_systemObjectDefinitionManagerRegistry);
	}

	@Test
	public void testUpgrade() throws Exception {
		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinitionByClassName(
				TestPropsValues.getCompanyId(), AccountEntry.class.getName());

		objectDefinition.setExternalReferenceCode(
			RandomTestUtil.randomString());

		objectDefinition = _objectDefinitionLocalService.updateObjectDefinition(
			objectDefinition);

		for (UpgradeStep upgradeStep : _upgradeProcess.getUpgradeSteps()) {
			UpgradeProcess upgradeProcess = (UpgradeProcess)upgradeStep;

			upgradeProcess.upgrade();
		}

		EntityCacheUtil.clearCache();

		objectDefinition = _objectDefinitionLocalService.fetchObjectDefinition(
			objectDefinition.getObjectDefinitionId());

		Assert.assertEquals(
			"L_ACCOUNT", objectDefinition.getExternalReferenceCode());
	}

	private static final String _CLASS_NAME =
		"com.liferay.object.internal.upgrade.v13_2_0." +
			"ObjectDefinitionExternalReferenceCodeUpgradeProcess";

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	private SystemObjectDefinitionManagerRegistry
		_systemObjectDefinitionManagerRegistry;
	private UpgradeProcess _upgradeProcess;

	@Inject(
		filter = "component.name=com.liferay.object.internal.upgrade.registry.ObjectServiceUpgradeStepRegistrator"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}