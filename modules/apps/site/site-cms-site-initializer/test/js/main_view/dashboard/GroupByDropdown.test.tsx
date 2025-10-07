/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {fireEvent, render, screen} from '@testing-library/react';
import React from 'react';

import {ViewDashboardContextProvider} from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/ViewDashboardContext';
import {Item} from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/components/FilterDropdown';
import {GroupByDropdown} from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/components/GroupByDropdown';
import {InventoryAnalysisDataType} from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/components/InventoryAnalysisCard';

const mockFetch = (data: InventoryAnalysisDataType) => {
	global.fetch = jest.fn().mockResolvedValue({
		json: async () => data,
		ok: true,
	});
};

const mockContextValue = {
	changeLanguage: jest.fn(),
	changeSpace: jest.fn(),
	constants: {},
	filters: {
		language: {
			label: 'English',
			value: 'en-US',
		},
		space: {
			label: 'Test Space',
			value: '123',
		},
	},
};

const structureTypes: Item[] = [
	{label: 'category', value: 'category'},
	{label: 'vocabulary', value: 'vocabulary'},
	{label: 'tag', value: 'tag'},
	{label: 'content-structure-label', value: 'structure'},
];

const WrappedComponent = ({
	initialItem,
	onSelectItem,
}: {
	initialItem: Item;
	onSelectItem: (item: Item) => void;
}) => (
	<ViewDashboardContextProvider value={mockContextValue}>
		<GroupByDropdown item={initialItem} onSelectItem={onSelectItem} />
	</ViewDashboardContextProvider>
);

describe('[CMS Dashboard] Components: GroupByDropdown - All Options', () => {
	afterEach(() => {
		jest.clearAllMocks();
	});

	const mockData: InventoryAnalysisDataType = {
		inventoryAnalysisItems: [
			{count: 10, key: '1', title: 'Item 1'},
			{count: 20, key: '2', title: 'Item 2'},
		],
		page: 1,
		pageSize: 20,
		totalCount: 30,
	};

	test.each(structureTypes)(
		'renders and handles selection for "%s"',
		async (item) => {
			mockFetch(mockData);

			const onSelectItem = jest.fn();

			render(
				<WrappedComponent
					initialItem={item}
					onSelectItem={onSelectItem}
				/>
			);

			const button = screen.getByRole('button', {
				name: item.label,
			});
			expect(button).toBeInTheDocument();

			fireEvent.click(button);

			const menuitem = screen.getByRole('menuitem', {
				name: item.label,
			});
			expect(menuitem).toBeInTheDocument();

			fireEvent.click(menuitem);

			expect(onSelectItem).toHaveBeenCalledWith(item);
		}
	);
});
