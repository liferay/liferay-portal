/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.internal.dto.v2_0.converter;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.service.AssetCategoryService;
import com.liferay.commerce.pricing.model.CommercePriceModifier;
import com.liferay.commerce.pricing.model.CommercePriceModifierRel;
import com.liferay.commerce.pricing.service.CommercePriceModifierRelService;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.PriceModifierCategory;
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
		"dto.class.name=com.liferay.commerce.pricing.model.CommercePriceModifierRel-Category"
	},
	service = DTOConverter.class
)
public class PriceModifierCategoryDTOConverter
	implements DTOConverter<CommercePriceModifierRel, PriceModifierCategory> {

	@Override
	public String getContentType() {
		return PriceModifierCategory.class.getSimpleName();
	}

	@Override
	public PriceModifierCategory toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		CommercePriceModifierRel commercePriceModifierRel =
			_commercePriceModifierRelService.getCommercePriceModifierRel(
				(Long)dtoConverterContext.getId());

		AssetCategory assetCategory = _assetCategoryService.getCategory(
			commercePriceModifierRel.getClassPK());

		CommercePriceModifier commercePriceModifier =
			commercePriceModifierRel.getCommercePriceModifier();

		return new PriceModifierCategory() {
			{
				setActions(dtoConverterContext::getActions);
				setCategoryExternalReferenceCode(
					assetCategory::getExternalReferenceCode);
				setCategoryId(assetCategory::getCategoryId);
				setPriceModifierCategoryId(
					commercePriceModifierRel::getCommercePriceModifierRelId);
				setPriceModifierExternalReferenceCode(
					commercePriceModifier::getExternalReferenceCode);
				setPriceModifierId(
					commercePriceModifier::getCommercePriceModifierId);
			}
		};
	}

	@Reference
	private AssetCategoryService _assetCategoryService;

	@Reference
	private CommercePriceModifierRelService _commercePriceModifierRelService;

}