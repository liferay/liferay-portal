/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shipping.engine.fixed.internal.upgrade.registry;

import com.liferay.commerce.shipping.engine.fixed.internal.upgrade.v2_2_0.util.CommerceShippingFixedOptionQualifierTable;
import com.liferay.commerce.shipping.engine.fixed.internal.upgrade.v2_3_0.CommerceShippingFixedOptionUpgradeProcess;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.upgrade.DummyUpgradeStep;
import com.liferay.portal.kernel.upgrade.MVCCVersionUpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import org.osgi.service.component.annotations.Component;

/**
 * @author Alessio Antonio Rendina
 */
@Component(service = UpgradeStepRegistrator.class)
public class CommerceShippingEngineFixedServiceUpgradeStepRegistrator
	implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		if (_log.isInfoEnabled()) {
			_log.info(
				"Commerce shipping engine fixed upgrade step registrator " +
					"STARTED");
		}

		registry.register(
			"1.0.0", "1.1.0",
			UpgradeProcessFactory.alterColumnName(
				"CShippingFixedOptionRel", "commerceWarehouseId",
				"commerceInventoryWarehouseId"));

		registry.register(
			"1.1.0", "2.0.0",
			UpgradeProcessFactory.alterColumnName(
				"CShippingFixedOptionRel", "commerceCountryId", "countryId"),
			UpgradeProcessFactory.alterColumnName(
				"CShippingFixedOptionRel", "commerceRegionId", "regionId"));

		registry.register(
			"2.0.0", "2.1.0",
			new MVCCVersionUpgradeProcess() {

				@Override
				protected String[] getTableNames() {
					return new String[] {
						"CShippingFixedOptionRel", "CommerceShippingFixedOption"
					};
				}

			});

		registry.register(
			"2.1.0", "2.2.0",
			CommerceShippingFixedOptionQualifierTable.create());

		registry.register(
			"2.2.0", "2.3.0", new CommerceShippingFixedOptionUpgradeProcess());

		registry.register("2.3.0", "2.4.0", new DummyUpgradeStep());

		if (_log.isInfoEnabled()) {
			_log.info(
				"Commerce shipping engine fixed upgrade step registrator " +
					"finished");
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceShippingEngineFixedServiceUpgradeStepRegistrator.class);

}