/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.pricing.internal.upgrade.registry;

import com.liferay.commerce.pricing.internal.upgrade.v2_1_0.CommercePricingConfigurationUpgradeProcess;
import com.liferay.commerce.pricing.internal.upgrade.v2_4_0.CommercePricingClassUpgradeProcess;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.upgrade.CTModelUpgradeProcess;
import com.liferay.portal.kernel.upgrade.DummyUpgradeProcess;
import com.liferay.portal.kernel.upgrade.MVCCVersionUpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Riccardo Alberti
 */
@Component(service = UpgradeStepRegistrator.class)
public class CommercePricingServiceUpgradeStepRegistrator
	implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		registry.register(
			"1.0.0", "1.1.0",
			UpgradeProcessFactory.alterColumnType(
				"CommercePricingClass", "title", "TEXT"),
			UpgradeProcessFactory.alterColumnType(
				"CommercePricingClass", "description", "TEXT"));

		registry.register(
			"1.1.0", "2.0.0",
			new com.liferay.commerce.pricing.internal.upgrade.v2_0_0.
				CommercePricingClassUpgradeProcess(
					_resourceActionLocalService, _resourceLocalService));

		registry.register(
			"2.0.0", "2.0.1",
			UpgradeProcessFactory.runSQL(
				"update CommercePriceModifier set target = 'product-groups' " +
					"where target = 'pricing-classes'",
				"update CommercePriceModifier set modifierType = " +
					"'fixed-amount' where modifierType = 'absolute'",
				"update CommercePriceModifier set modifierType = 'replace' " +
					"where modifierType = 'override'"));

		registry.register("2.0.1", "2.0.2", new DummyUpgradeProcess());

		registry.register(
			"2.0.2", "2.1.0",
			new CommercePricingConfigurationUpgradeProcess(
				_configurationProvider));

		registry.register(
			"2.1.0", "2.2.0",
			new MVCCVersionUpgradeProcess() {

				@Override
				protected String[] getTableNames() {
					return new String[] {
						"CPricingClassCPDefinitionRel", "CommercePriceModifier",
						"CommercePriceModifierRel", "CommercePricingClass"
					};
				}

			});

		registry.register(
			"2.2.0", "2.3.0",
			new CTModelUpgradeProcess(
				"CPricingClassCPDefinitionRel", "CommercePriceModifier",
				"CommercePriceModifierRel", "CommercePricingClass"));

		registry.register(
			"2.3.0", "2.4.0", new CommercePricingClassUpgradeProcess());
	}

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private ResourceActionLocalService _resourceActionLocalService;

	@Reference
	private ResourceLocalService _resourceLocalService;

}