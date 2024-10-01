/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import {createReadStream} from 'fs';
import path from 'node:path';

import {applicationsMenuPageTest} from '../../../fixtures/applicationsMenuPageTest';
import {commercePagesTest} from '../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../fixtures/loginTest';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import performLogin, {
	performLogout,
	userData,
} from '../../../utils/performLogin';
import {getTempDir} from '../../../utils/temp';
import {waitForAlert} from '../../../utils/waitForAlert';

export const test = mergeTests(
	applicationsMenuPageTest,
	commercePagesTest,
	dataApiHelpersTest,
	loginTest()
);

test('LPD-31658 Users cannot view and download owner limited product attachments', async ({
	apiHelpers,
	applicationsMenuPage,
	commerceLayoutsPage,
	page,
	productDetailsPage,
}) => {
	const site = await apiHelpers.headlessSite.createSite({
		name: getRandomString(),
	});

	apiHelpers.data.push({id: site.id, type: 'site'});

	const userAccount = await apiHelpers.headlessAdminUser.postUserAccount();

	userData[userAccount.alternateName] = {
		name: userAccount.givenName,
		password: 'test',
		surname: userAccount.familyName,
	};

	const role =
		await apiHelpers.headlessAdminUser.getRoleByName('Site Member');

	await apiHelpers.headlessAdminUser.assignUserToSite(
		role.id,
		site.id,
		userAccount.id
	);

	await apiHelpers.headlessCommerceAdminChannel.postChannel({
		name: getRandomString(),
		siteGroupId: site.id,
	});

	const catalog = await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
		name: getRandomString(),
	});

	const product = await apiHelpers.headlessCommerceAdminCatalog.postProduct({
		catalogId: catalog.id,
		name: {en_US: getRandomString()},
	});

	const document1 = await apiHelpers.headlessDelivery.postDocument(
		site.id,
		createReadStream(path.join(__dirname, '/dependencies/attachment.txt')),
		{
			description: getRandomString(),
			externalReferenceCode: getRandomString(),
			fileName: getRandomString(),
			title: getRandomString(),
			viewableBy: 'Owner',
		}
	);

	apiHelpers.data.push({id: document1.id, type: 'document'});

	const attachment1 =
		await apiHelpers.headlessCommerceAdminCatalog.postAttachment(
			product.productId,
			document1.id,
			document1.title
		);

	apiHelpers.data.push({id: attachment1.id, type: 'attachment'});

	await applicationsMenuPage.goToSite(site.name);

	await commerceLayoutsPage.goToPages(false);

	await commerceLayoutsPage.createWidgetPage('View product details');

	await page.goto(`/web/${site.name}`);

	await productDetailsPage.addProductDetailsWidget();

	await page.goto(`/web/${site.name}/p/` + product.name['en_US']);

	await expect(
		await productDetailsPage.nameField(product.name['en_US'])
	).toBeVisible();

	await expect(await productDetailsPage.attachments).toBeVisible();

	await expect(
		await productDetailsPage.attachmentItem(attachment1.title['en_US'])
	).toBeVisible();

	await expect(await productDetailsPage.attachmentItems).toHaveCount(1);

	try {
		await performLogout(page);
		await performLogin(page, userAccount.alternateName);

		const document2 = await apiHelpers.headlessDelivery.postDocument(
			site.id,
			createReadStream(
				path.join(__dirname, '/dependencies/attachment.txt')
			),
			{
				description: getRandomString(),
				externalReferenceCode: getRandomString(),
				fileName: getRandomString(),
				title: getRandomString(),
				viewableBy: 'Owner',
			}
		);

		apiHelpers.data.push({id: document2.id, type: 'document'});

		await performLogout(page);
		await performLogin(page, 'test');

		const attachment2 =
			await apiHelpers.headlessCommerceAdminCatalog.postAttachment(
				product.productId,
				document2.id,
				document2.title
			);

		apiHelpers.data.push({id: attachment2.id, type: 'attachment'});

		await page.goto(`/web/${site.name}/p/` + product.name['en_US']);

		await expect(await productDetailsPage.attachments).toBeVisible();

		await expect(
			await productDetailsPage.attachmentItem(attachment2.title['en_US'])
		).toBeVisible();

		await expect(await productDetailsPage.attachmentItems).toHaveCount(2);

		await performLogout(page);
		await performLogin(page, userAccount.alternateName);

		await page.goto(`/web/${site.name}/p/` + product.name['en_US']);

		await expect(
			await productDetailsPage.nameField(product.name['en_US'])
		).toBeVisible();

		await expect(await productDetailsPage.attachments).toBeVisible();

		await expect(
			await productDetailsPage.attachmentItem(attachment2.title['en_US'])
		).toBeVisible();

		await expect(await productDetailsPage.attachmentItems).toHaveCount(1);
	}
	finally {
		await performLogout(page);
		await performLogin(page, 'test');
	}
});

test('COMMERCE-9677 As a buyer, I want to be able to view a virtual product Detail page', async ({
	apiHelpers,
	applicationsMenuPage,
	commerceLayoutsPage,
	page,
	productDetailsPage,
}) => {
	const site = await apiHelpers.headlessSite.createSite({
		name: 'View product details',
	});

	apiHelpers.data.push({id: site.id, type: 'site'});

	await apiHelpers.headlessCommerceAdminChannel.postChannel({
		name: 'View product details',
		siteGroupId: site.id,
	});

	const catalog = await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
		name: 'View product details',
	});

	const virtualProduct =
		await apiHelpers.headlessCommerceAdminCatalog.postProduct({
			catalogId: catalog.id,
			description: {en_US: 'Full description'},
			name: {en_US: 'Virtual'},
			productType: 'virtual',
			productVirtualSettings: {
				activationStatus: 1,
				duration: 4,
				maxUsages: 4,
				sampleURL: 'http://www.google.com',
				useSample: true,
			},
			shortDescription: {en_US: 'Short description'},
			skus: [
				{
					cost: 0,
					gtin: 'GTIN1',
					manufacturerPartNumber: 'mpn',
					price: 0,
					published: true,
					purchasable: true,
					sku: 'SkuVirtual',
				},
			],
		});

	const basePriceListId =
		await apiHelpers.headlessCommerceAdminPricing.getBasePriceListId(
			catalog.id
		);

	await apiHelpers.headlessCommerceAdminPricing.postPriceEntry({
		price: 100,
		priceListId: basePriceListId.items[0].id,
		skuId: virtualProduct.skus[0].id,
	});

	const basePromoPriceListId =
		await apiHelpers.headlessCommerceAdminPricing.getBasePromoPriceListId(
			catalog.id
		);

	await apiHelpers.headlessCommerceAdminPricing.postPriceEntry({
		price: 50,
		priceListId: basePromoPriceListId.items[0].id,
		skuId: virtualProduct.skus[0].id,
	});

	await apiHelpers.headlessCommerceAdminPricing.postDiscount();

	await applicationsMenuPage.goToSite('View product details');

	await commerceLayoutsPage.goToPages(false);

	await commerceLayoutsPage.createWidgetPage('View product details');

	await page.goto(`/web/${site.name}`);

	await productDetailsPage.addProductDetailsWidget();

	await page.goto(`/web/${site.name}/p/virtual`);

	await expect(await productDetailsPage.skuField('SkuVirtual')).toBeVisible();
	await expect(await productDetailsPage.mpnField('mpn')).toBeVisible();
	await expect(await productDetailsPage.gtinField('GTIN1')).toBeVisible();
	await expect(
		await productDetailsPage.shortDescriptionField('Short description')
	).toBeVisible();
	await expect(await productDetailsPage.priceField('$ 100.00')).toBeVisible();
	await expect(await productDetailsPage.priceField('$ 100.00')).toHaveClass(
		/price-value-inactive/
	);
	await expect(
		await productDetailsPage.promoPriceField('$ 50.00')
	).toBeVisible();
	await expect(
		await productDetailsPage.promoPriceField('$ 50.00')
	).toHaveClass(/price-value-promo/);
	await expect(
		await productDetailsPage.fullDescriptionField('Full description')
	).toBeVisible();
	await expect(
		await productDetailsPage.downloadSampleField('Download Sample File')
	).toBeVisible();
});

test('COMMERCE-12167 User can see SKU updated on the product details page when values are selected from multiple options', async ({
	apiHelpers,
	applicationsMenuPage,
	commerceAdminProductPage,
	commerceLayoutsPage,
	page,
	productDetailsPage,
}) => {
	const site = await apiHelpers.headlessSite.createSite({
		name: 'View product details',
	});

	apiHelpers.data.push({id: site.id, type: 'site'});

	await apiHelpers.headlessCommerceAdminChannel.postChannel({
		name: 'View product details',
		siteGroupId: site.id,
	});

	const catalog = await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
		name: 'View product details',
	});

	const option1 = await apiHelpers.headlessCommerceAdminCatalog.postOption(
		'select',
		'color',
		'Color',
		1
	);

	const option2 = await apiHelpers.headlessCommerceAdminCatalog.postOption(
		'select',
		'size',
		'Size',
		2
	);

	const product1 = await apiHelpers.headlessCommerceAdminCatalog.postProduct({
		catalogId: catalog.id,
		name: {en_US: 'Product1'},
	});

	const product2 = await apiHelpers.headlessCommerceAdminCatalog.postProduct({
		catalogId: catalog.id,
		name: {en_US: 'Product2'},
	});

	const productBundle =
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
						},
					],
					skuContributor: true,
				},
				{
					fieldType: 'select',
					key: 'size',
					name: {
						en_US: 'Size',
					},
					optionId: option2.id,
					priceType: 'static',
					priority: 2,
					productOptionValues: [
						{
							deltaPrice: 30.0,
							key: 'xs',
							name: {
								en_US: 'XS',
							},
							priority: 1,
							quantity: 1,
						},
						{
							deltaPrice: 40.0,
							key: 'xl',
							name: {
								en_US: 'XL',
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

	await applicationsMenuPage.goToProducts();

	await commerceAdminProductPage.managementToolbarSearchInput.fill(
		'ProductBundle'
	);
	await commerceAdminProductPage.managementToolbarSearchInput.press('Enter');

	await commerceAdminProductPage
		.productsTableRowLink('ProductBundle')
		.click();

	await commerceAdminProductPage.generateSkus();

	const productBundleSkus = await apiHelpers.headlessCommerceAdminCatalog
		.getProduct(productBundle.productId)
		.then((product) => {
			return product.skus;
		});

	const sku1 = productBundleSkus.find(
		(sku) => sku.sku === 'WHITEXL' || sku.sku === 'XLWHITE'
	);

	await apiHelpers.headlessCommerceAdminCatalog.postSkuUnitOfMeasure(
		sku1.id,
		{
			incrementalOrderQuantity: 2,
			name: {en_US: 'Pallet'},
			priority: 2,
			rate: 3,
		}
	);

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

	const sku2 = productBundleSkus.find(
		(sku) => sku.sku === 'BLACKXL' || sku.sku === 'XLBLACK'
	);

	await apiHelpers.headlessCommerceAdminCatalog.postSkuUnitOfMeasure(
		sku2.id,
		{
			incrementalOrderQuantity: 3,
			name: {en_US: 'Box'},
			primary: true,
			priority: 1,
			rate: 1,
		}
	);

	await apiHelpers.headlessCommerceAdminCatalog.postSkuUnitOfMeasure(
		sku2.id,
		{
			incrementalOrderQuantity: 2,
			name: {en_US: 'Package'},
			priority: 2,
			rate: 0.5,
		}
	);

	await applicationsMenuPage.goToSite('View product details');

	await commerceLayoutsPage.goToPages(false);

	await commerceLayoutsPage.createWidgetPage('View product details');

	await page.goto(`/web/${site.name}`);

	await productDetailsPage.addProductDetailsWidget();

	await page.goto(`/web/${site.name}/p/productbundle`);

	await expect(await productDetailsPage.optionSelector('Size')).toBeVisible();

	await expect(
		await productDetailsPage.optionSelector('Color')
	).toBeVisible();

	await (
		await productDetailsPage.optionSelector('Color')
	).selectOption({label: 'Black'});

	await (
		await productDetailsPage.optionSelector('Size')
	).selectOption({label: 'XL + $ 10.00'});

	await expect(await productDetailsPage.uomTable('Unit')).toBeVisible();

	await expect(await productDetailsPage.priceField('$ 50.00')).toBeVisible();
});

test('LPD-18710 Price is correctly calculated for bundle product with options not marked as sku contributor', async ({
	apiHelpers,
	applicationsMenuPage,
	commerceLayoutsPage,
	page,
	productDetailsPage,
}) => {
	const siteName = getRandomString();

	const site = await apiHelpers.headlessSite.createSite({
		name: siteName,
	});

	apiHelpers.data.push({id: site.id, type: 'site'});

	await apiHelpers.headlessCommerceAdminChannel.postChannel({
		siteGroupId: site.id,
	});

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

	const product2 = await apiHelpers.headlessCommerceAdminCatalog.postProduct({
		catalogId: catalog.id,
		name: {en_US: getRandomString()},
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
				priceType: 'dynamic',
				priority: 1,
				productOptionValues: [
					{
						key: 'black',
						name: {
							en_US: 'Black',
						},
						priority: 1,
						quantity: 1,
						skuId: product1.skus[0].id,
					},
					{
						key: 'white',
						name: {
							en_US: 'White',
						},
						priority: 2,
						quantity: 1,
						skuId: product2.skus[0].id,
					},
				],
			},
		],
	});

	await applicationsMenuPage.goToSite(siteName);

	await commerceLayoutsPage.goToPages(false);
	await commerceLayoutsPage.createWidgetPage('View product details');

	await page.goto(`/web/${site.name}`);

	await productDetailsPage.addProductDetailsWidget();

	await page.goto(`/web/${site.name}/p/${productBundleName}`);

	await expect(
		await productDetailsPage.optionSelector('Color')
	).toBeVisible();
	await expect(await productDetailsPage.priceField('$ 0.00')).toBeVisible();

	await productDetailsPage.selectOption('Black', 'Color');

	await expect(await productDetailsPage.priceField('$ 10.00')).toBeVisible();

	await productDetailsPage.selectOption('White', 'Color');

	await expect(await productDetailsPage.priceField('$ 20.00')).toBeVisible();

	await productDetailsPage.selectOption('Choose an Option', 'Color');

	await expect(await productDetailsPage.priceField('$ 0.00')).toBeVisible();
});

test(`LPD-29993 Users can view and download a product's attachments`, async ({
	apiHelpers,
	applicationsMenuPage,
	commerceAdminChannelsPage,
	commerceLayoutsPage,
	page,
	productDetailsPage,
}) => {
	const site = await apiHelpers.headlessSite.createSite({
		name: getRandomString(),
	});

	apiHelpers.data.push({id: site.id, type: 'site'});

	const account = await apiHelpers.headlessAdminUser.postAccount({
		name: 'admin',
		type: 'business',
	});
	apiHelpers.data.push({id: account.id, type: 'account'});

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

	const channel = await apiHelpers.headlessCommerceAdminChannel.postChannel({
		name: getRandomString(),
		siteGroupId: site.id,
	});
	const catalog = await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
		name: getRandomString(),
	});
	const product = await apiHelpers.headlessCommerceAdminCatalog.postProduct({
		catalogId: catalog.id,
		name: {en_US: getRandomString()},
	});

	const document1 = await apiHelpers.headlessDelivery.postDocument(
		site.id,
		createReadStream(path.join(__dirname, '/dependencies/attachment.txt')),
		{
			fileName: 'attachment.txt',
			title: 'attachmentFile',
		}
	);

	apiHelpers.data.push({id: document1.id, type: 'document'});

	const attachment1 =
		await apiHelpers.headlessCommerceAdminCatalog.postAttachment(
			product.productId,
			document1.id,
			document1.title
		);

	apiHelpers.data.push({id: attachment1.id, type: 'attachment'});

	await commerceAdminChannelsPage.changeCommerceChannelSiteType(
		channel.name,
		'B2B'
	);

	await waitForAlert(page);

	await applicationsMenuPage.goToSite(site.name);
	await commerceLayoutsPage.goToPages(false);
	await commerceLayoutsPage.createWidgetPage('View product details');

	await page.goto(`/web/${site.name}`);

	await productDetailsPage.addProductDetailsWidget();

	await performLogout(page);

	await performLogin(page, 'demo.unprivileged');

	await page.goto(`/web/${site.name}/p/` + product.name['en_US']);

	await expect(
		await productDetailsPage.nameField(product.name['en_US'])
	).toBeVisible();
	await expect(productDetailsPage.attachments).toBeVisible();
	await expect(
		await productDetailsPage.attachmentItem(attachment1.title['en_US'])
	).toBeVisible();
	await expect(productDetailsPage.attachmentItems).toHaveCount(1);

	const downloadPromise = page.waitForEvent('download');

	await productDetailsPage.downloadAttachmentLink.click();

	const download = await downloadPromise;

	const filePath = getTempDir() + download.suggestedFilename();

	await download.saveAs(filePath);

	expect(filePath).toBeTruthy();
});
