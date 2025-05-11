/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {applicationsMenuPageTest} from '../../../../fixtures/applicationsMenuPageTest';
import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {instanceSettingsPagesTest} from '../../../../fixtures/instanceSettingsPagesTest';
import {isolatedSiteTest} from '../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../../fixtures/pageEditorPagesTest';
import {pageViewModePagesTest} from '../../../../fixtures/pageViewModePagesTest';
import {systemSettingsPageTest} from '../../../../fixtures/systemSettingsPageTest';
import {usersAndOrganizationsPagesTest} from '../../../../fixtures/usersAndOrganizationsPagesTest';
import getRandomString from '../../../../utils/getRandomString';
import performLogin, {
	performLoginViaApi,
	performLogout,
	userData,
} from '../../../../utils/performLogin';
import {waitForAlert} from '../../../../utils/waitForAlert';
import getPageDefinition from '../../../layout-content-page-editor-web/main/utils/getPageDefinition';
import getWidgetDefinition from '../../../layout-content-page-editor-web/main/utils/getWidgetDefinition';
import {miniumSetUp} from '../../utils/commerce';
import {
	customFormatDateTimeYY,
	customFormatDateTimeYYYY,
	customFormatDateYY,
	customFormatDateYYYY,
	getDateCustomFormat,
	twoDigitFormatDate,
} from '../../utils/date';

export const test = mergeTests(
	applicationsMenuPageTest,
	commercePagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	instanceSettingsPagesTest,
	isolatedSiteTest,
	loginTest(),
	pageEditorPagesTest,
	pageViewModePagesTest,
	systemSettingsPageTest,
	usersAndOrganizationsPagesTest
);

test('LPD-25831 Placed orders widget configuration to display full addresses and phone number', async ({
	apiHelpers,
	page,
	placedOrdersPage,
	site,
	widgetPagePage,
}) => {
	const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
		groupId: site.id,
		title: getRandomString(),
	});

	const channel = await apiHelpers.headlessCommerceAdminChannel.postChannel({
		name: getRandomString(),
		siteGroupId: site.id,
	});

	const catalog = await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

	const product = await apiHelpers.headlessCommerceAdminCatalog.postProduct({
		catalogId: catalog.id,
	});

	const productSkus = await apiHelpers.headlessCommerceAdminCatalog
		.getProduct(product.productId)
		.then((product) => {
			return product.skus;
		});

	const sku = productSkus[0];

	const account = await apiHelpers.headlessAdminUser.postAccount({
		name: getRandomString(),
		type: 'person',
	});

	await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
		account.id,
		['test@liferay.com']
	);

	const phoneNumber = '12345';

	const address = await apiHelpers.headlessCommerceAdminAccount.postAddress(
		account.id,
		{phoneNumber, regionISOCode: 'AL'}
	);

	await apiHelpers.headlessCommerceAdminOrder.postOrder({
		accountId: account.id,
		billingAddressId: address.id,
		channelId: channel.id,
		orderItems: [
			{
				decimalQuantity: 10,
				quantity: 2,
				skuId: sku.id,
			},
		],
		orderStatus: '0',
		paymentStatus: '0',
		shippingAddressId: address.id,
	});

	await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

	await widgetPagePage.addPortlet('Placed Orders');

	await placedOrdersPage.viewButton.click();

	await expect(placedOrdersPage.commerceBillingAddress).not.toContainText(
		'United States'
	);
	await expect(placedOrdersPage.commerceBillingAddress).not.toContainText(
		'Alabama'
	);
	await expect(placedOrdersPage.commerceBillingAddress).not.toContainText(
		phoneNumber
	);
	await expect(placedOrdersPage.commerceShippingAddress).not.toContainText(
		'United States'
	);
	await expect(placedOrdersPage.commerceShippingAddress).not.toContainText(
		'Alabama'
	);
	await expect(placedOrdersPage.commerceShippingAddress).not.toContainText(
		phoneNumber
	);

	await page.goto(`/web/${site.name}`);

	await placedOrdersPage.optionsButton.click();

	await placedOrdersPage.configurationMenuItem.click();
	await placedOrdersPage.configurationIFrameShowFullAddressToggle.check();
	await placedOrdersPage.configurationIFrameShowPhoneNumberToggle.check();
	await placedOrdersPage.configurationIFrameSaveButton.click();
	await waitForAlert(
		placedOrdersPage.configurationIFrame,
		'Success:You have successfully updated the setup'
	);
	await page.reload();

	await placedOrdersPage.viewButton.click();

	await expect(placedOrdersPage.commerceBillingAddress).toContainText(
		'United States'
	);
	await expect(placedOrdersPage.commerceBillingAddress).toContainText(
		'Alabama'
	);
	await expect(placedOrdersPage.commerceBillingAddress).toContainText(
		phoneNumber
	);
	await expect(placedOrdersPage.commerceShippingAddress).toContainText(
		'United States'
	);
	await expect(placedOrdersPage.commerceShippingAddress).toContainText(
		'Alabama'
	);
	await expect(placedOrdersPage.commerceShippingAddress).toContainText(
		phoneNumber
	);
});

test('LPD-26643 Reorder from placed orders details page', async ({
	apiHelpers,
	checkoutPage,
	commerceAdminOrderDetailsPage,
	commerceMiniCartPage,
	page,
}) => {
	const account = await apiHelpers.headlessAdminUser.postAccount({
		name: 'admin',
		type: 'business',
	});

	const {channel, site} = await miniumSetUp(apiHelpers);

	await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
		account.id,
		['demo.unprivileged@liferay.com']
	);
	const user =
		await apiHelpers.headlessAdminUser.getUserAccountByEmailAddress(
			'demo.unprivileged@liferay.com'
		);
	const rolesResponse = await apiHelpers.headlessAdminUser.getAccountRoles(
		account.id
	);

	const buyerAccountRole = rolesResponse?.items?.filter((role) => {
		return role.name === 'Buyer';
	});

	await apiHelpers.headlessAdminUser.assignAccountRoles(
		account.externalReferenceCode,
		buyerAccountRole[0].id,
		user.emailAddress
	);

	await apiHelpers.headlessCommerceAdminAccount.postAddress(account.id, {
		phoneNumber: '12345',
		regionISOCode: 'LA',
	});

	const product = await apiHelpers.headlessCommerceAdminCatalog.getProducts(
		new URLSearchParams({
			filter: `name eq 'U-Joint'`,
		})
	);

	const productId = product.items[0].productId;

	const productSkus = await apiHelpers.headlessCommerceAdminCatalog
		.getProduct(productId)
		.then((product) => {
			return product.skus;
		});

	const sku = productSkus[0];

	await apiHelpers.headlessCommerceDeliveryCart.postCart(
		{
			accountId: account.id,
			cartItems: [
				{
					quantity: 1,
					skuId: sku.id,
				},
			],
		},
		channel.id
	);

	const siteRole =
		await apiHelpers.headlessAdminUser.getRoleByName('Site Member');

	await apiHelpers.headlessAdminUser.assignUserToSite(
		siteRole.id,
		site.id,
		user.id
	);

	await performLogout(page);

	await performLogin(page, user.alternateName);

	await page.goto(`/web/${site.name}`);

	await commerceMiniCartPage.submitCart();

	await expect(page.getByText('U-joint')).toBeVisible();

	await checkoutPage.chooseShippingAddress({index: 1});

	await expect(page.getByText('Standard Delivery (+$ 15.00)')).toBeVisible();

	await checkoutPage.continueButton.click();

	await expect(page.getByText('U-joint')).toBeVisible();

	await checkoutPage.continueButton.click();

	await expect(checkoutPage.orderSuccessMessage).toBeVisible();

	await checkoutPage.goToOrderDetailsButton.click();

	await expect(page.getByText('U-joint')).toBeVisible();

	await commerceAdminOrderDetailsPage.reorder();

	await expect(page.getByText('U-joint')).toBeVisible();

	await checkoutPage.chooseShippingAddress({index: 1});

	await expect(page.getByText('Standard Delivery (+$ 15.00)')).toBeVisible();

	await checkoutPage.continueButton.click();

	await expect(page.getByText('U-joint')).toBeVisible();

	await checkoutPage.continueButton.click();

	await expect(checkoutPage.orderSuccessMessage).toBeVisible();
});

test('LPD-32095 A user can search orders by account name', async ({
	apiHelpers,
	commerceAdminChannelsPage,
	page,
	placedOrdersPage,
	site,
	widgetPagePage,
}) => {
	const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
		groupId: site.id,
		title: getRandomString(),
	});

	const userAccount = await apiHelpers.headlessAdminUser.postUserAccount();

	userData[userAccount.alternateName] = {
		name: userAccount.givenName,
		password: 'test',
		surname: userAccount.familyName,
	};

	const channel = await apiHelpers.headlessCommerceAdminChannel.postChannel({
		name: getRandomString(),
		siteGroupId: site.id,
	});

	await commerceAdminChannelsPage.changeCommerceChannelSiteType(
		channel.name,
		'B2B'
	);

	const catalog = await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

	const product = await apiHelpers.headlessCommerceAdminCatalog.postProduct({
		catalogId: catalog.id,
	});

	const account1 = await apiHelpers.headlessAdminUser.postAccount({
		name: getRandomString(),
		type: 'business',
	});

	await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
		account1.id,
		[userAccount.emailAddress]
	);

	const rolesResponse1 = await apiHelpers.headlessAdminUser.getAccountRoles(
		account1.id
	);

	const accountRoleBuyer1 = rolesResponse1?.items?.filter((role) => {
		return role.name === 'Buyer';
	});

	await apiHelpers.headlessAdminUser.assignAccountRoles(
		account1.externalReferenceCode,
		accountRoleBuyer1[0].id,
		userAccount.emailAddress
	);

	const account2 = await apiHelpers.headlessAdminUser.postAccount({
		name: getRandomString(),
		type: 'business',
	});

	apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
		account2.id,
		[userAccount.emailAddress]
	);

	await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

	await widgetPagePage.addPortlet('Placed Orders');

	const productSkus = await apiHelpers.headlessCommerceAdminCatalog
		.getProduct(product.productId)
		.then((product) => {
			return product.skus;
		});

	const sku = productSkus[0];

	const phoneNumber = '12345';

	const address1 = await apiHelpers.headlessCommerceAdminAccount.postAddress(
		account1.id,
		{phoneNumber, regionISOCode: 'AL'}
	);

	await apiHelpers.headlessCommerceAdminOrder.postOrder({
		accountId: account1.id,
		billingAddressId: address1.id,
		channelId: channel.id,
		orderItems: [
			{
				decimalQuantity: 10,
				quantity: 2,
				skuId: sku.id,
			},
		],
		orderStatus: '0',
		paymentMethod: 'paypal',
		paymentStatus: '0',
		shippingAddressId: address1.id,
	});

	const address2 = await apiHelpers.headlessCommerceAdminAccount.postAddress(
		account2.id,
		{phoneNumber, regionISOCode: 'AL'}
	);

	await apiHelpers.headlessCommerceAdminOrder.postOrder({
		accountId: account2.id,
		billingAddressId: address2.id,
		channelId: channel.id,
		orderItems: [
			{
				decimalQuantity: 10,
				quantity: 2,
				skuId: sku.id,
			},
		],
		orderStatus: '0',
		paymentMethod: 'paypal',
		paymentStatus: '0',
		shippingAddressId: address2.id,
	});

	await performLogout(page);
	await performLogin(page, userAccount.alternateName);

	await page.goto(`/web/${site.name}`);

	await placedOrdersPage.searchInput.fill(account2.name);
	await placedOrdersPage.searchButton.click();

	await expect(placedOrdersPage.orderAccountName(account1.name)).toHaveCount(
		0
	);
	await expect(placedOrdersPage.orderAccountName(account2.name)).toHaveCount(
		1
	);
});

test('LPD-33783 Placed orders table displays correct fields', async ({
	apiHelpers,
	page,
	placedOrdersPage,
	site,
	widgetPagePage,
}) => {
	const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
		groupId: site.id,
		title: getRandomString(),
	});

	const channel = await apiHelpers.headlessCommerceAdminChannel.postChannel({
		name: 'Placed order Channel',
		siteGroupId: site.id,
	});

	const account = await apiHelpers.headlessAdminUser.postAccount({
		name: getRandomString(),
		type: 'person',
	});

	await apiHelpers.headlessCommerceAdminOrder.postOrder({
		accountId: account.id,
		channelId: channel.id,
		name: 'order1',
		orderStatus: '0',
	});

	await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

	await widgetPagePage.addPortlet('Placed Orders');

	await expect(placedOrdersPage.table).toBeVisible();

	const tableHeaderLabels = [
		'Order ID',
		'Name',
		'Order Type',
		'ERC',
		'Purchase Order Number',
		'Order Date',
		'Account',
		'Submitted By',
		'Status',
		'Amount',
	];

	tableHeaderLabels.forEach((tableHeaderLabel) => {
		expect(
			page.getByRole('columnheader', {
				exact: true,
				name: tableHeaderLabel,
			})
		).toBeVisible();
	});
});

test('LPD-33658 Assert date and time are displayed as order date', async ({
	apiHelpers,
	commerceAdminChannelsPage,
	page,
	placedOrdersPage,
	site,
	widgetPagePage,
}) => {
	const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
		groupId: site.id,
		title: getRandomString(),
	});

	const channel = await apiHelpers.headlessCommerceAdminChannel.postChannel({
		name: getRandomString(),
		siteGroupId: site.id,
	});

	await commerceAdminChannelsPage.changeCommerceChannelSiteType(
		channel.name,
		'B2B'
	);

	const account = await apiHelpers.headlessAdminUser.postAccount({
		name: getRandomString(),
		type: 'business',
	});

	await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

	await widgetPagePage.addPortlet('Placed Orders');

	const catalog = await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

	const product = await apiHelpers.headlessCommerceAdminCatalog.postProduct({
		catalogId: catalog.id,
	});

	const productSkus = await apiHelpers.headlessCommerceAdminCatalog
		.getProduct(product.productId)
		.then((product) => {
			return product.skus;
		});

	const sku = productSkus[0];

	const address = await apiHelpers.headlessCommerceAdminAccount.postAddress(
		account.id,
		{phoneNumber: '12345', regionISOCode: 'AL'}
	);

	const order = await apiHelpers.headlessCommerceAdminOrder.postOrder({
		accountId: account.id,
		billingAddressId: address.id,
		channelId: channel.id,
		orderItems: [
			{
				decimalQuantity: 10,
				quantity: 2,
				skuId: sku.id,
			},
		],
		orderStatus: '0',
		paymentMethod: 'paypal',
		paymentStatus: '0',
		shippingAddressId: address.id,
	});

	await page.reload();

	const locale = await page.evaluate(() => {
		return Liferay.ThemeDisplay.getBCP47LanguageId();
	});

	await expect(
		page
			.getByText(
				getDateCustomFormat(
					order.createDate,
					locale,
					customFormatDateTimeYY.DATE_AND_TIME
				).replace(/,(?=[^,]*$)/, '')
			)
			.or(
				page.getByText(
					getDateCustomFormat(
						order.createDate,
						locale,
						customFormatDateTimeYYYY.DATE_AND_TIME
					).replace(/,(?=[^,]*$)/, '')
				)
			)
	).toBeVisible();

	await placedOrdersPage.placedOrderTableViewButton.click();

	await expect(
		page
			.getByText(
				getDateCustomFormat(
					order.createDate,
					locale,
					customFormatDateTimeYY.DATE_AND_TIME
				).replace(/,(?=[^,]*$)/, '')
			)
			.or(
				page.getByText(
					getDateCustomFormat(
						order.createDate,
						locale,
						customFormatDateTimeYYYY.DATE_AND_TIME
					).replace(/,(?=[^,]*$)/, '')
				)
			)
	).toBeVisible();

	await page.goto(`/web/${site.name}`);
});

test('LPD-33658 Global Settings for order date configuration', async ({
	apiHelpers,
	commerceAdminChannelsPage,
	page,
	placedOrdersPage,
	site,
	systemSettingsPage,
	widgetPagePage,
}) => {
	await systemSettingsPage.goToSystemSetting('Orders', 'Placed Orders');

	try {
		if (!(await page.getByLabel('Show Order Create Time').isChecked())) {
			await page.getByLabel('Show Order Create Time').check();
			await page.getByTestId('submitConfiguration').click();
		}

		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: getRandomString(),
		});

		const channel =
			await apiHelpers.headlessCommerceAdminChannel.postChannel({
				name: getRandomString(),
				siteGroupId: site.id,
			});

		await commerceAdminChannelsPage.changeCommerceChannelSiteType(
			channel.name,
			'B2B'
		);

		const account = await apiHelpers.headlessAdminUser.postAccount({
			name: getRandomString(),
			type: 'business',
		});

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account.id,
			['demo.unprivileged@liferay.com']
		);
		const user =
			await apiHelpers.headlessAdminUser.getUserAccountByEmailAddress(
				'demo.unprivileged@liferay.com'
			);
		const rolesResponse =
			await apiHelpers.headlessAdminUser.getAccountRoles(account.id);

		const buyerAccountRole = rolesResponse?.items?.filter((role) => {
			return role.name === 'Buyer';
		});

		await apiHelpers.headlessAdminUser.assignAccountRoles(
			account.externalReferenceCode,
			buyerAccountRole[0].id,
			user.emailAddress
		);

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

		await widgetPagePage.addPortlet('Placed Orders');

		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

		const product =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
			});

		const productSkus = await apiHelpers.headlessCommerceAdminCatalog
			.getProduct(product.productId)
			.then((product) => {
				return product.skus;
			});

		const sku = productSkus[0];

		const address =
			await apiHelpers.headlessCommerceAdminAccount.postAddress(
				account.id,
				{phoneNumber: '12345', regionISOCode: 'AL'}
			);

		const order = await apiHelpers.headlessCommerceAdminOrder.postOrder({
			accountId: account.id,
			billingAddressId: address.id,
			channelId: channel.id,
			orderItems: [
				{
					decimalQuantity: 10,
					quantity: 2,
					skuId: sku.id,
				},
			],
			orderStatus: '0',
			paymentMethod: 'paypal',
			paymentStatus: '0',
			shippingAddressId: address.id,
		});

		await performLogout(page);

		await performLogin(page, user.alternateName);

		await page.goto(`/web/${site.name}`);

		await placedOrdersPage.placedOrderTableViewButton.click();

		const locale = await page.evaluate(() => {
			return Liferay.ThemeDisplay.getBCP47LanguageId();
		});

		await expect(
			page
				.getByText(
					getDateCustomFormat(
						order.createDate,
						locale,
						customFormatDateTimeYYYY.DATE_AND_TIME
					).replace(/,(?=[^,]*$)/, '')
				)
				.or(
					page.getByText(
						getDateCustomFormat(
							order.createDate,
							locale,
							customFormatDateTimeYY.DATE_AND_TIME
						).replace(/,(?=[^,]*$)/, '')
					)
				)
		).toBeVisible();

		await performLogout(page);

		await performLogin(page, 'test');

		await systemSettingsPage.goToSystemSetting('Orders', 'Placed Orders');

		await page.getByLabel('Show Order Create Time').uncheck();

		await page.getByTestId('submitConfiguration').click();

		await performLogout(page);

		await performLogin(page, user.alternateName);

		await page.goto(`/web/${site.name}`);

		await placedOrdersPage.placedOrderTableViewButton.click();

		await expect(
			page
				.getByText(
					getDateCustomFormat(
						order.createDate,
						locale,
						customFormatDateYY.DATE_AND_TIME
					)
				)
				.or(
					page.getByText(
						getDateCustomFormat(
							order.createDate,
							locale,
							customFormatDateYYYY.DATE_AND_TIME
						)
					)
				)
		).toBeVisible();
	}
	finally {
		await performLogout(page);

		await performLogin(page, 'test');

		await systemSettingsPage.goToSystemSetting('Orders', 'Placed Orders');

		await page.getByLabel('Show Order Create Time').check();

		await page.getByTestId('submitConfiguration').click();
	}
});

test('LPD-41952 Reorder from placed orders details page with different currency enabled', async ({
	apiHelpers,
	applicationsMenuPage,
	commerceAccountManagementPage,
	commerceAdminOrderDetailsPage,
	commerceChannelDefaultsPage,
	page,
	placedOrdersPage,
}) => {
	const userAccount = await apiHelpers.headlessAdminUser.postUserAccount();

	userData[userAccount.alternateName] = {
		name: userAccount.givenName,
		password: 'test',
		surname: userAccount.familyName,
	};

	const account = await apiHelpers.headlessAdminUser.postAccount({
		name: 'admin',
		type: 'business',
	});

	await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
		account.id,
		[userAccount.emailAddress]
	);

	const rolesResponse = await apiHelpers.headlessAdminUser.getAccountRoles(
		account.id
	);

	const accountRoleBuyer = rolesResponse?.items?.filter((role) => {
		return role.name === 'Buyer';
	});

	await apiHelpers.headlessAdminUser.assignAccountRoles(
		account.externalReferenceCode,
		accountRoleBuyer[0].id,
		userAccount.emailAddress
	);

	const {channel, site} = await miniumSetUp(apiHelpers);

	const siteRole =
		await apiHelpers.headlessAdminUser.getRoleByName('Site Member');

	await apiHelpers.headlessAdminUser.assignUserToSite(
		siteRole.id,
		site.id,
		userAccount.id
	);

	const address = await apiHelpers.headlessCommerceAdminAccount.postAddress(
		account.id,
		{phoneNumber: '12345', regionISOCode: 'AL'}
	);

	const product = await apiHelpers.headlessCommerceAdminCatalog.getProducts(
		new URLSearchParams({
			filter: `name eq 'U-Joint'`,
		})
	);

	const productId = product.items[0].productId;

	const productSkus = await apiHelpers.headlessCommerceAdminCatalog
		.getProduct(productId)
		.then((product) => {
			return product.skus;
		});
	const sku = productSkus[0];

	await apiHelpers.headlessCommerceAdminOrder.postOrder({
		accountId: account.id,
		billingAddressId: address.id,
		channelId: channel.id,
		orderItems: [
			{
				decimalQuantity: 10,
				quantity: 2,
				skuId: sku.id,
			},
		],
		orderStatus: '0',
		paymentMethod: 'paypal',
		paymentStatus: '0',
		shippingAddressId: address.id,
	});

	await applicationsMenuPage.goToAccounts();

	await commerceAccountManagementPage
		.accountsTableRowLink(account.id)
		.click();
	await commerceAccountManagementPage.channelDefaultsLink.click();

	await commerceChannelDefaultsPage.defaultCommerceCurrenciesButton.click();
	await commerceChannelDefaultsPage.editFrameCurrencySelect.selectOption(
		'Chinese Yuan Renminbi'
	);

	await commerceChannelDefaultsPage.editFrameSaveButton.click();

	await expect(page.getByText('Chinese Yuan Renminbi')).toBeVisible();

	await performLogout(page);
	await performLogin(page, userAccount.alternateName);

	await page.goto(`/web/${site.name}/placed-orders`);

	await placedOrdersPage.viewButton.click();

	await expect(commerceAdminOrderDetailsPage.reorderButton).toBeVisible();

	await commerceAdminOrderDetailsPage.reorderButton.click();

	await expect(commerceAdminOrderDetailsPage.reorderButton).toBeHidden();
	await expect(commerceAdminOrderDetailsPage.checkoutButton).toBeVisible();
	await expect(
		page
			.locator('.col-md-3 > .commerce-panel > div')
			.first()
			.filter({hasText: '¥'})
	).toBeVisible();
	await expect(
		page
			.locator('.col-md-3 > .commerce-panel > div:nth-child(2)')
			.filter({hasText: '¥'})
	).toBeVisible();
});

test('LPD-41398 Local date format', async ({
	apiHelpers,
	commerceInstanceSettingsPage,
	page,
	site,
}) => {
	let user;

	try {
		await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getWidgetDefinition({
					id: getRandomString(),
					widgetName:
						'com_liferay_site_navigation_language_web_portlet_SiteNavigationLanguagePortlet',
				}),
				getWidgetDefinition({
					id: getRandomString(),
					widgetName:
						'com_liferay_commerce_order_content_web_internal_portlet_CommerceOrderContentPortlet',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		await commerceInstanceSettingsPage.goToInstanceSetting(
			'Orders',
			'Placed Orders'
		);
		await commerceInstanceSettingsPage
			.checkboxPlacedOrders('Show Order Create Time')
			.uncheck();
		await commerceInstanceSettingsPage.submitConfigurationButton.click();

		await waitForAlert(page);

		await expect(
			commerceInstanceSettingsPage.checkboxPlacedOrders(
				'Show Order Create Time'
			)
		).not.toBeChecked();

		const account = await apiHelpers.headlessAdminUser.postAccount({
			type: 'person',
		});

		user = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[user.alternateName] = {
			name: user.givenName,
			password: 'test',
			surname: user.familyName,
		};

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account.id,
			[user.emailAddress]
		);

		const rolesResponse =
			await apiHelpers.headlessAdminUser.getAccountRoles(account.id);

		const buyerAccountRole = rolesResponse?.items?.filter((role) => {
			return role.name === 'Buyer';
		});

		await apiHelpers.headlessAdminUser.assignAccountRoles(
			account.externalReferenceCode,
			buyerAccountRole[0].id,
			user.emailAddress
		);

		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

		const product =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
			});
		const productSkus = await apiHelpers.headlessCommerceAdminCatalog
			.getProduct(product.productId)
			.then((product) => {
				return product.skus;
			});

		const sku = productSkus[0];

		const address =
			await apiHelpers.headlessCommerceAdminAccount.postAddress(
				account.id,
				{
					phoneNumber: '12345',
					regionISOCode: 'LA',
				}
			);

		const channel =
			await apiHelpers.headlessCommerceAdminChannel.postChannel({
				name: getRandomString(),
				siteGroupId: site.id,
			});

		const order = await apiHelpers.headlessCommerceAdminOrder.postOrder({
			accountId: account.id,
			billingAddressId: address.id,
			channelId: channel.id,
			orderItems: [
				{
					decimalQuantity: 10,
					quantity: 2,
					skuId: sku.id,
				},
			],
			orderStatus: '0',
			paymentMethod: 'paypal',
			paymentStatus: '0',
			shippingAddressId: address.id,
		});

		const siteRole =
			await apiHelpers.headlessAdminUser.getRoleByName('Site Member');

		await apiHelpers.headlessAdminUser.assignUserToSite(
			siteRole.id,
			site.id,
			user.id
		);

		await performLogout(page);

		await performLoginViaApi({page, screenName: user.alternateName});

		await page.goto(`hu/web/${site.name}`);

		await expect(
			page.getByText(
				getDateCustomFormat(
					order.createDate,
					await page.evaluate(() => {
						return Liferay.ThemeDisplay.getBCP47LanguageId();
					}),
					customFormatDateYY.DATE_AND_TIME
				)
			)
		).toBeVisible();

		await page.getByRole('link', {name: order.id.toString()}).click();

		await expect(
			page.getByText(
				getDateCustomFormat(
					order.createDate,
					await page.evaluate(() => {
						return Liferay.ThemeDisplay.getBCP47LanguageId();
					}),
					customFormatDateYY.DATE_AND_TIME
				)
			)
		).toBeVisible();

		await page.getByRole('link', {name: 'Vissza a teljes oldalra'}).click();

		await page.goto(`de/web/${site.name}`);

		await expect(
			page.getByText(
				getDateCustomFormat(
					order.createDate,
					await page.evaluate(() => {
						return Liferay.ThemeDisplay.getBCP47LanguageId();
					}),
					twoDigitFormatDate.DATE_AND_TIME
				).replace(/,(?=[^,]*$)/, '')
			)
		).toBeVisible();

		await page.getByRole('link', {name: order.id.toString()}).click();

		await expect(
			page.getByText(
				getDateCustomFormat(
					order.createDate,
					await page.evaluate(() => {
						return Liferay.ThemeDisplay.getBCP47LanguageId();
					}),
					twoDigitFormatDate.DATE_AND_TIME
				).replace(/,(?=[^,]*$)/, '')
			)
		).toBeVisible();

		await page.getByRole('link', {name: 'Zurück zur Seite'}).click();

		await page.goto(`en/web/${site.name}`);

		await expect(
			page.getByText(
				getDateCustomFormat(
					order.createDate,
					await page.evaluate(() => {
						return Liferay.ThemeDisplay.getBCP47LanguageId();
					}),
					customFormatDateYY.DATE_AND_TIME
				)
			)
		).toBeVisible();

		await page.getByRole('link', {name: order.id.toString()}).click();

		await expect(
			page.getByText(
				getDateCustomFormat(
					order.createDate,
					await page.evaluate(() => {
						return Liferay.ThemeDisplay.getBCP47LanguageId();
					}),
					customFormatDateYY.DATE_AND_TIME
				)
			)
		).toBeVisible();
	}
	finally {
		await page.goto('/en');

		await performLogout(page);

		await performLoginViaApi({page, screenName: 'test'});

		await commerceInstanceSettingsPage.goToInstanceSetting(
			'Orders',
			'Placed Orders'
		);
		await commerceInstanceSettingsPage
			.checkboxPlacedOrders('Show Order Create Time')
			.check();
		await commerceInstanceSettingsPage.submitConfigurationButton.click();

		await waitForAlert(page);

		await expect(
			commerceInstanceSettingsPage.checkboxPlacedOrders(
				'Show Order Create Time'
			)
		).toBeChecked();
	}
});

test(
	'Can sort orders by order date',
	{tag: ['@COMMERCE-10147', '@LPD-48664']},
	async ({apiHelpers, page, placedOrdersPage, site}) => {
		const account = await apiHelpers.headlessAdminUser.postAccount({
			type: 'person',
		});

		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[user.alternateName] = {
			name: user.givenName,
			password: 'test',
			surname: user.familyName,
		};

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account.id,
			[user.emailAddress]
		);

		const rolesResponse =
			await apiHelpers.headlessAdminUser.getAccountRoles(account.id);

		const buyerAccountRole = rolesResponse?.items?.filter((role) => {
			return role.name === 'Buyer';
		});

		await apiHelpers.headlessAdminUser.assignAccountRoles(
			account.externalReferenceCode,
			buyerAccountRole[0].id,
			user.emailAddress
		);

		await apiHelpers.jsonWebServicesUser.assignUsersToSite(
			site.id,
			user.id
		);

		await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getWidgetDefinition({
					id: getRandomString(),
					widgetName:
						'com_liferay_commerce_order_content_web_internal_portlet_CommerceOrderContentPortlet',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

		const product =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
			});
		const productSkus = await apiHelpers.headlessCommerceAdminCatalog
			.getProduct(product.productId)
			.then((product) => {
				return product.skus;
			});

		const sku = productSkus[0];

		const address =
			await apiHelpers.headlessCommerceAdminAccount.postAddress(
				account.id,
				{
					phoneNumber: '12345',
					regionISOCode: 'LA',
				}
			);

		const channel =
			await apiHelpers.headlessCommerceAdminChannel.postChannel({
				name: getRandomString(),
				siteGroupId: site.id,
			});

		const order1 = await apiHelpers.headlessCommerceAdminOrder.postOrder({
			accountId: account.id,
			billingAddressId: address.id,
			channelId: channel.id,
			orderItems: [
				{
					decimalQuantity: 10,
					quantity: 2,
					skuId: sku.id,
				},
			],
			orderStatus: '0',
			paymentMethod: 'paypal',
			paymentStatus: '0',
			shippingAddressId: address.id,
		});

		await page.waitForTimeout(2000);

		const order2 = await apiHelpers.headlessCommerceAdminOrder.postOrder({
			accountId: account.id,
			billingAddressId: address.id,
			channelId: channel.id,
			orderItems: [
				{
					decimalQuantity: 10,
					quantity: 2,
					skuId: sku.id,
				},
			],
			orderStatus: '0',
			paymentMethod: 'paypal',
			paymentStatus: '0',
			shippingAddressId: address.id,
		});

		await performLogout(page);
		await performLoginViaApi({page, screenName: user.alternateName});

		await page.goto(`web/${site.name}`);

		await expect(
			placedOrdersPage.orderCell(String(order1.id))
		).toBeVisible();
		await expect(
			placedOrdersPage.orderCell(String(order2.id))
		).toBeVisible();

		let date1 = await placedOrdersPage.orderColumn(1, 5).innerHTML();
		let date2 = await placedOrdersPage.orderColumn(2, 5).innerHTML();

		expect(new Date(date1).getTime()).toBeGreaterThanOrEqual(
			new Date(date2).getTime()
		);

		await expect(async () => {
			await placedOrdersPage.orderDateSortButton.click();

			date1 = await placedOrdersPage.orderColumn(1, 5).innerHTML();
			date2 = await placedOrdersPage.orderColumn(2, 5).innerHTML();

			expect(new Date(date1).getTime()).toBeLessThanOrEqual(
				new Date(date2).getTime()
			);
		}).toPass();
	}
);
