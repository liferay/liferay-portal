/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.internal.dto.v2_0.converter;

import com.liferay.commerce.discount.model.CommerceDiscount;
import com.liferay.commerce.discount.model.CommerceDiscountOrderTypeRel;
import com.liferay.commerce.discount.service.CommerceDiscountOrderTypeRelService;
import com.liferay.commerce.model.CommerceOrderType;
import com.liferay.commerce.service.CommerceOrderTypeService;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.DiscountOrderType;
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
		"dto.class.name=com.liferay.commerce.discount.model.CommerceDiscountOrderTypeRel"
	},
	service = DTOConverter.class
)
public class DiscountOrderTypeDTOConverter
	implements DTOConverter<CommerceDiscountOrderTypeRel, DiscountOrderType> {

	@Override
	public String getContentType() {
		return DiscountOrderType.class.getSimpleName();
	}

	@Override
	public DiscountOrderType toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		CommerceDiscountOrderTypeRel commerceDiscountOrderTypeRel =
			_commerceDiscountOrderTypeRelService.
				getCommerceDiscountOrderTypeRel(
					(Long)dtoConverterContext.getId());

		CommerceOrderType commerceOrderType =
			_commerceOrderTypeService.getCommerceOrderType(
				commerceDiscountOrderTypeRel.getCommerceOrderTypeId());
		CommerceDiscount commerceDiscount =
			commerceDiscountOrderTypeRel.getCommerceDiscount();

		return new DiscountOrderType() {
			{
				setActions(dtoConverterContext::getActions);
				setDiscountExternalReferenceCode(
					commerceDiscount::getExternalReferenceCode);
				setDiscountId(commerceDiscount::getCommerceDiscountId);
				setDiscountOrderTypeId(
					commerceDiscountOrderTypeRel::
						getCommerceDiscountOrderTypeRelId);
				setOrderTypeExternalReferenceCode(
					commerceOrderType::getExternalReferenceCode);
				setOrderTypeId(commerceOrderType::getCommerceOrderTypeId);
				setPriority(commerceDiscountOrderTypeRel::getPriority);
			}
		};
	}

	@Reference
	private CommerceDiscountOrderTypeRelService
		_commerceDiscountOrderTypeRelService;

	@Reference
	private CommerceOrderTypeService _commerceOrderTypeService;

}