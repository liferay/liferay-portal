/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.channel.internal.dto.v1_0.converter;

import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.model.CommerceChannelRel;
import com.liferay.commerce.product.service.CommerceChannelRelService;
import com.liferay.headless.commerce.admin.channel.dto.v1_0.AccountAddressChannel;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.service.AddressLocalService;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Danny Situ
 */
@Component(
	property = {
		"default=true",
		"dto.class.name=com.liferay.commerce.product.model.CommerceChannelRel"
	},
	service = DTOConverter.class
)
public class AccountAddressChannelDTOConverter
	implements DTOConverter<CommerceChannelRel, AccountAddressChannel> {

	@Override
	public String getContentType() {
		return AccountAddressChannel.class.getSimpleName();
	}

	@Override
	public AccountAddressChannel toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		CommerceChannelRel accountAddressChannelRel =
			_commerceChannelRelService.getCommerceChannelRel(
				(Long)dtoConverterContext.getId());

		CommerceChannel commerceChannel =
			accountAddressChannelRel.getCommerceChannel();

		Address address = _addressLocalService.getAddress(
			accountAddressChannelRel.getClassPK());

		return new AccountAddressChannel() {
			{
				setAccountAddressChannelId(
					accountAddressChannelRel::getCommerceChannelRelId);
				setActions(dtoConverterContext::getActions);
				setAddressChannelExternalReferenceCode(
					commerceChannel::getExternalReferenceCode);
				setAddressChannelId(commerceChannel::getCommerceChannelId);
				setAddressExternalReferenceCode(
					address::getExternalReferenceCode);
				setAddressId(address::getAddressId);
			}
		};
	}

	@Reference
	private AddressLocalService _addressLocalService;

	@Reference
	private CommerceChannelRelService _commerceChannelRelService;

}