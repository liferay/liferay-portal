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
import getRandomString from '../../../../utils/getRandomString';
import {userData} from '../../../../utils/performLogin';

export const test = mergeTests(
	apiHelpersTest,
	commercePagesTest,
	dataApiHelpersTest,
	isolatedSiteTest,
	loginTest()
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
