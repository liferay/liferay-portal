/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {EConfigInURLBehavior} from '@liferay/frontend-data-set-web';

const COUNTRIES_API_URL = '/o/headless-admin-address/v1.0/countries';

function buildAPIURL(excludedCountryIds) {
	const url = new URL(COUNTRIES_API_URL, window.location.origin);

	if (excludedCountryIds.length) {
		url.searchParams.set(
			'filter',
			`not (id in (${excludedCountryIds.join(',')}))`
		);
	}

	url.searchParams.set('restrictFields', 'regions');

	return url.toString();
}

export default function getCountriesItemSelectorProps({
	excludedCountryIds = [],
	portletNamespace,
}) {
	return {
		apiURL: buildAPIURL(excludedCountryIds),
		fdsProps: {
			configInURLBehavior: EConfigInURLBehavior.OFF,
			id: `${portletNamespace}countriesItemSelector`,
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
								fieldName: ['title_i18n', 'LANG'],
								label: Liferay.Language.get('name'),
							},
							{
								fieldName: 'a2',
								label: Liferay.Language.get(
									'two-letter-iso-code'
								),
							},
						],
					},
				},
			],
		},
		locator: {
			id: 'id',
			label: `title_i18n.${Liferay.ThemeDisplay.getLanguageId()}`,
			value: 'id',
		},
		multiSelect: true,
		size: 'lg',
		title: Liferay.Language.get('add-country'),
	};
}
