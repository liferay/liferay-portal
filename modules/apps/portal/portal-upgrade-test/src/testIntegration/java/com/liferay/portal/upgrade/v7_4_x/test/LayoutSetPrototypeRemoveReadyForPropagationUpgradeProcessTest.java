/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v7_4_x.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.cache.CacheRegistryUtil;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.service.LayoutSetPrototypeLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.v7_4_x.LayoutSetPrototypeRemoveReadyForPropagationUpgradeProcess;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Carlos Correa
 */
@RunWith(Arquillian.class)
public class LayoutSetPrototypeRemoveReadyForPropagationUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testUpgradeRemovesReadyForPropagation() throws Exception {
		LayoutSetPrototype layoutSetPrototype =
			_layoutSetPrototypeLocalService.getLayoutSetPrototype(
				LayoutTestUtil.addLayoutSetPrototype(
					RandomTestUtil.randomString()
				).getLayoutSetPrototypeId());

		String randomKey = RandomTestUtil.randomString();
		String randomValue = RandomTestUtil.randomString();

		layoutSetPrototype.setSettingsProperties(
			UnicodePropertiesBuilder.fastLoad(
				layoutSetPrototype.getSettings()
			).put(
				randomKey, randomValue
			).put(
				"readyForPropagation", "true"
			).build());

		layoutSetPrototype =
			_layoutSetPrototypeLocalService.updateLayoutSetPrototype(
				layoutSetPrototype);

		UpgradeProcess upgradeProcess =
			new LayoutSetPrototypeRemoveReadyForPropagationUpgradeProcess();

		upgradeProcess.upgrade();

		CacheRegistryUtil.clear();

		layoutSetPrototype =
			_layoutSetPrototypeLocalService.getLayoutSetPrototype(
				layoutSetPrototype.getLayoutSetPrototypeId());

		Assert.assertEquals(
			randomValue, layoutSetPrototype.getSettingsProperty(randomKey));
		Assert.assertNull(
			layoutSetPrototype.getSettingsProperty("readyForPropagation"));
	}

	@Test
	public void testUpgradeWithoutReadyForPropagation() throws Exception {
		LayoutSetPrototype layoutSetPrototype =
			LayoutTestUtil.addLayoutSetPrototype(RandomTestUtil.randomString());

		String settings = layoutSetPrototype.getSettings();

		UpgradeProcess upgradeProcess =
			new LayoutSetPrototypeRemoveReadyForPropagationUpgradeProcess();

		upgradeProcess.upgrade();

		CacheRegistryUtil.clear();

		layoutSetPrototype =
			_layoutSetPrototypeLocalService.getLayoutSetPrototype(
				layoutSetPrototype.getLayoutSetPrototypeId());

		Assert.assertEquals(settings, layoutSetPrototype.getSettings());
	}

	@Inject
	private LayoutSetPrototypeLocalService _layoutSetPrototypeLocalService;

}