/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {
	fireEvent,
	render,
	screen,
	waitFor,
	within,
} from '@testing-library/react';
import React from 'react';

import ApiHelper from '../../../../src/main/resources/META-INF/resources/js/common/services/ApiHelper';
import {AllStructureTypesDropdown} from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/common/filters/AllStructureTypesDropdown';
import {Item} from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/common/filters/FilterDropdown';
import {initialFilters} from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/common/filters/filters';

const WrappedComponent = ({
	onSelectItem,
}: {
	onSelectItem: (item: Item) => void;
}) => {
	const [selectedItem, setSelectedItem] = React.useState<Item>(
		initialFilters.structure
	);

	return (
		<AllStructureTypesDropdown
			ercContentStructures="CONTENT_STRUCTURES"
			ercFileTypes="FILE_TYPES"
			item={selectedItem}
			onSelectItem={(item) => {
				setSelectedItem(item);
				onSelectItem(item);
			}}
		/>
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

		const trigger = screen.getByRole('combobox', {
			name: 'filter-by-content-structure-type',
		});

		expect(trigger).toHaveTextContent('all-content-structures');

		fireEvent.click(trigger);

		expect(screen.getByPlaceholderText('search')).toBeInTheDocument();

		const listbox = await screen.findByRole('listbox');

		const option = within(listbox).getByRole('option', {
			name: 'all-content-structures',
		});

		expect(within(listbox).getAllByRole('option')).toHaveLength(1);

		fireEvent.click(option);

		expect(onSelectItem).toHaveBeenCalledWith(
			expect.objectContaining({
				label: 'all-content-structures',
				value: 'all',
			})
		);
	});

	it('renders a structure list', async () => {
		jest.spyOn(ApiHelper, 'get').mockResolvedValue({
			data: mockStructureTypesApiResponse,
			error: null,
		});

		render(<WrappedComponent onSelectItem={jest.fn()} />);

		fireEvent.click(
			screen.getByRole('combobox', {
				name: 'filter-by-content-structure-type',
			})
		);

		const listbox = await screen.findByRole('listbox');

		expect(
			await within(listbox).findByRole('option', {name: 'structure 01'})
		).toBeInTheDocument();

		expect(
			within(listbox).getByRole('option', {name: 'structure 02'})
		).toBeInTheDocument();

		expect(
			within(listbox).getByRole('option', {
				name: 'all-content-structures',
			})
		).toBeInTheDocument();

		expect(within(listbox).getAllByRole('option')).toHaveLength(3);
	});

	it('searches by structure name and returns a filtered result', async () => {
		jest.spyOn(ApiHelper, 'get').mockResolvedValue({
			data: mockStructureTypesApiResponse,
			error: null,
		});

		render(<WrappedComponent onSelectItem={jest.fn()} />);

		fireEvent.click(
			screen.getByRole('combobox', {
				name: 'filter-by-content-structure-type',
			})
		);

		const listbox = await screen.findByRole('listbox');

		await within(listbox).findByRole('option', {name: 'structure 02'});

		fireEvent.change(screen.getByPlaceholderText('search'), {
			target: {value: 'structure 02'},
		});

		await waitFor(() =>
			expect(within(listbox).getAllByRole('option')).toHaveLength(1)
		);

		expect(
			within(listbox).getByRole('option', {name: 'structure 02'})
		).toBeInTheDocument();

		expect(
			within(listbox).queryByRole('option', {name: 'structure 01'})
		).not.toBeInTheDocument();

		expect(
			within(listbox).queryByRole('option', {
				name: 'all-content-structures',
			})
		).not.toBeInTheDocument();
	});

	it('shows a message when no structure matches the search', async () => {
		jest.spyOn(ApiHelper, 'get').mockResolvedValue({
			data: mockStructureTypesApiResponse,
			error: null,
		});

		render(<WrappedComponent onSelectItem={jest.fn()} />);

		fireEvent.click(
			screen.getByRole('combobox', {
				name: 'filter-by-content-structure-type',
			})
		);

		const listbox = await screen.findByRole('listbox');

		await within(listbox).findByRole('option', {name: 'structure 02'});

		fireEvent.change(screen.getByPlaceholderText('search'), {
			target: {value: 'no-match'},
		});

		await waitFor(() =>
			expect(
				within(listbox).queryByRole('option')
			).not.toBeInTheDocument()
		);

		expect(
			within(listbox).getByText('no-results-were-found')
		).toBeInTheDocument();
	});

	it('selects a new structure', async () => {
		jest.spyOn(ApiHelper, 'get').mockResolvedValue({
			data: mockStructureTypesApiResponse,
			error: null,
		});

		render(<WrappedComponent onSelectItem={jest.fn()} />);

		const trigger = screen.getByRole('combobox', {
			name: 'filter-by-content-structure-type',
		});

		fireEvent.click(trigger);

		const listbox = await screen.findByRole('listbox');

		fireEvent.click(
			await within(listbox).findByRole('option', {name: 'structure 02'})
		);

		await waitFor(() => expect(trigger).toHaveTextContent('structure 02'));
	});
});
