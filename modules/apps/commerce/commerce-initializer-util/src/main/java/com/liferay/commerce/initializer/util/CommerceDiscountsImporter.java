/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.initializer.util;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.commerce.discount.constants.CommerceDiscountConstants;
import com.liferay.commerce.discount.model.CommerceDiscount;
import com.liferay.commerce.discount.service.CommerceDiscountLocalService;
import com.liferay.commerce.discount.service.CommerceDiscountRelLocalService;
import com.liferay.portal.kernel.dao.orm.Criterion;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;

import java.math.BigDecimal;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Steven Smith
 */
@Component(service = CommerceDiscountsImporter.class)
public class CommerceDiscountsImporter {

	public void importCommerceDiscounts(
			JSONArray jsonArray, long scopeGroupId, long userId)
		throws Exception {

		User user = _userLocalService.getUser(userId);

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(user.getCompanyId());
		serviceContext.setScopeGroupId(scopeGroupId);
		serviceContext.setUserId(userId);

		for (int i = 0; i < jsonArray.length(); i++) {
			_importCommerceDiscount(jsonArray.getJSONObject(i), serviceContext);
		}
	}

	private CommerceDiscount _addCommerceDiscount(
			JSONObject jsonObject, ServiceContext serviceContext)
		throws Exception {

		// Add Commerce Discount

		String title = jsonObject.getString("title");

		boolean useCouponCode = jsonObject.getBoolean("useCouponCode");
		String couponCode = jsonObject.getString("couponCode");
		boolean usePercentage = jsonObject.getBoolean("usePercentage");
		BigDecimal maximumDiscountAmount = BigDecimal.valueOf(
			jsonObject.getDouble("maximumDiscountAmount"));
		String levelType = jsonObject.getString("level");
		BigDecimal level1 = BigDecimal.valueOf(jsonObject.getDouble("level1"));

		boolean active = jsonObject.getBoolean("active");

		return _commerceDiscountLocalService.addCommerceDiscount(
			serviceContext.getUserId(), title,
			CommerceDiscountConstants.TARGET_CATEGORIES, useCouponCode,
			couponCode, usePercentage, maximumDiscountAmount, levelType, level1,
			BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
			CommerceDiscountConstants.LIMITATION_TYPE_UNLIMITED, 0, false,
			active, 1, 1, 2019, -1, -1, 0, 0, 0, 0, 0, true, serviceContext);
	}

	private void _importCommerceDiscount(
			JSONObject jsonObject, ServiceContext serviceContext)
		throws Exception {

		CommerceDiscount commerceDiscount = _addCommerceDiscount(
			jsonObject, serviceContext);

		JSONArray categoriesJSONArray = jsonObject.getJSONArray("categories");

		if (categoriesJSONArray.length() > 0) {
			DynamicQuery dynamicQuery =
				_assetCategoryLocalService.dynamicQuery();

			Criterion criterion = RestrictionsFactoryUtil.eq(
				"companyId", serviceContext.getCompanyId());

			List<AssetCategory> assetCategories =
				_assetCategoryLocalService.dynamicQuery(
					dynamicQuery.add(criterion));

			for (int i = 0; i < categoriesJSONArray.length(); i++) {
				String category = categoriesJSONArray.getString(i);

				for (AssetCategory assetCategory : assetCategories) {
					String name = assetCategory.getName();

					if (!name.contains(category)) {
						continue;
					}

					_commerceDiscountRelLocalService.addCommerceDiscountRel(
						commerceDiscount.getCommerceDiscountId(),
						AssetCategory.class.getName(),
						assetCategory.getPrimaryKey(), null, serviceContext);
				}
			}
		}
	}

	@Reference
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Reference
	private CommerceDiscountLocalService _commerceDiscountLocalService;

	@Reference
	private CommerceDiscountRelLocalService _commerceDiscountRelLocalService;

	@Reference
	private UserLocalService _userLocalService;

}