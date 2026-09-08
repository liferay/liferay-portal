/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.internal.dto.v2_0.converter;

import com.liferay.commerce.discount.model.CommerceDiscount;
import com.liferay.commerce.discount.model.CommerceDiscountRel;
import com.liferay.commerce.discount.service.CommerceDiscountRelService;
import com.liferay.commerce.pricing.model.CommercePricingClass;
import com.liferay.commerce.pricing.service.CommercePricingClassService;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.DiscountProductGroup;
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
		"dto.class.name=com.liferay.commerce.discount.model.CommerceDiscountRel-ProductGroup"
	},
	service = DTOConverter.class
)
public class DiscountProductGroupDTOConverter
	implements DTOConverter<CommerceDiscountRel, DiscountProductGroup> {

	@Override
	public String getContentType() {
		return DiscountProductGroup.class.getSimpleName();
	}

	@Override
	public DiscountProductGroup toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		CommerceDiscountRel commerceDiscountRel =
			_commerceDiscountRelService.getCommerceDiscountRel(
				(Long)dtoConverterContext.getId());

		CommercePricingClass commercePricingClass =
			_commercePricingClassService.getCommercePricingClass(
				commerceDiscountRel.getClassPK());

		CommerceDiscount commerceDiscount =
			commerceDiscountRel.getCommerceDiscount();

		return new DiscountProductGroup() {
			{
				setActions(dtoConverterContext::getActions);
				setDiscountExternalReferenceCode(
					commerceDiscount::getExternalReferenceCode);
				setDiscountId(commerceDiscount::getCommerceDiscountId);
				setDiscountProductGroupId(
					commerceDiscountRel::getCommerceDiscountRelId);
				setProductGroupExternalReferenceCode(
					commercePricingClass::getExternalReferenceCode);
				setProductGroupId(
					commercePricingClass::getCommercePricingClassId);
			}
		};
	}

	@Reference
	private CommerceDiscountRelService _commerceDiscountRelService;

	@Reference
	private CommercePricingClassService _commercePricingClassService;

}