/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import getRandomString from '../utils/getRandomString';
import {ApiHelpers, DataApiHelpers} from './ApiHelpers';

type TDiscount = {
	active?: boolean;
	couponCode?: string;
	discountCategories?: string;
	discountProductGroups?: string;
	discountProducts?: [
		{
			productId: number | string;
		},
	];
	id?: number;
	level?: string;
	limitationTimes?: number;
	limitationType?: string;
	maximumDiscountAmount?: number;
	neverExpire?: boolean;
	percentageLevel1?: number;
	percentageLevel2?: number;
	percentageLevel3?: number;
	percentageLevel4?: number;
	target?: string;
	title?: string;
	useCouponCode?: boolean;
	usePercentage?: boolean;
};

type TDiscountRule = {
	id?: number;
	name?: string;
	type?: string;
	typeSettings?: string;
};

type TDiscountSku = {
	discountExternalReferenceCode?: string;
	discountSkuId?: number;
	productId?: number;
	productName?: string;
	sku?: [
		{
			basePrice?: number;
			basePriceFormatted?: string;
			basePromoPrice?: number;
			basePromoPriceFormatted?: string;
		},
	];
	skuExternalReferenceCode?: string;
	skuId: number;
	unitOfMeasureKey?: string;
};

class TPriceEntry {
	bulkPricing?: boolean;
	discountDiscovery?: boolean;
	discountLevel1?: number;
	discountLevel2?: number;
	discountLevel3?: number;
	discountLevel4?: number;
	price: number;
	priceEntryId?: number;
	priceFormatted?: string;
	priceListId: number;
	priceOnApplication?: boolean;
	skuId: number;
	unitOfMeasureKey?: string;
}

class TPriceList {
	catalogId: number;
	currencyCode: string;
	id?: number;
	name: string;
	priority?: number;
	type: string;
}

class TTierPrice {
	active?: boolean;
	id?: number;
	minimumQuantity: number;
	price: number;
	priceEntryId?: number;
	unitOfMeasureKey?: string;
}

export class HeadlessCommerceAdminPricingApiHelper {
	readonly apiHelpers: ApiHelpers;
	readonly basePath: string;

	constructor(apiHelpers: ApiHelpers) {
		this.apiHelpers = apiHelpers;
		this.basePath = 'headless-commerce-admin-pricing/v2.0';
	}

	async deleteDiscount(discountId: number) {
		return this.apiHelpers.delete(
			`${this.apiHelpers.baseUrl}${this.basePath}/discounts/${discountId}`
		);
	}
	async deleteDiscountSku(discountSkuId: number) {
		return this.apiHelpers.delete(
			`${this.apiHelpers.baseUrl}${this.basePath}/discount-skus/${discountSkuId}`
		);
	}

	async deletePriceEntry(priceEntryId: number) {
		return this.apiHelpers.delete(
			`${this.apiHelpers.baseUrl}${this.basePath}/price-entries/${priceEntryId}`
		);
	}

	async deletePriceList(priceListId: number) {
		return this.apiHelpers.delete(
			`${this.apiHelpers.baseUrl}${this.basePath}/price-lists/${priceListId}`
		);
	}

	async deletePriceModifier(priceModifierId: number) {
		return this.apiHelpers.delete(
			`${this.apiHelpers.baseUrl}${this.basePath}/price-modifiers/${priceModifierId}`
		);
	}

	async deleteTierPrice(tierPriceId: number) {
		return this.apiHelpers.delete(
			`${this.apiHelpers.baseUrl}${this.basePath}/tier-prices/${tierPriceId}`
		);
	}

	async getBasePriceList(catalogId: number) {
		return this.apiHelpers.get(
			`${this.apiHelpers.baseUrl}${this.basePath}/price-lists?filter=catalogId/any(x:(x eq ${catalogId})) and catalogBasePriceList eq true and type eq 'price-list'`
		);
	}

	async getBasePriceListId(catalogId: number) {
		return this.apiHelpers.get(
			`${this.apiHelpers.baseUrl}${this.basePath}/price-lists?filter=catalogId/any(x:(x eq ${catalogId})) and catalogBasePriceList eq true and type eq 'price-list'&fields=id`
		);
	}

	async getBasePriceLists(catalogId: number) {
		return this.apiHelpers.get(
			`${this.apiHelpers.baseUrl}${this.basePath}/price-lists?filter=catalogId/any(x:(x eq ${catalogId})) and catalogBasePriceList eq true`
		);
	}

	async getBasePromoPriceList(catalogId: number) {
		return this.apiHelpers.get(
			`${this.apiHelpers.baseUrl}${this.basePath}/price-lists?filter=catalogId/any(x:(x eq ${catalogId})) and catalogBasePriceList eq true and type eq 'promotion'`
		);
	}

	async getBasePromoPriceListId(catalogId: number) {
		return this.apiHelpers.get(
			`${this.apiHelpers.baseUrl}${this.basePath}/price-lists?filter=catalogId/any(x:(x eq ${catalogId})) and catalogBasePriceList eq true and type eq 'promotion'&fields=id`
		);
	}

	async getPriceEntry(priceEntryId: number) {
		return this.apiHelpers.get(
			`${this.apiHelpers.baseUrl}${this.basePath}/price-entries/${priceEntryId}`
		);
	}

	async getPriceListEntries(
		priceListId: number,
		{pageSize = 200}: {pageSize?: number} = {}
	) {
		return this.apiHelpers.get(
			`${this.apiHelpers.baseUrl}${this.basePath}/price-lists/${priceListId}/price-entries?pageSize=${pageSize}`
		);
	}

	async getPriceListModifiers(
		priceListId: number,
		{pageSize = 200}: {pageSize?: number} = {}
	) {
		return this.apiHelpers.get(
			`${this.apiHelpers.baseUrl}${this.basePath}/price-lists/${priceListId}/price-modifiers?pageSize=${pageSize}`
		);
	}

	async getPriceLists({
		pageSize = 200,
		search,
	}: {pageSize?: number; search?: string} = {}) {
		const searchParam = search
			? `&search=${encodeURIComponent(search)}`
			: '';

		return this.apiHelpers.get(
			`${this.apiHelpers.baseUrl}${this.basePath}/price-lists?pageSize=${pageSize}${searchParam}`
		);
	}

	async getPriceModifier(priceModifierId: number) {
		return this.apiHelpers.get(
			`${this.apiHelpers.baseUrl}${this.basePath}/price-modifiers/${priceModifierId}`
		);
	}

	async getPriceModifierProducts(priceModifierId: number) {
		return this.apiHelpers.get(
			`${this.apiHelpers.baseUrl}${this.basePath}/price-modifiers/${priceModifierId}/price-modifier-products`
		);
	}

	async getTierPrices(priceEntryId: number) {
		return this.apiHelpers.get(
			`${this.apiHelpers.baseUrl}${this.basePath}/price-entries/${priceEntryId}/tier-prices`
		);
	}

	async patchPriceEntry(
		priceEntryId: number,
		priceEntry: Partial<TPriceEntry>
	) {
		return this.apiHelpers.patch(
			`${this.apiHelpers.baseUrl}${this.basePath}/price-entries/${priceEntryId}`,
			priceEntry
		);
	}

	async patchTierPrice(tierPriceId: number, tierPrice: Partial<TTierPrice>) {
		return this.apiHelpers.patch(
			`${this.apiHelpers.baseUrl}${this.basePath}/tier-prices/${tierPriceId}`,
			tierPrice
		);
	}

	async postDiscount(discount?: TDiscount) {
		discount = {
			active: true,
			level: 'L1',
			limitationType: 'unlimited',
			maximumDiscountAmount: 0,
			neverExpire: true,
			percentageLevel1: 20,
			percentageLevel2: 0,
			percentageLevel3: 0,
			percentageLevel4: 0,
			target: 'products',
			title: getRandomString(),
			useCouponCode: false,
			usePercentage: false,
			...(discount || {}),
		};

		discount = await this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/discounts`,
			{
				data: discount,
				failOnStatusCode: true,
			}
		);

		if (this.apiHelpers instanceof DataApiHelpers) {
			this.apiHelpers.data.push({id: discount.id, type: 'discount'});
		}

		return discount;
	}

	async postDiscountRule(discountId: number, discountRule?: TDiscountRule) {
		discountRule = {
			name: getRandomString(),
			type: 'cart-total',
			...(discountRule || {}),
		};

		discountRule = await this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/discounts/${discountId}/discount-rules`,
			{
				data: discountRule,
				failOnStatusCode: true,
			}
		);

		if (this.apiHelpers instanceof DataApiHelpers) {
			this.apiHelpers.data.push({
				id: discountRule.id,
				type: 'discountRule',
			});
		}

		return discountRule;
	}

	async postDiscountSku(discountId: number, discountSku?: TDiscountSku) {
		discountSku = await this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/discounts/${discountId}/discount-skus`,
			{
				data: discountSku,
			}
		);

		return discountSku;
	}

	async postPriceEntry(priceEntry: TPriceEntry) {
		priceEntry = await this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/price-lists/${priceEntry.priceListId}/price-entries`,
			{
				data: priceEntry,
				failOnStatusCode: true,
			}
		);

		if (this.apiHelpers instanceof DataApiHelpers) {
			this.apiHelpers.data.push({
				id: priceEntry.priceEntryId,
				type: 'price-entry',
			});
		}

		return priceEntry;
	}

	async postPriceList(priceList?: TPriceList) {
		priceList = await this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/price-lists`,
			{
				data: priceList,
			}
		);

		if (this.apiHelpers instanceof DataApiHelpers) {
			this.apiHelpers.data.push({
				id: priceList.id,
				type: 'price-list',
			});
		}

		return priceList;
	}

	async postPriceListAccountRel(priceListId: number, accountId: number) {
		return this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/price-lists/${priceListId}/price-list-accounts`,
			{
				data: {accountId, priceListId},
				failOnStatusCode: true,
			}
		);
	}

	async postPriceListChannel(priceListId: number, channelId: number) {
		return this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/price-lists/${priceListId}/price-list-channels`,
			{
				data: {channelId, priceListId},
				failOnStatusCode: true,
			}
		);
	}

	async postDiscountAccount(discountId: number, accountId: number) {
		return this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/discounts/${discountId}/discount-accounts`,
			{
				data: {accountId, discountId},
				failOnStatusCode: true,
			}
		);
	}

	async postDiscountProductGroup(discountId: number, productGroupId: number) {
		return this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/discounts/${discountId}/discount-product-groups`,
			{
				data: {discountId, productGroupId},
				failOnStatusCode: true,
			}
		);
	}

	async postPriceModifier(
		priceListId: number,
		priceModifier: {
			active?: boolean;
			modifierAmount: number;
			modifierType: string;
			priority?: number;
			target: string;
			title: string;
		}
	) {
		return this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/price-lists/${priceListId}/price-modifiers`,
			{
				data: {active: true, ...priceModifier},
				failOnStatusCode: true,
			}
		);
	}

	async postPriceModifierProduct(priceModifierId: number, productId: number) {
		return this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/price-modifiers/${priceModifierId}/price-modifier-products`,
			{
				data: {priceModifierId, productId},
				failOnStatusCode: true,
			}
		);
	}

	async postPriceModifierProductGroup(
		priceModifierId: number,
		productGroupId: number
	) {
		return this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/price-modifiers/${priceModifierId}/price-modifier-product-groups`,
			{
				data: {priceModifierId, productGroupId},
				failOnStatusCode: true,
			}
		);
	}

	async postTierPrice(
		priceEntryId: number,
		tierPrice: TTierPrice,
		{failOnStatusCode = true}: {failOnStatusCode?: boolean} = {}
	) {
		const postTierPrice = await this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/price-entries/${priceEntryId}/tier-prices`,
			{
				data: {
					active: true,
					priceEntryId,
					unitOfMeasureKey: '',
					...tierPrice,
				},
				failOnStatusCode,
			}
		);

		if (this.apiHelpers instanceof DataApiHelpers && postTierPrice?.id) {
			this.apiHelpers.data.push({
				id: postTierPrice.id,
				type: 'tierPrice',
			});
		}

		return postTierPrice;
	}
}
