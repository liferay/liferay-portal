/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import getAccountGroupsItemSelectorProps from '../../../src/main/resources/META-INF/resources/js/item_selector/getAccountGroupsItemSelectorProps';

function getAPIURL(excludedAccountGroupIds) {
	const {apiURL} = getAccountGroupsItemSelectorProps({
		excludedAccountGroupIds,
		portletNamespace: 'ns',
	});

	return new URL(apiURL);
}

describe('getAccountGroupsItemSelectorProps', () => {
	it('lists the account groups', () => {
		const url = getAPIURL([]);

		expect(url.pathname).toBe('/o/headless-admin-user/v1.0/account-groups');
		expect(url.searchParams.has('filter')).toBe(false);
	});

	it('leaves out the account groups already on the product', () => {
		const url = getAPIURL([10, 20]);

		expect(url.searchParams.get('filter')).toBe('not (id in (10,20))');
	});

	it('keeps the data set state out of the page URL', () => {
		const {fdsProps} = getAccountGroupsItemSelectorProps({
			portletNamespace: 'ns',
		});

		expect(fdsProps.configInURLBehavior).toBe('off');
	});

	it('selects several account groups by their ID', () => {
		const {locator, multiSelect} = getAccountGroupsItemSelectorProps({
			portletNamespace: 'ns',
		});

		expect(multiSelect).toBe(true);
		expect(locator.id).toBe('id');
		expect(locator.value).toBe('id');
	});
});
