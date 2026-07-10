/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ObjectActionAPI} from '@liferay/object-admin-rest-client-js';
import {expect, mergeTests} from '@playwright/test';

import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {displayPageTemplatesPagesTest} from '../../../../fixtures/displayPageTemplatesPagesTest';
import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {globalMenuPagesTest} from '../../../../fixtures/globalMenuPagesTest';
import {isolatedSiteTest} from '../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {notificationPagesTest} from '../../../../fixtures/notificationPagesTest';
import {pageEditorPagesTest} from '../../../../fixtures/pageEditorPagesTest';
import {pageViewModePagesTest} from '../../../../fixtures/pageViewModePagesTest';
import {usersAndOrganizationsPagesTest} from '../../../../fixtures/usersAndOrganizationsPagesTest';
import {liferayConfig} from '../../../../liferay.config';
import {getRandomInt} from '../../../../utils/getRandomInt';
import getRandomString from '../../../../utils/getRandomString';
import {
	performLoginViaApi,
	performLogout,
	userData,
} from '../../../../utils/performLogin';
import {waitForAlert} from '../../../../utils/waitForAlert';
import getFragmentDefinition from '../../../layout-content-page-editor-web/main/utils/getFragmentDefinition';
import getPageDefinition from '../../../layout-content-page-editor-web/main/utils/getPageDefinition';
import getWidgetDefinition from '../../../layout-content-page-editor-web/main/utils/getWidgetDefinition';
import {
	configureBuyerUserForSite,
	configureOperationsManagerUserForSite,
	configureOrderManagerUserForSite,
	miniumSetUp,
} from '../../utils/commerce';

export const test = mergeTests(
	globalMenuPagesTest,
	commercePagesTest,
	dataApiHelpersTest,
	displayPageTemplatesPagesTest,
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

test(
	'Edit pending order item with UOM',
	{tag: '@LPD-13627'},
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
				name: getRandomString(),
			});

		const product1 =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				name: {en_US: getRandomString()},
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

		await (
			await pendingOrdersPage.orderItemsTableRowLink(
				product1.name['en_US']
			)
		).click();

		await expect(
			pendingOrdersPage.orderItemActionsButtonEdit
		).toBeVisible();
	}
);

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
	checkoutPage,
	commerceMiniCartPage,
	globalMenuPage,
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

	await globalMenuPage.goToSite(site.name);

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

		await globalMenuPage.goToControlPanel('Queue');

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

test(
	'When clicking on order item without visibility the user is not redirected to the catalog page',
	{tag: '@LPD-28683'},
	async ({
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

		const accountGroup =
			await apiHelpers.headlessAdminUser.postAccountGroup({
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

		const rolesResponse =
			await apiHelpers.headlessAdminUser.getAccountRoles(account.id);

		const accountRoleBuyer = rolesResponse?.items?.filter((role) => {
			return role.name === 'Buyer';
		});

		await apiHelpers.headlessAdminUser.assignAccountRoles(
			account.externalReferenceCode,
			accountRoleBuyer[0].id,
			user.emailAddress
		);

		const product =
			await apiHelpers.headlessCommerceAdminCatalog.getProducts(
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

		await commerceMiniCartPage.quickAddToCart(
			product.items[0].skuFormatted
		);

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
	}
);

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

test(
	'As a order manager with buyer approval workflow, I can manage order workflow status',
	{tag: ['@LPD-3440', '@LPD-70123']},
	async ({
		apiHelpers,
		commerceAdminChannelsPage,
		commerceLayoutsPage,
		commerceMiniCartPage,
		commerceThemeMiniumCatalogPage,
		page,
		pendingOrdersPage,
	}) => {
		let account;
		let channel;
		let orderManagerUser;
		let orderId;
		let productName;
		let site;

		await test.step('Create a Commerce Minium site', async () => {
			const {channel: channelSetup, site: siteSetup} =
				await miniumSetUp(apiHelpers);

			channel = channelSetup;
			site = siteSetup;
		});

		await test.step('Create a business account, a buyer user and an order manager user and assign them to the site', async () => {
			account = await apiHelpers.headlessAdminUser.postAccount({
				name: getRandomString(),
				type: 'business',
			});

			await configureBuyerUserForSite(
				account,
				apiHelpers,
				site,
				'demo.unprivileged@liferay.com'
			);

			const rolesResponse =
				await apiHelpers.headlessAdminUser.getAccountRoles(account.id);
			const accountRoleOrderManager = rolesResponse?.items?.filter(
				(role) => {
					return role.name === 'Order Manager';
				}
			);

			orderManagerUser =
				await apiHelpers.headlessAdminUser.postUserAccount();

			userData[orderManagerUser.alternateName] = {
				name: orderManagerUser.givenName,
				password: 'test',
				surname: orderManagerUser.familyName,
			};

			await apiHelpers.headlessAdminUser.assignAccountRoles(
				account.externalReferenceCode,
				accountRoleOrderManager[0].id,
				orderManagerUser.emailAddress
			);

			await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
				account.id,
				[orderManagerUser.emailAddress]
			);

			const siteRole =
				await apiHelpers.headlessAdminUser.getRoleByName('Site Member');
			await apiHelpers.headlessAdminUser.assignUserToSite(
				siteRole.id,
				site.id,
				orderManagerUser.id
			);
		});

		await test.step('Change commerce channel buyer order approval workflow and create an order', async () => {
			await commerceAdminChannelsPage.changeCommerceChannelBuyerOrderApprovalWorkflow(
				'Single Approver (Version 1)',
				channel.name
			);

			const product = (
				await apiHelpers.headlessCommerceAdminCatalog.getProducts(
					new URLSearchParams({
						filter: `name eq 'U-Joint'`,
					})
				)
			).items[0];

			productName = product.name['en_US'];
		});

		await test.step('As a Buyer submit the order', async () => {
			await performLogout(page);
			await performLoginViaApi({
				page,
				screenName: 'demo.unprivileged',
			});

			await page.goto(`/web/${site.name}`, {
				waitUntil: 'networkidle',
			});

			await commerceThemeMiniumCatalogPage
				.productCardAddToCartButton(productName)
				.click();

			await expect(commerceMiniCartPage.miniCartButton).toHaveClass(
				'has-badge mini-cart-opener'
			);

			orderId = await commerceThemeMiniumCatalogPage.accountSelectorButton
				.locator('.order-id')
				.textContent();

			apiHelpers.data.push({id: Number(orderId), type: 'order'});

			await expect(
				commerceThemeMiniumCatalogPage.accountSelectorOrderWorkflowStatus
			).toContainText('Draft');

			await commerceMiniCartPage.miniCartButton.click();

			await expect(
				commerceMiniCartPage.miniCartItem(productName)
			).toBeVisible();

			await commerceMiniCartPage.reviewOrderButton.click();

			await expect(
				await pendingOrdersPage.orderItemsTableRowLink(productName)
			).toBeVisible();

			await commerceMiniCartPage.submitButton.click();

			await expect(
				commerceThemeMiniumCatalogPage.accountSelectorOrderWorkflowStatus
			).toContainText('Pending');
		});

		await test.step('As a Order Manager reject the order', async () => {
			await performLogout(page);
			await performLoginViaApi({
				page,
				screenName: orderManagerUser.alternateName,
			});

			await page.goto(`/web/${site.name}`, {
				waitUntil: 'networkidle',
			});

			await commerceLayoutsPage.pendingOrdersLink.click();

			await pendingOrdersPage.viewButton.click();

			await expect(
				await pendingOrdersPage.orderItemsTableRowLink(productName)
			).toBeVisible();

			await expect(pendingOrdersPage.saveButton).toBeVisible();
			await expect(pendingOrdersPage.approveButton).toBeVisible();
			await expect(pendingOrdersPage.rejectButton).toBeVisible();

			await pendingOrdersPage.rejectButton.click();
			await pendingOrdersPage.doneButton.click();

			await expect(
				commerceThemeMiniumCatalogPage.accountSelectorOrderWorkflowStatus
			).toContainText('Pending');
		});

		await test.step('As a Buyer re-submit the order', async () => {
			await performLogout(page);
			await performLoginViaApi({
				page,
				screenName: 'demo.unprivileged',
			});

			await page.goto(`/web/${site.name}/pending-orders`, {
				waitUntil: 'networkidle',
			});

			await pendingOrdersPage.viewButton.click();

			await expect(
				await pendingOrdersPage.orderItemsTableRowLink(productName)
			).toBeVisible();

			await commerceMiniCartPage.resubmitButton.click();
			await pendingOrdersPage.doneButton.click();

			await expect(
				commerceThemeMiniumCatalogPage.accountSelectorOrderWorkflowStatus
			).toContainText('Pending');
		});

		await test.step('As a Order Manager approve the order', async () => {
			await performLogout(page);
			await performLoginViaApi({
				page,
				screenName: orderManagerUser.alternateName,
			});

			await page.goto(`/web/${site.name}`, {
				waitUntil: 'networkidle',
			});

			await commerceLayoutsPage.pendingOrdersLink.click();

			await pendingOrdersPage.viewButton.click();

			await expect(
				await pendingOrdersPage.orderItemsTableRowLink(productName)
			).toBeVisible();

			await expect(pendingOrdersPage.saveButton).toBeVisible();
			await expect(pendingOrdersPage.approveButton).toBeVisible();
			await expect(pendingOrdersPage.rejectButton).toBeVisible();

			await pendingOrdersPage.approveButton.click();
			await pendingOrdersPage.doneButton.click();

			await expect(
				commerceThemeMiniumCatalogPage.accountSelectorOrderWorkflowStatus
			).toContainText('Approved');
		});
	}
);

test(
	'Creating new orders with specific order rules and order types will trigger correct warnings',
	{tag: '@LPD-71058'},
	async ({
		apiHelpers,
		commerceAdminChannelsPage,
		commerceLayoutsPage,
		page,
		pendingOrdersPage,
		site,
	}) => {
		await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getFragmentDefinition({
					id: getRandomString(),
					key: 'COMMERCE_ACCOUNT_FRAGMENTS-account-selector',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
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

		const channel =
			await apiHelpers.headlessCommerceAdminChannel.postChannel({
				siteGroupId: site.id,
			});

		await commerceAdminChannelsPage.changeCommerceChannelSiteType(
			channel.name,
			'B2B'
		);

		const orderRuleOrderType1 =
			await pendingOrdersPage.addOrderRuleOrderType(apiHelpers, 50);
		const orderRuleOrderType2 =
			await pendingOrdersPage.addOrderRuleOrderType(apiHelpers, 100);

		const account = await apiHelpers.headlessAdminUser.postAccount({
			name: getRandomString(),
			type: 'business',
		});

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account.id,
			['test@liferay.com']
		);

		await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

		await page.waitForLoadState('networkidle');

		await commerceLayoutsPage.addOrderButton.click();
		await commerceLayoutsPage.orderTypeModalInput.selectOption({
			label: orderRuleOrderType1.orderType.name['en_US'],
		});
		await commerceLayoutsPage.orderTypeModalButton.click();

		await expect(
			await page.getByText('The minimum order amount is $ 50.00.')
		).toBeVisible();

		await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

		await page.waitForLoadState('networkidle');

		await commerceLayoutsPage.addOrderButton.click();
		await commerceLayoutsPage.orderTypeModalInput.selectOption({
			label: orderRuleOrderType2.orderType.name['en_US'],
		});
		await commerceLayoutsPage.orderTypeModalButton.click();

		await expect(
			await page.getByText('The minimum order amount is $ 100.00.')
		).toBeVisible();
	}
);

test(
	'Request Quote in the mini cart should redirect to Orders page and allow quote processing',
	{tag: ['@COMMERCE-11507', '@LPD-70003']},
	async ({
		apiHelpers,
		commerceAdminChannelDetailsPage,
		commerceAdminChannelsPage,
		commerceAdminOrdersPage,
		commerceCartSummaryPage,
		commerceLayoutsPage,
		commerceMiniCartPage,
		displayPageTemplatesPage,
		page,
		pageEditorPage,
		site,
	}) => {
		let account;
		let buyerUser;
		let cart;
		let channel;
		let operationsManagerUser;
		let product;

		await test.step('Create an account, channel and catalog', async () => {
			account = await apiHelpers.headlessAdminUser.postAccount({
				name: getRandomString(),
				type: 'business',
			});

			buyerUser = await configureBuyerUserForSite(
				account,
				apiHelpers,
				site,
				'demo.unprivileged@liferay.com'
			);

			const companyId = await page.evaluate(() => {
				return Liferay.ThemeDisplay.getCompanyId();
			});
			operationsManagerUser = await configureOperationsManagerUserForSite(
				account,
				apiHelpers,
				companyId,
				site,
				[
					{
						actionIds: ['MANAGE_QUOTES'],
						primaryKey: companyId,
						resourceName: 'com.liferay.commerce.order',
						scope: 1,
					},
				]
			);

			channel = await apiHelpers.headlessCommerceAdminChannel.postChannel(
				{
					siteGroupId: site.id,
				}
			);

			await commerceAdminChannelsPage.changeCommerceChannelSiteType(
				channel.name,
				'B2B'
			);

			await commerceAdminChannelDetailsPage.allowRequestAQuote.click();

			await commerceAdminChannelDetailsPage.saveButton.click();

			const catalog =
				await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
					name: 'Catalog_' + getRandomString(),
				});

			product = await apiHelpers.headlessCommerceAdminCatalog.postProduct(
				{
					catalogId: catalog.id,
					name: {en_US: 'Product1'},
				}
			);

			const basePriceListId =
				await apiHelpers.headlessCommerceAdminPricing.getBasePriceListId(
					catalog.id
				);

			await apiHelpers.headlessCommerceAdminPricing.postPriceEntry({
				price: 15,
				priceListId: basePriceListId.items[0].id,
				priceOnApplication: true,
				skuId: product.skus[0].id,
			});
		});

		await test.step('Setup Product and Order Details pages', async () => {
			await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([
					getWidgetDefinition({
						id: getRandomString(),
						widgetName:
							'com_liferay_commerce_product_content_web_internal_portlet_CPContentPortlet',
					}),
					getFragmentDefinition({
						id: getRandomString(),
						key: 'COMMERCE_CART_FRAGMENTS-mini-cart',
					}),
				]),
				siteId: site.id,
				title: getRandomString(),
			});

			await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([
					getWidgetDefinition({
						id: getRandomString(),
						widgetName:
							'com_liferay_commerce_checkout_web_internal_portlet_CommerceCheckoutPortlet',
					}),
				]),
				siteId: site.id,
				title: getRandomString(),
			});

			await displayPageTemplatesPage.goto(site.friendlyUrlPath);

			const displayPageTemplateName = getRandomString();

			await displayPageTemplatesPage.createTemplate({
				contentType: 'Order',
				name: displayPageTemplateName,
			});

			await displayPageTemplatesPage.editTemplate(
				displayPageTemplateName
			);

			await pageEditorPage.addFragment('Order', 'Order Actions');

			await expect(
				page.getByText(
					'The order actions component will be shown here.'
				)
			).toBeVisible();

			await pageEditorPage.addFragment('Order', 'Order Status Label');

			await expect(
				page.getByText(
					'The order status label component will appear here.'
				)
			).toBeVisible();

			await pageEditorPage.waitForChangesSaved();

			await displayPageTemplatesPage.publishTemplate();
			await displayPageTemplatesPage.clickMoreActions(
				displayPageTemplateName,
				'Mark as Default'
			);

			await expect(
				commerceLayoutsPage.defaultDisplayPageTemplateIcon
			).toBeVisible();
		});

		await test.step('As a Buyer, create a new quote from Order Details page', async () => {
			await performLogout(page);
			await performLoginViaApi({
				page,
				screenName: buyerUser.alternateName,
			});

			await page.goto(`/web/${site.name}/p/${product.name['en_US']}`, {
				waitUntil: 'networkidle',
			});

			cart = await apiHelpers.headlessCommerceDeliveryCart.postCart(
				{
					accountId: account.id,
					cartItems: [
						{
							quantity: 1,
							skuId: product.skus[0].id,
						},
					],
					currencyCode: 'USD',
				},
				channel.id
			);

			await page.reload();

			await expect(commerceMiniCartPage.miniCartButton).toHaveClass(
				'has-badge mini-cart-opener'
			);

			await commerceMiniCartPage.miniCartButton.click();
			await commerceMiniCartPage.requestAQuoteButton.click();
			await commerceCartSummaryPage.requestAQuoteButton.click();
			await commerceCartSummaryPage.requestAQuoteModal.isVisible();

			await expect(page.getByPlaceholder('Email Address')).toHaveValue(
				buyerUser.emailAddress
			);

			await commerceCartSummaryPage.requestAQuoteModalSubmit.click();

			await expect(
				page.getByText('Quote Requested', {exact: true})
			).toBeVisible();
		});

		await test.step('As an Operations Manager, process a quote', async () => {
			await performLogout(page);
			await performLoginViaApi({
				page,
				screenName: operationsManagerUser.alternateName,
			});

			await commerceAdminOrdersPage.goto();

			const orderLink = await commerceAdminOrdersPage.tableRowLink({
				colIndex: 1,
				rowValue: cart.id,
			});
			await expect(orderLink).toBeVisible();
			await orderLink.click();

			await expect(
				commerceAdminOrdersPage.quoteProcessedButton
			).toBeVisible();

			await commerceAdminOrdersPage.quoteProcessedButton.click();

			const quoteStep = page.locator('.step-tracker .step:last-child');

			expect(await quoteStep.textContent()).toEqual('Quote Processed');
			await expect(quoteStep).toHaveClass('step completed');
		});

		try {
			await test.step('As a Buyer, create a new order from the newly processed quote', async () => {
				await performLogout(page);
				await performLoginViaApi({
					page,
					screenName: buyerUser.alternateName,
				});

				await page.goto(`/web/${site.name}/order/${cart.id}`, {
					waitUntil: 'networkidle',
				});

				await expect(
					commerceLayoutsPage.orderActionsButton('Reorder')
				).toBeVisible();
				await commerceLayoutsPage.orderActionsButton('Reorder').click();

				await expect(page).not.toHaveURL(`order/${cart.id}`);

				await expect(
					commerceLayoutsPage.orderActionsButton('Request a Quote')
				).toBeVisible();
			});
		}
		finally {
			await performLogout(page);
			await performLoginViaApi({page, screenName: 'test'});

			const orders =
				await apiHelpers.headlessCommerceAdminOrder.getOrdersPage();

			if (orders && orders.items) {
				for (const order of orders.items) {
					apiHelpers.data.push({
						id: order.id,
						type: 'order',
					});
				}
			}
		}
	}
);

test(
	'User notification is sent when an order note is added',
	{tag: ['@LPD-77313', '@LPD-77315']},
	async ({
		apiHelpers,
		commerceAdminChannelsPage,
		commerceLayoutsPage,
		displayPageTemplatesPage,
		page,
		pageEditorPage,
		site,
		userPersonalBarPage,
	}) => {
		let orderId;
		let orderNote;

		const account = await apiHelpers.headlessAdminUser.postAccount({
			name: getRandomString(),
			type: 'business',
		});

		const buyerUser1 = await configureBuyerUserForSite(
			account,
			apiHelpers,
			site,
			'demo.unprivileged@liferay.com'
		);

		const user1 = await apiHelpers.headlessAdminUser.postUserAccount();
		const user2 = await apiHelpers.headlessAdminUser.postUserAccount();

		const buyerUser2 = await configureBuyerUserForSite(
			account,
			apiHelpers,
			site,
			user1.emailAddress
		);

		const orderManagerUser = await configureOrderManagerUserForSite(
			account,
			apiHelpers,
			true,
			site,
			user2.emailAddress
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
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
				name: 'Catalog_' + getRandomString(),
			});
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

		await test.step('Create an object action using a user notification template', async () => {
			const notificationTemplate =
				await apiHelpers.notification.postNotificationTemplate({
					editorType: 'richText',
					name: 'Commerce Order Note Template',
					recipientType: 'term',
					recipients: [
						{
							term: '[%COMMERCEORDERNOTE_RECIPIENT_IDS%]',
						},
					],
					subject: {
						en_US: '[%COMMERCEORDERNOTE_ORDERID%]',
					},
					type: 'userNotification',
				});

			apiHelpers.data.push({
				id: notificationTemplate.id,
				type: 'notificationTemplate',
			});

			const objectActionAPIClient =
				await apiHelpers.buildRestClient(ObjectActionAPI);

			const {body: objectAction} =
				await objectActionAPIClient.postObjectDefinitionByExternalReferenceCodeObjectAction(
					'L_COMMERCE_ORDER_NOTE',
					{
						active: true,
						label: {
							en_US: 'commerceOrderNoteOnAfterAdd',
						},
						name: 'commerceOrderNoteOnAfterAdd',
						objectActionExecutorKey: 'notification',
						objectActionTriggerKey: 'onAfterAdd',
						parameters: {
							notificationTemplateId: notificationTemplate.id,
							type: 'userNotification',
						},
					}
				);

			apiHelpers.data.push({
				id: objectAction.id,
				type: 'objectAction',
			});
		});

		await test.step('Create an order display page template and a default layout', async () => {
			await displayPageTemplatesPage.goto(site.friendlyUrlPath);

			const displayPageTemplateName = getRandomString();

			await displayPageTemplatesPage.createTemplate({
				contentType: 'Order',
				name: displayPageTemplateName,
			});

			await displayPageTemplatesPage.editTemplate(
				displayPageTemplateName
			);

			await pageEditorPage.addFragment('Basic Components', 'Heading');
			await pageEditorPage.waitForChangesSaved();

			await displayPageTemplatesPage.publishTemplate();
			await displayPageTemplatesPage.clickMoreActions(
				displayPageTemplateName,
				'Mark as Default'
			);

			await expect(
				commerceLayoutsPage.defaultDisplayPageTemplateIcon
			).toBeVisible();

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
		});

		await test.step('Create an order', async () => {
			const address =
				await apiHelpers.headlessCommerceAdminAccount.postAddress(
					account.id,
					{phoneNumber: '12345', regionISOCode: 'AL'}
				);

			const order = await apiHelpers.headlessCommerceAdminOrder.postOrder(
				{
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
				}
			);

			orderId = order.id;
		});

		await test.step('Add an order note', async () => {
			orderNote =
				await apiHelpers.headlessCommerceAdminOrder.postOrderIdOrderNote(
					orderId,
					{restricted: false}
				);
		});

		await test.step('As a Buyer1, verify that the notification is delivered', async () => {
			await performLogout(page);

			await performLoginViaApi({
				page,
				screenName: buyerUser1.alternateName,
			});

			await userPersonalBarPage.notificationBadge.click();

			await expect(page.getByText(orderId)).toHaveCount(1);
		});

		await test.step('As a Buyer2, verify that the notification is delivered', async () => {
			userData[buyerUser2.alternateName] = {
				name: buyerUser2.givenName,
				password: 'test',
				surname: buyerUser2.familyName,
			};

			await performLogout(page);

			await performLoginViaApi({
				page,
				screenName: buyerUser2.alternateName,
			});

			await userPersonalBarPage.notificationBadge.click();

			await expect(page.getByText(orderId)).toHaveCount(1);
		});

		await test.step('As a Buyer2, add an order with a note', async () => {
			const cart = await apiHelpers.headlessCommerceDeliveryCart.postCart(
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

			orderId = cart.id;

			orderNote =
				await apiHelpers.headlessCommerceDeliveryCart.postCartIdCartComment(
					{restricted: false},
					cart.id
				);
		});

		await test.step('As an Order Manager, verify that the notification is delivered', async () => {
			userData[orderManagerUser.alternateName] = {
				name: orderManagerUser.givenName,
				password: 'test',
				surname: orderManagerUser.familyName,
			};

			await performLogout(page);

			await performLoginViaApi({
				page,
				screenName: orderManagerUser.alternateName,
			});

			await userPersonalBarPage.notificationBadge.click();

			await expect(page.getByText(orderId)).toHaveCount(1);
		});

		await test.step('As an Order Administrator, click on the notification to be redirected to control panel', async () => {
			await page.getByText(orderId).click();

			await expect(page.getByText(orderNote.content)).toHaveCount(1);
		});

		await test.step('As an Order Manager, add a restricted note to the order created by Buyer2', async () => {
			orderNote =
				await apiHelpers.headlessCommerceAdminOrder.postOrderIdOrderNote(
					orderId,
					{restricted: true}
				);
		});

		await test.step('As a Buyer1, verify that the notification for the restricted note is not delivered', async () => {
			await performLogout(page);

			await performLoginViaApi({
				page,
				screenName: buyerUser1.alternateName,
			});

			await userPersonalBarPage.notificationBadge.click();

			await expect(page.getByText(orderId)).toHaveCount(0);
		});

		await test.step('As a Buyer2, verify that the notification for the restricted note is delivered', async () => {
			userData[buyerUser2.alternateName] = {
				name: buyerUser2.givenName,
				password: 'test',
				surname: buyerUser2.familyName,
			};

			await performLogout(page);

			await performLoginViaApi({
				page,
				screenName: buyerUser2.alternateName,
			});

			await userPersonalBarPage.notificationBadge.click();

			await expect(page.getByText(orderId)).toHaveCount(1);
		});

		await test.step('As a Buyer2, click on the notification to be redirected to the order display page', async () => {
			await page.getByText(orderId).click();

			await expect(page.getByText('Heading Example')).toBeVisible();
		});
	}
);

test(
	'Price On Application Order cannot be checked out',
	{tag: ['@LPD-80337']},
	async ({
		apiHelpers,
		commerceAdminChannelsPage,
		commerceLayoutsPage,
		displayPageTemplatesPage,
		page,
		pageEditorPage,
		site,
	}) => {
		const account = await apiHelpers.headlessAdminUser.postAccount({
			type: 'person',
		});

		const channel =
			await apiHelpers.headlessCommerceAdminChannel.postChannel({
				siteGroupId: site.id,
			});

		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

		await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getFragmentDefinition({
					id: getRandomString(),
					key: 'com.liferay.commerce.fragment.internal.renderer.PendingAccountOrdersDataSetFragmentRenderer',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		await displayPageTemplatesPage.goto(site.friendlyUrlPath);

		const displayPageTemplateName = getRandomString();

		await displayPageTemplatesPage.createTemplate({
			contentType: 'Order',
			name: displayPageTemplateName,
		});

		await displayPageTemplatesPage.editTemplate(displayPageTemplateName);

		await pageEditorPage.addFragment('Order', 'Order Actions');

		await expect(
			page.getByText('The order actions component will be shown here.')
		).toBeVisible();

		const product =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
			});

		const basePriceListId =
			await apiHelpers.headlessCommerceAdminPricing.getBasePriceListId(
				catalog.id
			);

		await apiHelpers.headlessCommerceAdminPricing.postPriceEntry({
			price: 15,
			priceListId: basePriceListId.items[0].id,
			priceOnApplication: true,
			skuId: product.skus[0].id,
		});

		const cart = await apiHelpers.headlessCommerceDeliveryCart.postCart(
			{
				accountId: account.id,
				cartItems: [
					{
						quantity: 1,
						skuId: product.skus[0].id,
					},
				],
			},
			channel.id
		);

		await pageEditorPage.waitForChangesSaved();

		await displayPageTemplatesPage.publishTemplate();
		await displayPageTemplatesPage.clickMoreActions(
			displayPageTemplateName,
			'Mark as Default'
		);

		await waitForAlert(page);

		await expect(
			commerceLayoutsPage.defaultDisplayPageTemplateIcon
		).toBeVisible();

		await commerceAdminChannelsPage.goto();
		await (
			await commerceAdminChannelsPage.channelsTableRowLink(channel.name)
		).click();
		await commerceAdminChannelsPage
			.ordersTabToggle('Quick Checkout')
			.click();
		await commerceAdminChannelsPage.headerActionsSaveButton.click();

		await waitForAlert(page);

		await page.goto(
			liferayConfig.environment.baseUrl +
				`/web/${site.name}/order/${cart.id}`,
			{waitUntil: 'networkidle'}
		);

		await expect(
			commerceLayoutsPage.orderActionsButton('Checkout')
		).toHaveCount(0);
		await expect(
			commerceLayoutsPage.orderActionsButton('Quick Checkout')
		).toHaveCount(0);
		await expect(
			commerceLayoutsPage.orderActionsButton('Request a Quote')
		).toHaveCount(1);
	}
);
