/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v7_4_x.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.cache.CacheRegistryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.v7_4_x.LayoutRemoveUnusedTypeSettingsUpgradeProcess;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Carlos Correa
 */
@RunWith(Arquillian.class)
public class LayoutRemoveUnusedTypeSettingsUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testUpgradeIgnoresLayoutWithoutLayoutSetPrototypeLayoutERC()
		throws Exception {

		Group group = GroupTestUtil.addGroup();

		Layout layout = LayoutTestUtil.addTypePortletLayout(group);

		String lastMergeLayoutModifiedTime = String.valueOf(
			RandomTestUtil.randomLong());
		String lastMergeTime = String.valueOf(RandomTestUtil.randomLong());

		layout.setTypeSettingsProperties(
			UnicodePropertiesBuilder.fastLoad(
				layout.getTypeSettings()
			).put(
				"last-merge-layout-modified-time", lastMergeLayoutModifiedTime
			).put(
				"last-merge-time", lastMergeTime
			).build());

		layout = _layoutLocalService.updateLayout(layout);

		UpgradeProcess upgradeProcess =
			new LayoutRemoveUnusedTypeSettingsUpgradeProcess();

		upgradeProcess.upgrade();

		CacheRegistryUtil.clear();

		layout = _layoutLocalService.getLayout(layout.getPlid());

		Assert.assertEquals(
			lastMergeLayoutModifiedTime,
			layout.getTypeSettingsProperty("last-merge-layout-modified-time"));
		Assert.assertEquals(
			lastMergeTime, layout.getTypeSettingsProperty("last-merge-time"));
	}

	@Test
	public void testUpgradeRemovesUnusedSettings() throws Exception {
		Group group = GroupTestUtil.addGroup();

		Layout layout = LayoutTestUtil.addTypePortletLayout(group);

		layout.setLayoutSetPrototypeLayoutERC(RandomTestUtil.randomString());

		layout.setTypeSettingsProperties(
			UnicodePropertiesBuilder.fastLoad(
				layout.getTypeSettings()
			).put(
				"last-merge-layout-modified-time",
				String.valueOf(RandomTestUtil.randomLong())
			).put(
				"last-merge-time", String.valueOf(RandomTestUtil.randomLong())
			).build());

		layout = _layoutLocalService.updateLayout(layout);

		UpgradeProcess upgradeProcess =
			new LayoutRemoveUnusedTypeSettingsUpgradeProcess();

		upgradeProcess.upgrade();

		CacheRegistryUtil.clear();

		layout = _layoutLocalService.getLayout(layout.getPlid());

		Assert.assertNull(
			layout.getTypeSettingsProperty("last-merge-layout-modified-time"));
		Assert.assertNull(layout.getTypeSettingsProperty("last-merge-time"));
	}

	@Inject
	private LayoutLocalService _layoutLocalService;

}