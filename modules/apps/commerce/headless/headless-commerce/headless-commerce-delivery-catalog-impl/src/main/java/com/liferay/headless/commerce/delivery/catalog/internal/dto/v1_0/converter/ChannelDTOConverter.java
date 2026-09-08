/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.catalog.internal.dto.v1_0.converter;

import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelService;
import com.liferay.headless.commerce.delivery.catalog.dto.v1_0.Channel;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Crescenzo Rega
 */
@Component(
	property = {
		"default=true",
		"dto.class.name=com.liferay.headless.commerce.delivery.catalog.dto.v1_0.Channel"
	},
	service = DTOConverter.class
)
public class ChannelDTOConverter
	implements DTOConverter<CommerceChannel, Channel> {

	@Override
	public String getContentType() {
		return Channel.class.getSimpleName();
	}

	@Override
	public Channel toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		CommerceChannel commerceChannel =
			_commerceChannelService.getCommerceChannel(
				(Long)dtoConverterContext.getId());

		return new Channel() {
			{
				setCurrencyCode(commerceChannel::getCommerceCurrencyCode);
				setExternalReferenceCode(
					commerceChannel::getExternalReferenceCode);
				setId(commerceChannel::getCommerceChannelId);
				setName(commerceChannel::getName);
				setSiteGroupId(commerceChannel::getSiteGroupId);
				setType(commerceChannel::getType);
			}
		};
	}

	@Reference
	private CommerceChannelService _commerceChannelService;

}