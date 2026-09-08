/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.internal.dto.v2_0.converter;

import com.liferay.commerce.discount.model.CommerceDiscount;
import com.liferay.commerce.discount.service.CommerceDiscountService;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.model.CommerceChannelRel;
import com.liferay.commerce.product.service.CommerceChannelRelService;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.DiscountChannel;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Riccardo Alberti
 */
@Component(
	property = {
		"default=true",
		"dto.class.name=com.liferay.commerce.discount.model.CommerceChannelRel"
	},
	service = DTOConverter.class
)
public class DiscountChannelDTOConverter
	implements DTOConverter<CommerceChannelRel, DiscountChannel> {

	@Override
	public String getContentType() {
		return DiscountChannel.class.getSimpleName();
	}

	@Override
	public DiscountChannel toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		CommerceChannelRel commerceDiscountChannelRel =
			_commerceChannelRelService.getCommerceChannelRel(
				(Long)dtoConverterContext.getId());

		CommerceChannel commerceChannel =
			commerceDiscountChannelRel.getCommerceChannel();

		CommerceDiscount commerceDiscount =
			_commerceDiscountService.getCommerceDiscount(
				commerceDiscountChannelRel.getClassPK());

		return new DiscountChannel() {
			{
				setActions(dtoConverterContext::getActions);
				setChannelExternalReferenceCode(
					commerceChannel::getExternalReferenceCode);
				setChannelId(commerceChannel::getCommerceChannelId);
				setDiscountChannelId(
					commerceDiscountChannelRel::getCommerceChannelRelId);
				setDiscountExternalReferenceCode(
					commerceDiscount::getExternalReferenceCode);
				setDiscountId(commerceDiscount::getCommerceDiscountId);
			}
		};
	}

	@Reference
	private CommerceChannelRelService _commerceChannelRelService;

	@Reference
	private CommerceDiscountService _commerceDiscountService;

}