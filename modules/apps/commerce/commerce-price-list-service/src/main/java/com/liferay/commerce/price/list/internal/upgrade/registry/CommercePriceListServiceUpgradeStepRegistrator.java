/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.price.list.internal.upgrade.registry;

import com.liferay.commerce.price.list.internal.upgrade.v1_1_0.CommercePriceEntryUpgradeProcess;
import com.liferay.commerce.price.list.internal.upgrade.v1_2_0.util.CommercePriceListAccountRelTable;
import com.liferay.commerce.price.list.internal.upgrade.v2_0_0.util.CommercePriceListCommerceAccountGroupRelTable;
import com.liferay.commerce.price.list.internal.upgrade.v2_1_0.util.CommercePriceListChannelRelTable;
import com.liferay.commerce.price.list.internal.upgrade.v2_1_0.util.CommercePriceListDiscountRelTable;
import com.liferay.commerce.price.list.internal.upgrade.v2_2_0.util.CommercePriceListOrderTypeRelTable;
import com.liferay.commerce.price.list.model.impl.CommercePriceEntryModelImpl;
import com.liferay.commerce.price.list.model.impl.CommercePriceListAccountRelModelImpl;
import com.liferay.commerce.price.list.model.impl.CommercePriceListModelImpl;
import com.liferay.commerce.price.list.model.impl.CommerceTierPriceEntryModelImpl;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CPInstanceLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.upgrade.BaseExternalReferenceCodeUpgradeProcess;
import com.liferay.portal.kernel.upgrade.CTModelUpgradeProcess;
import com.liferay.portal.kernel.upgrade.DummyUpgradeProcess;
import com.liferay.portal.kernel.upgrade.DummyUpgradeStep;
import com.liferay.portal.kernel.upgrade.MVCCVersionUpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alec Sloan
 * @author Alessio Antonio Rendina
 */
@Component(service = UpgradeStepRegistrator.class)
public class CommercePriceListServiceUpgradeStepRegistrator
	implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		if (_log.isInfoEnabled()) {
			_log.info("Commerce price list upgrade step registrator started");
		}

		registry.register(
			"1.0.0", "1.1.0",
			new CommercePriceEntryUpgradeProcess(
				_cpDefinitionLocalService, _cpInstanceLocalService));

		registry.register(
			"1.1.0", "1.2.0", CommercePriceListAccountRelTable.create());

		registry.register(
			"1.2.0", "2.0.0",
			UpgradeProcessFactory.dropColumns(
				CommercePriceEntryModelImpl.TABLE_NAME, "groupId"),
			UpgradeProcessFactory.dropColumns(
				CommercePriceListAccountRelModelImpl.TABLE_NAME, "groupId"),
			CommercePriceListCommerceAccountGroupRelTable.create(),
			UpgradeProcessFactory.dropColumns(
				CommerceTierPriceEntryModelImpl.TABLE_NAME, "groupId"));

		registry.register(
			"2.0.0", "2.0.1",
			new com.liferay.commerce.price.list.internal.upgrade.v2_1_0.
				CommercePriceEntryUpgradeProcess());

		registry.register(
			"2.0.1", "2.0.2",
			new com.liferay.commerce.price.list.internal.upgrade.v2_1_0.
				CommercePriceListUpgradeProcess());

		registry.register(
			"2.0.2", "2.0.3",
			new com.liferay.commerce.price.list.internal.upgrade.v2_1_0.
				CommerceTierPriceEntryUpgradeProcess());

		registry.register(
			"2.0.3", "2.1.0", CommercePriceListChannelRelTable.create(),
			CommercePriceListDiscountRelTable.create());

		registry.register(
			"2.1.0", "2.1.1",
			new com.liferay.commerce.price.list.internal.upgrade.v2_1_1.
				CommercePriceListUpgradeProcess());

		registry.register(
			"2.1.1", "2.1.2",
			new com.liferay.commerce.price.list.internal.upgrade.v2_1_2.
				CommercePriceListUpgradeProcess(
					_resourceActionLocalService, _resourceLocalService));

		registry.register("2.1.2", "2.1.3", new DummyUpgradeProcess());

		registry.register(
			"2.1.3", "2.2.0", CommercePriceListOrderTypeRelTable.create());

		registry.register(
			"2.2.0", "2.3.0",
			new MVCCVersionUpgradeProcess() {

				@Override
				protected String[] getTableNames() {
					return new String[] {
						"CPLCommerceGroupAccountRel", "CommercePriceEntry",
						"CommercePriceList", "CommercePriceListAccountRel",
						"CommercePriceListChannelRel",
						"CommercePriceListDiscountRel",
						"CommercePriceListOrderTypeRel",
						"CommerceTierPriceEntry"
					};
				}

			});

		registry.register(
			"2.3.0", "2.4.0",
			new com.liferay.commerce.price.list.internal.upgrade.v2_4_0.
				CommercePriceEntryUpgradeProcess());

		registry.register(
			"2.4.0", "2.5.0",
			new CTModelUpgradeProcess(
				"CPLCommerceGroupAccountRel", "CommercePriceEntry",
				"CommercePriceList", "CommercePriceListAccountRel",
				"CommercePriceListChannelRel", "CommercePriceListDiscountRel",
				"CommercePriceListOrderTypeRel", "CommerceTierPriceEntry"));

		registry.register(
			"2.5.0", "2.6.0",
			new BaseExternalReferenceCodeUpgradeProcess() {

				@Override
				protected String[] getTableNames() {
					return new String[] {
						"CommercePriceEntry", "CommercePriceList",
						"CommercePriceModifier", "CommercePricingClass",
						"CommerceTierPriceEntry"
					};
				}

			});

		registry.register("2.6.0", "2.6.1", new DummyUpgradeStep());

		registry.register(
			"2.6.1", "2.7.0",
			new com.liferay.commerce.price.list.internal.upgrade.v2_7_0.
				CommercePriceEntryUpgradeProcess());

		registry.register(
			"2.7.0", "2.8.0",
			UpgradeProcessFactory.addColumns(
				"CommercePriceEntry", "quantity BIGDECIMAL null",
				"unitOfMeasureKey VARCHAR(75) null"),
			UpgradeProcessFactory.alterColumnType(
				"CommerceTierPriceEntry", "minQuantity", "BIGDECIMAL null"));

		registry.register("2.8.0", "2.8.1", new DummyUpgradeStep());

		registry.register(
			"2.8.1", "2.9.0",
			UpgradeProcessFactory.addColumns(
				"CommercePriceEntry", "pricingQuantity BIGDECIMAL null"));

		registry.register(
			"2.9.0", "3.0.0",
			UpgradeProcessFactory.addColumns(
				CommercePriceListModelImpl.TABLE_NAME,
				"commerceCurrencyCode VARCHAR(75) null"),
			UpgradeProcessFactory.runSQL(
				StringBundler.concat(
					"update CommercePriceList set commerceCurrencyCode = ",
					"(select code_ from CommerceCurrency where ",
					"CommerceCurrency.commerceCurrencyId = ",
					"CommercePriceList.commerceCurrencyId)")),
			UpgradeProcessFactory.dropColumns(
				CommercePriceListModelImpl.TABLE_NAME, "commerceCurrencyId"));

		registry.register("3.0.0", "4.0.0", new DummyUpgradeStep());

		if (_log.isInfoEnabled()) {
			_log.info("Commerce price list upgrade step registrator finished");
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommercePriceListServiceUpgradeStepRegistrator.class);

	@Reference
	private CPDefinitionLocalService _cpDefinitionLocalService;

	@Reference
	private CPInstanceLocalService _cpInstanceLocalService;

	@Reference
	private ResourceActionLocalService _resourceActionLocalService;

	@Reference
	private ResourceLocalService _resourceLocalService;

}