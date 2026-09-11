/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import {readFileSync} from 'fs';
import path from 'node:path';

import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {DataApiHelpers} from '../../../../helpers/ApiHelpers';
import {getRandomInt} from '../../../../utils/getRandomInt';
import getRandomString from '../../../../utils/getRandomString';
import {
	performLoginViaApi,
	performLogout,
} from '../../../../utils/performLogin';
import getFragmentDefinition from '../../../layout-content-page-editor-web/main/utils/getFragmentDefinition';
import getPageDefinition from '../../../layout-content-page-editor-web/main/utils/getPageDefinition';
import getWidgetDefinition from '../../../layout-content-page-editor-web/main/utils/getWidgetDefinition';
import {createAccountWithBuyerUser} from '../../utils/commerce';

export const test = mergeTests(
	commercePagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest()
);

function getDiagram() {
	return {
		attachmentBase64: {
			attachment: readFileSync(
				path.join(__dirname, '/dependencies/liferay.png')
			).toString('base64'),
			title: {en_US: getRandomString()},
		},
		radius: 1,
	};
}

async function setUpStorefrontSite(
	apiHelpers: DataApiHelpers,
	commerceAdminChannelsPage,
	site
) {
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

	return channel;
}

async function createAccount(apiHelpers: DataApiHelpers, emailAddress: string) {
	const account = await apiHelpers.headlessAdminUser.postAccount({
		name: getRandomString(),
		type: 'business',
	});

	await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
		account.id,
		[emailAddress]
	);

	return account;
}

test('COMMERCE-11835 Account Supplier role user can upload diagram file/image', async ({
	apiHelpers,
	commerceAdminProductDetailsDiagramPage,
	commerceAdminProductDetailsPage,
	commerceAdminProductPage,
	page,
}) => {
	await page.goto('/');

	const account = await apiHelpers.headlessAdminUser.postAccount({
		name: 'Supplier account',
		type: 'supplier',
	});

	const catalog = await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
		accountId: account.id,
	});

	await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
		account.id,
		['demo.unprivileged@liferay.com']
	);

	const product = await apiHelpers.headlessCommerceAdminCatalog.postProduct({
		catalogId: catalog.id,
		productType: 'diagram',
	});

	const rolesResponse = await apiHelpers.headlessAdminUser.getAccountRoles(
		account.id
	);

	const accountSupplierRole = rolesResponse?.items?.filter((role) => {
		return role.name === 'Account Supplier';
	});

	await apiHelpers.headlessAdminUser.assignAccountRoles(
		account.externalReferenceCode,
		accountSupplierRole[0].id,
		'demo.unprivileged@liferay.com'
	);

	await commerceAdminProductPage.gotoProduct(product.name['en_US']);
	await commerceAdminProductDetailsPage.goToProductDiagram();
	await commerceAdminProductDetailsDiagramPage.goToDragAndDropImages();

	await expect(
		commerceAdminProductDetailsDiagramPage.dragAndDropImages
	).toBeVisible();
});

test(
	'Diagram pins render on the product details page with their mapped product info',
	{tag: ['@COMMERCE-8207', '@COMMERCE-8068']},
	async ({
		apiHelpers,
		commerceAdminChannelsPage,
		page,
		productDetailsPage,
		site,
	}) => {
		await setUpStorefrontSite(apiHelpers, commerceAdminChannelsPage, site);
		await createAccount(apiHelpers, 'test@liferay.com');

		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
				name: getRandomString(),
			});

		const mappedProducts =
			await test.step('Create two purchasable products, each mapped to one pin', async () => {
				const mappedProducts = [];

				for (const positions of [
					{positionX: 25, positionY: 25},
					{positionX: 75, positionY: 60},
				]) {
					const product =
						await apiHelpers.headlessCommerceAdminCatalog.postProduct(
							{
								catalogId: catalog.id,
								name: {en_US: getRandomString()},
							}
						);

					mappedProducts.push({...positions, product});
				}

				return mappedProducts;
			});

		const diagramProduct =
			await test.step('Pin each product onto a diagram', async () => {
				const diagramProduct =
					await apiHelpers.headlessCommerceAdminCatalog.postProduct({
						catalogId: catalog.id,
						diagram: getDiagram(),
						name: {en_US: getRandomString()},
						productType: 'diagram',
					});

				for (const [
					index,
					{positionX, positionY, product},
				] of mappedProducts.entries()) {
					await apiHelpers.headlessCommerceAdminCatalog.postPin(
						diagramProduct.productId,
						{
							mappedProduct: {
								productId: product.productId,
								quantity: 1,
								sequence: String(index + 1),
								sku: product.skus[0].sku,
								skuId: product.skus[0].id,
								type: 'sku',
							},
							positionX,
							positionY,
							sequence: String(index + 1),
						}
					);
				}

				return diagramProduct;
			});

		await page.goto(`/web/${site.name}/p/${diagramProduct.name['en_US']}`);

		await test.step('Check every pin is rendered on the diagram', async () => {
			await expect(
				await productDetailsPage.diagramPin('1')
			).toBeVisible();
			await expect(
				await productDetailsPage.diagramPin('2')
			).toBeVisible();
		});

		await test.step('Check each pin tooltip carries the SKU, product name and quantity of its mapped product', async () => {
			for (const [index, {product}] of mappedProducts.entries()) {
				await (
					await productDetailsPage.diagramPin(String(index + 1))
				).click();

				await expect(
					productDetailsPage.diagramTooltipTitleLink(
						product.skus[0].sku
					)
				).toBeVisible();
				await expect(
					productDetailsPage.diagramTooltipSubtitleLink(
						product.name['en_US']
					)
				).toBeVisible();
				await expect(
					productDetailsPage.diagramTooltipQuantity
				).toBeVisible();
			}
		});
	}
);

test(
	'Every pin type carries its own mapped product information and links to the related diagram',
	{tag: '@COMMERCE-8147'},
	async ({
		apiHelpers,
		commerceAdminChannelsPage,
		page,
		productDetailsPage,
		site,
	}) => {
		await setUpStorefrontSite(apiHelpers, commerceAdminChannelsPage, site);

		const {buyerUser} = await createAccountWithBuyerUser(
			apiHelpers,
			site.id
		);

		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
				name: getRandomString(),
			});

		const skuProduct =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				name: {en_US: getRandomString()},
			});

		const relatedDiagramProduct =
			await test.step('Create a second diagram that is both a pin target and a navigation destination', async () => {
				const relatedDiagramProduct =
					await apiHelpers.headlessCommerceAdminCatalog.postProduct({
						catalogId: catalog.id,
						diagram: getDiagram(),
						name: {en_US: getRandomString()},
						productType: 'diagram',
					});

				for (const sequence of ['1', '2']) {
					const product =
						await apiHelpers.headlessCommerceAdminCatalog.postProduct(
							{
								catalogId: catalog.id,
								name: {en_US: getRandomString()},
							}
						);

					await apiHelpers.headlessCommerceAdminCatalog.postPin(
						relatedDiagramProduct.productId,
						{
							mappedProduct: {
								productId: product.productId,
								quantity: 1,
								sequence,
								sku: product.skus[0].sku,
								skuId: product.skus[0].id,
								type: 'sku',
							},
							positionX: 30,
							positionY: Number(sequence) * 20,
							sequence,
						}
					);
				}

				return relatedDiagramProduct;
			});

		const externalLabel = getRandomString();

		const diagramProduct =
			await test.step('Create a diagram with one pin of each supported mapped product type', async () => {
				const diagramProduct =
					await apiHelpers.headlessCommerceAdminCatalog.postProduct({
						catalogId: catalog.id,
						diagram: getDiagram(),
						name: {en_US: getRandomString()},
						productType: 'diagram',
					});

				await apiHelpers.headlessCommerceAdminCatalog.postPin(
					diagramProduct.productId,
					{
						mappedProduct: {
							productId: skuProduct.productId,
							quantity: 1,
							sequence: '1',
							sku: skuProduct.skus[0].sku,
							skuId: skuProduct.skus[0].id,
							type: 'sku',
						},
						positionX: 20,
						positionY: 20,
						sequence: '1',
					}
				);

				await apiHelpers.headlessCommerceAdminCatalog.postPin(
					diagramProduct.productId,
					{
						mappedProduct: {
							productId: relatedDiagramProduct.productId,
							sequence: '2',
							type: 'diagram',
						},
						positionX: 60,
						positionY: 40,
						sequence: '2',
					}
				);

				await apiHelpers.headlessCommerceAdminCatalog.postPin(
					diagramProduct.productId,
					{
						mappedProduct: {
							quantity: 1,
							sequence: '3',
							sku: externalLabel,
							type: 'external',
						},
						positionX: 40,
						positionY: 70,
						sequence: '3',
					}
				);

				return diagramProduct;
			});

		await test.step('Open the product details page as a buyer', async () => {
			await performLogout(page);
			await performLoginViaApi({
				page,
				screenName: buyerUser.alternateName,
			});

			await page.goto(
				`/web/${site.name}/p/${diagramProduct.name['en_US']}`
			);
		});

		await test.step('Check a pin linked to a SKU exposes the SKU, the product name and the quantity', async () => {
			await (await productDetailsPage.diagramPin('1')).click();

			await expect(
				productDetailsPage.diagramTooltipTitleLink(
					skuProduct.skus[0].sku
				)
			).toBeVisible();
			await expect(
				productDetailsPage.diagramTooltipSubtitleLink(
					skuProduct.name['en_US']
				)
			).toBeVisible();
			await expect(
				productDetailsPage.diagramTooltipQuantity
			).toBeVisible();
			await expect(
				productDetailsPage.mappedProductRowAt(0)
			).toContainText(skuProduct.skus[0].sku);
			await expect(
				productDetailsPage.mappedProductRowAt(0)
			).toContainText(skuProduct.name['en_US']);
		});

		await test.step('Check a pin that is not linked to the catalog only carries its label and quantity', async () => {
			await (await productDetailsPage.diagramPin('3')).click();

			await expect(
				productDetailsPage.diagramTooltipExternalName(externalLabel)
			).toBeVisible();
			await expect(
				productDetailsPage.diagramTooltipQuantity
			).toBeVisible();
			await expect(
				productDetailsPage.mappedProductRowAt(2)
			).toContainText(externalLabel);
		});

		await test.step('Check a pin linked to another diagram exposes that diagram name and a link through to it', async () => {
			await (await productDetailsPage.diagramPin('2')).click();

			await expect(
				productDetailsPage.diagramTooltipTitleLink(
					relatedDiagramProduct.name['en_US']
				)
			).toBeVisible();
			await expect(
				productDetailsPage.mappedProductRowAt(1)
			).toContainText(relatedDiagramProduct.name['en_US']);

			await productDetailsPage.diagramTooltipViewLink.click();
		});

		await test.step('Check the related diagram renders its own product details page, pins included', async () => {
			await expect(
				productDetailsPage.productTitle(
					relatedDiagramProduct.name['en_US']
				)
			).toBeVisible();
			await expect(
				await productDetailsPage.diagramPin('1')
			).toBeVisible();
			await expect(
				await productDetailsPage.diagramPin('2')
			).toBeVisible();
		});
	}
);

test(
	'Only purchasable mapped products can be selected for the cart',
	{tag: '@COMMERCE-8146'},
	async ({
		apiHelpers,
		commerceAdminChannelsPage,
		page,
		productDetailsPage,
		site,
	}) => {
		const channel = await setUpStorefrontSite(
			apiHelpers,
			commerceAdminChannelsPage,
			site
		);

		const {buyerUser} = await createAccountWithBuyerUser(
			apiHelpers,
			site.id
		);

		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
				name: getRandomString(),
			});

		const products =
			await test.step('Create three products that cannot be back ordered', async () => {
				const products = [];

				for (let i = 0; i < 3; i++) {
					products.push(
						await apiHelpers.headlessCommerceAdminCatalog.postProduct(
							{
								catalogId: catalog.id,
								name: {en_US: getRandomString()},
								productConfiguration: {
									allowBackOrder: false,
									displayAvailability: true,
									displayStockQuantity: true,
								},
							}
						)
					);
				}

				return products;
			});

		await test.step('Stock every product except the first one', async () => {
			const warehouse =
				await apiHelpers.headlessCommerceAdminInventoryApiHelper.postWarehouses(
					{
						active: true,
						latitude: getRandomInt(),
						longitude: getRandomInt(),
						warehouseItems: products.slice(1).map((product) => ({
							quantity: 10,
							sku: product.skus[0].sku,
						})),
					}
				);

			await apiHelpers.headlessCommerceAdminInventoryApiHelper.postWarehousesChannels(
				warehouse.id,
				channel.id
			);
		});

		const diagramProduct =
			await test.step('Pin each product onto a diagram', async () => {
				const diagramProduct =
					await apiHelpers.headlessCommerceAdminCatalog.postProduct({
						catalogId: catalog.id,
						diagram: getDiagram(),
						name: {en_US: getRandomString()},
						productType: 'diagram',
					});

				for (const [index, product] of products.entries()) {
					await apiHelpers.headlessCommerceAdminCatalog.postPin(
						diagramProduct.productId,
						{
							mappedProduct: {
								productId: product.productId,
								quantity: 1,
								sequence: String(index + 1),
								sku: product.skus[0].sku,
								skuId: product.skus[0].id,
								type: 'sku',
							},
							positionX: 20 + index * 20,
							positionY: 20 + index * 20,
							sequence: String(index + 1),
						}
					);
				}

				return diagramProduct;
			});

		await test.step('Open the product details page as a buyer', async () => {
			await performLogout(page);
			await performLoginViaApi({
				page,
				screenName: buyerUser.alternateName,
			});

			await page.goto(
				`/web/${site.name}/p/${diagramProduct.name['en_US']}`
			);
		});

		const [unstockedProduct, ...stockedProducts] = products;

		const unstockedCheckbox = productDetailsPage.mappedProductCheckboxFor(
			unstockedProduct.skus[0].sku,
			unstockedProduct.name['en_US']
		);

		await expect(unstockedCheckbox).toBeDisabled();

		await test.step('Check selecting everything only picks up the mapped products in stock', async () => {
			await productDetailsPage.mappedProductSelectAllCheckbox.check();

			for (const product of stockedProducts) {
				await expect(
					productDetailsPage.mappedProductCheckboxFor(
						product.skus[0].sku,
						product.name['en_US']
					)
				).toBeChecked();
			}

			await expect(unstockedCheckbox).not.toBeChecked();
		});
	}
);

test('Pins created through the diagram editor are saved with all their information', async ({
	apiHelpers,
	commerceAdminProductDetailsDiagramPage,
	commerceAdminProductDetailsPage,
	commerceAdminProductPage,
}) => {
	const catalog = await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
		name: getRandomString(),
	});

	const skuProduct =
		await apiHelpers.headlessCommerceAdminCatalog.postProduct({
			catalogId: catalog.id,
			name: {en_US: getRandomString()},
		});

	const relatedDiagramProduct =
		await apiHelpers.headlessCommerceAdminCatalog.postProduct({
			catalogId: catalog.id,
			diagram: getDiagram(),
			name: {en_US: getRandomString()},
			productType: 'diagram',
		});

	const diagramProduct =
		await apiHelpers.headlessCommerceAdminCatalog.postProduct({
			catalogId: catalog.id,
			diagram: getDiagram(),
			name: {en_US: getRandomString()},
			productType: 'diagram',
		});

	const externalLabel = getRandomString();

	await commerceAdminProductPage.gotoProduct(diagramProduct.name['en_US']);
	await commerceAdminProductDetailsPage.goToProductDiagram();

	await test.step('Add one pin of each supported type through the pin editor', async () => {
		await commerceAdminProductDetailsDiagramPage.addPin({
			label: externalLabel,
			position: {x: 40, y: 40},
			sequence: '1',
			type: 'external',
		});

		await commerceAdminProductDetailsDiagramPage.addPin({
			position: {x: 120, y: 80},
			sequence: '2',
			sku: skuProduct.skus[0].sku,
			type: 'sku',
		});

		await commerceAdminProductDetailsDiagramPage.addPin({
			diagram: relatedDiagramProduct.name['en_US'],
			position: {x: 80, y: 120},
			sequence: '3',
			type: 'diagram',
		});
	});

	await test.step('Check the mapped products table saved every pin against its position', async () => {
		for (const [index, value] of [
			externalLabel,
			skuProduct.skus[0].sku,
			relatedDiagramProduct.name['en_US'],
		].entries()) {
			await expect(
				commerceAdminProductDetailsDiagramPage.mappedProductRowAt(index)
			).toContainText(value);
		}
	});
});
