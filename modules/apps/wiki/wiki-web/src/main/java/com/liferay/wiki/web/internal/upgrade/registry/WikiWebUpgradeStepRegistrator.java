/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.web.internal.upgrade.registry;

import com.liferay.portal.configuration.persistence.upgrade.ConfigurationUpgradeStepFactory;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.upgrade.BaseStagingGroupTypeSettingsUpgradeProcess;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.wiki.constants.WikiPortletKeys;
import com.liferay.wiki.web.internal.configuration.WikiPortletInstanceConfiguration;
import com.liferay.wiki.web.internal.upgrade.v1_0_0.UpgradePortletSettings;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Iván Zaera
 * @author Manuel de la Peña
 */
@Component(service = UpgradeStepRegistrator.class)
public class WikiWebUpgradeStepRegistrator implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		registry.registerInitialization();

		registry.register("0.0.1", "1.0.0", new UpgradePortletSettings());

		registry.register(
			"1.0.0", "1.0.1",
			new BaseStagingGroupTypeSettingsUpgradeProcess(
				_companyLocalService, _groupLocalService, WikiPortletKeys.WIKI,
				WikiPortletKeys.WIKI_ADMIN));

		registry.register(
			"1.0.1", "1.0.1.step-1",
			_configurationUpgradeStepFactory.createUpgradeStep(
				"com.liferay.wiki.configuration." +
					"WikiPortletInstanceConfiguration",
				WikiPortletInstanceConfiguration.class.getName()));

		registry.register(
			"1.0.1.step-1", "1.0.2",
			_configurationUpgradeStepFactory.createUpgradeStep(
				"com.liferay.wiki.web.configuration." +
					"WikiPortletInstanceConfiguration",
				WikiPortletInstanceConfiguration.class.getName()));
	}

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private ConfigurationUpgradeStepFactory _configurationUpgradeStepFactory;

	@Reference
	private GroupLocalService _groupLocalService;

}