/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../../fixtures/apiHelpersTest';
import {applicationsMenuPageTest} from '../../../../fixtures/applicationsMenuPageTest';
import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {getRandomInt} from '../../../../utils/getRandomInt';
import getRandomString from '../../../../utils/getRandomString';
import {
	performLoginViaApi,
	performLogout,
} from '../../../../utils/performLogin';
import {waitForAlert} from '../../../../utils/waitForAlert';
import {miniumSetUp} from '../../utils/commerce';

export const test = mergeTests(
	apiHelpersTest,
	applicationsMenuPageTest,
	commercePagesTest,
	dataApiHelpersTest,
	loginTest()
);

test(
	'As admin, I can cancel an order without getting an error',
	{tag: ['@COMMERCE-11386', '@LPD-56462']},
	async ({
		apiHelpers,
		commerceAdminOrderDetailsPage,
		commerceAdminOrdersPage,
		page,
	}) => {
		const site = await apiHelpers.headlessSite.createSite({
			name: getRandomString(),
		});

		apiHelpers.data.push({id: site.id, type: 'site'});

		const channel =
			await apiHelpers.headlessCommerceAdminChannel.postChannel({
				siteGroupId: site.id,
			});

		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

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
			type: 'business',
		});

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account.id,
			['test@liferay.com']
		);

		const address =
			await apiHelpers.headlessCommerceAdminAccount.postAddress(
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
			paymentStatus: '2',
			shippingAddressId: address.id,
			shippingMethod: 'by-weight',
			shippingOption: 'standard-option',
		});

		await commerceAdminOrdersPage.goto();

		await (
			await commerceAdminOrdersPage.tableRowLink({
				colIndex: 1,
				rowValue: order.id,
			})
		).click();

		await commerceAdminOrderDetailsPage.cancelButton.click();

		await waitForAlert(page);
	}
);

test('LPD-15231 Escape account name on admin order details page', async ({
	apiHelpers,
	commerceAdminOrderDetailsPage,
	commerceAdminOrdersPage,
	page,
}) => {
	await page.goto('/');

	const site =
		await apiHelpers.headlessAdminUser.getSiteByFriendlyUrlPath('guest');

	const channel = await apiHelpers.headlessCommerceAdminChannel.postChannel({
		siteGroupId: site.id,
	});

	const account = await apiHelpers.headlessAdminUser.postAccount({
		name: '<img src="x" onError="alert(document.location)">',
	});

	await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
		account.id,
		['test@liferay.com']
	);

	const cart = await apiHelpers.headlessCommerceDeliveryCart.postCart(
		{
			accountId: account.id,
		},
		channel.id
	);

	await commerceAdminOrdersPage.goto();

	await (
		await commerceAdminOrdersPage.tableRowLink({
			colIndex: 1,
			rowValue: cart.id,
		})
	).click();

	await expect(
		commerceAdminOrderDetailsPage.headerDetailsTitle
	).toBeVisible();

	await expect(
		commerceAdminOrderDetailsPage.commerceOrderAccountEntryName
	).toHaveText(account.name);
});

test('LPD-26244 Split order items are shown on admin order details page when show separate order items toggle is enabled', async ({
	apiHelpers,
	commerceAdminChannelDetailsPage,
	commerceAdminChannelsPage,
	commerceAdminOrderDetailsPage,
	commerceAdminOrdersPage,
}) => {
	const site = await apiHelpers.headlessSite.createSite({
		name: getRandomString(),
	});

	apiHelpers.data.push({id: site.id, type: 'site'});

	const channel = await apiHelpers.headlessCommerceAdminChannel.postChannel({
		siteGroupId: site.id,
	});

	await commerceAdminChannelsPage.goto();

	await (
		await commerceAdminChannelsPage.channelsTableRowLink(channel.name)
	).click();

	await (
		await commerceAdminChannelDetailsPage.showSeparateOrderItemsToggle
	).check();

	await expect(
		await commerceAdminChannelDetailsPage.showSeparateOrderItemsToggle
	).toBeChecked();

	await (await commerceAdminChannelDetailsPage.saveButton).click();

	await expect(
		await commerceAdminChannelDetailsPage.showSeparateOrderItemsToggle
	).toBeChecked();

	const catalog = await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
		name: getRandomString(),
	});

	const product = await apiHelpers.headlessCommerceAdminCatalog.postProduct({
		catalogId: catalog.id,
		name: {en_US: getRandomString()},
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

	const cartItem1 = {
		options: '[]',
		quantity: 1,
		replacedSkuId: 0,
		skuId: sku.id,
	};

	const cartItem2 = {
		options: '[]',
		quantity: 10,
		replacedSkuId: 0,
		skuId: sku.id,
	};

	const cart = await apiHelpers.headlessCommerceDeliveryCart.postCart(
		{
			accountId: account.id,
			cartItems: [cartItem1, cartItem2],
		},
		channel.id
	);

	await commerceAdminOrdersPage.goto();

	await (
		await commerceAdminOrdersPage.tableRowLink({
			colIndex: 1,
			rowValue: cart.id,
		})
	).click();

	await expect(
		commerceAdminOrderDetailsPage.headerDetailsTitle
	).toBeVisible();

	await expect(
		(
			await commerceAdminOrderDetailsPage.tableRow(
				2,
				product.name['en_US'],
				true
			)
		).row
	).toBeVisible();

	await expect(
		(
			await commerceAdminOrderDetailsPage.tableRow(
				8,
				cartItem1.quantity,
				true
			)
		).row
	).toBeVisible();

	await expect(
		(
			await commerceAdminOrderDetailsPage.tableRow(
				8,
				cartItem2.quantity,
				true
			)
		).row
	).toBeVisible();
});

test(
	'Split order items are shown on admin shipment details page when show separate order items toggle is enabled',
	{tag: ['@LPD-39379', '@COMMERCE-12599']},
	async ({
		apiHelpers,
		commerceAdminChannelDetailsPage,
		commerceAdminChannelsPage,
		commerceAdminOrderDetailsPage,
		commerceAdminOrdersPage,
		commerceAdminShipmentsPage,
		page,
	}) => {
		const site = await apiHelpers.headlessSite.createSite({
			name: getRandomString(),
		});

		apiHelpers.data.push({id: site.id, type: 'site'});

		const channel =
			await apiHelpers.headlessCommerceAdminChannel.postChannel({
				siteGroupId: site.id,
			});

		await commerceAdminChannelsPage.goto();

		await (
			await commerceAdminChannelsPage.channelsTableRowLink(channel.name)
		).click();

		await (
			await commerceAdminChannelDetailsPage.showSeparateOrderItemsToggle
		).check();

		await expect(
			await commerceAdminChannelDetailsPage.showSeparateOrderItemsToggle
		).toBeChecked();

		await (await commerceAdminChannelDetailsPage.saveButton).click();

		await expect(
			await commerceAdminChannelDetailsPage.showSeparateOrderItemsToggle
		).toBeChecked();

		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
				name: getRandomString(),
			});

		const product =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				name: {en_US: getRandomString()},
			});

		const productSkus = await apiHelpers.headlessCommerceAdminCatalog
			.getProduct(product.productId)
			.then((product) => {
				return product.skus;
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
				account.id
			);

		const cartItem1 = {
			options: '[]',
			quantity: 3,
			replacedSkuId: 0,
			skuId: sku.id,
		};

		const cartItem2 = {
			options: '[]',
			quantity: 4,
			replacedSkuId: 0,
			skuId: sku.id,
		};

		const cart = await apiHelpers.headlessCommerceDeliveryCart.postCart(
			{
				accountId: account.id,
				billingAddressId: address.id,
				cartItems: [cartItem1, cartItem2],
				shippingAddressId: address.id,
			},
			channel.id
		);

		await apiHelpers.headlessCommerceDeliveryCart.checkoutCart(cart.id);

		await commerceAdminOrdersPage.goto();

		await (
			await commerceAdminOrdersPage.tableRowLink({
				colIndex: 1,
				rowValue: cart.id,
			})
		).click();

		await expect(
			commerceAdminOrderDetailsPage.headerDetailsTitle
		).toBeVisible();

		await expect(
			(
				await commerceAdminOrderDetailsPage.tableRow(
					2,
					product.name['en_US'],
					true
				)
			).row
		).toBeVisible();

		await expect(
			(
				await commerceAdminOrderDetailsPage.tableRow(
					8,
					cartItem1.quantity,
					true
				)
			).row
		).toBeVisible();

		await expect(
			(
				await commerceAdminOrderDetailsPage.tableRow(
					8,
					cartItem2.quantity,
					true
				)
			).row
		).toBeVisible();

		await commerceAdminOrderDetailsPage.acceptOrderButton.click();
		await commerceAdminOrderDetailsPage.createShipmentButton.click();

		await commerceAdminShipmentsPage.addProductsToShipment.click();

		await expect(
			await commerceAdminShipmentsPage.shipmentItemFrame.getByText(
				'Showing 1 to 2 of 2 entries.'
			)
		).toBeVisible();

		await expect(
			(
				await commerceAdminShipmentsPage.shipmentItemsTableRow(
					3,
					cartItem1.quantity,
					true
				)
			).row
		).toContainText('3');

		await expect(
			(
				await commerceAdminShipmentsPage.shipmentItemsTableRow(
					3,
					cartItem2.quantity,
					true
				)
			).row
		).toContainText('4');

		await (
			await commerceAdminShipmentsPage.shipmentItemsTableRowAction(
				1,
				sku.sku
			)
		).check();

		await commerceAdminShipmentsPage.shipmentsItemSubmitButton.click();
		await commerceAdminShipmentsPage.productsSkuLink(sku.sku).click();
		await commerceAdminShipmentsPage.addQuantityInShipment.fill('2');
		await commerceAdminShipmentsPage.editProductSaveButton.click();

		await waitForAlert(page.frameLocator('iframe'));

		await commerceAdminShipmentsPage.editProductCloseButton.click();
		await commerceAdminShipmentsPage
			.shipmentStatusLink('Finish Processing')
			.click();

		await waitForAlert(page);

		await commerceAdminShipmentsPage.shipmentStatusLink('Ship').click();

		await waitForAlert(page);

		await commerceAdminShipmentsPage.shipmentStatusLink('Deliver').click();

		await waitForAlert(page);

		await commerceAdminOrdersPage.goto();

		await expect(
			(await commerceAdminOrdersPage.tableRow(7, 'Partially Shipped')).row
		).toBeVisible();

		await (
			await commerceAdminOrdersPage.tableRowLink({
				colIndex: 1,
				rowValue: cart.id,
			})
		).click();

		await commerceAdminOrderDetailsPage.createShipmentButton.click();

		await commerceAdminShipmentsPage.addProductsToShipment.click();

		await expect(
			await commerceAdminShipmentsPage.shipmentItemFrame.getByText(
				'Showing 1 to 2 of 2 entries.'
			)
		).toBeVisible();

		await expect(
			(
				await commerceAdminShipmentsPage.shipmentItemsTableRow(
					3,
					cartItem1.quantity - 2,
					true
				)
			).row
		).toContainText('1');

		await expect(
			(
				await commerceAdminShipmentsPage.shipmentItemsTableRow(
					3,
					cartItem2.quantity,
					true
				)
			).row
		).toContainText('4');
	}
);

test('COMMERCE-11888. As a supplier user, I can edit the order details, payments and shipments', async ({
	apiHelpers,
	applicationsMenuPage,
	commerceAdminChannelDetailsPage,
	commerceAdminChannelsPage,
	commerceAdminOrderDetailsPage,
	commerceAdminOrdersPage,
	page,
}) => {
	test.setTimeout(180000);

	const {channel} = await miniumSetUp(apiHelpers);

	const accountBusiness = await apiHelpers.headlessAdminUser.postAccount({
		name: 'Account Business',
		type: 'business',
	});

	const phoneNumber = '12345';

	const address = await apiHelpers.headlessCommerceAdminAccount.postAddress(
		accountBusiness.id,
		{phoneNumber, regionISOCode: 'AL'}
	);

	const accountSupplier = await apiHelpers.headlessAdminUser.postAccount({
		name: 'Account Supplier',
		type: 'supplier',
	});

	await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
		accountSupplier.id,
		['demo.unprivileged@liferay.com']
	);

	const rolesResponse = await apiHelpers.headlessAdminUser.getAccountRoles(
		accountSupplier.id
	);

	const accountSupplierRole = rolesResponse?.items?.filter((role) => {
		return role.name === 'Account Supplier';
	});

	await apiHelpers.headlessAdminUser.assignAccountRoles(
		accountSupplier.externalReferenceCode,
		accountSupplierRole[0].id,
		'demo.unprivileged@liferay.com'
	);

	await apiHelpers.headlessCommerceAdminChannel.patchChannelWithAccountId(
		accountSupplier.id,
		channel
	);

	const deliveryTerm1 = await apiHelpers.headlessCommerceAdminOrder.postTerm({
		type: 'delivery-terms',
	});

	const deliveryTerm2 = await apiHelpers.headlessCommerceAdminOrder.postTerm({
		type: 'delivery-terms',
	});

	const paymentTerm1 = await apiHelpers.headlessCommerceAdminOrder.postTerm({
		type: 'payment-terms',
	});

	const paymentTerm2 = await apiHelpers.headlessCommerceAdminOrder.postTerm({
		type: 'payment-terms',
	});

	const orderType = await apiHelpers.headlessCommerceAdminOrder.postOrderType(
		{
			active: true,
		}
	);

	await commerceAdminChannelsPage.goto();
	await (
		await commerceAdminChannelsPage.channelsTableRowLink(channel.name)
	).click();
	await commerceAdminChannelDetailsPage.activateChannelConfiguration(
		'Money Order',
		'Payment Methods'
	);
	await (
		await commerceAdminChannelDetailsPage.generalCommerceAdminChannelTableLink(
			'Money Order'
		)
	).click();
	await commerceAdminChannelDetailsPage.setEntryEligibility(
		'Specific Payment Terms',
		paymentTerm1.name,
		'Payment Methods'
	);
	await commerceAdminChannelDetailsPage.activateChannelConfiguration(
		'PayPal',
		'Payment Methods'
	);
	await (
		await commerceAdminChannelDetailsPage.generalCommerceAdminChannelTableLink(
			'PayPal'
		)
	).click();
	await commerceAdminChannelDetailsPage.setEntryEligibility(
		'Specific Payment Terms',
		paymentTerm2.name,
		'Payment Methods'
	);
	await commerceAdminChannelDetailsPage.activateChannelConfiguration(
		'Flat Rate',
		'Shipping Methods'
	);
	await (
		await commerceAdminChannelDetailsPage.generalCommerceAdminChannelTableLink(
			'Flat Rate'
		)
	).click();
	await commerceAdminChannelDetailsPage.setEntryEligibility(
		'Specific Delivery Terms',
		deliveryTerm1.name,
		'Shipping Methods',
		'Expedited Delivery'
	);
	await commerceAdminChannelDetailsPage.setEntryEligibility(
		'Specific Delivery Terms',
		deliveryTerm2.name,
		'Shipping Methods',
		'Standard Delivery'
	);

	const miniumProduct =
		await apiHelpers.headlessCommerceAdminCatalog.getProducts(
			new URLSearchParams({
				filter: `name eq 'Abs Sensor'`,
			})
		);

	const miniumProductId = miniumProduct.items[0].productId;

	const productSkus = await apiHelpers.headlessCommerceAdminCatalog
		.getProduct(miniumProductId)
		.then((product) => {
			return product.skus;
		});

	const sku = productSkus[0];

	const order = await apiHelpers.headlessCommerceAdminOrder.postOrder({
		accountId: accountBusiness.id,
		billingAddressId: address.id,
		channelId: channel.id,
		orderItems: [
			{
				quantity: 2,
				skuId: sku.id,
			},
		],
		orderStatus: '1',
		paymentMethod: 'paypal-integration',
		paymentStatus: '0',
		shippingAddressId: address.id,
	});

	apiHelpers.data.push({id: order.id, type: 'order'});

	await performLogout(page);
	await performLoginViaApi({page, screenName: 'demo.unprivileged'});

	await applicationsMenuPage.goToCommerceOrders(false);

	await (
		await commerceAdminOrdersPage.tableRowLink({
			colIndex: 1,
			rowValue: order.id,
		})
	).click();

	await expect(
		page.getByText('PendingProcessingShippedCompleted')
	).toBeVisible();

	await expect(
		await commerceAdminOrderDetailsPage.editEntryActionLink(
			'Billing Address Edit',
			'Edit'
		)
	).toBeVisible();

	await (
		await commerceAdminOrderDetailsPage.editEntryActionLink(
			'Billing Address Edit',
			'Edit'
		)
	).click();

	await (
		await commerceAdminOrderDetailsPage.editEntryActionLink(
			'Billing Address Edit',
			'Edit'
		)
	).waitFor();

	await expect(
		await commerceAdminOrderDetailsPage.orderDetailsModalHeader(
			'Edit Billing Address'
		)
	).toBeVisible();

	const city = 'City';
	const country = 'Italy';
	const region = 'Lombardia';
	const streetName = 'Street1';
	const zip = 'zip';

	await commerceAdminOrderDetailsPage.editAddress(
		city,
		country,
		region,
		streetName,
		zip
	);

	await page.waitForLoadState('domcontentloaded');

	await expect(
		page.getByText('PendingProcessingShippedCompleted')
	).toBeVisible();

	await expect(
		await commerceAdminOrderDetailsPage.orderDetailsEntryDescription(
			'Billing Address'
		)
	).toContainText(`${city}, ${region}, ${zip}`);

	await expect(
		await commerceAdminOrderDetailsPage.orderDetailsEntryDescription(
			'Shipping Address'
		)
	).toContainText(`${city}, ${region}, ${zip}`);

	await expect(
		await commerceAdminOrderDetailsPage.editEntryActionLink(
			'Payment Terms Add',
			'Add'
		)
	).toBeVisible();

	await (
		await commerceAdminOrderDetailsPage.editEntryActionLink(
			'Payment Terms Add',
			'Add'
		)
	).click();

	await commerceAdminOrderDetailsPage.selectPaymentTerms.click();
	await commerceAdminOrderDetailsPage.selectPaymentTerms.selectOption(
		paymentTerm1.id.toString()
	);
	await commerceAdminOrderDetailsPage.submitModalButton.click();

	await page.waitForLoadState('domcontentloaded');

	await expect(
		await commerceAdminOrderDetailsPage.orderDetailsEntryDescription(
			'Payment Terms'
		)
	).toContainText(paymentTerm1.label['en_US']);

	await (
		await commerceAdminOrderDetailsPage.editEntryActionLink(
			'Delivery Terms Add',
			'Add'
		)
	).scrollIntoViewIfNeeded();

	await (
		await commerceAdminOrderDetailsPage.editEntryActionLink(
			'Delivery Terms Add',
			'Add'
		)
	).click();

	await commerceAdminOrderDetailsPage.selectDeliveryTerms.click();

	await commerceAdminOrderDetailsPage.selectDeliveryTerms.selectOption(
		deliveryTerm1.id.toString()
	);
	await commerceAdminOrderDetailsPage.submitModalButton.click();

	await expect(
		await commerceAdminOrderDetailsPage.orderDetailsEntryDescription(
			'Delivery Terms'
		)
	).toContainText(deliveryTerm1.label['en_US']);
	await expect(
		await commerceAdminOrderDetailsPage.orderDetailsEntryDescription(
			'Order Type'
		)
	).toContainText(orderType.name['en_US']);

	await commerceAdminOrderDetailsPage.orderSummaryLink.click();

	await commerceAdminOrderDetailsPage.orderSummarySubtotalInput.fill('2');

	await commerceAdminOrderDetailsPage.orderSummarySaveButton.click();

	await expect(
		await commerceAdminOrderDetailsPage.orderSummarySubtotal
	).toContainText('2');

	await commerceAdminOrderDetailsPage.orderItemActions.click();

	await commerceAdminOrderDetailsPage.orderItemActionEdit.click();

	await commerceAdminOrderDetailsPage.orderItemDecimalQuantity.fill('3');

	await commerceAdminOrderDetailsPage.orderItemSaveButton.click();

	await expect(
		await commerceAdminOrderDetailsPage.orderItemQuantityColumn('3')
	).toBeVisible();

	await commerceAdminOrderDetailsPage.orderItemFrameCloseButton.click();

	await (
		await commerceAdminOrderDetailsPage.orderDetailsTab(
			'Questions and Answers'
		)
	).click();

	await commerceAdminOrderDetailsPage.orderNotesTextArea.fill('Note test');

	await commerceAdminOrderDetailsPage.saveButton.click();

	await expect(
		await commerceAdminOrderDetailsPage.orderNote('Note test')
	).toBeVisible();

	await (
		await commerceAdminOrderDetailsPage.orderDetailsTab('Payments')
	).click();
	await (
		await commerceAdminOrderDetailsPage.editEntryActionLink(
			'Payment Method Edit',
			'Edit'
		)
	).click();
	await (
		await commerceAdminOrderDetailsPage.paymentMethodRadioButton('PayPal')
	).check();
	await commerceAdminOrderDetailsPage.submitPaymentMethod.click();

	await page.waitForLoadState('domcontentloaded');

	await expect(page.getByText('PayPal', {exact: true})).toBeVisible();
});

test('LPD-30856 Can update order status by deleting unshipped items', async ({
	apiHelpers,
	commerceAdminOrdersPage,
	commerceAdminShipmentsPage,
	page,
}) => {
	const site = await apiHelpers.headlessSite.createSite({
		name: getRandomString(),
	});

	apiHelpers.data.push({id: site.id, type: 'site'});

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

	const product2 = await apiHelpers.headlessCommerceAdminCatalog.postProduct({
		catalogId: catalog.id,
		name: {en_US: 'Product2'},
	});

	const productSkus2 = await apiHelpers.headlessCommerceAdminCatalog
		.getProduct(product2.productId)
		.then((product) => {
			return product.skus;
		});

	const sku2 = productSkus2[0];

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
					{
						quantity: 1,
						sku: sku2.sku,
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
			{
				quantity: 1,
				skuId: sku2.id,
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
	await (
		await commerceAdminOrdersPage.tableRowLink({
			colIndex: 1,
			rowValue: order.id,
		})
	).click();
	await commerceAdminOrdersPage.orderStatusLink('Accept Order').click();
	await commerceAdminOrdersPage.orderStatusLink('Create Shipment').click();

	await waitForAlert(page);

	await commerceAdminShipmentsPage.addProductsToShipment.click();
	await (
		await commerceAdminShipmentsPage.shipmentItemsTableRowAction(
			1,
			sku1.sku
		)
	).check();
	await commerceAdminShipmentsPage.shipmentsItemSubmitButton.click();
	await commerceAdminShipmentsPage.productEllipsis.click();
	await commerceAdminShipmentsPage.editProductMenuItem.click();
	await commerceAdminShipmentsPage.addQuantityInShipment.fill('1');
	await commerceAdminShipmentsPage.editProductSaveButton.click();
	await commerceAdminShipmentsPage.editProductCloseButton.click();
	await commerceAdminShipmentsPage
		.shipmentStatusLink('Finish Processing')
		.click();
	await commerceAdminShipmentsPage.shipmentStatusLink('Ship').click();

	await waitForAlert(page);

	const shipments =
		await apiHelpers.headlessCommerceAdminShipment.getShipments();

	await commerceAdminOrdersPage.goto();
	await expect(
		commerceAdminOrdersPage.keyOrderStatus('Partially Shipped')
	).toBeVisible();
	await (
		await commerceAdminOrdersPage.tableRowLink({
			colIndex: 1,
			rowValue: order.id,
		})
	).click();
	await (await commerceAdminOrdersPage.itemsTableRowAction(sku2.sku)).click();
	await commerceAdminOrdersPage.deleteItemMenuItem.click();

	await waitForAlert(page);

	await commerceAdminOrdersPage.backLink.click();
	await commerceAdminOrdersPage
		.keyOrderStatus('Shipped')
		.waitFor({state: 'visible'});
	await expect(
		commerceAdminOrdersPage.keyOrderStatus('Shipped')
	).toBeVisible();

	await commerceAdminShipmentsPage.goTo();

	await expect(
		commerceAdminShipmentsPage.keyShipmentStatus('Shipped')
	).toBeVisible();
	await commerceAdminShipmentsPage
		.shipmentIdLink(shipments.items[0].id)
		.click();
	await commerceAdminShipmentsPage.shipmentStatusLink('Deliver').click();
	await commerceAdminShipmentsPage.backLink.click();
	await expect(
		commerceAdminShipmentsPage.keyShipmentStatus('Delivered')
	).toBeVisible();

	await commerceAdminOrdersPage.goto();

	await expect(
		commerceAdminOrdersPage.keyOrderStatus('Completed')
	).toBeVisible();
});
