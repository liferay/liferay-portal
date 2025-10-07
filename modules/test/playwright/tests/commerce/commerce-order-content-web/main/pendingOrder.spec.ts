/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ObjectActionAPI} from '@liferay/object-admin-rest-client-js';
import {expect, mergeTests} from '@playwright/test';

import {applicationsMenuPageTest} from '../../../../fixtures/applicationsMenuPageTest';
import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {notificationPagesTest} from '../../../../fixtures/notificationPagesTest';
import {pageEditorPagesTest} from '../../../../fixtures/pageEditorPagesTest';
import {pageViewModePagesTest} from '../../../../fixtures/pageViewModePagesTest';
import {usersAndOrganizationsPagesTest} from '../../../../fixtures/usersAndOrganizationsPagesTest';
import {liferayConfig} from '../../../../liferay.config';
import {getRandomInt} from '../../../../utils/getRandomInt';
import getRandomString from '../../../../utils/getRandomString';
import performLogin, {
	performLoginViaApi,
	performLogout,
	userData,
} from '../../../../utils/performLogin';
import getPageDefinition from '../../../layout-content-page-editor-web/main/utils/getPageDefinition';
import getWidgetDefinition from '../../../layout-content-page-editor-web/main/utils/getWidgetDefinition';
import {miniumSetUp} from '../../utils/commerce';

export const test = mergeTests(
	applicationsMenuPageTest,
	commercePagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	notificationPagesTest,
	pageEditorPagesTest,
	pageViewModePagesTest,
	usersAndOrganizationsPagesTest
);

test(
	'Orders that have a subtotal less than the minimum order limit rule will trigger a warning',
	{tag: '@LPD-56179'},
	async ({
		apiHelpers,
		commerceAdminChannelsPage,
		page,
		pendingOrdersPage,
		site,
		widgetPagePage,
	}) => {
		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
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
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
				name: 'Edit pending order Catalog',
			});

		await apiHelpers.headlessCommerceAdminOrder.postOrderRule({
			type: 'minimum-order-amount',
			typeSettings:
				'minimum-order-amount-field-amount=' +
				'50.00' +
				'\nminimum-order-amount-field-apply-to=' +
				'minimum-order-amount-field-apply-to-order-total' +
				'\nminimum-order-amount-field-currency-code=' +
				'USD\n',
		});

		const account = await apiHelpers.headlessAdminUser.postAccount({
			name: getRandomString(),
			type: 'person',
		});

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account.id,
			['test@liferay.com']
		);

		const product1 =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				name: {en_US: 'Product1'},
				skus: [
					{
						cost: 0,
						price: 0,
						published: true,
						purchasable: true,
						sku: 'Sku' + getRandomInt(),
					},
				],
			});

		const product1Skus = await apiHelpers.headlessCommerceAdminCatalog
			.getProduct(product1.productId)
			.then((product) => {
				return product.skus;
			});

		const sku1 = product1Skus[0];

		const order = await apiHelpers.headlessCommerceDeliveryCart.postCart(
			{
				accountId: account.id,
				cartItems: [
					{
						options: '[]',
						quantity: 1,
						replacedSkuId: 0,
						skuId: sku1.id,
					},
				],
			},
			channel.id
		);

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

		await widgetPagePage.addPortlet('Open Carts');

		await pendingOrdersPage.viewButton.click();

		await expect(
			await page.getByText('The minimum order amount is')
		).toBeVisible();

		const product2 =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				name: {en_US: 'Product2'},
				skus: [
					{
						cost: 0,
						price: 50,
						published: true,
						purchasable: true,
						sku: 'Sku' + getRandomInt(),
					},
				],
			});

		const product2Skus = await apiHelpers.headlessCommerceAdminCatalog
			.getProduct(product2.productId)
			.then((product) => {
				return product.skus;
			});

		const sku2 = product2Skus[0];

		await apiHelpers.headlessCommerceDeliveryCart.patchCart(
			{
				accountId: account.id,
				cartItems: [
					{
						options: '[]',
						quantity: 1,
						replacedSkuId: 0,
						skuId: sku2.id,
					},
				],
			},
			order.id
		);

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

		await pendingOrdersPage.viewButton.click();

		await expect(
			await page.getByText('The minimum order amount is')
		).toBeHidden();
	}
);

test('LPD-13627 Edit pending order item with UOM', async ({
	apiHelpers,
	commerceAdminChannelsPage,
	page,
	pendingOrdersPage,
	site,
	widgetPagePage,
}) => {
	const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
		groupId: site.id,
		title: getRandomString(),
	});

	const channel = await apiHelpers.headlessCommerceAdminChannel.postChannel({
		siteGroupId: site.id,
	});

	await commerceAdminChannelsPage.changeCommerceChannelSiteType(
		channel.name,
		'B2C'
	);

	const catalog = await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
		name: 'Edit pending order Catalog',
	});

	const product1 = await apiHelpers.headlessCommerceAdminCatalog.postProduct({
		catalogId: catalog.id,
		name: {en_US: 'Product1'},
	});

	const sku1 = product1.skus[0];

	const uom1 =
		await apiHelpers.headlessCommerceAdminCatalog.postSkuUnitOfMeasure(
			sku1.id,
			{
				incrementalOrderQuantity: 3,
				name: {en_US: 'Box'},
				primary: true,
				priority: 1,
				rate: 1,
			}
		);

	const account = await apiHelpers.headlessAdminUser.postAccount({
		name: getRandomString(),
		type: 'person',
	});

	await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
		account.id,
		['test@liferay.com']
	);

	await apiHelpers.headlessCommerceDeliveryCart.postCart(
		{
			accountId: account.id,
			cartItems: [
				{
					options: '[]',
					quantity: 3,
					replacedSkuId: 0,
					skuId: sku1.id,
					skuUnitOfMeasure: {key: uom1.key},
				},
			],
		},
		channel.id
	);

	await page.waitForLoadState('networkidle');

	await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

	await widgetPagePage.addPortlet('Open Carts');

	await pendingOrdersPage.viewButton.click();

	await pendingOrdersPage.orderItemActionsButton.click();

	await expect(pendingOrdersPage.orderItemActionsButtonEdit).toBeVisible();
});

test('LPD-13627 Edit pending order item without UOM', async ({
	apiHelpers,
	commerceAdminChannelsPage,
	page,
	pendingOrdersPage,
	site,
	widgetPagePage,
}) => {
	const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
		groupId: site.id,
		title: getRandomString(),
	});

	const channel = await apiHelpers.headlessCommerceAdminChannel.postChannel({
		siteGroupId: site.id,
	});

	const catalog = await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
		name: 'Edit pending order Catalog',
	});

	await commerceAdminChannelsPage.changeCommerceChannelSiteType(
		channel.name,
		'B2C'
	);

	const product1 = await apiHelpers.headlessCommerceAdminCatalog.postProduct({
		catalogId: catalog.id,
		name: {en_US: 'Product1'},
	});

	const product1Skus = await apiHelpers.headlessCommerceAdminCatalog
		.getProduct(product1.productId)
		.then((product) => {
			return product.skus;
		});

	const sku1 = product1Skus[0];

	const account = await apiHelpers.headlessAdminUser.postAccount({
		name: getRandomString(),
		type: 'person',
	});

	await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
		account.id,
		['test@liferay.com']
	);

	await apiHelpers.headlessCommerceDeliveryCart.postCart(
		{
			accountId: account.id,
			cartItems: [
				{
					options: '[]',
					quantity: 1,
					replacedSkuId: 0,
					skuId: sku1.id,
				},
			],
		},
		channel.id
	);

	await page.waitForLoadState('networkidle');

	await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

	await widgetPagePage.addPortlet('Open Carts');

	await pendingOrdersPage.viewButton.click();

	await pendingOrdersPage.orderItemActionsButton.click();

	await expect(pendingOrdersPage.orderItemActionsButtonEdit).toHaveCount(0);
});

test('LPD-4174 Sales agent can receive email notifications for new orders placed to their accounts', async ({
	apiHelpers,
	applicationsMenuPage,
	checkoutPage,
	commerceMiniCartPage,
	page,
	queuePage,
}) => {
	test.setTimeout(180000);

	const {site} = await miniumSetUp(apiHelpers);

	const account = await apiHelpers.headlessAdminUser.postAccount({
		name: 'Sales agent can receive email notifications account',
		type: 'business',
	});

	await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
		account.id,
		['test@liferay.com']
	);

	const user =
		await apiHelpers.headlessAdminUser.getUserAccountByEmailAddress(
			'test@liferay.com'
		);

	const roles = await apiHelpers.headlessAdminUser.getRoles('Sales Agent');

	await apiHelpers.headlessAdminUser.postRoleUserAccountAssociation(
		roles.items[0].id,
		user.id
	);

	apiHelpers.data.push({
		id: `${roles.items[0].id}_${user.id}`,
		type: 'roleUserAccountAssociation',
	});

	const notificationTemplate =
		await apiHelpers.notification.postNotificationTemplate({
			editorType: 'richText',
			name: 'Sales agent can receive email notifications Template',
			recipientType: 'email',
			recipients: [
				{
					from: 'do-not-reply@liferay.com',
					fromName: {
						en_US: 'do-not-replay@liferay.com',
					},
					to: {
						en_US: '[%SALES_AGENT%]',
					},
					toType: 'email',
				},
			],
			subject: {
				en_US: 'Sales agent can receive email notifications',
			},
			type: 'email',
		});

	apiHelpers.data.push({
		id: notificationTemplate.id,
		type: 'notificationTemplate',
	});

	const objectActionAPIClient =
		await apiHelpers.buildRestClient(ObjectActionAPI);

	const {body: objectAction} =
		await objectActionAPIClient.postObjectDefinitionByExternalReferenceCodeObjectAction(
			'L_COMMERCE_ORDER',
			{
				active: true,
				label: {
					en_US: 'commerceOrderStatusOnChange',
				},
				name: 'commerceOrderStatusOnChange',
				objectActionExecutorKey: 'notification',
				objectActionTriggerKey: 'liferay/commerce_order_status',
				parameters: {
					notificationTemplateId: notificationTemplate.id,
				},
			}
		);

	apiHelpers.data.push({
		id: objectAction.id,
		type: 'objectAction',
	});

	await applicationsMenuPage.goToSite(site.name);

	try {
		await commerceMiniCartPage.miniCartButton.waitFor();
		await commerceMiniCartPage.miniCartButton.click();
		await commerceMiniCartPage.searchProductsInput.fill('MIN55861');
		await commerceMiniCartPage.quickAddToCartSku('MIN55861').click();
		await commerceMiniCartPage.quickAddToCartButton.click();
		await commerceMiniCartPage.submitButton.click();

		await checkoutPage.nameInput.fill('name');
		await checkoutPage.addressInput.fill('address');
		await checkoutPage.zipInput.fill('1234');
		await checkoutPage.phoneNumberInput.fill('1234');
		await checkoutPage.cityInput.fill('city');
		await checkoutPage.countryInput.selectOption({label: 'Italy'});
		await checkoutPage.continueButton.click();
		await checkoutPage.continueButton.click();
		await checkoutPage.continueButton.click();

		await expect(checkoutPage.orderSuccessMessage).toBeVisible();

		await applicationsMenuPage.goToQueue();

		await expect(queuePage.pageTitle).toBeVisible();
		await expect(
			page.getByText('Sales agent can receive email notifications')
		).toHaveCount(1);
	}
	finally {
		const orders =
			await apiHelpers.headlessCommerceAdminOrder.getOrdersPage();

		apiHelpers.data.push({id: orders.items[0].id, type: 'order'});

		const notificationQueueEntry =
			await apiHelpers.notification.getNotificationQueueEntriesPage(
				'Sales agent can receive email notifications'
			);

		apiHelpers.data.push({
			id: notificationQueueEntry.items[0].id,
			type: 'notificationQueueEntry',
		});
	}
});

test(
	'Order summary can generate a pdf report',
	{tag: '@LPD-13492'},
	async ({
		apiHelpers,
		commerceAdminChannelsPage,
		page,
		pendingOrdersPage,
		site,
		widgetPagePage,
	}) => {
		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
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
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
				name: 'Catalog',
			});

		const product =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				name: {en_US: 'Product'},
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

		await apiHelpers.headlessCommerceDeliveryCart.postCart(
			{
				accountId: account.id,
				cartItems: [
					{
						options: '[]',
						quantity: 1,
						replacedSkuId: 0,
						skuId: sku.id,
					},
				],
			},
			channel.id
		);

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

		await widgetPagePage.addPortlet('Open Carts');

		await pendingOrdersPage.viewButton.click();

		await pendingOrdersPage.orderItemActionsButton.click();

		await page
			.locator(
				"//div[contains(@class, 'dropdown')]/a[contains(@class, 'action') and contains(@class, 'btn-primary')]"
			)
			.click();
		await page.getByRole('menuitem', {name: 'Print'}).click();

		const downloadPromise = page.waitForEvent('download');

		const download = await downloadPromise;
		expect(download.suggestedFilename()).toEqual(
			layout.titleCurrentValue + '.pdf'
		);
	}
);

test('LPD-28683 When clicking on order item without visibility the user is not redirected to the catalog page', async ({
	apiHelpers,
	commerceMiniCartPage,
	commerceThemeMiniumPage,
	page,
	pendingOrdersPage,
}) => {
	const account = await apiHelpers.headlessAdminUser.postAccount({
		name: getRandomString(),
		type: 'business',
	});

	const {site} = await miniumSetUp(apiHelpers);

	const accountGroup = await apiHelpers.headlessAdminUser.postAccountGroup({
		name: 'AG1',
	});

	apiHelpers.data.push({id: accountGroup.id, type: 'accountGroup'});

	await apiHelpers.headlessAdminUser.assignAccountToAccountGroup(
		account.externalReferenceCode,
		accountGroup.externalReferenceCode
	);

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

	const accountRoleBuyer = rolesResponse?.items?.filter((role) => {
		return role.name === 'Buyer';
	});

	await apiHelpers.headlessAdminUser.assignAccountRoles(
		account.externalReferenceCode,
		accountRoleBuyer[0].id,
		user.emailAddress
	);

	const product = await apiHelpers.headlessCommerceAdminCatalog.getProducts(
		new URLSearchParams({
			filter: `name eq 'U-Joint'`,
			nestedFields: `productSkus`,
		})
	);

	await apiHelpers.headlessCommerceAdminCatalog.patchProduct(
		product.items[0].productId,
		{
			productAccountGroupFilter: true,
			productAccountGroups: [
				{
					accountGroupId: accountGroup.id,
				},
			],
		}
	);

	const productAccountGroups =
		await apiHelpers.headlessCommerceAdminCatalog.getProductAccountGroups(
			product.items[0].productId
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

	await commerceMiniCartPage.quickAddToCart(product.items[0].skuFormatted);

	await expect(
		await commerceMiniCartPage.priceField(
			'$ 24.00',
			commerceMiniCartPage.miniCartItemsContainer
		)
	).toBeVisible();

	await apiHelpers.headlessCommerceAdminCatalog.deleteProductAccountGroup(
		productAccountGroups.items[0].id
	);

	await commerceMiniCartPage.viewDetailsButton.click();

	await expect(
		page.getByText('One or more products are no longer available.')
	).toBeVisible();

	await pendingOrdersPage.errorMessageCloseButton.click();
	await pendingOrdersPage.skuLink(product.items[0].skuFormatted).click();

	await expect(
		await commerceThemeMiniumPage.goToMiniumLink(site.name)
	).toBeVisible();
});

test('LPD-26906 As a buyer, I can edit product options from the pending orders page', async ({
	apiHelpers,
	commerceAdminProductPage,
	commerceMiniCartPage,
	page,
	pendingOrdersPage,
}) => {
	test.setTimeout(90000);

	const account = await apiHelpers.headlessAdminUser.postAccount({
		type: 'business',
	});

	const {site} = await miniumSetUp(apiHelpers);

	const user =
		await apiHelpers.headlessAdminUser.getUserAccountByEmailAddress(
			'demo.unprivileged@liferay.com'
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
		user.emailAddress
	);
	const siteRole =
		await apiHelpers.headlessAdminUser.getRoleByName('Site Member');
	await apiHelpers.headlessAdminUser.assignUserToSite(
		siteRole.id,
		site.id,
		user.id
	);
	await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
		account.id,
		[user.emailAddress]
	);

	const option = await apiHelpers.headlessCommerceAdminCatalog.postOption(
		'select',
		getRandomString(),
		'Color',
		1
	);

	const catalog = await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

	const product1 = await apiHelpers.headlessCommerceAdminCatalog.postProduct({
		catalogId: catalog.id,
		name: {en_US: getRandomString()},
	});

	const product2 = await apiHelpers.headlessCommerceAdminCatalog.postProduct({
		catalogId: catalog.id,
		name: {en_US: getRandomString()},
	});

	const productBundleName = getRandomString();

	await apiHelpers.headlessCommerceAdminCatalog.postProduct({
		catalogId: catalog.id,
		name: {en_US: productBundleName},
		productOptions: [
			{
				fieldType: 'select',
				key: option.key,
				name: option.name,
				optionId: option.id,
				priceType: 'static',
				priority: 1,
				productOptionValues: [
					{
						deltaPrice: 10.0,
						key: 'black',
						name: {
							en_US: 'Black',
						},
						priority: 1,
						quantity: 1,
						skuId: product1.skus[0].id,
					},
					{
						deltaPrice: 20.0,
						key: 'white',
						name: {
							en_US: 'White',
						},
						priority: 2,
						quantity: 1,
						skuId: product2.skus[0].id,
					},
				],
				skuContributor: true,
			},
		],
	});

	await commerceAdminProductPage.gotoProduct(productBundleName);
	await commerceAdminProductPage.generateSkus();

	await expect(page.getByText('Showing 1 to 3 of 3 entries.')).toBeVisible();

	await performLogout(page);
	await performLoginViaApi({page, screenName: user.alternateName});

	await page.goto(
		`${liferayConfig.environment.baseUrl}/web${site.friendlyUrlPath}/catalog`,
		{waitUntil: 'networkidle'}
	);

	await commerceMiniCartPage.quickAddToCart('BLACK');
	await commerceMiniCartPage.quickAddToCart('MIN55858');
	await commerceMiniCartPage.quickAddToCart('MIN93016A');

	await pendingOrdersPage.layoutsPage.pendingOrdersLink.click();
	await page.getByLabel('View').click();

	await expect(page.getByText('$ 4.00').nth(1)).toBeVisible();
	await expect(page.getByText('$ 72.00').nth(1)).toBeVisible();
	await expect(page.getByText('$ 10.00').nth(1)).toBeVisible();

	await pendingOrdersPage.orderItemExpandButton(productBundleName).click();

	await expect(page.getByText('$ 10.00').nth(3)).toBeVisible();

	await (
		await pendingOrdersPage.orderItemsTableRowLink('Brake Fluid')
	).click();

	await pendingOrdersPage.editMenuItem.click();

	await commerceMiniCartPage.selectOption('48', 'Package Quantity');
	await commerceMiniCartPage.miniCartSaveButton.click();

	await expect(commerceMiniCartPage.miniCartSaveButton).toBeHidden();

	await page.waitForLoadState('load');

	await expect(
		await pendingOrdersPage.orderItemsTableRowLink(productBundleName)
	).toBeVisible();

	await (
		await pendingOrdersPage.orderItemsTableRowLink(productBundleName)
	).click();

	await pendingOrdersPage.editMenuItem.click();

	await commerceMiniCartPage.selectOption('White', 'Color');
	await commerceMiniCartPage.miniCartSaveButton.click();

	await expect(commerceMiniCartPage.miniCartSaveButton).toBeHidden();

	await page.waitForLoadState('load');

	await expect(
		await pendingOrdersPage.orderItemsTableRowLink('Wheel Seal - Front')
	).toBeVisible();

	await (
		await pendingOrdersPage.orderItemsTableRowLink('Wheel Seal - Front')
	).click();

	await expect(pendingOrdersPage.editMenuItem).toHaveCount(0);

	await expect(page.getByText('$ 4.00').nth(1)).toBeVisible();
	await expect(page.getByText('$ 72.00').nth(1)).toBeVisible();
	await expect(page.getByText('$ 20.00').nth(1)).toBeVisible();

	await pendingOrdersPage.table.click();

	await pendingOrdersPage.orderItemExpandButton(productBundleName).click();

	await expect(page.getByText('$ 20.00').nth(3)).toBeVisible();

	await commerceMiniCartPage.miniCartButton.click();

	await expect(
		commerceMiniCartPage.miniCartItemPrice(/^List Price\$ 4\.00$/)
	).toBeVisible();
	await expect(
		commerceMiniCartPage.miniCartItemPrice(
			/^List Price\$ 80\.00Promotion Price\$ 72\.00$/
		)
	).toBeVisible();
	await expect(
		commerceMiniCartPage.miniCartItemPrice(/^List Price\$ 20\.00$/)
	).toBeVisible();
});

test('LPD-3259 As a buyer with approval workflow, when I click review order in minicart, I get redirect to pending orders page', async ({
	apiHelpers,
	commerceAdminChannelDetailsPage,
	commerceAdminChannelsPage,
	commerceMiniCartPage,
	page,
	pendingOrdersPage,
}) => {
	const {site} = await miniumSetUp(apiHelpers);

	const account = await apiHelpers.headlessAdminUser.postAccount({
		name: getRandomString(),
		type: 'business',
	});

	const user =
		await apiHelpers.headlessAdminUser.getUserAccountByEmailAddress(
			'demo.unprivileged@liferay.com'
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
		user.emailAddress
	);
	await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
		account.id,
		['demo.unprivileged@liferay.com']
	);
	const siteRole =
		await apiHelpers.headlessAdminUser.getRoleByName('Site Member');
	await apiHelpers.headlessAdminUser.assignUserToSite(
		siteRole.id,
		site.id,
		user.id
	);

	const channels =
		await apiHelpers.headlessCommerceAdminChannel.getChannelsPage(
			`${site.name} Portal`
		);

	await commerceAdminChannelsPage.changeCommerceChannelBuyerOrderApprovalWorkflow(
		'Single Approver (Version 1)',
		channels.items[0].name
	);

	await (
		await commerceAdminChannelDetailsPage.commerceChannelHealthChecksTableRowAction(
			'Fix Issue',
			'Commerce Cart'
		)
	).click();

	const product = await apiHelpers.headlessCommerceAdminCatalog.getProducts(
		new URLSearchParams({
			filter: `name eq 'Abs Sensor'`,
		})
	);

	const productId = product.items[0].productId;

	const productSkus = await apiHelpers.headlessCommerceAdminCatalog
		.getProduct(productId)
		.then((product) => {
			return product.skus;
		});

	const sku = productSkus[0];

	await performLogout(page);
	await performLoginViaApi({page, screenName: 'demo.unprivileged'});

	await apiHelpers.headlessCommerceDeliveryCart.postCart(
		{
			accountId: account.id,
			cartItems: [
				{
					options: '[]',
					quantity: 1,
					replacedSkuId: 0,
					skuId: sku.id,
				},
			],
		},
		channels.items[0].id
	);

	await page.goto(`/web/${site.name}`, {waitUntil: 'networkidle'});

	await commerceMiniCartPage.miniCartButton.click();

	await expect(commerceMiniCartPage.reviewOrderButton).toBeVisible();

	await commerceMiniCartPage.reviewOrderButton.click();

	await expect(pendingOrdersPage.orderItemsTable).toBeVisible();
});

test('LPD-33783 Pending orders table displays correct fields', async ({
	apiHelpers,
	commerceAdminChannelsPage,
	page,
	pendingOrdersPage,
	site,
	widgetPagePage,
}) => {
	const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
		groupId: site.id,
		title: getRandomString(),
	});

	const channel = await apiHelpers.headlessCommerceAdminChannel.postChannel({
		siteGroupId: site.id,
	});

	await commerceAdminChannelsPage.changeCommerceChannelSiteType(
		channel.name,
		'B2C'
	);

	const account = await apiHelpers.headlessAdminUser.postAccount({
		name: getRandomString(),
		type: 'person',
	});

	await apiHelpers.headlessCommerceAdminOrder.postOrder({
		accountId: account.id,
		channelId: channel.id,
		name: 'order1',
		orderStatus: '2',
	});

	await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

	await widgetPagePage.addPortlet('Open Carts');

	await expect(pendingOrdersPage.orderItemsTable).toBeVisible();

	const tableHeaderLabels = [
		'Order ID',
		'Name',
		'Order Type',
		'ERC',
		'Purchase Order Number',
		'Create Date',
		'Account',
		'Created By',
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

test('LPD-3440 As a order manager with buyer approval workflow, I can approve orders on pending orders page', async ({
	apiHelpers,
	commerceAdminChannelDetailsPage,
	commerceAdminChannelsPage,
	commerceLayoutsPage,
	commerceMiniCartPage,
	page,
	pendingOrdersPage,
}) => {
	const {channel, site} = await miniumSetUp(apiHelpers);

	const account = await apiHelpers.headlessAdminUser.postAccount({
		name: getRandomString(),
		type: 'business',
	});

	const user =
		await apiHelpers.headlessAdminUser.getUserAccountByEmailAddress(
			'demo.unprivileged@liferay.com'
		);
	const rolesResponse = await apiHelpers.headlessAdminUser.getAccountRoles(
		account.id
	);

	const accountRoleOrderManager = rolesResponse?.items?.filter((role) => {
		return role.name === 'Order Manager';
	});

	await apiHelpers.headlessAdminUser.assignAccountRoles(
		account.externalReferenceCode,
		accountRoleOrderManager[0].id,
		user.emailAddress
	);
	await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
		account.id,
		['demo.unprivileged@liferay.com']
	);
	const siteRole =
		await apiHelpers.headlessAdminUser.getRoleByName('Site Member');
	await apiHelpers.headlessAdminUser.assignUserToSite(
		siteRole.id,
		site.id,
		user.id
	);

	await commerceAdminChannelsPage.changeCommerceChannelBuyerOrderApprovalWorkflow(
		'Single Approver (Version 1)',
		channel.name
	);

	await (
		await commerceAdminChannelDetailsPage.commerceChannelHealthChecksTableRowAction(
			'Fix Issue',
			'Commerce Cart'
		)
	).click();

	const product = await apiHelpers.headlessCommerceAdminCatalog.getProducts(
		new URLSearchParams({
			filter: `name eq 'Abs Sensor'`,
		})
	);

	const productId = product.items[0].productId;

	const productSkus = await apiHelpers.headlessCommerceAdminCatalog
		.getProduct(productId)
		.then((product) => {
			return product.skus;
		});

	const sku = productSkus[0];

	const phoneNumber = '12345';

	const address = await apiHelpers.headlessCommerceAdminAccount.postAddress(
		account.id,
		{phoneNumber, regionISOCode: 'AL'}
	);

	await apiHelpers.headlessCommerceDeliveryCart.postCart(
		{
			accountId: account.id,
			billingAddressId: address.id,
			cartItems: [
				{
					options: '[]',
					quantity: 1,
					replacedSkuId: 0,
					skuId: sku.id,
				},
			],
			shippingAddressId: address.id,
			shippingMethod: 'fixed',
		},
		channel.id
	);

	await page.goto(`/web/${site.name}`);

	await commerceMiniCartPage.miniCartButton.waitFor();
	await commerceMiniCartPage.miniCartButton.click();
	await commerceMiniCartPage.reviewOrderButton.waitFor();
	await commerceMiniCartPage.reviewOrderButton.click();
	await commerceMiniCartPage.submitButton.click();

	await expect(commerceMiniCartPage.submitButton).toBeHidden();

	await performLogout(page);

	await performLogin(page, 'demo.unprivileged');

	await page.goto(`/web/${site.name}`);

	await commerceLayoutsPage.pendingOrdersLink.click();

	await pendingOrdersPage.viewButton.click();
	await pendingOrdersPage.approveButton.click();
	await pendingOrdersPage.doneButton.click();

	await expect(pendingOrdersPage.checkoutButton).toBeVisible();
});

test(
	'Can sort orders by create date',
	{tag: ['@COMMERCE-10147', '@LPD-48664']},
	async ({
		apiHelpers,
		commerceAdminChannelsPage,
		page,
		pendingOrdersPage,
		site,
	}) => {
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
						'com_liferay_commerce_order_content_web_internal_portlet_CommerceOpenOrderContentPortlet',
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

		const channel =
			await apiHelpers.headlessCommerceAdminChannel.postChannel({
				siteGroupId: site.id,
			});

		await commerceAdminChannelsPage.changeCommerceChannelSiteType(
			channel.name,
			'B2C'
		);

		const order1 = await apiHelpers.headlessCommerceAdminOrder.postOrder({
			accountId: account.id,
			channelId: channel.id,
			orderItems: [
				{
					decimalQuantity: 10,
					quantity: 2,
					skuId: sku.id,
				},
			],
			orderStatus: '2',
		});

		await page.waitForTimeout(2000);

		const order2 = await apiHelpers.headlessCommerceAdminOrder.postOrder({
			accountId: account.id,
			channelId: channel.id,
			orderItems: [
				{
					decimalQuantity: 10,
					quantity: 2,
					skuId: sku.id,
				},
			],
			orderStatus: '2',
		});

		await performLogout(page);
		await performLoginViaApi({page, screenName: user.alternateName});

		await page.goto(`web/${site.name}`);

		await expect(
			pendingOrdersPage.orderCell(String(order1.id))
		).toBeVisible();
		await expect(
			pendingOrdersPage.orderCell(String(order2.id))
		).toBeVisible();

		let date1 = await pendingOrdersPage.orderColumn(1, 5).innerHTML();
		let date2 = await pendingOrdersPage.orderColumn(2, 5).innerHTML();

		expect(new Date(date1).getTime()).toBeGreaterThanOrEqual(
			new Date(date2).getTime()
		);

		await expect(async () => {
			await pendingOrdersPage.createDateSortButton.click();

			date1 = await pendingOrdersPage.orderColumn(1, 5).innerHTML();
			date2 = await pendingOrdersPage.orderColumn(2, 5).innerHTML();

			expect(new Date(date1).getTime()).toBeLessThanOrEqual(
				new Date(date2).getTime()
			);
		}).toPass();
	}
);
