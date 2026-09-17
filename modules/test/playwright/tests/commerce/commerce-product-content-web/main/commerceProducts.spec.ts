/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import {createReadStream} from 'fs';
import path from 'node:path';

import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {globalMenuPagesTest} from '../../../../fixtures/globalMenuPagesTest';
import {isolatedSiteTest} from '../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {pageViewModePagesTest} from '../../../../fixtures/pageViewModePagesTest';
import {liferayConfig} from '../../../../liferay.config';
import {getRandomInt} from '../../../../utils/getRandomInt';
import getRandomString from '../../../../utils/getRandomString';
import performLogin, {
	performLoginViaApi,
	performLogout,
} from '../../../../utils/performLogin';
import {waitForAlert} from '../../../../utils/waitForAlert';
import getFragmentDefinition from '../../../layout-content-page-editor-web/main/utils/getFragmentDefinition';
import getPageDefinition from '../../../layout-content-page-editor-web/main/utils/getPageDefinition';
import getWidgetDefinition from '../../../layout-content-page-editor-web/main/utils/getWidgetDefinition';
import {createAccountWithBuyerUser, miniumSetUp} from '../../utils/commerce';

export const test = mergeTests(
	commercePagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	globalMenuPagesTest,
	isolatedSiteTest,
	loginTest(),
	pageViewModePagesTest
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
	commerceAdminProductPage,
	commerceInstanceSettingsPage,
	globalMenuPage,
	page,
	site,
	widgetPagePage,
}) => {
	await commerceInstanceSettingsPage.toggleShowUnselectableOptions(true);

	try {
		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: getRandomString(),
		});

		await apiHelpers.headlessCommerceAdminChannel.postChannel({
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
				name: {en_US: getRandomString()},
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

		await globalMenuPage.goToCommerce('Products');

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

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account.id,
			['test@liferay.com']
		);

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

		await widgetPagePage.addPortlet('Product Details');

		await page.goto(`/web/${site.name}/p/${bundleProduct.name.en_US}`);

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

	const account2 = await apiHelpers.headlessAdminUser.postAccount({
		name: 'Account2',
		type: 'person',
	});

	await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
		account2.id,
		['test@liferay.com']
	);

	const accountGroup = await apiHelpers.headlessAdminUser.postAccountGroup({
		name: getRandomString(),
	});

	apiHelpers.data.push({id: accountGroup.id, type: 'accountGroup'});

	await apiHelpers.headlessAdminUser.assignAccountToAccountGroup(
		account2.externalReferenceCode,
		accountGroup.externalReferenceCode
	);

	const site = await apiHelpers.headlessAdminSite.postSite({
		name: 'ProductDetailsSite',
	});

	const channel = await apiHelpers.headlessCommerceAdminChannel.postChannel({
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

test('LPD-33807 Mapped product add to cart', async ({
	apiHelpers,
	commerceAdminChannelsPage,
	page,
	productDetailsPage,
}) => {
	const catalog = await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
		name: 'ProductDetailsSite',
	});

	const product = await apiHelpers.headlessCommerceAdminCatalog.postProduct({
		catalogId: catalog.id,
		name: {en_US: 'Product'},
	});

	const productDiagram =
		await apiHelpers.headlessCommerceAdminCatalog.postProduct({
			catalogId: catalog.id,
			diagram: {
				attachmentBase64: {
					attachment:
						'iVBORw0KGgoAAAANSUhEUgAAAOAAAADgCAMAAAAt85rTAAAAeFBMVEX///8LY84AXM+frr4AYM0AWcwAXMwAUspRgta9zO2NquIAYc0AXs35+ffL0tqotsS5zOkRadH///sAV8u0xefv8/sueNOyye2HpN5vldjA0efu8fR5ntnn7PJjktiowuVfjNfR3ezX4OqOqtpIhdcob9CNseMYbtMc8dVDAAADbUlEQVR4nO2da3OqMBBAQwv4CGoFi4+qtbW2//8fFmE6t9mAo3cwpMw5H3e2jqcwCYlLVikAAAAAAAAAAAAAAAAAAAAAAAAAAACAP8hkOnt0xGw6ca73vAyyB2dkyfLZrd9TnoWBQ3SWP7n1S1zqnUlcGj679zsburtLl5l7vyDIlq78JonuQjAMXI2l004uYHEJp44EZw/dCD7MHAk+diX4iCCCCCKIIII+CuokNqnWUlpEq0c8K/naB7/uBHVwWI1/sxqW4dyMjvfzwkXP12Z0lV9p2J1gctiJlLe8uIbZJjWjL9u4SN6+mNF0c+WzbXeC8djKGRaCAyu6iIIgGlnhQW8ERwgi6AIEFYImCCLYLggqBE0QRLBdbMGVTElLwVSGy9XEwkr2XjAcvomvvDkVi9joVRju9kmxHtyLxWP6GvkuGASnocnpHNRfIppXybkIf3m/oi+uoaSM6tqolez/nowjELyfoM4GJlF528UieqkyQ0cy2bpzO9w2zDdmRvp6HjjiTzGKvn80Guq5HHI31m5il/OgnPGqedD6TX3ROCGEQ+sjVrE/grc8yTQJWsljBO8FggpBBBWCdwRBhSCCCsE7gqDqm2CyFoUTaldWWbyL6OQov/M/wVwWarysZeV7hwve+XZksNiXX/pjYYaPl/bP9mbyYjv3Z8Eb6CQyqf75oYherGmSH2Ens+mE4H8L6rj2Fq3HSq52oqy73KNbtBhkxAixv+D3dRQjUrXXJgaZkU+DTLKvnSbqiY9ys+09832aaJjo66n/fdDviR5BBBFEsE0QVAiaIIhguyCoEDTxX7C+CKFB0CrGmwx8L0JoKCOpJ/wQu4npZ3xeBvtcRtJUCNRgKJPLK+V1IZAjEHQueFM5ZX1FpteC1xbEVttntTW1fgveUtKsT/J92DdrpvFO8JaidHseVB7Ng02C1l82v1bg95MMgj8g+BsE7waCCAoQRLBdEERQ0DNBazXxZwVrjjwq1oNx7ZFHYS7eklW7gz9VFg1Yh1atLx1aNRTnXh0Cj7YNmwxvOXYsrE32W7BlEEQQQQQRRNAHwd4fJN77o+AngdNmGj/oxFljlL63Y+h/Q43et0Qpm9o4baoROm5qc25LlLhsSxS4bkuket9YCgAAAAAAAAAAAAAAAAAAAAAAAAAAAKAFvgGY6WrR7U77yAAAAABJRU5ErkJggg==',
					title: {en_US: 'title'},
				},
			},
			name: {en_US: 'diagram'},
			productType: 'diagram',
		});

	await apiHelpers.headlessCommerceAdminCatalog.postPin(
		productDiagram.productId,
		{
			mappedProduct: {
				productId: product.productId,
				quantity: 1,
				sequence: '1',
				sku: product.skus[0].sku,
				skuId: product.skus[0].id,
			},
			sequence: '1',
		}
	);

	const account = await apiHelpers.headlessAdminUser.postAccount({
		name: 'Account',
		type: 'business',
	});

	await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
		account.id,
		['test@liferay.com']
	);

	const site = await apiHelpers.headlessAdminSite.postSite({
		name: getRandomString(),
	});

	await apiHelpers.headlessDelivery.createSitePage({
		pageDefinition: getPageDefinition([
			getFragmentDefinition({
				id: getRandomString(),
				key: 'COMMERCE_CART_FRAGMENTS-mini-cart',
			}),
			getFragmentDefinition({
				id: getRandomString(),
				key: 'COMMERCE_ACCOUNT_FRAGMENTS-account-selector',
			}),
			getWidgetDefinition({
				id: getRandomString(),
				widgetName:
					'com_liferay_commerce_product_content_web_internal_portlet_CPContentPortlet',
			}),
		]),
		siteId: site.id,
		title: getRandomString(),
	});

	const channel = await apiHelpers.headlessCommerceAdminChannel.postChannel({
		siteGroupId: site.id,
	});

	await commerceAdminChannelsPage.changeCommerceChannelSiteType(
		channel.name,
		'B2B'
	);

	const cart = await apiHelpers.headlessCommerceDeliveryCart.postCart(
		{
			accountId: account.id,
		},
		channel.id
	);

	await page.goto(`/web/${site.name}/p/diagram`);

	await productDetailsPage.mappedProductCheckbox.setChecked(true);

	await productDetailsPage.mappedProductAddToCartButton.click();

	await waitForAlert(
		page,
		'Success:The product was successfully added to the cart.'
	);

	await productDetailsPage.mappedProductAddToCartButton.click();

	await waitForAlert(
		page,
		'Success:The product was successfully added to the cart.'
	);

	const cartItems =
		await apiHelpers.headlessCommerceDeliveryCart.getCartItems(cart.id);

	await expect(cartItems.items[0].quantity).toEqual(2);
});

test('COMMERCE-12805 As a buyer, I want to be able to verify the included and excluded option values are disabled with reason messages', async ({
	apiHelpers,
	commerceAdminProductPage,
	commerceInstanceSettingsPage,
	globalMenuPage,
	page,
	productDetailsPage,
	site,
	widgetPagePage,
}) => {
	await commerceInstanceSettingsPage.toggleShowUnselectableOptions(true);

	try {
		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: getRandomString(),
		});

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
				name: {en_US: getRandomString()},
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

		await globalMenuPage.goToCommerce('Products');

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

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account.id,
			['test@liferay.com']
		);

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

		await widgetPagePage.addPortlet('Product Details');

		await page.goto(`/web/${site.name}/p/${bundleProduct.name.en_US}`);

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
	commerceAdminChannelsPage,
	page,
	productPublisherPage,
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

	await commerceAdminChannelsPage.changeCommerceChannelSiteType(
		channel.name,
		'B2B'
	);

	await waitForAlert(page);

	await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

	await widgetPagePage.addPortlet('Product Publisher');

	await performLogout(page);

	await performLogin(page, 'demo.unprivileged');

	await page.goto(`/web/${site.name}/`);

	await expect(
		await productPublisherPage.productSku(product.skus[0].sku)
	).toBeVisible();
});

test('LPD-3424 Can click AddToButton button multiple times on Diagram Product Display Page', async ({
	apiHelpers,
	commerceAdminProductDetailsDiagramPage,
	commerceAdminProductDetailsPage,
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

	const account1 = await apiHelpers.headlessAdminUser.postAccount({
		name: getRandomString(),
		type: 'person',
	});

	await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
		account1.id,
		['test@liferay.com']
	);

	await apiHelpers.headlessCommerceAdminChannel.postChannel({
		siteGroupId: site.id,
	});

	const catalog = await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
		name: getRandomString(),
	});

	const product1 = await apiHelpers.headlessCommerceAdminCatalog.postProduct({
		catalogId: catalog.id,
		name: {en_US: getRandomString()},
	});

	const productDiagram =
		await apiHelpers.headlessCommerceAdminCatalog.postProduct({
			catalogId: catalog.id,
			name: {en_US: getRandomString()},
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

	await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

	await widgetPagePage.addPortlet('Product Details');

	await page.goto(`/web/${site.name}/p/${productDiagram.name['en_US']}`);
	await (await productDetailsPage.diagramPin(pin.sequence)).click();
	await productDetailsPage.pinAddToCartButton.click();
	await expect(productDetailsPage.pinAddToCartButton).toHaveClass(/is-added/);
	await expect(productDetailsPage.pinAddToCartButton).not.toHaveClass(
		/not-allowed/
	);
});

test('LPD-37780 Friendly URLs history for products', async ({
	apiHelpers,
	commerceAdminProductDetailsPage,
	commerceAdminProductPage,
	page,
	site,
	widgetPagePage,
}) => {
	const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
		groupId: site.id,
		title: getRandomString(),
	});

	await apiHelpers.headlessAdminUser.postAccount({
		name: getRandomString(),
		type: 'person',
	});

	await apiHelpers.headlessCommerceAdminChannel.postChannel({
		siteGroupId: site.id,
	});

	const catalog = await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

	const product = await apiHelpers.headlessCommerceAdminCatalog.postProduct({
		catalogId: catalog.id,
		name: {en_US: getRandomString()},
	});

	await commerceAdminProductPage.gotoProduct(product.name['en_US']);

	await (
		await commerceAdminProductDetailsPage.productDetailsInput(
			'Friendly URL'
		)
	).fill('product2');
	await commerceAdminProductDetailsPage.publishLink.click();

	await waitForAlert(page);

	await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

	await widgetPagePage.addPortlet('Product Details');

	await page.goto(`/web/${site.name}/p/${product.name['en_US']}`);

	await expect(page.getByText(product.name['en_US'])).toBeVisible();

	await page.goto(`/web/${site.name}/p/product2`);

	await expect(page.getByText(product.name['en_US'])).toBeVisible();
});

function verifyDateFormat(date: string) {
	const dateFormatPattern =
		/\w{3} \d{1,2}, \d{2,4},? \d{1,2}:\d{2}:\d{2}\s?[AP]M/;

	if (!dateFormatPattern.test(date)) {
		throw new Error(`Date format is incorrect: ${date}`);
	}
}

test('LPD-39067 Can product media and relation show correct date format', async ({
	apiHelpers,
	commerceAdminProductDetailsMediaPage,
	commerceAdminProductDetailsPage,
	commerceAdminProductDetailsProductRelationsPage,
	commerceAdminProductPage,
}) => {
	const site = await apiHelpers.headlessAdminSite.postSite({
		name: getRandomString(),
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

	const document2 = await apiHelpers.headlessDelivery.postDocument(
		site.id,
		createReadStream(path.join(__dirname, '/dependencies/liferay.png')),
		{
			description: getRandomString(),
			externalReferenceCode: getRandomString(),
			fileName: getRandomString(),
			title: getRandomString(),
			viewableBy: 'Owner',
		}
	);

	apiHelpers.data.push({id: document2.id, type: 'document'});

	const catalog = await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

	const simpleProduct =
		await apiHelpers.headlessCommerceAdminCatalog.postProduct({
			catalogId: catalog.id,
			name: {en_US: 'Simple product'},
		});

	await apiHelpers.headlessCommerceAdminCatalog.postAttachment(
		simpleProduct.productId,
		document1.id,
		document1.title
	);

	await apiHelpers.headlessCommerceAdminCatalog.postImage(
		simpleProduct.productId,
		document2.id,
		document2.title
	);

	const relationProduct =
		await apiHelpers.headlessCommerceAdminCatalog.postProduct({
			catalogId: catalog.id,
			name: {en_US: 'Relation product'},
		});

	await apiHelpers.headlessCommerceAdminCatalog.postProductRelatedProduct(
		simpleProduct.productId,
		{productId: relationProduct.productId, type: 'up-sell'}
	);

	await commerceAdminProductPage.gotoProduct(simpleProduct.name['en_US']);
	await commerceAdminProductDetailsPage.productMediaLink.click();

	await expect(
		commerceAdminProductDetailsMediaPage.addImageButton
	).toBeVisible();

	const imagesTableModifiedDate =
		await commerceAdminProductDetailsMediaPage.tableRowModifiedDateField(
			document2.title,
			commerceAdminProductDetailsMediaPage.mediaImagesTable
		);

	verifyDateFormat(await imagesTableModifiedDate.textContent());

	const attachmentsTableModifiedDate =
		await commerceAdminProductDetailsMediaPage.tableRowModifiedDateField(
			document1.title,
			commerceAdminProductDetailsMediaPage.mediaAttachmentsTable
		);

	verifyDateFormat(await attachmentsTableModifiedDate.textContent());

	await commerceAdminProductDetailsPage.productRelationsLink.click();

	await expect(
		commerceAdminProductDetailsProductRelationsPage.creationMenuNewButton
	).toBeVisible();

	const createDate =
		await commerceAdminProductDetailsProductRelationsPage.tableRowCreateDateField(
			relationProduct.name['en_US']
		);

	verifyDateFormat(await createDate.textContent());

	await commerceAdminProductDetailsProductRelationsPage.creationMenuNewButton.click();
	await commerceAdminProductDetailsProductRelationsPage.addUpSellProductMenuButton.click();

	const newProductRelationFrameTableModifiedDate =
		await commerceAdminProductDetailsProductRelationsPage.addNewProductFrameTableRowModifiedDateField(
			relationProduct.name['en_US']
		);

	verifyDateFormat(
		await newProductRelationFrameTableModifiedDate.textContent()
	);
});

test('LPD-52731 Product shows in catalog after updating Account Group Visibility Filter through Batch API', async ({
	apiHelpers,
	globalMenuPage,
	page,
}) => {
	const siteName = 'minium-' + getRandomInt();

	const {site} = await miniumSetUp(apiHelpers, siteName);

	const catalogs =
		await apiHelpers.headlessCommerceAdminCatalog.getCatalogsPage(siteName);

	const product = await apiHelpers.headlessCommerceAdminCatalog.postProduct({
		catalogId: catalogs.items[0].id,
		productAccountGroupFilter: true,
	});

	const accountGroup = await apiHelpers.headlessAdminUser.postAccountGroup({
		name: getRandomString(),
	});

	apiHelpers.data.push({id: accountGroup.id, type: 'accountGroup'});

	const account1 = await apiHelpers.headlessAdminUser.postAccount({
		name: 'Account1',
		type: 'business',
	});

	apiHelpers.data.push({id: account1.id, type: 'account'});

	await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
		account1.id,
		['test@liferay.com']
	);

	await apiHelpers.headlessAdminUser.assignAccountToAccountGroup(
		account1.externalReferenceCode,
		accountGroup.externalReferenceCode
	);

	await apiHelpers.headlessCommerceAdminCatalog.postProductBatch([
		{
			catalogId: catalogs.items[0].id,
			externalReferenceCode: product.externalReferenceCode,
			productAccountGroupFilter: true,
			productAccountGroups: [{accountGroupId: accountGroup.id, id: 0}],
		},
	]);

	await globalMenuPage.goToSite(site.name);

	await expect(page.getByText(product.name['en_US'])).toBeVisible();
});

test(
	'If AccountGroupFilter is enabled and the relationship is deleted, product should not show',
	{tag: '@LPD-65844'},
	async ({
		apiHelpers,
		commerceAdminChannelsPage,
		page,
		productPublisherPage,
		site,
		widgetPagePage,
	}) => {
		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: getRandomString(),
		});

		const channel =
			await apiHelpers.headlessCommerceAdminChannel.postChannel({
				siteGroupId: site.id,
			});

		await commerceAdminChannelsPage.changeCommerceChannelSiteType(
			channel.name,
			'B2B'
		);

		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
				name: getRandomString(),
			});

		const account = await apiHelpers.headlessAdminUser.postAccount({
			name: getRandomString(),
			type: 'business',
		});

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account.id,
			['test@liferay.com']
		);

		const accountGroup =
			await apiHelpers.headlessAdminUser.postAccountGroup({
				name: getRandomString(),
			});

		apiHelpers.data.push({id: accountGroup.id, type: 'accountGroup'});

		await apiHelpers.headlessAdminUser.assignAccountToAccountGroup(
			account.externalReferenceCode,
			accountGroup.externalReferenceCode
		);

		const product =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				name: {en_US: getRandomString()},
				productAccountGroupFilter: true,
				productAccountGroups: [
					{accountGroupId: accountGroup.id, id: 0},
				],
			});

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

		await widgetPagePage.addPortlet('Product Publisher');

		await expect(
			productPublisherPage.productCard(product.name.en_US)
		).toBeVisible();

		await page.goto(`/web/${site.name}`);

		await expect(
			productPublisherPage.productCard(product.name.en_US)
		).toBeVisible();

		const productAccountGroups =
			await apiHelpers.headlessCommerceAdminCatalog.getProductAccountGroups(
				product.productId
			);

		await apiHelpers.headlessCommerceAdminCatalog.deleteProductAccountGroup(
			productAccountGroups.items[0].id
		);

		await page.goto(`/web/${site.name}`);

		await expect(
			productPublisherPage.productCard(product.name.en_US)
		).not.toBeVisible();
	}
);

test(
	'The uploaded svg in product media is downloaded instead of being opened',
	{tag: '@LPD-70547'},
	async ({apiHelpers, attachmentsPage, commerceAdminProductPage, page}) => {
		let catalog;
		let product;
		let site;
		let siteDocument;
		let siteDocumentsPage;

		await test.step('Create channel, catalog and product via API', async () => {
			site =
				await apiHelpers.headlessAdminUser.getSiteByFriendlyUrlPath(
					'guest'
				);

			await apiHelpers.headlessCommerceAdminChannel.postChannel({});

			catalog =
				await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

			product = await apiHelpers.headlessCommerceAdminCatalog.postProduct(
				{
					catalogId: catalog.id,
				}
			);
		});

		await test.step('Upload the svg file to the product', async () => {
			await attachmentsPage.goToDocumentsAndMedia();
			await attachmentsPage.createFileEntry(
				path.join(__dirname, '/dependencies/diagram.svg'),
				'diagram'
			);

			siteDocumentsPage =
				await apiHelpers.headlessDelivery.getSiteDocumentsPage(
					site.id,
					'id:desc'
				);

			siteDocument = siteDocumentsPage.items[0];

			await apiHelpers.headlessCommerceAdminCatalog.postImage(
				product.productId,
				siteDocument.id,
				siteDocument.title
			);

			apiHelpers.data.push({id: siteDocument.id, type: 'document'});
		});

		await test.step('Navigate to product, get the link to src from the thumbnail, perform a get request and check the response', async () => {
			await commerceAdminProductPage.gotoProduct(product.name['en_US']);

			const svgURL = await page
				.locator('.d-none > .sticker-overlay .sticker-img')
				.getAttribute('src');

			const response = await page.request.get(
				`${liferayConfig.environment.baseUrl}${svgURL}`
			);

			expect(response.ok).toBeTruthy();

			const headers = response.headers();

			const contentDisposition = headers['content-disposition'];
			const contentType = headers['content-type'];

			expect(contentDisposition).toContain('attachment');
			expect(contentDisposition).toContain('diagram.svg');
			expect(contentType).toBe('image/svg+xml');
		});
	}
);

test(
	'Mapped product with unit of measure carries the SKU UOM into the cart',
	{tag: '@LPD-91224'},
	async ({
		apiHelpers,
		commerceAdminChannelsPage,
		page,
		productDetailsPage,
		site,
	}) => {
		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
				name: getRandomString(),
			});

		const product =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				name: {en_US: getRandomString()},
			});

		const skuUnitOfMeasure =
			await apiHelpers.headlessCommerceAdminCatalog.postSkuUnitOfMeasure(
				product.skus[0].id,
				{
					incrementalOrderQuantity: 1,
					name: {en_US: 'Box'},
					primary: true,
					priority: 1,
					rate: 1,
				}
			);

		const productDiagram =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				diagram: {
					attachmentBase64: {
						attachment:
							'iVBORw0KGgoAAAANSUhEUgAAAOAAAADgCAMAAAAt85rTAAAAeFBMVEX///8LY84AXM+frr4AYM0AWcwAXMwAUspRgta9zO2NquIAYc0AXs35+ffL0tqotsS5zOkRadH///sAV8u0xefv8/sueNOyye2HpN5vldjA0efu8fR5ntnn7PJjktiowuVfjNfR3ezX4OqOqtpIhdcob9CNseMYbtMc8dVDAAADbUlEQVR4nO2da3OqMBBAQwv4CGoFi4+qtbW2//8fFmE6t9mAo3cwpMw5H3e2jqcwCYlLVikAAAAAAAAAAAAAAAAAAAAAAAAAAACAP8hkOnt0xGw6ca73vAyyB2dkyfLZrd9TnoWBQ3SWP7n1S1zqnUlcGj679zsburtLl5l7vyDIlq78JonuQjAMXI2l004uYHEJp44EZw/dCD7MHAk+diX4iCCCCCKIIII+CuokNqnWUlpEq0c8K/naB7/uBHVwWI1/sxqW4dyMjvfzwkXP12Z0lV9p2J1gctiJlLe8uIbZJjWjL9u4SN6+mNF0c+WzbXeC8djKGRaCAyu6iIIgGlnhQW8ERwgi6AIEFYImCCLYLggqBE0QRLBdbMGVTElLwVSGy9XEwkr2XjAcvomvvDkVi9joVRju9kmxHtyLxWP6GvkuGASnocnpHNRfIppXybkIf3m/oi+uoaSM6tqolez/nowjELyfoM4GJlF528UieqkyQ0cy2bpzO9w2zDdmRvp6HjjiTzGKvn80Guq5HHI31m5il/OgnPGqedD6TX3ROCGEQ+sjVrE/grc8yTQJWsljBO8FggpBBBWCdwRBhSCCCsE7gqDqm2CyFoUTaldWWbyL6OQov/M/wVwWarysZeV7hwve+XZksNiXX/pjYYaPl/bP9mbyYjv3Z8Eb6CQyqf75oYherGmSH2Ens+mE4H8L6rj2Fq3HSq52oqy73KNbtBhkxAixv+D3dRQjUrXXJgaZkU+DTLKvnSbqiY9ys+09832aaJjo66n/fdDviR5BBBFEsE0QVAiaIIhguyCoEDTxX7C+CKFB0CrGmwx8L0JoKCOpJ/wQu4npZ3xeBvtcRtJUCNRgKJPLK+V1IZAjEHQueFM5ZX1FpteC1xbEVttntTW1fgveUtKsT/J92DdrpvFO8JaidHseVB7Ng02C1l82v1bg95MMgj8g+BsE7waCCAoQRLBdEERQ0DNBazXxZwVrjjwq1oNx7ZFHYS7eklW7gz9VFg1Yh1atLx1aNRTnXh0Cj7YNmwxvOXYsrE32W7BlEEQQQQQRRNAHwd4fJN77o+AngdNmGj/oxFljlL63Y+h/Q43et0Qpm9o4baoROm5qc25LlLhsSxS4bkuket9YCgAAAAAAAAAAAAAAAAAAAAAAAAAAAKAFvgGY6WrR7U77yAAAAABJRU5ErkJggg==',
						title: {en_US: 'title'},
					},
				},
				name: {en_US: 'diagram'},
				productType: 'diagram',
			});

		await apiHelpers.headlessCommerceAdminCatalog.postPin(
			productDiagram.productId,
			{
				mappedProduct: {
					productId: product.productId,
					quantity: 1,
					sequence: '1',
					sku: product.skus[0].sku,
					skuId: product.skus[0].id,
				},
				sequence: '1',
			}
		);

		const account = await apiHelpers.headlessAdminUser.postAccount({
			name: getRandomString(),
			type: 'business',
		});

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account.id,
			['test@liferay.com']
		);

		await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getFragmentDefinition({
					id: getRandomString(),
					key: 'COMMERCE_CART_FRAGMENTS-mini-cart',
				}),
				getFragmentDefinition({
					id: getRandomString(),
					key: 'COMMERCE_ACCOUNT_FRAGMENTS-account-selector',
				}),
				getWidgetDefinition({
					id: getRandomString(),
					widgetName:
						'com_liferay_commerce_product_content_web_internal_portlet_CPContentPortlet',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		const channel =
			await apiHelpers.headlessCommerceAdminChannel.postChannel({
				siteGroupId: site.id,
			});

		await commerceAdminChannelsPage.changeCommerceChannelSiteType(
			channel.name,
			'B2B'
		);

		const cart = await apiHelpers.headlessCommerceDeliveryCart.postCart(
			{accountId: account.id},
			channel.id
		);

		await page.goto(`/web/${site.name}/p/diagram`);

		await productDetailsPage.mappedProductCheckbox.setChecked(true);
		await productDetailsPage.mappedProductAddToCartButton.click();

		await waitForAlert(
			page,
			'Success:The product was successfully added to the cart.'
		);

		const cartItems =
			await apiHelpers.headlessCommerceDeliveryCart.getCartItems(cart.id);

		expect(cartItems.items).toHaveLength(1);
		expect(cartItems.items[0].skuId).toEqual(product.skus[0].id);
		expect(cartItems.items[0].skuUnitOfMeasure?.key).toEqual(
			skuUnitOfMeasure.key
		);
	}
);

test(
	'Diagram tooltip Add to Cart matches the existing cart row by SKU and UOM',
	{tag: '@LPD-91224'},
	async ({
		apiHelpers,
		commerceAdminChannelsPage,
		page,
		productDetailsPage,
		site,
	}) => {
		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
				name: getRandomString(),
			});

		const product =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				name: {en_US: getRandomString()},
			});

		const eachSkuUnitOfMeasure =
			await apiHelpers.headlessCommerceAdminCatalog.postSkuUnitOfMeasure(
				product.skus[0].id,
				{
					incrementalOrderQuantity: 1,
					name: {en_US: 'Each'},
					primary: true,
					priority: 1,
					rate: 1,
				}
			);

		const boxSkuUnitOfMeasure =
			await apiHelpers.headlessCommerceAdminCatalog.postSkuUnitOfMeasure(
				product.skus[0].id,
				{
					incrementalOrderQuantity: 1,
					name: {en_US: 'Box'},
					primary: false,
					priority: 2,
					rate: 1,
				}
			);

		const productDiagram =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				diagram: {
					attachmentBase64: {
						attachment:
							'iVBORw0KGgoAAAANSUhEUgAAAOAAAADgCAMAAAAt85rTAAAAeFBMVEX///8LY84AXM+frr4AYM0AWcwAXMwAUspRgta9zO2NquIAYc0AXs35+ffL0tqotsS5zOkRadH///sAV8u0xefv8/sueNOyye2HpN5vldjA0efu8fR5ntnn7PJjktiowuVfjNfR3ezX4OqOqtpIhdcob9CNseMYbtMc8dVDAAADbUlEQVR4nO2da3OqMBBAQwv4CGoFi4+qtbW2//8fFmE6t9mAo3cwpMw5H3e2jqcwCYlLVikAAAAAAAAAAAAAAAAAAAAAAAAAAACAP8hkOnt0xGw6ca73vAyyB2dkyfLZrd9TnoWBQ3SWP7n1S1zqnUlcGj679zsburtLl5l7vyDIlq78JonuQjAMXI2l004uYHEJp44EZw/dCD7MHAk+diX4iCCCCCKIIII+CuokNqnWUlpEq0c8K/naB7/uBHVwWI1/sxqW4dyMjvfzwkXP12Z0lV9p2J1gctiJlLe8uIbZJjWjL9u4SN6+mNF0c+WzbXeC8djKGRaCAyu6iIIgGlnhQW8ERwgi6AIEFYImCCLYLggqBE0QRLBdbMGVTElLwVSGy9XEwkr2XjAcvomvvDkVi9joVRju9kmxHtyLxWP6GvkuGASnocnpHNRfIppXybkIf3m/oi+uoaSM6tqolez/nowjELyfoM4GJlF528UieqkyQ0cy2bpzO9w2zDdmRvp6HjjiTzGKvn80Guq5HHI31m5il/OgnPGqedD6TX3ROCGEQ+sjVrE/grc8yTQJWsljBO8FggpBBBWCdwRBhSCCCsE7gqDqm2CyFoUTaldWWbyL6OQov/M/wVwWarysZeV7hwve+XZksNiXX/pjYYaPl/bP9mbyYjv3Z8Eb6CQyqf75oYherGmSH2Ens+mE4H8L6rj2Fq3HSq52oqy73KNbtBhkxAixv+D3dRQjUrXXJgaZkU+DTLKvnSbqiY9ys+09832aaJjo66n/fdDviR5BBBFEsE0QVAiaIIhguyCoEDTxX7C+CKFB0CrGmwx8L0JoKCOpJ/wQu4npZ3xeBvtcRtJUCNRgKJPLK+V1IZAjEHQueFM5ZX1FpteC1xbEVttntTW1fgveUtKsT/J92DdrpvFO8JaidHseVB7Ng02C1l82v1bg95MMgj8g+BsE7waCCAoQRLBdEERQ0DNBazXxZwVrjjwq1oNx7ZFHYS7eklW7gz9VFg1Yh1atLx1aNRTnXh0Cj7YNmwxvOXYsrE32W7BlEEQQQQQRRNAHwd4fJN77o+AngdNmGj/oxFljlL63Y+h/Q43et0Qpm9o4baoROm5qc25LlLhsSxS4bkuket9YCgAAAAAAAAAAAAAAAAAAAAAAAAAAAKAFvgGY6WrR7U77yAAAAABJRU5ErkJggg==',
						title: {en_US: 'title'},
					},
				},
				name: {en_US: 'diagram'},
				productType: 'diagram',
			});

		const pin = await apiHelpers.headlessCommerceAdminCatalog.postPin(
			productDiagram.productId,
			{
				mappedProduct: {
					productId: product.productId,
					quantity: 1,
					sequence: '1',
					sku: product.skus[0].sku,
					skuId: product.skus[0].id,
				},
				sequence: '1',
			}
		);

		const account = await apiHelpers.headlessAdminUser.postAccount({
			name: getRandomString(),
			type: 'business',
		});

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account.id,
			['test@liferay.com']
		);

		await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getFragmentDefinition({
					id: getRandomString(),
					key: 'COMMERCE_CART_FRAGMENTS-mini-cart',
				}),
				getFragmentDefinition({
					id: getRandomString(),
					key: 'COMMERCE_ACCOUNT_FRAGMENTS-account-selector',
				}),
				getWidgetDefinition({
					id: getRandomString(),
					widgetName:
						'com_liferay_commerce_product_content_web_internal_portlet_CPContentPortlet',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		const channel =
			await apiHelpers.headlessCommerceAdminChannel.postChannel({
				siteGroupId: site.id,
			});

		await commerceAdminChannelsPage.changeCommerceChannelSiteType(
			channel.name,
			'B2B'
		);

		const cart = await apiHelpers.headlessCommerceDeliveryCart.postCart(
			{
				accountId: account.id,
				cartItems: [
					{
						options: '[]',
						quantity: 1,
						skuId: product.skus[0].id,
						skuUnitOfMeasure: {key: boxSkuUnitOfMeasure.key},
					},
				],
			},
			channel.id
		);

		await page.goto(`/web/${site.name}/p/diagram`);

		await (await productDetailsPage.diagramPin(pin.sequence)).click();

		await expect(productDetailsPage.pinAddToCartButton).toBeVisible();
		await expect(productDetailsPage.pinAddToCartButton).not.toHaveClass(
			/is-added/
		);

		await productDetailsPage.pinAddToCartButton.click();

		await expect(productDetailsPage.pinAddToCartButton).toHaveClass(
			/is-added/
		);

		const cartItems =
			await apiHelpers.headlessCommerceDeliveryCart.getCartItems(cart.id);

		expect(cartItems.items).toHaveLength(2);

		const uomKeys = cartItems.items
			.map((item) => item.skuUnitOfMeasure?.key)
			.sort();

		expect(uomKeys).toEqual(
			[boxSkuUnitOfMeasure.key, eachSkuUnitOfMeasure.key].sort()
		);
	}
);

test(
	'A products limit rule counts a bundle product together with its bundled product',
	{tag: ['@COMMERCE-12889', '@LPD-106024']},
	async ({
		apiHelpers,
		commerceAdminChannelsPage,
		commerceMiniCartPage,
		page,
		productDetailsPage,
		site,
	}) => {
		test.setTimeout(120000);

		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
				name: getRandomString(),
			});

		for (const widgetName of [
			'com_liferay_commerce_checkout_web_internal_portlet_CommerceCheckoutPortlet',
			'com_liferay_commerce_order_content_web_internal_portlet_CommerceOpenOrderContentPortlet',
		]) {
			await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([
					getWidgetDefinition({id: getRandomString(), widgetName}),
				]),
				siteId: site.id,
				title: getRandomString(),
			});
		}

		await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getWidgetDefinition({
					id: getRandomString(),
					widgetName:
						'com_liferay_commerce_product_content_web_internal_portlet_CPContentPortlet',
				}),
				getFragmentDefinition({
					id: getRandomString(),
					key: 'COMMERCE_CART_FRAGMENTS-mini-cart',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		const channel =
			await apiHelpers.headlessCommerceAdminChannel.postChannel({
				siteGroupId: site.id,
			});

		await commerceAdminChannelsPage.changeCommerceChannelSiteType(
			channel.name,
			'B2B'
		);

		await waitForAlert(page);

		const {buyerUser} = await createAccountWithBuyerUser(
			apiHelpers,
			site.id
		);

		const bundleProductName = getRandomString();
		const linkedProductName = getRandomString();
		const optionName = 'Option1';

		let bundleProduct;
		let linkedProduct;

		await test.step('Create a bundle product that adds two units of a linked product', async () => {
			linkedProduct =
				await apiHelpers.headlessCommerceAdminCatalog.postProduct({
					catalogId: catalog.id,
					name: {en_US: linkedProductName},
					productConfiguration: {allowBackOrder: true},
					skus: [
						{
							cost: 0,
							price: 24,
							published: true,
							purchasable: true,
							sku: `SKU-${linkedProductName}`,
						},
					],
				});

			const optionKey = `option-${getRandomInt()}`;

			const option =
				await apiHelpers.headlessCommerceAdminCatalog.postOption(
					'select',
					optionKey,
					optionName,
					1
				);

			bundleProduct =
				await apiHelpers.headlessCommerceAdminCatalog.postProduct({
					catalogId: catalog.id,
					name: {en_US: bundleProductName},
					productConfiguration: {allowBackOrder: true},
					productOptions: [
						{
							fieldType: 'select',
							key: optionKey,
							name: {en_US: optionName},
							optionId: option.id,
							priceType: 'static',
							priority: 1,
							productOptionValues: [
								{
									deltaPrice: 0.0,
									key: 'value1',
									name: {en_US: 'Value1'},
									preselected: true,
									priority: 1,
									quantity: 2,
									skuId: linkedProduct.skus[0].id,
								},
							],
							required: true,
							skuContributor: false,
						},
					],
					skus: [
						{
							cost: 0,
							price: 50,
							published: true,
							purchasable: true,
							sku: `SKU-${bundleProductName}`,
						},
					],
				});
		});

		await test.step('Create an active order rule limiting both products to two', async () => {
			await apiHelpers.headlessCommerceAdminOrder.postOrderRule({
				type: 'products-limit',
				typeSettings:
					'products-limit-field-product-external-reference-codes=' +
					`${bundleProduct.externalReferenceCode},` +
					`${linkedProduct.externalReferenceCode}\n` +
					'products-limit-field-product-quantity=2\n',
			});
		});

		await test.step('Add the bundle to the cart as the buyer and submit the order', async () => {
			await performLogout(page);

			await performLoginViaApi({
				page,
				screenName: buyerUser.alternateName,
			});

			await page.goto(`/web/${site.name}/p/${bundleProductName}`);

			await expect(
				productDetailsPage.optionSelector(optionName)
			).toBeVisible();

			await productDetailsPage.addToCartButton.click();

			await commerceMiniCartPage.miniCartButton.click();

			await expect(
				commerceMiniCartPage.miniCartItem(bundleProductName)
			).toBeVisible();

			await commerceMiniCartPage.submitButton.click();
		});

		await test.step('Verify the order is rejected because the two products total three', async () => {
			await expect(
				page
					.getByRole('dialog')
					.getByText(
						'No more than 2 products in this product range can be purchased together.'
					)
			).toBeVisible();

			await expect(page.locator('.commerce-multi-step-nav')).toBeHidden();
		});
	}
);
