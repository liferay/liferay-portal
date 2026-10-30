/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import getCurrenciesItemSelectorProps from '../../../src/main/resources/META-INF/resources/js/item_selector/getCurrenciesItemSelectorProps';

function getAPIURL(excludedCurrencyIds) {
	const {apiURL} = getCurrenciesItemSelectorProps({
		excludedCurrencyIds,
		portletNamespace: 'ns',
	});

	return new URL(apiURL);
}

describe('getCurrenciesItemSelectorProps', () => {
	it('lists only the active currencies', () => {
		const url = getAPIURL([]);

		expect(url.pathname).toBe(
			'/o/headless-commerce-admin-catalog/v1.0/currencies'
		);
		expect(url.searchParams.get('filter')).toBe('active eq true');
	});

	it('leaves out the currencies already on the channel', () => {
		const url = getAPIURL([10, 20]);

		expect(url.searchParams.get('filter')).toBe(
			'active eq true and not (id in (10,20))'
		);
	});

	it('keeps the data set state out of the page URL', () => {
		const {fdsProps} = getCurrenciesItemSelectorProps({
			portletNamespace: 'ns',
		});

		expect(fdsProps.configInURLBehavior).toBe('off');
	});

	it('selects several currencies by their ID', () => {
		const {locator, multiSelect} = getCurrenciesItemSelectorProps({
			portletNamespace: 'ns',
		});

		expect(multiSelect).toBe(true);
		expect(locator.id).toBe('id');
		expect(locator.value).toBe('id');
	});
});
