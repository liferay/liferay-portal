/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.tax.engine.fixed.internal.upgrade.registry;

import com.liferay.portal.kernel.upgrade.MVCCVersionUpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import org.osgi.service.component.annotations.Component;

/**
 * @author Pei-Jung Lan
 */
@Component(service = UpgradeStepRegistrator.class)
public class CommerceTaxEngineFixedServiceUpgradeStepRegistrator
	implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		registry.register(
			"1.0.0", "2.0.0",
			UpgradeProcessFactory.alterColumnName(
				"CommerceTaxFixedRateAddressRel", "commerceCountryId",
				"countryId"),
			UpgradeProcessFactory.alterColumnName(
				"CommerceTaxFixedRateAddressRel", "commerceRegionId",
				"regionId"));

		registry.register(
			"2.0.0", "2.1.0",
			new MVCCVersionUpgradeProcess() {

				@Override
				protected String[] getTableNames() {
					return new String[] {
						"CommerceTaxFixedRate", "CommerceTaxFixedRateAddressRel"
					};
				}

			});
	}

}