/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../../fixtures/apiHelpersTest';
import {applicationsMenuPageTest} from '../../../../fixtures/applicationsMenuPageTest';
import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {systemSettingsPageTest} from '../../../../fixtures/systemSettingsPageTest';
import {liferayConfig} from '../../../../liferay.config';
import {getRandomInt} from '../../../../utils/getRandomInt';
import getRandomString from '../../../../utils/getRandomString';
import performLogin, {performLogout} from '../../../../utils/performLogin';
import {waitForAlert} from '../../../../utils/waitForAlert';
import {ORDER_WORKFLOW_STATUS_CODE} from '../../../workspaces/liferay-workspace-marketplace/main/utils/constants';
import {classicCommerceSetUp} from '../../utils/commerce';

export const test = mergeTests(
	apiHelpersTest,
	applicationsMenuPageTest,
	commercePagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-10562': {enabled: true},
		'LPD-20379': {enabled: true},
	}),
	loginTest(),
	systemSettingsPageTest
);

test(
	'Commerce Classic Header main fragment is correctly displayed',
	{tag: ['@LPD-23780']},
	async ({apiHelpers, page}) => {
		test.setTimeout(180000);

		const {site} = await classicCommerceSetUp(
			apiHelpers,
			`classic-commerce`
		);

		await page.goto(`/web${site.friendlyUrlPath}`);

		await page.getByRole('link', {exact: true, name: 'Edit'}).click();
		await page.getByLabel('Page Design Options').click();
		await page.getByLabel('Commerce Classic Master').click();
		await page.getByLabel('Publish', {exact: true}).click();

		const commerceHeaderTagFragments = page.locator(
			'#commerce-components-group'
		);

		await expect(commerceHeaderTagFragments).toBeVisible();
		await expect(
			commerceHeaderTagFragments.locator('.account-selector-root')
		).toHaveClass(/mr-2/);
		await expect(
			commerceHeaderTagFragments.locator('.cart-root')
		).toBeVisible();
		await expect(page.locator('header .portlet-search-bar')).toBeVisible();
	}
);

test(
	'Multishipping tab displays correctly when enabled',
	{tag: ['@LPD-35323']},
	async ({
		apiHelpers,
		commerceAdminChannelDetailsPage,
		commerceAdminChannelsPage,
		page,
	}) => {
		test.setTimeout(180000);

		const {catalog, channel, site} = await classicCommerceSetUp(apiHelpers);

		const account = await apiHelpers.headlessAdminUser.postAccount({
			name: getRandomString(),
			type: 'business',
		});

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

		const orderDetailsPageURL =
			liferayConfig.environment.baseUrl +
			`/web/${site.name}/order/${cart.id}`;

		await page.goto(orderDetailsPageURL);

		const multishippingTab = page.getByRole('tab', {name: 'Multishipping'});

		await expect(multishippingTab).toHaveCount(0);

		await commerceAdminChannelsPage.goto();

		await (
			await commerceAdminChannelsPage.channelsTableRowLink(channel.name)
		).click();

		await commerceAdminChannelDetailsPage.allowMultishippingToggle.setChecked(
			true
		);

		await expect(
			commerceAdminChannelDetailsPage.allowMultishippingToggle
		).toBeChecked();

		await commerceAdminChannelDetailsPage.saveButton.click();

		await expect(
			commerceAdminChannelDetailsPage.allowMultishippingToggle
		).toBeChecked();

		await waitForAlert(page);

		await page.goto(orderDetailsPageURL);

		await expect(multishippingTab).toBeVisible();
	}
);

test(
	'Returns and Shipments tabs should not be visible when the order is open',
	{tag: ['@LPD-53393']},
	async ({
		apiHelpers,
		commerceAdminHealthCheckPage,
		commerceThemeClassicOrdersPage,
		page,
	}) => {
		test.setTimeout(180000);

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

			await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
				account.id,
				['demo.unprivileged@liferay.com']
			);

			user =
				await apiHelpers.headlessAdminUser.getUserAccountByEmailAddress(
					'demo.unprivileged@liferay.com'
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

			address = await apiHelpers.headlessCommerceAdminAccount.postAddress(
				account.id,
				{phoneNumber: '323262', regionISOCode: 'AL'}
			);
		});

		await test.step('Login as a Buyer and add a product to cart, assert that only Details tab is visible in pending order and checkout', async () => {
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

			await performLogin(page, user.alternateName);

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
			).toBeVisible();
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

			await performLogin(page, 'test');

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

			await performLogin(page, user.alternateName);

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
