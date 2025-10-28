/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render} from '@testing-library/react';
import React from 'react';
import {DndProvider} from 'react-dnd';
import {HTML5Backend} from 'react-dnd-html5-backend';

import {ColumnWithControls} from '../../../../../src/main/resources/META-INF/resources/page_editor/app/components/layout_data_items';
import {getNextLayoutData} from '../../../../../src/main/resources/META-INF/resources/page_editor/app/components/layout_data_items/ColumnWithControls';
import {LAYOUT_DATA_ITEM_TYPES} from '../../../../../src/main/resources/META-INF/resources/page_editor/app/config/constants/layoutDataItemTypes';
import {VIEWPORT_SIZES} from '../../../../../src/main/resources/META-INF/resources/page_editor/app/config/constants/viewportSizes';
import {
	ControlsProvider,
	useSelectItem,
} from '../../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/ControlsContext';
import {StoreAPIContextProvider} from '../../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/StoreContext';

const LAYOUT_DATA = {
	deletedItems: [],
	items: {
		'column-1': {
			config: {
				mobile: {},
				size: 4,
				tablet: {size: 5},
			},
		},
		'column-2': {
			config: {
				mobile: {},
				size: 4,
				tablet: {size: 3},
			},
		},
		'column-3': {
			config: {
				mobile: {},
				size: 4,
				tablet: {},
			},
		},
	},
};

const renderColumn = ({
	activeItemId = 'row',
	columnIndex = 1,
	hasUpdatePermissions = true,
	lockedExperience = false,
} = {}) => {
	const columnA = {
		children: [],
		config: {},
		itemId: 'columnA',
		parentId: 'row',
		type: LAYOUT_DATA_ITEM_TYPES.column,
	};

	const columnB = {
		children: [],
		config: {},
		itemId: 'columnB',
		parentId: 'row',
		type: LAYOUT_DATA_ITEM_TYPES.column,
	};

	const row = {
		children: ['columnA', 'columnB'],
		config: {},
		itemId: 'row',
		parentId: null,
		type: LAYOUT_DATA_ITEM_TYPES.row,
	};

	const layoutData = {
		items: {columnA, columnB, row},
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
						layoutData,
						permissions: {
							LOCKED_SEGMENTS_EXPERIMENT: lockedExperience,
							UPDATE: hasUpdatePermissions,
						},
						selectedViewportSize: VIEWPORT_SIZES.desktop,
					})}
				>
					<AutoSelect />

					<ColumnWithControls
						item={columnIndex ? columnB : columnA}
					/>
				</StoreAPIContextProvider>
			</ControlsProvider>
		</DndProvider>
	);
};

describe('ColumnWithControls', () => {
	it('hides resize handler if parent row is not active', () => {
		const {queryByTitle} = renderColumn({activeItemId: 'nothing'});

		expect(queryByTitle('resize-column')).toBe(null);
	});

	it('hides resize handler if user has no permissions', () => {
		const {queryByTitle} = renderColumn({hasUpdatePermissions: false});

		expect(queryByTitle('resize-column')).toBe(null);
	});

	it('hides resize handler for first column', () => {
		const {queryByTitle} = renderColumn({columnIndex: 0});

		expect(queryByTitle('resize-column')).toBe(null);
	});

	describe('updateNewLayoutDataContext', () => {
		it('allows changing module width for a viewport', async () => {
			const nextSizes = {'column-1': 2, 'column-2': 6};

			expect(
				getNextLayoutData(LAYOUT_DATA, nextSizes, 'desktop')
			).toEqual({
				...LAYOUT_DATA,
				items: {
					...LAYOUT_DATA.items,
					'column-1': {
						config: {
							mobile: {},
							size: 2,
							tablet: {size: 5},
						},
					},
					'column-2': {
						config: {
							mobile: {},
							size: 6,
							tablet: {size: 3},
						},
					},
				},
			});
		});

		it('allows changing module without affecting previous viewports', async () => {
			const nextSizes = {'column-1': 2, 'column-2': 6};

			expect(getNextLayoutData(LAYOUT_DATA, nextSizes, 'mobile')).toEqual(
				{
					...LAYOUT_DATA,
					items: {
						...LAYOUT_DATA.items,
						'column-1': {
							config: {
								mobile: {size: 2},
								size: 4,
								tablet: {size: 5},
							},
						},
						'column-2': {
							config: {
								mobile: {size: 6},
								size: 4,
								tablet: {size: 3},
							},
						},
					},
				}
			);
		});

		it('allows changing previously modified module without affecting the rest of viewports', async () => {
			const nextSizes = {'column-1': 1, 'column-2': 7};

			expect(getNextLayoutData(LAYOUT_DATA, nextSizes, 'tablet')).toEqual(
				{
					...LAYOUT_DATA,
					items: {
						...LAYOUT_DATA.items,
						'column-1': {
							config: {
								mobile: {},
								size: 4,
								tablet: {size: 1},
							},
						},
						'column-2': {
							config: {
								mobile: {},
								size: 4,
								tablet: {size: 7},
							},
						},
					},
				}
			);
		});
	});
});
