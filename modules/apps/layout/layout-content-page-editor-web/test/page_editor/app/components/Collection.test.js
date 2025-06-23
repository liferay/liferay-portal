/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {act, render, screen} from '@testing-library/react';
import React from 'react';
import {DndProvider} from 'react-dnd';
import {HTML5Backend} from 'react-dnd-html5-backend';

import {CollectionItemWithControls} from '../../../../src/main/resources/META-INF/resources/page_editor/app/components/layout_data_items';
import Collection from '../../../../src/main/resources/META-INF/resources/page_editor/app/components/layout_data_items/Collection';
import {StoreAPIContextProvider} from '../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/StoreContext';
import CollectionService from '../../../../src/main/resources/META-INF/resources/page_editor/app/services/CollectionService';
import {DragAndDropContextProvider} from '../../../../src/main/resources/META-INF/resources/page_editor/app/utils/drag_and_drop/useDragAndDrop';

jest.mock(
	'../../../../src/main/resources/META-INF/resources/page_editor/app/services/CollectionService',
	() => ({
		getCollectionField: jest.fn(),
		getCollectionMappingFields: jest.fn(() =>
			Promise.resolve([
				{key: 'field-1', label: 'Field 1', type: 'text'},
				{key: 'field-2', label: 'Field 2', type: 'text'},
			])
		),
	})
);

jest.mock(
	'../../../../src/main/resources/META-INF/resources/page_editor/app/config/index',
	() => ({
		config: {
			maxNumberOfItemsInEditMode: 2,
			searchContainerPageMaxDelta: 10,
		},
	})
);

jest.mock(
	'../../../../src/main/resources/META-INF/resources/page_editor/app/services/serviceFetch',
	() => jest.fn(() => Promise.resolve({}))
);

jest.mock(
	'../../../../src/main/resources/META-INF/resources/page_editor/common/components/ItemSelector.js',
	() => () => <div />
);

function renderCollection(itemConfig = {}) {
	const state = {
		permissions: {
			UPDATE: true,
			UPDATE_LAYOUT_CONTENT: true,
		},
	};

	const defaultConfig = {
		numberOfColumns: 1,
		numberOfItems: 5,
	};

	const collectionItemChildren = [];

	return render(
		<DndProvider backend={HTML5Backend}>
			<StoreAPIContextProvider dispatch={() => {}} getState={() => state}>
				<DragAndDropContextProvider
					layoutData={{
						items: [],
					}}
				>
					<Collection
						item={{
							config: {...defaultConfig, ...itemConfig},
							itemId: 'collection',
							parentId: '',
							type: '',
						}}
					>
						<CollectionItemWithControls
							item={{
								children: [],
								itemId: 'collection-item',
								parentId: 'collection',
								type: 'collection-item',
							}}
							layoutData={{}}
						>
							{collectionItemChildren}
						</CollectionItemWithControls>
					</Collection>
				</DragAndDropContextProvider>
			</StoreAPIContextProvider>
		</DndProvider>,
		{
			baseElement: document.body,
		}
	);
}

describe('Collection', () => {
	beforeAll(() => {
		jest.useFakeTimers();
	});

	it('renders not collection message when no collection is selected', async () => {
		await act(async () => {
			renderCollection();
		});

		expect(
			screen.getByText('select-a-collection-to-display')
		).toBeInTheDocument();
	});

	it('renders empty collection items', async () => {
		const items = [
			{content: 'Item 1 Content', title: 'Item 1 Title'},
			{content: 'Item 2 Content', title: 'Item 2 Title'},
		];

		CollectionService.getCollectionField.mockImplementation(() =>
			Promise.resolve({
				items,
				length: 2,
				totalNumberOfItems: 2,
			})
		);

		await act(async () => {
			renderCollection({
				collection: {
					itemSubtype: 'CollectionItemSubtype',
					itemType: 'CollectionItemType',
				},
				numberOfItems: 2,
				numberOfPages: 1,
				paginationType: 'none',
			});

			jest.runAllTimers();
		});

		items.forEach((item) =>
			expect(screen.getByText(item.title)).toBeInTheDocument()
		);
	});

	it('renders numeric pagination', async () => {
		await act(async () => {
			renderCollection({
				collection: {
					classNameId: '1',
					classPK: '1',
					title: 'collection1',
				},
				numberOfItemsPerPage: 5,
				numberOfPages: 1,
				paginationType: 'numeric',
			});
		});

		expect(
			screen.getByText('showing-1-to-2-of-2-entries')
		).toBeInTheDocument();
	});

	it('renders simple pagination', async () => {
		await act(async () => {
			renderCollection({
				collection: {
					classNameId: '1',
					classPK: '1',
					title: 'collection1',
				},
				numberOfItemsPerPage: 5,
				numberOfPages: 1,
				paginationType: 'simple',
			});
		});

		expect(screen.getByText('previous')).toBeInTheDocument();
		expect(screen.getByText('next')).toBeInTheDocument();
	});

	it('shows alert when edit mode max number of items is being exceeded', async () => {
		const items = [
			{content: 'Item 1 Content', title: 'Item 1 Title'},
			{content: 'Item 2 Content', title: 'Item 2 Title'},
			{content: 'Item 3 Content', title: 'Item 3 Title'},
		];

		CollectionService.getCollectionField.mockImplementation(() =>
			Promise.resolve({
				items,
				length: 3,
				totalNumberOfItems: 3,
			})
		);

		await act(async () => {
			renderCollection({
				collection: {
					classNameId: '1',
					classPK: '1',
					title: 'collection1',
				},
				numberOfItems: 3,
				numberOfPages: 1,
				paginationType: 'none',
			});

			jest.runAllTimers();
		});

		expect(
			screen.getByText(
				'in-edit-mode,-the-number-of-elements-displayed-is-limited-to-2-due-to-performance'
			)
		).toBeInTheDocument();
	});

	it('shows a permission restriction message when user does not have update permissions', async () => {
		const items = [
			{content: 'Item 1 Content', title: 'Item 1 Title'},
			{content: 'Item 2 Content', title: 'Item 2 Title'},
			{content: 'Item 3 Content', title: 'Item 3 Title'},
		];

		CollectionService.getCollectionField.mockImplementation(() =>
			Promise.resolve({
				isRestricted: true,
				items,
				length: 3,
				totalNumberOfItems: 3,
			})
		);

		await act(async () => {
			renderCollection({
				collection: {
					classNameId: '1',
					classPK: '1',
					title: 'collection1',
				},
				numberOfItems: 3,
				numberOfPages: 1,
				paginationType: 'none',
			});

			jest.runAllTimers();
		});

		expect(
			screen.getByText(
				'this-content-cannot-be-displayed-due-to-permission-restrictions'
			)
		).toBeInTheDocument();
	});
});
