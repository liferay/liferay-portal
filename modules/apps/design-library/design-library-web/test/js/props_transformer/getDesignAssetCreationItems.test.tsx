/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import getDesignAssetCreationItems from '../../../src/main/resources/META-INF/resources/js/props_transformer/getDesignAssetCreationItems';

describe('getDesignAssetCreationItems', () => {
	it('returns no items when neither permission is granted', () => {
		expect(
			getDesignAssetCreationItems({
				canAddStyleBook: false,
				canManageFragments: false,
			})
		).toEqual([]);
	});

	it('returns only the style book item when only that permission is granted', () => {
		expect(
			getDesignAssetCreationItems({
				addStyleBookEntryURL: 'addStyleBookEntryURL',
				canAddStyleBook: true,
				canManageFragments: false,
			}).map((item) => item.label)
		).toEqual(['new-style-book']);
	});

	it('returns the three fragment items when only that permission is granted', () => {
		expect(
			getDesignAssetCreationItems({
				addFragmentCollectionURL: 'addFragmentCollectionURL',
				addFragmentEntryURL: 'addFragmentEntryURL',
				canAddStyleBook: false,
				canManageFragments: true,
			}).map((item) => item.label)
		).toEqual([
			'new-basic-fragment',
			'new-form-fragment',
			'new-fragment-set',
		]);
	});

	it('returns every item when both permissions are granted', () => {
		expect(
			getDesignAssetCreationItems({
				addFragmentCollectionURL: 'addFragmentCollectionURL',
				addFragmentEntryURL: 'addFragmentEntryURL',
				addStyleBookEntryURL: 'addStyleBookEntryURL',
				canAddStyleBook: true,
				canManageFragments: true,
			}).map((item) => item.label)
		).toEqual([
			'new-style-book',
			'new-basic-fragment',
			'new-form-fragment',
			'new-fragment-set',
		]);
	});
});
