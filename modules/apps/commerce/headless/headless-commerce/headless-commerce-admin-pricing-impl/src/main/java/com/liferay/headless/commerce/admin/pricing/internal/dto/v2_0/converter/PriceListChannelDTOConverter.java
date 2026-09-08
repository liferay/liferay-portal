/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.internal.dto.v2_0.converter;

import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.model.CommercePriceListChannelRel;
import com.liferay.commerce.price.list.service.CommercePriceListChannelRelService;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.PriceListChannel;
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
		"dto.class.name=com.liferay.commerce.price.list.model.CommercePriceListChannelRel"
	},
	service = DTOConverter.class
)
public class PriceListChannelDTOConverter
	implements DTOConverter<CommercePriceListChannelRel, PriceListChannel> {

	@Override
	public String getContentType() {
		return PriceListChannel.class.getSimpleName();
	}

	@Override
	public PriceListChannel toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		CommercePriceListChannelRel commercePriceListChannelRel =
			_commercePriceListChannelRelService.getCommercePriceListChannelRel(
				(Long)dtoConverterContext.getId());

		CommerceChannel commerceChannel =
			commercePriceListChannelRel.getCommerceChannel();
		CommercePriceList commercePriceList =
			commercePriceListChannelRel.getCommercePriceList();

		return new PriceListChannel() {
			{
				setActions(dtoConverterContext::getActions);
				setChannelExternalReferenceCode(
					commerceChannel::getExternalReferenceCode);
				setChannelId(commerceChannel::getCommerceChannelId);
				setOrder(commercePriceListChannelRel::getOrder);
				setPriceListChannelId(
					commercePriceListChannelRel::
						getCommercePriceListChannelRelId);
				setPriceListExternalReferenceCode(
					commercePriceList::getExternalReferenceCode);
				setPriceListId(commercePriceList::getCommercePriceListId);
			}
		};
	}

	@Reference
	private CommercePriceListChannelRelService
		_commercePriceListChannelRelService;

}