/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.navigation.menu.web.internal.upgrade.registry;

import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.site.navigation.menu.web.internal.upgrade.v1_0_0.UpgradePortletId;
import com.liferay.site.navigation.menu.web.internal.upgrade.v1_0_0.UpgradePortletPreferences;
import com.liferay.site.navigation.menu.web.internal.upgrade.v1_0_1.PortletPreferencesUpgradeProcess;
import com.liferay.site.navigation.service.SiteNavigationMenuItemLocalService;
import com.liferay.site.navigation.service.SiteNavigationMenuLocalService;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(service = UpgradeStepRegistrator.class)
public class SiteNavigationMenuWebUpgradeStepRegistrator
	implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		registry.registerInitialization();

		registry.register("0.0.1", "0.0.2", new UpgradePortletId());

		registry.register("0.0.2", "1.0.0", new UpgradePortletPreferences());

		registry.register(
			"1.0.0", "1.0.1", new PortletPreferencesUpgradeProcess());

		registry.register(
			"1.0.1", "1.0.2",
			new com.liferay.site.navigation.menu.web.internal.upgrade.v1_0_2.
				UpgradePortletPreferences(
					_siteNavigationMenuItemLocalService,
					_siteNavigationMenuLocalService));

		registry.register(
			"1.0.2", "1.0.3",
			new com.liferay.site.navigation.menu.web.internal.upgrade.v1_0_3.
				UpgradePortletPreferences(_layoutLocalService));

		registry.register(
			"1.0.3", "1.0.4",
			new com.liferay.site.navigation.menu.web.internal.upgrade.v1_0_4.
				UpgradePortletPreferences(_layoutLocalService));

		registry.register(
			"1.0.4", "1.0.5",
			new com.liferay.site.navigation.menu.web.internal.upgrade.v1_0_5.
				UpgradePortletPreferences(_layoutLocalService));
	}

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private SiteNavigationMenuItemLocalService
		_siteNavigationMenuItemLocalService;

	@Reference
	private SiteNavigationMenuLocalService _siteNavigationMenuLocalService;

}