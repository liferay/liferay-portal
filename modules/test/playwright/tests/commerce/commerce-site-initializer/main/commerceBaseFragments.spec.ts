/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../../fixtures/apiHelpersTest';
import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../../fixtures/pageEditorPagesTest';
import {systemSettingsPageTest} from '../../../../fixtures/systemSettingsPageTest';
import {liferayConfig} from '../../../../liferay.config';
import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';
import {getRandomInt} from '../../../../utils/getRandomInt';
import getRandomString from '../../../../utils/getRandomString';
import {
	performLoginViaApi,
	performLogout,
	performUserSwitchViaApi,
} from '../../../../utils/performLogin';
import {waitForAlert} from '../../../../utils/waitForAlert';
import {ORDER_WORKFLOW_STATUS_CODE} from '../../../workspaces/liferay-workspace-marketplace/main/utils/constants';
import {
	classicCommerceSetUp,
	configureBuyerUserForSite,
	createAccountWithBuyerUser,
} from '../../utils/commerce';

export const test = mergeTests(
	apiHelpersTest,
	commercePagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-10562': {enabled: true},
	}),
	loginTest(),
	pageEditorPagesTest,
	systemSettingsPageTest
);

test(
	'Commerce Classic Header main fragment is correctly displayed',
	{tag: ['@LPD-23780', '@LPD-94883']},
	async ({
		apiHelpers,
		commerceThemeClassicCatalogPage,
		page,
		pageEditorPage,
	}) => {
		test.setTimeout(180000);

		const {site} = await classicCommerceSetUp(
			apiHelpers,
			`classic-commerce`
		);

		await test.step('Publish the Commerce Classic Master and verify the header fragments are displayed', async () => {
			await page.goto(`/web${site.friendlyUrlPath}?p_l_mode=edit`, {
				waitUntil: 'networkidle',
			});

			await pageEditorPage.goToSidebarTab('Page Design Options');

			await page.getByLabel('Commerce Classic Master').click();
			await page.getByLabel('Publish', {exact: true}).click();

			await expect(
				page.locator(
					'.lfr-layout-structure-item-commerce-account-selector-fragments-account-selector-fragment'
				)
			).toBeVisible();
			await expect(
				page.locator(
					'.lfr-layout-structure-item-commerce-cart-fragments-mini-cart'
				)
			).toBeVisible();
			await expect(
				page.locator('header .portlet-search-bar')
			).toBeVisible();
		});

		await test.step('The account selector order list refreshes when a new order is created', async () => {
			await apiHelpers.headlessAdminUser.postAccount({
				name: getRandomString(),
				type: 'business',
			});

			await page.goto(`/web${site.friendlyUrlPath}/catalog`, {
				waitUntil: 'networkidle',
			});

			await commerceThemeClassicCatalogPage
				.productCardAddToCartButton('Wear Sensors')
				.click();

			await page.waitForLoadState('networkidle');

			const triggerOrderId = page.locator(
				'.btn-account-selector .order-id'
			);

			await expect(triggerOrderId).toBeVisible();

			const orderId = await triggerOrderId.innerText();

			const accountSelectorDropdownMenu = page.locator(
				'[id$="-account-selector-dropdown-menu"]'
			);

			await clickAndExpectToBeVisible({
				target: accountSelectorDropdownMenu,
				trigger: page.locator('.account-selector-cta-container'),
			});

			await expect(
				accountSelectorDropdownMenu
					.locator(
						'.lfr-layout-structure-item-com-liferay-commerce-fragment-internal-renderer-pendingaccountordersdatasetfragmentrenderer'
					)
					.getByText(orderId)
			).toBeVisible();
		});
	}
);

test(
	'A mini cart item keeps its multishipping delivery group after its unit of measure is changed',
	{tag: ['@LPD-35323', '@LPD-62368']},
	async ({
		apiHelpers,
		commerceAdminChannelDetailsPage,
		commerceAdminChannelsPage,
		commerceLayoutsPage,
		commerceMiniCartPage,
		commerceThemeClassicOrdersPage,
		page,
	}) => {
		const {catalog, channel, site} = await classicCommerceSetUp(apiHelpers);

		const product =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				skus: [
					{
						cost: 0,
						price: 20,
						published: true,
						purchasable: true,
						sku: 'Sku' + getRandomInt(),
					},
				],
			});

		const sku = product.skus[0];

		const unitOfMeasure1 =
			await apiHelpers.headlessCommerceAdminCatalog.postSkuUnitOfMeasure(
				sku.id,
				{
					basePrice: 1,
					precision: 0,
					priority: 1,
					rate: 1,
				}
			);

		const unitOfMeasure2 =
			await apiHelpers.headlessCommerceAdminCatalog.postSkuUnitOfMeasure(
				sku.id,
				{
					basePrice: 2,
					precision: 0,
					priority: 2,
					rate: 1,
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
							quantity: 10,
							sku: sku.sku,
							unitOfMeasureKey: unitOfMeasure1.key,
						},
						{
							quantity: 10,
							sku: sku.sku,
							unitOfMeasureKey: unitOfMeasure2.key,
						},
					],
				}
			);

		await apiHelpers.headlessCommerceAdminInventoryApiHelper.postWarehousesChannels(
			warehouse.id,
			channel.id
		);

		const {account, buyerUser} = await createAccountWithBuyerUser(
			apiHelpers,
			site.id
		);

		await performUserSwitchViaApi(page, buyerUser.alternateName);

		const cart = await apiHelpers.headlessCommerceDeliveryCart.postCart(
			{
				accountId: account.id,
				cartItems: [
					{
						quantity: 1,
						skuId: sku.id,
						skuUnitOfMeasure: {key: unitOfMeasure1.key},
					},
				],
			},
			channel.id
		);

		const orderDetailsPageURL =
			liferayConfig.environment.baseUrl +
			`/web/${site.name}/order/${cart.id}`;

		await test.step('Multishipping tab is hidden while multishipping is disabled', async () => {
			await page.goto(orderDetailsPageURL);

			await expect(
				commerceThemeClassicOrdersPage.orderTabs('Multishipping')
			).toHaveCount(0);
		});

		await test.step('Enable multishipping on the channel', async () => {
			await performUserSwitchViaApi(page, 'test');

			await commerceAdminChannelsPage.changeCommerceChannelAllowMultishipping(
				commerceAdminChannelDetailsPage,
				true,
				channel.name
			);

			await performUserSwitchViaApi(page, buyerUser.alternateName);
		});

		const deliveryGroupName = getRandomString();

		await test.step('Add a delivery group with a new shipping address', async () => {
			await page.goto(orderDetailsPageURL);

			await commerceThemeClassicOrdersPage
				.orderTabs('Multishipping')
				.click();

			await commerceLayoutsPage.addDeliveryGroupButton.click();
			await commerceLayoutsPage.deliveryGroupNameInput.fill(
				deliveryGroupName
			);
			await commerceLayoutsPage.deliveryGroupDateInput.fill('2030-01-15');

			await commerceLayoutsPage.addressNameInput.fill(getRandomString());
			await commerceLayoutsPage.addressCountrySelect.selectOption({
				label: 'United States',
			});
			await commerceLayoutsPage.addressStreet1Input.fill(
				getRandomString()
			);
			await commerceLayoutsPage.addressCityInput.fill(getRandomString());
			await commerceLayoutsPage.addressRegionSelect.selectOption({
				label: 'Alabama',
			});
			await commerceLayoutsPage.addressZipInput.fill('35004');
			await commerceLayoutsPage.saveButton.click();

			await waitForAlert(page);

			await expect(
				commerceLayoutsPage.deliveryGroupQuantityInput(sku.sku)
			).toHaveValue('1');
		});

		await test.step('Change the unit of measure from the mini cart after a cancel', async () => {
			await commerceMiniCartPage.miniCartButton.click();
			await commerceMiniCartPage
				.miniCartItemActionsButton(product.name.en_US)
				.click();
			await commerceMiniCartPage.editMenuItem.click();
			await commerceMiniCartPage.miniCartCancelButton.click();
			await commerceMiniCartPage
				.miniCartItemActionsButton(product.name.en_US)
				.click();
			await commerceMiniCartPage.editMenuItem.click();
			await commerceMiniCartPage.miniCartUnitOfMeasureSelector.selectOption(
				unitOfMeasure2.key
			);

			await expect(
				commerceMiniCartPage.miniCartEditItemPrice('List Price')
			).toHaveText('$ 2.00');

			await commerceMiniCartPage.miniCartSaveButton.click();

			await expect(
				commerceMiniCartPage.miniCartItemUnitOfMeasure(
					product.name.en_US
				)
			).toHaveText(unitOfMeasure2.key);
		});

		await test.step('The item stays in its delivery group', async () => {
			await page.goto(orderDetailsPageURL);

			await commerceThemeClassicOrdersPage
				.orderTabs('Multishipping')
				.click();

			await expect(
				commerceLayoutsPage.deliveryGroupQuantityInput(sku.sku)
			).toHaveValue('1');

			await commerceLayoutsPage
				.deliveryGroupCell(deliveryGroupName)
				.click();

			await expect(
				commerceLayoutsPage.deliveryGroupNameInput
			).toHaveValue(deliveryGroupName);
			await expect(
				commerceLayoutsPage.deliveryGroupDateInput
			).toHaveValue('2030-01-15');
			await expect(commerceLayoutsPage.addressRegionSelect).toHaveValue(
				'Alabama'
			);
		});
	}
);

test(
	'Returns and Shipments tabs should not be visible when the order is open',
	{tag: ['@LPD-53393', '@LPD-93819']},
	async ({
		apiHelpers,
		commerceAdminHealthCheckPage,
		commerceThemeClassicOrdersPage,
		page,
	}) => {
		let account;
		let address;
		let channel;
		let checkoutCart;
		let postCart;
		let shipment;
		let site;
		let user;

		await test.step('Assign Return permissions to User ', async () => {
			await commerceAdminHealthCheckPage.goto();

			await expect(commerceAdminHealthCheckPage.pageTitle).toBeVisible();

			if (
				await commerceAdminHealthCheckPage.userRolesButton.isEnabled()
			) {
				await commerceAdminHealthCheckPage.userRolesButton.click();
				await page.waitForTimeout(1000);
				await page.reload();
			}

			await expect(
				commerceAdminHealthCheckPage.userRolesButton
			).toBeDisabled();
		});

		await test.step('Initialize Commerce Classic Site', async () => {
			const {channel: channelSetUp, site: siteSetUp} =
				await classicCommerceSetUp(apiHelpers);

			channel = channelSetUp;
			site = siteSetUp;
		});

		await test.step('Create an Account and Buyer user', async () => {
			account = await apiHelpers.headlessAdminUser.postAccount({
				name: getRandomString(),
				type: 'business',
			});

			user = await configureBuyerUserForSite(
				account,
				apiHelpers,
				site,
				'demo.unprivileged@liferay.com'
			);

			address = await apiHelpers.headlessCommerceAdminAccount.postAddress(
				account.id,
				{phoneNumber: '323262', regionISOCode: 'AL'}
			);
		});

		await test.step('Login as a Buyer and add a product to cart, assert that no tabs are visible in pending order and checkout', async () => {
			const product =
				await apiHelpers.headlessCommerceAdminCatalog.getProducts(
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

			await performLogout(page);
			await performLoginViaApi({page, screenName: user.alternateName});

			postCart = await apiHelpers.headlessCommerceDeliveryCart.postCart(
				{
					accountId: account.id,
					cartItems: [
						{
							quantity: 1,
							replacedSkuId: 0,
							skuId: sku.id,
						},
					],
				},
				channel.id
			);

			await page.goto(
				liferayConfig.environment.baseUrl +
					`/web/${site.name}/order/${postCart.id}`
			);

			await expect(
				commerceThemeClassicOrdersPage.orderTabs('Details')
			).not.toBeVisible();
			await expect(
				commerceThemeClassicOrdersPage.orderTabs('Shipments')
			).not.toBeVisible();
			await expect(
				commerceThemeClassicOrdersPage.orderTabs('Returns')
			).not.toBeVisible();

			checkoutCart =
				await apiHelpers.headlessCommerceDeliveryCart.checkoutCart(
					postCart.id
				);
		});

		await test.step('Login as a Admin, create a shipment and complete the order', async () => {
			await performLogout(page);
			await performLoginViaApi({page, screenName: 'test'});

			await apiHelpers.headlessCommerceAdminOrder.patchOrder(
				postCart.id,
				{
					orderStatus: ORDER_WORKFLOW_STATUS_CODE.PROCESSING,
				}
			);

			const warehouses =
				await apiHelpers.headlessCommerceAdminInventoryApiHelper.getWarehousesPage();

			const warehouse = warehouses.items.filter(
				(warehouse) => warehouse.name.en_US === 'Italy'
			);

			shipment =
				await apiHelpers.headlessCommerceAdminShipment.postShipment({
					orderId: checkoutCart.id,
					shipmentItems: [
						{
							orderItemId: postCart.cartItems[0].id,
							quantity: 1,
							warehouseId: warehouse[0].id,
						},
					],
					shippingAddressId: address.id,
				});

			await apiHelpers.headlessCommerceAdminShipment.postShipmentStatusDelivered(
				shipment.id
			);

			await apiHelpers.headlessCommerceAdminOrder.patchOrder(
				postCart.id,
				{
					orderStatus: ORDER_WORKFLOW_STATUS_CODE.COMPLETED,
				}
			);
		});

		await test.step('Login as a Buyer and assert that in Placed Order only Details and Shipments tabs are visible', async () => {
			await performLogout(page);
			await performLoginViaApi({page, screenName: user.alternateName});

			await page.goto(
				liferayConfig.environment.baseUrl +
					`/web/${site.name}/order/${checkoutCart.id}`
			);

			await expect(
				commerceThemeClassicOrdersPage.orderTabs('Details')
			).toBeVisible();
			await expect(
				commerceThemeClassicOrdersPage.orderTabs('Shipments')
			).toBeVisible();
			await expect(
				commerceThemeClassicOrdersPage.orderTabs('Returns')
			).not.toBeVisible();

			await commerceThemeClassicOrdersPage.orderTabs('Shipments').click();

			await expect(page.getByText(shipment.id.toString())).toBeVisible();
		});

		await test.step('Make a return and assert that also Returns tab is shown', async () => {
			const postReturn =
				await apiHelpers.headlessCommerceReturn.postCommerceReturn({
					channelId: channel.id,
					commerceReturnToCommerceReturnItems: [
						{
							amount: checkoutCart.summary.total,
							authorized: 1,
							quantity: 1,
							r_accountToCommerceReturnItems_accountEntryId:
								account.id,
							r_commerceOrderItemToCommerceReturnItems_commerceOrderItemId:
								postCart.cartItems[0].id,
							r_commerceOrderToCommerceReturns_commerceOrderId:
								checkoutCart.id,
							received: 1,
							returnItemStatus: {
								key: 'toBeProccessed',
							},
							returnReason: {
								key: 'changeOfMind',
							},
							returnResolutionMethod: {
								key: 'refund',
							},
						},
					],
					r_accountToCommerceReturns_accountEntryId: account.id,
					r_commerceOrderToCommerceReturns_commerceOrderId:
						checkoutCart.id,
					returnStatus: {
						key: 'processing',
					},
				});

			await page.reload();

			await expect(
				commerceThemeClassicOrdersPage.orderTabs('Details')
			).toBeVisible();
			await expect(
				commerceThemeClassicOrdersPage.orderTabs('Shipments')
			).toBeVisible();
			await expect(
				commerceThemeClassicOrdersPage.orderTabs('Returns')
			).toBeVisible();

			await commerceThemeClassicOrdersPage.orderTabs('Returns').click();

			await expect(
				page.getByText(postReturn.id.toString())
			).toBeVisible();
		});
	}
);
