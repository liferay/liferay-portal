/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.internal.dto.v2_0.converter;

import com.liferay.commerce.model.CommerceOrderType;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.model.CommercePriceListOrderTypeRel;
import com.liferay.commerce.price.list.service.CommercePriceListOrderTypeRelService;
import com.liferay.commerce.service.CommerceOrderTypeService;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.PriceListOrderType;
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
		"dto.class.name=com.liferay.commerce.price.list.model.CommercePriceListOrderTypeRel"
	},
	service = DTOConverter.class
)
public class PriceListOrderTypeDTOConverter
	implements DTOConverter<CommercePriceListOrderTypeRel, PriceListOrderType> {

	@Override
	public String getContentType() {
		return PriceListOrderType.class.getSimpleName();
	}

	@Override
	public PriceListOrderType toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		CommercePriceListOrderTypeRel commercePriceListOrderTypeRel =
			_commercePriceListOrderTypeRelService.
				getCommercePriceListOrderTypeRel(
					(Long)dtoConverterContext.getId());

		CommercePriceList commercePriceList =
			commercePriceListOrderTypeRel.getCommercePriceList();

		return new PriceListOrderType() {
			{
				setActions(dtoConverterContext::getActions);
				setOrderTypeExternalReferenceCode(
					() -> {
						CommerceOrderType commerceOrderType =
							_commerceOrderTypeService.getCommerceOrderType(
								commercePriceListOrderTypeRel.
									getCommerceOrderTypeId());

						return commerceOrderType.getExternalReferenceCode();
					});
				setOrderTypeId(
					commercePriceListOrderTypeRel::getCommerceOrderTypeId);
				setPriceListExternalReferenceCode(
					commercePriceList::getExternalReferenceCode);
				setPriceListId(commercePriceList::getCommercePriceListId);
				setPriceListOrderTypeId(
					commercePriceListOrderTypeRel::
						getCommercePriceListOrderTypeRelId);
				setPriority(commercePriceListOrderTypeRel::getPriority);
			}
		};
	}

	@Reference
	private CommerceOrderTypeService _commerceOrderTypeService;

	@Reference
	private CommercePriceListOrderTypeRelService
		_commercePriceListOrderTypeRelService;

}