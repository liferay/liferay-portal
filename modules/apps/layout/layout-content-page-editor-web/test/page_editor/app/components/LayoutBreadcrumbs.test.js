/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import React, {useEffect} from 'react';
import {DndProvider} from 'react-dnd';
import {HTML5Backend} from 'react-dnd-html5-backend';

import {LayoutBreadcrumbs} from '../../../../src/main/resources/META-INF/resources/page_editor/app/components/LayoutBreadcrumbs';
import {LAYOUT_DATA_ITEM_TYPES} from '../../../../src/main/resources/META-INF/resources/page_editor/app/config/constants/layoutDataItemTypes';
import {
	ControlsProvider,
	useActivateMultiSelect,
	useSelectItem,
	useSelectMultipleItems,
} from '../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/ControlsContext';
import StoreMother from '../../../../src/main/resources/META-INF/resources/page_editor/test_utils/StoreMother';

const AutoSelect = ({itemId, multiSelect = null}) => {
	useActivateMultiSelect()(multiSelect);

	const selectMultipleItems = useSelectMultipleItems();
	const selectItem = useSelectItem();

	const select = multiSelect ? selectMultipleItems : selectItem;

	useEffect(() => {
		if (itemId) {
			select(itemId);
		}
	}, [itemId, select]);

	return null;
};

const renderComponent = ({
	multiSelect = null,
	initialActiveItemIds = [],
	activeItemId,
}) => {
	return render(
		<StoreMother.Component
			getState={() => ({
				fragmentEntryLinks: {
					'item-1-fragment': {
						name: 'Item 1',
					},
					'item-2-fragment': {
						name: 'Item 2',
					},
					'item-3-fragment': {
						name: 'Item 3',
					},
					'item-4-fragment': {
						name: 'Item 4',
					},
				},
				layoutData: {
					deletedItems: [],
					items: {
						'column': {
							children: ['item-4'],
							config: {},
							itemId: 'column',
							parentId: 'grid',
							type: LAYOUT_DATA_ITEM_TYPES.column,
						},
						'grid': {
							children: ['column'],
							config: {},
							itemId: 'grid',
							type: LAYOUT_DATA_ITEM_TYPES.row,
						},
						'item-1': {
							children: ['item-2'],
							config: {
								fragmentEntryLinkId: 'item-1-fragment',
							},
							itemId: 'item-1',
							type: LAYOUT_DATA_ITEM_TYPES.fragment,
						},
						'item-2': {
							children: ['item-3'],
							config: {
								fragmentEntryLinkId: 'item-2-fragment',
							},
							itemId: 'item-2',
							parentId: 'item-1',
							type: LAYOUT_DATA_ITEM_TYPES.fragment,
						},
						'item-3': {
							children: [],
							config: {
								fragmentEntryLinkId: 'item-3-fragment',
							},
							itemId: 'item-3',
							parentId: 'item-2',
							type: LAYOUT_DATA_ITEM_TYPES.fragment,
						},
						'item-4': {
							children: [],
							config: {
								fragmentEntryLinkId: 'item-4-fragment',
							},
							itemId: 'item-4',
							parentId: 'column',
							type: LAYOUT_DATA_ITEM_TYPES.fragment,
						},
					},
				},
			})}
		>
			<DndProvider backend={HTML5Backend}>
				<ControlsProvider
					activeInitialState={{
						activeItemIds: initialActiveItemIds,
					}}
				>
					<AutoSelect
						itemId={activeItemId || initialActiveItemIds[0]}
						multiSelect={multiSelect}
					/>

					<LayoutBreadcrumbs />
				</ControlsProvider>
			</DndProvider>
		</StoreMother.Component>
	);
};

describe('LayoutBreadcrumbs', () => {
	beforeAll(() => {
		const wrapper = document.createElement('div');
		wrapper.setAttribute('id', 'wrapper');
		document.body.appendChild(wrapper);
	});

	afterAll(() => {
		const wrapper = document.getElementById('wrapper');
		document.body.removeChild(wrapper);
	});

	it('renders item in breadcrumbs when selecting it', () => {
		renderComponent({initialActiveItemIds: ['item-1']});

		expect(screen.getByText('Item 1')).toBeInTheDocument();
	});

	it('renders ancestors in breadcrumbs when selecting an item', () => {
		renderComponent({initialActiveItemIds: ['item-3']});

		expect(screen.getByText('Item 3')).toBeInTheDocument();
		expect(screen.getByText('Item 2')).toBeInTheDocument();
		expect(screen.getByText('Item 1')).toBeInTheDocument();
	});

	it('does not render children in breadcrumbs when selecting an item', () => {
		renderComponent({initialActiveItemIds: ['item-2']});

		expect(screen.queryByText('Item 3')).not.toBeInTheDocument();
	});

	it('renders column as Module in breadcrumbs even if they are in the path', () => {
		renderComponent({initialActiveItemIds: ['item-4']});

		expect(screen.getByText('Item 4')).toBeInTheDocument();
		expect(screen.getByText('grid')).toBeInTheDocument();
		expect(screen.getByText('module')).toBeInTheDocument();
	});

	it('does not render anything when multiple elements are selected', async () => {
		renderComponent({
			activeItemId: 'item-4',
			initialActiveItemIds: ['item-1'],
			multiSelect: 'simple',
		});

		expect(screen.queryByText('Item 1')).not.toBeInTheDocument();
		expect(screen.queryByText('Item 4')).not.toBeInTheDocument();
	});
});
