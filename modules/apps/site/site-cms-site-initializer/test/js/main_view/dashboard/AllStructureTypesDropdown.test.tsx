/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {
	act,
	fireEvent,
	render,
	screen,
	waitFor,
	waitForElementToBeRemoved,
} from '@testing-library/react';
import React from 'react';

import ApiHelper from '../../../../src/main/resources/META-INF/resources/js/common/services/ApiHelper';
import {ViewDashboardContextProvider} from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/ViewDashboardContext';
import {AllStructureTypesDropdown} from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/components/AllStructureTypesDropdown';
import {Item} from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/components/FilterDropdown';
import {initialFilters} from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/components/InventoryAnalysisCard';

const WrappedComponent = ({
	onSelectItem,
}: {
	onSelectItem: (item: Item) => void;
}) => {
	const [selectedItem, setSelectedItem] = React.useState<Item>(
		initialFilters.structure
	);

	return (
		<ViewDashboardContextProvider value={{}}>
			<AllStructureTypesDropdown
				item={selectedItem}
				onSelectItem={(item) => {
					setSelectedItem(item);
					onSelectItem(item);
				}}
			/>
		</ViewDashboardContextProvider>
	);
};

describe('[CMS Dashboard] Components: AllStructureTypesDropdown', () => {
	const mockStructureTypesApiResponse = {
		items: [
			{
				id: '01',
				label: {
					en_US: 'structure 01',
				},
			},
			{
				id: '02',
				label: {
					en_US: 'structure 02',
				},
			},
		],
	};

	afterEach(() => {
		jest.clearAllMocks();
		jest.restoreAllMocks();
	});

	it('renders correctly', async () => {
		jest.spyOn(ApiHelper, 'get').mockResolvedValue({
			data: {items: []},
			error: null,
		});

		const onSelectItem = jest.fn();

		render(<WrappedComponent onSelectItem={onSelectItem} />);

		const button = screen.getByRole('button', {
			name: 'all-content-structures',
		});

		expect(button).toBeInTheDocument();

		fireEvent.click(button);

		expect(
			screen.queryByText('filter-by-content-structure-type')
		).toBeInTheDocument();

		expect(screen.queryByPlaceholderText('search')).toBeInTheDocument();

		await waitForElementToBeRemoved(() => screen.getByTestId('loading'));

		expect(screen.getAllByRole('menuitem').length).toBe(1);

		const menuitem = screen.getByRole('menuitem', {
			name: 'all-content-structures',
		});

		expect(menuitem).toBeInTheDocument();

		fireEvent.click(menuitem);

		expect(onSelectItem).toHaveBeenCalledTimes(1);

		expect(onSelectItem).toHaveBeenCalledWith({
			label: 'all-content-structures',
			value: 'all',
		});
	});

	it('renders a structure list', async () => {
		jest.spyOn(ApiHelper, 'get').mockResolvedValue({
			data: mockStructureTypesApiResponse,
			error: null,
		});

		render(<WrappedComponent onSelectItem={jest.fn()} />);

		const button = screen.getByRole('button', {
			name: 'all-content-structures',
		});

		fireEvent.click(button);

		await waitForElementToBeRemoved(() => screen.getByTestId('loading'));

		expect(screen.getAllByRole('menuitem').length).toBe(3);

		expect(
			screen.queryByRole('menuitem', {name: 'all-content-structures'})
		).toBeInTheDocument();

		expect(
			screen.queryByRole('menuitem', {name: 'structure 01'})
		).toBeInTheDocument();

		expect(
			screen.queryByRole('menuitem', {name: 'structure 02'})
		).toBeInTheDocument();
	});

	it('searches by structure name and returns a filtered result', async () => {
		jest.useFakeTimers();

		jest.spyOn(ApiHelper, 'get')
			.mockResolvedValueOnce({
				data: mockStructureTypesApiResponse,
				error: null,
			})
			.mockResolvedValueOnce({
				data: {
					items: [
						{
							id: '02',
							label: {
								en_US: 'structure 02',
							},
						},
					],
				},
				error: null,
			});

		render(<WrappedComponent onSelectItem={jest.fn()} />);

		const dropdownButton = screen.getByRole('button', {
			name: 'all-content-structures',
		});

		fireEvent.click(dropdownButton);

		await waitForElementToBeRemoved(() => screen.getByTestId('loading'));

		expect(screen.getAllByRole('menuitem').length).toBe(3);

		await act(async () => {
			fireEvent.change(screen.getByPlaceholderText('search'), {
				target: {value: 'structure 02'},
			});

			jest.advanceTimersByTime(300);
		});

		await waitFor(() => {
			expect(screen.getAllByRole('menuitem').length).toBe(1);

			expect(
				screen.getByRole('menuitem', {name: 'structure 02'})
			).toBeInTheDocument();
		});

		expect(
			screen.queryByRole('menuitem', {name: 'structure 01'})
		).not.toBeInTheDocument();

		expect(
			screen.queryByRole('menuitem', {name: 'all-content-structures'})
		).not.toBeInTheDocument();

		jest.useRealTimers();
	});

	it('search by a structure and returns a empty result', async () => {
		jest.useFakeTimers();

		jest.spyOn(ApiHelper, 'get')
			.mockResolvedValueOnce({
				data: mockStructureTypesApiResponse,
				error: null,
			})
			.mockResolvedValueOnce({
				data: {
					items: [],
				},
				error: null,
			});

		render(<WrappedComponent onSelectItem={jest.fn()} />);

		const structuresDropdownButton = screen.getByRole('button', {
			name: 'all-content-structures',
		});

		fireEvent.click(structuresDropdownButton);

		await waitForElementToBeRemoved(() => screen.getByTestId('loading'));

		expect(screen.getAllByRole('menuitem').length).toBe(3);

		global.fetch = jest.fn().mockResolvedValue({
			json: jest.fn().mockResolvedValue({items: []}),
			ok: true,
		});

		await act(async () => {
			fireEvent.change(screen.getByPlaceholderText('search'), {
				target: {
					value: 'empty?',
				},
			});

			jest.advanceTimersByTime(300);
		});

		await waitFor(() => {
			expect(screen.getAllByRole('menuitem').length).toBe(1);

			expect(
				screen.queryByRole('menuitem', {
					name: 'no-filters-were-found',
				})
			).toBeInTheDocument();
		});

		expect(
			screen.queryByRole('menuitem', {name: 'all-content-structures'})
		).not.toBeInTheDocument();

		jest.useRealTimers();
	});

	it('selects a new strucuture', async () => {
		jest.spyOn(ApiHelper, 'get').mockResolvedValue({
			data: mockStructureTypesApiResponse,
			error: null,
		});

		render(<WrappedComponent onSelectItem={() => {}} />);

		expect(screen.getByTestId('structures')).toHaveTextContent(
			'all-content-structures'
		);

		fireEvent.click(screen.getByTestId('structures'));

		await waitForElementToBeRemoved(() => screen.getByTestId('loading'));

		fireEvent.click(screen.getByRole('menuitem', {name: 'structure 02'}));

		expect(screen.getByTestId('structures')).toHaveTextContent(
			'structure 02'
		);
	});
});
