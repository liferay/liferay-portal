/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import SearchBuilder from '../core/SearchBuilder';
import {SkuOptions} from '../enums/Product';
import HeadlessCommerceAdminPricing from '../services/rest/HeadlessCommerceAdminPricing';
import {MarketplaceDeliveryProduct} from './MarketplaceDeliveryProduct';

export class MarketplaceProduct extends MarketplaceDeliveryProduct {
	constructor(product: Product) {
		super(product as unknown as DeliveryProduct);
	}

	get specificationValues() {
		const specificationValues = super.specificationValues;

		for (const key in specificationValues) {
			(specificationValues as any)[key] = (specificationValues as any)[
				key
			].en_US;
		}

		return specificationValues;
	}

	async getProductPrices() {
		const product = this.product as unknown as Product;

		const {items: priceLists} =
			await HeadlessCommerceAdminPricing.getPriceLists(
				new URLSearchParams({
					filter: SearchBuilder.eq('type', 'price-list'),
					nestedFields: 'priceEntries',
					search: SearchBuilder.eq(
						'catalogName',
						product.catalog.name
					),
				})
			);

		const prices = {} as {
			[currency: string]: {
				[sku: string]: {
					[quantity: number]: number;
				};
			};
		};

		const marketplaceProduct = new MarketplaceProduct(product);

		const productSkus = product!.skus
			.filter((sku) =>
				sku.skuOptions.some(
					(skuOption) =>
						skuOption.key ===
							marketplaceProduct.getProductOptionKey() &&
						skuOption.value !== SkuOptions.TRIAL
				)
			)
			.map((sku) => sku.id);

		for (const priceList of priceLists) {
			const {items: priceEntries} =
				await HeadlessCommerceAdminPricing.getPriceListEntries(
					priceList.id,
					new URLSearchParams({
						filter: SearchBuilder.in('skuId', productSkus),
						nestedFields: 'priceEntry',
					})
				);

			const tierPricesItems = await Promise.all(
				priceEntries.map((priceEntry) =>
					HeadlessCommerceAdminPricing.getTierPricesByPriceEntryId(
						priceEntry.priceEntryId
					).then(({items}) => items)
				)
			);

			for (const [index, priceEntry] of priceEntries.entries()) {
				const tierPrices = tierPricesItems[index];

				const sku = product!.skus.find(
					(sku) => sku.id === priceEntry.skuId
				) as SKU;

				const skuName = sku.sku.toLowerCase();

				if (!prices[priceList.currencyCode]) {
					prices[priceList.currencyCode] = {};
				}

				if (!prices[priceList.currencyCode][skuName]) {
					prices[priceList.currencyCode][skuName] = {};
				}

				for (const tierPrice of tierPrices) {
					if (
						!prices[priceList.currencyCode][skuName][
							tierPrice.minimumQuantity
						]
					) {
						prices[priceList.currencyCode][skuName][
							tierPrice.minimumQuantity
						] = tierPrice.price;
					}
				}
			}
		}

		return prices;
	}
}
