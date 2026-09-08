/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.order.internal.dto.v1_0.converter;

import com.liferay.commerce.model.CommerceOrderType;
import com.liferay.commerce.model.CommerceOrderTypeRel;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelService;
import com.liferay.commerce.service.CommerceOrderTypeRelService;
import com.liferay.commerce.service.CommerceOrderTypeService;
import com.liferay.headless.commerce.admin.order.dto.v1_0.OrderTypeChannel;
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
		"dto.class.name=com.liferay.commerce.model.CommerceOrderTypeRel"
	},
	service = DTOConverter.class
)
public class OrderTypeChannelDTOConverter
	implements DTOConverter<CommerceOrderTypeRel, OrderTypeChannel> {

	@Override
	public String getContentType() {
		return OrderTypeChannel.class.getSimpleName();
	}

	@Override
	public OrderTypeChannel toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		CommerceOrderTypeRel commerceOrderTypeRel =
			_commerceOrderTypeRelService.getCommerceOrderTypeRel(
				(Long)dtoConverterContext.getId());

		CommerceOrderType commerceOrderType =
			_commerceOrderTypeService.fetchCommerceOrderType(
				commerceOrderTypeRel.getCommerceOrderTypeId());

		CommerceChannel commerceChannel =
			_commerceChannelService.getCommerceChannel(
				commerceOrderTypeRel.getClassPK());

		return new OrderTypeChannel() {
			{
				setActions(dtoConverterContext::getActions);
				setChannelExternalReferenceCode(
					commerceChannel::getExternalReferenceCode);
				setChannelId(commerceChannel::getCommerceChannelId);
				setOrderTypeChannelId(
					commerceOrderTypeRel::getCommerceOrderTypeRelId);
				setOrderTypeExternalReferenceCode(
					commerceOrderType::getExternalReferenceCode);
				setOrderTypeId(commerceOrderType::getCommerceOrderTypeId);
			}
		};
	}

	@Reference
	private CommerceChannelService _commerceChannelService;

	@Reference
	private CommerceOrderTypeRelService _commerceOrderTypeRelService;

	@Reference
	private CommerceOrderTypeService _commerceOrderTypeService;

}