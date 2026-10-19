/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openItemSelectorModal} from '@liferay/frontend-js-item-selector-web';

import propsTransformer from '../../src/main/resources/META-INF/resources/js/AssetEntrySelectionDropdownPropsTransformer';

jest.mock('@liferay/frontend-js-item-selector-web');

const ASSET_ENTRY_TYPES = [
	{classNameId: 1, groupIds: [123], label: 'Web Content'},
	{classNameId: 2, groupIds: [123], label: 'Blogs'},
];

function open(additionalProps = {}) {
	propsTransformer({
		additionalProps: {
			assetEntryTypes: ASSET_ENTRY_TYPES,
			currentURL: 'currentURL',
			groupIds: '123',
			...additionalProps,
		},
		portletNamespace: 'ns',
	}).onClick({preventDefault: () => {}});

	return openItemSelectorModal.mock.calls[0][0];
}

describe('AssetEntrySelectionDropdownPropsTransformer', () => {
	afterEach(() => {
		openItemSelectorModal.mockReset();
	});

	it('opens the asset entries endpoint scoped to the group and permitted types', () => {
		const config = open();

		const url = new URL(config.apiURL);

		expect(url.pathname).toBe('/o/headless-delivery/v1.0/asset-entries');
		expect(url.searchParams.get('groupIds')).toBe('123');
		expect(url.searchParams.get('showNonindexable')).toBe('true');

		const filter = url.searchParams.get('filter');

		expect(filter).toContain('classNameId in (1,2)');
		expect(filter).toContain(
			"(status eq 'approved' or status eq 'scheduled')"
		);
		expect(config.multiSelect).toBe(true);
	});

	it('shows a type filter on classNameId when multiple types are permitted', () => {
		const [filter] = open().fdsProps.filters;

		expect(filter.id).toBe('classNameId');
		expect(filter.items).toEqual([
			{label: 'Web Content', value: '1'},
			{label: 'Blogs', value: '2'},
		]);
	});

	it('pins the type and hides the filter when one type is permitted', () => {
		const config = open({
			assetEntryTypes: [
				{classNameId: 9, groupIds: [123], label: 'Document'},
			],
		});

		expect(config.fdsProps.filters).toHaveLength(0);

		const url = new URL(config.apiURL);

		expect(url.searchParams.get('filter')).toContain('classNameId eq 9');
	});

	it('posts the selected asset entry IDs on selection', () => {
		const form = document.createElement('form');

		document.nsfm = form;

		Liferay.Util.postForm = jest.fn();

		open().onItemsChange([
			{assetEntryId: 10, className: 'com.example.A'},
			{assetEntryId: 11, className: 'com.example.B'},
		]);

		expect(Liferay.Util.postForm).toHaveBeenCalledWith(form, {
			data: {
				assetEntryIds: '10,11',
				cmd: 'add-selection',
				redirect: 'currentURL',
			},
		});
	});
});
