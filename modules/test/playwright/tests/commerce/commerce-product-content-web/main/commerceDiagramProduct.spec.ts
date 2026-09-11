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
import {createAccountWithBuyerUser, miniumSetUp} from '../../utils/commerce';

export const test = mergeTests(
	commercePagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest()
);

let miniumCatalog: {id: number; name: string};
let miniumSite: {id: number; name: string};
let setupData: Array<{id: number | string; type: string}>;

test.beforeAll(async ({browser}) => {
	const page = await browser.newPage();

	await performLoginViaApi({page, screenName: 'test'});

	const apiHelpers = new DataApiHelpers(page);

	const miniumResult = await miniumSetUp(apiHelpers);

	miniumSite = {
		id: Number(miniumResult.site.id),
		name: miniumResult.site.name,
	};

	const catalogs =
		await apiHelpers.headlessCommerceAdminCatalog.getCatalogsPage('Minium');

	miniumCatalog = catalogs.items[0];

	setupData = [...apiHelpers.data];

	await page.close();
});

test.afterAll(async ({browser}) => {
	const page = await browser.newPage();

	await performLoginViaApi({page, screenName: 'test'});

	const apiHelpers = new DataApiHelpers(page);

	apiHelpers.setData(setupData);

	await apiHelpers.clearData();

	await page.close();
});

function getDiagram(title: string = getRandomString()) {
	return {
		attachmentBase64: {
			attachment: readFileSync(
				path.join(__dirname, '/dependencies/liferay.png')
			).toString('base64'),
			title: {en_US: title},
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

		await test.step('Check the tooltip product name links through to that product', async () => {
			const [{product}] = mappedProducts;

			await (await productDetailsPage.diagramPin('1')).click();

			await productDetailsPage
				.diagramTooltipSubtitleLink(product.name['en_US'])
				.click();

			await expect(
				await productDetailsPage.nameField(product.name['en_US'])
			).toBeVisible();
			await expect(
				await productDetailsPage.skuField(product.skus[0].sku)
			).toBeVisible();
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

test('Pins can be edited and removed through the diagram editor', async ({
	apiHelpers,
	commerceAdminProductDetailsDiagramPage,
	commerceAdminProductDetailsPage,
	commerceAdminProductPage,
}) => {
	const catalog = await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
		name: getRandomString(),
	});

	const diagramProduct =
		await apiHelpers.headlessCommerceAdminCatalog.postProduct({
			catalogId: catalog.id,
			diagram: getDiagram(),
			name: {en_US: getRandomString()},
			productType: 'diagram',
		});

	const label = getRandomString();
	const updatedLabel = getRandomString();

	await commerceAdminProductPage.gotoProduct(diagramProduct.name['en_US']);
	await commerceAdminProductDetailsPage.goToProductDiagram();

	await test.step('Add a pin and check it is drawn on the diagram', async () => {
		await commerceAdminProductDetailsDiagramPage.addPin({
			label,
			position: {x: 60, y: 60},
			sequence: '1',
			type: 'external',
		});

		await expect(
			commerceAdminProductDetailsDiagramPage.pin('1')
		).toBeVisible();
		await expect(
			commerceAdminProductDetailsDiagramPage.mappedProductRowAt(0)
		).toContainText(label);
	});

	await test.step('Edit the pin and check the table reflects it', async () => {
		await commerceAdminProductDetailsDiagramPage.editPin('1', {
			label: updatedLabel,
			quantity: 4,
		});

		await expect(
			commerceAdminProductDetailsDiagramPage.mappedProductRowAt(0)
		).toContainText(updatedLabel);
		await expect(
			commerceAdminProductDetailsDiagramPage.mappedProductRow(label)
		).toHaveCount(0);
	});

	await test.step('Delete the pin and check it is gone', async () => {
		await commerceAdminProductDetailsDiagramPage.deletePin('1');

		await expect(
			commerceAdminProductDetailsDiagramPage.pin('1')
		).toHaveCount(0);
		await expect(
			commerceAdminProductDetailsDiagramPage.mappedProductRow(
				updatedLabel
			)
		).toHaveCount(0);
	});
});

test('The diagram pin size and full screen controls change the canvas', async ({
	apiHelpers,
	commerceAdminProductDetailsDiagramPage,
	commerceAdminProductDetailsPage,
	commerceAdminProductPage,
}) => {
	const catalog = await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
		name: getRandomString(),
	});

	const diagramProduct =
		await apiHelpers.headlessCommerceAdminCatalog.postProduct({
			catalogId: catalog.id,
			diagram: getDiagram(),
			name: {en_US: getRandomString()},
			productType: 'diagram',
		});

	await commerceAdminProductPage.gotoProduct(diagramProduct.name['en_US']);
	await commerceAdminProductDetailsPage.goToProductDiagram();

	await commerceAdminProductDetailsDiagramPage.addPin({
		label: getRandomString(),
		position: {x: 60, y: 60},
		sequence: '1',
		type: 'external',
	});

	await test.step('Changing the pin size rescales the pin marker', async () => {
		const marker =
			commerceAdminProductDetailsDiagramPage.pinRadiusHandler('1');

		await commerceAdminProductDetailsDiagramPage.setPinSize('Large');

		await expect(marker).toHaveAttribute('transform', 'scale(2)');

		await commerceAdminProductDetailsDiagramPage.setPinSize('Small');

		await expect(marker).toHaveAttribute('transform', 'scale(0.5)');
	});

	await test.step('Expanding fills the page and compressing restores it', async () => {
		await commerceAdminProductDetailsDiagramPage.toggleExpanded();

		await expect(
			commerceAdminProductDetailsDiagramPage.diagram
		).toHaveClass(/expanded/);

		await commerceAdminProductDetailsDiagramPage.toggleExpanded();

		await expect(
			commerceAdminProductDetailsDiagramPage.diagram
		).not.toHaveClass(/expanded/);
	});
});

test('A diagram product cannot be published without an image', async ({
	apiHelpers,
	commerceAdminProductDetailsDiagramPage,
	commerceAdminProductDetailsPage,
	commerceAdminProductPage,
}) => {
	const catalog = await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
		name: getRandomString(),
	});

	const diagramProduct =
		await apiHelpers.headlessCommerceAdminCatalog.postProduct({
			catalogId: catalog.id,
			name: {en_US: getRandomString()},
			productType: 'diagram',
		});

	await commerceAdminProductPage.gotoProduct(diagramProduct.name['en_US']);
	await commerceAdminProductDetailsPage.goToProductDiagram();

	await expect(
		commerceAdminProductDetailsDiagramPage.diagramTypeSelect
	).toBeVisible();

	await expect(async () => {
		await commerceAdminProductDetailsPage.publishLink.click();

		await expect(
			commerceAdminProductDetailsDiagramPage.missingFileError
		).toBeVisible({timeout: 5000});
	}).toPass({timeout: 30000});
});

test('The diagram image can be uploaded and replaced', async ({
	apiHelpers,
	commerceAdminProductDetailsDiagramPage,
	commerceAdminProductDetailsPage,
	commerceAdminProductPage,
}) => {
	const catalog = await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
		name: getRandomString(),
	});

	const diagramProduct =
		await apiHelpers.headlessCommerceAdminCatalog.postProduct({
			catalogId: catalog.id,
			name: {en_US: getRandomString()},
			productType: 'diagram',
		});

	await commerceAdminProductPage.gotoProduct(diagramProduct.name['en_US']);
	await commerceAdminProductDetailsPage.goToProductDiagram();

	await test.step('Upload an image and check it reaches the canvas', async () => {
		await commerceAdminProductDetailsDiagramPage.uploadDiagramImage(
			path.join(__dirname, '/dependencies/liferay.png')
		);

		await expect(
			commerceAdminProductDetailsDiagramPage.diagramFile('liferay')
		).toBeVisible();
		await expect(
			commerceAdminProductDetailsDiagramPage.diagramImageNamed('liferay')
		).toBeAttached();
	});

	await test.step('Remove the image and check it is gone', async () => {
		await commerceAdminProductDetailsDiagramPage.removeDiagramImage();

		await expect(
			commerceAdminProductDetailsDiagramPage.diagramFile('liferay')
		).toHaveCount(0);
	});

	await test.step('Upload a different image and check it replaced the first', async () => {
		await commerceAdminProductDetailsDiagramPage.uploadDiagramImage(
			path.join(__dirname, '/dependencies/diagram-replacement.png')
		);

		await expect(
			commerceAdminProductDetailsDiagramPage.diagramFile(
				'diagram-replacement'
			)
		).toBeVisible();
		await expect(
			commerceAdminProductDetailsDiagramPage.diagramImageNamed(
				'diagram-replacement'
			)
		).toBeAttached();
		await expect(
			commerceAdminProductDetailsDiagramPage.diagramFile('liferay')
		).toHaveCount(0);
	});
});

test(
	'The diagram name and short description render on the product details page',
	{tag: '@COMMERCE-8565'},
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

		const shortDescription = getRandomString();

		const diagramProduct =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				diagram: getDiagram(),
				name: {en_US: getRandomString()},
				productType: 'diagram',
				shortDescription: {en_US: shortDescription},
			});

		await page.goto(`/web/${site.name}/p/${diagramProduct.name['en_US']}`);

		await expect(
			productDetailsPage.productTitle(diagramProduct.name['en_US'])
		).toBeVisible();
		await expect(
			await productDetailsPage.shortDescriptionField(shortDescription)
		).toBeVisible();
	}
);

test(
	'A mapped product reached from a pin adds the chosen quantity to the cart',
	{tag: '@COMMERCE-8209'},
	async ({apiHelpers, commerceMiniCartPage, page, productDetailsPage}) => {
		await createAccount(apiHelpers, 'test@liferay.com');

		const catalog = miniumCatalog;
		const site = miniumSite;

		const mappedProduct =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				name: {en_US: getRandomString()},
			});

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
					productId: mappedProduct.productId,
					quantity: 1,
					sequence: '1',
					sku: mappedProduct.skus[0].sku,
					skuId: mappedProduct.skus[0].id,
					type: 'sku',
				},
				positionX: 40,
				positionY: 40,
				sequence: '1',
			}
		);

		await page.goto(`/web/${site.name}/p/${diagramProduct.name['en_US']}`);

		await test.step('Follow the pin through to the mapped product', async () => {
			await (await productDetailsPage.diagramPin('1')).click();

			await productDetailsPage
				.diagramTooltipSubtitleLink(mappedProduct.name['en_US'])
				.click();

			await expect(
				await productDetailsPage.nameField(mappedProduct.name['en_US'])
			).toBeVisible();
		});

		await test.step('Add two units to the cart', async () => {
			await productDetailsPage.productDetailQuantitySelector.fill('2');

			await productDetailsPage.productDetailAddToCartButton.click();

			await expect(
				productDetailsPage.productDetailAddToCartButton
			).toHaveClass(/is-added/);
		});

		await test.step('Check the mini cart holds that SKU at that quantity', async () => {
			await commerceMiniCartPage.miniCartButton.click();

			await expect(
				commerceMiniCartPage.miniCartItem(mappedProduct.name['en_US'])
			).toBeVisible();
			await expect(
				commerceMiniCartPage.miniCartSku(mappedProduct.skus[0].sku)
			).toBeVisible();

			await expect(
				commerceMiniCartPage
					.miniCartItem(mappedProduct.name['en_US'])
					.getByRole('spinbutton')
			).toHaveValue('2');
		});
	}
);

test(
	'The breadcrumb on a diagram details page links back to the catalog',
	{tag: '@COMMERCE-8265'},
	async ({apiHelpers, page, productDetailsPage}) => {
		const mappedProduct =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: miniumCatalog.id,
				name: {en_US: getRandomString()},
			});

		const diagramProduct =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: miniumCatalog.id,
				diagram: getDiagram(),
				name: {en_US: getRandomString()},
				productType: 'diagram',
			});

		await apiHelpers.headlessCommerceAdminCatalog.postPin(
			diagramProduct.productId,
			{
				mappedProduct: {
					productId: mappedProduct.productId,
					quantity: 1,
					sequence: '1',
					sku: mappedProduct.skus[0].sku,
					skuId: mappedProduct.skus[0].id,
					type: 'sku',
				},
				positionX: 40,
				positionY: 40,
				sequence: '1',
			}
		);

		await page.goto(
			`/web/${miniumSite.name}/p/${diagramProduct.name['en_US']}`
		);

		await expect(productDetailsPage.breadcrumb).toBeVisible();

		await productDetailsPage.breadcrumbLink('Catalog').click();

		await expect(page).toHaveURL(/\/catalog/);
	}
);

test(
	'A diagram product appears in the catalog search results with its own thumbnail',
	{tag: '@COMMERCE-8129'},
	async ({apiHelpers, commerceThemeMiniumCatalogPage, page}) => {
		const diagramProduct =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: miniumCatalog.id,
				diagram: getDiagram(),
				name: {en_US: `Diagram${getRandomInt()}`},
				productType: 'diagram',
			});

		await page.goto(`/web/${miniumSite.name}/catalog`);

		const card = commerceThemeMiniumCatalogPage.productCard(
			diagramProduct.name['en_US']
		);

		await expect(async () => {
			await commerceThemeMiniumCatalogPage.catalogSearch.fill(
				diagramProduct.name['en_US']
			);
			await commerceThemeMiniumCatalogPage.catalogSearch.press('Enter');

			await expect(card).toBeVisible({timeout: 5000});
		}).toPass({timeout: 30000});

		await expect(card.locator('img')).toHaveAttribute(
			'src',
			/\/documents\//
		);
		await expect(card.locator('img')).not.toHaveAttribute(
			'src',
			/commerce-media\/default/
		);
	}
);

test(
	'A diagram product card shows its name and truncates a long description',
	{tag: '@COMMERCE-8971'},
	async ({apiHelpers, commerceThemeMiniumCatalogPage, page}) => {
		const shortDescription = 'This is a short description.';
		const longDescription = `${'This description is far too long to fit on a product card. '.repeat(
			8
		)}`;

		const products = [];

		for (const description of [shortDescription, longDescription]) {
			products.push(
				await apiHelpers.headlessCommerceAdminCatalog.postProduct({
					catalogId: miniumCatalog.id,
					diagram: getDiagram(),
					name: {en_US: `Diagram${getRandomInt()}`},
					productType: 'diagram',
					shortDescription: {en_US: description},
				})
			);
		}

		const [shortProduct, longProduct] = products;

		await page.goto(`/web/${miniumSite.name}/catalog`);

		await test.step('The short description shows in full', async () => {
			const card = commerceThemeMiniumCatalogPage.productCard(
				shortProduct.name['en_US']
			);

			await expect(async () => {
				await commerceThemeMiniumCatalogPage.catalogSearch.fill(
					shortProduct.name['en_US']
				);
				await commerceThemeMiniumCatalogPage.catalogSearch.press(
					'Enter'
				);

				await expect(card).toBeVisible({timeout: 5000});
			}).toPass({timeout: 30000});

			await expect(card).toContainText(shortDescription);

			expect(
				await card
					.getByText(shortDescription)
					.evaluate(
						(element) => element.scrollHeight > element.clientHeight
					)
			).toBe(false);
		});

		await test.step('The long description is truncated on the card', async () => {
			const card = commerceThemeMiniumCatalogPage.productCard(
				longProduct.name['en_US']
			);

			await expect(async () => {
				await commerceThemeMiniumCatalogPage.catalogSearch.fill(
					longProduct.name['en_US']
				);
				await commerceThemeMiniumCatalogPage.catalogSearch.press(
					'Enter'
				);

				await expect(card).toBeVisible({timeout: 5000});
			}).toPass({timeout: 30000});

			expect(
				await card
					.getByText(longDescription.trim())
					.evaluate(
						(element) => element.scrollHeight > element.clientHeight
					)
			).toBe(true);
		});
	}
);

test(
	'The related diagrams widget only lists diagrams the buyer can see',
	{tag: '@COMMERCE-8358'},
	async ({apiHelpers, page, productDetailsPage}) => {
		const mappedProduct =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: miniumCatalog.id,
				name: {en_US: `Mapped${getRandomInt()}`},
			});

		const diagramProduct =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: miniumCatalog.id,
				diagram: getDiagram(),
				name: {en_US: `Diagram${getRandomInt()}`},
				productType: 'diagram',
			});

		await apiHelpers.headlessCommerceAdminCatalog.postPin(
			diagramProduct.productId,
			{
				mappedProduct: {
					productId: mappedProduct.productId,
					quantity: 1,
					sequence: '1',
					sku: mappedProduct.skus[0].sku,
					skuId: mappedProduct.skus[0].id,
					type: 'sku',
				},
				positionX: 40,
				positionY: 40,
				sequence: '1',
			}
		);

		const relatedDiagram = productDetailsPage.relatedDiagramLink(
			diagramProduct.name['en_US']
		);

		const relatedDiagramLink = relatedDiagram.first();

		await test.step('The mapped product lists the diagram it belongs to', async () => {
			await page.goto(
				`/web/${miniumSite.name}/p/${mappedProduct.name['en_US']}`
			);

			await expect(relatedDiagramLink).toBeVisible();

			await relatedDiagramLink.click();

			await expect(
				productDetailsPage.productTitle(diagramProduct.name['en_US'])
			).toBeVisible();
		});

		await test.step('A diagram restricted to another account group is hidden', async () => {
			const accountGroup =
				await apiHelpers.headlessAdminUser.postAccountGroup({
					name: `Group${getRandomInt()}`,
				});

			await apiHelpers.headlessCommerceAdminCatalog.patchProduct(
				String(diagramProduct.productId),
				{
					productAccountGroupFilter: true,
					productAccountGroups: [{accountGroupId: accountGroup.id}],
				}
			);

			await page.goto(
				`/web/${miniumSite.name}/p/${mappedProduct.name['en_US']}`
			);

			await expect(relatedDiagram).toHaveCount(0);
		});
	}
);

test(
	'A discontinued mapped product offers its replacement from the pin and the table',
	{tag: '@COMMERCE-8151'},
	async ({apiHelpers, commerceMiniCartPage, page, productDetailsPage}) => {
		await createAccount(apiHelpers, 'test@liferay.com');

		const discontinuedProduct =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: miniumCatalog.id,
				name: {en_US: `Discontinued${getRandomInt()}`},
				productConfiguration: {
					allowBackOrder: false,
					displayAvailability: true,
					displayStockQuantity: true,
				},
			});

		const replacementProduct =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: miniumCatalog.id,
				name: {en_US: `Replacement${getRandomInt()}`},
			});

		await apiHelpers.headlessCommerceAdminCatalog.patchSku(
			String(discontinuedProduct.skus[0].id),
			{
				discontinued: true,
				replacementSkuId: replacementProduct.skus[0].id,
				sku: discontinuedProduct.skus[0].sku,
			}
		);

		const diagramProduct =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: miniumCatalog.id,
				diagram: getDiagram(),
				name: {en_US: `Diagram${getRandomInt()}`},
				productType: 'diagram',
			});

		await apiHelpers.headlessCommerceAdminCatalog.postPin(
			diagramProduct.productId,
			{
				mappedProduct: {
					productId: discontinuedProduct.productId,
					quantity: 1,
					sequence: '1',
					sku: discontinuedProduct.skus[0].sku,
					skuId: discontinuedProduct.skus[0].id,
					type: 'sku',
				},
				positionX: 40,
				positionY: 40,
				sequence: '1',
			}
		);

		const replacementNotice = `${discontinuedProduct.skus[0].sku} has been replaced by ${replacementProduct.skus[0].sku}`;

		await page.goto(
			`/web/${miniumSite.name}/p/${diagramProduct.name['en_US']}`
		);

		await test.step('The pin tooltip announces the replacement', async () => {
			await (await productDetailsPage.diagramPin('1')).click();

			await expect(
				productDetailsPage.diagramTooltipReplacementAlert(
					replacementNotice
				)
			).toBeVisible();
			await expect(
				productDetailsPage.diagramTooltipTitleLink(
					replacementProduct.skus[0].sku
				)
			).toBeVisible();
		});

		await test.step('Adding from the pin puts the replacement in the cart', async () => {
			await productDetailsPage.pinAddToCartButton.click();

			await expect(productDetailsPage.pinAddToCartButton).toHaveClass(
				/is-added/
			);

			await commerceMiniCartPage.miniCartButton.click();

			await expect(
				commerceMiniCartPage.miniCartSku(replacementProduct.skus[0].sku)
			).toBeVisible();
			await expect(
				commerceMiniCartPage.miniCartSku(
					discontinuedProduct.skus[0].sku
				)
			).toHaveCount(0);

			await commerceMiniCartPage.miniCartButtonClose.click();
		});

		await test.step('Adding from the mapped products table substitutes it too', async () => {
			await productDetailsPage
				.mappedProductCheckboxFor(
					replacementProduct.skus[0].sku,
					replacementProduct.name['en_US']
				)
				.check();

			await productDetailsPage.mappedProductAddToCartButton.click();

			await commerceMiniCartPage.miniCartButton.click();

			await expect(
				commerceMiniCartPage
					.miniCartItem(replacementProduct.name['en_US'])
					.getByRole('spinbutton')
			).toHaveValue('2');
			await expect(
				commerceMiniCartPage.miniCartSku(
					discontinuedProduct.skus[0].sku
				)
			).toHaveCount(0);
		});
	}
);

test(
	'A diagram product can be created and deleted from the products list',
	{tag: ['@COMMERCE-7019', '@COMMERCE-7126']},
	async ({commerceAdminProductPage}) => {
		const productName = `Diagram${getRandomInt()}`;

		await commerceAdminProductPage.goto();

		await test.step('Create a diagram product through the products list', async () => {
			await commerceAdminProductPage.creationMenuNewButton.click();
			await commerceAdminProductPage
				.menuItemProductType('Diagram')
				.click();

			await commerceAdminProductPage.modalFieldName.fill(productName);

			await commerceAdminProductPage.modalFrameLocator
				.getByPlaceholder('Type Here')
				.fill(miniumCatalog.name);

			await commerceAdminProductPage
				.modalMenuItem(miniumCatalog.name)
				.click();

			await commerceAdminProductPage.modalFrameLocator
				.getByRole('button', {exact: true, name: 'Submit'})
				.click();
		});

		await test.step('The product is listed as a diagram', async () => {
			await commerceAdminProductPage.goto();

			await commerceAdminProductPage.managementToolbarSearchInput.fill(
				productName
			);
			await commerceAdminProductPage.managementToolbarSearchInput.press(
				'Enter'
			);

			await expect(
				commerceAdminProductPage.productsTableRowLink(productName)
			).toBeVisible();
			await expect(
				commerceAdminProductPage.productsTableRow(productName)
			).toContainText('Diagram');
		});

		await test.step('Deleting it removes it from the list', async () => {
			await commerceAdminProductPage
				.productRowActionsButton(productName)
				.click();

			await commerceAdminProductPage.deleteMenuItem.click();

			await expect(
				commerceAdminProductPage.productsTableRowLink(productName)
			).toHaveCount(0);
			await expect(commerceAdminProductPage.errorAlert).toHaveCount(0);
		});
	}
);

test(
	'A pin can be dragged to a new position on the diagram',
	{tag: '@COMMERCE-7036'},
	async ({
		apiHelpers,
		commerceAdminProductDetailsDiagramPage,
		commerceAdminProductDetailsPage,
		commerceAdminProductPage,
	}) => {
		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
				name: getRandomString(),
			});

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
					quantity: 1,
					sequence: '1',
					sku: getRandomString(),
					type: 'external',
				},
				positionX: 30,
				positionY: 30,
				sequence: '1',
			}
		);

		await commerceAdminProductPage.gotoProduct(
			diagramProduct.name['en_US']
		);
		await commerceAdminProductDetailsPage.goToProductDiagram();

		const marker = commerceAdminProductDetailsDiagramPage.pinMarker('1');

		const before = await marker.getAttribute('transform');

		await commerceAdminProductDetailsDiagramPage.dragFrom(
			commerceAdminProductDetailsDiagramPage.pin('1'),
			{x: 120, y: 60}
		);

		await expect(marker).not.toHaveAttribute('transform', before);

		await expect(async () => {
			const pins = await apiHelpers.headlessCommerceAdminCatalog.getPins(
				diagramProduct.productId
			);

			const [saved] = pins.items;

			expect([saved.positionX, saved.positionY]).not.toEqual([30, 30]);
		}).toPass({timeout: 15000});
	}
);

test(
	'The canvas still pans and keeps a valid zoom after a mapped product is double clicked',
	{tag: '@COMMERCE-8865'},
	async ({
		apiHelpers,
		commerceAdminProductDetailsDiagramPage,
		commerceAdminProductDetailsPage,
		commerceAdminProductPage,
	}) => {
		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
				name: getRandomString(),
			});

		const mappedProduct =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				name: {en_US: getRandomString()},
			});

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
					productId: mappedProduct.productId,
					quantity: 1,
					sequence: '1',
					sku: mappedProduct.skus[0].sku,
					skuId: mappedProduct.skus[0].id,
					type: 'sku',
				},
				positionX: 40,
				positionY: 40,
				sequence: '1',
			}
		);

		await commerceAdminProductPage.gotoProduct(
			diagramProduct.name['en_US']
		);
		await commerceAdminProductDetailsPage.goToProductDiagram();

		await test.step('Double clicking a mapped product keeps the zoom valid', async () => {
			await commerceAdminProductDetailsDiagramPage
				.mappedProductRow(mappedProduct.skus[0].sku)
				.dblclick();

			await expect(
				commerceAdminProductDetailsDiagramPage.zoomSelect
			).not.toHaveValue(/NaN/);
		});

		await test.step('The canvas can still be panned afterwards', async () => {
			await commerceAdminProductDetailsDiagramPage.pinCancelButton.click();

			const before =
				await commerceAdminProductDetailsDiagramPage.zoomHandler.getAttribute(
					'transform'
				);

			await commerceAdminProductDetailsDiagramPage.dragFrom(
				commerceAdminProductDetailsDiagramPage.diagramImage,
				{x: 220, y: 0}
			);

			await expect(
				commerceAdminProductDetailsDiagramPage.zoomHandler
			).not.toHaveAttribute('transform', before);
		});
	}
);

test(
	'Switching a diagram to the SVG type maps its pins from the image',
	{tag: ['@COMMERCE-7100', '@COMMERCE-7021']},
	async ({
		apiHelpers,
		commerceAdminProductDetailsDiagramPage,
		commerceAdminProductDetailsPage,
		commerceAdminProductPage,
	}) => {
		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
				name: getRandomString(),
			});

		const diagramProduct =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				name: {en_US: getRandomString()},
				productType: 'diagram',
			});

		await commerceAdminProductPage.gotoProduct(
			diagramProduct.name['en_US']
		);
		await commerceAdminProductDetailsPage.goToProductDiagram();

		await commerceAdminProductDetailsDiagramPage.uploadDiagramImage(
			path.join(__dirname, '/dependencies/diagram-mapped.svg')
		);

		await test.step('The diagram starts on the default type', async () => {
			await expect(
				commerceAdminProductDetailsDiagramPage.diagramTypeSelect
			).toHaveValue('diagram.type.default');
		});

		await test.step('Switching to SVG turns the image labels into pins', async () => {
			await commerceAdminProductDetailsDiagramPage.setDiagramType('svg');

			await expect(
				commerceAdminProductDetailsDiagramPage.diagramTypeSelect
			).toHaveValue('diagram.type.svg');

			await expect(
				commerceAdminProductDetailsDiagramPage.svgPin('1')
			).toBeVisible();
			await expect(
				commerceAdminProductDetailsDiagramPage.svgPin('2')
			).toBeVisible();
		});

		await test.step('An SVG pin opens its editor with its own position', async () => {
			await commerceAdminProductDetailsDiagramPage.svgPin('1').click();

			await expect(
				commerceAdminProductDetailsDiagramPage.pinForm
			).toBeVisible();
			await expect(
				commerceAdminProductDetailsDiagramPage.pinPositionInput
			).toHaveValue('1');
		});
	}
);

test(
	'An SVG diagram maps its pins to SKUs and shows them on the storefront',
	{tag: '@COMMERCE-8078'},
	async ({
		apiHelpers,
		commerceAdminProductDetailsDiagramPage,
		commerceAdminProductDetailsPage,
		commerceAdminProductPage,
		page,
		productDetailsPage,
	}) => {
		await createAccount(apiHelpers, 'test@liferay.com');

		const mappedProducts = [];

		for (const sequence of ['1', '2', '32']) {
			mappedProducts.push({
				product:
					await apiHelpers.headlessCommerceAdminCatalog.postProduct({
						catalogId: miniumCatalog.id,
						name: {en_US: `Mapped${getRandomInt()}`},
					}),
				sequence,
			});
		}

		const diagramProduct =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: miniumCatalog.id,
				name: {en_US: `Diagram${getRandomInt()}`},
				productType: 'diagram',
			});

		await commerceAdminProductPage.gotoProduct(
			diagramProduct.name['en_US']
		);
		await commerceAdminProductDetailsPage.goToProductDiagram();

		await commerceAdminProductDetailsDiagramPage.uploadDiagramImage(
			path.join(__dirname, '/dependencies/diagram-mapped.svg')
		);

		await commerceAdminProductDetailsDiagramPage.setDiagramType('svg');

		await test.step('Link every SVG pin to a SKU', async () => {
			for (const {product, sequence} of mappedProducts) {
				await commerceAdminProductDetailsDiagramPage.mapSvgPin(
					sequence,
					{
						quantity: 2,
						sku: product.skus[0].sku,
						type: 'sku',
					}
				);
			}

			for (const {product} of mappedProducts) {
				await expect(
					commerceAdminProductDetailsDiagramPage.mappedProductRow(
						product.skus[0].sku
					)
				).toBeVisible();
			}
		});

		await test.step('Every linked pin opens its tooltip on the storefront', async () => {
			await page.goto(
				`/web/${miniumSite.name}/p/${diagramProduct.name['en_US']}`
			);

			for (const {product, sequence} of mappedProducts) {
				await productDetailsPage
					.diagramSvgPin(sequence)
					.first()
					.click();

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
			}
		});

		await test.step('The mapped products table holds all three', async () => {
			for (const {product} of mappedProducts) {
				await expect(
					productDetailsPage.mappedProductRow(product.skus[0].sku)
				).toBeVisible();
			}
		});
	}
);
