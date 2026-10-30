/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import getCountriesItemSelectorProps from '../../../src/main/resources/META-INF/resources/js/item_selector/getCountriesItemSelectorProps';

function getAPIURL(excludedCountryIds) {
	const {apiURL} = getCountriesItemSelectorProps({
		excludedCountryIds,
		portletNamespace: 'ns',
	});

	return new URL(apiURL);
}

describe('getCountriesItemSelectorProps', () => {
	it('lists every country without its regions', () => {
		const url = getAPIURL([]);

		expect(url.pathname).toBe('/o/headless-admin-address/v1.0/countries');
		expect(url.searchParams.has('filter')).toBe(false);
		expect(url.searchParams.get('restrictFields')).toBe('regions');
	});

	it('leaves out the countries already on the channel', () => {
		const url = getAPIURL([10, 20]);

		expect(url.searchParams.get('filter')).toBe('not (id in (10,20))');
	});

	it('keeps the data set state out of the page URL', () => {
		const {fdsProps} = getCountriesItemSelectorProps({
			portletNamespace: 'ns',
		});

		expect(fdsProps.configInURLBehavior).toBe('off');
	});

	it('selects several countries by their ID', () => {
		const {locator, multiSelect} = getCountriesItemSelectorProps({
			portletNamespace: 'ns',
		});

		expect(multiSelect).toBe(true);
		expect(locator.id).toBe('id');
		expect(locator.value).toBe('id');
	});

	it('labels the selected country with its title in the user language', () => {
		const {locator} = getCountriesItemSelectorProps({
			portletNamespace: 'ns',
		});

		expect(locator.label).toBe('title_i18n.en_US');
	});
});
