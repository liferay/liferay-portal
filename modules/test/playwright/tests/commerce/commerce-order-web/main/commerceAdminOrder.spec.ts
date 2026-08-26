/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {globalMenuPagesTest} from '../../../../fixtures/globalMenuPagesTest';
import {isolatedSiteTest} from '../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {getRandomInt} from '../../../../utils/getRandomInt';
import getRandomString from '../../../../utils/getRandomString';
import performLogin, {
	performLoginViaApi,
	performLogout,
	userData,
} from '../../../../utils/performLogin';
import {miniumSetUp} from '../../utils/commerce';

export const test = mergeTests(
	commercePagesTest,
	dataApiHelpersTest,
	globalMenuPagesTest,
	isolatedSiteTest,
	loginTest()
);

test('LPD-44010 Check no delete dropdown in order admin page without delete permission', async ({
	apiHelpers,
	commerceAdminOrdersPage,
	page,
}) => {
	const site = await apiHelpers.headlessAdminSite.postSite({
		name: getRandomString(),
	});

	const channel = await apiHelpers.headlessCommerceAdminChannel.postChannel({
		siteGroupId: site.id,
	});

	const catalog = await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

	const product1 = await apiHelpers.headlessCommerceAdminCatalog.postProduct({
		catalogId: catalog.id,
		name: {en_US: 'Product1'},
	});

	const productSkus1 = await apiHelpers.headlessCommerceAdminCatalog
		.getProduct(product1.productId)
		.then((product) => {
			return product.skus;
		});

	const sku1 = productSkus1[0];

	const account = await apiHelpers.headlessAdminUser.postAccount({
		name: getRandomString(),
		type: 'business',
	});

	await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
		account.id,
		['test@liferay.com']
	);

	const address = await apiHelpers.headlessCommerceAdminAccount.postAddress(
		account.id,
		{
			regionISOCode: 'LA',
		}
	);

	const warehouse =
		await apiHelpers.headlessCommerceAdminInventoryApiHelper.postWarehouses(
			{
				active: true,
				latitude: getRandomInt(),
				longitude: getRandomInt(),
				warehouseItems: [
					{
						quantity: 1,
						sku: sku1.sku,
					},
				],
			}
		);

	await apiHelpers.headlessCommerceAdminInventoryApiHelper.postWarehousesChannels(
		warehouse.id,
		channel.id
	);

	const order = await apiHelpers.headlessCommerceAdminOrder.postOrder({
		accountId: account.id,
		billingAddressId: address.id,
		channelId: channel.id,
		orderItems: [
			{
				quantity: 1,
				skuId: sku1.id,
			},
		],
		orderStatus: '1',
		paymentMethod: 'money-order',
		paymentStatus: '0',
		shippingAddressId: address.id,
		shippingMethod: 'by-weight',
		shippingOption: 'standard-option',
	});

	await commerceAdminOrdersPage.goto();

	await expect(
		await commerceAdminOrdersPage.tableRowLink({
			colIndex: 1,
			rowValue: order.id,
		})
	).toBeVisible();

	await commerceAdminOrdersPage.orderActionsButton.click();

	await expect(
		await commerceAdminOrdersPage.deleteItemMenuItem
	).toBeVisible();

	const companyId = await page.evaluate(() => {
		return Liferay.ThemeDisplay.getCompanyId();
	});

	const role = await apiHelpers.headlessAdminUser.postRole({
		name: 'Test Role ' + getRandomString(),
		rolePermissions: [
			{
				actionIds: ['VIEW_CONTROL_PANEL'],
				primaryKey: companyId,
				resourceName: '90',
				scope: 1,
			},
			{
				actionIds: ['VIEW'],
				primaryKey: companyId,
				resourceName:
					'com.liferay.commerce.product.model.CommerceChannel',
				scope: 1,
			},
			{
				actionIds: ['ACCESS_IN_CONTROL_PANEL', 'VIEW'],
				primaryKey: companyId,
				resourceName:
					'com_liferay_commerce_order_web_internal_portlet_CommerceOrderPortlet',
				scope: 1,
			},
			{
				actionIds: ['VIEW'],
				primaryKey: companyId,
				resourceName: 'com.liferay.commerce.model.CommerceOrder',
				scope: 1,
			},
			{
				actionIds: [
					'VIEW_OPEN_COMMERCE_ORDERS',
					'VIEW_COMMERCE_ORDERS',
				],
				primaryKey: companyId,
				resourceName: 'com.liferay.commerce.order',
				scope: 1,
			},
		],
	});

	const userAccount = await apiHelpers.headlessAdminUser.postUserAccount();

	userData[userAccount.alternateName] = {
		name: userAccount.givenName,
		password: 'test',
		surname: userAccount.familyName,
	};

	await apiHelpers.headlessAdminUser.postRoleByExternalReferenceCodeUserAccountAssociation(
		role.externalReferenceCode,
		userAccount.id
	);

	await performLogout(page);
	await performLogin(page, userAccount.alternateName);

	await commerceAdminOrdersPage.goto();

	await expect(
		await commerceAdminOrdersPage.tableRowLink({
			colIndex: 1,
			rowValue: order.id,
		})
	).toBeVisible();

	await expect(commerceAdminOrdersPage.orderActionsButton).not.toBeVisible();
});

test('LPD-45268 Notes tab should not be visible if user does not have required permissions', async ({
	apiHelpers,
	commerceAdminOrderDetailsPage,
	commerceAdminOrdersPage,
	page,
}) => {
	const site = await apiHelpers.headlessAdminSite.postSite({
		name: getRandomString(),
	});

	const channel = await apiHelpers.headlessCommerceAdminChannel.postChannel({
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

	const account = await apiHelpers.headlessAdminUser.postAccount();

	const address = await apiHelpers.headlessCommerceAdminAccount.postAddress(
		account.id,
		{
			regionISOCode: 'CA',
		}
	);

	const warehouse =
		await apiHelpers.headlessCommerceAdminInventoryApiHelper.postWarehouses(
			{
				active: true,
				latitude: getRandomInt(),
				longitude: getRandomInt(),
				warehouseItems: [
					{
						quantity: 1,
						sku: sku.sku,
					},
				],
			}
		);

	await apiHelpers.headlessCommerceAdminInventoryApiHelper.postWarehousesChannels(
		warehouse.id,
		channel.id
	);

	const order = await apiHelpers.headlessCommerceAdminOrder.postOrder({
		accountId: account.id,
		billingAddressId: address.id,
		channelId: channel.id,
		orderItems: [
			{
				quantity: 1,
				skuId: sku.id,
			},
		],
		orderStatus: '1',
		paymentMethod: 'money-order',
		paymentStatus: '0',
		shippingAddressId: address.id,
		shippingMethod: 'by-weight',
		shippingOption: 'standard-option',
	});

	const companyId = await page.evaluate(() => {
		return Liferay.ThemeDisplay.getCompanyId();
	});

	const role = await apiHelpers.headlessAdminUser.postRole({
		name: getRandomString(),
		rolePermissions: [
			{
				actionIds: ['VIEW_CONTROL_PANEL'],
				primaryKey: companyId,
				resourceName: '90',
				scope: 1,
			},
			{
				actionIds: ['VIEW'],
				primaryKey: companyId,
				resourceName:
					'com.liferay.commerce.product.model.CommerceChannel',
				scope: 1,
			},
			{
				actionIds: ['ACCESS_IN_CONTROL_PANEL', 'VIEW'],
				primaryKey: companyId,
				resourceName:
					'com_liferay_commerce_order_web_internal_portlet_CommerceOrderPortlet',
				scope: 1,
			},
			{
				actionIds: ['VIEW'],
				primaryKey: companyId,
				resourceName: 'com.liferay.commerce.model.CommerceOrder',
				scope: 1,
			},
			{
				actionIds: [
					'VIEW_OPEN_COMMERCE_ORDERS',
					'VIEW_COMMERCE_ORDERS',
				],
				primaryKey: companyId,
				resourceName: 'com.liferay.commerce.order',
				scope: 1,
			},
		],
	});

	const user = await apiHelpers.headlessAdminUser.postUserAccount();

	userData[user.alternateName] = {
		name: user.givenName,
		password: 'test',
		surname: user.familyName,
	};

	await apiHelpers.headlessAdminUser.assignUserToRole(
		role.externalReferenceCode,
		user.id
	);

	await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
		account.id,
		[user.emailAddress]
	);

	const role2 = await apiHelpers.headlessAdminUser.postRole({
		name: getRandomString(),
		rolePermissions: [
			{
				actionIds: ['VIEW_CONTROL_PANEL'],
				primaryKey: companyId,
				resourceName: '90',
				scope: 1,
			},
			{
				actionIds: ['VIEW'],
				primaryKey: companyId,
				resourceName:
					'com.liferay.commerce.product.model.CommerceChannel',
				scope: 1,
			},
			{
				actionIds: ['ACCESS_IN_CONTROL_PANEL', 'VIEW'],
				primaryKey: companyId,
				resourceName:
					'com_liferay_commerce_order_web_internal_portlet_CommerceOrderPortlet',
				scope: 1,
			},
			{
				actionIds: ['VIEW'],
				primaryKey: companyId,
				resourceName: 'com.liferay.commerce.model.CommerceOrder',
				scope: 1,
			},
			{
				actionIds: [
					'MANAGE_COMMERCE_ORDER_NOTES',
					'MANAGE_COMMERCE_ORDER_RESTRICTED_NOTES',
					'VIEW_OPEN_COMMERCE_ORDERS',
					'VIEW_COMMERCE_ORDERS',
				],
				primaryKey: companyId,
				resourceName: 'com.liferay.commerce.order',
				scope: 1,
			},
		],
	});

	const user2 = await apiHelpers.headlessAdminUser.postUserAccount();

	userData[user2.alternateName] = {
		name: user2.givenName,
		password: 'test',
		surname: user2.familyName,
	};

	await apiHelpers.headlessAdminUser.assignUserToRole(
		role2.externalReferenceCode,
		user2.id
	);

	await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
		account.id,
		[user2.emailAddress]
	);

	await performLogout(page);
	await performLogin(page, user.alternateName);

	await commerceAdminOrdersPage.goto();

	await (
		await commerceAdminOrdersPage.tableRowLink({
			colIndex: 1,
			rowValue: order.id,
		})
	).click();

	await expect(commerceAdminOrderDetailsPage.orderNotesLink).toHaveCount(0);

	await performLogout(page);
	await performLogin(page, user2.alternateName);

	await commerceAdminOrdersPage.goto();

	await (
		await commerceAdminOrdersPage.tableRowLink({
			colIndex: 1,
			rowValue: order.id,
		})
	).click();

	await expect(commerceAdminOrderDetailsPage.orderNotesLink).toBeVisible();
});

test('LPD-47793 Notes should be visible using their respective permissions in the Order menu', async ({
	apiHelpers,
	commerceAdminOrderDetailsPage,
	commerceAdminOrderNotesPage,
	commerceAdminOrdersPage,
	page,
	site,
}) => {
	const channel = await apiHelpers.headlessCommerceAdminChannel.postChannel({
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

	const account = await apiHelpers.headlessAdminUser.postAccount();

	const address = await apiHelpers.headlessCommerceAdminAccount.postAddress(
		account.id,
		{
			regionISOCode: 'CA',
		}
	);

	const warehouse =
		await apiHelpers.headlessCommerceAdminInventoryApiHelper.postWarehouses(
			{
				active: true,
				latitude: getRandomInt(),
				longitude: getRandomInt(),
				warehouseItems: [
					{
						quantity: 1,
						sku: sku.sku,
					},
				],
			}
		);

	await apiHelpers.headlessCommerceAdminInventoryApiHelper.postWarehousesChannels(
		warehouse.id,
		channel.id
	);

	const order = await apiHelpers.headlessCommerceAdminOrder.postOrder({
		accountId: account.id,
		billingAddressId: address.id,
		channelId: channel.id,
		orderItems: [
			{
				quantity: 1,
				skuId: sku.id,
			},
		],
		orderStatus: '1',
		paymentMethod: 'money-order',
		paymentStatus: '0',
		shippingAddressId: address.id,
		shippingMethod: 'by-weight',
		shippingOption: 'standard-option',
	});

	const companyId = await page.evaluate(() => {
		return Liferay.ThemeDisplay.getCompanyId();
	});

	const role = await apiHelpers.headlessAdminUser.postRole({
		name: getRandomString(),
		rolePermissions: [
			{
				actionIds: ['VIEW_CONTROL_PANEL'],
				primaryKey: companyId,
				resourceName: '90',
				scope: 1,
			},
			{
				actionIds: ['VIEW'],
				primaryKey: companyId,
				resourceName:
					'com.liferay.commerce.product.model.CommerceChannel',
				scope: 1,
			},
			{
				actionIds: ['ACCESS_IN_CONTROL_PANEL', 'VIEW'],
				primaryKey: companyId,
				resourceName:
					'com_liferay_commerce_order_web_internal_portlet_CommerceOrderPortlet',
				scope: 1,
			},
			{
				actionIds: ['VIEW'],
				primaryKey: companyId,
				resourceName: 'com.liferay.commerce.model.CommerceOrder',
				scope: 1,
			},
			{
				actionIds: [
					'MANAGE_COMMERCE_ORDER_NOTES',
					'VIEW_OPEN_COMMERCE_ORDERS',
					'VIEW_COMMERCE_ORDERS',
				],
				primaryKey: companyId,
				resourceName: 'com.liferay.commerce.order',
				scope: 1,
			},
		],
	});

	const publicOrderNote =
		await apiHelpers.headlessCommerceAdminOrder.postOrderIdOrderNote(
			order.id,
			{restricted: false}
		);

	const privateOrderNote =
		await apiHelpers.headlessCommerceAdminOrder.postOrderIdOrderNote(
			order.id,
			{restricted: true}
		);

	const user = await apiHelpers.headlessAdminUser.postUserAccount();

	userData[user.alternateName] = {
		name: user.givenName,
		password: 'test',
		surname: user.familyName,
	};

	await apiHelpers.headlessAdminUser.assignUserToRole(
		role.externalReferenceCode,
		user.id
	);

	await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
		account.id,
		[user.emailAddress]
	);

	const role2 = await apiHelpers.headlessAdminUser.postRole({
		name: getRandomString(),
		rolePermissions: [
			{
				actionIds: ['VIEW_CONTROL_PANEL'],
				primaryKey: companyId,
				resourceName: '90',
				scope: 1,
			},
			{
				actionIds: ['VIEW'],
				primaryKey: companyId,
				resourceName:
					'com.liferay.commerce.product.model.CommerceChannel',
				scope: 1,
			},
			{
				actionIds: ['ACCESS_IN_CONTROL_PANEL', 'VIEW'],
				primaryKey: companyId,
				resourceName:
					'com_liferay_commerce_order_web_internal_portlet_CommerceOrderPortlet',
				scope: 1,
			},
			{
				actionIds: ['VIEW'],
				primaryKey: companyId,
				resourceName: 'com.liferay.commerce.model.CommerceOrder',
				scope: 1,
			},
			{
				actionIds: [
					'MANAGE_COMMERCE_ORDER_NOTES',
					'MANAGE_COMMERCE_ORDER_RESTRICTED_NOTES',
					'VIEW_OPEN_COMMERCE_ORDERS',
					'VIEW_COMMERCE_ORDERS',
				],
				primaryKey: companyId,
				resourceName: 'com.liferay.commerce.order',
				scope: 1,
			},
		],
	});

	const user2 = await apiHelpers.headlessAdminUser.postUserAccount();

	userData[user2.alternateName] = {
		name: user2.givenName,
		password: 'test',
		surname: user2.familyName,
	};

	await apiHelpers.headlessAdminUser.assignUserToRole(
		role2.externalReferenceCode,
		user2.id
	);

	await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
		account.id,
		[user2.emailAddress]
	);

	await performLogout(page);
	await performLogin(page, user.alternateName);

	await commerceAdminOrdersPage.goto();

	await (
		await commerceAdminOrdersPage.tableRowLink({
			colIndex: 1,
			rowValue: order.id,
		})
	).click();

	await commerceAdminOrderDetailsPage.orderNotesLink.click();

	expect(
		await commerceAdminOrderNotesPage.orderNoteContent(
			publicOrderNote.content
		)
	).toBeVisible();

	expect(
		await commerceAdminOrderNotesPage.orderNoteContent(
			privateOrderNote.content
		)
	).toHaveCount(0);

	await performLogout(page);
	await performLogin(page, user2.alternateName);

	await commerceAdminOrdersPage.goto();

	await (
		await commerceAdminOrdersPage.tableRowLink({
			colIndex: 1,
			rowValue: order.id,
		})
	).click();

	await commerceAdminOrderDetailsPage.orderNotesLink.click();

	expect(
		await commerceAdminOrderNotesPage.orderNoteContent(
			publicOrderNote.content
		)
	).toBeVisible();

	expect(
		await commerceAdminOrderNotesPage.orderNoteContent(
			privateOrderNote.content
		)
	).toBeVisible();
});

test('LPD-31378 Check order date formatted correctly', async ({
	apiHelpers,
	checkoutPage,
	commerceAdminOrdersPage,
	commerceMiniCartPage,
	commerceThemeMiniumCatalogPage,
	page,
}) => {
	const {site} = await miniumSetUp(apiHelpers);

	await apiHelpers.headlessAdminUser.postAccount({
		name: getRandomString(),
		type: 'business',
	});

	await page.goto(`/web/${site.name}/catalog`);

	await commerceThemeMiniumCatalogPage.addToCart('Mount');

	await commerceMiniCartPage.miniCartButton.click();
	await commerceMiniCartPage.submitButton.click();

	await checkoutPage.addAddress({
		city: 'testCity',
		countryLabel: 'United States',
		name: 'John Doe',
		regionLabel: 'Florida',
		street: 'testStreet',
		zip: '12345',
	});

	await checkoutPage.continueButton.click();
	await expect(page.getByText('Standard Delivery (+$ 15.00)')).toBeVisible();
	await checkoutPage.continueButton.click();
	await expect(page.getByText('Mount')).toBeVisible();
	await checkoutPage.continueButton.click();
	await expect(checkoutPage.orderSuccessMessage).toBeVisible();

	await checkoutPage.goToOrderDetailsButton.click();

	const orderDate = await commerceAdminOrdersPage.orderDate.textContent();
	const cleanedOrderDate = orderDate.replace(/\s+/g, ' ').trim();

	const orderId = await commerceAdminOrdersPage.orderId.textContent();

	apiHelpers.data.push({id: orderId, type: 'order'});

	await commerceAdminOrdersPage.goto();

	const orderDate2 = await commerceAdminOrdersPage
		.orderDateByOrderId(orderId)
		.textContent();
	const cleanedOrderDate2 = orderDate2.replace(
		/(\d{2}),(\s*\d{1,2}:)/,
		'$1$2'
	);

	expect(cleanedOrderDate).toBe(cleanedOrderDate2);
});

test(
	'Verify the order type management when creating a new order via the account selector.',
	{tag: ['@LPD-56416']},
	async ({
		apiHelpers,
		commerceAdminOrderTypesPage,
		commerceLayoutsPage,
		page,
		pendingOrdersPage,
	}) => {
		test.setTimeout(90000);

		const account = await apiHelpers.headlessAdminUser.postAccount({
			name: getRandomString(),
			type: 'business',
		});

		const user =
			await apiHelpers.headlessAdminUser.getUserAccountByEmailAddress(
				'demo.unprivileged@liferay.com'
			);

		const {site} = await miniumSetUp(apiHelpers);

		await test.step('Setup Buyer user', async () => {
			await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
				account.id,
				['demo.unprivileged@liferay.com']
			);

			const rolesResponse =
				await apiHelpers.headlessAdminUser.getAccountRoles(account.id);

			const accountRoleBuyer = rolesResponse?.items?.filter((role) => {
				return role.name === 'Buyer';
			});

			const siteRole =
				await apiHelpers.headlessAdminUser.getRoleByName('Site Member');

			await apiHelpers.headlessAdminUser.assignAccountRoles(
				account.externalReferenceCode,
				accountRoleBuyer[0].id,
				user.emailAddress
			);
			await apiHelpers.headlessAdminUser.assignUserToSite(
				siteRole.id,
				site.id,
				user.id
			);
		});

		await test.step('Verify note label in an order created via the account selector', async () => {
			await page.goto(`/web/${site.name}`);

			await commerceLayoutsPage
				.accountSelectorButton('Account Selector')
				.click();
			await commerceLayoutsPage.createNewOrderButton.click();

			await expect(
				pendingOrdersPage.questionAndAnswersText
			).toBeVisible();
		});

		await test.step('Verify inactive order type is not assigned to an order created via the account selector', async () => {
			await page.goto(`/web/${site.name}`);

			await commerceLayoutsPage
				.accountSelectorButton('Account Selector')
				.click();
			await commerceLayoutsPage.createNewOrderButton.click();

			await expect(pendingOrdersPage.orderType).toBeEmpty();
		});

		await test.step('Verify inactive order type is not assigned to an order created via the account selector', async () => {
			await commerceAdminOrderTypesPage.addOrderType(apiHelpers, false);

			await page.goto(`/web/${site.name}`);

			await commerceLayoutsPage
				.accountSelectorButton('Account Selector')
				.click();
			await commerceLayoutsPage.createNewOrderButton.click();

			await expect(pendingOrdersPage.orderType).toBeEmpty();
		});

		await test.step('Verify single active order type is automatically assigned to orders created via the account selector.', async () => {
			const {orderTypeName} =
				await commerceAdminOrderTypesPage.addOrderType(
					apiHelpers,
					true
				);

			await page.goto(`/web/${site.name}`);

			await commerceLayoutsPage
				.accountSelectorButton('Account Selector')
				.click();
			await commerceLayoutsPage.createNewOrderButton.click();

			await expect(pendingOrdersPage.orderType).toHaveText(orderTypeName);
		});

		await test.step('Verify user can select order type when creating a new order via the account selector', async () => {
			const {orderTypeName} =
				await commerceAdminOrderTypesPage.addOrderType(
					apiHelpers,
					true
				);

			await performLogout(page);

			await performLoginViaApi({page, screenName: user.alternateName});

			await page.goto(`/web/${site.name}`);

			await commerceLayoutsPage
				.accountSelectorButton('Account Selector')
				.click();
			await commerceLayoutsPage.createNewOrderButton.click();
			await expect(
				commerceLayoutsPage.orderTypeModalHeading
			).toBeVisible();
			await commerceLayoutsPage.orderTypeModalInput.selectOption({
				label: orderTypeName,
			});
			await commerceLayoutsPage.orderTypeModalButton.click();

			await expect(pendingOrdersPage.orderType).toHaveText(orderTypeName);
		});
	}
);

test(
	'Verify the order type management when creating a new order via pending orders.',
	{tag: ['@LPD-56416']},
	async ({
		apiHelpers,
		commerceAdminOrderTypesPage,
		commerceLayoutsPage,
		page,
		pendingOrdersPage,
	}) => {
		test.setTimeout(180000);
		const account = await apiHelpers.headlessAdminUser.postAccount({
			name: getRandomString(),
			type: 'business',
		});

		const user =
			await apiHelpers.headlessAdminUser.getUserAccountByEmailAddress(
				'demo.unprivileged@liferay.com'
			);

		const {site} = await miniumSetUp(apiHelpers);

		await test.step('Setup Buyer user', async () => {
			await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
				account.id,
				['demo.unprivileged@liferay.com']
			);

			const rolesResponse =
				await apiHelpers.headlessAdminUser.getAccountRoles(account.id);

			const accountRoleBuyer = rolesResponse?.items?.filter((role) => {
				return role.name === 'Buyer';
			});

			const siteRole =
				await apiHelpers.headlessAdminUser.getRoleByName('Site Member');

			await apiHelpers.headlessAdminUser.assignAccountRoles(
				account.externalReferenceCode,
				accountRoleBuyer[0].id,
				user.emailAddress
			);
			await apiHelpers.headlessAdminUser.assignUserToSite(
				siteRole.id,
				site.id,
				user.id
			);
		});

		await test.step('Verify inactive order type is not assigned to an order created via pending orders', async () => {
			await page.goto(`/web/${site.name}/pending-orders`, {
				waitUntil: 'networkidle',
			});

			await commerceLayoutsPage.addOrderButton.click();

			await expect(page).not.toHaveURL(/pending-orders$/);

			await expect(pendingOrdersPage.orderType).toBeEmpty();
		});

		await test.step('Verify inactive order type is not assigned to an order created via pending orders', async () => {
			await commerceAdminOrderTypesPage.addOrderType(apiHelpers, false);

			await page.goto(`/web/${site.name}/pending-orders`, {
				waitUntil: 'networkidle',
			});

			await commerceLayoutsPage.addOrderButton.click();

			await expect(page).not.toHaveURL(/pending-orders$/);

			await expect(pendingOrdersPage.orderType).toBeEmpty();
		});

		await test.step('Verify single active order type is automatically assigned to orders created via pending orders', async () => {
			const {orderTypeName} =
				await commerceAdminOrderTypesPage.addOrderType(
					apiHelpers,
					true
				);
			await page.goto(`/web/${site.name}/pending-orders`, {
				waitUntil: 'networkidle',
			});

			await commerceLayoutsPage.addOrderButton.click();

			await expect(page).not.toHaveURL(/pending-orders$/);

			await expect(pendingOrdersPage.orderType).toHaveText(orderTypeName);
		});

		await test.step('Verify user can select order type when creating a new order via pending orders', async () => {
			const {orderTypeName} =
				await commerceAdminOrderTypesPage.addOrderType(
					apiHelpers,
					true
				);

			await performLogout(page);
			await performLoginViaApi({page, screenName: user.alternateName});

			await page.goto(`/web/${site.name}`);

			await page.goto(`/web/${site.name}/pending-orders`, {
				waitUntil: 'networkidle',
			});

			await commerceLayoutsPage.addOrderButton.click();

			await expect(
				commerceLayoutsPage.orderTypeModalHeading
			).toBeVisible();
			await commerceLayoutsPage.orderTypeModalInput.selectOption({
				label: orderTypeName,
			});
			await commerceLayoutsPage.orderTypeModalButton.click();

			await expect(pendingOrdersPage.orderType).toHaveText(orderTypeName);
		});
	}
);

test(
	'Verify the order type management when creating a new order via add to cart',
	{tag: ['@COMMERCE-11565', '@LPD-56416']},
	async ({
		apiHelpers,
		commerceAdminOrderTypesPage,
		commerceLayoutsPage,
		commerceMiniCartPage,
		commerceThemeMiniumCatalogPage,
		page,
		pendingOrdersPage,
	}) => {
		test.setTimeout(180000);

		const account = await apiHelpers.headlessAdminUser.postAccount({
			name: getRandomString(),
			type: 'business',
		});

		const user =
			await apiHelpers.headlessAdminUser.getUserAccountByEmailAddress(
				'demo.unprivileged@liferay.com'
			);

		const {site} = await miniumSetUp(apiHelpers);

		await test.step('Setup Buyer user', async () => {
			await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
				account.id,
				['demo.unprivileged@liferay.com']
			);

			const rolesResponse =
				await apiHelpers.headlessAdminUser.getAccountRoles(account.id);

			const accountRoleBuyer = rolesResponse?.items?.filter((role) => {
				return role.name === 'Buyer';
			});

			const siteRole =
				await apiHelpers.headlessAdminUser.getRoleByName('Site Member');

			await apiHelpers.headlessAdminUser.assignAccountRoles(
				account.externalReferenceCode,
				accountRoleBuyer[0].id,
				user.emailAddress
			);
			await apiHelpers.headlessAdminUser.assignUserToSite(
				siteRole.id,
				site.id,
				user.id
			);
		});

		await test.step('Verify inactive order type is not assigned to an order created via add to cart', async () => {
			await page.goto(`/web/${site.name}`);

			await commerceThemeMiniumCatalogPage.addToCart('U-Joint');

			await commerceMiniCartPage.miniCartButton.click();

			await page.goto(`/web/${site.name}/pending-orders`);

			await pendingOrdersPage.viewButton.click();

			await expect(pendingOrdersPage.orderType).toBeEmpty();

			await apiHelpers.headlessCommerceAdminOrder.deleteOrder(
				Number(await pendingOrdersPage.orderId.textContent())
			);
		});

		await test.step('Verify inactive order type is not assigned to an order created via add to cart', async () => {
			await commerceAdminOrderTypesPage.addOrderType(apiHelpers, false);

			await page.goto(`/web/${site.name}`);

			await commerceThemeMiniumCatalogPage.addToCart('U-Joint');

			await commerceMiniCartPage.miniCartButton.click();

			await page.goto(`/web/${site.name}/pending-orders`);

			await pendingOrdersPage.viewButton.click();

			await expect(pendingOrdersPage.orderType).toBeEmpty();

			await apiHelpers.headlessCommerceAdminOrder.deleteOrder(
				Number(await pendingOrdersPage.orderId.textContent())
			);
		});

		await test.step('Verify when creating a new order with add to cart and there is only 1 order type active for the channel, that order type is set in the order', async () => {
			const {orderTypeName} =
				await commerceAdminOrderTypesPage.addOrderType(
					apiHelpers,
					true
				);
			await page.goto(`/web/${site.name}`);

			await commerceThemeMiniumCatalogPage.addToCart('U-Joint');

			await commerceMiniCartPage.miniCartButton.click();

			await page.goto(`/web/${site.name}/pending-orders`);

			await pendingOrdersPage.viewButton.click();

			await expect(pendingOrdersPage.orderType).toHaveText(orderTypeName);

			await apiHelpers.headlessCommerceAdminOrder.deleteOrder(
				Number(await pendingOrdersPage.orderId.textContent())
			);
		});

		await test.step('Verify user can select order type when creating a new order via add to cart', async () => {
			const {orderTypeName} =
				await commerceAdminOrderTypesPage.addOrderType(
					apiHelpers,
					true
				);

			await performLogout(page);

			await performLoginViaApi({page, screenName: user.alternateName});

			await page.goto(`/web/${site.name}`);

			await commerceThemeMiniumCatalogPage.addToCart('U-Joint');

			await expect(
				commerceLayoutsPage.orderTypeModalHeading
			).toBeVisible();
			await commerceLayoutsPage.orderTypeModalInput.selectOption({
				label: orderTypeName,
			});
			await commerceLayoutsPage.orderTypeModalButton.click();

			await page.goto(`/web/${site.name}/catalog`);

			await commerceThemeMiniumCatalogPage.addToCart('Mount');

			await page.goto(`/web/${site.name}/pending-orders`);

			await pendingOrdersPage.viewButton.click();

			await expect(pendingOrdersPage.orderType).toHaveText(orderTypeName);
		});
	}
);

test(
	'Order Manager can only manage its account orders in control panel',
	{tag: ['@LPD-74672']},
	async ({apiHelpers, commerceAdminOrdersPage, page, site}) => {
		const channel =
			await apiHelpers.headlessCommerceAdminChannel.postChannel({
				siteGroupId: site.id,
			});

		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

		const product =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				name: {en_US: 'Product1'},
			});

		const productSkus = await apiHelpers.headlessCommerceAdminCatalog
			.getProduct(product.productId)
			.then((product) => {
				return product.skus;
			});

		const sku = productSkus[0];

		const account1 = await apiHelpers.headlessAdminUser.postAccount({
			name: getRandomString(),
			type: 'business',
		});
		const account2 = await apiHelpers.headlessAdminUser.postAccount({
			name: getRandomString(),
			type: 'business',
		});
		const account3 = await apiHelpers.headlessAdminUser.postAccount({
			name: getRandomString(),
			type: 'business',
		});

		const user =
			await apiHelpers.headlessAdminUser.getUserAccountByEmailAddress(
				'demo.unprivileged@liferay.com'
			);

		await test.step('Setup Order Manager user', async () => {
			await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
				account1.id,
				['demo.unprivileged@liferay.com']
			);
			await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
				account2.id,
				['demo.unprivileged@liferay.com']
			);
			await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
				account3.id,
				['demo.unprivileged@liferay.com']
			);

			const rolesResponse =
				await apiHelpers.headlessAdminUser.getAccountRoles(account1.id);

			const accountRoleOrderManager = rolesResponse?.items?.filter(
				(role) => {
					return role.name === 'Order Manager';
				}
			);

			await apiHelpers.headlessAdminUser.assignAccountRoles(
				account1.externalReferenceCode,
				accountRoleOrderManager[0].id,
				user.emailAddress
			);
			await apiHelpers.headlessAdminUser.assignAccountRoles(
				account2.externalReferenceCode,
				accountRoleOrderManager[0].id,
				user.emailAddress
			);

			const accountRoleBuyer = rolesResponse?.items?.filter((role) => {
				return role.name === 'Buyer';
			});

			await apiHelpers.headlessAdminUser.assignAccountRoles(
				account3.externalReferenceCode,
				accountRoleBuyer[0].id,
				user.emailAddress
			);

			const orderAdministratorRole =
				await apiHelpers.headlessAdminUser.getRoleByName(
					'Order Administrator'
				);

			await apiHelpers.headlessAdminUser.assignUserToRole(
				orderAdministratorRole.externalReferenceCode,
				user.id
			);

			const siteRole =
				await apiHelpers.headlessAdminUser.getRoleByName('Site Member');

			await apiHelpers.headlessAdminUser.assignUserToSite(
				siteRole.id,
				site.id,
				user.id
			);
		});

		await test.step('Create some orders with different accounts', async () => {
			const address1 =
				await apiHelpers.headlessCommerceAdminAccount.postAddress(
					account1.id,
					{phoneNumber: '12345', regionISOCode: 'AL'}
				);
			const address2 =
				await apiHelpers.headlessCommerceAdminAccount.postAddress(
					account2.id,
					{phoneNumber: '12345', regionISOCode: 'AL'}
				);
			const address3 =
				await apiHelpers.headlessCommerceAdminAccount.postAddress(
					account3.id,
					{phoneNumber: '12345', regionISOCode: 'AL'}
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
				paymentStatus: '0',
				shippingAddressId: address1.id,
			});
			await apiHelpers.headlessCommerceAdminOrder.postOrder({
				accountId: account1.id,
				billingAddressId: address1.id,
				channelId: channel.id,
				orderItems: [
					{
						decimalQuantity: 5,
						quantity: 1,
						skuId: sku.id,
					},
				],
				orderStatus: '0',
				paymentStatus: '0',
				shippingAddressId: address1.id,
			});
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
				paymentStatus: '0',
				shippingAddressId: address2.id,
			});
			await apiHelpers.headlessCommerceAdminOrder.postOrder({
				accountId: account2.id,
				billingAddressId: address2.id,
				channelId: channel.id,
				orderItems: [
					{
						decimalQuantity: 5,
						quantity: 1,
						skuId: sku.id,
					},
				],
				orderStatus: '0',
				paymentStatus: '0',
				shippingAddressId: address2.id,
			});
			await apiHelpers.headlessCommerceAdminOrder.postOrder({
				accountId: account3.id,
				billingAddressId: address3.id,
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
				shippingAddressId: address3.id,
			});
			await apiHelpers.headlessCommerceAdminOrder.postOrder({
				accountId: account3.id,
				billingAddressId: address3.id,
				channelId: channel.id,
				orderItems: [
					{
						decimalQuantity: 5,
						quantity: 1,
						skuId: sku.id,
					},
				],
				orderStatus: '0',
				paymentStatus: '0',
				shippingAddressId: address3.id,
			});
		});

		await test.step('Verify Order Manager can only see account1 and account2 orders', async () => {
			await performLogout(page);

			await performLoginViaApi({page, screenName: user.alternateName});

			await commerceAdminOrdersPage.goto();

			await expect(page.getByText(account1.name)).toHaveCount(2);
			await expect(page.getByText(account2.name)).toHaveCount(2);
			await expect(page.getByText(account3.name)).toHaveCount(0);
		});
	}
);
