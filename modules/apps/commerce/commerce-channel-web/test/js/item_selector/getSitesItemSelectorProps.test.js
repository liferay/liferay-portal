/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import getSitesItemSelectorProps from '../../../src/main/resources/META-INF/resources/js/item_selector/getSitesItemSelectorProps';

describe('getSitesItemSelectorProps', () => {
	it('lists the sites available to the channel', () => {
		const {apiURL} = getSitesItemSelectorProps({
			commerceChannelId: 123,
			portletNamespace: 'ns',
		});

		expect(apiURL).toBe(
			'/o/headless-commerce-admin-channel/v1.0/channels/123/available-sites'
		);
	});

	it('keeps the data set state out of the page URL', () => {
		const {fdsProps} = getSitesItemSelectorProps({portletNamespace: 'ns'});

		expect(fdsProps.configInURLBehavior).toBe('off');
	});

	it('selects a single site by its ID', () => {
		const {locator, multiSelect} = getSitesItemSelectorProps({
			portletNamespace: 'ns',
		});

		expect(multiSelect).toBeFalsy();
		expect(locator.id).toBe('id');
		expect(locator.value).toBe('id');
	});
});
