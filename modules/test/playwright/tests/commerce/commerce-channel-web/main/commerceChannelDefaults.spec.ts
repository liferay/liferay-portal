/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {accountsPagesTest} from '../../../../fixtures/accountsPagesTest';
import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {productMenuPageTest} from '../../../../fixtures/productMenuPageTest';
import {siteSettingsPagesTest} from '../../../../fixtures/siteSettingsPagesTest';
import {liferayConfig} from '../../../../liferay.config';
import getRandomString from '../../../../utils/getRandomString';
import {
	performLoginViaApi,
	performLogout,
	userData,
} from '../../../../utils/performLogin';
import {waitForAlert} from '../../../../utils/waitForAlert';
import getPageDefinition from '../../../layout-content-page-editor-web/main/utils/getPageDefinition';
import getWidgetDefinition from '../../../layout-content-page-editor-web/main/utils/getWidgetDefinition';

export const test = mergeTests(
	accountsPagesTest,
	commercePagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	productMenuPageTest,
	siteSettingsPagesTest
);

test(
	'A Sales Agent can manage channel defaults',
	{tag: '@LPD-26142'},
	async ({
		apiHelpers,
		commerceAccountManagementPage,
		commerceAdminChannelsPage,
		commerceChannelDefaultsPage,
		page,
		site,
	}) => {
		await page.goto(liferayConfig.environment.baseUrl);

		const companyId = await page.evaluate(() => {
			return Liferay.ThemeDisplay.getCompanyId();
		});

		const role = await apiHelpers.headlessAdminUser.postRole({
			name: 'Sales Agent ' + getRandomString(),
			rolePermissions: [
				{
					actionIds: [
						'ADD_USER',
						'ASSIGN_USERS',
						'INVITE_USER',
						'MANAGE_ORGANIZATIONS',
						'MANAGE_CHANNEL_DEFAULTS',
						'UNASSIGN_USERS',
						'UPDATE',
						'UPDATE_USERS',
					],
					primaryKey: companyId,
					resourceName: 'com.liferay.account.model.AccountEntry',
					scope: 1,
				},
				{
					actionIds: ['VIEW'],
					primaryKey: companyId,
					resourceName: 'com.liferay.account.model.AccountRole',
					scope: 1,
				},
				{
					actionIds: ['MANAGE_COMMERCE_CURRENCIES'],
					primaryKey: companyId,
					resourceName: 'com.liferay.commerce.currency',
					scope: 1,
				},
				{
					actionIds: ['VIEW_COMMERCE_DISCOUNTS'],
					primaryKey: companyId,
					resourceName: 'com.liferay.commerce.discount',
					scope: 1,
				},
				{
					actionIds: ['VIEW'],
					primaryKey: companyId,
					resourceName:
						'com.liferay.commerce.discount.model.CommerceDiscount',
					scope: 1,
				},
				{
					actionIds: ['UPDATE', 'VIEW'],
					primaryKey: companyId,
					resourceName:
						'com.liferay.commerce.payment.model.CommercePaymentMethodGroupRel',
					scope: 1,
				},
				{
					actionIds: ['VIEW'],
					primaryKey: companyId,
					resourceName:
						'com.liferay.commerce.price.list.model.CommercePriceList',
					scope: 1,
				},
				{
					actionIds: ['UPDATE', 'VIEW'],
					primaryKey: companyId,
					resourceName:
						'com.liferay.commerce.product.model.CommerceChannel',
					scope: 1,
				},
				{
					actionIds: ['VIEW_COMMERCE_TERM_ENTRY'],
					primaryKey: companyId,
					resourceName: 'com.liferay.commerce.term',
					scope: 1,
				},
				{
					actionIds: ['VIEW'],
					primaryKey: companyId,
					resourceName:
						'com.liferay.commerce.term.model.CommerceTermEntry',
					scope: 1,
				},
				{
					actionIds: ['MANAGE_AVAILABLE_ACCOUNTS'],
					primaryKey: companyId,
					resourceName:
						'com.liferay.portal.kernel.model.Organization',
					scope: 1,
				},
				{
					actionIds: ['VIEW'],
					primaryKey: companyId,
					resourceName: 'com.liferay.portal.kernel.model.User',
					scope: 1,
				},
				{
					actionIds: ['ACCESS_IN_CONTROL_PANEL'],
					primaryKey: companyId,
					resourceName:
						'com_liferay_account_admin_web_internal_portlet_AccountEntriesAdminPortlet',
					scope: 1,
				},
			],
		});

		const user =
			await apiHelpers.headlessAdminUser.getUserAccountByEmailAddress(
				'demo.unprivileged@liferay.com'
			);

		await apiHelpers.headlessAdminUser.assignUserToRole(
			role.externalReferenceCode,
			user.id
		);

		const account1 = await apiHelpers.headlessAdminUser.postAccount({
			name: getRandomString(),
			type: 'business',
		});

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account1.id,
			[user.emailAddress]
		);

		await apiHelpers.headlessCommerceAdminAccount.postAddress(account1.id);

		const account2 = await apiHelpers.headlessAdminUser.postAccount({
			name: getRandomString(),
			type: 'business',
		});

		const deliveryTerm =
			await apiHelpers.headlessCommerceAdminOrder.postTerm({
				type: 'delivery-terms',
			});

		const paymentTerm =
			await apiHelpers.headlessCommerceAdminOrder.postTerm({
				type: 'payment-terms',
			});

		const discount =
			await apiHelpers.headlessCommerceAdminPricing.postDiscount();

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getWidgetDefinition({
					id: getRandomString(),
					widgetName:
						'com_liferay_account_admin_web_internal_portlet_AccountEntriesManagementPortlet',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		const channel =
			await apiHelpers.headlessCommerceAdminChannel.postChannel({
				siteGroupId: site.id,
			});

		await commerceAdminChannelsPage.goto();

		await (
			await commerceAdminChannelsPage.channelsTableRowLink(channel.name)
		).click();

		await page
			.getByLabel('Commerce Site Type')
			.selectOption({label: 'B2B'});

		const headerActions = page.locator('.header-actions');

		await headerActions.getByText('Save').click();

		await waitForAlert(page);

		await performLogout(page);
		await performLoginViaApi({page, screenName: user.alternateName});

		await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

		await expect(
			commerceAccountManagementPage.accountsTableRowLink(account1.id)
		).toBeVisible();
		await expect(
			commerceAccountManagementPage.accountsTableRowLink(account2.id)
		).toHaveCount(0);

		await commerceAccountManagementPage
			.accountsTableRowLink(account1.id)
			.click();
		await commerceAccountManagementPage.channelDefaultsLink.click();

		await expect(
			commerceChannelDefaultsPage.defaultBillingCommerceAddresses
		).toBeVisible();

		await commerceChannelDefaultsPage.defaultBillingCommerceAddressesActions.click();
		await commerceChannelDefaultsPage.editMenuItem.click();

		await expect(
			commerceChannelDefaultsPage.editFrameChannelSelect
		).not.toBeEmpty();

		await commerceChannelDefaultsPage.editFrameSaveButton.click();
		await commerceChannelDefaultsPage.defaultShippingCommerceAddressesActions.click();
		await commerceChannelDefaultsPage.editMenuItem.click();

		await expect(
			commerceChannelDefaultsPage.editFrameChannelSelect
		).not.toBeEmpty();
		await commerceChannelDefaultsPage.editFrameSaveButton.click();

		await commerceChannelDefaultsPage.defaultDeliveryCommerceTermEntriesButton.click();

		await expect(
			commerceChannelDefaultsPage.editFrameChannelSelect
		).not.toBeEmpty();

		await commerceChannelDefaultsPage.editFrameSaveButton.click();

		await expect(
			commerceChannelDefaultsPage.defaultDeliveryCommerceTermEntries.getByText(
				deliveryTerm.label['en_US']
			)
		).toBeVisible();

		await commerceChannelDefaultsPage.defaultPaymentCommerceTermEntriesButton.click();

		await expect(
			commerceChannelDefaultsPage.editFrameChannelSelect
		).not.toBeEmpty();

		await commerceChannelDefaultsPage.editFrameSaveButton.click();

		await expect(
			commerceChannelDefaultsPage.defaultPaymentCommerceTermEntries.getByText(
				paymentTerm.label['en_US']
			)
		).toBeVisible();

		await commerceChannelDefaultsPage.defaultCommerceShippingOptionButton.click();
		await commerceChannelDefaultsPage.editFrameSaveButton.click();

		await expect(
			commerceChannelDefaultsPage.defaultCommerceShippingOption
				.getByText('Use Priority Settings')
				.first()
		).toBeVisible();

		await commerceChannelDefaultsPage.defaultCommercePriceListsButton.click();
		await commerceChannelDefaultsPage.editFramePriceListSelect.selectOption(
			{
				label: 'Master Base Price List',
			}
		);
		await commerceChannelDefaultsPage.editFrameSaveButton.click();

		await expect(
			commerceChannelDefaultsPage.defaultCommercePriceLists.getByText(
				'Master Base Price List'
			)
		).toBeVisible();

		await commerceChannelDefaultsPage.defaultCommerceDiscountsButton.click();

		await expect(
			commerceChannelDefaultsPage.editFrameChannelSelect
		).not.toBeEmpty();

		await commerceChannelDefaultsPage.editFrameSaveButton.click();

		await expect(
			commerceChannelDefaultsPage.defaultCommerceDiscounts.getByText(
				discount.title
			)
		).toBeVisible();

		await commerceChannelDefaultsPage.defaultCommerceCurrenciesButton.click();

		await expect(
			commerceChannelDefaultsPage.editFrameChannelSelect
		).not.toBeEmpty();

		await commerceChannelDefaultsPage.editFrameSaveButton.click();

		await expect(
			commerceChannelDefaultsPage.defaultCommerceCurrencies.getByText(
				'US Dollar'
			)
		).toBeVisible();

		await commerceChannelDefaultsPage.defaultCommercePaymentMethodButton.click();
		await commerceChannelDefaultsPage.editFrameSaveButton.click();

		await expect(
			commerceChannelDefaultsPage.defaultCommercePaymentMethod
				.getByText('Use Priority Settings')
				.first()
		).toBeVisible();

		await commerceChannelDefaultsPage.defaultUsersButton.click();

		await expect(
			commerceChannelDefaultsPage.editFrameChannelSelect
		).not.toBeEmpty();

		await commerceChannelDefaultsPage.editFrameSaveButton.click();

		await expect(
			commerceChannelDefaultsPage.defaultUsers.getByText('Test')
		).toBeVisible();
	}
);

test(
	'Can user with account manager role view and manage channel defaults',
	{tag: ['@LPD-28220', '@LPD-102068']},
	async ({
		accountManagementWidgetPage,
		accountsPage,
		apiHelpers,
		commerceChannelDefaultsPage,
		page,
		site,
	}) => {
		const userAccount =
			await apiHelpers.headlessAdminUser.postUserAccount();

		userData[userAccount.alternateName] = {
			name: userAccount.givenName,
			password: 'test',
			surname: userAccount.familyName,
		};

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getWidgetDefinition({
					id: getRandomString(),
					widgetName:
						'com_liferay_account_admin_web_internal_portlet_AccountEntriesManagementPortlet',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		const organization =
			await apiHelpers.headlessAdminUser.postOrganization();

		const account1 = await apiHelpers.headlessAdminUser.postAccount({
			name: getRandomString(),
			type: 'business',
		});

		const account2 = await apiHelpers.headlessAdminUser.postAccount({
			name: getRandomString(),
			type: 'business',
		});

		await apiHelpers.headlessAdminUser.assignAccountToOrganization(
			account1.id,
			organization.id
		);
		await apiHelpers.headlessAdminUser.assignAccountToOrganization(
			account2.id,
			organization.id
		);
		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account1.id,
			[userAccount.emailAddress]
		);
		await apiHelpers.headlessAdminUser.assignUserToOrganizationByEmailAddress(
			organization.id,
			userAccount.emailAddress
		);

		await apiHelpers.jsonWebServicesUser.assignUsersToSite(
			site.id,
			userAccount.id
		);

		await performLogout(page);
		await performLoginViaApi({page, screenName: userAccount.alternateName});

		await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

		await expect(
			accountManagementWidgetPage.accountCell(account1.name)
		).toBeVisible();
		await expect(
			accountManagementWidgetPage.accountNameLink(account1.name)
		).toHaveCount(0);
		await expect(
			accountManagementWidgetPage.accountCell(account2.name)
		).toHaveCount(0);

		await performLogout(page);
		await performLoginViaApi({page, screenName: 'test'});

		const accountManagerRole =
			await apiHelpers.headlessAdminUser.getRoleByName('Account Manager');

		await apiHelpers.jsonWebServicesUser.assignUsersToRole(
			String(accountManagerRole.id),
			userAccount.id
		);

		await performLogout(page);
		await performLoginViaApi({page, screenName: userAccount.alternateName});

		for (const account of [account1, account2]) {
			await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

			await accountManagementWidgetPage
				.accountNameLink(account.name)
				.click();

			await accountsPage.channelDefaultsTab.click();

			const channelEntryHeaderNames = [
				'Billing Addresses',
				'Channel Account Managers',
				'Currencies',
				'Delivery Terms and Conditions',
				'Discounts',
				'Payment Methods',
				'Payment Terms and Conditions',
				'Price Lists',
				'Shipping Addresses',
				'Shipping Options',
			];

			for (const channelEntryHeaderName of channelEntryHeaderNames) {
				await expect(
					commerceChannelDefaultsPage.channelEntryHeader(
						channelEntryHeaderName
					)
				).toBeVisible();
			}

			const channelEntryNames = [
				'Billing',
				'Currencies',
				'Delivery',
				'Discounts',
				'PaymentCommerceTerm',
				'Price',
				'ShippingCommerceAddress',
				'Users',
			];

			for (const channelEntryName of channelEntryNames) {
				await expect(
					commerceChannelDefaultsPage.channelEntryAddButton(
						channelEntryName
					)
				).toHaveCount(2);
				await expect(
					commerceChannelDefaultsPage.channelEntry(channelEntryName)
				).toContainText('No Results Found');
			}

			const uneditableEntryNames = ['ShippingOption', 'PaymentMethod'];

			for (const channelEntryName of uneditableEntryNames) {
				await expect(
					commerceChannelDefaultsPage.channelEntryAddButton(
						channelEntryName
					)
				).toHaveCount(0);
				await expect(
					commerceChannelDefaultsPage.channelEntry(channelEntryName)
				).toContainText('No Results Found');
			}
		}
	}
);

test(
	'Can see the status of a default payment method linked to an account',
	{tag: '@LPD-85835'},
	async ({
		accountsPage,
		apiHelpers,
		commerceAdminChannelDetailsPage,
		commerceAdminChannelsPage,
		commerceChannelDefaultsPage,
		site,
	}) => {
		const [channel, account] = await Promise.all([
			apiHelpers.headlessCommerceAdminChannel.postChannel({
				siteGroupId: site.id,
			}),
			apiHelpers.headlessAdminUser.postAccount({
				name: getRandomString(),
				type: 'business',
			}),
		]);

		const channelPaymentMethodRow =
			commerceChannelDefaultsPage.paymentMethodRow(channel.name);

		await test.step('Set the channel as B2B', async () => {
			await commerceAdminChannelsPage.changeCommerceChannelSiteType(
				channel.name,
				'B2B'
			);
		});

		await test.step('Activate payment methods on the channel', async () => {
			await commerceAdminChannelsPage.goto();
			await (
				await commerceAdminChannelsPage.channelsTableRowLink(
					channel.name
				)
			).click();

			for (const paymentMethod of [
				'Authorize.Net',
				'Mercanet',
				'Money Order',
			]) {
				await commerceAdminChannelDetailsPage.activatePaymentMethod(
					paymentMethod,
					paymentMethod
				);
			}
		});

		await test.step('Set Money Order as the channel default for the account', async () => {
			await accountsPage.gotoAccountChannelDefaults(account.name);

			await channelPaymentMethodRow.getByLabel('Edit').click();

			await commerceChannelDefaultsPage.editFrame
				.getByLabel('Money Order')
				.check();
			await commerceChannelDefaultsPage.editFrameSaveButton.click();

			await expect(
				channelPaymentMethodRow.getByText('Money Order')
			).toBeVisible();
		});

		await test.step('Deactivate Money Order on the channel', async () => {
			await commerceAdminChannelsPage.goto();
			await (
				await commerceAdminChannelsPage.channelsTableRowLink(
					channel.name
				)
			).click();
			await commerceAdminChannelDetailsPage.deactivateChannelConfiguration(
				'Money Order',
				'Payment Methods'
			);
		});

		await test.step('Verify Money Order remains the account default with status No', async () => {
			await accountsPage.gotoAccountChannelDefaults(account.name);

			await expect(
				channelPaymentMethodRow.getByText('Money Order')
			).toBeVisible();
			await expect(
				channelPaymentMethodRow.getByRole('cell', {
					exact: true,
					name: 'No',
				})
			).toBeVisible();
		});
	}
);

test(
	'Account reverts to priority settings after the offline default payment method is removed',
	{tag: '@LPD-85835'},
	async ({
		accountsPage,
		apiHelpers,
		commerceAdminChannelDetailsPage,
		commerceAdminChannelsPage,
		commerceChannelDefaultsPage,
		offlinePaymentMethodsSystemSettingPage,
		site,
	}) => {
		const offlineKey = 'Offline ' + getRandomString();

		const [channel, account] = await Promise.all([
			apiHelpers.headlessCommerceAdminChannel.postChannel({
				siteGroupId: site.id,
			}),
			apiHelpers.headlessAdminUser.postAccount({
				name: getRandomString(),
				type: 'business',
			}),
		]);

		const channelPaymentMethodRow =
			commerceChannelDefaultsPage.paymentMethodRow(channel.name);

		try {
			await test.step('Add an offline payment method key', async () => {
				await offlinePaymentMethodsSystemSettingPage.goto();
				await offlinePaymentMethodsSystemSettingPage.addKey(offlineKey);
			});

			await test.step('Set the channel as B2B', async () => {
				await commerceAdminChannelsPage.changeCommerceChannelSiteType(
					channel.name,
					'B2B'
				);
			});

			await test.step('Activate payment methods on the channel', async () => {
				await commerceAdminChannelsPage.goto();
				await (
					await commerceAdminChannelsPage.channelsTableRowLink(
						channel.name
					)
				).click();

				for (const paymentMethod of [
					'Authorize.Net',
					'Mercanet',
					'Money Order',
					offlineKey,
				]) {
					await commerceAdminChannelDetailsPage.activatePaymentMethod(
						paymentMethod,
						paymentMethod
					);
				}
			});

			await test.step('Set the offline payment method as the channel default for the account', async () => {
				await accountsPage.gotoAccountChannelDefaults(account.name);

				await channelPaymentMethodRow.getByLabel('Edit').click();

				await commerceChannelDefaultsPage.editFrame
					.getByLabel(offlineKey)
					.check();
				await commerceChannelDefaultsPage.editFrameSaveButton.click();

				await expect(
					channelPaymentMethodRow.getByText(offlineKey)
				).toBeVisible();
			});

			await test.step('Delete the offline payment method key', async () => {
				await offlinePaymentMethodsSystemSettingPage.goto();
				await offlinePaymentMethodsSystemSettingPage.deleteKey(
					offlineKey
				);
			});

			await test.step('Verify the account default reverts to Use Priority Settings', async () => {
				await accountsPage.gotoAccountChannelDefaults(account.name);

				await expect(
					channelPaymentMethodRow.getByText('Use Priority Settings')
				).toBeVisible();
			});
		}
		finally {
			await offlinePaymentMethodsSystemSettingPage.goto();
			await offlinePaymentMethodsSystemSettingPage.deleteKey(offlineKey);
		}
	}
);

test(
	'Can edit default payment method linked to an account',
	{tag: '@LPD-85835'},
	async ({
		accountsPage,
		apiHelpers,
		commerceAdminChannelDetailsPage,
		commerceAdminChannelsPage,
		commerceChannelDefaultsPage,
		site,
	}) => {
		const [channel, account] = await Promise.all([
			apiHelpers.headlessCommerceAdminChannel.postChannel({
				siteGroupId: site.id,
			}),
			apiHelpers.headlessAdminUser.postAccount({
				name: getRandomString(),
				type: 'business',
			}),
		]);

		const channelPaymentMethodRow =
			commerceChannelDefaultsPage.paymentMethodRow(channel.name);

		await test.step('Set the channel as B2B', async () => {
			await commerceAdminChannelsPage.changeCommerceChannelSiteType(
				channel.name,
				'B2B'
			);
		});

		await test.step('Activate payment methods on the channel', async () => {
			await commerceAdminChannelsPage.goto();
			await (
				await commerceAdminChannelsPage.channelsTableRowLink(
					channel.name
				)
			).click();

			for (const paymentMethod of [
				'Authorize.Net',
				'Mercanet',
				'Money Order',
			]) {
				await commerceAdminChannelDetailsPage.activatePaymentMethod(
					paymentMethod,
					paymentMethod
				);
			}
		});

		await test.step('Verify the available payment method radios in the edit dialog', async () => {
			await accountsPage.gotoAccountChannelDefaults(account.name);

			await channelPaymentMethodRow.getByLabel('Edit').click();

			for (const name of [
				'Use Priority Settings',
				'Authorize.Net',
				'Mercanet',
				'Money Order',
			]) {
				await expect(
					commerceChannelDefaultsPage.editFrame.getByRole('radio', {
						name,
					})
				).toBeVisible();
			}
		});

		await test.step('Set Money Order as the channel default and verify', async () => {
			await commerceChannelDefaultsPage.editFrame
				.getByLabel('Money Order')
				.check();
			await commerceChannelDefaultsPage.editFrameSaveButton.click();

			await expect(
				channelPaymentMethodRow.getByText('Money Order')
			).toBeVisible();
		});

		await test.step('Set Use Priority Settings as the channel default and verify', async () => {
			await channelPaymentMethodRow.getByLabel('Edit').click();

			await commerceChannelDefaultsPage.editFrame
				.getByLabel('Use Priority Settings')
				.check();
			await commerceChannelDefaultsPage.editFrameSaveButton.click();

			await expect(
				channelPaymentMethodRow.getByText('Use Priority Settings')
			).toBeVisible();
		});
	}
);

test(
	'The Channel Defaults datasets do not show a search bar',
	{tag: ['@COMMERCE-11224', '@LPD-85835']},
	async ({accountsPage, apiHelpers, commerceChannelDefaultsPage}) => {
		const account = await apiHelpers.headlessAdminUser.postAccount({
			name: getRandomString(),
			type: 'business',
		});

		await accountsPage.gotoAccountChannelDefaults(account.name);

		await expect(
			commerceChannelDefaultsPage.defaultCommercePaymentMethod
		).toBeVisible();

		const datasets = [
			commerceChannelDefaultsPage.defaultBillingCommerceAddresses,
			commerceChannelDefaultsPage.defaultCommerceCurrencies,
			commerceChannelDefaultsPage.defaultCommerceDiscounts,
			commerceChannelDefaultsPage.defaultCommercePaymentMethod,
			commerceChannelDefaultsPage.defaultCommercePriceLists,
			commerceChannelDefaultsPage.defaultCommerceShippingOption,
			commerceChannelDefaultsPage.defaultDeliveryCommerceTermEntries,
			commerceChannelDefaultsPage.defaultPaymentCommerceTermEntries,
			commerceChannelDefaultsPage.defaultShippingCommerceAddresses,
			commerceChannelDefaultsPage.defaultUsers,
		];

		for (const dataset of datasets) {
			await expect(
				dataset.getByPlaceholder('Search', {exact: false})
			).toHaveCount(0);
		}
	}
);

test(
	'Can see help messages for CP Display Layout Configuration',
	{tag: '@LPD-64407'},
	async ({page, site, siteSettingsPage}) => {
		await siteSettingsPage.goToSiteSetting(
			'Channel',
			'CP Display Layout',
			site.friendlyUrlPath
		);

		await expect(
			page.getByText(
				'The specified layout will be used to display the category.'
			)
		).toBeVisible();
		await expect(
			page.getByText(
				'The specified layout will be used to display the product.'
			)
		).toBeVisible();
	}
);
