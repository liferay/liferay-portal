/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {CurrencyAbbreviation} from '../../../enums/CurrencyAbbreviation';

type NumericKeys<T> = {
	[K in keyof T]: T[K] extends number | undefined ? K : never;
}[keyof T];

export function formatCurrency(currencyCode: string, price: number) {
	if (!price) {
		return '-';
	}

	return price.toLocaleString(currencyCode, {
		currency: currencyCode,
		currencyDisplay: 'narrowSymbol',
		maximumFractionDigits: 2,
		minimumFractionDigits: 2,
		style: 'currency',
	});
}

export function getTotalByOrderKey(
	field: NumericKeys<Order>,
	orders: Order[],
	multiplier = 1
) {
	if (!orders?.length) {
		return 0;
	}

	const totals: number[] = orders.map((order) => {
		const {currencyCode} = order;
		const value = order[field as keyof Order];

		if (
			currencyCode !== CurrencyAbbreviation.USD ||
			typeof value !== 'number'
		) {
			return 0;
		}

		return value * multiplier;
	});

	const total = totals.reduce((prev, curr) => prev + curr, 0);

	return formatCurrency(CurrencyAbbreviation.USD, total);
}
