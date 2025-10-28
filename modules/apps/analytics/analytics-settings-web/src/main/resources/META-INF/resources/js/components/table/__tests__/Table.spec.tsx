/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import fetch from 'jest-fetch-mock';

import '@testing-library/jest-dom';
import {act, fireEvent, render, screen, within} from '@testing-library/react';
import React from 'react';

import {loadingElement, mockResponse} from '../../../utils/__tests__/helpers';
import {
	fetchTableData,
	fetchTableDataResponse,
} from '../../../utils/__tests__/mocks';
import Table, {ITableProps} from '../Table';
import {TColumn} from '../types';

type TRawItem = {
	id: string;
	name: string;
};

const HEADER: TColumn[] = [
	{
		expanded: true,
		id: 'firstName',
		label: 'First Name',
	},
	{
		expanded: true,
		id: 'lastName',
		label: 'Last Name',
	},
	{
		expanded: true,
		id: 'age',
		label: 'Age',
	},
];

const DEFAULT_ITEMS = [
	{
		age: 19,
		firstName: 'Andre',
		id: '8189001028599808',
		lastName: 'Patton',
	},
	{
		age: 76,
		firstName: 'Jayden',
		id: '6317360201859072',
		lastName: 'Holloway',
	},
	{
		age: 63,
		firstName: 'Etta',
		id: '7304891437416448',
		lastName: 'Garrett',
	},
	{
		age: 21,
		firstName: 'Eugenia',
		id: '6979819478712320',
		lastName: 'Rios',
	},
	{
		age: 33,
		firstName: 'Earl',
		id: '8324407053254656',
		lastName: 'Medina',
	},
];

const COLUMNS = HEADER.map(({id}) => id) as Array<keyof TRawItem>;

const EMPTY_STATE = {
	noResultsTitle: 'no items were found',
	title: 'there are no items',
};

const WrappedTable: React.FC<
	{children?: React.ReactNode | undefined} & Partial<ITableProps<TRawItem>>
> = (props) => {
	return (
		<Table<TRawItem>
			columns={HEADER}
			emptyState={EMPTY_STATE}
			mapperItems={(items: TRawItem[]) => {
				return items.map((item) => {
					return {
						checked: false,
						columns: COLUMNS.map((column) => ({
							id: column,
							value: item[column],
						})),
						disabled: false,
						id: item.id,
					};
				});
			}}
			requestFn={fetchTableData}
			{...props}
		/>
	);
};

describe('Table', () => {
	afterEach(() => {
		jest.restoreAllMocks();
	});

	it('renders table without crashing', async () => {
		fetch.mockReturnValueOnce(
			mockResponse(fetchTableDataResponse(DEFAULT_ITEMS))
		);

		render(<WrappedTable />);

		await loadingElement();

		const column1 = screen.getByTestId(/Andre/i);

		expect(within(column1).getByText(/Andre/i)).toBeInTheDocument();
		expect(within(column1).getByText(/Patton/i)).toBeInTheDocument();
		expect(within(column1).getByText(/19/i)).toBeInTheDocument();

		const column2 = screen.getByTestId(/Jayden/i);

		expect(within(column2).getByText(/Jayden/i)).toBeInTheDocument();
		expect(within(column2).getByText(/Holloway/i)).toBeInTheDocument();
		expect(within(column2).getByText(/76/i)).toBeInTheDocument();

		const column3 = screen.getByTestId(/Etta/i);

		expect(within(column3).getByText(/Etta/i)).toBeInTheDocument();
		expect(within(column3).getByText(/Garrett/i)).toBeInTheDocument();
		expect(within(column3).getByText(/63/i)).toBeInTheDocument();

		const column4 = screen.getByTestId(/Eugenia/i);

		expect(within(column4).getByText(/Eugenia/i)).toBeInTheDocument();
		expect(within(column4).getByText(/Rios/i)).toBeInTheDocument();
		expect(within(column4).getByText(/21/i)).toBeInTheDocument();

		const column5 = screen.getByTestId(/Earl/i);

		expect(within(column5).getByText(/Earl/i)).toBeInTheDocument();
		expect(within(column5).getByText(/Medina/i)).toBeInTheDocument();
		expect(within(column5).getByText(/33/i)).toBeInTheDocument();
	});

	it('renders a button with a title "Add item", and that clicking the button calls the onAddItem callback prop', async () => {
		fetch.mockReturnValueOnce(
			mockResponse(fetchTableDataResponse(DEFAULT_ITEMS))
		);

		const onAddItem = jest.fn();

		const {rerender} = render(<WrappedTable />);

		await loadingElement();

		expect(screen.queryByTitle(/add-item/i)).toBeNull();

		rerender(<WrappedTable onAddItem={onAddItem} />);

		expect(screen.getByTitle(/add-item/i)).toBeInTheDocument();
		expect(onAddItem).not.toHaveBeenCalled();

		fireEvent.click(screen.getByTitle(/add-item/i));

		expect(onAddItem).toHaveBeenCalled();
	});

	it('renders a button with a custom title "Add New User", and that clicking the button calls the onAddItem callback prop', async () => {
		fetch.mockResponseOnce(
			JSON.stringify(fetchTableDataResponse(DEFAULT_ITEMS))
		);

		const onAddItem = jest.fn();

		render(
			<WrappedTable addItemTitle="Add New User" onAddItem={onAddItem} />
		);

		await loadingElement();

		const button = screen.getByTitle(/Add New User/i);

		expect(button).toBeInTheDocument();
		expect(onAddItem).not.toHaveBeenCalled();

		fireEvent.click(button);

		expect(onAddItem).toHaveBeenCalled();
	});

	it('renders table with specific parts disabled', async () => {
		fetch.mockReturnValueOnce(
			mockResponse(fetchTableDataResponse(DEFAULT_ITEMS))
		);

		const {rerender} = render(<WrappedTable />);

		await loadingElement();

		const row1 = screen.getByTestId(/Andre/i);
		const row2 = screen.getByTestId(/Jayden/i);
		const row3 = screen.getByTestId(/Etta/i);
		const row4 = screen.getByTestId(/Eugenia/i);
		const row5 = screen.getByTestId(/Earl/i);

		expect(screen.getByTestId(/globalCheckbox/i)).toBeEnabled();
		expect(
			screen.getByRole('button', {name: /filter-and-order/i})
		).toBeEnabled();
		expect(screen.getByRole('button', {name: /sort/i})).toBeEnabled();
		expect(screen.getByRole('textbox', {name: /search/i})).toBeEnabled();
		expect(screen.getByRole('button', {name: /search/i})).toBeEnabled();
		expect(screen.getByRole('table').getAttribute('class')).toMatch(
			/table-hover/gi
		);

		expect(within(row1).getByRole('cell')).not.toHaveClass('text-muted');
		expect(within(row1).getByRole('checkbox')).toBeEnabled();

		expect(within(row2).getByRole('cell')).not.toHaveClass('text-muted');
		expect(within(row2).getByRole('checkbox')).toBeEnabled();

		expect(within(row3).getByRole('cell')).not.toHaveClass('text-muted');
		expect(within(row3).getByRole('checkbox')).toBeEnabled();

		expect(within(row4).getByRole('cell')).not.toHaveClass('text-muted');
		expect(within(row4).getByRole('checkbox')).toBeEnabled();

		expect(within(row5).getByRole('cell')).not.toHaveClass('text-muted');
		expect(within(row5).getByRole('checkbox')).toBeEnabled();

		rerender(<WrappedTable disabled />);

		expect(screen.getByTestId(/globalCheckbox/i)).toBeDisabled();
		expect(
			screen.getByRole('button', {name: /filter-and-order/i})
		).toBeDisabled();
		expect(screen.getByRole('button', {name: /sort/i})).toBeDisabled();
		expect(screen.getByRole('textbox', {name: /search/i})).toBeDisabled();
		expect(screen.getByRole('button', {name: /search/i})).toBeDisabled();
		expect(screen.getByRole('table').getAttribute('class')).not.toMatch(
			/table-hover/gi
		);

		expect(within(row1).getByRole('cell')).toHaveClass('text-muted');
		expect(within(row1).getByRole('checkbox')).toBeDisabled();

		expect(within(row2).getByRole('cell')).toHaveClass('text-muted');
		expect(within(row2).getByRole('checkbox')).toBeDisabled();

		expect(within(row3).getByRole('cell')).toHaveClass('text-muted');
		expect(within(row3).getByRole('checkbox')).toBeDisabled();

		expect(within(row4).getByRole('cell')).toHaveClass('text-muted');
		expect(within(row4).getByRole('checkbox')).toBeDisabled();

		expect(within(row5).getByRole('cell')).toHaveClass('text-muted');
		expect(within(row5).getByRole('checkbox')).toBeDisabled();
	});

	it('renders table with filtered items', async () => {
		fetch
			.mockReturnValueOnce(
				mockResponse(fetchTableDataResponse(DEFAULT_ITEMS))
			)
			.mockReturnValueOnce(
				mockResponse(
					fetchTableDataResponse([
						{
							age: 19,
							firstName: 'Andre',
							id: '8189001028599808',
							lastName: 'Patton',
						},
					])
				)
			)
			.mockReturnValueOnce(
				mockResponse(fetchTableDataResponse(DEFAULT_ITEMS))
			);

		const onItemsChange = jest.fn();

		const {container} = render(
			<WrappedTable onItemsChange={onItemsChange} />
		);

		await loadingElement();

		expect(container.querySelectorAll('tbody tr')).toHaveLength(5);
		expect(fetch.mock.calls).toHaveLength(1);

		const input = screen.getByRole('textbox', {
			name: /search/i,
		}) as HTMLInputElement;

		act(() => {
			fireEvent.change(input, {
				target: {value: 'Andre'},
			});
		});

		act(() => {
			fireEvent.submit(input);
		});

		expect(input.value).toEqual('Andre');

		await loadingElement();

		expect(container.querySelectorAll('tbody tr')).toHaveLength(1);
		expect(fetch.mock.calls).toHaveLength(2);
		expect(screen.getByTestId('subnav-description')).toBeInTheDocument();
		expect(onItemsChange).toHaveBeenCalled();

		act(() => {
			fireEvent.click(screen.getByTestId('subnav-clear-button'));
		});

		await loadingElement();

		expect(input.value).toEqual('');
		expect(container.querySelectorAll('tbody tr')).toHaveLength(5);
		expect(fetch.mock.calls).toHaveLength(3);
		expect(onItemsChange).toHaveBeenCalled();
	});

	// TODO:

	// 1. Create unit test for table state renderer (error, loading, empty)
	// 2. Create unit test for management toolbar filters
	// 3. Create unit test for pagination

});
