/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';

import {accountsPagesTest} from '../../../../fixtures/accountsPagesTest';
import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {DataApiHelpers} from '../../../../helpers/ApiHelpers';
import {CommerceAdminChannelDetailsPage} from '../../../../pages/commerce/commerce-channel-web/commerceAdminChannelDetailsPage';
import {CommerceAdminChannelsPage} from '../../../../pages/commerce/commerce-channel-web/commerceAdminChannelsPage';
import {clickAndExpectToBeHidden} from '../../../../utils/clickAndExpectToBeHidden';
import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../../utils/getRandomString';
import {
	performLoginViaApi,
	performUserSwitch,
} from '../../../../utils/performLogin';
import {waitForAlert} from '../../../../utils/waitForAlert';
import {createSalesAgentUser, miniumSetUp} from '../../utils/commerce';

export const test = mergeTests(
	accountsPagesTest,
	commercePagesTest,
	dataApiHelpersTest,
	loginTest()
);

let channel: {id: number; name: string};
let companyId: string;
let salesAgent: {alternateName?: string; emailAddress?: string; id?: string};
let setupData: Array<{id: number | string; type: string}>;
let site: Site;
let testOrganization: {id?: string; name?: string};

test.beforeAll(async ({browser}) => {
	const page = await browser.newPage();

	await performLoginViaApi({page, screenName: 'test'});

	companyId = await page.evaluate(() => {
		return Liferay.ThemeDisplay.getCompanyId();
	});

	const apiHelpers = new DataApiHelpers(page);

	const miniumResult = await miniumSetUp(apiHelpers);

	channel = miniumResult.channel;
	site = miniumResult.site;

	testOrganization = await apiHelpers.headlessAdminUser.postOrganization({
		name: `Test Organization ${getRandomString()}`,
	});

	salesAgent = await createSalesAgentUser(apiHelpers, {siteId: site.id});

	await apiHelpers.headlessAdminUser.assignUserToOrganizationByEmailAddress(
		testOrganization.id,
		salesAgent.emailAddress
	);

	const salesAgentRole =
		await apiHelpers.headlessAdminUser.getRoleByName('Sales Agent');

	await apiHelpers.jsonWebServicesResourcePermissionApiHelper.addResourcePermission(
		'VIEW_COMMERCE_ORDERS',
		companyId,
		'0',
		'com.liferay.commerce.order',
		String(companyId),
		String(salesAgentRole.id),
		'1'
	);

	setupData = [...apiHelpers.data];

	await page.close();
});

test.afterAll(async ({browser}) => {
	const page = await browser.newPage();

	await performLoginViaApi({page, screenName: 'test'});

	const apiHelpers = new DataApiHelpers(page);

	apiHelpers.setData(setupData);

	await apiHelpers.clearData();

	await page.close();
});

const allowRequestAQuote = async (
	commerceAdminChannelsPage: CommerceAdminChannelsPage,
	commerceAdminChannelDetailsPage: CommerceAdminChannelDetailsPage,
	page: Page
) => {
	await commerceAdminChannelsPage.goto();

	await (
		await commerceAdminChannelsPage.channelsTableRowLink(channel.name)
	).click();

	await commerceAdminChannelDetailsPage.allowRequestAQuote.click();
	await commerceAdminChannelDetailsPage.saveButton.click();

	await waitForAlert(page);
};

test(
	'Sales Agent sees only accounts and orders linked to their organization',
	{tag: ['@COMMERCE-9871', '@LPD-89343']},
	async ({
		accountEntriesManagementPortletPage,
		apiHelpers,
		commerceThemeMiniumCatalogPage,
		page,
		placedOrdersPage,
	}) => {
		const accountNames: Record<string, string> = {};
		const accounts: Record<
			string,
			{externalReferenceCode?: string; id?: number}
		> = {};

		for (const key of ['A', 'B', 'C', 'D']) {
			accountNames[key] = `Account ${key} ${getRandomString()}`;
			accounts[key] = await apiHelpers.headlessAdminUser.postAccount({
				name: accountNames[key],
				type: 'business',
			});
		}

		for (const key of ['A', 'B']) {
			await apiHelpers.headlessAdminUser.postAccountOrganization(
				accounts[key].id,
				testOrganization.id
			);
		}

		const product =
			await apiHelpers.headlessCommerceAdminCatalog.getProductByName(
				'U-Joint'
			);
		const sku = product.skus[0];

		await apiHelpers.headlessCommerceAdminOrder.postOrder({
			accountId: accounts.A.id,
			channelId: channel.id,
			orderItems: [{quantity: 1, skuId: String(sku.id)}],
			orderStatus: '0',
		});
		await apiHelpers.headlessCommerceAdminOrder.postOrder({
			accountId: accounts.B.id,
			channelId: channel.id,
			orderItems: [{quantity: 1, skuId: String(sku.id)}],
			orderStatus: '1',
		});

		await performUserSwitch(page, salesAgent.alternateName);

		await test.step('Account selector lists only A and B', async () => {
			await page.goto(`/web${site.friendlyUrlPath}`, {
				waitUntil: 'networkidle',
			});

			await commerceThemeMiniumCatalogPage.openAccountSelectorDropdown();

			await expect(
				page.getByText(accountNames['A'], {exact: false})
			).toBeVisible({timeout: 5000});
			await expect(
				page.getByText(accountNames['B'], {exact: false})
			).toBeVisible({timeout: 5000});
			await expect(
				page.getByText(accountNames['C'], {exact: false})
			).toHaveCount(0);
			await expect(
				page.getByText(accountNames['D'], {exact: false})
			).toHaveCount(0);
		});

		await test.step('Account Management page lists only A and B', async () => {
			await page.goto(`/web${site.friendlyUrlPath}/account-management`, {
				waitUntil: 'networkidle',
			});

			await expect(
				accountEntriesManagementPortletPage.accountNameLink(
					accountNames['A']
				)
			).toBeVisible();
			await expect(
				accountEntriesManagementPortletPage.accountNameLink(
					accountNames['B']
				)
			).toBeVisible();
			await expect(
				accountEntriesManagementPortletPage.accountNameLink(
					accountNames['C']
				)
			).toHaveCount(0);
			await expect(
				accountEntriesManagementPortletPage.accountNameLink(
					accountNames['D']
				)
			).toHaveCount(0);
		});

		await test.step('Placed Orders shows orders from accounts in the organization', async () => {
			await page.goto(`/web${site.friendlyUrlPath}/placed-orders`, {
				waitUntil: 'networkidle',
			});

			await expect(
				placedOrdersPage.orderAccountName(accountNames['A'])
			).toBeVisible();
			await expect(
				placedOrdersPage.orderAccountName(accountNames['B'])
			).toBeVisible();
			await expect(
				placedOrdersPage.orderAccountName(accountNames['C'])
			).toHaveCount(0);
			await expect(
				placedOrdersPage.orderAccountName(accountNames['D'])
			).toHaveCount(0);
		});
	}
);

test(
	'Sales Agent Channel Defaults access depends on UPDATE and VIEW_CHANNEL_DEFAULTS permissions',
	{tag: ['@COMMERCE-12691', '@COMMERCE-12693', '@LPD-89343']},
	async ({
		accountEntriesManagementPortletPage,
		apiHelpers,
		commerceAccountManagementPage,
		page,
	}) => {
		const account1Name = `Account 1 ${getRandomString()}`;
		const account2Name = `Account 2 ${getRandomString()}`;

		const account1 = await apiHelpers.headlessAdminUser.postAccount({
			name: account1Name,
			type: 'business',
		});
		await apiHelpers.headlessAdminUser.postAccount({
			name: account2Name,
			type: 'business',
		});

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account1.id,
			[salesAgent.emailAddress]
		);

		const salesAgentRole =
			await apiHelpers.headlessAdminUser.getRoleByName('Sales Agent');

		for (const action of ['UPDATE', 'VIEW_CHANNEL_DEFAULTS']) {
			try {
				await apiHelpers.jsonWebServicesResourcePermissionApiHelper.removeResourcePermission(
					action,
					companyId,
					'0',
					'com.liferay.account.model.AccountEntry',
					String(companyId),
					String(salesAgentRole.id),
					'1'
				);
			}
			catch {
				continue;
			}
		}

		await performUserSwitch(page, salesAgent.alternateName);

		await test.step('Without UPDATE/VIEW_CHANNEL_DEFAULTS perms, Sales Agent sees only the assigned account and has no Edit option', async () => {
			await page.goto(`/web${site.friendlyUrlPath}/account-management`, {
				waitUntil: 'networkidle',
			});

			await expect(
				accountEntriesManagementPortletPage.accountName(account1Name)
			).toBeVisible();
			await expect(
				accountEntriesManagementPortletPage.accountNameLink(
					account1Name
				)
			).toHaveCount(0);
			await expect(
				accountEntriesManagementPortletPage.accountName(account2Name)
			).toHaveCount(0);
			await expect(
				accountEntriesManagementPortletPage.accountNameLink(
					account2Name
				)
			).toHaveCount(0);

			await (
				await accountEntriesManagementPortletPage.accountEntriesTableRowActions(
					account1Name
				)
			).click();

			await expect(
				accountEntriesManagementPortletPage.editMenuItem
			).toHaveCount(0);
		});

		await performLoginViaApi({page, screenName: 'test'});

		for (const action of ['UPDATE', 'VIEW_CHANNEL_DEFAULTS']) {
			await apiHelpers.jsonWebServicesResourcePermissionApiHelper.addResourcePermission(
				action,
				companyId,
				'0',
				'com.liferay.account.model.AccountEntry',
				String(companyId),
				String(salesAgentRole.id),
				'1'
			);
		}

		await performUserSwitch(page, salesAgent.alternateName);

		try {
			await test.step('With UPDATE/VIEW_CHANNEL_DEFAULTS perms, Sales Agent can open the account and see the Channel Defaults tab', async () => {
				await page.goto(
					`/web${site.friendlyUrlPath}/account-management`,
					{
						waitUntil: 'networkidle',
					}
				);

				await accountEntriesManagementPortletPage
					.accountNameLink(account1Name)
					.click();

				await commerceAccountManagementPage.channelDefaultsLink.click();

				for (const heading of [
					'Billing Addresses',
					'Shipping Addresses',
					'Delivery Terms and Conditions',
					'Payment Terms and Conditions',
					'Shipping Options',
					'Price Lists',
					'Discounts',
					'Currencies',
					'Payment Methods',
					'Channel Account Managers',
				]) {
					await expect(
						commerceAccountManagementPage.sectionHeading(heading)
					).toBeVisible();
				}
			});
		}
		finally {
			await performLoginViaApi({page, screenName: 'test'});

			for (const action of ['UPDATE', 'VIEW_CHANNEL_DEFAULTS']) {
				await apiHelpers.jsonWebServicesResourcePermissionApiHelper.removeResourcePermission(
					action,
					companyId,
					'0',
					'com.liferay.account.model.AccountEntry',
					String(companyId),
					String(salesAgentRole.id),
					'1'
				);
			}
		}
	}
);

test(
	'Sales Agent can invite a buyer user to an account',
	{tag: ['@COMMERCE-9883', '@LPD-89343']},
	async ({
		accountEntriesManagementPortletPage,
		apiHelpers,
		commerceAccountManagementPage,
		page,
	}) => {
		const accountName = `Commerce Account ${getRandomString()}`;
		const buyerSuffix = getRandomString().slice(0, 8);
		const buyerEmail = `buyer.${buyerSuffix}@liferay.com`;
		const buyerFirstName = `Buyer${buyerSuffix}`;
		const buyerLastName = 'User';
		const buyerScreenName = `buyer${buyerSuffix}`;

		const account = await apiHelpers.headlessAdminUser.postAccount({
			name: accountName,
			type: 'business',
		});

		await apiHelpers.headlessAdminUser.postAccountOrganization(
			account.id,
			testOrganization.id
		);

		await performUserSwitch(page, salesAgent.alternateName);

		await page.goto(`/web${site.friendlyUrlPath}/account-management`, {
			waitUntil: 'networkidle',
		});

		await accountEntriesManagementPortletPage
			.accountNameLink(accountName)
			.click();

		await commerceAccountManagementPage.usersLink.click();

		await commerceAccountManagementPage.newButton.click();
		await commerceAccountManagementPage.assignUsersMenuItem.click();

		await commerceAccountManagementPage.newUserButton.click();

		await commerceAccountManagementPage.screenNameInput.fill(
			buyerScreenName
		);
		await commerceAccountManagementPage.emailAddressInput.fill(buyerEmail);
		await commerceAccountManagementPage.firstNameInput.fill(buyerFirstName);
		await commerceAccountManagementPage.lastNameInput.fill(buyerLastName);

		await commerceAccountManagementPage.saveButton.click();

		await waitForAlert(page);

		await expect(
			page.getByText(`${buyerFirstName} ${buyerLastName}`, {exact: false})
		).toBeVisible({timeout: 30000});
	}
);

test(
	'Sales Agent can edit account details and addresses',
	{tag: ['@COMMERCE-9882', '@LPD-89343']},
	async ({
		accountEntriesManagementPortletPage,
		apiHelpers,
		commerceAccountManagementPage,
		page,
	}) => {
		const accountName = `Commerce Account ${getRandomString()}`;
		const addressName = `Address ${getRandomString()}`;
		const city = 'Test City';
		const editedAccountName = `Edited ${accountName}`;
		const editedDescription = `Edited Description ${getRandomString()}`;
		const street = '36205 Snake Hill Rd';
		const taxId = '123456';
		const zip = '12345';

		const account = await apiHelpers.headlessAdminUser.postAccount({
			name: accountName,
			type: 'business',
		});

		await apiHelpers.headlessAdminUser.postAccountOrganization(
			account.id,
			testOrganization.id
		);

		await performUserSwitch(page, salesAgent.alternateName);

		await page.goto(`/web${site.friendlyUrlPath}/account-management`, {
			waitUntil: 'networkidle',
		});

		await accountEntriesManagementPortletPage
			.accountNameLink(accountName)
			.click();

		await test.step('Add an address via the Addresses tab', async () => {
			await commerceAccountManagementPage.addressesLink.click();

			await expect(
				commerceAccountManagementPage.noAddressesFoundMessage
			).toBeVisible();

			await commerceAccountManagementPage.newAddressButton.click();

			await commerceAccountManagementPage.nameInput.fill(addressName);
			await commerceAccountManagementPage.street1Input.fill(street);
			await commerceAccountManagementPage.cityInput.fill(city);
			await commerceAccountManagementPage.countryCombobox.selectOption({
				label: 'United States',
			});
			await commerceAccountManagementPage.regionCombobox.selectOption({
				label: 'California',
			});
			await commerceAccountManagementPage.postalCodeInput.fill(zip);

			await commerceAccountManagementPage.saveButton.click();

			await waitForAlert(page);
		});

		await test.step('Edit account details on the Details tab', async () => {
			await commerceAccountManagementPage.detailsLink.click();

			await commerceAccountManagementPage.nameInput.fill(
				editedAccountName
			);
			await commerceAccountManagementPage.descriptionInput.fill(
				editedDescription
			);
			await commerceAccountManagementPage.taxIdInput.fill(taxId);

			await commerceAccountManagementPage.saveButton.click();

			await waitForAlert(page);
		});

		await test.step('Admin sees the changes after re-login', async () => {
			await performLoginViaApi({page, screenName: 'test'});

			await page.goto(`/web${site.friendlyUrlPath}/account-management`, {
				waitUntil: 'networkidle',
			});

			await accountEntriesManagementPortletPage
				.accountNameLink(editedAccountName)
				.click();

			await commerceAccountManagementPage.detailsLink.click();

			await expect(commerceAccountManagementPage.nameInput).toHaveValue(
				editedAccountName
			);
			await expect(
				commerceAccountManagementPage.descriptionInput
			).toHaveValue(editedDescription);
			await expect(commerceAccountManagementPage.taxIdInput).toHaveValue(
				taxId
			);

			await commerceAccountManagementPage.addressesLink.click();

			await expect(
				commerceAccountManagementPage.addressNameInList(addressName)
			).toBeVisible();
		});
	}
);

test(
	'Sales Agent can checkout an existing order and create new orders for accounts in their organization',
	{tag: ['@COMMERCE-9880', '@LPD-89343']},
	async ({
		apiHelpers,
		checkoutPage,
		commerceAdminChannelDetailsPage,
		commerceAdminChannelsPage,
		commerceMiniCartPage,
		commerceThemeMiniumCatalogPage,
		page,
	}) => {
		const account1Name = `Account 1 ${getRandomString()}`;
		const account2Name = `Account 2 ${getRandomString()}`;

		const account1 = await apiHelpers.headlessAdminUser.postAccount({
			name: account1Name,
			type: 'business',
		});
		const account2 = await apiHelpers.headlessAdminUser.postAccount({
			name: account2Name,
			type: 'business',
		});

		await apiHelpers.headlessAdminUser.postAccountOrganization(
			account1.id,
			testOrganization.id
		);
		await apiHelpers.headlessAdminUser.postAccountOrganization(
			account2.id,
			testOrganization.id
		);

		const defaultAddress = {
			city: 'Middleburg',
			countryISOCode: 'US',
			defaultBilling: true,
			defaultShipping: true,
			regionISOCode: 'VA',
			street1: '36205 Snake Hill Rd',
			zip: '50309',
		};

		await apiHelpers.headlessCommerceAdminAccount.postAddress(account1.id, {
			...defaultAddress,
			name: 'Test Name',
		});

		const salesAgentRole =
			await apiHelpers.headlessAdminUser.getRoleByName('Sales Agent');

		await apiHelpers.jsonWebServicesResourcePermissionApiHelper.addResourcePermission(
			'VIEW_BILLING_ADDRESS',
			companyId,
			'0',
			'com.liferay.commerce.order',
			String(companyId),
			String(salesAgentRole.id),
			'1'
		);

		await commerceAdminChannelsPage.goto();

		await (
			await commerceAdminChannelsPage.channelsTableRowLink(channel.name)
		).click();

		await commerceAdminChannelDetailsPage.activatePaymentMethod(
			'Money Order',
			'Money Order'
		);

		const product =
			await apiHelpers.headlessCommerceAdminCatalog.getProductByName(
				'U-Joint'
			);

		await apiHelpers.headlessCommerceAdminOrder.postOrder({
			accountId: account1.id,
			channelId: channel.id,
			orderItems: [{quantity: 1, skuId: String(product.skus[0].id)}],
			orderStatus: '2',
		});

		try {
			await performUserSwitch(page, salesAgent.alternateName);

			await test.step(`Checkout the pre-existing Open order for ${account1Name}`, async () => {
				await page.goto(`/web/${site.name}`, {
					waitUntil: 'networkidle',
				});

				await commerceThemeMiniumCatalogPage.openAccountSelectorDropdown();

				await commerceThemeMiniumCatalogPage
					.accountSelectorAccount(account1Name)
					.click();

				await page.waitForLoadState('networkidle');

				await commerceMiniCartPage.miniCartButton.click();
				await commerceMiniCartPage.submitButton.click();

				await expect(checkoutPage.activeCheckoutStep).toContainText(
					'Shipping Address'
				);

				await checkoutPage.continueButton.click();

				await expect(checkoutPage.activeCheckoutStep).toContainText(
					'Order Summary'
				);

				await checkoutPage.continueButton.click();

				await expect(
					checkoutPage.orderConfirmationContainer
				).toBeVisible();
				await expect(checkoutPage.goToOrderDetailsButton).toBeVisible();
			});

			await test.step(`Create a new order for ${account2Name} entering a new address inline at checkout`, async () => {
				await page.goto(`/web/${site.name}`, {
					waitUntil: 'networkidle',
				});

				await commerceThemeMiniumCatalogPage.openAccountSelectorDropdown();

				await commerceThemeMiniumCatalogPage
					.accountSelectorAccount(account2Name)
					.click();

				await page.waitForLoadState('networkidle');

				await expect(
					commerceThemeMiniumCatalogPage.productCardAddToCartButton(
						'U-Joint'
					)
				).toBeEnabled();

				await expect(async () => {
					await commerceThemeMiniumCatalogPage
						.productCardAddToCartButton('U-Joint')
						.click();

					await expect(
						commerceMiniCartPage.miniCartButton
					).toHaveClass('has-badge mini-cart-opener', {
						timeout: 1000,
					});
				}).toPass({timeout: 10000});

				await commerceMiniCartPage.miniCartButton.click();
				await commerceMiniCartPage.submitButton.click();

				await expect(checkoutPage.activeCheckoutStep).toContainText(
					'Shipping Address'
				);

				await checkoutPage.addAddress({
					city: 'Test City',
					countryLabel: 'United States',
					name: 'Account 2 Address',
					regionLabel: 'California',
					street: 'Test Address',
					zip: '12345',
				});

				await checkoutPage.continueButton.click();

				await expect(checkoutPage.activeCheckoutStep).toContainText(
					'Order Summary'
				);

				await checkoutPage.continueButton.click();

				await expect(
					checkoutPage.orderConfirmationContainer
				).toBeVisible();
				await expect(checkoutPage.goToOrderDetailsButton).toBeVisible();
			});
		}
		finally {
			await performLoginViaApi({page, screenName: 'test'});

			await apiHelpers.jsonWebServicesResourcePermissionApiHelper.removeResourcePermission(
				'VIEW_BILLING_ADDRESS',
				companyId,
				'0',
				'com.liferay.commerce.order',
				String(companyId),
				String(salesAgentRole.id),
				'1'
			);

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
		}
	}
);

test(
	'Sales Agent can create a quote request from the order details page',
	{tag: ['@COMMERCE-11277', '@LPD-89343']},
	async ({
		apiHelpers,
		commerceAdminChannelDetailsPage,
		commerceAdminChannelsPage,
		commerceMiniCartPage,
		commerceThemeMiniumCatalogPage,
		orderDetailsPage,
		page,
	}) => {
		const accountName = `Commerce Account ${getRandomString()}`;

		const account = await apiHelpers.headlessAdminUser.postAccount({
			name: accountName,
			type: 'business',
		});

		await apiHelpers.headlessAdminUser.postAccountOrganization(
			account.id,
			testOrganization.id
		);

		await apiHelpers.headlessCommerceAdminAccount.postAddress(account.id, {
			city: 'Middleburg',
			countryISOCode: 'US',
			defaultBilling: true,
			defaultShipping: true,
			name: 'Test Address',
			regionISOCode: 'VA',
			street1: '36205 Snake Hill Rd',
			zip: '50309',
		});

		await allowRequestAQuote(
			commerceAdminChannelsPage,
			commerceAdminChannelDetailsPage,
			page
		);

		try {
			await performUserSwitch(page, salesAgent.alternateName);

			await page.goto(`/web/${site.name}`, {
				waitUntil: 'networkidle',
			});

			await commerceThemeMiniumCatalogPage
				.productCardAddToCartButton('U-Joint')
				.click();

			await expect(commerceMiniCartPage.miniCartButton).toHaveClass(
				'has-badge mini-cart-opener'
			);

			await commerceMiniCartPage.miniCartButton.click();
			await commerceMiniCartPage.requestAQuoteButton.click();

			await expect(orderDetailsPage.requestAQuoteButton).toBeVisible();

			await orderDetailsPage.requestAQuoteButton.click();

			await expect(orderDetailsPage.requestAQuoteDialog).toBeVisible();

			await orderDetailsPage.requestAQuoteNoteInput.fill(
				'I kindly request a quote for this product.'
			);

			await orderDetailsPage.requestAQuoteSubmitButton.click();

			await expect(orderDetailsPage.quoteRequestedStatus).toBeVisible();

			await orderDetailsPage.notesButton.click();

			await expect(orderDetailsPage.editButton).toHaveCount(0);
		}
		finally {
			await performLoginViaApi({page, screenName: 'test'});

			await allowRequestAQuote(
				commerceAdminChannelsPage,
				commerceAdminChannelDetailsPage,
				page
			);
		}
	}
);

test(
	'Sales Agent can process a quote when MANAGE_QUOTES permission is granted',
	{tag: ['@COMMERCE-11509', '@LPD-89343']},
	async ({
		apiHelpers,
		commerceAdminChannelDetailsPage,
		commerceAdminChannelsPage,
		orderDetailsPage,
		page,
		placedOrdersPage,
	}) => {
		const accountName = `Commerce Account ${getRandomString()}`;

		const account = await apiHelpers.headlessAdminUser.postAccount({
			name: accountName,
			type: 'business',
		});

		await apiHelpers.headlessAdminUser.postAccountOrganization(
			account.id,
			testOrganization.id
		);

		await allowRequestAQuote(
			commerceAdminChannelsPage,
			commerceAdminChannelDetailsPage,
			page
		);

		const salesAgentRole =
			await apiHelpers.headlessAdminUser.getRoleByName('Sales Agent');

		try {
			const product =
				await apiHelpers.headlessCommerceAdminCatalog.getProductByName(
					'U-Joint'
				);
			const sku = product.skus[0];

			const orders = [];

			for (let i = 0; i < 2; i++) {
				const order =
					await apiHelpers.headlessCommerceAdminOrder.postOrder({
						accountId: account.id,
						channelId: channel.id,
						orderItems: [{quantity: 1, skuId: String(sku.id)}],
						orderStatus: '21',
					});

				orders.push(order);
			}

			await performUserSwitch(page, salesAgent.alternateName);

			await page.goto(`/web/${site.name}/placed-orders`, {
				waitUntil: 'networkidle',
			});

			await placedOrdersPage.orderRowLink(orders[0].id).click();

			await expect(orderDetailsPage.quoteRequestedStatus).toBeVisible();
			await expect(orderDetailsPage.processQuoteButton).toBeVisible();

			await orderDetailsPage.processQuoteButton.click();

			await expect(orderDetailsPage.quoteProcessedStatus).toBeVisible();
			await expect(orderDetailsPage.processQuoteButton).toHaveCount(0);

			await performLoginViaApi({page, screenName: 'test'});

			await apiHelpers.jsonWebServicesResourcePermissionApiHelper.removeResourcePermission(
				'MANAGE_QUOTES',
				companyId,
				'0',
				'com.liferay.commerce.order',
				String(companyId),
				String(salesAgentRole.id),
				'1'
			);

			try {
				await performUserSwitch(page, salesAgent.alternateName);

				await page.goto(`/web/${site.name}/placed-orders`, {
					waitUntil: 'networkidle',
				});

				await placedOrdersPage.orderRowLink(orders[1].id).click();

				await expect(
					orderDetailsPage.quoteRequestedStatus
				).toBeVisible();
				await expect(orderDetailsPage.processQuoteButton).toHaveCount(
					0
				);
			}
			finally {
				await performLoginViaApi({page, screenName: 'test'});

				await apiHelpers.jsonWebServicesResourcePermissionApiHelper.addResourcePermission(
					'MANAGE_QUOTES',
					companyId,
					'0',
					'com.liferay.commerce.order',
					String(companyId),
					String(salesAgentRole.id),
					'1'
				);
			}
		}
		finally {
			await performLoginViaApi({page, screenName: 'test'});

			await allowRequestAQuote(
				commerceAdminChannelsPage,
				commerceAdminChannelDetailsPage,
				page
			);
		}
	}
);

test(
	'Sales Agent without MANAGE_AVAILABLE_ACCOUNTS permission sees no accounts',
	{tag: ['@COMMERCE-9874', '@LPD-89343']},
	async ({
		accountEntriesManagementPortletPage,
		apiHelpers,
		commerceThemeMiniumCatalogPage,
		page,
	}) => {
		const accountNames: Record<string, string> = {};
		const accounts: Record<
			string,
			{externalReferenceCode?: string; id?: number}
		> = {};

		for (const key of ['A', 'B']) {
			accountNames[key] = `Account ${key} ${getRandomString()}`;
			accounts[key] = await apiHelpers.headlessAdminUser.postAccount({
				name: accountNames[key],
				type: 'business',
			});

			await apiHelpers.headlessAdminUser.postAccountOrganization(
				accounts[key].id,
				testOrganization.id
			);
		}

		const product =
			await apiHelpers.headlessCommerceAdminCatalog.getProductByName(
				'U-Joint'
			);
		const sku = product.skus[0];

		await apiHelpers.headlessCommerceAdminOrder.postOrder({
			accountId: accounts.A.id,
			channelId: channel.id,
			orderItems: [{quantity: 1, skuId: String(sku.id)}],
			orderStatus: '0',
		});
		await apiHelpers.headlessCommerceAdminOrder.postOrder({
			accountId: accounts.B.id,
			channelId: channel.id,
			orderItems: [{quantity: 1, skuId: String(sku.id)}],
			orderStatus: '1',
		});

		const salesAgentRole =
			await apiHelpers.headlessAdminUser.getRoleByName('Sales Agent');

		await apiHelpers.jsonWebServicesResourcePermissionApiHelper.removeResourcePermission(
			'MANAGE_AVAILABLE_ACCOUNTS',
			companyId,
			'0',
			'com.liferay.portal.kernel.model.Organization',
			String(companyId),
			String(salesAgentRole.id),
			'1'
		);

		try {
			await performUserSwitch(page, salesAgent.alternateName);

			await test.step('Account selector shows "No accounts were found"', async () => {
				await page.goto(`/web${site.friendlyUrlPath}`, {
					waitUntil: 'networkidle',
				});

				await commerceThemeMiniumCatalogPage.openAccountSelectorDropdown();

				await expect(
					page.getByText(accountNames['A'], {exact: false})
				).toHaveCount(0);
				await expect(
					page.getByText(accountNames['B'], {exact: false})
				).toHaveCount(0);
			});

			await test.step('Account Management page shows no accounts', async () => {
				await page.goto(
					`/web${site.friendlyUrlPath}/account-management`,
					{
						waitUntil: 'networkidle',
					}
				);

				await expect(
					accountEntriesManagementPortletPage.noAccountsFoundMessage
				).toBeVisible();
			});
		}
		finally {
			await performLoginViaApi({page, screenName: 'test'});

			await apiHelpers.jsonWebServicesResourcePermissionApiHelper.addResourcePermission(
				'MANAGE_AVAILABLE_ACCOUNTS',
				companyId,
				'0',
				'com.liferay.portal.kernel.model.Organization',
				String(companyId),
				String(salesAgentRole.id),
				'1'
			);
		}
	}
);

test(
	'Sales Agent with ADD_ACCOUNT_ENTRY permission can create an account from the account selector and order for it',
	{tag: ['@COMMERCE-9022', '@LPD-94598']},
	async ({apiHelpers, commerceThemeMiniumCatalogPage, page}) => {
		const accountExternalReferenceCode = getRandomString();
		const accountName = `Commerce Account ${getRandomString()}`;

		const salesAgentRole =
			await apiHelpers.headlessAdminUser.getRoleByName('Sales Agent');

		await apiHelpers.jsonWebServicesResourcePermissionApiHelper.addResourcePermission(
			'ADD_ACCOUNT_ENTRY',
			companyId,
			'0',
			'90',
			String(companyId),
			String(salesAgentRole.id),
			'1'
		);

		try {
			await performUserSwitch(page, salesAgent.alternateName);

			await test.step('Create a new account from the account selector', async () => {
				await page.goto(`/web${site.friendlyUrlPath}`, {
					waitUntil: 'networkidle',
				});

				await commerceThemeMiniumCatalogPage.openAccountSelectorDropdown();

				await clickAndExpectToBeVisible({
					target: commerceThemeMiniumCatalogPage.createNewAccountModal,
					trigger:
						commerceThemeMiniumCatalogPage.createNewAccountButton,
				});

				await commerceThemeMiniumCatalogPage.createNewAccountModalExternalReferenceCodeInput.fill(
					accountExternalReferenceCode
				);
				await commerceThemeMiniumCatalogPage.createNewAccountModalNameInput.fill(
					accountName
				);

				await clickAndExpectToBeHidden({
					target: commerceThemeMiniumCatalogPage.createNewAccountModal,
					trigger:
						commerceThemeMiniumCatalogPage.createNewAccountModalCreateButton,
				});

				await expect(
					commerceThemeMiniumCatalogPage.accountSelectorSelectedAccount
				).toHaveText(accountName);
			});

			await test.step('The new account is a business account', async () => {
				const account =
					await apiHelpers.headlessAdminUser.getAccountByExternalReferenceCode(
						accountExternalReferenceCode
					);

				apiHelpers.data.push({id: account.id, type: 'account'});

				expect(account.name).toBe(accountName);
				expect(account.type).toBe('business');
			});

			await test.step('Create a new order for the new account from the account selector', async () => {
				await expect(
					commerceThemeMiniumCatalogPage.accountSelectorNoOrderSelectedMessage
				).toBeVisible();

				await clickAndExpectToBeVisible({
					target: commerceThemeMiniumCatalogPage.createNewOrderButton,
					trigger:
						commerceThemeMiniumCatalogPage.accountSelectorButton,
				});

				await commerceThemeMiniumCatalogPage.createNewOrderButton.click();

				await page.waitForLoadState('networkidle');

				await page.goto(`/web${site.friendlyUrlPath}`, {
					waitUntil: 'networkidle',
				});

				await expect(
					commerceThemeMiniumCatalogPage.accountSelectorOrderId
				).toHaveText(/\d+/);
				await expect(
					commerceThemeMiniumCatalogPage.accountSelectorSelectedAccount
				).toHaveText(accountName);
			});
		}
		finally {
			await performLoginViaApi({page, screenName: 'test'});

			await apiHelpers.jsonWebServicesResourcePermissionApiHelper.removeResourcePermission(
				'ADD_ACCOUNT_ENTRY',
				companyId,
				'0',
				'90',
				String(companyId),
				String(salesAgentRole.id),
				'1'
			);
		}
	}
);
