/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {cleanup, queryByText, render} from '@testing-library/react';
import React from 'react';
import {DndProvider} from 'react-dnd';
import {HTML5Backend} from 'react-dnd-html5-backend';

import {RowWithControls} from '../../../../../src/main/resources/META-INF/resources/page_editor/app/components/layout_data_items';
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

const ROW_ID = 'ROW_ID';

const renderRow = ({
	activeItemId = 'row',
	columnConfiguration = [],
	hasUpdatePermissions = true,
	lockedExperience = false,
	rowConfig = {styles: {}},
	viewportSize = VIEWPORT_SIZES.desktop,
} = {}) => {
	const childrenItems = {};

	columnConfiguration.forEach(({size, children = []}, index) => {
		const itemId = `column${index}`;

		childrenItems[itemId] = {
			children,
			config: {
				size,
				styles: {},
			},
			itemId,
			parentId: 'row',
			type: LAYOUT_DATA_ITEM_TYPES.col,
		};
	});

	const row = {
		children: Object.keys(childrenItems),
		config: rowConfig,
		itemId: ROW_ID,
		parentId: null,
		type: LAYOUT_DATA_ITEM_TYPES.row,
	};

	const child = {
		children: [],
		config: {styles: {}},
		itemId: 'child',
		parentId: null,
		type: LAYOUT_DATA_ITEM_TYPES.container,
	};

	const layoutData = {
		items: {[child.itemId]: child, [row.itemId]: row, ...childrenItems},
	};

	const AutoSelect = () => {
		useSelectItem()(activeItemId);

		return null;
	};

	return render(
		<DndProvider backend={HTML5Backend}>
			<ControlsProvider>
				<StoreAPIContextProvider
					getState={() => ({
						fragmentEntryLinks: {},
						layoutData,
						permissions: {
							LOCKED_SEGMENTS_EXPERIMENT: lockedExperience,
							UPDATE: hasUpdatePermissions,
						},
						selectedViewportSize: viewportSize,
					})}
				>
					<AutoSelect />

					<RowWithControls item={row} layoutData={layoutData} />
				</StoreAPIContextProvider>
			</ControlsProvider>
		</DndProvider>
	);
};

describe('RowWithControls', () => {
	afterEach(cleanup);

	it('does not add row class if user has no permissions', () => {
		const {baseElement} = renderRow({hasUpdatePermissions: false});

		expect(baseElement.querySelector('.page-editor__row')).toBe(null);
	});

	it('does not allow deleting or duplicating the row if user has no permissions', () => {
		const {baseElement} = renderRow({hasUpdatePermissions: false});

		expect(queryByText(baseElement, 'delete')).not.toBeInTheDocument();
		expect(queryByText(baseElement, 'duplicate')).not.toBeInTheDocument();
	});

	it('does include the empty class when having one row and columns are empty', () => {
		const {baseElement} = renderRow({
			columnConfiguration: [{size: 4}, {size: 4}, {size: 4}],
		});

		expect(
			baseElement.querySelector('.page-editor__row.empty')
		).toBeInTheDocument();
	});

	it('does not include the empty class when having one row and one column is not empty', () => {
		const {baseElement} = renderRow({
			columnConfiguration: [
				{children: ['child'], size: 4},
				{size: 4},
				{size: 4},
			],
		});

		expect(baseElement.querySelector('.page-editor__row.empty')).toBe(null);
	});

	it('does not include the empty class when having several rows and some columns from one row are empty', () => {
		const {baseElement} = renderRow({
			columnConfiguration: [
				{children: ['child'], size: 6},
				{size: 6},
				{size: 4},
				{size: 4},
				{size: 4},
			],
		});

		expect(
			baseElement.querySelector('.page-editor__row.empty')
		).toBeInTheDocument();
	});

	it('set classes for referencing the item', () => {
		const {baseElement} = renderRow();

		const classes = [
			getLayoutDataItemClassName(LAYOUT_DATA_ITEM_TYPES.row),
			getLayoutDataItemTopperUniqueClassName(ROW_ID),
			getLayoutDataItemUniqueClassName(ROW_ID),
		];

		classes.forEach((className) => {
			const item = baseElement.querySelector(`.${className}`);

			expect(item).toBeVisible();
		});
	});
});
