/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {accountsPagesTest} from '../../../../fixtures/accountsPagesTest';
import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../fixtures/loginTest';
import getRandomString from '../../../../utils/getRandomString';
import {performUserSwitch} from '../../../../utils/performLogin';
import {checkoutStorefrontSetUp} from '../../utils/commerce';

export const test = mergeTests(
	accountsPagesTest,
	commercePagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest()
);

test(
	'Address channel eligibility filters the checkout addresses and the channel default addresses',
	{
		tag: [
			'@COMMERCE-12890',
			'@COMMERCE-12891',
			'@COMMERCE-12893',
			'@COMMERCE-12894',
			'@COMMERCE-12934',
			'@LPD-108644',
		],
	},
	async ({
		accountsPage,
		apiHelpers,
		checkoutPage,
		commerceAccountAddressEligibilityPage,
		commerceAdminChannelsPage,
		editAccountChannelDefaultsPage,
		editAccountPage,
		page,
		site,
	}) => {
		const {
			account,
			buyerUser,
			channel: channel1,
			checkoutLayout,
			sku,
		} = await checkoutStorefrontSetUp(
			apiHelpers,
			commerceAdminChannelsPage,
			page,
			site
		);

		const channel2 =
			await apiHelpers.headlessCommerceAdminChannel.postChannel({
				name: getRandomString(),
			});

		const billingAddress =
			await apiHelpers.headlessCommerceAdminAccount.postAddress(
				account.id,
				{
					defaultBilling: false,
					defaultShipping: false,
					type: 1,
				}
			);
		const shippingAddress =
			await apiHelpers.headlessCommerceAdminAccount.postAddress(
				account.id,
				{
					defaultBilling: false,
					defaultShipping: false,
					type: 3,
				}
			);

		const billingAddressChannel =
			await apiHelpers.headlessCommerceAdminChannel.postAccountAddressChannel(
				billingAddress.id,
				channel1.id
			);
		const shippingAddressChannel =
			await apiHelpers.headlessCommerceAdminChannel.postAccountAddressChannel(
				shippingAddress.id,
				channel1.id
			);

		await apiHelpers.headlessCommerceAdminAccount.postAccountChannelBillingAddress(
			account.id,
			{
				channelId: channel1.id,
				classPK: billingAddress.id,
			}
		);
		await apiHelpers.headlessCommerceAdminAccount.postAccountChannelShippingAddress(
			account.id,
			{
				channelId: channel1.id,
				classPK: shippingAddress.id,
			}
		);

		const checkoutPageURL = `/web${site.friendlyUrlPath}${checkoutLayout.friendlyUrlPath}`;

		await test.step('The eligible channel default addresses are preselected at checkout', async () => {
			await apiHelpers.headlessCommerceDeliveryCart.postCart(
				{
					accountId: account.id,
					cartItems: [{quantity: 1, skuId: sku.id}],
				},
				channel1.id
			);

			await performUserSwitch(page, buyerUser.alternateName);

			await page.goto(checkoutPageURL);

			for (const {defaultAddress, step} of [
				{defaultAddress: shippingAddress, step: 'Shipping Address'},
				{defaultAddress: billingAddress, step: 'Billing Address'},
			]) {
				await checkoutPage.performCheckoutUntilStep(step);

				await expect(checkoutPage.commerceAddressSelect).toHaveValue(
					String(defaultAddress.id)
				);

				await expect(checkoutPage.commerceAddressOptions).toHaveText([
					'Add New Address',
					defaultAddress.name,
				]);

				if (step === 'Shipping Address') {
					await checkoutPage.useAsBillingCheckbox.setChecked(false);
				}
			}

			await checkoutPage.performCheckoutUntilStep('Order Confirmation');

			await expect(checkoutPage.orderSuccessMessage).toBeVisible();
		});

		await test.step('Restrict the channel default addresses to another channel', async () => {
			await performUserSwitch(page, 'test');

			await apiHelpers.headlessCommerceAdminChannel.deleteAccountAddressChannel(
				billingAddressChannel.accountAddressChannelId
			);
			await apiHelpers.headlessCommerceAdminChannel.deleteAccountAddressChannel(
				shippingAddressChannel.accountAddressChannelId
			);

			await apiHelpers.headlessCommerceAdminChannel.postAccountAddressChannel(
				billingAddress.id,
				channel2.id
			);
			await apiHelpers.headlessCommerceAdminChannel.postAccountAddressChannel(
				shippingAddress.id,
				channel2.id
			);

			await apiHelpers.headlessCommerceDeliveryCart.postCart(
				{
					accountId: account.id,
					cartItems: [{quantity: 1, skuId: sku.id}],
				},
				channel1.id
			);
		});

		await test.step('The channel defaults stay listed but cannot be saved again with the ineligible addresses', async () => {
			await accountsPage.gotoAccountAdmin();

			await accountsPage.accountsTable.search(account.name);

			await accountsPage.accountNameLink(account.name).click();

			await editAccountPage.channelDefaultsLink.click();

			for (const {addressSelect, addressType, defaultAddress} of [
				{
					addressSelect:
						editAccountChannelDefaultsPage.setDefaultBillingAddressFrameBillingAddressDropdownMenu,
					addressType: 'Billing' as const,
					defaultAddress: billingAddress,
				},
				{
					addressSelect:
						editAccountChannelDefaultsPage.setDefaultShippingAddressFrameBillingAddressDropdownMenu,
					addressType: 'Shipping' as const,
					defaultAddress: shippingAddress,
				},
			]) {
				await expect(
					await editAccountChannelDefaultsPage.addressTableRowColumn(
						1,
						addressType,
						defaultAddress.name
					)
				).toBeVisible();

				await (
					await editAccountChannelDefaultsPage.addressTableRowColumn(
						3,
						addressType,
						defaultAddress.name
					)
				).click();

				await editAccountChannelDefaultsPage.editMenuItem.click();

				await expect(
					editAccountChannelDefaultsPage.modalSaveButton
				).toBeVisible();

				await expect(
					addressSelect.locator('option', {
						hasText: defaultAddress.name,
					})
				).toHaveCount(0);

				await editAccountChannelDefaultsPage.modalSaveButton.click();

				await expect(
					editAccountChannelDefaultsPage.modalContainer.getByText(
						`The ${addressType} Address field is required.`
					)
				).toBeVisible();

				await editAccountChannelDefaultsPage.modalContainer
					.getByRole('button', {exact: true, name: 'Cancel'})
					.click();

				await expect(
					editAccountChannelDefaultsPage.modalIframe
				).toBeHidden();
			}
		});

		const eligibleAddress =
			await apiHelpers.headlessCommerceAdminAccount.postAddress(
				account.id,
				{
					defaultBilling: false,
					defaultShipping: false,
					type: 2,
				}
			);
		const restrictedAddress =
			await apiHelpers.headlessCommerceAdminAccount.postAddress(
				account.id,
				{
					defaultBilling: false,
					defaultShipping: false,
					type: 2,
				}
			);

		await apiHelpers.headlessCommerceAdminChannel.postAccountAddressChannel(
			eligibleAddress.id,
			channel1.id
		);

		await test.step('Restrict an address to another channel from the eligibility tab', async () => {
			await commerceAccountAddressEligibilityPage.goto(
				account.id,
				restrictedAddress.id
			);

			await expect(
				commerceAccountAddressEligibilityPage.allChannelsRadio
			).toBeChecked();

			await commerceAccountAddressEligibilityPage.addChannel(
				channel2.name
			);
		});

		await test.step('Only the eligible addresses are offered at checkout', async () => {
			await performUserSwitch(page, buyerUser.alternateName);

			await page.goto(checkoutPageURL);

			for (const step of ['Shipping Address', 'Billing Address']) {
				await checkoutPage.performCheckoutUntilStep(step);

				await expect(checkoutPage.commerceAddressSelect).toBeVisible();

				await expect(checkoutPage.commerceAddressOptions).toHaveText([
					'Add New Address',
					eligibleAddress.name,
				]);

				if (step === 'Shipping Address') {
					await checkoutPage.commerceAddressSelect.selectOption({
						label: eligibleAddress.name,
					});

					await checkoutPage.useAsBillingCheckbox.setChecked(false);
				}
			}
		});

		await test.step('Remove the channel from the eligibility tab', async () => {
			await performUserSwitch(page, 'test');

			await commerceAccountAddressEligibilityPage.goto(
				account.id,
				restrictedAddress.id
			);

			await commerceAccountAddressEligibilityPage.removeChannel(
				channel2.name
			);

			await commerceAccountAddressEligibilityPage.goto(
				account.id,
				restrictedAddress.id
			);

			await expect(
				commerceAccountAddressEligibilityPage.allChannelsRadio
			).toBeChecked();
		});
	}
);
