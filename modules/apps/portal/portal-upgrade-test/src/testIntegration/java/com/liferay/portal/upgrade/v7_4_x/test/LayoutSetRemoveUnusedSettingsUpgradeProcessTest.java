/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v7_4_x.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.cache.CacheRegistryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.v7_4_x.LayoutSetRemoveUnusedSettingsUpgradeProcess;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Carlos Correa
 */
@RunWith(Arquillian.class)
public class LayoutSetRemoveUnusedSettingsUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testUpgradeRemovesUnusedSettings() throws Exception {
		Group group = GroupTestUtil.addGroup();

		LayoutSet layoutSet = _layoutSetLocalService.getLayoutSet(
			group.getGroupId(), false);

		String survivingKey = RandomTestUtil.randomString();
		String survivingValue = RandomTestUtil.randomString();

		layoutSet.setSettingsProperties(
			UnicodePropertiesBuilder.fastLoad(
				layoutSet.getSettings()
			).put(
				survivingKey, survivingValue
			).put(
				"last-merge-time", String.valueOf(RandomTestUtil.randomLong())
			).put(
				"last-merge-version", String.valueOf(RandomTestUtil.randomInt())
			).put(
				"last-reset-time", String.valueOf(RandomTestUtil.randomLong())
			).put(
				"merge-fail-count", String.valueOf(RandomTestUtil.randomInt())
			).put(
				"merge-fail-friendly-url-layouts",
				"/" + RandomTestUtil.randomString()
			).build());

		layoutSet = _layoutSetLocalService.updateLayoutSet(layoutSet);

		UpgradeProcess upgradeProcess =
			new LayoutSetRemoveUnusedSettingsUpgradeProcess();

		upgradeProcess.upgrade();

		CacheRegistryUtil.clear();

		layoutSet = _layoutSetLocalService.getLayoutSet(
			layoutSet.getLayoutSetId());

		Assert.assertEquals(
			survivingValue, layoutSet.getSettingsProperty(survivingKey));

		Assert.assertNull(layoutSet.getSettingsProperty("last-merge-time"));
		Assert.assertNull(layoutSet.getSettingsProperty("last-merge-version"));
		Assert.assertNull(layoutSet.getSettingsProperty("last-reset-time"));
		Assert.assertNull(layoutSet.getSettingsProperty("merge-fail-count"));
		Assert.assertNull(
			layoutSet.getSettingsProperty("merge-fail-friendly-url-layouts"));
	}

	@Test
	public void testUpgradeWithoutUnusedSettings() throws Exception {
		Group group = GroupTestUtil.addGroup();

		LayoutSet layoutSet = _layoutSetLocalService.getLayoutSet(
			group.getGroupId(), false);

		String settings = layoutSet.getSettings();

		UpgradeProcess upgradeProcess =
			new LayoutSetRemoveUnusedSettingsUpgradeProcess();

		upgradeProcess.upgrade();

		CacheRegistryUtil.clear();

		layoutSet = _layoutSetLocalService.getLayoutSet(
			layoutSet.getLayoutSetId());

		Assert.assertEquals(settings, layoutSet.getSettings());
	}

	@Inject
	private LayoutSetLocalService _layoutSetLocalService;

}