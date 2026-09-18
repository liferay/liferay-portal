/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.internal.util.v2_0;

import com.liferay.asset.kernel.exception.NoSuchCategoryException;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetCategoryService;
import com.liferay.commerce.discount.model.CommerceDiscount;
import com.liferay.commerce.discount.model.CommerceDiscountRel;
import com.liferay.commerce.discount.service.CommerceDiscountRelService;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.DiscountCategory;
import com.liferay.headless.commerce.core.helper.ServiceContextHelper;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

/**
 * @author Riccardo Alberti
 */
public class DiscountCategoryUtil {

	public static CommerceDiscountRel addCommerceDiscountRel(
			long groupId, AssetCategoryLocalService assetCategoryLocalService,
			AssetCategoryService assetCategoryService,
			CommerceDiscountRelService commerceDiscountRelService,
			DiscountCategory discountCategory,
			CommerceDiscount commerceDiscount,
			ServiceContextHelper serviceContextHelper)
		throws PortalException {

		ServiceContext serviceContext =
			serviceContextHelper.getServiceContext();

		AssetCategory assetCategory = _getAssetCategory(
			groupId, assetCategoryLocalService, assetCategoryService,
			discountCategory, serviceContext);

		CommerceDiscountRel commerceDiscountRel =
			commerceDiscountRelService.fetchCommerceDiscountRel(
				commerceDiscount.getCommerceDiscountId(),
				AssetCategory.class.getName(), assetCategory.getCategoryId());

		if (commerceDiscountRel != null) {
			return commerceDiscountRel;
		}

		return commerceDiscountRelService.addCommerceDiscountRel(
			commerceDiscount.getCommerceDiscountId(),
			AssetCategory.class.getName(), assetCategory.getCategoryId(), null,
			serviceContext);
	}

	private static AssetCategory _getAssetCategory(
			long groupId, AssetCategoryLocalService assetCategoryLocalService,
			AssetCategoryService assetCategoryService,
			DiscountCategory discountCategory, ServiceContext serviceContext)
		throws PortalException {

		String categoryExternalReferenceCode =
			discountCategory.getCategoryExternalReferenceCode();

		if (Validator.isNull(categoryExternalReferenceCode)) {
			return assetCategoryLocalService.getCategory(
				GetterUtil.getLong(discountCategory.getCategoryId()));
		}

		AssetCategory assetCategory =
			assetCategoryLocalService.fetchAssetCategoryByExternalReferenceCode(
				categoryExternalReferenceCode, groupId);

		if (assetCategory != null) {
			return assetCategory;
		}

		long categoryId = GetterUtil.getLong(discountCategory.getCategoryId());

		if (categoryId > 0) {
			assetCategory = assetCategoryLocalService.fetchAssetCategory(
				categoryId);

			if ((assetCategory != null) &&
				(assetCategory.getCompanyId() ==
					serviceContext.getCompanyId())) {

				return assetCategory;
			}
		}

		if (!LazyReferencingThreadLocal.isEnabled()) {
			throw new NoSuchCategoryException(
				"Unable to find category with external reference code " +
					categoryExternalReferenceCode);
		}

		return assetCategoryService.getOrAddEmptyCategory(
			categoryExternalReferenceCode, groupId);
	}

}