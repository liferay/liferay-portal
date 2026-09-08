/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.internal.dto.v2_0.converter;

import com.liferay.commerce.discount.model.CommerceDiscount;
import com.liferay.commerce.discount.service.CommerceDiscountService;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.model.CommercePriceListDiscountRel;
import com.liferay.commerce.price.list.service.CommercePriceListDiscountRelService;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.PriceListDiscount;
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
		"dto.class.name=com.liferay.commerce.price.list.model.CommercePriceListDiscountRel"
	},
	service = DTOConverter.class
)
public class PriceListDiscountDTOConverter
	implements DTOConverter<CommercePriceListDiscountRel, PriceListDiscount> {

	@Override
	public String getContentType() {
		return PriceListDiscount.class.getSimpleName();
	}

	@Override
	public PriceListDiscount toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		CommercePriceListDiscountRel commercePriceListDiscountRel =
			_commercePriceListDiscountRelService.
				getCommercePriceListDiscountRel(
					(Long)dtoConverterContext.getId());

		CommerceDiscount commerceDiscount =
			_commerceDiscountService.getCommerceDiscount(
				commercePriceListDiscountRel.getCommerceDiscountId());
		CommercePriceList commercePriceList =
			commercePriceListDiscountRel.getCommercePriceList();

		return new PriceListDiscount() {
			{
				setDiscountExternalReferenceCode(
					commerceDiscount::getExternalReferenceCode);
				setDiscountId(commerceDiscount::getCommerceDiscountId);
				setDiscountName(commerceDiscount::getTitle);
				setOrder(commercePriceListDiscountRel::getOrder);
				setPriceListDiscountId(
					commercePriceListDiscountRel::
						getCommercePriceListDiscountRelId);
				setPriceListExternalReferenceCode(
					commercePriceList::getExternalReferenceCode);
				setPriceListId(commercePriceList::getCommercePriceListId);
			}
		};
	}

	@Reference
	private CommerceDiscountService _commerceDiscountService;

	@Reference
	private CommercePriceListDiscountRelService
		_commercePriceListDiscountRelService;

}