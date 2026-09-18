/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.internal.util.v2_0;

import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetCategoryService;
import com.liferay.commerce.currency.service.CommerceCurrencyService;
import com.liferay.commerce.pricing.model.CommercePriceModifier;
import com.liferay.commerce.pricing.service.CommercePriceModifierRelService;
import com.liferay.commerce.pricing.service.CommercePricingClassService;
import com.liferay.commerce.product.service.CPDefinitionService;
import com.liferay.commerce.product.service.CProductLocalService;
import com.liferay.commerce.product.service.CommerceCatalogService;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.PriceModifier;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.PriceModifierCategory;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.PriceModifierProduct;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.PriceModifierProductGroup;
import com.liferay.headless.commerce.core.helper.ServiceContextHelper;
import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author Riccardo Alberti
 */
public class PriceModifierUtil {

	public static void addOrUpdateCommercePriceModifierRels(
			long groupId, AssetCategoryLocalService assetCategoryLocalService,
			AssetCategoryService assetCategoryService,
			CProductLocalService cProductLocalService,
			CommerceCatalogService commerceCatalogService,
			CommerceCurrencyService commerceCurrencyService,
			CommercePriceModifierRelService commercePriceModifierRelService,
			CommercePricingClassService commercePricingClassService,
			CPDefinitionService cpDefinitionService,
			PriceModifier priceModifier,
			CommercePriceModifier commercePriceModifier,
			ServiceContextHelper serviceContextHelper)
		throws PortalException {

		PriceModifierCategory[] priceModifierCategories =
			priceModifier.getPriceModifierCategories();

		if (priceModifierCategories != null) {
			for (PriceModifierCategory priceModifierCategory :
					priceModifierCategories) {

				PriceModifierCategoryUtil.addCommercePriceModifierRel(
					groupId, assetCategoryLocalService, assetCategoryService,
					commercePriceModifierRelService, priceModifierCategory,
					commercePriceModifier, serviceContextHelper);
			}
		}

		PriceModifierProductGroup[] priceModifierProductGroups =
			priceModifier.getPriceModifierProductGroups();

		if (priceModifierProductGroups != null) {
			for (PriceModifierProductGroup priceModifierProductGroup :
					priceModifierProductGroups) {

				PriceModifierProductGroupUtil.addCommercePriceModifierRel(
					commercePricingClassService,
					commercePriceModifierRelService, priceModifierProductGroup,
					commercePriceModifier, serviceContextHelper);
			}
		}

		PriceModifierProduct[] priceModifierProducts =
			priceModifier.getPriceModifierProducts();

		if (priceModifierProducts != null) {
			for (PriceModifierProduct priceModifierProduct :
					priceModifierProducts) {

				PriceModifierProductUtil.addCommercePriceModifierRel(
					cProductLocalService, commerceCatalogService,
					commerceCurrencyService, commercePriceModifierRelService,
					cpDefinitionService, priceModifierProduct,
					commercePriceModifier, serviceContextHelper);
			}
		}
	}

}