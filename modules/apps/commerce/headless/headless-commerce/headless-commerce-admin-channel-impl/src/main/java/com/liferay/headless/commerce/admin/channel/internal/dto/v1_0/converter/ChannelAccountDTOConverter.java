/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.channel.internal.dto.v1_0.converter;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.model.CommerceChannelAccountEntryRel;
import com.liferay.commerce.product.service.CommerceChannelAccountEntryRelService;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.headless.commerce.admin.channel.dto.v1_0.ChannelAccount;
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
		"dto.class.name=com.liferay.commerce.product.model.CommerceChannelAccountEntryRel"
	},
	service = DTOConverter.class
)
public class ChannelAccountDTOConverter
	implements DTOConverter<CommerceChannelAccountEntryRel, ChannelAccount> {

	@Override
	public String getContentType() {
		return ChannelAccount.class.getSimpleName();
	}

	@Override
	public ChannelAccount toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		CommerceChannelAccountEntryRel commerceChannelAccountEntryRel =
			_commerceChannelAccountEntryRelService.
				getCommerceChannelAccountEntryRel(
					(Long)dtoConverterContext.getId());

		AccountEntry accountEntry = _accountEntryLocalService.getAccountEntry(
			commerceChannelAccountEntryRel.getAccountEntryId());
		CommerceChannel commerceChannel =
			_commerceChannelLocalService.getCommerceChannel(
				commerceChannelAccountEntryRel.getCommerceChannelId());

		return new ChannelAccount() {
			{
				setAccountExternalReferenceCode(
					accountEntry::getExternalReferenceCode);
				setAccountId(accountEntry::getAccountEntryId);
				setActions(dtoConverterContext::getActions);
				setChannelAccountId(
					commerceChannelAccountEntryRel::
						getCommerceChannelAccountEntryRelId);
				setChannelExternalReferenceCode(
					commerceChannel::getExternalReferenceCode);
				setChannelId(commerceChannel::getCommerceChannelId);
			}
		};
	}

	@Reference
	private AccountEntryLocalService _accountEntryLocalService;

	@Reference
	private CommerceChannelAccountEntryRelService
		_commerceChannelAccountEntryRelService;

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

}