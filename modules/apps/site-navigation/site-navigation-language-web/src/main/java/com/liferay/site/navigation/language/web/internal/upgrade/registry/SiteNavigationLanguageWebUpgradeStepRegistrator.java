/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.navigation.language.web.internal.upgrade.registry;

import com.liferay.portal.configuration.persistence.upgrade.ConfigurationUpgradeStepFactory;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portlet.display.template.upgrade.BaseUpgradePortletPreferences;
import com.liferay.site.navigation.language.constants.SiteNavigationLanguagePortletKeys;
import com.liferay.site.navigation.language.web.internal.configuration.SiteNavigationLanguagePortletInstanceConfiguration;
import com.liferay.site.navigation.language.web.internal.upgrade.v1_0_0.UpgradePortletId;
import com.liferay.site.navigation.language.web.internal.upgrade.v1_0_0.UpgradePortletPreferences;

import jakarta.portlet.PortletPreferences;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 * @author Julio Camarero
 */
@Component(service = UpgradeStepRegistrator.class)
public class SiteNavigationLanguageWebUpgradeStepRegistrator
	implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		registry.registerInitialization();

		registry.register(
			"0.0.1", "1.0.0", new UpgradePortletId(),
			new UpgradePortletPreferences());

		registry.register(
			"1.0.0", "1.0.1",
			_configurationUpgradeStepFactory.createUpgradeStep(
				"com.liferay.site.navigation.language.web.configuration." +
					"SiteNavigationLanguagePortletInstanceConfiguration",
				SiteNavigationLanguagePortletInstanceConfiguration.class.
					getName()));

		registry.register(
			"1.0.1", "1.0.2",
			new BaseUpgradePortletPreferences() {

				@Override
				protected String[] getPortletIds() {
					return new String[] {
						SiteNavigationLanguagePortletKeys.
							SITE_NAVIGATION_LANGUAGE
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

	@Reference
	private ConfigurationUpgradeStepFactory _configurationUpgradeStepFactory;

}