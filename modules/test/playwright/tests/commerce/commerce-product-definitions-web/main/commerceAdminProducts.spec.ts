/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../../fixtures/apiHelpersTest';
import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {isolatedSiteTest} from '../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {serverAdministrationPageTest} from '../../../../fixtures/serverAdministrationPageTest';
import {userPersonalBarPagesTest} from '../../../../fixtures/userPersonalBarPagesTest';
import {EActions} from '../../../../helpers/ServerAdministrationHelper';
import {getRandomInt} from '../../../../utils/getRandomInt';
import getRandomString from '../../../../utils/getRandomString';
import {userData} from '../../../../utils/performLogin';
import {waitForAlert} from '../../../../utils/waitForAlert';

export const test = mergeTests(
	apiHelpersTest,
	commercePagesTest,
	dataApiHelpersTest,
	isolatedSiteTest,
	loginTest(),
	serverAdministrationPageTest,
	userPersonalBarPagesTest
);

test(
	'Add a SKU',
	{tag: '@COMMERCE-6021'},
	async ({
		apiHelpers,
		commerceAdminProductDetailsPage,
		commerceAdminProductDetailsSkusPage,
		commerceAdminProductPage,
	}) => {
		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

		const product =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
			});

		await commerceAdminProductPage.gotoProduct(product.name['en_US']);

		await commerceAdminProductDetailsPage.goToProductSkus();

		await commerceAdminProductDetailsSkusPage.skuAddButton.click();

		await commerceAdminProductDetailsSkusPage.skuAddModalSkuInput.fill(
			'BLACKSKU'
		);

		await commerceAdminProductDetailsSkusPage.skuAddModalSkuPurchasableToggle.check();

		await commerceAdminProductDetailsSkusPage.skuAddModalSkuPublishButton.click();

		await expect(
			commerceAdminProductDetailsSkusPage.skuAddModalSuccessMessage
		).toBeVisible();
	}
);

test(
	'Back button works as expected',
	{tag: '@LPD-43791'},
	async ({
		apiHelpers,
		commerceAdminProductDetailsPage,
		commerceAdminProductPage,
		page,
		site,
	}) => {
		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

		const product =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
			});

		await commerceAdminProductPage.gotoProduct(product.name['en_US']);

		await commerceAdminProductDetailsPage.backLink.click();

		await expect(
			commerceAdminProductPage.productsTableRowLink(product.name['en_US'])
		).toBeVisible();

		await commerceAdminProductPage.gotoProduct(product.name['en_US']);

		await page.goto(
			`${page.url()}&_com_liferay_commerce_product_definitions_web_internal_portlet_CPDefinitionsPortlet_backURL=${site.friendlyUrlPath}`
		);

		await commerceAdminProductDetailsPage.backLink.click();

		await expect(
			commerceAdminProductPage.productsTableRowLink(product.name['en_US'])
		).toHaveCount(0);
	}
);

test(
	'Add a SKU with subscriptions',
	{tag: '@COMMERCE-6024'},
	async ({
		apiHelpers,
		commerceAdminProductDetailsPage,
		commerceAdminProductDetailsSkusPage,
		commerceAdminProductPage,
	}) => {
		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
				name: 'Master',
			});

		const product =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				name: {en_US: 'Simple T-Shirt'},
				productType: 'simple',
			});

		await commerceAdminProductPage.gotoProduct(product.name['en_US']);

		await commerceAdminProductDetailsPage.goToProductSkus();

		await commerceAdminProductDetailsSkusPage.skuAddButton.click();

		await commerceAdminProductDetailsSkusPage.skuAddModalSkuInput.fill(
			'BLACKSKU'
		);

		await commerceAdminProductDetailsSkusPage.skuAddModalSkuPublishButton.click();

		await expect(
			commerceAdminProductDetailsSkusPage.skuAddModalSuccessMessage
		).toBeVisible();

		await (
			await commerceAdminProductDetailsSkusPage.closeSidePanelFrame(false)
		).click();

		await expect(
			await commerceAdminProductDetailsSkusPage.closeSidePanelFrame(false)
		).toBeHidden();

		await commerceAdminProductDetailsSkusPage
			.skusTableRowLink('BLACKSKU')
			.click();

		await commerceAdminProductDetailsSkusPage.goToSkuTab('Subscriptions');

		const overrideToggle =
			commerceAdminProductDetailsSkusPage.sidePanelFrame.getByLabel(
				'Override Subscription Settings'
			);

		await overrideToggle.check();

		await expect(
			commerceAdminProductDetailsSkusPage.sidePanelFrame.getByText(
				'Payment Subscription'
			)
		).toBeVisible();

		await expect(
			commerceAdminProductDetailsSkusPage.sidePanelFrame.getByText(
				'Delivery Subscription'
			)
		).toBeVisible();

		await commerceAdminProductDetailsSkusPage.sidePanelSaveButton.click();

		await expect(
			commerceAdminProductDetailsSkusPage.sidePanelFrame.getByText(
				'Success:Your request completed successfully.'
			)
		).toBeVisible();

		await overrideToggle.uncheck();

		await commerceAdminProductDetailsSkusPage.sidePanelSaveButton.click();

		await expect(
			commerceAdminProductDetailsSkusPage.sidePanelFrame.getByText(
				'Success:Your request completed successfully.'
			)
		).toBeVisible();

		await expect(
			commerceAdminProductDetailsSkusPage.sidePanelFrame.getByText(
				'Payment Subscription'
			)
		).not.toBeVisible();

		await expect(
			commerceAdminProductDetailsSkusPage.sidePanelFrame.getByText(
				'Delivery Subscription'
			)
		).not.toBeVisible();
	}
);

test(
	'Currency changes based on price lists',
	{tag: '@LPD-52938'},
	async ({
		apiHelpers,
		commerceAdminProductDetailsPage,
		commerceAdminProductDetailsSkusPage,
		commerceAdminProductPage,
	}) => {
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

		const currencies =
			await apiHelpers.headlessCommerceAdminCatalog.getCurrenciesPage('');

		const currencyEUR = currencies.items.find(
			(item) => item.name['en_US'] === 'Euro'
		);
		const currencyUSD = currencies.items.find(
			(item) => item.name['en_US'] === 'US Dollar'
		);

		const priceListEUR =
			await apiHelpers.headlessCommerceAdminPricing.postPriceList({
				catalogId: catalog.id,
				currencyCode: currencyEUR.code,
				name: 'EUR-pl',
				type: 'price-list',
			});
		const priceListUSD =
			await apiHelpers.headlessCommerceAdminPricing.postPriceList({
				catalogId: catalog.id,
				currencyCode: currencyUSD.code,
				name: 'USD-pl',
				type: 'price-list',
			});

		await commerceAdminProductPage.gotoProduct(product.name['en_US']);

		await commerceAdminProductDetailsPage.goToProductSkus();

		await commerceAdminProductDetailsSkusPage
			.skusTableRowLink(`${productSkus[0].sku}`)
			.click();
		await commerceAdminProductDetailsSkusPage.goToSkuTab('Price');
		await commerceAdminProductDetailsSkusPage.skuPriceAddButton.click();
		await commerceAdminProductDetailsSkusPage.skuPriceListSelect.selectOption(
			priceListEUR.name
		);

		await expect(
			commerceAdminProductDetailsSkusPage.skuPriceAddModal.getByText(
				'EUR',
				{exact: true}
			)
		).toBeVisible();

		await commerceAdminProductDetailsSkusPage.skuPriceListSelect.selectOption(
			priceListUSD.name
		);

		await expect(
			commerceAdminProductDetailsSkusPage.skuPriceAddModal.getByText(
				'USD',
				{exact: true}
			)
		).toBeVisible();
	}
);

test(
	'Add price entries from several price lists to a SKU',
	{tag: ['@COMMERCE-11084', '@LPD-110416']},
	async ({
		apiHelpers,
		commerceAdminProductDetailsPage,
		commerceAdminProductDetailsSkusPage,
		commerceAdminProductPage,
	}) => {
		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

		const product =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
			});

		const productDetails =
			await apiHelpers.headlessCommerceAdminCatalog.getProduct(
				product.productId
			);

		const priceList1 =
			await apiHelpers.headlessCommerceAdminPricing.postPriceList({
				catalogId: catalog.id,
				currencyCode: 'USD',
				name: getRandomString(),
				type: 'price-list',
			});
		const priceList2 =
			await apiHelpers.headlessCommerceAdminPricing.postPriceList({
				catalogId: catalog.id,
				currencyCode: 'USD',
				name: getRandomString(),
				type: 'price-list',
			});

		await test.step('Open the add price dialog', async () => {
			await commerceAdminProductPage.gotoProduct(product.name['en_US']);

			await commerceAdminProductDetailsPage.goToProductSkus();

			await commerceAdminProductDetailsSkusPage
				.skusTableRowLink(productDetails.skus[0].sku)
				.click();
			await commerceAdminProductDetailsSkusPage.goToSkuTab('Price');
			await commerceAdminProductDetailsSkusPage.skuPriceAddButton.click();
		});

		await test.step('Add a price entry for each price list', async () => {
			await commerceAdminProductDetailsSkusPage.skuPriceListSelect
				.nth(0)
				.selectOption(priceList1.name);
			await commerceAdminProductDetailsSkusPage.skuPriceAddModalUnitPriceInput
				.nth(0)
				.fill('10');

			await commerceAdminProductDetailsSkusPage.skuPriceAddModal
				.getByRole('button', {exact: true, name: 'Add Entry'})
				.click();

			await commerceAdminProductDetailsSkusPage.skuPriceListSelect
				.nth(1)
				.selectOption(priceList2.name);
			await commerceAdminProductDetailsSkusPage.skuPriceAddModalUnitPriceInput
				.nth(1)
				.fill('20');

			await commerceAdminProductDetailsSkusPage.skuPriceAddModalAddButton.click();
		});

		await test.step('Check the dialog closes and the list refreshes', async () => {
			await expect(
				commerceAdminProductDetailsSkusPage.skuPriceAddModalUnitPriceInput.first()
			).toBeHidden();

			await expect(
				commerceAdminProductDetailsSkusPage.sidePanelFrame
					.getByRole('row')
					.filter({hasText: priceList1.name})
			).toContainText('$ 10.00');
			await expect(
				commerceAdminProductDetailsSkusPage.sidePanelFrame
					.getByRole('row')
					.filter({hasText: priceList2.name})
			).toContainText('$ 20.00');
		});
	}
);

test(
	'Product name should be created for both user language and instance language',
	{tag: '@LPD-64086'},
	async ({apiHelpers, commerceAdminProductPage, page}) => {
		const user =
			await apiHelpers.headlessAdminUser.getUserAccountByEmailAddress(
				'test@liferay.com'
			);

		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

		userData[user.alternateName] = {
			name: user.givenName,
			password: 'test',
			surname: user.familyName,
		};

		const role =
			await apiHelpers.headlessAdminUser.getRoleByName('Administrator');

		await apiHelpers.headlessAdminUser.assignUserToRole(
			role.externalReferenceCode,
			user.id
		);

		await commerceAdminProductPage.goto();

		await apiHelpers.headlessAdminUser.patchUserAccount(user, {
			languageId: 'es_ES',
		});

		await page.reload();

		const translateLink = page.getByRole('link', {
			name: 'Mostrar la página en español (España).',
		});

		const inEnglish = await translateLink.isVisible();

		if (inEnglish) {
			await translateLink.click();
		}

		await commerceAdminProductPage.addButton.click();
		await commerceAdminProductPage.menuItemProductType('Simple').click();

		await expect(
			commerceAdminProductPage.modalFrameLocator.getByText(
				'Crear nuevo producto'
			)
		).toBeVisible();

		const productName = getRandomString();

		await commerceAdminProductPage.modalFrameLocator
			.getByLabel('Nombre Requerido')
			.fill(productName);
		await commerceAdminProductPage.modalFrameLocator
			.getByPlaceholder('Escriba aquí')
			.fill(catalog.name);
		await commerceAdminProductPage.modalMenuItem(catalog.name).click();
		await commerceAdminProductPage.modalFrameLocator
			.getByRole('button', {
				exact: true,
				name: 'Enviar',
			})
			.click();

		await expect(page.getByText(productName)).toBeVisible();

		const product = (
			await apiHelpers.headlessCommerceAdminCatalog.getProducts(
				new URLSearchParams({
					filter: `name eq '${productName}'`,
				})
			)
		).items[0];

		const productNameUS = product.name['en_US'];
		const productNameES = product.name['es_ES'];

		expect(productNameUS).toBe(productName);
		expect(productNameES).toBe(productName);

		await apiHelpers.headlessAdminUser.patchUserAccount(user, {
			languageId: 'en_US',
		});
	}
);

test(
	'Creating product with pending status in headless triggers workflow notification',
	{tag: ['@LPD-73496']},
	async ({
		apiHelpers,
		commerceAdminProductPage,
		page,
		userPersonalBarPage,
	}) => {
		await userPersonalBarPage.goToProcessBuilderConfigurationTab();
		await userPersonalBarPage.enableSingleApproverWorkflowProduct();

		try {
			const catalog =
				await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

			const product =
				await apiHelpers.headlessCommerceAdminCatalog.postProduct({
					catalogId: catalog.id,
					productStatus: 1,
				});

			await commerceAdminProductPage.gotoProduct(product.name['en_US']);

			await expect(page.getByText('Assigned to:')).toBeVisible();
			await expect(userPersonalBarPage.notificationBadge).toBeVisible();
		}
		finally {
			await userPersonalBarPage.disableSingleApproverWorkflowProduct();
		}
	}
);

test(
	'Add SKU with duplicate values and confirm warning message shows',
	{tag: '@LPD-90070'},
	async ({
		apiHelpers,
		commerceAdminProductDetailsPage,
		commerceAdminProductDetailsSkusPage,
		commerceAdminProductPage,
		page,
	}) => {
		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

		const product1 =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
			});

		const product2 =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
			});

		await commerceAdminProductPage.gotoProduct(product2.name['en_US']);

		await commerceAdminProductDetailsPage.goToProductSkus();

		await commerceAdminProductDetailsSkusPage.skuAddButton.click();

		await commerceAdminProductDetailsSkusPage.skuAddModalSkuInput.fill(
			product1.skus[0].sku.toUpperCase()
		);

		await commerceAdminProductDetailsSkusPage.skuAddModalSkuPurchasableToggle.check();

		page.once('dialog', async (dialog) => {
			expect(dialog.message()).toContain('The SKU is already in use');

			await dialog.accept();
		});

		await commerceAdminProductDetailsSkusPage.skuAddModalSkuPublishButton.click();

		await expect(
			commerceAdminProductDetailsSkusPage.skuAddModalSuccessMessage
		).toBeVisible();
	}
);

test(
	'Product fields default to the catalog default language',
	{tag: ['@COMMERCE-9532', '@LPD-103842']},
	async ({
		apiHelpers,
		commerceAdminProductDetailsPage,
		commerceAdminProductPage,
	}) => {
		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
				defaultLanguageId: 'es_ES',
				name: getRandomString(),
			});

		const productName = getRandomString();

		await apiHelpers.headlessCommerceAdminCatalog.postProduct({
			catalogId: catalog.id,
			name: {en_US: productName},
		});

		await commerceAdminProductPage.gotoProduct(productName);

		await expect(
			commerceAdminProductDetailsPage.nameInputLocaleSelector
		).toHaveText('es-ES');
	}
);

test(
	'Cannot publish a product with empty or zero order quantities',
	{tag: ['@COMMERCE-10173', '@LPD-107429']},
	async ({
		apiHelpers,
		commerceAdminProductDetailsConfigurationPage,
		commerceAdminProductDetailsPage,
		commerceAdminProductPage,
		page,
	}) => {
		const defaultOrderQuantities = {
			'Maximum Order Quantity': '10000.0',
			'Minimum Order Quantity': '1.0',
			'Multiple Order Quantity': '1.0',
		};

		await test.step('Open the configuration of a new product', async () => {
			const catalog =
				await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

			const product =
				await apiHelpers.headlessCommerceAdminCatalog.postProduct({
					catalogId: catalog.id,
				});

			await commerceAdminProductPage.gotoProduct(product.name['en_US']);

			await commerceAdminProductDetailsPage.goToProductConfiguration();

			for (const [
				orderQuantityLabel,
				orderQuantityValue,
			] of Object.entries(defaultOrderQuantities)) {
				await expect(
					commerceAdminProductDetailsConfigurationPage.orderQuantityInput(
						orderQuantityLabel
					)
				).toHaveValue(orderQuantityValue);
			}
		});

		await test.step('Publishing empty order quantities shows the required errors', async () => {
			for (const orderQuantityLabel of Object.keys(
				defaultOrderQuantities
			)) {
				await commerceAdminProductDetailsConfigurationPage
					.orderQuantityInput(orderQuantityLabel)
					.clear();
			}

			await commerceAdminProductDetailsConfigurationPage.publishLink.click();

			await expect(
				commerceAdminProductDetailsConfigurationPage.orderQuantityInput(
					'Minimum Order Quantity'
				)
			).toBeFocused();

			for (const orderQuantityLabel of Object.keys(
				defaultOrderQuantities
			)) {
				await expect(
					commerceAdminProductDetailsConfigurationPage.orderQuantityErrorMessage(
						orderQuantityLabel
					)
				).toHaveText(`The ${orderQuantityLabel} field is required.`);
			}
		});

		await test.step('Publishing zero order quantities shows the minimum value errors', async () => {
			for (const orderQuantityLabel of Object.keys(
				defaultOrderQuantities
			)) {
				await commerceAdminProductDetailsConfigurationPage
					.orderQuantityInput(orderQuantityLabel)
					.fill('0');
			}

			await commerceAdminProductDetailsConfigurationPage.publishLink.click();

			await expect(
				commerceAdminProductDetailsConfigurationPage.orderQuantityInput(
					'Minimum Order Quantity'
				)
			).toBeFocused();

			for (const orderQuantityLabel of Object.keys(
				defaultOrderQuantities
			)) {
				await expect(
					commerceAdminProductDetailsConfigurationPage.orderQuantityErrorMessage(
						orderQuantityLabel
					)
				).toHaveText(
					'Please enter a value greater than or equal to 0.000001.'
				);
			}
		});

		await test.step('The order quantities are not saved', async () => {
			await page.reload();

			for (const [
				orderQuantityLabel,
				orderQuantityValue,
			] of Object.entries(defaultOrderQuantities)) {
				await expect(
					commerceAdminProductDetailsConfigurationPage.orderQuantityInput(
						orderQuantityLabel
					)
				).toHaveValue(orderQuantityValue);
			}
		});
	}
);

test(
	'Update the inventory of a SKU with a zero reserved quantity',
	{tag: '@LPD-113063'},
	async ({
		apiHelpers,
		commerceAdminProductDetailsPage,
		commerceAdminProductDetailsSkusPage,
		commerceAdminProductPage,
		serverAdministrationPage,
	}) => {
		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

		const sku1 = getRandomString();
		const sku2 = getRandomString();

		const product =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				skus: [
					{
						cost: 0,
						price: 0,
						published: true,
						purchasable: true,
						sku: sku1,
					},
					{
						cost: 0,
						price: 0,
						published: true,
						purchasable: true,
						sku: sku2,
					},
				],
			});

		const skuUnitOfMeasure =
			await apiHelpers.headlessCommerceAdminCatalog.postSkuUnitOfMeasure(
				product.skus.find((sku) => sku.sku === sku2).id,
				{
					precision: 7,
					primary: true,
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
							quantity: 5,
							sku: sku1,
						},
						{
							quantity: 5,
							sku: sku2,
							unitOfMeasureKey: skuUnitOfMeasure.key,
						},
					],
				}
			);

		await test.step('Update the inventory of a SKU after its reserved quantity is reloaded from the database', async () => {
			await serverAdministrationPage.goto();

			await serverAdministrationPage.executeAction(
				EActions.CLEAR_DATABASE_CACHE
			);

			await commerceAdminProductPage.gotoProduct(product.name['en_US']);

			await commerceAdminProductDetailsPage.goToProductSkus();

			await commerceAdminProductDetailsSkusPage
				.skusTableRowLink(sku1)
				.click();

			await commerceAdminProductDetailsSkusPage.goToSkuTab('Inventory');

			await commerceAdminProductDetailsSkusPage.addWarehouseQuantity(
				'10',
				warehouse.name['en_US']
			);

			await waitForAlert(
				commerceAdminProductDetailsSkusPage.sidePanelFrame
			);

			await expect(
				await commerceAdminProductDetailsSkusPage.inventoryTableRowAction(
					warehouse.name['en_US']
				)
			).toHaveValue('10');
		});

		await test.step('Update the inventory of a SKU whose unit of measure has a precision greater than six', async () => {
			await commerceAdminProductPage.gotoProduct(product.name['en_US']);

			await commerceAdminProductDetailsPage.goToProductSkus();

			await commerceAdminProductDetailsSkusPage
				.skusTableRowLink(sku2)
				.click();

			await commerceAdminProductDetailsSkusPage.goToSkuTab('Inventory');

			await commerceAdminProductDetailsSkusPage.addWarehouseQuantity(
				'10',
				warehouse.name['en_US']
			);

			await waitForAlert(
				commerceAdminProductDetailsSkusPage.sidePanelFrame
			);

			await expect(
				await commerceAdminProductDetailsSkusPage.inventoryTableRowAction(
					warehouse.name['en_US']
				)
			).toHaveValue('10.0000000');
		});
	}
);
