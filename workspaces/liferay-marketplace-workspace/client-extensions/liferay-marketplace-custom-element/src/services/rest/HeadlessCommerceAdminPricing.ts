/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import fetcher from '../fetcher';

export default class HeadlessCommerceAdminPricing {
	static createPriceEntry(body: unknown, priceListId: number) {
		return fetcher.post<PriceEntry>(
			`/o/headless-commerce-admin-pricing/v2.0/price-lists/${priceListId}/price-entries`,
			body
		);
	}

	static createPriceList(body: unknown) {
		return fetcher.post<PriceList>(
			'o/headless-commerce-admin-pricing/v2.0/price-lists',
			body
		);
	}

	static createTierPrice(body: unknown, priceEntryId: number) {
		return fetcher.post<TierPrice>(
			`o/headless-commerce-admin-pricing/v2.0/price-entries/${priceEntryId}/tier-prices`,
			body
		);
	}

	static deleteTierPrice(tierPriceId: number) {
		return fetcher.delete(
			`o/headless-commerce-admin-pricing/v2.0/tier-prices/${tierPriceId}`
		);
	}

	static getPriceListEntries(
		priceListId: number,
		searchParams = new URLSearchParams()
	) {
		return fetcher<APIResponse<PriceEntry>>(
			`o/headless-commerce-admin-pricing/v2.0/price-lists/${priceListId}/price-entries?${searchParams.toString()}`
		);
	}

	static getPriceLists(searchParams = new URLSearchParams()) {
		return fetcher<APIResponse<PriceList>>(
			`o/headless-commerce-admin-pricing/v2.0/price-lists?${searchParams.toString()}`
		);
	}

	static getTierPricesByPriceEntryId(priceEntryId: number) {
		return fetcher<APIResponse<TierPrice>>(
			`o/headless-commerce-admin-pricing/v2.0/price-entries/${priceEntryId}/tier-prices`
		);
	}

	static updatePriceEntry(body: unknown, priceEntryId: number) {
		return fetcher.patch<PriceEntry>(
			`/o/headless-commerce-admin-pricing/v2.0/price-entries/${priceEntryId}`,
			body
		);
	}
}
