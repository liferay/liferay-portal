/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import DesignLibraryAssetsFDSPropsTransformer from '../../../src/main/resources/META-INF/resources/js/props_transformer/DesignLibraryAssetsFDSPropsTransformer';

const BASE_PROPS = {
	id: 'fds-design-library-resources',
	items: [],
} as any;

const STYLE_BOOK_RESOURCE_TYPE = {
	color: 'purple',
	defaultActionId: 'edit',
	entryClassName: 'com.liferay.style.book.model.StyleBookEntry',
	key: 'style-book',
	label: 'Style Book',
	symbol: 'book',
};

const FRAGMENT_RESOURCE_TYPE = {
	color: 'pink',
	defaultActionId: 'view',
	entryClassName: 'com.liferay.fragment.model.FragmentCollection',
	key: 'fragment',
	label: 'Fragment Set',
	symbol: 'cards2',
};

function creationItem(id: string, label: string) {
	return {
		id,
		label,
		module: `http://localhost/${id}`,
		moduleProps: {},
	};
}

describe('DesignLibraryAssetsFDSPropsTransformer', () => {
	it('builds the creation menu from the contributed creation items', () => {
		const {creationMenu} = DesignLibraryAssetsFDSPropsTransformer({
			...BASE_PROPS,
			additionalProps: {
				resourceTypes: [
					{
						...STYLE_BOOK_RESOURCE_TYPE,
						creationItems: [
							creationItem('add-style-book', 'new-style-book'),
						],
					},
				],
			},
		});

		expect(creationMenu?.primaryItems.map((item) => item.label)).toEqual([
			'new-style-book',
		]);
	});

	it('orders the creation menu by contributor', () => {
		const {creationMenu} = DesignLibraryAssetsFDSPropsTransformer({
			...BASE_PROPS,
			additionalProps: {
				resourceTypes: [
					{
						...STYLE_BOOK_RESOURCE_TYPE,
						creationItems: [
							creationItem('add-style-book', 'new-style-book'),
						],
					},
					{
						...FRAGMENT_RESOURCE_TYPE,
						creationItems: [
							creationItem(
								'add-basic-fragment',
								'new-basic-fragment'
							),
							creationItem(
								'add-fragment-set',
								'new-fragment-set'
							),
						],
					},
				],
			},
		});

		expect(creationMenu?.primaryItems.map((item) => item.label)).toEqual([
			'new-style-book',
			'new-basic-fragment',
			'new-fragment-set',
		]);
	});

	it('omits the creation menu when no type contributes creation items', () => {
		expect(
			DesignLibraryAssetsFDSPropsTransformer({
				...BASE_PROPS,
				additionalProps: {
					resourceTypes: [STYLE_BOOK_RESOURCE_TYPE],
				},
			}).creationMenu
		).toBeUndefined();
	});

	it('omits the creation menu when there are no resource types', () => {
		expect(
			DesignLibraryAssetsFDSPropsTransformer(BASE_PROPS).creationMenu
		).toBeUndefined();
	});

	it('exposes the table view', () => {
		expect(
			DesignLibraryAssetsFDSPropsTransformer(BASE_PROPS).views?.map(
				(view) => view.name
			)
		).toEqual(['table']);
	});
});
