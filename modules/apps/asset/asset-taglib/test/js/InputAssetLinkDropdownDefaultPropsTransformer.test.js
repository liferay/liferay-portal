/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openItemSelectorModal} from '@liferay/frontend-js-item-selector-web';

import propsTransformer from '../../src/main/resources/META-INF/resources/js/InputAssetLinkDropdownDefaultPropsTransformer';

jest.mock('@liferay/frontend-js-item-selector-web');

function openFilter(additionalProps = {}) {
	Liferay.SearchContainer = {
		get: () => ({
			addRow: () => {},
			getData: () => '',
			updateDataStore: () => {},
		}),
	};

	propsTransformer({
		additionalProps: {
			assetEntryTypes: [
				{classNameId: 1, groupIds: [123], label: 'Web Content'},
			],
			groupIds: '123',
			refererClassNameId: 0,
			refererClassPK: 0,
			removeIcon: '',
			...additionalProps,
		},
		portletNamespace: 'ns',
	}).onClick({preventDefault: () => {}});

	return decodeURIComponent(
		openItemSelectorModal.mock.calls[0][0].apiURL.replace(/\+/g, '%20')
	);
}

describe('InputAssetLinkDropdownDefaultPropsTransformer', () => {
	afterEach(() => {
		openItemSelectorModal.mockReset();
	});

	it('excludes the referer entry when one is being edited', () => {
		expect(
			openFilter({refererClassNameId: 5, refererClassPK: 99})
		).toContain('classNameId ne 5 or classPK ne 99');
	});

	it('adds no exclusion when there is no referer entry', () => {
		expect(openFilter()).not.toContain('classNameId ne');
	});

	it('adds no exclusion when the referer ids arrive as "0" strings', () => {
		expect(
			openFilter({refererClassNameId: '0', refererClassPK: '0'})
		).not.toContain('classNameId ne');
	});
});
