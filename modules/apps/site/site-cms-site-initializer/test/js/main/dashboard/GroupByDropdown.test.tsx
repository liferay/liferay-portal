/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {fireEvent, render, screen, waitFor} from '@testing-library/react';
import React from 'react';

import {ViewDashboardContextProvider} from '../../../../src/main/resources/META-INF/resources/js/main/dashboard/ViewDashboardContext';
import {Item} from '../../../../src/main/resources/META-INF/resources/js/main/dashboard/components/FilterDropdown';
import {
	GroupByDropdown,
	IStructureProps,
} from '../../../../src/main/resources/META-INF/resources/js/main/dashboard/components/GroupByDropdown';

const mockFetch = (data: IStructureProps) => {
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
	{label: 'structure-label', value: 'structure'},
];

const WrappedComponent = ({
	initialItem,
	onSelectItem,
	setStructureTypeData,
}: {
	initialItem: Item;
	onSelectItem: (item: Item) => void;
	setStructureTypeData: (data: IStructureProps) => void;
}) => (
	<ViewDashboardContextProvider value={mockContextValue}>
		<GroupByDropdown
			item={initialItem}
			onSelectItem={onSelectItem}
			setStructureTypeData={setStructureTypeData}
		/>
	</ViewDashboardContextProvider>
);

describe('[CMS Dashboard] Components: GroupByDropdown - All Options', () => {
	afterEach(() => {
		jest.clearAllMocks();
	});

	const mockData: IStructureProps = {
		items: [
			{count: 10, key: '1', title: 'Item 1'},
			{count: 20, key: '2', title: 'Item 2'},
		],
		totalCount: 30,
	};

	test.each(structureTypes)(
		'renders and handles selection for "%s"',
		async (item) => {
			mockFetch(mockData);

			const onSelectItem = jest.fn();
			const setStructureTypeData = jest.fn();

			render(
				<WrappedComponent
					initialItem={item}
					onSelectItem={onSelectItem}
					setStructureTypeData={setStructureTypeData}
				/>
			);

			await waitFor(() =>
				expect(setStructureTypeData).toHaveBeenCalledWith(mockData)
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
