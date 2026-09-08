/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.account.internal.dto.v1_0.converter;

import com.liferay.account.exception.NoSuchGroupAccountEntryRelException;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryService;
import com.liferay.commerce.discount.model.CommerceDiscount;
import com.liferay.commerce.discount.service.CommerceDiscountService;
import com.liferay.commerce.model.CommerceAddress;
import com.liferay.commerce.payment.model.CommercePaymentMethodGroupRel;
import com.liferay.commerce.payment.service.CommercePaymentMethodGroupRelLocalService;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.service.CommercePriceListService;
import com.liferay.commerce.product.constants.CommerceChannelAccountEntryRelConstants;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.model.CommerceChannelAccountEntryRel;
import com.liferay.commerce.product.service.CommerceChannelAccountEntryRelService;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.service.CommerceAddressService;
import com.liferay.commerce.term.model.CommerceTermEntry;
import com.liferay.commerce.term.service.CommerceTermEntryService;
import com.liferay.headless.commerce.admin.account.dto.v1_0.AccountChannelEntry;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Sbarra
 */
@Component(
	property = {
		"default=true",
		"dto.class.name=com.liferay.commerce.model.AccountChannelEntry"
	},
	service = DTOConverter.class
)
public class AccountChannelEntryDTOConverter
	implements DTOConverter
		<CommerceChannelAccountEntryRel, AccountChannelEntry> {

	@Override
	public String getContentType() {
		return AccountChannelEntry.class.getSimpleName();
	}

	@Override
	public AccountChannelEntry toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		CommerceChannelAccountEntryRel commerceChannelAccountEntryRel =
			_commerceChannelAccountEntryRelService.
				fetchCommerceChannelAccountEntryRel(
					(Long)dtoConverterContext.getId());

		if (commerceChannelAccountEntryRel == null) {
			throw new NoSuchGroupAccountEntryRelException();
		}

		return new AccountChannelEntry() {
			{
				setAccountExternalReferenceCode(
					() -> {
						AccountEntry accountEntry =
							_accountEntryService.fetchAccountEntry(
								commerceChannelAccountEntryRel.
									getAccountEntryId());

						if ((accountEntry != null) &&
							!Validator.isBlank(
								accountEntry.getExternalReferenceCode())) {

							return accountEntry.getExternalReferenceCode();
						}

						return null;
					});
				setAccountId(commerceChannelAccountEntryRel::getAccountEntryId);
				setActions(dtoConverterContext::getActions);
				setChannelExternalReferenceCode(
					() -> {
						CommerceChannel commerceChannel =
							_commerceChannelLocalService.fetchCommerceChannel(
								commerceChannelAccountEntryRel.
									getCommerceChannelId());

						if ((commerceChannel != null) &&
							!Validator.isBlank(
								commerceChannel.getExternalReferenceCode())) {

							return commerceChannel.getExternalReferenceCode();
						}

						return null;
					});
				setChannelId(
					() ->
						commerceChannelAccountEntryRel.getCommerceChannelId());
				setClassExternalReferenceCode(
					() -> _toClassExternalReferenceCode(
						commerceChannelAccountEntryRel));
				setClassPK(commerceChannelAccountEntryRel::getClassPK);
				setId(
					() ->
						commerceChannelAccountEntryRel.
							getCommerceChannelAccountEntryRelId());
				setOverrideEligibility(
					() ->
						commerceChannelAccountEntryRel.isOverrideEligibility());
				setPriority(commerceChannelAccountEntryRel::getPriority);
			}
		};
	}

	private String _toClassExternalReferenceCode(
			CommerceChannelAccountEntryRel commerceChannelAccountEntryRel)
		throws Exception {

		int type = commerceChannelAccountEntryRel.getType();

		if ((type ==
				CommerceChannelAccountEntryRelConstants.TYPE_BILLING_ADDRESS) ||
			(type ==
				CommerceChannelAccountEntryRelConstants.
					TYPE_SHIPPING_ADDRESS)) {

			CommerceAddress commerceAddress =
				_commerceAddressService.getCommerceAddress(
					GetterUtil.getLong(
						commerceChannelAccountEntryRel.getClassPK()));

			if (!Validator.isBlank(
					commerceAddress.getExternalReferenceCode())) {

				return commerceAddress.getExternalReferenceCode();
			}
		}
		else if (type ==
					CommerceChannelAccountEntryRelConstants.
						TYPE_DELIVERY_TERM) {

			CommerceTermEntry commerceTermEntry =
				_commerceTermEntryService.getCommerceTermEntry(
					GetterUtil.getLong(
						commerceChannelAccountEntryRel.getClassPK()));

			if (!Validator.isBlank(
					commerceTermEntry.getExternalReferenceCode())) {

				return commerceTermEntry.getExternalReferenceCode();
			}
		}
		else if (type ==
					CommerceChannelAccountEntryRelConstants.TYPE_DISCOUNT) {

			CommerceDiscount commerceDiscount =
				_commerceDiscountService.getCommerceDiscount(
					GetterUtil.getLong(
						commerceChannelAccountEntryRel.getClassPK()));

			if (!Validator.isBlank(
					commerceDiscount.getExternalReferenceCode())) {

				return commerceDiscount.getExternalReferenceCode();
			}
		}
		else if (type == CommerceChannelAccountEntryRelConstants.TYPE_PAYMENT) {
			CommercePaymentMethodGroupRel commercePaymentMethodGroupRel =
				_commercePaymentMethodGroupRelLocalService.
					getCommercePaymentMethodGroupRel(
						GetterUtil.getLong(
							commerceChannelAccountEntryRel.getClassPK()));

			if (!Validator.isBlank(
					commercePaymentMethodGroupRel.getPaymentIntegrationKey())) {

				return commercePaymentMethodGroupRel.getPaymentIntegrationKey();
			}
		}
		else if (type ==
					CommerceChannelAccountEntryRelConstants.TYPE_PAYMENT_TERM) {

			CommerceTermEntry commerceTermEntry =
				_commerceTermEntryService.getCommerceTermEntry(
					GetterUtil.getLong(
						commerceChannelAccountEntryRel.getClassPK()));

			if (!Validator.isBlank(
					commerceTermEntry.getExternalReferenceCode())) {

				return commerceTermEntry.getExternalReferenceCode();
			}
		}
		else if (type ==
					CommerceChannelAccountEntryRelConstants.TYPE_PRICE_LIST) {

			CommercePriceList commercePriceList =
				_commercePriceListService.getCommercePriceList(
					GetterUtil.getLong(
						commerceChannelAccountEntryRel.getClassPK()));

			if (!Validator.isBlank(
					commercePriceList.getExternalReferenceCode())) {

				return commercePriceList.getExternalReferenceCode();
			}
		}
		else if (type == CommerceChannelAccountEntryRelConstants.TYPE_USER) {
			User user = _userService.getUserById(
				GetterUtil.getLong(
					commerceChannelAccountEntryRel.getClassPK()));

			if (!Validator.isBlank(user.getExternalReferenceCode())) {
				return user.getExternalReferenceCode();
			}
		}

		return null;
	}

	@Reference
	private AccountEntryService _accountEntryService;

	@Reference
	private CommerceAddressService _commerceAddressService;

	@Reference
	private CommerceChannelAccountEntryRelService
		_commerceChannelAccountEntryRelService;

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private CommerceDiscountService _commerceDiscountService;

	@Reference
	private CommercePaymentMethodGroupRelLocalService
		_commercePaymentMethodGroupRelLocalService;

	@Reference
	private CommercePriceListService _commercePriceListService;

	@Reference
	private CommerceTermEntryService _commerceTermEntryService;

	@Reference
	private UserService _userService;

}