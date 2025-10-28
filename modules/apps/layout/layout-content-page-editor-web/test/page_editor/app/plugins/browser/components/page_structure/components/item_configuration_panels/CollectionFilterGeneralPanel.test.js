/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render} from '@testing-library/react';

import '@testing-library/jest-dom';
import React from 'react';

import {FREEMARKER_FRAGMENT_ENTRY_PROCESSOR} from '../../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/config/constants/freemarkerFragmentEntryProcessor';
import {LAYOUT_DATA_ITEM_TYPES} from '../../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/config/constants/layoutDataItemTypes';
import {StoreContextProvider} from '../../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/StoreContext';
import CollectionService from '../../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/services/CollectionService';
import {CollectionFilterGeneralPanel} from '../../../../../../../../../src/main/resources/META-INF/resources/page_editor/plugins/browser/components/page_structure/components/item_configuration_panels/CollectionFilterGeneralPanel';

jest.mock(
	'../../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/services/CollectionService',
	() => ({
		getCollectionFilters: jest.fn(),
		getCollectionSupportedFilters: jest.fn(),
	})
);

const renderComponent = (
	{
		collectionDisplays,
		collectionFilters,
		supportedFilters,
		targetCollections,
	} = {
		collectionDisplays: {},
		collectionFilters: [],
		supportedFilters: {},
		targetCollections: [],
	}
) => {
	const collectionFilterItems = {
		...collectionDisplays,

		'collection-filter-a': {
			children: [],
			config: {
				collection: {title: 'Collection Filter A'},
				fragmentEntryLinkId: 'collection-filter-a-fragment',
			},
			itemId: 'collection-filter-a',
			type: LAYOUT_DATA_ITEM_TYPES.fragment,
		},
	};

	CollectionService.getCollectionFilters.mockImplementation(() =>
		Promise.resolve(collectionFilters)
	);

	CollectionService.getCollectionSupportedFilters.mockImplementation(() =>
		Promise.resolve(supportedFilters)
	);

	return render(
		<StoreContextProvider
			initialState={{
				fragmentEntryLinks: {
					'collection-filter-a-fragment': {
						editableValues: {
							[FREEMARKER_FRAGMENT_ENTRY_PROCESSOR]: {
								targetCollections,
							},
						},
						fragmentEntryLinkId: 'collection-filter-a-fragment',
					},
				},
				layoutData: {
					deletedItems: [],
					items: collectionFilterItems,
				},
				permissions: {UPDATE: true},
			}}
			reducer={(state) => state}
		>
			<CollectionFilterGeneralPanel
				item={collectionFilterItems['collection-filter-a']}
			/>
		</StoreContextProvider>
	);
};

describe('CollectionFilterGeneralPanel', () => {
	afterEach(() => {
		CollectionService.getCollectionFilters.mockClear();
		CollectionService.getCollectionSupportedFilters.mockClear();
	});

	it('shows a warning message if there are no compatible collections or filters', async () => {
		renderComponent({
			collectionDisplays: {},
			supportedFilters: {},
		});

		const alert = document.querySelector('.alert');

		expect(alert).toHaveTextContent(
			'display-a-collection-on-the-page-that-support-at-least-one-type-of-filter'
		);
	});

	it('shows a selector with compatible filters', async () => {
		const {findByLabelText} = renderComponent({
			collectionDisplays: {
				'collection-display-a': {
					config: {collection: {title: 'Collection A'}},
					itemId: 'collection-display-a',
					type: LAYOUT_DATA_ITEM_TYPES.collection,
				},
				'collection-display-b': {
					config: {collection: {title: 'Collection B'}},
					itemId: 'collection-display-b',
					type: LAYOUT_DATA_ITEM_TYPES.collection,
				},
			},
			collectionFilters: [
				{key: 'category', label: 'Category'},
				{key: 'keywords', label: 'Keywords'},
			],
			supportedFilters: {
				'collection-display-a': ['category'],
				'collection-display-b': ['category', 'keywords'],
			},
			targetCollections: ['collection-display-a', 'collection-display-b'],
		});

		const select = await findByLabelText('filter');

		expect(
			Array.from(select.options).map(({text, value}) => ({text, value}))
		).toEqual([
			{text: 'none', value: ''},
			{text: 'Category', value: 'category'},
		]);
	});
});
