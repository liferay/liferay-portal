/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {applicationsMenuPageTest} from '../../fixtures/applicationsMenuPageTest';
import {commercePagesTest} from '../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../fixtures/loginTest';
import {getRandomInt} from '../../utils/getRandomInt';
import getRandomString from '../../utils/getRandomString';
import performLogin, {performLogout} from '../../utils/performLogin';
import {waitForAlert} from '../../utils/waitForAlert';

export const test = mergeTests(
	applicationsMenuPageTest,
	commercePagesTest,
	dataApiHelpersTest,
	loginTest()
);

test('LPD-5780 Modal title and product name appear properly in product menu', async ({
	apiHelpers,
	commerceAdminProductDetailsProductRelationsPage,
	commerceAdminProductPage,
}) => {
	const catalog = await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
		name: 'Product Catalog',
	});

	const product1 = await apiHelpers.headlessCommerceAdminCatalog.postProduct({
		catalogId: catalog.id,
		name: {
			en_US: '"Product' + getRandomInt(),
		},
	});

	const product2 = await apiHelpers.headlessCommerceAdminCatalog.postProduct({
		catalogId: catalog.id,
	});

	await commerceAdminProductPage.gotoProduct(product1.name.en_US);

	await commerceAdminProductDetailsProductRelationsPage.addSpareProductRelation();

	await expect(
		await commerceAdminProductDetailsProductRelationsPage.addProductRelationHeading(
			product1.name.en_US
		)
	).toBeVisible();

	await commerceAdminProductPage.modalCancelButton.click();

	await commerceAdminProductPage.gotoProduct(product2.name.en_US);

	await commerceAdminProductDetailsProductRelationsPage.addSpareProductRelation();

	await (
		await commerceAdminProductPage.validProductCheckbox(product1.name.en_US)
	).check();

	await commerceAdminProductPage.modalAddButton.click();

	await expect(
		await commerceAdminProductPage.specificProductMenuLink(
			product1.name.en_US
		)
	).toBeVisible();
});

test('COMMERCE-12809 As a buyer, I want to be able to verify the included and excluded option values by combining the Products Limit rule', async ({
	apiHelpers,
	applicationsMenuPage,
	commerceAdminProductPage,
	commerceInstanceSettingsPage,
	commerceLayoutsPage,
	page,
	productDetailsPage,
}) => {
	await commerceInstanceSettingsPage.toggleShowUnselectableOptions(true);

	try {
		const site = await apiHelpers.headlessSite.createSite({
			name: 'ProductDetailsSite',
		});

		apiHelpers.data.push({id: site.id, type: 'site'});

		await apiHelpers.headlessCommerceAdminChannel.postChannel({
			name: 'ProductDetailsSite',
			siteGroupId: site.id,
		});

		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
				name: 'ProductDetailsSite',
			});

		const product1 =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				name: {en_US: 'Product1'},
			});
		const product2 =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				name: {en_US: 'Product2'},
			});
		const product3 =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				name: {en_US: 'Product3'},
			});
		const product4 =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				name: {en_US: 'Product4'},
			});

		await Promise.all([
			apiHelpers.headlessCommerceAdminCatalog.postProductRelatedProduct(
				product1.productId,
				{productId: product3.productId, type: 'requires-in-bundle'}
			),
			apiHelpers.headlessCommerceAdminCatalog.postProductRelatedProduct(
				product2.productId,
				{productId: product4.productId, type: 'incompatible-in-bundle'}
			),
		]);

		const option1 =
			await apiHelpers.headlessCommerceAdminCatalog.postOption(
				'select',
				'option1',
				'Option1',
				1
			);
		const option2 =
			await apiHelpers.headlessCommerceAdminCatalog.postOption(
				'select',
				'option2',
				'Option2',
				1
			);

		const bundleProduct =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				name: {en_US: 'ProductBundle'},
				productOptions: [
					{
						fieldType: 'select',
						key: 'option1',
						name: {
							en_US: 'Option1',
						},
						optionId: option1.id,
						priceType: 'static',
						priority: 1,
						productOptionValues: [
							{
								deltaPrice: 0.0,
								key: 'value1',
								name: {
									en_US: 'Value1',
								},
								priority: 1,
								quantity: 1,
								skuId: product1.skus[0].id,
							},
							{
								deltaPrice: 0.0,
								key: 'value2',
								name: {
									en_US: 'Value2',
								},
								priority: 2,
								quantity: 1,
								skuId: product2.skus[0].id,
							},
						],
						skuContributor: true,
					},
					{
						fieldType: 'select',
						key: 'option2',
						name: {
							en_US: 'Option2',
						},
						optionId: option2.id,
						priceType: 'static',
						priority: 2,
						productOptionValues: [
							{
								deltaPrice: 0.0,
								key: 'value3',
								name: {
									en_US: 'Value3',
								},
								priority: 1,
								quantity: 1,
								skuId: product3.skus[0].id,
							},
							{
								deltaPrice: 0.0,
								key: 'value4',
								name: {
									en_US: 'Value4',
								},
								priority: 2,
								quantity: 1,
								skuId: product4.skus[0].id,
							},
						],
						skuContributor: true,
					},
				],
			});

		await apiHelpers.headlessCommerceAdminOrder.postOrderRule({
			type: 'products-limit',
			typeSettings:
				'products-limit-field-product-ids=' +
				product3.productId +
				'\nproducts-limit-field-product-quantity=0.9\n',
		});

		await applicationsMenuPage.goToProducts();

		await commerceAdminProductPage.managementToolbarSearchInput.fill(
			bundleProduct.name.en_US
		);
		await commerceAdminProductPage.managementToolbarSearchInput.press(
			'Enter'
		);

		await page
			.getByRole('link', {exact: true, name: bundleProduct.name.en_US})
			.click();

		await commerceAdminProductPage.generateSkus();

		const account = await apiHelpers.headlessAdminUser.postAccount({
			name: getRandomString(),
			type: 'person',
		});

		apiHelpers.data.push({id: account.id, type: 'account'});

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account.id,
			['test@liferay.com']
		);

		await applicationsMenuPage.goToSite('ProductDetailsSite');

		await commerceLayoutsPage.goToPages(false);
		await commerceLayoutsPage.createWidgetPage('ProductDetailsSite');

		await page.goto(`/web/${site.name}`);

		await productDetailsPage.addProductDetailsWidget();

		await page.goto(`/web/${site.name}/p/productbundle`);

		await expect(page.getByText('Value1', {exact: true})).toBeVisible();
		await expect(page.getByText('Value3', {exact: true})).toBeVisible();

		await page.getByLabel('Option2').click();

		await expect(
			page.getByRole('option', {
				name: 'Value3 No more than 0.9 products in this product range can be purchased together.',
			})
		).toBeVisible();

		await expect(page.getByRole('option', {name: 'Value4'})).toBeVisible();

		await page.getByRole('option', {name: 'Value4'}).click();
		await page.getByLabel('Option1').click();

		await expect(
			page.getByRole('option', {
				name: 'Value1 Product1 requires Product3 to be purchased also.',
			})
		).toBeVisible();
		await expect(
			page.getByRole('option', {
				name: 'Value2 Product2 cannot be combined with Product4.',
			})
		).toBeVisible();
	}
	finally {
		await commerceInstanceSettingsPage.toggleShowUnselectableOptions(false);
	}
});

test('COMMERCE-8153 Verify the visibility rules', async ({
	apiHelpers,
	commerceAdminProductDetailsDiagramPage,
	commerceAdminProductDetailsPage,
	commerceAdminProductPage,
	page,
}) => {
	const account1 = await apiHelpers.headlessAdminUser.postAccount({
		name: 'Account1',
		type: 'person',
	});

	await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
		account1.id,
		['test@liferay.com']
	);

	apiHelpers.data.push({id: account1.id, type: 'account'});

	const account2 = await apiHelpers.headlessAdminUser.postAccount({
		name: 'Account2',
		type: 'person',
	});

	await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
		account2.id,
		['test@liferay.com']
	);

	apiHelpers.data.push({id: account2.id, type: 'account'});

	const accountGroup = await apiHelpers.headlessAdminUser.postAccountGroup({
		name: getRandomString(),
	});

	apiHelpers.data.push({id: accountGroup.id, type: 'accountGroup'});

	await apiHelpers.headlessAdminUser.assignAccountToAccountGroup(
		account2.externalReferenceCode,
		accountGroup.externalReferenceCode
	);

	const site = await apiHelpers.headlessSite.createSite({
		name: 'ProductDetailsSite',
	});

	apiHelpers.data.push({id: site.id, type: 'site'});

	const channel = await apiHelpers.headlessCommerceAdminChannel.postChannel({
		name: 'ProductDetailsSite',
		siteGroupId: site.id,
	});

	const catalog = await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
		name: 'ProductDetailsSite',
	});

	const product1 = await apiHelpers.headlessCommerceAdminCatalog.postProduct({
		catalogId: catalog.id,
		name: {en_US: 'Product1'},
		productAccountGroupFilter: true,
		productAccountGroups: [{accountGroupId: accountGroup.id, id: 0}],
	});

	const product2 = await apiHelpers.headlessCommerceAdminCatalog.postProduct({
		catalogId: catalog.id,
		name: {en_US: 'Product2'},
	});

	const productDiagram =
		await apiHelpers.headlessCommerceAdminCatalog.postProduct({
			catalogId: catalog.id,
			name: {en_US: 'Diagram'},
			productType: 'diagram',
		});

	await commerceAdminProductPage.gotoProduct(productDiagram.name['en_US']);
	await commerceAdminProductDetailsPage.goToProductDiagram();
	await commerceAdminProductDetailsDiagramPage.goToDragAndDropImages();

	await page
		.frameLocator('iframe[title="Select File"]')
		.getByRole('link', {name: 'Provided by Liferay'})
		.click();
	await page
		.frameLocator('iframe[title="Select File"]')
		.locator(
			'[id="_com_liferay_item_selector_web_portlet_ItemSelectorPortlet_repositoryEntriesSearchContainer_1"] img'
		)
		.click();

	await apiHelpers.headlessCommerceAdminCatalog.postPin(
		productDiagram.productId,
		{
			mappedProduct: {
				productId: product1.productId,
				quantity: 1,
				sequence: '1',
				sku: product1.skus[0].sku,
				skuId: product1.skus[0].id,
			},
			sequence: '1',
		}
	);

	await apiHelpers.headlessCommerceAdminCatalog.postPin(
		productDiagram.productId,
		{
			mappedProduct: {
				productId: product2.productId,
				quantity: 1,
				sequence: '2',
				sku: product2.skus[0].sku,
				skuId: product2.skus[0].id,
			},
			sequence: '2',
		}
	);

	const productPins1 =
		await apiHelpers.headlessCommerceDeliveryCatalog.getChannelProductPinsPage(
			account1.id,
			channel.id,
			productDiagram.productId
		);

	const productPins2 =
		await apiHelpers.headlessCommerceDeliveryCatalog.getChannelProductPinsPage(
			account2.id,
			channel.id,
			productDiagram.productId
		);

	expect(productPins1.items[0].mappedProduct).toBeUndefined();
	expect(productPins1.items[1].mappedProduct.productId).toEqual(
		product2.productId
	);
	expect(productPins2.items[0].mappedProduct.productId).toEqual(
		product1.productId
	);
	expect(productPins2.items[1].mappedProduct.productId).toEqual(
		product2.productId
	);
});

test('COMMERCE-12805 As a buyer, I want to be able to verify the included and excluded option values are disabled with reason messages', async ({
	apiHelpers,
	applicationsMenuPage,
	commerceAdminProductPage,
	commerceInstanceSettingsPage,
	commerceLayoutsPage,
	page,
	productDetailsPage,
}) => {
	await commerceInstanceSettingsPage.toggleShowUnselectableOptions(true);

	try {
		const site = await apiHelpers.headlessSite.createSite({
			name: getRandomString(),
		});

		apiHelpers.data.push({id: site.id, type: 'site'});

		await apiHelpers.headlessCommerceAdminChannel.postChannel({
			siteGroupId: site.id,
		});

		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

		const product1 =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				name: {en_US: 'Product1'},
			});
		const product2 =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				name: {en_US: 'Product2'},
			});
		const product3 =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				name: {en_US: 'Product3'},
			});
		const product4 =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				name: {en_US: 'Product4'},
			});

		await Promise.all([
			apiHelpers.headlessCommerceAdminCatalog.postProductRelatedProduct(
				product1.productId,
				{productId: product3.productId, type: 'requires-in-bundle'}
			),
			apiHelpers.headlessCommerceAdminCatalog.postProductRelatedProduct(
				product2.productId,
				{productId: product4.productId, type: 'incompatible-in-bundle'}
			),
		]);

		const option1 =
			await apiHelpers.headlessCommerceAdminCatalog.postOption(
				'select',
				'option1',
				'Option1',
				1
			);
		const option2 =
			await apiHelpers.headlessCommerceAdminCatalog.postOption(
				'select',
				'option2',
				'Option2',
				1
			);

		const bundleProduct =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				name: {en_US: 'ProductBundle'},
				productOptions: [
					{
						fieldType: 'select',
						key: 'option1',
						name: {
							en_US: 'Option1',
						},
						optionId: option1.id,
						priceType: 'static',
						priority: 1,
						productOptionValues: [
							{
								deltaPrice: 0.0,
								key: 'value1',
								name: {
									en_US: 'Value1',
								},
								priority: 1,
								quantity: 1,
								skuId: product1.skus[0].id,
							},
							{
								deltaPrice: 0.0,
								key: 'value2',
								name: {
									en_US: 'Value2',
								},
								preselected: true,
								priority: 2,
								quantity: 1,
								skuId: product2.skus[0].id,
							},
						],
						skuContributor: true,
					},
					{
						fieldType: 'select',
						key: 'option2',
						name: {
							en_US: 'Option2',
						},
						optionId: option2.id,
						priceType: 'static',
						priority: 2,
						productOptionValues: [
							{
								deltaPrice: 0.0,
								key: 'value3',
								name: {
									en_US: 'Value3',
								},
								priority: 1,
								quantity: 1,
								skuId: product3.skus[0].id,
							},
							{
								deltaPrice: 0.0,
								key: 'value4',
								name: {
									en_US: 'Value4',
								},
								preselected: true,
								priority: 2,
								quantity: 1,
								skuId: product4.skus[0].id,
							},
						],
						skuContributor: true,
					},
				],
			});

		await applicationsMenuPage.goToProducts();

		await commerceAdminProductPage.managementToolbarSearchInput.fill(
			bundleProduct.name.en_US
		);
		await commerceAdminProductPage.managementToolbarSearchInput.press(
			'Enter'
		);

		await page
			.getByRole('link', {exact: true, name: bundleProduct.name.en_US})
			.click();

		await commerceAdminProductPage.generateSkus();

		const account = await apiHelpers.headlessAdminUser.postAccount({
			name: getRandomString(),
			type: 'person',
		});

		apiHelpers.data.push({id: account.id, type: 'account'});

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account.id,
			['test@liferay.com']
		);

		await applicationsMenuPage.goToSite(site.name);

		await commerceLayoutsPage.goToPages(false);
		await commerceLayoutsPage.createWidgetPage(getRandomString());

		await page.goto(`/web/${site.name}`);

		await productDetailsPage.addProductDetailsWidget();

		await page.goto(`/web/${site.name}/p/productbundle`);

		await expect(page.getByText('Value2', {exact: true})).toBeVisible();
		await expect(page.getByText('Value4', {exact: true})).toBeVisible();

		await productDetailsPage.addToCartButton.click();

		await expect(
			page.getByText('Danger:Product4 cannot be combined with Product2.')
		).toBeVisible();
	}
	finally {
		await commerceInstanceSettingsPage.toggleShowUnselectableOptions(false);
	}
});

test('LPD-33075 Verify buyers can view the SKU of a product on the product card if it set.', async ({
	apiHelpers,
	applicationsMenuPage,
	commerceAdminChannelsPage,
	commerceLayoutsPage,
	page,
	productPublisherPage,
}) => {
	const site = await apiHelpers.headlessSite.createSite({
		name: getRandomString(),
	});

	apiHelpers.data.push({id: site.id, type: 'site'});

	const account = await apiHelpers.headlessAdminUser.postAccount({
		name: getRandomString(),
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

	await commerceAdminChannelsPage.changeCommerceChannelSiteType(
		channel.name,
		'B2B'
	);

	await waitForAlert(page);

	await applicationsMenuPage.goToSite(site.name);

	await commerceLayoutsPage.goToPages(false);
	await commerceLayoutsPage.createWidgetPage('View product Sku');

	await page.goto(`/web/${site.name}`);

	await productPublisherPage.addProductPublisherWidget();

	await performLogout(page);

	await performLogin(page, 'demo.unprivileged');

	await page.goto(`/web/${site.name}/` + 'view-product-sku');

	await expect(
		await productPublisherPage.productSku(product.skus[0].sku)
	).toBeVisible();
});

test('LPD-3424 Can click AddToButton button multiple times on Diagram Product Display Page', async ({
	apiHelpers,
	applicationsMenuPage,
	commerceAdminProductDetailsDiagramPage,
	commerceAdminProductDetailsPage,
	commerceAdminProductPage,
	commerceLayoutsPage,
	page,
	productDetailsPage,
}) => {
	const site = await apiHelpers.headlessSite.createSite({
		name: 'ProductDetailsSite',
	});

	apiHelpers.data.push({id: site.id, type: 'site'});

	const account1 = await apiHelpers.headlessAdminUser.postAccount({
		name: 'Account1',
		type: 'person',
	});

	await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
		account1.id,
		['test@liferay.com']
	);

	apiHelpers.data.push({id: account1.id, type: 'account'});

	await apiHelpers.headlessCommerceAdminChannel.postChannel({
		name: getRandomString(),
		siteGroupId: site.id,
	});

	const catalog = await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
		name: 'ProductDetailsSite',
	});

	const product1 = await apiHelpers.headlessCommerceAdminCatalog.postProduct({
		catalogId: catalog.id,
		name: {en_US: 'Product1'},
	});

	const productDiagram =
		await apiHelpers.headlessCommerceAdminCatalog.postProduct({
			catalogId: catalog.id,
			name: {en_US: 'Diagram'},
			productType: 'diagram',
		});

	await commerceAdminProductPage.gotoProduct(productDiagram.name['en_US']);
	await commerceAdminProductDetailsPage.goToProductDiagram();
	await commerceAdminProductDetailsDiagramPage.goToDragAndDropImages();

	await page
		.frameLocator('iframe[title="Select File"]')
		.getByRole('link', {name: 'Provided by Liferay'})
		.click();
	await page
		.frameLocator('iframe[title="Select File"]')
		.locator(
			'[id="_com_liferay_item_selector_web_portlet_ItemSelectorPortlet_repositoryEntriesSearchContainer_1"] img'
		)
		.click();

	const pin = await apiHelpers.headlessCommerceAdminCatalog.postPin(
		productDiagram.productId,
		{
			mappedProduct: {
				productId: product1.productId,
				quantity: 1,
				sequence: 'pinitem',
				sku: product1.skus[0].sku,
				skuId: product1.skus[0].id,
			},
			positionX: 50,
			positionY: 50,
			sequence: 'pinitem',
		}
	);

	await applicationsMenuPage.goToSite(site.name);

	await commerceLayoutsPage.goToPages(false);
	await commerceLayoutsPage.createWidgetPage('View product Sku');

	await page.goto(`/web/${site.name}`);

	await productDetailsPage.addProductDetailsWidget();
	await page.goto(`/web/${site.name}/p/diagram`);
	await (await productDetailsPage.diagramPin(pin.sequence)).click();
	await productDetailsPage.pinAddToCartButton.click();
	await expect(productDetailsPage.pinAddToCartButton).toHaveClass(/is-added/);
	await expect(productDetailsPage.pinAddToCartButton).not.toHaveClass(
		/not-allowed/
	);
});
