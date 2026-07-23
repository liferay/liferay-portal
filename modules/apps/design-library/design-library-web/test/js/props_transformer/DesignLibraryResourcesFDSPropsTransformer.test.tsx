/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import DesignLibraryResourcesFDSPropsTransformer from '../../../src/main/resources/META-INF/resources/js/props_transformer/DesignLibraryResourcesFDSPropsTransformer';

const BASE_PROPS = {
	customRenderers: undefined,
	id: 'fds-design-library-resources',
	items: [],
} as any;

describe('DesignLibraryResourcesFDSPropsTransformer', () => {
	it('returns no creationMenu when neither permission is granted', () => {
		const result = DesignLibraryResourcesFDSPropsTransformer({
			...BASE_PROPS,
			additionalProps: {
				canAddStyleBook: false,
				canManageFragments: false,
			},
		});

		expect(result.creationMenu).toBeUndefined();
	});

	it('exposes only the style book primary item when only that permission is granted', () => {
		const result = DesignLibraryResourcesFDSPropsTransformer({
			...BASE_PROPS,
			additionalProps: {
				addStyleBookEntryURL: 'addStyleBookEntryURL',
				canAddStyleBook: true,
				canManageFragments: false,
			},
		});

		expect(
			result.creationMenu?.primaryItems?.map((item) => item.label)
		).toEqual(['new-style-book']);
	});

	it('exposes the three fragment primary items when only that permission is granted', () => {
		const result = DesignLibraryResourcesFDSPropsTransformer({
			...BASE_PROPS,
			additionalProps: {
				addFragmentCollectionURL: 'addFragmentCollectionURL',
				addFragmentEntryURL: 'addFragmentEntryURL',
				canAddStyleBook: false,
				canManageFragments: true,
			},
		});

		expect(
			result.creationMenu?.primaryItems?.map((item) => item.label)
		).toEqual([
			'new-basic-fragment',
			'new-form-fragment',
			'new-fragment-set',
		]);
	});

	it('exposes every primary item when both permissions are granted', () => {
		const result = DesignLibraryResourcesFDSPropsTransformer({
			...BASE_PROPS,
			additionalProps: {
				addFragmentCollectionURL: 'addFragmentCollectionURL',
				addFragmentEntryURL: 'addFragmentEntryURL',
				addStyleBookEntryURL: 'addStyleBookEntryURL',
				canAddStyleBook: true,
				canManageFragments: true,
			},
		});

		expect(
			result.creationMenu?.primaryItems?.map((item) => item.label)
		).toEqual([
			'new-style-book',
			'new-basic-fragment',
			'new-form-fragment',
			'new-fragment-set',
		]);
	});
});
