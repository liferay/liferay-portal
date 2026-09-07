/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.internal.dto.v2_0.converter;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.service.AssetCategoryService;
import com.liferay.commerce.discount.model.CommerceDiscount;
import com.liferay.commerce.discount.model.CommerceDiscountRel;
import com.liferay.commerce.discount.service.CommerceDiscountRelService;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.DiscountCategory;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"default=true",
		"dto.class.name=com.liferay.commerce.discount.model.CommerceDiscountRel-Category"
	},
	service = DTOConverter.class
)
public class DiscountCategoryDTOConverter
	implements DTOConverter<CommerceDiscountRel, DiscountCategory> {

	@Override
	public String getContentType() {
		return DiscountCategory.class.getSimpleName();
	}

	@Override
	public DiscountCategory toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		CommerceDiscountRel commerceDiscountRel =
			_commerceDiscountRelService.getCommerceDiscountRel(
				(Long)dtoConverterContext.getId());

		AssetCategory assetCategory = _assetCategoryService.getCategory(
			commerceDiscountRel.getClassPK());

		CommerceDiscount commerceDiscount =
			commerceDiscountRel.getCommerceDiscount();

		return new DiscountCategory() {
			{
				setActions(dtoConverterContext::getActions);
				setCategoryExternalReferenceCode(
					assetCategory::getExternalReferenceCode);
				setCategoryId(assetCategory::getCategoryId);
				setDiscountCategoryId(
					commerceDiscountRel::getCommerceDiscountRelId);
				setDiscountExternalReferenceCode(
					commerceDiscount::getExternalReferenceCode);
				setDiscountId(commerceDiscount::getCommerceDiscountId);
			}
		};
	}

	@Reference
	private AssetCategoryService _assetCategoryService;

	@Reference
	private CommerceDiscountRelService _commerceDiscountRelService;

}