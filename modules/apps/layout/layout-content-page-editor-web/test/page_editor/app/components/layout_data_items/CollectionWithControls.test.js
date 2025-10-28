/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {cleanup, render} from '@testing-library/react';
import React from 'react';
import {DndProvider} from 'react-dnd';
import {HTML5Backend} from 'react-dnd-html5-backend';

import {CollectionWithControls} from '../../../../../src/main/resources/META-INF/resources/page_editor/app/components/layout_data_items';
import {LAYOUT_DATA_ITEM_TYPES} from '../../../../../src/main/resources/META-INF/resources/page_editor/app/config/constants/layoutDataItemTypes';
import {VIEWPORT_SIZES} from '../../../../../src/main/resources/META-INF/resources/page_editor/app/config/constants/viewportSizes';
import {
	ControlsProvider,
	useSelectItem,
} from '../../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/ControlsContext';
import {StoreAPIContextProvider} from '../../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/StoreContext';
import getLayoutDataItemClassName from '../../../../../src/main/resources/META-INF/resources/page_editor/app/utils/getLayoutDataItemClassName';
import getLayoutDataItemTopperUniqueClassName from '../../../../../src/main/resources/META-INF/resources/page_editor/app/utils/getLayoutDataItemTopperUniqueClassName';
import getLayoutDataItemUniqueClassName from '../../../../../src/main/resources/META-INF/resources/page_editor/app/utils/getLayoutDataItemUniqueClassName';

const COLLECTION_ID = 'COLLECTION_ID';

jest.mock(
	'../../../../../src/main/resources/META-INF/resources/page_editor/app/services/InfoItemService',
	() => ({
		getPageContents: jest.fn(() => Promise.resolve()),
	})
);

const renderCollection = ({
	isActive = true,
	collectionConfig = {styles: {}},
	viewportSize = VIEWPORT_SIZES.desktop,
	lockedSegment = false,
	hasUpdatePermission = true,
} = {}) => {
	const collection = {
		children: [],
		config: collectionConfig,
		itemId: COLLECTION_ID,
		parentId: null,
		type: LAYOUT_DATA_ITEM_TYPES.collection,
	};

	const layoutData = {
		items: {[collection.itemId]: collection},
	};

	const AutoSelector = () => {
		useSelectItem()(isActive ? 'collection' : null);

		return null;
	};

	return render(
		<DndProvider backend={HTML5Backend}>
			<ControlsProvider>
				<StoreAPIContextProvider
					dispatch={() => {}}
					getState={() => ({
						fragmentEntryLinks: {},
						layoutData,
						permissions: {
							LOCKED_SEGMENTS_EXPERIMENT: lockedSegment,
							UPDATE: hasUpdatePermission,
						},
						selectedViewportSize: viewportSize,
					})}
				>
					<AutoSelector />

					<CollectionWithControls
						item={collection}
						layoutData={layoutData}
					/>
				</StoreAPIContextProvider>
			</ControlsProvider>
		</DndProvider>
	);
};

describe('CollectionWithControls', () => {
	afterEach(cleanup);

	it('removes all buttons if user has no permissions', () => {
		const {queryByTitle} = renderCollection({hasUpdatePermission: false});

		expect(queryByTitle('collection-display-configuration')).toBe(null);
	});

	it('removes all buttons if experience is locked', () => {
		const {queryByTitle} = renderCollection({lockedSegment: true});

		expect(queryByTitle('collection-display-configuration')).toBe(null);
	});

	it('does not allow deleting or duplicating the collection if user has no permissions', () => {
		const {queryByText} = renderCollection({
			hasUpdatePermission: false,
		});

		expect(queryByText('delete')).not.toBeInTheDocument();
		expect(queryByText('duplicate')).not.toBeInTheDocument();
	});

	it('set classes for referencing the item', () => {
		const {baseElement} = renderCollection();

		const classes = [
			getLayoutDataItemClassName(LAYOUT_DATA_ITEM_TYPES.collection),
			getLayoutDataItemTopperUniqueClassName(COLLECTION_ID),
			getLayoutDataItemUniqueClassName(COLLECTION_ID),
		];

		classes.forEach((className) => {
			const item = baseElement.querySelector(`.${className}`);

			expect(item).toBeVisible();
		});
	});
});
