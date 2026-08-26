/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {globalMenuPagesTest} from '../../../../fixtures/globalMenuPagesTest';
import {isolatedSiteTest} from '../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../fixtures/loginTest';
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
	globalMenuPagesTest,
	isolatedSiteTest,
	loginTest()
);

test(
	'Tier Price quantities are not converted to scientific notation',
	{tag: ['@LPD-75285']},
	async ({
		apiHelpers,
		commerceAdminPriceListDetailsPage,
		commerceAdminPriceListsPage,
		page,
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

		const productSku = productSkus[0];

		const currencies =
			await apiHelpers.headlessCommerceAdminCatalog.getCurrenciesPage(
				'USD'
			);

		const priceList =
			await apiHelpers.headlessCommerceAdminPricing.postPriceList({
				catalogId: catalog.id,
				currencyCode: currencies.items[0].code,
				name: 'Price List' + getRandomInt(),
				type: 'price-list',
			});

		await commerceAdminPriceListsPage.goto();

		await (
			await commerceAdminPriceListsPage.tableRowLink({
				colIndex: 0,
				rowValue: priceList.name,
			})
		).click();

		await commerceAdminPriceListDetailsPage.entriesTab.click();

		await commerceAdminPriceListDetailsPage.findSkuInput.click();
		await commerceAdminPriceListDetailsPage.findSkuInput.fill(
			productSku.sku
		);

		await commerceAdminPriceListDetailsPage.selectButton.click();

		await page.reload();

		await commerceAdminPriceListDetailsPage
			.skusTableRowLink(productSku.sku)
			.click();

		await commerceAdminPriceListDetailsPage.addTierPriceButton.click();

		await commerceAdminPriceListDetailsPage.addTierPriceEntryQuantity.fill(
			'50'
		);

		const price = '100';
		await commerceAdminPriceListDetailsPage.addTierPriceEntryPrice.fill(
			price
		);

		await commerceAdminPriceListDetailsPage.addTierPriceEntrySaveButton.click();

		await commerceAdminPriceListDetailsPage
			.skuLink(`$ ${price}.00`)
			.click();

		await expect(
			commerceAdminPriceListDetailsPage.editPriceTierPrice
		).toHaveValue(`${price}.00`);
	}
);

test(
	'Verify base price lists do not have UI option for schedule',
	{tag: ['@LPD-78018']},
	async ({
		apiHelpers,
		commerceAdminPriceListDetailsPage,
		commerceAdminPriceListsPage,
	}) => {
		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
				name: 'Catalog',
			});

		const currencies =
			await apiHelpers.headlessCommerceAdminCatalog.getCurrenciesPage(
				'USD'
			);

		const priceList =
			await apiHelpers.headlessCommerceAdminPricing.postPriceList({
				catalogId: catalog.id,
				currencyCode: currencies.items[0].code,
				name: 'Price List' + getRandomInt(),
				type: 'price-list',
			});

		await commerceAdminPriceListsPage.goto();

		await (
			await commerceAdminPriceListsPage.tableRowLink({
				colIndex: 0,
				rowValue: 'Catalog Base Price List',
			})
		).click();

		await expect(
			commerceAdminPriceListDetailsPage.scheduleLabel
		).not.toBeVisible();

		await commerceAdminPriceListsPage.goto();

		await (
			await commerceAdminPriceListsPage.tableRowLink({
				colIndex: 0,
				rowValue: priceList.name,
			})
		).click();

		await expect(
			commerceAdminPriceListDetailsPage.scheduleLabel
		).toBeVisible();
	}
);

test(
	'Admin can view UOM information for a SKU from the Price Lists, Promotions and SKU Price tab admin panels',
	{tag: ['@COMMERCE-12470', '@COMMERCE-12471', '@LPD-87067']},
	async ({
		apiHelpers,
		commerceAdminPriceListDetailsPage,
		commerceAdminPriceListsPage,
		commerceAdminProductDetailsSkusPage,
		commerceAdminProductPage,
		commerceAdminPromotionsPage,
	}) => {
		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

		const product =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
			});

		const productSkus = await apiHelpers.headlessCommerceAdminCatalog
			.getProduct(product.productId)
			.then((product) => product.skus);

		const sku = productSkus[0];

		for (const index of [1, 2]) {
			await apiHelpers.headlessCommerceAdminCatalog.postSkuUnitOfMeasure(
				sku.id,
				{
					basePrice: index,
					key: `uomKey${index}`,
					name: {en_US: `uomName${index}`},
					primary: index === 1,
					priority: index,
					promoPrice: index,
				}
			);
		}

		await test.step('Verify UOM is shown for each entry in the Price List', async () => {
			await commerceAdminPriceListsPage.goto();

			await (
				await commerceAdminPriceListsPage.tableRowLink({
					colIndex: 0,
					rowValue: `${catalog.name} Base Price List`,
				})
			).click();

			await commerceAdminPriceListDetailsPage.entriesTab.click();

			await commerceAdminPriceListDetailsPage.searchByValue(sku.sku);

			await commerceAdminPriceListDetailsPage.assertUOMSelectedInSidePanel(
				{
					linkName: sku.sku,
					rowText: sku.sku,
				}
			);
		});

		await test.step('Verify UOM is shown for each entry in the Promotion', async () => {
			await commerceAdminPromotionsPage.goto();

			await (
				await commerceAdminPromotionsPage.tableRowLink({
					colIndex: 0,
					rowValue: `${catalog.name} Base Promotion`,
				})
			).click();

			await commerceAdminPriceListDetailsPage.entriesTab.click();

			await commerceAdminPriceListDetailsPage.searchByValue(sku.sku);

			await commerceAdminPriceListDetailsPage.assertUOMSelectedInSidePanel(
				{
					linkName: sku.sku,
					rowText: sku.sku,
				}
			);
		});

		await test.step('Verify UOM is shown for each entry in the SKU Price tab', async () => {
			await commerceAdminProductPage.gotoProduct(product.name.en_US);

			await commerceAdminProductPage.productSkusLink.click();

			await commerceAdminProductDetailsSkusPage
				.skusTableRowLink(sku.sku)
				.click();

			await commerceAdminProductDetailsSkusPage.skuTab('Price').click();

			const skuSidePanel =
				commerceAdminProductDetailsSkusPage.sidePanelFrame;

			for (const baseEntryName of [
				`${catalog.name} Base Price List`,
				`${catalog.name} Base Promotion`,
			]) {
				await commerceAdminPriceListDetailsPage.assertUOMSelectedInSidePanel(
					{
						linkName: baseEntryName,
						rowText: baseEntryName,
						scope: skuSidePanel,
					}
				);
			}
		});
	}
);

test(
	'Buyer sees Price on Application label and Request A Quote button when switching to a UOM with PoA enabled',
	{tag: ['@COMMERCE-12479', '@LPD-87067']},
	async ({
		apiHelpers,
		commerceAdminChannelDetailsPage,
		commerceAdminChannelsPage,
		commerceMiniCartPage,
		page,
		productDetailsPage,
		site,
	}) => {
		const channel =
			await apiHelpers.headlessCommerceAdminChannel.postChannel({
				siteGroupId: site.id,
			});

		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

		const product =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
			});

		const productSkus = await apiHelpers.headlessCommerceAdminCatalog
			.getProduct(product.productId)
			.then((p) => p.skus);

		const sku = productSkus[0];

		for (const index of [1, 2]) {
			await apiHelpers.headlessCommerceAdminCatalog.postSkuUnitOfMeasure(
				sku.id,
				{
					basePrice: index,
					key: `uomKey${index}`,
					name: {en_US: `uomName${index}`},
					primary: index === 1,
					priority: index,
					promoPrice: index,
				}
			);
		}

		const basePriceList = (
			await apiHelpers.headlessCommerceAdminPricing.getBasePriceListId(
				catalog.id
			)
		).items[0];

		const priceEntries =
			await apiHelpers.headlessCommerceAdminPricing.getPriceListEntries(
				basePriceList.id
			);

		const uom2Entry = priceEntries.items.find(
			(entry: {skuId: number; unitOfMeasureKey: string}) =>
				entry.skuId === sku.id && entry.unitOfMeasureKey === 'uomKey2'
		);

		await apiHelpers.headlessCommerceAdminPricing.patchPriceEntry(
			uom2Entry.priceEntryId,
			{priceOnApplication: true}
		);

		await commerceAdminChannelsPage.goto();

		await (
			await commerceAdminChannelsPage.channelsTableRowLink(channel.name)
		).click();

		await commerceAdminChannelDetailsPage.allowRequestAQuote.click();

		await commerceAdminChannelDetailsPage.saveButton.click();

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

		const {buyerUser} = await createAccountWithBuyerUser(
			apiHelpers,
			site.id
		);

		await performLogout(page);
		await performLoginViaApi({
			page,
			screenName: buyerUser.alternateName,
		});

		await page.goto(
			`/web${site.friendlyUrlPath}/p/${product.name['en_US']}`
		);

		const uomSelect = page.locator('select.unit-of-measure-selector');

		const priceOnApplicationLabel =
			productDetailsPage.priceContainer.getByText(
				'Price on Application',
				{exact: true}
			);

		await test.step('UOM1 does not show the Price on Application label', async () => {
			await expect(uomSelect).toBeVisible();

			await expect(priceOnApplicationLabel).toBeHidden();
		});

		await test.step('Switching to UOM2 reveals the Price on Application label', async () => {
			await uomSelect.selectOption({label: 'uomName2'});

			await expect(priceOnApplicationLabel).toBeVisible();
		});

		try {
			await test.step('Mini cart shows the Request A Quote button after adding the PoA item', async () => {
				await productDetailsPage.addToCartButton.click();

				await expect(commerceMiniCartPage.miniCartButton).toHaveClass(
					'has-badge mini-cart-opener'
				);

				await commerceMiniCartPage.miniCartButton.click();

				await expect(
					commerceMiniCartPage.miniCartItem(product.name['en_US'])
				).toContainText('Price on Application');

				await expect(
					commerceMiniCartPage.requestAQuoteButton
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
