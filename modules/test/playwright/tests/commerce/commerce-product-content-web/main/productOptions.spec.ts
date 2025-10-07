/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {applicationsMenuPageTest} from '../../../../fixtures/applicationsMenuPageTest';
import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {getRandomInt} from '../../../../utils/getRandomInt';
import getRandomString from '../../../../utils/getRandomString';
import {
	performLoginViaApi,
	performLogout,
	userData,
} from '../../../../utils/performLogin';
import {waitForAlert} from '../../../../utils/waitForAlert';
import {
	classicCommerceSetUp,
	configureBuyerUserForSite,
} from '../../utils/commerce';

export const test = mergeTests(
	applicationsMenuPageTest,
	commercePagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-20379': {enabled: true},
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest()
);

test(
	'User can add to cart and checkout a product with upload option',
	{tag: ['@LPD-58776']},
	async ({
		apiHelpers,
		checkoutPage,
		commerceMiniCartPage,
		commerceThemeClassicOrdersPage,
		page,
		productDetailsPage,
	}) => {
		let catalog;
		let fileName;
		let productWithUploadOption;
		let site;

		await test.step('Initialize Commerce Classic Site', async () => {
			const {catalog: catalogSetup, site: siteSetup} =
				await classicCommerceSetUp(apiHelpers, getRandomString());

			catalog = catalogSetup;
			site = siteSetup;
		});

		await test.step('Create an Account and a Buyer user', async () => {
			const account = await apiHelpers.headlessAdminUser.postAccount({
				name: getRandomString(),
				type: 'business',
			});

			await configureBuyerUserForSite(
				account,
				apiHelpers,
				site,
				'demo.unprivileged@liferay.com'
			);
		});

		await test.step('Create a product with upload option', async () => {
			const uploadOption =
				await apiHelpers.headlessCommerceAdminCatalog.postOption(
					'document_library',
					'Upload',
					'Upload',
					1
				);

			productWithUploadOption =
				await apiHelpers.headlessCommerceAdminCatalog.postProduct({
					catalogId: catalog.id,
					name: {en_US: 'ProductWithUploadOption'},
					productOptions: [
						{
							fieldType: 'document_library',
							key: 'upload',
							name: {
								en_US: 'Upload',
							},
							optionId: uploadOption.id,
							priceType: '',
							priority: 1,
						},
					],
					skus: [
						{
							cost: 0,
							price: 10,
							published: true,
							purchasable: true,
							sku: 'Sku' + getRandomInt(),
						},
					],
				});
		});

		await test.step('Upload a document to the product', async () => {
			await performLogout(page);
			await performLoginViaApi({page, screenName: 'demo.unprivileged'});

			await page.goto(
				`/web/${site.name}/p/${productWithUploadOption.name['en_US']}`,
				{waitUntil: 'networkidle'}
			);

			await expect(page.getByText('UploadSelect')).toBeVisible();

			await productDetailsPage.optionSelector('Upload').click();

			await expect(
				page.getByRole('heading', {name: 'Select Document'})
			).toBeVisible();

			await productDetailsPage.selectDocumentFrame
				.getByRole('link', {name: 'Commerce'})
				.click();

			fileName = '400_color.svg';

			await productDetailsPage.selectDocumentFrame
				.getByText(fileName)
				.click();

			await expect(productDetailsPage.selectedDocumentLabel).toHaveValue(
				fileName
			);
		});

		try {
			await test.step('Add the product to the cart and check-out', async () => {
				await productDetailsPage.addToCartButton.click();

				await commerceMiniCartPage.miniCartButton.click();

				await expect(
					commerceMiniCartPage.miniCartItem(
						productWithUploadOption.name['en_US']
					)
				).toBeVisible();

				await expect(
					commerceMiniCartPage.miniCartTotalPrice
				).toHaveText('$ 10.00');

				await commerceMiniCartPage.submitButton.click();

				await checkoutPage.performCheckout({
					shippingAddress: {
						city: 'testCity',
						countryLabel: 'United States',
						name: 'John Doe',
						regionLabel: 'Florida',
						street: 'testStreet',
						zip: '12345',
					},
				});
			});

			await test.step('Navigate to order details and assert that the file are shown', async () => {
				await checkoutPage.goToOrderDetailsButton.click();

				await expect(
					(
						await commerceThemeClassicOrdersPage.orderItemsTableRow(
							2,
							productWithUploadOption.productOptions[0].name[
								'en_US'
							]
						)
					).column.getByText(
						productWithUploadOption.productOptions[0].name['en_US']
					)
				).toBeVisible();
				await expect(
					(
						await commerceThemeClassicOrdersPage.orderItemsTableRow(
							2,
							fileName
						)
					).column.getByText(fileName)
				).toBeVisible();
				await expect(
					(
						await commerceThemeClassicOrdersPage.orderItemsTableRow(
							9,
							'$ 10.00',
							true
						)
					).column.getByText('$ 10.00')
				).toBeVisible();
			});
		}
		finally {
			await performLogout(page);
			await performLoginViaApi({page, screenName: 'test'});

			const orders =
				await apiHelpers.headlessCommerceAdminOrder.getOrdersPage();

			if (orders.items[0]) {
				apiHelpers.data.push({id: orders.items[0].id, type: 'order'});
			}
		}
	}
);

test(
	'Bundled product shows option values in the orders admin',
	{tag: ['@LPD-61820']},
	async ({
		apiHelpers,
		checkoutPage,
		commerceAdminOrderDetailsPage,
		commerceAdminOrdersPage,
		commerceAdminProductPage,
		commerceMiniCartPage,
		commerceThemeClassicCatalogPage,
		commerceThemeClassicOrdersPage,
		page,
		productDetailsPage,
	}) => {
		test.setTimeout(90000);

		let account: any;
		let buyerUser: any;
		let catalog;
		let operationsManagerUser: any;
		let option1;
		let option2;
		let option3;
		let option4;
		let option5;
		let option6;
		let option7;
		let optionValues;
		let productBundle;
		let product1;
		let product2;
		let site;

		await test.step('Initialize Commerce Classic Site', async () => {
			const {catalog: catalogSetup, site: siteSetup} =
				await classicCommerceSetUp(apiHelpers, getRandomString());

			catalog = catalogSetup;
			site = siteSetup;
		});

		await test.step('Create a Business Account, a Buyer and an Operations Manager', async () => {
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

			operationsManagerUser =
				await apiHelpers.headlessAdminUser.postUserAccount();

			userData[operationsManagerUser.alternateName] = {
				name: operationsManagerUser.givenName,
				password: 'test',
				surname: operationsManagerUser.familyName,
			};

			const companyId = await page.evaluate(() => {
				return Liferay.ThemeDisplay.getCompanyId();
			});

			const role = await apiHelpers.headlessAdminUser.postRole({
				name: 'Test Role ' + getRandomString(),
				rolePermissions: [
					{
						actionIds: ['ACCESS_IN_CONTROL_PANEL'],
						primaryKey: companyId,
						resourceName:
							'com_liferay_commerce_inventory_web_internal_portlet_CommerceInventoryPortlet',
						scope: 1,
					},
					{
						actionIds: ['ACCESS_IN_CONTROL_PANEL'],
						primaryKey: companyId,
						resourceName:
							'com_liferay_commerce_order_web_internal_portlet_CommerceOrderPortlet',
						scope: 1,
					},
					{
						actionIds: ['ACCESS_IN_CONTROL_PANEL'],
						primaryKey: companyId,
						resourceName:
							'com_liferay_commerce_shipment_web_internal_portlet_CommerceShipmentPortlet',
						scope: 1,
					},
					{
						actionIds: ['ACCESS_IN_CONTROL_PANEL'],
						primaryKey: companyId,
						resourceName:
							'com_liferay_commerce_subscription_web_internal_portlet_CommerceSubscriptionEntryPortlet',
						scope: 1,
					},
					{
						actionIds: ['VIEW'],
						primaryKey: companyId,
						resourceName: 'com.liferay.account.model.AccountEntry',
						scope: 1,
					},
					{
						actionIds: ['MANAGE_INVENTORY'],
						primaryKey: companyId,
						resourceName: 'com.liferay.commerce.inventory',
						scope: 1,
					},
					{
						actionIds: ['VIEW'],
						primaryKey: companyId,
						resourceName:
							'com.liferay.commerce.inventory.model.CommerceInventoryWarehouse',
						scope: 1,
					},
					{
						actionIds: ['VIEW'],
						primaryKey: companyId,
						resourceName:
							'com.liferay.commerce.model.CommerceOrder',
						scope: 1,
					},
					{
						actionIds: [
							'MANAGE_COMMERCE_ORDER_PAYMENT_TERMS',
							'MANAGE_COMMERCE_ORDERS',
							'MANAGE_COMMERCE_ORDER_PAYMENT_STATUSES',
							'MANAGE_COMMERCE_ORDER_DELIVERY_TERMS',
						],
						primaryKey: companyId,
						resourceName: 'com.liferay.commerce.order',
						scope: 1,
					},
					{
						actionIds: [
							'MANAGE_COMMERCE_PRODUCT_MEASUREMENT_UNITS',
						],
						primaryKey: companyId,
						resourceName: 'com.liferay.commerce.product',
						scope: 1,
					},
					{
						actionIds: [
							'MANAGE_ALL_ACCOUNTS',
							'MANAGE_COMMERCE_SHIPMENTS',
						],
						primaryKey: companyId,
						resourceName: 'com.liferay.commerce.shipment',
						scope: 1,
					},
					{
						actionIds: ['MANAGE_COMMERCE_SUBSCRIPTIONS'],
						primaryKey: companyId,
						resourceName: 'com.liferay.commerce.subscription',
						scope: 1,
					},
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
				],
			});

			await apiHelpers.headlessAdminUser.postRoleByExternalReferenceCodeUserAccountAssociation(
				role.externalReferenceCode,
				operationsManagerUser.id
			);

			const siteRole =
				await apiHelpers.headlessAdminUser.getRoleByName('Site Member');

			await apiHelpers.headlessAdminUser.assignUserToSite(
				siteRole.id,
				site.id,
				operationsManagerUser.id
			);
			await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
				account.id,
				[operationsManagerUser.emailAddress]
			);
		});

		await test.step('Create a bundled product with all types of options and generate all skus combinations', async () => {
			option1 = await apiHelpers.headlessCommerceAdminCatalog.postOption(
				'select',
				'color',
				'Color',
				1
			);

			option2 = await apiHelpers.headlessCommerceAdminCatalog.postOption(
				'checkbox',
				'option-checkbox-key',
				'checkbox',
				1
			);

			option3 = await apiHelpers.headlessCommerceAdminCatalog.postOption(
				'date',
				'option-date-key',
				'date',
				1
			);

			option4 = await apiHelpers.headlessCommerceAdminCatalog.postOption(
				'numeric',
				'option-numeric-key',
				'numeric',
				1
			);

			option5 = await apiHelpers.headlessCommerceAdminCatalog.postOption(
				'text',
				'option-text-key',
				'text',
				1
			);

			option6 = await apiHelpers.headlessCommerceAdminCatalog.postOption(
				'checkbox_multiple',
				'option-checkbox_multiple-key',
				'checkbox_multiple',
				1
			);

			option7 = await apiHelpers.headlessCommerceAdminCatalog.postOption(
				'radio',
				'option-radio-key',
				'radio',
				1
			);

			product1 =
				await apiHelpers.headlessCommerceAdminCatalog.postProduct({
					catalogId: catalog.id,
					name: {en_US: 'Product1'},
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
			product2 =
				await apiHelpers.headlessCommerceAdminCatalog.postProduct({
					catalogId: catalog.id,
					name: {en_US: 'Product2'},
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

			productBundle =
				await apiHelpers.headlessCommerceAdminCatalog.postProduct({
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
									key: 'black',
									name: {
										en_US: 'Black',
									},
									priority: 1,
									quantity: 1,
									skuId: product1.skus[0].id,
								},
								{
									deltaPrice: 30.0,
									key: 'white',
									name: {
										en_US: 'White',
									},
									priority: 2,
									quantity: 1,
									skuId: product2.skus[0].id,
								},
							],
							required: true,
							skuContributor: true,
						},
						{
							fieldType: option2.fieldType,
							key: option2.key,
							name: {
								en_US: 'checkbox',
							},
							optionId: option2.id,
							priority: 2,
						},
						{
							fieldType: option3.fieldType,
							key: option3.key,
							name: {
								en_US: 'date',
							},
							optionId: option3.id,
							priority: 2,
						},
						{
							fieldType: option4.fieldType,
							key: option4.key,
							name: {
								en_US: 'numeric',
							},
							optionId: option4.id,
							priority: 2,
						},
						{
							fieldType: option5.fieldType,
							key: option5.key,
							name: {
								en_US: 'text',
							},
							optionId: option5.id,
							priority: 2,
						},
						{
							fieldType: option6.fieldType,
							key: option6.key,
							name: {
								en_US: 'checkbox_multiple',
							},
							optionId: option6.id,
							priority: 2,
							productOptionValues: [
								{
									deltaPrice: 20.0,
									key: 'value1',
									name: {
										en_US: 'value1',
									},
									priority: 1,
									quantity: 1,
								},
								{
									deltaPrice: 30.0,
									key: 'value2',
									name: {
										en_US: 'value2',
									},
									priority: 2,
									quantity: 1,
								},
							],
						},
						{
							fieldType: option7.fieldType,
							key: option7.key,
							name: {
								en_US: 'radio',
							},
							optionId: option7.id,
							priority: 2,
							productOptionValues: [
								{
									deltaPrice: 20.0,
									key: 'value3',
									name: {
										en_US: 'value3',
									},
									priority: 1,
									quantity: 1,
								},
								{
									deltaPrice: 30.0,
									key: 'value4',
									name: {
										en_US: 'value4',
									},
									priority: 2,
									quantity: 1,
								},
							],
						},
					],
				});

			await commerceAdminProductPage.gotoProduct(
				productBundle.name['en_US']
			);
			await commerceAdminProductPage.generateSkus();
		});

		try {
			await test.step('As Buyer navigate in product details page, create an order', async () => {
				await performLogout(page);
				await performLoginViaApi({
					page,
					screenName: buyerUser.alternateName,
				});

				await page.goto(`/web/${site.name}`);

				await expect(
					commerceThemeClassicCatalogPage.productCard(
						productBundle.name.en_US
					)
				).toBeVisible();

				await commerceThemeClassicCatalogPage
					.productCardImage(productBundle.name.en_US)
					.click();

				await expect(
					await productDetailsPage.nameField(productBundle.name.en_US)
				).toBeVisible();

				await page.getByLabel('date').fill('2025-10-10');
				await page.getByLabel('numeric').fill('10');
				await page.getByLabel('text').fill('Text');

				await expect(async () => {
					await page.getByLabel('checkbox').check();
					await page.getByRole('checkbox', {name: 'value1'}).check();
					await page.getByRole('checkbox', {name: 'value2'}).check();
					await page.getByRole('radio', {name: 'value4'}).check();
				}).toPass();

				await productDetailsPage.addToCartButton.click();

				await page.waitForLoadState('networkidle');
			});

			await test.step('Open the mini cart, submit and check-out the order', async () => {
				await commerceMiniCartPage.miniCartButton.click();

				await expect(
					commerceMiniCartPage.miniCartItem(productBundle.name.en_US)
				).toHaveCount(1);

				await commerceMiniCartPage.submitButton.click();

				await checkoutPage.performCheckout({
					shippingAddress: {
						city: 'testCity',
						countryLabel: 'United States',
						name: 'John Doe',
						regionLabel: 'Florida',
						street: 'testStreet',
						zip: '12345',
					},
				});
			});

			await test.step('Navigate to order details and assert that the bundled product is shown with the correct option values', async () => {
				await checkoutPage.goToOrderDetailsButton.click();

				optionValues = [
					/Black/,
					/option-checkbox-key/,
					/2025-10-10/,
					/10/,
					/Text/,
					/value1/,
					/value2/,
					/value4/,
				];

				const optionValuesColumn = (
					await commerceThemeClassicOrdersPage.orderItemsTableRow(
						2,
						'option-checkbox-key'
					)
				).column;

				for (const optionValue of optionValues) {
					await expect(optionValuesColumn).toContainText(optionValue);
				}

				await commerceThemeClassicOrdersPage.expandProductButton.click();

				await expect(
					(
						await commerceThemeClassicOrdersPage.orderItemsTableRow(
							3,
							product1.name['en_US'],
							true
						)
					).column
				).toBeVisible();
			});

			await test.step('As Operations Manager, the bundled product is shown with the correct option values on the order admin page', async () => {
				await performLogout(page);
				await performLoginViaApi({
					page,
					screenName: operationsManagerUser.alternateName,
				});

				await commerceAdminOrdersPage.goto();

				await expect(
					(await commerceAdminOrdersPage.tableRow(7, 'Pending')).row
				).toBeVisible();

				await (await commerceAdminOrdersPage.tableRow(7, 'Pending')).row
					.getByRole('link')
					.click();

				await expect(
					commerceAdminOrderDetailsPage.headerDetailsTitle
				).toBeVisible();

				await expect(
					(
						await commerceAdminOrderDetailsPage.tableRow(
							2,
							productBundle.name['en_US'],
							true
						)
					).row
				).toBeVisible();

				const optionValuesRow = (
					await commerceAdminOrderDetailsPage.tableRow(
						3,
						'option-checkbox-key'
					)
				).row;

				for (const optionValue of optionValues) {
					await expect(optionValuesRow).toContainText(optionValue);
				}

				await commerceAdminOrderDetailsPage.expandProductButton.click();

				await expect(
					(
						await commerceAdminOrderDetailsPage.tableRow(
							2,
							product1.name['en_US'],
							true
						)
					).row
				).toBeVisible();

				await expect(
					await commerceAdminOrderDetailsPage.orderSummarySubtotal
				).toContainText('$ 20.00');
			});
		}
		finally {
			await performLogout(page);
			await performLoginViaApi({page, screenName: 'test'});

			const orders =
				await apiHelpers.headlessCommerceAdminOrder.getOrdersPage();

			if (orders.items[0]) {
				apiHelpers.data.push({id: orders.items[0].id, type: 'order'});
			}
		}
	}
);

test(
	'Option selector shows informations correctly',
	{tag: ['@LPD-61820']},
	async ({
		apiHelpers,
		commerceAdminProductDetailsSkusPage,
		commerceAdminProductPage,
		page,
		productDetailsPage,
	}) => {
		let catalog;
		let product1;
		let site;

		await test.step('Initialize Commerce Classic Site', async () => {
			const {catalog: catalogSetup, site: siteSetup} =
				await classicCommerceSetUp(apiHelpers, getRandomString());

			catalog = catalogSetup;
			site = siteSetup;
		});

		await test.step('Create an Account and a Buyer user', async () => {
			const account = await apiHelpers.headlessAdminUser.postAccount({
				name: getRandomString(),
				type: 'business',
			});

			await configureBuyerUserForSite(
				account,
				apiHelpers,
				site,
				'demo.unprivileged@liferay.com'
			);
		});

		await test.step('Create a simple product with four values sku contributor option', async () => {
			const option1 =
				await apiHelpers.headlessCommerceAdminCatalog.postOption(
					'select',
					'size',
					'Size',
					1
				);

			const option2 =
				await apiHelpers.headlessCommerceAdminCatalog.postOption(
					'select',
					'color',
					'Color',
					1
				);

			product1 =
				await apiHelpers.headlessCommerceAdminCatalog.postProduct({
					catalogId: catalog.id,
					name: {en_US: 'Product1'},
					productOptions: [
						{
							fieldType: 'select',
							key: 'color',
							name: {
								en_US: 'Color',
							},
							optionId: option1.id,
							position: 1,
							priority: 1,
							productOptionValues: [
								{
									deltaPrice: 20.0,
									key: 'black',
									name: {
										en_US: 'Black',
									},
									position: 1,
									priority: 1,
									quantity: 1,
								},
								{
									deltaPrice: 30.0,
									key: 'white',
									name: {
										en_US: 'White',
									},
									priority: 2,
									quantity: 1,
								},
								{
									deltaPrice: 40.0,
									key: 'blue',
									name: {
										en_US: 'Blue',
									},
									priority: 2,
									quantity: 1,
								},
								{
									deltaPrice: 50.0,
									key: 'red',
									name: {
										en_US: 'Red',
									},
									priority: 2,
									quantity: 1,
								},
							],
							skuContributor: true,
						},
						{
							fieldType: option2.fieldType,
							key: option2.key,
							name: {
								en_US: 'Size',
							},
							optionId: option2.id,
							position: 0,
						},
					],
				});
		});

		await test.step('Add only two of the four values as SKUs from SKUs tab of the Product', async () => {
			await commerceAdminProductPage.gotoProduct(product1.name['en_US']);

			await commerceAdminProductPage.addSku('Red', {
				name: 'Color',
				value: 'Red',
			});

			await commerceAdminProductDetailsSkusPage.goToSkuTab('Price');
			await commerceAdminProductDetailsSkusPage
				.sidePanelSkuPriceTableRowLink(
					`${catalog.name} Base Price List`
				)
				.click();

			await commerceAdminProductDetailsSkusPage.sidePanelNestedPriceListPrice.fill(
				'10'
			);
			await commerceAdminProductDetailsSkusPage.sidePanelNestedSaveButton.click();

			await waitForAlert(
				commerceAdminProductDetailsSkusPage.sidePanelNestedFrame
			);

			await page.reload();

			await commerceAdminProductPage.addSku('Black', {
				name: 'Color',
				value: 'Black',
			});

			await commerceAdminProductDetailsSkusPage.goToSkuTab('Price');
			await commerceAdminProductDetailsSkusPage
				.sidePanelSkuPriceTableRowLink(
					`${catalog.name} Base Price List`
				)
				.click();

			await commerceAdminProductDetailsSkusPage.sidePanelNestedPriceListPrice.fill(
				'20'
			);
			await commerceAdminProductDetailsSkusPage.sidePanelNestedSaveButton.click();

			await waitForAlert(
				commerceAdminProductDetailsSkusPage.sidePanelNestedFrame
			);
		});

		await test.step('As Buyer go to product detail page', async () => {
			await performLogout(page);
			await performLoginViaApi({page, screenName: 'demo.unprivileged'});

			await page.goto(`/web/${site.name}/p/${product1.name['en_US']}`);
		});

		await test.step('Assert that the option with lower position is the first', async () => {
			const firstOptionBox = await productDetailsPage
				.optionSelector('Color')
				.boundingBox();
			const secondOptionBox = await productDetailsPage
				.optionSelector('Size')
				.boundingBox();

			expect(secondOptionBox.y).toBeLessThan(firstOptionBox.y);
		});

		await test.step('Also assert that only the 2 values set for the SKUs before are shown', async () => {
			await productDetailsPage.optionSelector('Color').click();

			const optionSelectorOptionsValues = await productDetailsPage
				.optionSelector('Color')
				.locator('option')
				.allTextContents();

			expect(optionSelectorOptionsValues).toEqual([
				'Choose an Option',
				'Black',
				'Red',
			]);
		});
	}
);

test(
	'Can configure and checkout a bundled product with different catalog sku linked',
	{tag: ['@LPD-61820']},
	async ({
		apiHelpers,
		checkoutPage,
		commerceAdminProductDetailsSkusPage,
		commerceAdminProductPage,
		commerceMiniCartPage,
		commerceThemeClassicOrdersPage,
		page,
		placedOrdersPage,
		productDetailsPage,
	}) => {
		test.setTimeout(120000);

		let catalog1;
		let option;
		let product2;
		let product3;
		let product3Name;
		let site;

		await test.step('Initialize Commerce Classic Site', async () => {
			option = await apiHelpers.headlessCommerceAdminCatalog.postOption(
				'select',
				'color',
				'Color'
			);

			const {catalog: catalogSetup, site: siteSetup} =
				await classicCommerceSetUp(apiHelpers, getRandomString());

			catalog1 = catalogSetup;
			site = siteSetup;
		});

		await test.step('Create an Account and a Buyer user', async () => {
			const account = await apiHelpers.headlessAdminUser.postAccount({
				name: getRandomString(),
				type: 'business',
			});

			await configureBuyerUserForSite(
				account,
				apiHelpers,
				site,
				'demo.unprivileged@liferay.com'
			);
		});

		await test.step('Create a bundled product with different catalog sku linked and generate all skus combinations', async () => {
			const catalog2 =
				await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
					name: getRandomString(),
				});

			const catalog3 =
				await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
					name: getRandomString(),
				});

			const product1 =
				await apiHelpers.headlessCommerceAdminCatalog.postProduct({
					catalogId: catalog3.id,
					name: {en_US: 'Product1'},
				});

			product2 =
				await apiHelpers.headlessCommerceAdminCatalog.postProduct({
					catalogId: catalog2.id,
					name: {en_US: 'Product2'},
					skus: [
						{
							cost: 0,
							price: 15,
							published: true,
							purchasable: true,
							sku: 'Sku' + getRandomInt(),
						},
					],
				});

			product3 = (
				await apiHelpers.headlessCommerceAdminCatalog.getProducts(
					new URLSearchParams({
						filter: `name eq 'ABS Sensor'`,
						nestedFields: `skus`,
					})
				)
			).items[0];

			product3Name = product3.name['en_US'];

			product3 =
				await apiHelpers.headlessCommerceAdminCatalog.patchProduct(
					product3.productId,
					{
						name: {en_US: product3Name},
						productConfiguration: {
							displayStockQuantity: true,
						},
						productOptions: [
							{
								fieldType: 'select',
								key: 'color',
								name: {
									en_US: 'Color',
								},
								optionId: option.id,
								priceType: 'static',
								productOptionValues: [
									{
										key: 'black',
										name: {
											en_US: 'Black',
										},
										position: 1,
										quantity: 1,
										skuId: product1.skus[0].id,
									},
									{
										key: 'white',
										name: {
											en_US: 'White',
										},
										position: 2,
										quantity: 1,
										skuId: product2.skus[0].id,
									},
								],
								required: true,
								skuContributor: true,
							},
						],
					}
				);

			await commerceAdminProductPage.gotoProduct(product3Name);
			await commerceAdminProductPage.generateSkus();
		});

		await test.step('Add Inventory and price for each sku', async () => {
			product3 = (
				await apiHelpers.headlessCommerceAdminCatalog.getProducts(
					new URLSearchParams({
						filter: `name eq '${product3Name}'`,
						nestedFields: `skus`,
					})
				)
			).items[0];

			const productSku1 = product3.skus.filter(
				(sku) => sku.sku === 'BLACK'
			);

			const productSku2 = product3.skus.filter(
				(sku) => sku.sku === 'WHITE'
			);

			const basePriceListId =
				await apiHelpers.headlessCommerceAdminPricing.getBasePriceListId(
					catalog1.id
				);

			await apiHelpers.headlessCommerceAdminPricing.postPriceEntry({
				price: 20,
				priceListId: basePriceListId.items[0].id,
				skuId: productSku1[0].id,
			});

			await apiHelpers.headlessCommerceAdminPricing.postPriceEntry({
				price: 30,
				priceListId: basePriceListId.items[0].id,
				skuId: productSku2[0].id,
			});

			for (const skuValue of ['BLACK', 'WHITE']) {
				const index = ['BLACK', 'WHITE'].indexOf(skuValue);
				await commerceAdminProductDetailsSkusPage
					.skusTableRowLink(`${skuValue}`)
					.click();

				await commerceAdminProductDetailsSkusPage.goToSkuTab(
					'Inventory'
				);
				await commerceAdminProductDetailsSkusPage.addWarehouseQuantity(
					String(index * 10 + 10),
					'Italy'
				);

				await waitForAlert(
					commerceAdminProductDetailsSkusPage.sidePanelFrame
				);
			}
		});

		await test.step('As Buyer go to product detail page', async () => {
			await performLogout(page);
			await performLoginViaApi({page, screenName: 'demo.unprivileged'});

			await page.goto(`/web/${site.name}/p/${product3Name}`, {
				waitUntil: 'networkidle',
			});
		});

		await test.step('Assert that option selector shows correct prices', async () => {
			await productDetailsPage.optionSelector('Color').click();

			const optionSelectorOptionsValues = await productDetailsPage
				.optionSelector('Color')
				.locator('option')
				.allTextContents();

			expect(optionSelectorOptionsValues).toEqual([
				'Choose an Option',
				'Black',
				'White + $ 10.00',
			]);

			let itemStock =
				await productDetailsPage.inStockQuantity.textContent();

			expect(itemStock).toContain('10 in Stock');

			await productDetailsPage
				.optionSelector('Color')
				.selectOption({label: 'White + $ 10.00'});
			await expect(
				await productDetailsPage.priceField('$ 30.00')
			).toBeVisible();

			itemStock = await productDetailsPage.inStockQuantity.textContent();

			expect(itemStock).toContain('20 in Stock');
		});

		try {
			await test.step('Add to Cart and assert that the product that is linked to the option is shown correctly in the cart details', async () => {
				await productDetailsPage.addToCartButton.click();

				await page.waitForLoadState('networkidle');

				await commerceMiniCartPage.miniCartButton.click();

				await expect(
					commerceMiniCartPage.miniCartItem(product3Name)
				).toBeVisible();

				await commerceMiniCartPage.showOptionsButton.click();

				await expect(page.getByLabel('White')).toBeVisible();
				await expect(
					page.getByLabel(`1 × ${product2.name['en_US']}`)
				).toBeVisible();
			});

			await test.step('Submit and check-out the order', async () => {
				await commerceMiniCartPage.submitButton.click();

				await checkoutPage.performCheckout({
					shippingAddress: {
						city: 'testCity',
						countryLabel: 'United States',
						name: 'John Doe',
						regionLabel: 'Florida',
						street: 'testStreet',
						zip: '12345',
					},
				});
			});

			await test.step('Navigate to order details and assert that the bundled product is shown with the correct option values', async () => {
				await checkoutPage.goToOrderDetailsButton.click();

				await placedOrdersPage.expandProductButton.click();

				await expect(
					(
						await commerceThemeClassicOrdersPage.orderItemsTableRow(
							3,
							product2.name['en_US'],
							true
						)
					).row
				).toBeVisible();
			});
		}
		finally {
			await performLogout(page);
			await performLoginViaApi({page, screenName: 'test'});

			const orders =
				await apiHelpers.headlessCommerceAdminOrder.getOrdersPage();

			if (orders.items[0]) {
				apiHelpers.data.push({id: orders.items[0].id, type: 'order'});
			}
		}
	}
);
