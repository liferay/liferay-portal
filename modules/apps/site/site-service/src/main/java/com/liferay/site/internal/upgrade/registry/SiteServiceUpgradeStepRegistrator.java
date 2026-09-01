/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.internal.upgrade.registry;

import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.upgrade.BaseSQLServerDatetimeUpgradeProcess;
import com.liferay.portal.kernel.upgrade.DummyUpgradeStep;
import com.liferay.portal.kernel.upgrade.MVCCVersionUpgradeProcess;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.site.internal.upgrade.v2_0_0.util.SiteFriendlyURLTable;
import com.liferay.site.internal.upgrade.v2_1_2.XMLSitemapIndexEnabledConfigurationUpgradeProcess;
import com.liferay.site.internal.upgrade.v2_2_0.util.SiteSitemapRegenerationEntryTable;

import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author José Ángel Jiménez
 */
@Component(service = UpgradeStepRegistrator.class)
public class SiteServiceUpgradeStepRegistrator
	implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		registry.register(
			"1.0.0", "2.0.0",
			new BaseSQLServerDatetimeUpgradeProcess(
				new Class<?>[] {SiteFriendlyURLTable.class}));

		registry.register(
			"2.0.0", "2.1.0",
			new MVCCVersionUpgradeProcess() {

				@Override
				protected String[] getTableNames() {
					return new String[] {"SiteFriendlyURL"};
				}

			});

		registry.register("2.1.0", "2.1.1", new DummyUpgradeStep());

		registry.register(
			"2.1.1", "2.1.2",
			new XMLSitemapIndexEnabledConfigurationUpgradeProcess(
				_companyLocalService, _configurationAdmin));

		registry.register(
			"2.1.2", "2.2.0", SiteSitemapRegenerationEntryTable.create());
	}

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private ConfigurationAdmin _configurationAdmin;

}