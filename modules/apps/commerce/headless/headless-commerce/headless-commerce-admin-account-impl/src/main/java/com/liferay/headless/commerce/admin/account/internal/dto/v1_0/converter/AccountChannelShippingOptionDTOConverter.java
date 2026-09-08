/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.account.internal.dto.v1_0.converter;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.commerce.model.CommerceShippingMethod;
import com.liferay.commerce.model.CommerceShippingOptionAccountEntryRel;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.service.CommerceShippingMethodLocalService;
import com.liferay.commerce.shipping.engine.fixed.model.CommerceShippingFixedOption;
import com.liferay.commerce.shipping.engine.fixed.service.CommerceShippingFixedOptionLocalService;
import com.liferay.headless.commerce.admin.account.dto.v1_0.AccountChannelShippingOption;
import com.liferay.portal.kernel.util.Validator;
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
		"dto.class.name=com.liferay.commerce.model.CommerceShippingOptionAccountEntryRel"
	},
	service = DTOConverter.class
)
public class AccountChannelShippingOptionDTOConverter
	implements DTOConverter
		<CommerceShippingOptionAccountEntryRel, AccountChannelShippingOption> {

	@Override
	public String getContentType() {
		return AccountChannelShippingOption.class.getSimpleName();
	}

	@Override
	public AccountChannelShippingOption toDTO(
			DTOConverterContext dtoConverterContext,
			CommerceShippingOptionAccountEntryRel
				commerceShippingOptionAccountEntryRel)
		throws Exception {

		return new AccountChannelShippingOption() {
			{
				setAccountExternalReferenceCode(
					() -> {
						AccountEntry accountEntry =
							_accountEntryLocalService.fetchAccountEntry(
								commerceShippingOptionAccountEntryRel.
									getAccountEntryId());

						if ((accountEntry != null) &&
							!Validator.isBlank(
								accountEntry.getExternalReferenceCode())) {

							return accountEntry.getExternalReferenceCode();
						}

						return null;
					});
				setAccountId(
					() ->
						commerceShippingOptionAccountEntryRel.
							getAccountEntryId());
				setActions(dtoConverterContext::getActions);
				setChannelId(
					() ->
						commerceShippingOptionAccountEntryRel.
							getCommerceChannelId());
				setId(
					() ->
						commerceShippingOptionAccountEntryRel.
							getCommerceShippingOptionAccountEntryRelId());
				setShippingMethodId(
					() -> {
						CommerceChannel commerceChannel =
							_commerceChannelLocalService.getCommerceChannel(
								commerceShippingOptionAccountEntryRel.
									getCommerceChannelId());

						CommerceShippingMethod commerceShippingMethod =
							_commerceShippingMethodLocalService.
								fetchCommerceShippingMethod(
									commerceChannel.getGroupId(),
									commerceShippingOptionAccountEntryRel.
										getCommerceShippingMethodKey());

						if (commerceShippingMethod == null) {
							return 0L;
						}

						return commerceShippingMethod.
							getCommerceShippingMethodId();
					});
				setShippingMethodKey(
					() ->
						commerceShippingOptionAccountEntryRel.
							getCommerceShippingMethodKey());
				setShippingOptionId(
					() -> {
						CommerceShippingFixedOption
							commerceShippingFixedOption =
								_commerceShippingFixedOptionLocalService.
									fetchCommerceShippingFixedOption(
										commerceShippingOptionAccountEntryRel.
											getCompanyId(),
										commerceShippingOptionAccountEntryRel.
											getCommerceShippingOptionKey());

						if (commerceShippingFixedOption == null) {
							return 0L;
						}

						return commerceShippingFixedOption.
							getCommerceShippingFixedOptionId();
					});
				setShippingOptionKey(
					() ->
						commerceShippingOptionAccountEntryRel.
							getCommerceShippingOptionKey());
			}
		};
	}

	@Reference
	private AccountEntryLocalService _accountEntryLocalService;

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private CommerceShippingFixedOptionLocalService
		_commerceShippingFixedOptionLocalService;

	@Reference
	private CommerceShippingMethodLocalService
		_commerceShippingMethodLocalService;

}