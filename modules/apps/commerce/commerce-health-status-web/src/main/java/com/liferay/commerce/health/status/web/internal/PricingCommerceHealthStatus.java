/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.health.status.web.internal;

import com.liferay.commerce.constants.CommerceHealthStatusConstants;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.service.CommerceCurrencyLocalService;
import com.liferay.commerce.health.status.CommerceHealthStatus;
import com.liferay.commerce.price.list.constants.CommercePriceListConstants;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.service.CommercePriceEntryLocalService;
import com.liferay.commerce.price.list.service.CommercePriceListLocalService;
import com.liferay.commerce.pricing.configuration.CommercePricingConfiguration;
import com.liferay.commerce.pricing.constants.CommercePricingConstants;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CPInstanceUnitOfMeasure;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CPInstanceLocalService;
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.settings.SystemSettingsLocator;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"commerce.health.status.display.order:Integer=130",
		"commerce.health.status.key=" + CommerceHealthStatusConstants.PRICING_COMMERCE_HEALTH_STATUS_KEY
	},
	service = CommerceHealthStatus.class
)
public class PricingCommerceHealthStatus implements CommerceHealthStatus {

	@Override
	public void fixIssue(HttpServletRequest httpServletRequest)
		throws PortalException {

		try {
			ServiceContext serviceContext = ServiceContextFactory.getInstance(
				httpServletRequest);

			Callable<Object> pricingCallable =
				new PricingCommerceHealthStatus.PricingCallable(serviceContext);

			TransactionInvokerUtil.invoke(_transactionConfig, pricingCallable);
		}
		catch (Throwable throwable) {
			_log.error(throwable, throwable);
		}
	}

	@Override
	public String getDescription(Locale locale) {
		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", locale, getClass());

		return _language.get(
			resourceBundle,
			CommerceHealthStatusConstants.
				PRICING_COMMERCE_HEALTH_STATUS_DESCRIPTION);
	}

	@Override
	public String getKey() {
		return CommerceHealthStatusConstants.PRICING_COMMERCE_HEALTH_STATUS_KEY;
	}

	@Override
	public String getName(Locale locale) {
		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", locale, getClass());

		return _language.get(
			resourceBundle,
			CommerceHealthStatusConstants.PRICING_COMMERCE_HEALTH_STATUS_KEY);
	}

	@Override
	public int getType() {
		return CommerceHealthStatusConstants.
			COMMERCE_HEALTH_STATUS_TYPE_VIRTUAL_INSTANCE;
	}

	@Override
	public boolean isActive() {
		return true;
	}

	@Override
	public boolean isFixed(long companyId, long commerceChannelId)
		throws PortalException {

		CommercePricingConfiguration commercePricingConfiguration =
			_configurationProvider.getConfiguration(
				CommercePricingConfiguration.class,
				new SystemSettingsLocator(
					CommercePricingConstants.SERVICE_NAME));

		if (Objects.equals(
				commercePricingConfiguration.commercePricingCalculationKey(),
				CommercePricingConstants.VERSION_2_0)) {

			List<CommerceCatalog> commerceCatalogs =
				_commerceCatalogLocalService.search(companyId);

			for (CommerceCatalog commerceCatalog : commerceCatalogs) {
				CommercePriceList commercePriceList =
					_commercePriceListLocalService.
						fetchCatalogBaseCommercePriceListByType(
							commerceCatalog.getGroupId(),
							CommercePriceListConstants.TYPE_PRICE_LIST);

				if (commercePriceList == null) {
					return false;
				}

				commercePriceList =
					_commercePriceListLocalService.
						fetchCatalogBaseCommercePriceListByType(
							commerceCatalog.getGroupId(),
							CommercePriceListConstants.TYPE_PROMOTION);

				if (commercePriceList == null) {
					return false;
				}
			}
		}

		return true;
	}

	private void _addBaseCommercePriceListEntries(
			CPDefinition cpDefinition, CommercePriceList commercePriceList,
			ServiceContext serviceContext)
		throws Exception {

		List<CPInstance> cpInstances =
			_cpInstanceLocalService.getCPDefinitionApprovedCPInstances(
				cpDefinition.getCPDefinitionId());

		for (CPInstance cpInstance : cpInstances) {
			BigDecimal price = cpInstance.getPrice();

			if (CommercePriceListConstants.TYPE_PROMOTION.equals(
					commercePriceList.getType())) {

				price = cpInstance.getPromoPrice();
			}

			List<CPInstanceUnitOfMeasure> cpInstanceUnitOfMeasures =
				cpInstance.getCPInstanceUnitOfMeasures(
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

			if (cpInstanceUnitOfMeasures.isEmpty()) {
				_commercePriceEntryLocalService.addCommercePriceEntry(
					null, cpDefinition.getCProductId(),
					cpInstance.getCPInstanceUuid(),
					commercePriceList.getCommercePriceListId(), price, false,
					BigDecimal.ZERO, null, serviceContext);
			}
			else {
				for (CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure :
						cpInstanceUnitOfMeasures) {

					_commercePriceEntryLocalService.addCommercePriceEntry(
						null, cpDefinition.getCProductId(),
						cpInstance.getCPInstanceUuid(),
						commercePriceList.getCommercePriceListId(), price,
						false, BigDecimal.ZERO,
						cpInstanceUnitOfMeasure.getKey(), serviceContext);
				}
			}
		}
	}

	private void _addCatalogBaseCommercePriceList(
			CommerceCatalog commerceCatalog, String type, String name,
			ServiceContext serviceContext)
		throws Exception {

		CommercePriceList commercePriceList =
			_commercePriceListLocalService.
				fetchCatalogBaseCommercePriceListByType(
					commerceCatalog.getGroupId(), type);

		if (commercePriceList == null) {
			CommerceCurrency commerceCurrency =
				_commerceCurrencyLocalService.getCommerceCurrency(
					serviceContext.getCompanyId(),
					commerceCatalog.getCommerceCurrencyCode());

			commercePriceList =
				_commercePriceListLocalService.addCatalogBaseCommercePriceList(
					commerceCatalog.getGroupId(), serviceContext.getUserId(),
					commerceCurrency.getCode(), type, name, serviceContext);

			List<CPDefinition> cpDefinitions =
				_cpDefinitionLocalService.getCPDefinitions(
					commerceCatalog.getGroupId(),
					WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS);

			for (CPDefinition cpDefinition : cpDefinitions) {
				_addBaseCommercePriceListEntries(
					cpDefinition, commercePriceList, serviceContext);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PricingCommerceHealthStatus.class);

	private static final TransactionConfig _transactionConfig =
		TransactionConfig.Factory.create(
			Propagation.REQUIRED, new Class<?>[] {Exception.class});

	@Reference
	private CommerceCatalogLocalService _commerceCatalogLocalService;

	@Reference
	private CommerceCurrencyLocalService _commerceCurrencyLocalService;

	@Reference
	private CommercePriceEntryLocalService _commercePriceEntryLocalService;

	@Reference
	private CommercePriceListLocalService _commercePriceListLocalService;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private CPDefinitionLocalService _cpDefinitionLocalService;

	@Reference
	private CPInstanceLocalService _cpInstanceLocalService;

	@Reference
	private Language _language;

	private class PricingCallable implements Callable<Object> {

		@Override
		public Object call() throws Exception {
			CommercePricingConfiguration commercePricingConfiguration =
				_configurationProvider.getConfiguration(
					CommercePricingConfiguration.class,
					new SystemSettingsLocator(
						CommercePricingConstants.SERVICE_NAME));

			if (Objects.equals(
					commercePricingConfiguration.
						commercePricingCalculationKey(),
					CommercePricingConstants.VERSION_2_0)) {

				List<CommerceCatalog> commerceCatalogs =
					_commerceCatalogLocalService.search(
						_serviceContext.getCompanyId());

				for (CommerceCatalog commerceCatalog : commerceCatalogs) {
					_addCatalogBaseCommercePriceList(
						commerceCatalog,
						CommercePriceListConstants.TYPE_PRICE_LIST,
						_language.format(
							LocaleUtil.fromLanguageId(
								commerceCatalog.getCatalogDefaultLanguageId()),
							"x-base-price-list", commerceCatalog.getName(),
							false),
						_serviceContext);
					_addCatalogBaseCommercePriceList(
						commerceCatalog,
						CommercePriceListConstants.TYPE_PROMOTION,
						_language.format(
							LocaleUtil.fromLanguageId(
								commerceCatalog.getCatalogDefaultLanguageId()),
							"x-base-promotion", commerceCatalog.getName(),
							false),
						_serviceContext);
				}
			}

			return null;
		}

		private PricingCallable(ServiceContext serviceContext) {
			_serviceContext = serviceContext;
		}

		private final ServiceContext _serviceContext;

	}

}