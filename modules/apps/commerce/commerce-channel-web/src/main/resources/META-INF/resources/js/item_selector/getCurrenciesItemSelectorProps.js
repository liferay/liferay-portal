/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {EConfigInURLBehavior} from '@liferay/frontend-data-set-web';

const CURRENCIES_API_URL = '/o/headless-commerce-admin-catalog/v1.0/currencies';

function buildAPIURL(excludedCurrencyIds) {
	const url = new URL(CURRENCIES_API_URL, window.location.origin);

	const filters = ['active eq true'];

	if (excludedCurrencyIds.length) {
		filters.push(`not (id in (${excludedCurrencyIds.join(',')}))`);
	}

	url.searchParams.set('filter', filters.join(' and '));

	return url.toString();
}

export default function getCurrenciesItemSelectorProps({
	excludedCurrencyIds = [],
	portletNamespace,
}) {
	return {
		apiURL: buildAPIURL(excludedCurrencyIds),
		fdsProps: {
			configInURLBehavior: EConfigInURLBehavior.OFF,
			id: `${portletNamespace}currenciesItemSelector`,
			pagination: {
				deltas: [{label: 20}, {label: 40}, {label: 60}],
				initialDelta: 20,
			},
			views: [
				{
					contentRenderer: 'table',
					label: '',
					name: 'table',
					schema: {
						fields: [
							{
								fieldName: ['name', 'LANG'],
								label: Liferay.Language.get('name'),
							},
							{
								fieldName: 'code',
								label: Liferay.Language.get('code'),
							},
							{
								fieldName: 'symbol',
								label: Liferay.Language.get('symbol'),
							},
						],
					},
				},
			],
		},
		locator: {
			id: 'id',
			label: 'code',
			value: 'id',
		},
		multiSelect: true,
		size: 'lg',
		title: Liferay.Language.get('add-currency'),
	};
}
