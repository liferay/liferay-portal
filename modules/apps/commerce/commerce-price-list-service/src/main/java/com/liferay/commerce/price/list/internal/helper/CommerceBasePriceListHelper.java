/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.price.list.internal.helper;

import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.service.CommerceCurrencyLocalService;
import com.liferay.commerce.price.list.constants.CommercePriceListConstants;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.service.CommercePriceListLocalService;
import com.liferay.commerce.pricing.configuration.CommercePricingConfiguration;
import com.liferay.commerce.pricing.constants.CommercePricingConstants;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.settings.SystemSettingsLocator;
import com.liferay.portal.kernel.util.LocaleUtil;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Riccardo Alberti
 */
@Component(service = CommerceBasePriceListHelper.class)
public class CommerceBasePriceListHelper {

	public void addCatalogBaseCommercePriceList(CommerceCatalog commerceCatalog)
		throws PortalException {

		CommercePricingConfiguration commercePricingConfiguration =
			_configurationProvider.getConfiguration(
				CommercePricingConfiguration.class,
				new SystemSettingsLocator(
					CommercePricingConstants.SERVICE_NAME));

		if (!Objects.equals(
				commercePricingConfiguration.commercePricingCalculationKey(),
				CommercePricingConstants.VERSION_2_0)) {

			return;
		}

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(commerceCatalog.getCompanyId());
		serviceContext.setScopeGroupId(commerceCatalog.getGroupId());
		serviceContext.setUserId(commerceCatalog.getUserId());

		_addCatalogBaseCommercePriceList(
			commerceCatalog, CommercePriceListConstants.TYPE_PRICE_LIST,
			_language.format(
				LocaleUtil.fromLanguageId(
					commerceCatalog.getCatalogDefaultLanguageId()),
				"x-base-price-list", commerceCatalog.getName(), false),
			serviceContext);
		_addCatalogBaseCommercePriceList(
			commerceCatalog, CommercePriceListConstants.TYPE_PROMOTION,
			_language.format(
				LocaleUtil.fromLanguageId(
					commerceCatalog.getCatalogDefaultLanguageId()),
				"x-base-promotion", commerceCatalog.getName(), false),
			serviceContext);
	}

	public void deleteCatalogBaseCommercePriceList(
			CommerceCatalog commerceCatalog)
		throws PortalException {

		_deleteCatalogBaseCommercePriceList(
			commerceCatalog, CommercePriceListConstants.TYPE_PRICE_LIST);

		_deleteCatalogBaseCommercePriceList(
			commerceCatalog, CommercePriceListConstants.TYPE_PROMOTION);
	}

	private void _addCatalogBaseCommercePriceList(
			CommerceCatalog commerceCatalog, String type, String name,
			ServiceContext serviceContext)
		throws PortalException {

		CommercePriceList catalogBaseCommercePriceList =
			_commercePriceListLocalService.
				fetchCatalogBaseCommercePriceListByType(
					commerceCatalog.getGroupId(), type);

		if (catalogBaseCommercePriceList == null) {
			CommerceCurrency commerceCurrency =
				_commerceCurrencyLocalService.getCommerceCurrency(
					serviceContext.getCompanyId(),
					commerceCatalog.getCommerceCurrencyCode());

			_commercePriceListLocalService.addCatalogBaseCommercePriceList(
				commerceCatalog.getGroupId(), serviceContext.getUserId(),
				commerceCurrency.getCode(), name, type, serviceContext);
		}
	}

	private void _deleteCatalogBaseCommercePriceList(
			CommerceCatalog commerceCatalog, String type)
		throws PortalException {

		CommercePriceList catalogBaseCommercePriceList =
			_commercePriceListLocalService.
				fetchCatalogBaseCommercePriceListByType(
					commerceCatalog.getGroupId(), type);

		if (catalogBaseCommercePriceList != null) {
			_commercePriceListLocalService.forceDeleteCommercePriceList(
				catalogBaseCommercePriceList);
		}
	}

	@Reference
	private CommerceCurrencyLocalService _commerceCurrencyLocalService;

	@Reference
	private CommercePriceListLocalService _commercePriceListLocalService;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private Language _language;

}