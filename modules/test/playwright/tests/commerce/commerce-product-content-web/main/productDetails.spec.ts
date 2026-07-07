/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import {createReadStream} from 'fs';
import path from 'node:path';

import {accountsPagesTest} from '../../../../fixtures/accountsPagesTest';
import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {displayPageTemplatesPagesTest} from '../../../../fixtures/displayPageTemplatesPagesTest';
import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {globalMenuPagesTest} from '../../../../fixtures/globalMenuPagesTest';
import {isolatedSiteTest} from '../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../../fixtures/pageEditorPagesTest';
import {pageViewModePagesTest} from '../../../../fixtures/pageViewModePagesTest';
import {getRandomInt} from '../../../../utils/getRandomInt';
import getRandomString from '../../../../utils/getRandomString';
import performLogin, {
	performLoginViaApi,
	performLogout,
	performUserSwitchViaApi,
	userData,
} from '../../../../utils/performLogin';
import {getTempDir} from '../../../../utils/temp';
import {waitForAlert} from '../../../../utils/waitForAlert';
import getPageDefinition from '../../../layout-content-page-editor-web/main/utils/getPageDefinition';
import getWidgetDefinition from '../../../layout-content-page-editor-web/main/utils/getWidgetDefinition';
import {
	configureBuyerUserForSite,
	createChannelAccountManagerUser,
	miniumSetUp,
} from '../../utils/commerce';

export const test = mergeTests(
	accountsPagesTest,
	commercePagesTest,
	dataApiHelpersTest,
	displayPageTemplatesPagesTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	globalMenuPagesTest,
	isolatedSiteTest,
	loginTest(),
	pageEditorPagesTest,
	pageViewModePagesTest
);

test('LPD-31658 Users cannot view and download owner limited product attachments', async ({
	apiHelpers,
	page,
	productDetailsPage,
	site,
	widgetPagePage,
}) => {
	const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
		groupId: site.id,
		title: getRandomString(),
	});

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

	await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

	await widgetPagePage.addPortlet('Product Details');

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
	page,
	productDetailsPage,
	site,
	widgetPagePage,
}) => {
	const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
		groupId: site.id,
		title: getRandomString(),
	});

	await apiHelpers.headlessCommerceAdminChannel.postChannel({
		siteGroupId: site.id,
	});

	const catalog = await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
		name: 'View product details',
	});

	const virtualProduct =
		await apiHelpers.headlessCommerceAdminCatalog.postProduct({
			catalogId: catalog.id,
			description: {en_US: 'Full description'},
			name: {en_US: getRandomString()},
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

	await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

	await widgetPagePage.addPortlet('Product Details');

	await page.goto(`/web/${site.name}/p/${virtualProduct.name['en_US']}`);

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

test(
	'User can see SKU updated on the product details page when values are selected from multiple options',
	{tag: '@COMMERCE-12167'},
	async ({
		apiHelpers,
		commerceAdminProductPage,
		globalMenuPage,
		page,
		productDetailsPage,
		site,
		widgetPagePage,
	}) => {
		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: getRandomString(),
		});

		await apiHelpers.headlessCommerceAdminChannel.postChannel({
			siteGroupId: site.id,
		});

		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
				name: 'View product details',
			});
		const option1 =
			await apiHelpers.headlessCommerceAdminCatalog.postOption(
				'select',
				'color',
				'Color',
				1
			);
		const option2 =
			await apiHelpers.headlessCommerceAdminCatalog.postOption(
				'select',
				'size',
				'Size',
				2
			);
		const product1 =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				name: {en_US: getRandomString()},
			});
		const product2 =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				name: {en_US: getRandomString()},
			});
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

		await globalMenuPage.goToCommerce('Products');

		await commerceAdminProductPage.managementToolbarSearchInput.fill(
			'ProductBundle'
		);
		await commerceAdminProductPage.managementToolbarSearchInput.press(
			'Enter'
		);

		await commerceAdminProductPage
			.productsTableRowLink('ProductBundle')
			.click();
		await commerceAdminProductPage.generateSkus();

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

		await widgetPagePage.addPortlet('Product Details');

		await page.goto(`/web/${site.name}/p/productbundle`);

		await expect(productDetailsPage.optionSelector('Color')).toBeVisible();
		await expect(productDetailsPage.optionSelector('Size')).toBeVisible();
		await productDetailsPage
			.optionSelector('Color')
			.selectOption({label: 'Choose an Option'});

		await expect(page.getByText('This field is required.')).toBeVisible();

		await expect(productDetailsPage.addToCartButton).toBeDisabled();

		await productDetailsPage
			.optionSelector('Color')
			.selectOption({label: 'Black'});
		await productDetailsPage
			.optionSelector('Size')
			.selectOption({label: 'XL + $ 10.00'});

		await expect(
			await productDetailsPage.priceField('$ 50.00')
		).toBeVisible();
	}
);

test(
	'Unit of measure table is rendered for the resolved combination SKU on the product details page',
	{tag: '@COMMERCE-12167'},
	async ({
		apiHelpers,
		commerceAdminProductPage,
		globalMenuPage,
		page,
		productDetailsPage,
		site,
		widgetPagePage,
	}) => {
		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: getRandomString(),
		});

		await apiHelpers.headlessCommerceAdminChannel.postChannel({
			siteGroupId: site.id,
		});

		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
				name: 'UoM combo SKU',
			});

		const option1 =
			await apiHelpers.headlessCommerceAdminCatalog.postOption(
				'select',
				'color',
				'Color',
				1
			);

		const productBundle =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				name: {en_US: 'UoMBundle'},
				productOptions: [
					{
						fieldType: 'select',
						key: 'color',
						name: {en_US: 'Color'},
						optionId: option1.id,
						priceType: 'static',
						priority: 1,
						productOptionValues: [
							{
								key: 'black',
								name: {en_US: 'Black'},
								priority: 1,
								quantity: 1,
							},
							{
								key: 'white',
								name: {en_US: 'White'},
								priority: 2,
								quantity: 1,
							},
						],
						skuContributor: true,
					},
				],
			});

		await globalMenuPage.goToCommerce('Products');

		await commerceAdminProductPage.managementToolbarSearchInput.fill(
			'UoMBundle'
		);
		await commerceAdminProductPage.managementToolbarSearchInput.press(
			'Enter'
		);

		await commerceAdminProductPage
			.productsTableRowLink('UoMBundle')
			.click();
		await commerceAdminProductPage.generateSkus();

		const skus = await apiHelpers.headlessCommerceAdminCatalog
			.getProduct(productBundle.productId)
			.then((product) => product.skus);

		const sku1 = skus.find((sku) => sku.sku === 'BLACK');

		await apiHelpers.headlessCommerceAdminCatalog.postSkuUnitOfMeasure(
			sku1.id,
			{
				basePrice: 150,
				incrementalOrderQuantity: 3,
				name: {en_US: 'Box'},
				primary: true,
				priority: 1,
				rate: 1,
			}
		);
		await apiHelpers.headlessCommerceAdminCatalog.postSkuUnitOfMeasure(
			sku1.id,
			{
				basePrice: 50,
				incrementalOrderQuantity: 2,
				name: {en_US: 'Package'},
				priority: 2,
				rate: 0.5,
			}
		);

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

		await widgetPagePage.addPortlet('Product Details');

		await page.goto(`/web/${site.name}/p/uombundle`);

		await productDetailsPage
			.optionSelector('Color')
			.selectOption({label: 'Black'});

		await expect(await productDetailsPage.uomTable('Unit')).toBeVisible();
		await expect(
			await productDetailsPage.priceField('$ 50.00 / Box')
		).toBeVisible();
	}
);

test('LPD-18710 Price is correctly calculated for bundle product with options not marked as sku contributor', async ({
	apiHelpers,
	page,
	productDetailsPage,
	site,
	widgetPagePage,
}) => {
	const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
		groupId: site.id,
		title: getRandomString(),
	});

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

	await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

	await widgetPagePage.addPortlet('Product Details');

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
	commerceAdminChannelsPage,
	page,
	productDetailsPage,
	site,
	widgetPagePage,
}) => {
	const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
		groupId: site.id,
		title: getRandomString(),
	});

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

	await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

	await widgetPagePage.addPortlet('Product Details');

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

test('LPD-39598 Can view SKU UOM discount is applied on product details page', async ({
	apiHelpers,
	commerceAdminChannelsPage,
	page,
	productDetailsPage,
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
		'B2B'
	);

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

	const catalog = await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
		name: getRandomString(),
	});

	const product = await apiHelpers.headlessCommerceAdminCatalog.postProduct({
		catalogId: catalog.id,
		name: {en_US: getRandomString()},
	});

	const discount = await apiHelpers.headlessCommerceAdminPricing.postDiscount(
		{
			discountProducts: [
				{
					productId: product.productId,
				},
			],
			percentageLevel1: 50,
			target: 'skus',
			usePercentage: true,
		}
	);

	const productSkus = await apiHelpers.headlessCommerceAdminCatalog
		.getProduct(product.productId)
		.then((product) => {
			return product.skus;
		});

	const sku = productSkus[0];

	const uom1 =
		await apiHelpers.headlessCommerceAdminCatalog.postSkuUnitOfMeasure(
			sku.id,
			{
				basePrice: 10,
				incrementalOrderQuantity: 1,
				name: {en_US: getRandomString()},
				primary: true,
				priority: 1,
			}
		);

	await apiHelpers.headlessCommerceAdminPricing.postDiscountSku(discount.id, {
		skuId: sku.id,
		unitOfMeasureKey: uom1.key,
	});

	const uom2 =
		await apiHelpers.headlessCommerceAdminCatalog.postSkuUnitOfMeasure(
			sku.id,
			{
				basePrice: 20,
				incrementalOrderQuantity: 1,
				name: {en_US: getRandomString()},
				priority: 2,
			}
		);

	const discount2 =
		await apiHelpers.headlessCommerceAdminPricing.postDiscountSku(
			discount.id,
			{
				skuId: sku.id,
				unitOfMeasureKey: uom2.key,
			}
		);

	await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

	await widgetPagePage.addPortlet('Product Details');

	await performLogout(page);

	await performLogin(page, user.alternateName);

	await page.goto(`/web/${site.name}/p/` + product.name['en_US']);

	await productDetailsPage.uomCombobox.selectOption(uom1.key);

	await expect(
		await productDetailsPage.priceField(
			'$ 10.00',
			productDetailsPage.priceContainer
		)
	).toBeVisible();

	await expect(
		await productDetailsPage.priceField(
			'–50%',
			productDetailsPage.priceContainer
		)
	).toBeVisible();

	await expect(
		await productDetailsPage.priceField(
			'$ 5.00',
			productDetailsPage.priceContainer
		)
	).toBeVisible();

	await productDetailsPage.uomCombobox.selectOption(uom2.key);

	await expect(
		await productDetailsPage.priceField(
			'$ 20.00',
			productDetailsPage.priceContainer
		)
	).toBeVisible();

	await expect(
		await productDetailsPage.priceField(
			'–50%',
			productDetailsPage.priceContainer
		)
	).toBeVisible();

	await expect(
		await productDetailsPage.priceField(
			'$ 10.00',
			productDetailsPage.priceContainer
		)
	).toBeVisible();

	await performLogout(page);

	await performLoginViaApi({page, screenName: 'test'});

	await apiHelpers.headlessCommerceAdminPricing.deleteDiscountSku(
		discount2.discountSkuId
	);

	await performLogout(page);

	await performLogin(page, user.alternateName);

	await page.goto(`/web/${site.name}/p/` + product.name['en_US']);

	await productDetailsPage.uomCombobox.selectOption(uom1.key);

	await expect(
		await productDetailsPage.priceField(
			'$ 10.00',
			productDetailsPage.priceContainer
		)
	).toBeVisible();

	await expect(
		await productDetailsPage.priceField(
			'–50%',
			productDetailsPage.priceContainer
		)
	).toBeVisible();

	await expect(
		await productDetailsPage.priceField(
			'$ 10.00',
			productDetailsPage.priceContainer
		)
	).toBeVisible();

	await productDetailsPage.uomCombobox.selectOption(uom2.key);

	await expect(
		await productDetailsPage.priceField(
			'$ 20.00',
			productDetailsPage.priceContainer
		)
	).toBeVisible();

	await expect(
		await productDetailsPage.priceField(
			'–50%',
			productDetailsPage.priceContainer
		)
	).not.toBeVisible();

	await expect(
		await productDetailsPage.priceField(
			'$ 10.00',
			productDetailsPage.priceContainer
		)
	).not.toBeVisible();
});

test('COMMERCE-6364. As a buyer, I want the first selectable quantity of a product in Product Details to be the minimum multiple quantity if Minimum Order Quantity is higher than Multiple Order Quantity', async ({
	apiHelpers,
	commerceThemeMiniumCatalogPage,
	page,
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
	await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
		account.id,
		['demo.unprivileged@liferay.com']
	);

	const companyId = await page.evaluate(() => {
		return Liferay.ThemeDisplay.getCompanyId();
	});

	const role = await apiHelpers.headlessAdminUser.postRole({
		name: 'Buyer ' + getRandomString(),
		rolePermissions: [
			{
				actionIds: ['MANAGE_ADDRESSES', 'VIEW_ADDRESSES'],
				primaryKey: '0',
				resourceName: 'com.liferay.account.model.AccountEntry',
				scope: 3,
			},
			{
				actionIds: ['VIEW'],
				primaryKey: companyId,
				resourceName: 'com.liferay.commerce.model.CommerceOrderType',
				scope: 1,
			},
			{
				actionIds: [
					'ADD_COMMERCE_ORDER',
					'CHECKOUT_OPEN_COMMERCE_ORDERS',
					'MANAGE_COMMERCE_ORDER_DELIVERY_TERMS',
					'MANAGE_COMMERCE_ORDER_PAYMENT_METHODS',
					'MANAGE_COMMERCE_ORDER_PAYMENT_TERMS',
					'MANAGE_COMMERCE_ORDER_SHIPPING_OPTIONS',
					'VIEW_BILLING_ADDRESS',
					'VIEW_COMMERCE_ORDERS',
					'VIEW_OPEN_COMMERCE_ORDERS',
				],
				primaryKey: '0',
				resourceName: 'com.liferay.commerce.order',
				scope: 3,
			},
		],
	});

	await apiHelpers.headlessAdminUser.postRoleUserAccountAssociation(
		role.id,
		user.id
	);

	apiHelpers.data.push({
		id: `${role.id}_${user.id}`,
		type: 'roleUserAccountAssociation',
	});

	await apiHelpers.jsonWebServicesUser.addGroupUsers(site.id, [user.id]);

	const product = (
		await apiHelpers.headlessCommerceAdminCatalog.getProducts(
			new URLSearchParams({
				filter: `name eq 'U-Joint'`,
			})
		)
	).items[0];

	const productName = product.name['en_US'];

	await apiHelpers.headlessCommerceAdminCatalog.patchProduct(
		product.productId,
		{
			name: {en_US: productName},
			productConfiguration: {
				minOrderQuantity: 6,
				multipleOrderQuantity: 5,
			},
		}
	);

	const patchedProduct = (
		await apiHelpers.headlessCommerceAdminCatalog.getProducts(
			new URLSearchParams({
				filter: `name eq 'U-Joint'`,
				nestedFields: `skus,productConfiguration`,
			})
		)
	).items[0];

	const multipleQuantity = commerceThemeMiniumCatalogPage.getMultipleQuantity(
		0,
		patchedProduct.productConfiguration.multipleOrderQuantity
	);
	const minQuantity = commerceThemeMiniumCatalogPage.getProductMinQuantity(
		patchedProduct.productConfiguration.minOrderQuantity,
		multipleQuantity
	);
	const maxQuantity = commerceThemeMiniumCatalogPage.getProductMaxQuantity(
		patchedProduct.productConfiguration.maxOrderQuantity,
		multipleQuantity
	);

	await performLogout(page);
	await performLogin(page, user.alternateName);

	await page.goto(`/web/${site.name}`);

	await commerceThemeMiniumCatalogPage
		.productCard(productName)
		.getByRole('link')
		.first()
		.click();

	await expect(
		commerceThemeMiniumCatalogPage.quantitySelector(
			page.locator('.product-detail')
		)
	).toHaveValue(`${minQuantity}`);

	await commerceThemeMiniumCatalogPage
		.quantitySelector(page.locator('.product-detail'))
		.focus();

	let minQuantityNotSatisfied;
	let multipleQuantityNotSatisfied;
	let maxQuantityNotSatisfied;

	for (const quantitySelectorActualQuantity of [5, 20]) {
		await commerceThemeMiniumCatalogPage
			.quantitySelector(page.locator('.product-detail'))
			.fill(`${quantitySelectorActualQuantity}`);

		maxQuantityNotSatisfied = quantitySelectorActualQuantity > maxQuantity;
		minQuantityNotSatisfied = quantitySelectorActualQuantity < minQuantity;
		multipleQuantityNotSatisfied = !Number.isInteger(
			quantitySelectorActualQuantity / multipleQuantity
		);

		await commerceThemeMiniumCatalogPage.checkQuantitiesInPopOverMessages(
			maxQuantity,
			minQuantity,
			multipleQuantity,
			maxQuantityNotSatisfied,
			minQuantityNotSatisfied,
			multipleQuantityNotSatisfied
		);
	}
});

test(
	'Price is updated on initial load for single select options in product details',
	{tag: '@LPD-56974'},
	async ({
		apiHelpers,
		globalMenuPage,
		page,
		productDetailsPage,
		site,
		widgetPagePage,
	}) => {
		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: getRandomString(),
		});

		await apiHelpers.headlessCommerceAdminChannel.postChannel({
			siteGroupId: site.id,
		});

		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
				name: 'View product details',
			});
		const option = await apiHelpers.headlessCommerceAdminCatalog.postOption(
			'radio',
			'color',
			'Color',
			1
		);

		const product =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				name: {en_US: getRandomString()},
			});

		await apiHelpers.headlessCommerceAdminCatalog.postProduct({
			catalogId: catalog.id,
			name: {en_US: 'ProductBundle'},
			productOptions: [
				{
					fieldType: 'radio',
					key: 'color',
					name: {
						en_US: 'Color',
					},
					optionId: option.id,
					priceType: 'static',
					priority: 1,
					productOptionValues: [
						{
							deltaPrice: 5.0,
							key: 'black',
							name: {
								en_US: 'Black',
							},
							priority: 1,
							quantity: 10,
							skuId: product.skus[0].id,
						},
					],
				},
			],
		});

		await globalMenuPage.goToCommerce('Products');

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

		await widgetPagePage.addPortlet('Product Details');

		await page.goto(`/web/${site.name}/p/productbundle`);

		await expect(
			await productDetailsPage.priceField('$ 50.00')
		).toBeVisible();
	}
);

test(
	'The Request Quote action in the Product Details page automatically creates a new cart and requests the quote',
	{tag: ['@COMMERCE-11507', '@LPD-70003']},
	async ({
		apiHelpers,
		commerceAdminChannelDetailsPage,
		commerceAdminChannelsPage,
		page,
		productDetailsPage,
		site,
	}) => {
		let account;
		let buyerUser;
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

			const channel =
				await apiHelpers.headlessCommerceAdminChannel.postChannel({
					siteGroupId: site.id,
				});

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

			await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([
					getWidgetDefinition({
						id: getRandomString(),
						widgetName:
							'com_liferay_commerce_order_content_web_internal_portlet_CommerceOrderContentPortlet',
					}),
				]),
				siteId: site.id,
				title: getRandomString(),
			});
		});

		try {
			await test.step('As a Buyer, request a quote from Product Details page', async () => {
				await performLogout(page);
				await performLoginViaApi({
					page,
					screenName: buyerUser.alternateName,
				});

				await page.goto(
					`/web/${site.name}/p/${product.name['en_US']}`,
					{
						waitUntil: 'networkidle',
					}
				);

				await productDetailsPage.requestAQuoteButton.click();
				await productDetailsPage.requestAQuoteModal.isVisible();

				await expect(
					page.getByPlaceholder('Email Address')
				).toHaveValue(buyerUser.emailAddress);

				await productDetailsPage.requestAQuoteModalSubmit.click();

				await expect(
					page.getByText('Quote Requested', {exact: true})
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
	'User cannot see expired SKUs',
	{tag: '@LPD-85260'},
	async ({
		apiHelpers,
		commerceAdminProductPage,
		page,
		productDetailsPage,
		site,
		widgetPagePage,
	}) => {
		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: getRandomString(),
		});

		await apiHelpers.headlessCommerceAdminChannel.postChannel({
			siteGroupId: site.id,
		});

		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
				name: 'Catalog',
			});
		const option = await apiHelpers.headlessCommerceAdminCatalog.postOption(
			'select',
			'color',
			'Color',
			1
		);
		const product =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				name: {en_US: getRandomString()},
				productOptions: [
					{
						fieldType: 'select',
						key: 'color',
						name: {
							en_US: 'Color',
						},
						optionId: option.id,
						priority: 1,
						productOptionValues: [
							{
								key: 'black',
								name: {
									en_US: 'Black',
								},
								priority: 0,
							},
							{
								key: 'white',
								name: {
									en_US: 'White',
								},
								priority: 1,
							},
						],
						skuContributor: true,
					},
				],
			});

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

		await widgetPagePage.addPortlet('Product Details');

		await page.goto(`/web/${site.name}/p/${product.name['en_US']}`, {
			waitUntil: 'networkidle',
		});

		await expect(
			await productDetailsPage.optionSelector('Color')
		).toBeVisible();

		await productDetailsPage.optionSelector('Color').click();

		let optionSelectorOptionValues = await productDetailsPage
			.optionSelector('Color')
			.locator('option')
			.allTextContents();

		expect(optionSelectorOptionValues).toEqual([
			'Choose an Option',
			'Black',
			'White',
		]);

		await commerceAdminProductPage.gotoProduct(product.name['en_US']);

		await commerceAdminProductPage.generateSkus();

		await expect(
			page.getByText('Showing 1 to 3 of 3 entries.')
		).toBeVisible();

		const blackSku =
			await apiHelpers.headlessCommerceAdminCatalog.getSkuByName('BLACK');

		await apiHelpers.headlessCommerceAdminCatalog.patchSku(blackSku.id, {
			expirationDate: '2020-01-01T00:00:00Z',
		});

		await page.goto(`/web/${site.name}/p/${product.name['en_US']}`, {
			waitUntil: 'networkidle',
		});

		await expect(
			await productDetailsPage.optionSelector('Color')
		).toBeVisible();

		optionSelectorOptionValues = await productDetailsPage
			.optionSelector('Color')
			.locator('option')
			.allTextContents();

		expect(optionSelectorOptionValues).toEqual([
			'Choose an Option',
			'White',
		]);
	}
);

test(
	'Sales Agent can view product details when the selected account belongs to an authorized account group',
	{tag: ['@COMMERCE-12657', '@LPD-87650']},
	async ({
		accountsPage,
		apiHelpers,
		commerceThemeMiniumCatalogPage,
		editAccountChannelDefaultsPage,
		editAccountPage,
		page,
		productDetailsPage,
	}) => {
		const {channel, site} = await miniumSetUp(apiHelpers);

		const companyId = await page.evaluate(() =>
			Liferay.ThemeDisplay.getCompanyId()
		);

		const uJoint = (
			await apiHelpers.headlessCommerceAdminCatalog.getProducts(
				new URLSearchParams({filter: `name eq 'U-Joint'`})
			)
		).items[0];
		const uJointName = uJoint.name['en_US'];

		let account1;
		let account2;
		let accountGroup;
		let salesAgent;

		await test.step('Create accounts, sales agent and account group', async () => {
			let camResult;

			[account1, account2, camResult, accountGroup] = await Promise.all([
				apiHelpers.headlessAdminUser.postAccount({
					name: `Account ${getRandomString()}`,
					type: 'business',
				}),
				apiHelpers.headlessAdminUser.postAccount({
					name: `Account ${getRandomString()}`,
					type: 'business',
				}),
				createChannelAccountManagerUser(apiHelpers, {
					accountEntryActionIds: [
						'MANAGE_AVAILABLE_ACCOUNTS_VIA_USER_CHANNEL_REL',
					],
					companyId,
					siteId: site.id,
				}),
				apiHelpers.headlessAdminUser.postAccountGroup({
					name: `Account Group ${getRandomString()}`,
				}),
			]);

			salesAgent = camResult.user;

			apiHelpers.data.push({
				id: accountGroup.id,
				type: 'accountGroup',
			});
		});

		await test.step('Restrict U-Joint to the account group', async () => {
			await apiHelpers.headlessAdminUser.assignAccountToAccountGroup(
				account2.externalReferenceCode,
				accountGroup.externalReferenceCode
			);

			await apiHelpers.headlessCommerceAdminCatalog.patchProduct(
				uJoint.productId,
				{
					name: uJoint.name,
					productAccountGroupFilter: true,
					productAccountGroups: [
						{accountGroupId: accountGroup.id, id: 0},
					],
				}
			);
		});

		await test.step('Assign the sales agent as Channel Account Manager for both accounts', async () => {
			for (const account of [account1, account2]) {
				await accountsPage.goto();
				await accountsPage.accountNameLink(account.name).click();
				await editAccountPage.channelDefaultsLink.click();
				await editAccountChannelDefaultsPage.addChannelAccountManager({
					channelName: channel.name,
					userScreenName: salesAgent.alternateName,
				});
			}
		});

		await test.step('Switch user, select account2, and verify product details', async () => {
			await performUserSwitchViaApi(page, salesAgent.alternateName);

			await page.goto(`/web/${site.name}`);

			await commerceThemeMiniumCatalogPage.openAccountSelectorDropdown();

			await commerceThemeMiniumCatalogPage
				.accountSelectorAccount(account2.name)
				.click();

			const productLink =
				commerceThemeMiniumCatalogPage.productLink(uJointName);

			await expect(productLink).toBeVisible();

			await productLink.click();

			await expect(
				await productDetailsPage.nameField(uJointName)
			).toBeVisible();
			await expect(
				await productDetailsPage.skuField('MIN55861')
			).toBeVisible();
			await expect(
				await productDetailsPage.priceField('$ 24.00')
			).toBeVisible();
		});
	}
);

test(
	'Verify the storefront product page reflects the calculated stock quantity',
	{tag: ['@COMMERCE-11873']},
	async ({
		apiHelpers,
		commerceAdminChannelsPage,
		page,
		productDetailsPage,
		site,
		widgetPagePage,
	}) => {
		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

		const channel =
			await apiHelpers.headlessCommerceAdminChannel.postChannel({
				siteGroupId: site.id,
			});

		await commerceAdminChannelsPage.changeCommerceChannelSiteType(
			channel.name,
			'B2B'
		);

		const product =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				name: {en_US: 'Product' + getRandomInt()},
				productConfiguration: {
					allowBackOrder: false,
					displayAvailability: true,
					displayStockQuantity: true,
					maxOrderQuantity: 10000,
					minOrderQuantity: 1,
					minStockQuantity: 0,
					multipleOrderQuantity: 1,
				},
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
					warehouseItems: [{quantity: 3, sku: sku.sku}],
				}
			);

		await apiHelpers.headlessCommerceAdminInventoryApiHelper.postWarehousesChannels(
			warehouse.id,
			channel.id
		);

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
					city: 'Test City',
					countryISOCode: 'US',
					defaultBilling: true,
					defaultShipping: true,
					name: 'Test Address',
					regionISOCode: 'CA',
					street1: 'Test Street',
					zip: '12345',
				}
			);

		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: getRandomString(),
		});

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

		await widgetPagePage.addPortlet('Product Details');

		await test.step('Verify the product page shows the calculated stock', async () => {
			await page.goto(`/web/${site.name}/p/${product.name['en_US']}`);

			await expect(page.getByText('3 in Stock')).toBeVisible();
		});

		await test.step('Verify ordering above the available stock is rejected', async () => {
			await productDetailsPage.quantitySelector.fill('4');

			await productDetailsPage.addToCartButton.click();

			await waitForAlert(page, 'The specified quantity is unavailable.', {
				autoClose: false,
				type: 'danger',
			});
		});

		await test.step('Place an order that consumes the available stock', async () => {
			const cart = await apiHelpers.headlessCommerceDeliveryCart.postCart(
				{
					accountId: account.id,
					billingAddressId: address.id,
					cartItems: [{quantity: 3, skuId: Number(sku.id)}],
					shippingAddressId: address.id,
				},
				channel.id
			);

			await apiHelpers.headlessCommerceDeliveryCart.checkoutCart(cart.id);
		});

		await test.step('Verify the product page shows no stock and disables ordering', async () => {
			await page.goto(`/web/${site.name}/p/${product.name['en_US']}`);

			await expect(page.getByText('0 in Stock')).toBeVisible();

			await expect(productDetailsPage.addToCartButton).toBeDisabled();
		});
	}
);
