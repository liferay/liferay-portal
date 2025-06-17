/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {
	fireEvent,
	render,
	screen,
	waitFor,
	waitForElementToBeRemoved,
} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import {ViewDashboardContextProvider} from '../../../../src/main/resources/META-INF/resources/js/main/dashboard/ViewDashboardContext';
import {AllStructureTypesDropdown} from '../../../../src/main/resources/META-INF/resources/js/main/dashboard/components/AllStructureTypesDropdown';
import {Item} from '../../../../src/main/resources/META-INF/resources/js/main/dashboard/components/FilterDropdown';
import {initialStructure} from '../../../../src/main/resources/META-INF/resources/js/main/dashboard/components/InventoryAnalysisCard';

const mockStructures = (items: {id: string; name: string}[] = []) => {
	global.fetch = jest.fn().mockReturnValue({
		json: jest.fn().mockReturnValue({items}),
		ok: true,
	});
};

const WrappedComponent = ({
	onSelectItem,
}: {
	onSelectItem: (item: Item) => void;
}) => {
	const [selectedItem, setSelectedItem] =
		React.useState<Item>(initialStructure);

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
	afterEach(() => {
		jest.clearAllMocks();
	});

	it('renders correctly', async () => {
		mockStructures();

		const onSelectItem = jest.fn();

		render(<WrappedComponent onSelectItem={onSelectItem} />);

		const button = screen.getByRole('button', {
			name: 'all-structures',
		});

		expect(button).toBeInTheDocument();

		fireEvent.click(button);

		expect(
			screen.queryByText('filter-by-structure-type')
		).toBeInTheDocument();

		expect(screen.queryByPlaceholderText('search')).toBeInTheDocument();

		await waitForElementToBeRemoved(() => screen.getByTestId('loading'));

		expect(screen.getAllByRole('menuitem').length).toBe(1);

		const menuitem = screen.getByRole('menuitem', {
			name: 'all-structures',
		});

		expect(menuitem).toBeInTheDocument();

		fireEvent.click(menuitem);

		expect(onSelectItem).toHaveBeenCalledTimes(1);

		expect(onSelectItem).toHaveBeenCalledWith({
			label: 'all-structures',
			value: 'all',
		});
	});

	it('renders a structure list', async () => {
		mockStructures([
			{
				id: '01',
				name: 'structure 01',
			},
			{
				id: '02',
				name: 'structure 02',
			},
		]);

		render(<WrappedComponent onSelectItem={jest.fn()} />);

		const button = screen.getByRole('button', {
			name: 'all-structures',
		});

		fireEvent.click(button);

		await waitForElementToBeRemoved(() => screen.getByTestId('loading'));

		expect(screen.getAllByRole('menuitem').length).toBe(3);

		expect(
			screen.queryByRole('menuitem', {name: 'all-structures'})
		).toBeInTheDocument();

		expect(
			screen.queryByRole('menuitem', {name: 'structure 01'})
		).toBeInTheDocument();

		expect(
			screen.queryByRole('menuitem', {name: 'structure 02'})
		).toBeInTheDocument();
	});

	xit('search by a structure and returns a filtered result', async () => {
		mockStructures([
			{id: '01', name: 'structure 01'},
			{id: '02', name: 'structure 02'},
		]);

		render(<WrappedComponent onSelectItem={jest.fn()} />);

		const structuresDropdownButton = screen.getByRole('button', {
			name: 'all-structures',
		});

		fireEvent.click(structuresDropdownButton);

		await waitForElementToBeRemoved(() => screen.getByTestId('loading'));

		expect(screen.getAllByRole('menuitem').length).toBe(3);

		mockStructures([{id: '02', name: 'structure 02'}]);

		await userEvent.type(
			screen.getByPlaceholderText('search'),
			'structure 02'
		);

		await waitFor(
			() => {
				expect(screen.getAllByRole('menuitem').length).toBe(1);

				expect(
					screen.queryByRole('menuitem', {name: 'all-structures'})
				).not.toBeInTheDocument();

				expect(
					screen.queryByRole('menuitem', {name: 'structure 01'})
				).not.toBeInTheDocument();

				expect(
					screen.queryByRole('menuitem', {name: 'structure 02'})
				).toBeInTheDocument();
			},
			{timeout: 100}
		);
	});

	xit('search by a structure and returns a empty result', async () => {
		mockStructures([
			{id: '01', name: 'structure 01'},
			{id: '02', name: 'structure 02'},
		]);

		render(<WrappedComponent onSelectItem={jest.fn()} />);

		const structuresDropdownButton = screen.getByRole('button', {
			name: 'all-structures',
		});

		fireEvent.click(structuresDropdownButton);

		await waitForElementToBeRemoved(() => screen.getByTestId('loading'));

		expect(screen.getAllByRole('menuitem').length).toBe(3);

		mockStructures();

		await userEvent.type(screen.getByPlaceholderText('search'), 'empty?');

		await waitFor(
			() => {
				expect(screen.getAllByRole('menuitem').length).toBe(1);

				expect(
					screen.queryByRole('menuitem', {name: 'all-structures'})
				).not.toBeInTheDocument();

				expect(
					screen.queryByRole('menuitem', {
						name: 'no-filters-were-found',
					})
				).toBeInTheDocument();
			},
			{timeout: 100}
		);
	});

	it('selects a new strucuture', async () => {
		mockStructures([
			{id: '01', name: 'structure 01'},
			{id: '02', name: 'structure 02'},
		]);

		render(<WrappedComponent onSelectItem={() => {}} />);

		expect(screen.getByTestId('structures')).toHaveTextContent(
			'all-structures'
		);

		fireEvent.click(screen.getByTestId('structures'));

		await waitForElementToBeRemoved(() => screen.getByTestId('loading'));

		fireEvent.click(screen.getByRole('menuitem', {name: 'structure 02'}));

		expect(screen.getByTestId('structures')).toHaveTextContent(
			'structure 02'
		);
	});
});
