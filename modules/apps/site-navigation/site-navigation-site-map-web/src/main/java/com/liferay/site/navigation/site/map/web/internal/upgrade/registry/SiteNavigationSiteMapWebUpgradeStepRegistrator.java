/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.navigation.site.map.web.internal.upgrade.registry;

import com.liferay.portal.kernel.upgrade.BasePortletIdUpgradeProcess;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portlet.display.template.upgrade.BaseUpgradePortletPreferences;
import com.liferay.site.navigation.site.map.web.internal.constants.SiteNavigationSiteMapPortletKeys;

import jakarta.portlet.PortletPreferences;

import org.osgi.service.component.annotations.Component;

/**
 * @author Eudaldo Alonso
 */
@Component(service = UpgradeStepRegistrator.class)
public class SiteNavigationSiteMapWebUpgradeStepRegistrator
	implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		registry.registerInitialization();

		registry.register(
			"0.0.1", "1.0.0",
			new BasePortletIdUpgradeProcess() {

				@Override
				protected String[][] getRenamePortletIdsArray() {
					return new String[][] {
						{
							"85",
							SiteNavigationSiteMapPortletKeys.
								SITE_NAVIGATION_SITE_MAP
						}
					};
				}

			});

		registry.register(
			"1.0.0", "1.0.1",
			new BaseUpgradePortletPreferences() {

				@Override
				protected String[] getPortletIds() {
					return new String[] {
						SiteNavigationSiteMapPortletKeys.
							SITE_NAVIGATION_SITE_MAP + "_INSTANCE_%"
					};
				}

				@Override
				protected void upgradePreferences(
						long companyId, long ownerId, int ownerType, long plid,
						String portletId, PortletPreferences portletPreferences)
					throws Exception {
				}

			});
	}

}