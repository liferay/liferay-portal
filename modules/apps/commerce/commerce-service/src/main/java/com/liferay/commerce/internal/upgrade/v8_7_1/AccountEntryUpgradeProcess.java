/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.upgrade.v8_7_1;

import com.liferay.commerce.product.constants.CommerceChannelAccountEntryRelConstants;
import com.liferay.commerce.product.model.CommerceChannelAccountEntryRel;
import com.liferay.commerce.product.service.CommerceChannelAccountEntryRelLocalService;
import com.liferay.commerce.term.model.CommerceTermEntry;
import com.liferay.commerce.term.service.CommerceTermEntryLocalService;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.service.AddressLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.kernel.upgrade.UpgradeStep;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Alessio Antonio Rendina
 */
public class AccountEntryUpgradeProcess extends UpgradeProcess {

	public AccountEntryUpgradeProcess(
		AddressLocalService addressLocalService,
		CommerceChannelAccountEntryRelLocalService
			commerceChannelAccountEntryRelLocalService,
		CommerceTermEntryLocalService commerceTermEntryLocalService) {

		_addressLocalService = addressLocalService;
		_commerceChannelAccountEntryRelLocalService =
			commerceChannelAccountEntryRelLocalService;
		_commerceTermEntryLocalService = commerceTermEntryLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		if (hasColumn("AccountEntry", "defaultBillingAddressId") &&
			hasColumn("AccountEntry", "defaultShippingAddressId")) {

			_updateDefaultAddresses();
		}

		if (hasColumn("AccountEntry", "defaultDeliveryCTermEntryId") &&
			hasColumn("AccountEntry", "defaultPaymentCTermEntryId")) {

			_updateDefaultCommerceTermEntries();
		}
	}

	@Override
	protected UpgradeStep[] getPostUpgradeSteps() {
		return new UpgradeStep[] {
			UpgradeProcessFactory.dropColumns(
				"AccountEntry", "defaultDeliveryCTermEntryId",
				"defaultPaymentCTermEntryId")
		};
	}

	private void _updateDefaultAddresses() throws Exception {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select accountEntryId, userId, defaultBillingAddressId, " +
					"defaultShippingAddressId from AccountEntry");

			ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				long userId = resultSet.getLong("userId");
				long accountEntryId = resultSet.getLong("accountEntryId");

				long defaultBillingAddressId = resultSet.getLong(
					"defaultBillingAddressId");

				Address billingAddress = _addressLocalService.fetchAddress(
					defaultBillingAddressId);

				if (billingAddress != null) {
					CommerceChannelAccountEntryRel
						existingCommerceChannelAccountEntryRel =
							_commerceChannelAccountEntryRelLocalService.
								fetchCommerceChannelAccountEntryRel(
									accountEntryId, Address.class.getName(),
									billingAddress.getAddressId(), 0,
									CommerceChannelAccountEntryRelConstants.
										TYPE_BILLING_ADDRESS);

					if (existingCommerceChannelAccountEntryRel != null) {
						continue;
					}

					_commerceChannelAccountEntryRelLocalService.
						addCommerceChannelAccountEntryRel(
							userId, accountEntryId, Address.class.getName(),
							billingAddress.getAddressId(), 0, false, 0,
							CommerceChannelAccountEntryRelConstants.
								TYPE_BILLING_ADDRESS);
				}

				long defaultShippingAddressId = resultSet.getLong(
					"defaultShippingAddressId");

				Address shippingAddress = _addressLocalService.fetchAddress(
					defaultShippingAddressId);

				if (shippingAddress == null) {
					continue;
				}

				CommerceChannelAccountEntryRel
					existingCommerceChannelAccountEntryRel =
						_commerceChannelAccountEntryRelLocalService.
							fetchCommerceChannelAccountEntryRel(
								accountEntryId, Address.class.getName(),
								shippingAddress.getAddressId(), 0,
								CommerceChannelAccountEntryRelConstants.
									TYPE_SHIPPING_ADDRESS);

				if (existingCommerceChannelAccountEntryRel != null) {
					continue;
				}

				_commerceChannelAccountEntryRelLocalService.
					addCommerceChannelAccountEntryRel(
						userId, accountEntryId, Address.class.getName(),
						shippingAddress.getAddressId(), 0, false, 0,
						CommerceChannelAccountEntryRelConstants.
							TYPE_SHIPPING_ADDRESS);
			}
		}
	}

	private void _updateDefaultCommerceTermEntries() throws Exception {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select accountEntryId, userId, defaultDeliveryCTermEntryId, " +
					"defaultPaymentCTermEntryId from AccountEntry");

			ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				long userId = resultSet.getLong("userId");
				long accountEntryId = resultSet.getLong("accountEntryId");
				long defaultDeliveryCTermEntryId = resultSet.getLong(
					"defaultDeliveryCTermEntryId");
				long defaultPaymentCTermEntryId = resultSet.getLong(
					"defaultPaymentCTermEntryId");

				CommerceTermEntry deliveryCommerceTermEntry =
					_commerceTermEntryLocalService.fetchCommerceTermEntry(
						defaultDeliveryCTermEntryId);

				if (deliveryCommerceTermEntry != null) {
					_commerceChannelAccountEntryRelLocalService.
						addCommerceChannelAccountEntryRel(
							userId, accountEntryId,
							CommerceTermEntry.class.getName(),
							defaultDeliveryCTermEntryId, 0, false, 0,
							CommerceChannelAccountEntryRelConstants.
								TYPE_DELIVERY_TERM);
				}

				CommerceTermEntry paymentCommerceTermEntry =
					_commerceTermEntryLocalService.fetchCommerceTermEntry(
						defaultPaymentCTermEntryId);

				if (paymentCommerceTermEntry == null) {
					continue;
				}

				_commerceChannelAccountEntryRelLocalService.
					addCommerceChannelAccountEntryRel(
						userId, accountEntryId,
						CommerceTermEntry.class.getName(),
						defaultPaymentCTermEntryId, 0, false, 0,
						CommerceChannelAccountEntryRelConstants.
							TYPE_PAYMENT_TERM);
			}
		}
	}

	private final AddressLocalService _addressLocalService;
	private final CommerceChannelAccountEntryRelLocalService
		_commerceChannelAccountEntryRelLocalService;
	private final CommerceTermEntryLocalService _commerceTermEntryLocalService;

}