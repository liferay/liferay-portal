/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.internal.util.v1_0;

import com.liferay.commerce.currency.exception.NoSuchCurrencyException;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.service.CommerceCurrencyService;
import com.liferay.commerce.pricing.model.CommercePricingClass;
import com.liferay.commerce.pricing.model.CommercePricingClassCPDefinitionRel;
import com.liferay.commerce.pricing.service.CommercePricingClassCPDefinitionRelService;
import com.liferay.commerce.product.exception.NoSuchCatalogException;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CPDefinitionService;
import com.liferay.commerce.product.service.CProductLocalService;
import com.liferay.commerce.product.service.CommerceCatalogService;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductGroupProduct;
import com.liferay.headless.commerce.core.helper.ServiceContextHelper;
import com.liferay.headless.commerce.core.util.CommerceCurrencyUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.Validator;

/**
 * @author Riccardo Alberti
 */
public class ProductGroupProductUtil {

	public static CommercePricingClassCPDefinitionRel
			addCommercePricingClassCPDefinitionRel(
				CProductLocalService cProductLocalService,
				CommerceCatalogService commerceCatalogService,
				CommerceCurrencyService commerceCurrencyService,
				CommercePricingClass commercePricingClass,
				CommercePricingClassCPDefinitionRelService
					commercePricingClassCPDefinitionRelService,
				CPDefinitionService cpDefinitionService,
				ProductGroupProduct productGroupProduct,
				ServiceContextHelper serviceContextHelper)
		throws PortalException {

		ServiceContext serviceContext =
			serviceContextHelper.getServiceContext();

		return commercePricingClassCPDefinitionRelService.
			addCommercePricingClassCPDefinitionRel(
				commercePricingClass.getCommercePricingClassId(),
				getCPDefinitionId(
					cProductLocalService, commerceCatalogService,
					commerceCurrencyService, cpDefinitionService,
					productGroupProduct, serviceContext),
				serviceContext);
	}

	public static long getCPDefinitionId(
			CProductLocalService cProductLocalService,
			CommerceCatalogService commerceCatalogService,
			CommerceCurrencyService commerceCurrencyService,
			CPDefinitionService cpDefinitionService,
			ProductGroupProduct productGroupProduct,
			ServiceContext serviceContext)
		throws PortalException {

		String productExternalReferenceCode =
			productGroupProduct.getProductExternalReferenceCode();

		if (Validator.isNull(productExternalReferenceCode)) {
			CProduct cProduct = cProductLocalService.getCProduct(
				productGroupProduct.getProductId());

			return cProduct.getPublishedCPDefinitionId();
		}

		CProduct cProduct =
			cProductLocalService.fetchCProductByExternalReferenceCode(
				productExternalReferenceCode, serviceContext.getCompanyId());

		if (cProduct != null) {
			return cProduct.getPublishedCPDefinitionId();
		}

		String catalogExternalReferenceCode =
			productGroupProduct.getCatalogExternalReferenceCode();

		if (Validator.isNull(catalogExternalReferenceCode)) {
			throw new NoSuchCatalogException(
				"Unable to find catalog with external reference code " +
					catalogExternalReferenceCode);
		}

		CommerceCatalog commerceCatalog =
			commerceCatalogService.fetchCommerceCatalogByExternalReferenceCode(
				catalogExternalReferenceCode, serviceContext.getCompanyId());

		if (commerceCatalog == null) {
			commerceCatalog = _getCommerceCatalog(
				catalogExternalReferenceCode, commerceCatalogService,
				commerceCurrencyService, productGroupProduct, serviceContext);
		}

		CPDefinition cpDefinition =
			ProductUtil.getCPDefinitionByCProductExternalReferenceCode(
				serviceContext.getCompanyId(), cpDefinitionService,
				productExternalReferenceCode, commerceCatalog.getGroupId(),
				productGroupProduct.getProductType());

		return cpDefinition.getCPDefinitionId();
	}

	private static CommerceCatalog _getCommerceCatalog(
			String catalogExternalReferenceCode,
			CommerceCatalogService commerceCatalogService,
			CommerceCurrencyService commerceCurrencyService,
			ProductGroupProduct productGroupProduct,
			ServiceContext serviceContext)
		throws PortalException {

		if (!LazyReferencingThreadLocal.isEnabled()) {
			throw new NoSuchCatalogException(
				"Unable to find catalog with external reference code " +
					catalogExternalReferenceCode);
		}

		CommerceCurrency commerceCurrency = _getCommerceCurrency(
			commerceCurrencyService, productGroupProduct, serviceContext);

		return commerceCatalogService.getOrAddEmptyCommerceCatalog(
			catalogExternalReferenceCode, commerceCurrency.getCode());
	}

	private static CommerceCurrency _getCommerceCurrency(
			CommerceCurrencyService commerceCurrencyService,
			ProductGroupProduct productGroupProduct,
			ServiceContext serviceContext)
		throws PortalException {

		String catalogCurrencyCode =
			productGroupProduct.getCatalogCurrencyCode();
		String catalogCurrencyExternalReferenceCode =
			productGroupProduct.getCatalogCurrencyExternalReferenceCode();

		CommerceCurrency commerceCurrency =
			CommerceCurrencyUtil.fetchCommerceCurrency(
				serviceContext.getCompanyId(), catalogCurrencyCode,
				catalogCurrencyExternalReferenceCode, 0);

		if (commerceCurrency != null) {
			return commerceCurrency;
		}

		if (Validator.isNull(catalogCurrencyExternalReferenceCode)) {
			throw new NoSuchCurrencyException(
				"Unable to find currency with external reference code " +
					catalogCurrencyExternalReferenceCode);
		}

		return commerceCurrencyService.getOrAddEmptyCommerceCurrency(
			catalogCurrencyExternalReferenceCode, catalogCurrencyCode);
	}

}