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
import {liferayConfig} from '../../../../liferay.config';
import {getRandomInt} from '../../../../utils/getRandomInt';
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

	await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`, {
		waitUntil: 'networkidle',
	});

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

	await expect(placedOrdersPage.configurationMenuItem).toBeVisible();

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

test(
	'Orders with incomplete payments can retry payment when valid payment is enabled',
	{tag: ['@COMMERCE-9217', '@LPD-56275']},
	async ({
		apiHelpers,
		commerceAdminChannelDetailsPage,
		commerceAdminChannelsPage,
		page,
		placedOrderPage,
		placedOrdersPage,
		site,
	}) => {
		const layout = await apiHelpers.headlessDelivery.createSitePage({
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

		const channel =
			await apiHelpers.headlessCommerceAdminChannel.postChannel({
				siteGroupId: site.id,
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

		const account = await apiHelpers.headlessAdminUser.postAccount({
			name: getRandomString(),
			type: 'person',
		});

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account.id,
			['test@liferay.com']
		);

		const phoneNumber = '12345';

		const address =
			await apiHelpers.headlessCommerceAdminAccount.postAddress(
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
			orderStatus: '6',
			paymentMethod: 'paypal-integration',
			paymentStatus: '2',
			shippingAddressId: address.id,
		});

		await page.goto(
			`${liferayConfig.environment.baseUrl}/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`,
			{waitUntil: 'networkidle'}
		);

		await placedOrdersPage.viewButton.click();

		await commerceAdminChannelsPage.goto();
		await (
			await commerceAdminChannelsPage.channelsTableRowLink(channel.name)
		).click();
		await commerceAdminChannelDetailsPage.activateChannelConfiguration(
			'PayPal',
			'Payment Methods'
		);

		await page.goto(
			`${liferayConfig.environment.baseUrl}/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`,
			{waitUntil: 'networkidle'}
		);

		await placedOrdersPage.viewButton.click();

		await expect(placedOrderPage.retryPaymentButton).toBeVisible();

		await commerceAdminChannelsPage.goto();
		await (
			await commerceAdminChannelsPage.channelsTableRowLink(channel.name)
		).click();
		await commerceAdminChannelDetailsPage.deactivateChannelConfiguration(
			'PayPal',
			'Payment Methods'
		);

		await page.goto(
			`${liferayConfig.environment.baseUrl}/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`,
			{waitUntil: 'networkidle'}
		);

		await placedOrdersPage.viewButton.click();

		await expect(placedOrderPage.reorderButton).toBeVisible();

		await expect(placedOrderPage.retryPaymentButton).toBeHidden();
	}
);

test(
	'Bundled product are shown with option values in the placed order details',
	{tag: ['@COMMERCE-12610', '@LPD-39379']},
	async ({
		apiHelpers,
		applicationsMenuPage,
		commerceAdminChannelsPage,
		commerceAdminProductPage,
		page,
		placedOrderPage,
		placedOrdersPage,
		site,
	}) => {
		const layout = await apiHelpers.headlessDelivery.createSitePage({
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

		const channel =
			await apiHelpers.headlessCommerceAdminChannel.postChannel({
				siteGroupId: site.id,
			});
		await commerceAdminChannelsPage.changeCommerceChannelSiteType(
			channel.name,
			'B2C'
		);

		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

		const product1 =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
			});

		const productSkus1 = await apiHelpers.headlessCommerceAdminCatalog
			.getProduct(product1.productId)
			.then((product) => {
				return product.skus;
			});

		const sku1 = productSkus1[0];

		const product2 =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
			});

		const productSkus2 = await apiHelpers.headlessCommerceAdminCatalog
			.getProduct(product2.productId)
			.then((product) => {
				return product.skus;
			});

		const sku2 = productSkus2[0];

		const option1 =
			await apiHelpers.headlessCommerceAdminCatalog.postOption(
				'select',
				'color',
				'Color',
				1
			);
		const option2 =
			await apiHelpers.headlessCommerceAdminCatalog.postOption(
				'checkbox',
				'checkbox',
				'Checkbox',
				2
			);
		const option3 =
			await apiHelpers.headlessCommerceAdminCatalog.postOption(
				'date',
				'date',
				'Date',
				3
			);
		const option4 =
			await apiHelpers.headlessCommerceAdminCatalog.postOption(
				'numeric',
				'numeric',
				'Numeric',
				4
			);
		const option5 =
			await apiHelpers.headlessCommerceAdminCatalog.postOption(
				'text',
				'text',
				'Text',
				5
			);
		const option6 =
			await apiHelpers.headlessCommerceAdminCatalog.postOption(
				'checkbox_multiple',
				'checkbox_multiple',
				'checkbox_multiple',
				6
			);
		const option7 =
			await apiHelpers.headlessCommerceAdminCatalog.postOption(
				'radio',
				'radio',
				'Radio',
				7
			);

		let product = await apiHelpers.headlessCommerceAdminCatalog.postProduct(
			{
				catalogId: catalog.id,
				name: {en_US: 'ProductBundle'},
				productOptions: [
					{
						fieldType: 'select',
						key: 'color',
						name: {
							en_US: 'Color',
						},
						optionId: option1.id,
						priceType: 'static',
						priority: 1,
						productOptionValues: [
							{
								deltaPrice: 20.0,
								key: 'blue',
								name: {
									en_US: 'Blue',
								},
								priority: 1,
								quantity: 1,
								skuId: sku1.id,
							},
							{
								deltaPrice: 30.0,
								key: 'white',
								name: {
									en_US: 'White',
								},
								priority: 2,
								quantity: 1,
								skuId: sku2.id,
							},
						],
						skuContributor: true,
					},
					{
						fieldType: 'checkbox',
						key: 'checkbox',
						name: {
							en_US: 'Checkbox',
						},
						optionId: option2.id,
						priceType: 'static',
						priority: 2,
						skuContributor: false,
					},
					{
						fieldType: 'date',
						key: 'date',
						name: {
							en_US: 'Date',
						},
						optionId: option3.id,
						priceType: 'static',
						priority: 3,
						skuContributor: false,
					},
					{
						fieldType: 'numeric',
						key: 'numeric',
						name: {
							en_US: 'Numeric',
						},
						optionId: option4.id,
						priceType: 'static',
						priority: 4,
						skuContributor: false,
					},
					{
						fieldType: 'text',
						key: 'text',
						name: {
							en_US: 'Text',
						},
						optionId: option5.id,
						priceType: 'static',
						priority: 5,
						skuContributor: false,
					},
					{
						fieldType: 'checkbox_multiple',
						key: 'checkbox_multiple',
						name: {
							en_US: 'Checkbox Multiple',
						},
						optionId: option6.id,
						priceType: 'static',
						priority: 6,
						productOptionValues: [
							{
								key: 'value1',
								name: {
									en_US: 'value1',
								},
								priority: 1,
							},
							{
								key: 'value2',
								name: {
									en_US: 'value2',
								},
								priority: 2,
							},
						],
						skuContributor: false,
					},
					{
						fieldType: 'radio',
						key: 'radio',
						name: {
							en_US: 'Radio',
						},
						optionId: option7.id,
						priceType: 'static',
						priority: 7,
						productOptionValues: [
							{
								key: 'value1',
								name: {
									en_US: 'value1',
								},
								priority: 1,
							},
							{
								key: 'value2',
								name: {
									en_US: 'value2',
								},
								priority: 2,
							},
						],
						skuContributor: false,
					},
				],
			}
		);

		await applicationsMenuPage.goToProducts();

		await commerceAdminProductPage.managementToolbarSearchInput.fill(
			'ProductBundle'
		);
		await commerceAdminProductPage.managementToolbarSearchInput.press(
			'Enter'
		);
		await commerceAdminProductPage
			.productsTableRowLink('ProductBundle')
			.click();
		await commerceAdminProductPage.generateSkus();

		product = (
			await apiHelpers.headlessCommerceAdminCatalog.getProducts(
				new URLSearchParams({
					filter: `name eq '${product.name['en_US']}'`,
					nestedFields: `skus`,
				})
			)
		).items[0];

		const productSkus = product.skus.filter((sku) => {
			return ['BLUE'].includes(sku.sku);
		});

		const sku = productSkus[0];

		const warehouse =
			await apiHelpers.headlessCommerceAdminInventoryApiHelper.postWarehouses(
				{
					active: true,
					latitude: getRandomInt(),
					longitude: getRandomInt(),
					warehouseItems: [
						{
							quantity: 100,
							sku: sku.sku,
						},
					],
				}
			);

		await apiHelpers.headlessCommerceAdminInventoryApiHelper.postWarehousesChannels(
			warehouse.id,
			channel.id
		);

		const account = await apiHelpers.headlessAdminUser.postAccount({
			name: getRandomString(),
			type: 'person',
		});

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account.id,
			['test@liferay.com']
		);

		const address =
			await apiHelpers.headlessCommerceAdminAccount.postAddress(
				account.id,
				{phoneNumber: '12345', regionISOCode: 'AL'}
			);

		const postCart = await apiHelpers.headlessCommerceDeliveryCart.postCart(
			{
				accountId: account.id,
				billingAddressId: address.id,
				cartItems: [
					{
						options: `[{key: ${option2.key}, value: 'checkbox'}, {key: ${option3.key}, value: '10-23-2023'}, {key: ${option4.key}, value: '10'}, {key: ${option5.key}, value: 'Text Content'}, {key: ${option6.key}, value: ['value1', 'value2']}, {key: ${option7.key}, value: 'value2'}]`,
						quantity: 1,
						skuId: sku.id,
					},
				],
				currencyCode: 'USD',
				shippingAddressId: address.id,
			},
			channel.id
		);

		await apiHelpers.headlessCommerceDeliveryCart.checkoutCart(postCart.id);

		await page.goto(
			`${liferayConfig.environment.baseUrl}/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
		);

		await placedOrdersPage.viewButton.click();

		await expect(
			placedOrderPage.page.getByText(
				'Blue,\ncheckbox,\n10-23-2023, 10, Text Content, value1,\nvalue2,\nvalue2'
			)
		).toBeVisible();
	}
);

test('LPD-26643 Reorder from placed orders details page', async ({
	apiHelpers,
	checkoutPage,
	commerceAdminOrderDetailsPage,
	commerceMiniCartPage,
	page,
}) => {
	const account = await apiHelpers.headlessAdminUser.postAccount({
		name: getRandomString(),
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
	await performLoginViaApi({page, screenName: user.alternateName});

	await page.goto(`/web/${site.name}`, {waitUntil: 'networkidle'});

	await commerceMiniCartPage.miniCartButton.click();

	await expect(commerceMiniCartPage.miniCartItem('U-joint')).toBeVisible();

	await commerceMiniCartPage.submitButton.click();

	await checkoutPage.chooseShippingAddress({index: 1});

	await expect(page.getByText('Standard Delivery (+$ 15.00)')).toBeVisible();

	await checkoutPage.continueButton.click();

	await expect(page.getByText('U-joint')).toBeVisible();

	await checkoutPage.continueButton.click();

	await expect(checkoutPage.orderSuccessMessage).toBeVisible();

	await checkoutPage.goToOrderDetailsButton.click();

	await expect(page.getByText('U-joint')).toBeVisible();

	try {
		await commerceAdminOrderDetailsPage.reorderButton.click();

		await expect(commerceAdminOrderDetailsPage.reorderButton).toBeHidden();

		await commerceAdminOrderDetailsPage.checkoutButton.click();

		await expect(page.getByText('U-joint')).toBeVisible();

		await checkoutPage.chooseShippingAddress({index: 1});

		await expect(
			page.getByText('Standard Delivery (+$ 15.00)')
		).toBeVisible();

		await checkoutPage.continueButton.click();

		await expect(page.getByText('U-joint')).toBeVisible();

		await checkoutPage.continueButton.click();

		await expect(checkoutPage.orderSuccessMessage).toBeVisible();
	}
	finally {
		await performLogout(page);
		await performLoginViaApi({page, screenName: 'test'});

		const orders =
			await apiHelpers.headlessCommerceAdminOrder.getOrdersPage();

		if (orders && orders.items) {
			for (const order of orders.items) {
				await apiHelpers.headlessCommerceAdminOrder.deleteOrder(
					order.id
				);
			}
		}
	}
});

test('LPD-32095 A user can search orders by account name', async ({
	apiHelpers,
	commerceAdminChannelsPage,
	page,
	placedOrdersPage,
	site,
}) => {
	const layout = await apiHelpers.headlessDelivery.createSitePage({
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

	const userAccount = await apiHelpers.headlessAdminUser.postUserAccount();

	userData[userAccount.alternateName] = {
		name: userAccount.givenName,
		password: 'test',
		surname: userAccount.familyName,
	};

	const channel = await apiHelpers.headlessCommerceAdminChannel.postChannel({
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

	await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
		account2.id,
		[userAccount.emailAddress]
	);

	await page.goto(
		`${liferayConfig.environment.baseUrl}/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
	);

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
	await expect(
		(await placedOrdersPage.searchTableRowByValue(6, account2.name, true))
			.row
	).toBeVisible();
});

test(
	'A user can search orders by SKU and translated product name',
	{tag: '@LPD-56011'},
	async ({
		apiHelpers,
		commerceAdminChannelsPage,
		page,
		placedOrdersPage,
		site,
	}) => {
		const account = await apiHelpers.headlessAdminUser.postAccount({
			name: getRandomString(),
			type: 'business',
		});

		const userAccount =
			await apiHelpers.headlessAdminUser.postUserAccount();

		userData[userAccount.alternateName] = {
			name: userAccount.givenName,
			password: 'test',
			surname: userAccount.familyName,
		};

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account.id,
			[userAccount.emailAddress]
		);

		const rolesResponse =
			await apiHelpers.headlessAdminUser.getAccountRoles(account.id);

		const accountRoleBuyer = rolesResponse?.items?.filter((role) => {
			return role.name === 'Buyer';
		});

		await apiHelpers.headlessAdminUser.assignAccountRoles(
			account.externalReferenceCode,
			accountRoleBuyer[0].id,
			userAccount.emailAddress
		);

		const address =
			await apiHelpers.headlessCommerceAdminAccount.postAddress(
				account.id,
				{phoneNumber: '12345', regionISOCode: 'AL'}
			);

		const channel =
			await apiHelpers.headlessCommerceAdminChannel.postChannel({
				siteGroupId: site.id,
			});

		await commerceAdminChannelsPage.changeCommerceChannelSiteType(
			channel.name,
			'B2B'
		);

		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

		const product1 =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				name: {
					en_US: 'Red',
					es_ES: 'Roja',
				},
			});

		const productSkus1 = await apiHelpers.headlessCommerceAdminCatalog
			.getProduct(product1.productId)
			.then((product) => {
				return product.skus;
			});

		const sku1 = productSkus1[0];

		await apiHelpers.headlessCommerceAdminOrder.postOrder({
			accountId: account.id,
			billingAddressId: address.id,
			channelId: channel.id,
			orderItems: [
				{
					decimalQuantity: 10,
					quantity: 2,
					skuId: sku1.id,
				},
			],
			orderStatus: '0',
			paymentMethod: 'paypal',
			paymentStatus: '0',
			shippingAddressId: address.id,
		});

		const product2 =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				name: {
					en_US: 'Yellow',
					es_ES: 'Amarillo',
				},
			});

		const productSkus2 = await apiHelpers.headlessCommerceAdminCatalog
			.getProduct(product2.productId)
			.then((product) => {
				return product.skus;
			});

		const sku2 = productSkus2[0];

		await apiHelpers.headlessCommerceAdminOrder.postOrder({
			accountId: account.id,
			billingAddressId: address.id,
			channelId: channel.id,
			orderItems: [
				{
					decimalQuantity: 10,
					quantity: 2,
					skuId: sku2.id,
				},
			],
			orderStatus: '0',
			paymentMethod: 'paypal',
			paymentStatus: '0',
			shippingAddressId: address.id,
		});

		await apiHelpers.headlessCommerceAdminOrder.postOrder({
			accountId: account.id,
			billingAddressId: address.id,
			channelId: channel.id,
			orderItems: [
				{
					decimalQuantity: 10,
					quantity: 2,
					skuId: sku2.id,
				},
			],
			orderStatus: '0',
			paymentMethod: 'paypal',
			paymentStatus: '0',
			shippingAddressId: address.id,
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
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

		await page.goto(
			`${liferayConfig.environment.baseUrl}/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
		);

		await performLogout(page);
		await performLogin(page, userAccount.alternateName);

		await page.goto(`/web/${site.name}`);

		await expect(
			placedOrdersPage.orderAccountName(account.name)
		).toHaveCount(3);

		await placedOrdersPage.searchInput.fill(sku2.sku);
		await placedOrdersPage.searchButton.click();

		await expect(
			placedOrdersPage.orderAccountName(account.name),
			'Search orders by sku'
		).toHaveCount(2);

		await placedOrdersPage.searchInput.fill(product1.name['en_US']);
		await placedOrdersPage.searchButton.click();

		await expect(
			placedOrdersPage.orderAccountName(account.name),
			'Search orders by product name'
		).toHaveCount(1);

		await placedOrdersPage.searchInput.fill(product2.name['es_ES']);
		await placedOrdersPage.searchButton.click();

		await expect(
			placedOrdersPage.orderAccountName(account.name),
			'Search orders by translated product name'
		).toHaveCount(2);
	}
);

test('LPD-33783 Placed orders table displays correct fields', async ({
	apiHelpers,
	page,
	placedOrdersPage,
	site,
}) => {
	const layout = await apiHelpers.headlessDelivery.createSitePage({
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

	const channel = await apiHelpers.headlessCommerceAdminChannel.postChannel({
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

	await page.goto(
		`${liferayConfig.environment.baseUrl}/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`,
		{waitUntil: 'networkidle'}
	);

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
}) => {
	const layout = await apiHelpers.headlessDelivery.createSitePage({
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

	const channel = await apiHelpers.headlessCommerceAdminChannel.postChannel({
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

	await page.goto(
		`${liferayConfig.environment.baseUrl}/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
	);

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
}) => {
	await systemSettingsPage.goToSystemSetting('Orders', 'Placed Orders');

	try {
		if (!(await page.getByLabel('Show Order Create Time').isChecked())) {
			await page.getByLabel('Show Order Create Time').check();
			await page.getByTestId('submitConfiguration').click();
		}

		const layout = await apiHelpers.headlessDelivery.createSitePage({
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

		const channel =
			await apiHelpers.headlessCommerceAdminChannel.postChannel({
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

		await page.goto(
			`${liferayConfig.environment.baseUrl}/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
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
		name: getRandomString(),
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

	try {
		await commerceAdminOrderDetailsPage.reorderButton.click();

		await expect(commerceAdminOrderDetailsPage.reorderButton).toBeHidden();

		await expect(
			commerceAdminOrderDetailsPage.checkoutButton
		).toBeVisible();
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
	}
	finally {
		await performLogout(page);
		await performLoginViaApi({page, screenName: 'test'});

		const orders =
			await apiHelpers.headlessCommerceAdminOrder.getOrdersPage();

		if (orders && orders.items) {
			for (const order of orders.items) {
				await apiHelpers.headlessCommerceAdminOrder.deleteOrder(
					order.id
				);
			}
		}
	}
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
