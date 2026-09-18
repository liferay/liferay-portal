/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.internal.util.v2_0;

import com.liferay.commerce.currency.service.CommerceCurrencyService;
import com.liferay.commerce.pricing.exception.NoSuchPricingClassException;
import com.liferay.commerce.pricing.model.CommercePriceModifier;
import com.liferay.commerce.pricing.model.CommercePriceModifierRel;
import com.liferay.commerce.pricing.service.CommercePriceModifierRelService;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CPDefinitionService;
import com.liferay.commerce.product.service.CProductLocalService;
import com.liferay.commerce.product.service.CommerceCatalogService;
import com.liferay.commerce.product.type.simple.constants.SimpleCPTypeConstants;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.PriceModifierProduct;
import com.liferay.headless.commerce.admin.pricing.internal.util.CatalogUtil;
import com.liferay.headless.commerce.core.helper.ServiceContextHelper;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

/**
 * @author Riccardo Alberti
 */
public class PriceModifierProductUtil {

	public static CommercePriceModifierRel addCommercePriceModifierRel(
			CProductLocalService cProductLocalService,
			CommerceCatalogService commerceCatalogService,
			CommerceCurrencyService commerceCurrencyService,
			CommercePriceModifierRelService commercePriceModifierRelService,
			CPDefinitionService cpDefinitionService,
			PriceModifierProduct priceModifierProduct,
			CommercePriceModifier commercePriceModifier,
			ServiceContextHelper serviceContextHelper)
		throws PortalException {

		ServiceContext serviceContext =
			serviceContextHelper.getServiceContext();

		CProduct cProduct = _getCProduct(
			cProductLocalService, commerceCatalogService,
			commerceCurrencyService, cpDefinitionService, priceModifierProduct,
			serviceContext);

		CommercePriceModifierRel commercePriceModifierRel =
			commercePriceModifierRelService.fetchCommercePriceModifierRel(
				commercePriceModifier.getCommercePriceModifierId(),
				CPDefinition.class.getName(),
				cProduct.getPublishedCPDefinitionId());

		if (commercePriceModifierRel != null) {
			return commercePriceModifierRel;
		}

		return commercePriceModifierRelService.addCommercePriceModifierRel(
			commercePriceModifier.getCommercePriceModifierId(),
			CPDefinition.class.getName(), cProduct.getPublishedCPDefinitionId(),
			serviceContext);
	}

	private static CProduct _getCProduct(
			CProductLocalService cProductLocalService,
			CommerceCatalogService commerceCatalogService,
			CommerceCurrencyService commerceCurrencyService,
			CPDefinitionService cpDefinitionService,
			PriceModifierProduct priceModifierProduct,
			ServiceContext serviceContext)
		throws PortalException {

		String productExternalReferenceCode =
			priceModifierProduct.getProductExternalReferenceCode();

		if (Validator.isNull(productExternalReferenceCode)) {
			return cProductLocalService.getCProduct(
				GetterUtil.getLong(priceModifierProduct.getProductId()));
		}

		CProduct cProduct =
			cProductLocalService.fetchCProductByExternalReferenceCode(
				productExternalReferenceCode, serviceContext.getCompanyId());

		if (cProduct != null) {
			return cProduct;
		}

		long productId = GetterUtil.getLong(
			priceModifierProduct.getProductId());

		if (productId > 0) {
			cProduct = cProductLocalService.fetchCProduct(productId);

			if ((cProduct != null) &&
				(cProduct.getCompanyId() == serviceContext.getCompanyId())) {

				return cProduct;
			}
		}

		if (!LazyReferencingThreadLocal.isEnabled()) {
			throw new NoSuchPricingClassException(
				"Unable to find product with external reference code " +
					productExternalReferenceCode);
		}

		CommerceCatalog commerceCatalog = CatalogUtil.getCommerceCatalog(
			0, priceModifierProduct.getCatalogCurrencyCode(),
			priceModifierProduct.getCatalogCurrencyExternalReferenceCode(),
			priceModifierProduct.getCatalogExternalReferenceCode(),
			commerceCatalogService, commerceCurrencyService, serviceContext);

		CPDefinition cpDefinition =
			cpDefinitionService.getOrAddEmptyCPDefinition(
				productExternalReferenceCode, commerceCatalog.getGroupId(),
				GetterUtil.getString(
					priceModifierProduct.getProductType(),
					SimpleCPTypeConstants.NAME));

		return cProductLocalService.getCProduct(cpDefinition.getCProductId());
	}

}