/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.internal.util.v2_0;

import com.liferay.commerce.currency.service.CommerceCurrencyService;
import com.liferay.commerce.discount.model.CommerceDiscount;
import com.liferay.commerce.discount.model.CommerceDiscountRel;
import com.liferay.commerce.discount.service.CommerceDiscountRelService;
import com.liferay.commerce.product.exception.NoSuchCPInstanceException;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CPDefinitionService;
import com.liferay.commerce.product.service.CPInstanceService;
import com.liferay.commerce.product.service.CPInstanceUnitOfMeasureLocalService;
import com.liferay.commerce.product.service.CommerceCatalogService;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.DiscountSku;
import com.liferay.headless.commerce.admin.pricing.internal.util.CatalogUtil;
import com.liferay.headless.commerce.admin.pricing.internal.util.SkuUtil;
import com.liferay.headless.commerce.core.helper.ServiceContextHelper;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.Validator;

/**
 * @author Alessio Antonio Rendina
 */
public class DiscountSkuUtil {

	public static CommerceDiscountRel addCommerceDiscountRel(
			CommerceCatalogService commerceCatalogService,
			CommerceCurrencyService commerceCurrencyService,
			CommerceDiscount commerceDiscount,
			CommerceDiscountRelService commerceDiscountRelService,
			CPDefinitionService cpDefinitionService,
			CPInstanceService cpInstanceService,
			CPInstanceUnitOfMeasureLocalService
				cpInstanceUnitOfMeasureLocalService,
			DiscountSku discountSku, ServiceContextHelper serviceContextHelper)
		throws PortalException {

		ServiceContext serviceContext =
			serviceContextHelper.getServiceContext();

		CPInstance cpInstance = _getCPInstance(
			commerceCatalogService, commerceCurrencyService,
			cpDefinitionService, cpInstanceService, discountSku,
			serviceContext);

		UnicodeProperties typeSettingsUnicodeProperties = null;
		String unitOfMeasureKey = discountSku.getUnitOfMeasureKey();

		if (unitOfMeasureKey != null) {
			cpInstanceUnitOfMeasureLocalService.getCPInstanceUnitOfMeasure(
				cpInstance.getCPInstanceId(), unitOfMeasureKey);

			typeSettingsUnicodeProperties = UnicodePropertiesBuilder.create(
				HashMapBuilder.put(
					"unitOfMeasureKey", unitOfMeasureKey
				).build(),
				true
			).build();
		}

		CommerceDiscountRel commerceDiscountRel =
			commerceDiscountRelService.fetchCommerceDiscountRel(
				commerceDiscount.getCommerceDiscountId(),
				CPInstance.class.getName(), cpInstance.getCPInstanceId());

		if (commerceDiscountRel != null) {
			return commerceDiscountRelService.updateTypeSettings(
				commerceDiscountRel.getCommerceDiscountRelId(),
				typeSettingsUnicodeProperties);
		}

		return commerceDiscountRelService.addCommerceDiscountRel(
			commerceDiscount.getCommerceDiscountId(),
			CPInstance.class.getName(), cpInstance.getCPInstanceId(),
			typeSettingsUnicodeProperties, serviceContext);
	}

	private static CPInstance _getCPInstance(
			CommerceCatalogService commerceCatalogService,
			CommerceCurrencyService commerceCurrencyService,
			CPDefinitionService cpDefinitionService,
			CPInstanceService cpInstanceService, DiscountSku discountSku,
			ServiceContext serviceContext)
		throws PortalException {

		String skuExternalReferenceCode =
			discountSku.getSkuExternalReferenceCode();

		if (Validator.isNull(skuExternalReferenceCode)) {
			return cpInstanceService.getCPInstance(
				GetterUtil.getLong(discountSku.getSkuId()));
		}

		long groupId = 0;

		if (LazyReferencingThreadLocal.isEnabled()) {
			CommerceCatalog commerceCatalog = CatalogUtil.getCommerceCatalog(
				0, discountSku.getCatalogCurrencyCode(),
				discountSku.getCatalogCurrencyExternalReferenceCode(),
				discountSku.getCatalogExternalReferenceCode(),
				commerceCatalogService, commerceCurrencyService,
				serviceContext);

			groupId = commerceCatalog.getGroupId();
		}

		CPInstance cpInstance = SkuUtil.fetchCPInstance(
			cpDefinitionService, cpInstanceService, groupId,
			discountSku.getProductExternalReferenceCode(),
			discountSku.getProductType(), serviceContext,
			skuExternalReferenceCode,
			GetterUtil.getLong(discountSku.getSkuId()));

		if (cpInstance == null) {
			throw new NoSuchCPInstanceException(
				"Unable to find SKU with external reference code " +
					skuExternalReferenceCode);
		}

		return cpInstance;
	}

}