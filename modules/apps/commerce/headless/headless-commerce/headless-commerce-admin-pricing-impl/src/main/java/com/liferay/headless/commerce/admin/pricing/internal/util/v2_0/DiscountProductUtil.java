/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.internal.util.v2_0;

import com.liferay.commerce.currency.service.CommerceCurrencyService;
import com.liferay.commerce.discount.model.CommerceDiscount;
import com.liferay.commerce.discount.model.CommerceDiscountRel;
import com.liferay.commerce.discount.service.CommerceDiscountRelService;
import com.liferay.commerce.product.exception.NoSuchCProductException;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CPDefinitionService;
import com.liferay.commerce.product.service.CProductLocalService;
import com.liferay.commerce.product.service.CommerceCatalogService;
import com.liferay.commerce.product.type.simple.constants.SimpleCPTypeConstants;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.DiscountProduct;
import com.liferay.headless.commerce.admin.pricing.internal.util.CatalogUtil;
import com.liferay.headless.commerce.core.helper.ServiceContextHelper;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

/**
 * @author Alessio Antonio Rendina
 */
public class DiscountProductUtil {

	public static CommerceDiscountRel addCommerceDiscountRel(
			CProductLocalService cProductLocalService,
			CommerceCatalogService commerceCatalogService,
			CommerceCurrencyService commerceCurrencyService,
			CommerceDiscountRelService commerceDiscountRelService,
			CPDefinitionService cpDefinitionService,
			DiscountProduct discountProduct, CommerceDiscount commerceDiscount,
			ServiceContextHelper serviceContextHelper)
		throws PortalException {

		ServiceContext serviceContext =
			serviceContextHelper.getServiceContext();

		CProduct cProduct = _getCProduct(
			cProductLocalService, commerceCatalogService,
			commerceCurrencyService, cpDefinitionService, discountProduct,
			serviceContext);

		CommerceDiscountRel commerceDiscountRel =
			commerceDiscountRelService.fetchCommerceDiscountRel(
				commerceDiscount.getCommerceDiscountId(),
				CPDefinition.class.getName(),
				cProduct.getPublishedCPDefinitionId());

		if (commerceDiscountRel != null) {
			return commerceDiscountRel;
		}

		return commerceDiscountRelService.addCommerceDiscountRel(
			commerceDiscount.getCommerceDiscountId(),
			CPDefinition.class.getName(), cProduct.getPublishedCPDefinitionId(),
			null, serviceContext);
	}

	private static CProduct _getCProduct(
			CProductLocalService cProductLocalService,
			CommerceCatalogService commerceCatalogService,
			CommerceCurrencyService commerceCurrencyService,
			CPDefinitionService cpDefinitionService,
			DiscountProduct discountProduct, ServiceContext serviceContext)
		throws PortalException {

		String productExternalReferenceCode =
			discountProduct.getProductExternalReferenceCode();

		if (Validator.isNull(productExternalReferenceCode)) {
			return cProductLocalService.getCProduct(
				GetterUtil.getLong(discountProduct.getProductId()));
		}

		CProduct cProduct =
			cProductLocalService.fetchCProductByExternalReferenceCode(
				productExternalReferenceCode, serviceContext.getCompanyId());

		if (cProduct != null) {
			return cProduct;
		}

		long productId = GetterUtil.getLong(discountProduct.getProductId());

		if (productId > 0) {
			cProduct = cProductLocalService.fetchCProduct(productId);

			if ((cProduct != null) &&
				(cProduct.getCompanyId() == serviceContext.getCompanyId())) {

				return cProduct;
			}
		}

		if (!LazyReferencingThreadLocal.isEnabled()) {
			throw new NoSuchCProductException(
				"Unable to find product with external reference code " +
					productExternalReferenceCode);
		}

		CommerceCatalog commerceCatalog = CatalogUtil.getCommerceCatalog(
			0, discountProduct.getCatalogCurrencyCode(),
			discountProduct.getCatalogCurrencyExternalReferenceCode(),
			discountProduct.getCatalogExternalReferenceCode(),
			commerceCatalogService, commerceCurrencyService, serviceContext);

		CPDefinition cpDefinition =
			cpDefinitionService.getOrAddEmptyCPDefinition(
				productExternalReferenceCode, commerceCatalog.getGroupId(),
				GetterUtil.getString(
					discountProduct.getProductType(),
					SimpleCPTypeConstants.NAME));

		return cProductLocalService.getCProduct(cpDefinition.getCProductId());
	}

}