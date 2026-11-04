/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import getChannelsItemSelectorProps from '../../../src/main/resources/META-INF/resources/js/item_selector/getChannelsItemSelectorProps';

function getAPIURL(excludedChannelIds) {
	const {apiURL} = getChannelsItemSelectorProps({
		excludedChannelIds,
		portletNamespace: 'ns',
	});

	return new URL(apiURL);
}

describe('getChannelsItemSelectorProps', () => {
	it('lists the channels', () => {
		const url = getAPIURL([]);

		expect(url.pathname).toBe(
			'/o/headless-commerce-admin-channel/v1.0/channels'
		);
		expect(url.searchParams.has('filter')).toBe(false);
	});

	it('leaves out the channels already on the product', () => {
		const url = getAPIURL([10, 20]);

		expect(url.searchParams.get('filter')).toBe('not (id in (10,20))');
	});

	it('keeps the data set state out of the page URL', () => {
		const {fdsProps} = getChannelsItemSelectorProps({
			portletNamespace: 'ns',
		});

		expect(fdsProps.configInURLBehavior).toBe('off');
	});

	it('selects several channels by their ID', () => {
		const {locator, multiSelect} = getChannelsItemSelectorProps({
			portletNamespace: 'ns',
		});

		expect(multiSelect).toBe(true);
		expect(locator.id).toBe('id');
		expect(locator.value).toBe('id');
	});
});
