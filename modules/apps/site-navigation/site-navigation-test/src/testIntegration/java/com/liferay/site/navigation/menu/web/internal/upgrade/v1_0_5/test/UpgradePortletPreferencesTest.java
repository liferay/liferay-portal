/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.navigation.menu.web.internal.upgrade.v1_0_5.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.version.Version;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portlet.display.template.test.util.BaseUpgradePortletPreferencesTestCase;
import com.liferay.site.navigation.constants.SiteNavigationMenuPortletKeys;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jonathan McCann
 */
@RunWith(Arquillian.class)
public class UpgradePortletPreferencesTest
	extends BaseUpgradePortletPreferencesTestCase {

	@Test
	public void testUpgrade() throws Exception {
		_updateLayoutExternalReferenceCode();

		testUpgrade(
			HashMapBuilder.put(
				"rootMenuItemExternalReferenceCode",
				layout.getExternalReferenceCode()
			).build(),
			HashMapBuilder.put(
				"rootMenuItemExternalReferenceCode", layout.getUuid()
			).build());
	}

	@Test
	public void testUpgradeWithLayoutExternalReferenceCode() throws Exception {
		_updateLayoutExternalReferenceCode();

		testUpgrade(
			HashMapBuilder.put(
				"rootMenuItemExternalReferenceCode",
				layout.getExternalReferenceCode()
			).build());
	}

	@Test
	public void testUpgradeWithNonexistentLayout() throws Exception {
		testUpgrade(
			HashMapBuilder.put(
				"rootMenuItemExternalReferenceCode",
				RandomTestUtil.randomString()
			).build());
	}

	@Override
	protected String getPortletId() {
		return SiteNavigationMenuPortletKeys.SITE_NAVIGATION_MENU;
	}

	@Override
	protected UpgradeStepRegistrator getUpgradeStepRegistrator() {
		return _upgradeStepRegistrator;
	}

	@Override
	protected Version getVersion() {
		return _VERSION;
	}

	private void _updateLayoutExternalReferenceCode() throws Exception {
		layout = _layoutLocalService.getLayout(layout.getPlid());

		layout.setExternalReferenceCode(RandomTestUtil.randomString());

		layout = _layoutLocalService.updateLayout(layout);
	}

	private static final Version _VERSION = new Version(1, 0, 5);

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject(
		filter = "(&(component.name=com.liferay.site.navigation.menu.web.internal.upgrade.registry.SiteNavigationMenuWebUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}