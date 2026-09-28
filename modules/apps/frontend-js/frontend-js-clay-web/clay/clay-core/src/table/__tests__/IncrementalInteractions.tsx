/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {cleanup, render} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React, {useCallback, useMemo, useState} from 'react';

import '@testing-library/jest-dom';

import {Body, Cell, Head, Row, Table} from '../../';

type Sorting = {
	column: React.Key;
	direction: 'ascending' | 'descending';
};

const columns = [
	{id: 'name', name: 'Name'},
	{id: 'type', name: 'Type'},
];

const itemsTree = [
	{
		id: 1,
		name: 'Foo',
		type: 'PNG',
	},
	{id: 2, name: 'Bar', type: 'Files'},
	{
		children: [
			{id: 4, name: 'Bar', type: 'Vector'},
			{id: 5, name: 'Foo', type: 'Files'},
		],
		id: 3,
		name: 'Baz',
		type: 'Folder',
	},
];

const emptyStateItems = [
	{
		id: 1,
		name: 'Foo',
		type: 'PNG',
	},
	{id: 2, name: 'Bar', type: 'Files'},
	{
		children: [],
		id: 3,
		name: 'Baz',
		type: 'Folder',
	},
];

function getSortIconSymbol(columnHeader: HTMLElement) {
	return columnHeader.querySelector('use')!.getAttribute('href');
}

// The stylesheet sizes and colors the sorted column's arrow off these classes.

function getSortStateClass(columnHeader: HTMLElement) {
	if (columnHeader.classList.contains('order-arrow-up-active')) {
		return 'up';
	}

	if (columnHeader.classList.contains('order-arrow-down-active')) {
		return 'down';
	}

	return 'none';
}

describe('Table incremental interactions', () => {
	afterEach(cleanup);

	it('clicking on the column does the sorting', () => {
		function ColumnSorting() {
			const [sort, setSort] = useState<Sorting | null>(null);
			const [items, setItems] = useState([
				{id: 1, name: 'Foo', type: 'Foo'},
				{id: 2, name: 'Bar', type: 'Bar'},
				{id: 3, name: 'Baz', type: 'Baz'},
			]);

			const onSortChange = useCallback((sort: Sorting | null) => {
				if (sort) {
					setItems((items) =>
						items.sort((a, b) => {
							let cmp = new Intl.Collator('en', {
								numeric: true,
							}).compare(

								// @ts-ignore

								a[sort.column],

								// @ts-ignore

								b[sort.column]
							);

							if (sort.direction === 'descending') {
								cmp *= -1;
							}

							return cmp;
						})
					);
				}

				setSort(sort);
			}, []);

			return (
				<Table onSortChange={onSortChange} sort={sort}>
					<Head items={columns}>
						{(column) => (
							<Cell key={column.id} sortable>
								{column.name}
							</Cell>
						)}
					</Head>

					<Body defaultItems={items}>
						{(row) => (
							<Row items={columns}>

								{/** @ts-ignore */}

								{(column) => <Cell>{row[column.id]}</Cell>}
							</Row>
						)}
					</Body>
				</Table>
			);
		}

		const {getAllByRole} = render(<ColumnSorting />);

		const [name, type] = getAllByRole('columnheader');

		expect(name!.getAttribute('aria-sort')).toBe('none');
		expect(type!.getAttribute('aria-sort')).toBe('none');

		expect(getSortIconSymbol(name!)).toBe('#order-arrow');
		expect(getSortIconSymbol(type!)).toBe('#order-arrow');

		expect(getSortStateClass(name!)).toBe('none');
		expect(getSortStateClass(type!)).toBe('none');

		const [row1Col1, , , row2Col1, , , row3Col1] = getAllByRole('cell');

		expect(row1Col1!.textContent).toBe('Foo');
		expect(row2Col1!.textContent).toBe('Bar');
		expect(row3Col1!.textContent).toBe('Baz');

		userEvent.click(name!);

		expect(name!.getAttribute('aria-sort')).toBe('ascending');
		expect(type!.getAttribute('aria-sort')).toBe('none');

		expect(getSortIconSymbol(name!)).toBe('#order-arrow-up');
		expect(getSortIconSymbol(type!)).toBe('#order-arrow');

		expect(getSortStateClass(name!)).toBe('up');
		expect(getSortStateClass(type!)).toBe('none');

		const [newRow1Col1, , , newRow2Col1, , , newRow3Col1] =
			getAllByRole('cell');

		expect(newRow1Col1!.textContent).toBe('Bar');
		expect(newRow2Col1!.textContent).toBe('Baz');
		expect(newRow3Col1!.textContent).toBe('Foo');
	});

	it('clicking again on the header column with sorting reverses the ordering', () => {
		const items = [
			{id: 1, name: 'Foo', type: 'Foo'},
			{id: 2, name: 'Bar', type: 'Bar'},
			{id: 3, name: 'Baz', type: 'Baz'},
		];

		function ColumnSorting() {
			const [sort, setSort] = useState<Sorting | null>({
				column: 'name',
				direction: 'ascending',
			});

			const filteredItems = useMemo(() => {
				if (!sort) {
					return items;
				}

				return items.sort((a, b) => {
					let cmp = new Intl.Collator('en', {numeric: true}).compare(

						// @ts-ignore

						a[sort.column],

						// @ts-ignore

						b[sort.column]
					);

					if (sort.direction === 'descending') {
						cmp *= -1;
					}

					return cmp;
				});
			}, [sort]);

			return (
				<Table onSortChange={setSort} sort={sort}>
					<Head items={columns}>
						{(column) => (
							<Cell key={column.id} sortable>
								{column.name}
							</Cell>
						)}
					</Head>

					<Body defaultItems={filteredItems}>
						{(row) => (
							<Row items={columns}>

								{/** @ts-ignore */}

								{(column) => <Cell>{row[column.id]}</Cell>}
							</Row>
						)}
					</Body>
				</Table>
			);
		}

		const {getAllByRole} = render(<ColumnSorting />);

		const [name, type] = getAllByRole('columnheader');

		expect(name!.getAttribute('aria-sort')).toBe('ascending');
		expect(type!.getAttribute('aria-sort')).toBe('none');

		expect(getSortIconSymbol(name!)).toBe('#order-arrow-up');
		expect(getSortIconSymbol(type!)).toBe('#order-arrow');

		expect(getSortStateClass(name!)).toBe('up');
		expect(getSortStateClass(type!)).toBe('none');

		const [row1Col1, , , row2Col1, , , row3Col1] = getAllByRole('cell');

		expect(row1Col1!.textContent).toBe('Bar');
		expect(row2Col1!.textContent).toBe('Baz');
		expect(row3Col1!.textContent).toBe('Foo');

		userEvent.click(name!);

		expect(name!.getAttribute('aria-sort')).toBe('descending');
		expect(type!.getAttribute('aria-sort')).toBe('none');

		expect(getSortIconSymbol(name!)).toBe('#order-arrow-down');
		expect(getSortIconSymbol(type!)).toBe('#order-arrow');

		expect(getSortStateClass(name!)).toBe('down');
		expect(getSortStateClass(type!)).toBe('none');

		const [newRow1Col1, , , newRow2Col1, , , newRow3Col1] =
			getAllByRole('cell');

		expect(newRow1Col1!.textContent).toBe('Foo');
		expect(newRow2Col1!.textContent).toBe('Baz');
		expect(newRow3Col1!.textContent).toBe('Bar');
	});

	it('clicking on a new header column will reset the order of the previous column', () => {
		const items = [
			{id: 1, name: 'Foo', type: 'PNG'},
			{id: 2, name: 'Bar', type: 'Files'},
			{id: 3, name: 'Baz', type: 'Folder'},
		];

		function ColumnSorting() {
			const [sort, setSort] = useState<Sorting | null>({
				column: 'name',
				direction: 'ascending',
			});

			const filteredItems = useMemo(() => {
				if (!sort) {
					return items;
				}

				return items.sort((a, b) => {
					let cmp = new Intl.Collator('en', {numeric: true}).compare(

						// @ts-ignore

						a[sort.column],

						// @ts-ignore

						b[sort.column]
					);

					if (sort.direction === 'descending') {
						cmp *= -1;
					}

					return cmp;
				});
			}, [sort]);

			return (
				<Table onSortChange={setSort} sort={sort}>
					<Head items={columns}>
						{(column) => (
							<Cell key={column.id} sortable>
								{column.name}
							</Cell>
						)}
					</Head>

					<Body defaultItems={filteredItems}>
						{(row) => (
							<Row items={columns}>

								{/** @ts-ignore */}

								{(column) => <Cell>{row[column.id]}</Cell>}
							</Row>
						)}
					</Body>
				</Table>
			);
		}

		const {getAllByRole} = render(<ColumnSorting />);

		const [name, type] = getAllByRole('columnheader');

		expect(name!.getAttribute('aria-sort')).toBe('ascending');
		expect(type!.getAttribute('aria-sort')).toBe('none');

		const [row1Col1, , , row2Col1, , , row3Col1] = getAllByRole('cell');

		expect(row1Col1!.textContent).toBe('Bar');
		expect(row2Col1!.textContent).toBe('Baz');
		expect(row3Col1!.textContent).toBe('Foo');

		userEvent.click(type!);

		expect(name!.getAttribute('aria-sort')).toBe('none');
		expect(type!.getAttribute('aria-sort')).toBe('ascending');

		expect(getSortIconSymbol(name!)).toBe('#order-arrow');
		expect(getSortIconSymbol(type!)).toBe('#order-arrow-up');

		expect(getSortStateClass(name!)).toBe('none');
		expect(getSortStateClass(type!)).toBe('up');

		const [, newRow1Col2, , , newRow2Col2, , , newRow3Col2] =
			getAllByRole('cell');

		expect(newRow1Col2!.textContent).toBe('Files');
		expect(newRow2Col2!.textContent).toBe('Folder');
		expect(newRow3Col2!.textContent).toBe('PNG');
	});

	describe('tree grid', () => {
		it('pressing tab will move the focus to the first row', () => {
			const {getAllByRole} = render(
				<Table columnsVisibility={false} nestedKey="children">
					<Head items={columns}>
						{(column) => <Cell key={column.id}>{column.name}</Cell>}
					</Head>

					<Body defaultItems={itemsTree}>
						{(row) => (
							<Row items={columns}>
								{(column) => (
									<Cell key={`${row.id}:${column.id}`}>

										{/** @ts-ignore */}

										{row[column.id]}
									</Cell>
								)}
							</Row>
						)}
					</Body>
				</Table>
			);

			const [, firstRow] = getAllByRole('row');

			expect(document.activeElement).not.toEqual(firstRow);

			userEvent.tab();

			expect(firstRow).toHaveFocus();
		});

		it('pressing tab again will move the focus to the next element outside the table', () => {
			const {getAllByRole} = render(
				<Table columnsVisibility={false} nestedKey="children">
					<Head items={columns}>
						{(column) => <Cell key={column.id}>{column.name}</Cell>}
					</Head>

					<Body defaultItems={itemsTree}>
						{(row) => (
							<Row items={columns}>
								{(column) => (
									<Cell key={`${row.id}:${column.id}`}>

										{/** @ts-ignore */}

										{row[column.id]}
									</Cell>
								)}
							</Row>
						)}
					</Body>
				</Table>
			);

			const [, firstRow] = getAllByRole('row');

			expect(document.activeElement).not.toEqual(firstRow);

			userEvent.tab();

			expect(firstRow).toHaveFocus();

			userEvent.tab();

			expect(document.body).toHaveFocus();
		});

		describe('keyboard interaction on row', () => {
			it('pressing the down arrow key moves focus to the row below', () => {
				const {getAllByRole} = render(
					<Table columnsVisibility={false} nestedKey="children">
						<Head items={columns}>
							{(column) => (
								<Cell key={column.id}>{column.name}</Cell>
							)}
						</Head>

						<Body items={itemsTree}>
							{(row) => (
								<Row items={columns}>
									{(column) => (
										<Cell key={`${row.id}:${column.id}`}>

											{/** @ts-ignore */}

											{row[column.id]}
										</Cell>
									)}
								</Row>
							)}
						</Body>
					</Table>
				);

				const [, firstRow, secondRow] = getAllByRole('row');

				expect(document.activeElement).not.toEqual(firstRow);

				userEvent.tab();

				expect(firstRow).toHaveFocus();

				userEvent.keyboard('{ArrowDown/}');

				expect(secondRow).toHaveFocus();
			});

			it('pressing the up arrow key moves focus to the row above', () => {
				const {getAllByRole} = render(
					<Table columnsVisibility={false} nestedKey="children">
						<Head items={columns}>
							{(column) => (
								<Cell key={column.id}>{column.name}</Cell>
							)}
						</Head>

						<Body defaultItems={itemsTree}>
							{(row) => (
								<Row items={columns}>
									{(column) => (
										<Cell key={`${row.id}:${column.id}`}>

											{/** @ts-ignore */}

											{row[column.id]}
										</Cell>
									)}
								</Row>
							)}
						</Body>
					</Table>
				);

				const [, firstRow, secondRow] = getAllByRole('row');

				expect(document.activeElement).not.toEqual(firstRow);

				userEvent.tab();

				expect(firstRow).toHaveFocus();

				userEvent.keyboard('{ArrowDown/}');

				expect(secondRow).toHaveFocus();

				userEvent.keyboard('{ArrowUp/}');

				expect(firstRow).toHaveFocus();
			});

			it('press the left arrow key to collapse the node with expandable', () => {
				const {getAllByRole} = render(
					<Table columnsVisibility={false} nestedKey="children">
						<Head items={columns}>
							{(column) => (
								<Cell key={column.id}>{column.name}</Cell>
							)}
						</Head>

						<Body defaultItems={itemsTree}>
							{(row) => (
								<Row items={columns}>
									{(column) => (
										<Cell key={`${row.id}:${column.id}`}>

											{/** @ts-ignore */}

											{row[column.id]}
										</Cell>
									)}
								</Row>
							)}
						</Body>
					</Table>
				);

				const [, firstRow, _secondRow, thirdRow] = getAllByRole('row');

				expect(document.activeElement).not.toEqual(firstRow);

				userEvent.tab();

				expect(firstRow).toHaveFocus();

				userEvent.keyboard('{ArrowDown/}');
				userEvent.keyboard('{ArrowDown/}');

				expect(thirdRow).toHaveFocus();
				expect(thirdRow!.getAttribute('aria-expanded')).toBe('false');

				userEvent.keyboard('{ArrowRight/}');

				expect(thirdRow!.getAttribute('aria-expanded')).toBe('true');

				userEvent.keyboard('{ArrowLeft/}');

				expect(thirdRow!.getAttribute('aria-expanded')).toBe('false');
			});

			it('does not render left arrow button when child array is empty', () => {
				const {queryByRole} = render(
					<Table columnsVisibility={false} nestedKey="children">
						<Head items={columns}>
							{(column) => (
								<Cell key={column.id}>{column.name}</Cell>
							)}
						</Head>

						<Body defaultItems={emptyStateItems}>
							{(row) => (
								<Row items={columns}>
									{(column) => (
										<Cell key={`${row.id}:${column.id}`}>

											{/** @ts-ignore */}

											{row[column.id]}
										</Cell>
									)}
								</Row>
							)}
						</Body>
					</Table>
				);

				const buttonRole = queryByRole('presentation');

				expect(buttonRole).not.toBeInTheDocument();
			});

			it('pressing the left arrow key moves the focus to the row on the level above', () => {
				const {getAllByRole} = render(
					<Table columnsVisibility={false} nestedKey="children">
						<Head items={columns}>
							{(column) => (
								<Cell key={column.id}>{column.name}</Cell>
							)}
						</Head>

						<Body defaultItems={itemsTree}>
							{(row) => (
								<Row items={columns}>
									{(column) => (
										<Cell key={`${row.id}:${column.id}`}>

											{/** @ts-ignore */}

											{row[column.id]}
										</Cell>
									)}
								</Row>
							)}
						</Body>
					</Table>
				);

				const [, firstRow, _secondRow, thirdRow] = getAllByRole('row');

				expect(document.activeElement).not.toEqual(firstRow);

				userEvent.tab();

				expect(firstRow).toHaveFocus();

				userEvent.keyboard('{ArrowDown/}');
				userEvent.keyboard('{ArrowDown/}');

				expect(thirdRow).toHaveFocus();
				expect(thirdRow!.getAttribute('aria-expanded')).toBe('false');

				userEvent.keyboard('{ArrowRight/}');

				expect(thirdRow!.getAttribute('aria-expanded')).toBe('true');

				userEvent.keyboard('{ArrowDown/}');

				const [, , , , fourthRow] = getAllByRole('row');

				expect(fourthRow).toHaveFocus();

				userEvent.keyboard('{ArrowLeft/}');

				expect(thirdRow).toHaveFocus();
			});

			it('pressing the right arrow key moves the focus to the first cell', () => {
				const {getAllByRole} = render(
					<Table columnsVisibility={false} nestedKey="children">
						<Head items={columns}>
							{(column) => (
								<Cell key={column.id}>{column.name}</Cell>
							)}
						</Head>

						<Body defaultItems={itemsTree}>
							{(row) => (
								<Row items={columns}>
									{(column) => (
										<Cell key={`${row.id}:${column.id}`}>

											{/** @ts-ignore */}

											{row[column.id]}
										</Cell>
									)}
								</Row>
							)}
						</Body>
					</Table>
				);

				const [, firstRow, secondRow] = getAllByRole('row');

				expect(document.activeElement).not.toEqual(firstRow);

				userEvent.tab();

				expect(firstRow).toHaveFocus();

				userEvent.keyboard('{ArrowDown/}');

				expect(secondRow).toHaveFocus();

				userEvent.keyboard('{ArrowRight/}');

				const [, , , , row2Cell1] = getAllByRole('gridcell');

				expect(row2Cell1).toHaveFocus();
			});
		});

		describe('keyboard interaction on cell', () => {
			it('pressing the up arrow key moves the focus to the cell above', () => {
				const {getAllByRole} = render(
					<Table columnsVisibility={false} nestedKey="children">
						<Head items={columns}>
							{(column) => (
								<Cell key={column.id}>{column.name}</Cell>
							)}
						</Head>

						<Body defaultItems={itemsTree}>
							{(row) => (
								<Row items={columns}>
									{(column) => (
										<Cell key={`${row.id}:${column.id}`}>

											{/** @ts-ignore */}

											{row[column.id]}
										</Cell>
									)}
								</Row>
							)}
						</Body>
					</Table>
				);

				const [, firstRow, secondRow] = getAllByRole('row');

				expect(document.activeElement).not.toEqual(firstRow);

				userEvent.tab();

				expect(firstRow).toHaveFocus();

				userEvent.keyboard('{ArrowDown/}');

				expect(secondRow).toHaveFocus();

				userEvent.keyboard('{ArrowRight/}');

				const [, , row1Cell1, , row2Cell1] = getAllByRole('gridcell');

				expect(row2Cell1).toHaveFocus();

				userEvent.keyboard('{ArrowUp/}');

				expect(row1Cell1).toHaveFocus();
			});

			it('pressing the down arrow key moves focus to the cell below', () => {
				const {getAllByRole} = render(
					<Table columnsVisibility={false} nestedKey="children">
						<Head items={columns}>
							{(column) => (
								<Cell key={column.id}>{column.name}</Cell>
							)}
						</Head>

						<Body defaultItems={itemsTree}>
							{(row) => (
								<Row items={columns}>
									{(column) => (
										<Cell key={`${row.id}:${column.id}`}>

											{/** @ts-ignore */}

											{row[column.id]}
										</Cell>
									)}
								</Row>
							)}
						</Body>
					</Table>
				);

				const [, firstRow] = getAllByRole('row');

				expect(document.activeElement).not.toEqual(firstRow);

				userEvent.tab();

				expect(firstRow).toHaveFocus();

				userEvent.keyboard('{ArrowRight/}');

				const [, , row1Cell1, , row2Cell1] = getAllByRole('gridcell');

				expect(row1Cell1).toHaveFocus();

				userEvent.keyboard('{ArrowDown/}');

				expect(row2Cell1).toHaveFocus();
			});

			it('pressing the right arrow key moves focus to the cell to the right', () => {
				const {getAllByRole} = render(
					<Table columnsVisibility={false} nestedKey="children">
						<Head items={columns}>
							{(column) => (
								<Cell key={column.id}>{column.name}</Cell>
							)}
						</Head>

						<Body defaultItems={itemsTree}>
							{(row) => (
								<Row items={columns}>
									{(column) => (
										<Cell key={`${row.id}:${column.id}`}>

											{/** @ts-ignore */}

											{row[column.id]}
										</Cell>
									)}
								</Row>
							)}
						</Body>
					</Table>
				);

				const [, firstRow] = getAllByRole('row');

				expect(document.activeElement).not.toEqual(firstRow);

				userEvent.tab();

				expect(firstRow).toHaveFocus();

				userEvent.keyboard('{ArrowRight/}');

				const [, , row1Cell1, row1Cell2] = getAllByRole('gridcell');

				expect(row1Cell1).toHaveFocus();

				userEvent.keyboard('{ArrowRight/}');

				expect(row1Cell2).toHaveFocus();
			});

			it('pressing the left arrow key moves the focus to the right cell', () => {
				const {getAllByRole} = render(
					<Table columnsVisibility={false} nestedKey="children">
						<Head items={columns}>
							{(column) => (
								<Cell key={column.id}>{column.name}</Cell>
							)}
						</Head>

						<Body defaultItems={itemsTree}>
							{(row) => (
								<Row items={columns}>
									{(column) => (
										<Cell key={`${row.id}:${column.id}`}>

											{/** @ts-ignore */}

											{row[column.id]}
										</Cell>
									)}
								</Row>
							)}
						</Body>
					</Table>
				);

				const [, firstRow] = getAllByRole('row');

				expect(document.activeElement).not.toEqual(firstRow);

				userEvent.tab();

				expect(firstRow).toHaveFocus();

				userEvent.keyboard('{ArrowRight/}');

				const [, , row1Cell1, row1Cell2] = getAllByRole('gridcell');

				expect(row1Cell1).toHaveFocus();

				userEvent.keyboard('{ArrowRight/}');

				expect(row1Cell2).toHaveFocus();

				userEvent.keyboard('{ArrowLeft/}');

				expect(row1Cell1).toHaveFocus();
			});

			it('pressing the left arrow key in the first cell moves the focus to the row', () => {
				const {getAllByRole} = render(
					<Table columnsVisibility={false} nestedKey="children">
						<Head items={columns}>
							{(column) => (
								<Cell key={column.id}>{column.name}</Cell>
							)}
						</Head>

						<Body defaultItems={itemsTree}>
							{(row) => (
								<Row items={columns}>
									{(column) => (
										<Cell key={`${row.id}:${column.id}`}>

											{/** @ts-ignore */}

											{row[column.id]}
										</Cell>
									)}
								</Row>
							)}
						</Body>
					</Table>
				);

				const [, firstRow] = getAllByRole('row');

				expect(document.activeElement).not.toEqual(firstRow);

				userEvent.tab();

				expect(firstRow).toHaveFocus();

				userEvent.keyboard('{ArrowRight/}');

				const [, , row1Cell1] = getAllByRole('gridcell');

				expect(row1Cell1).toHaveFocus();

				userEvent.keyboard('{ArrowLeft/}');

				expect(firstRow).toHaveFocus();
			});
		});

		describe('keyboard arrows indicator', () => {
			it('does not render the indicator by default', () => {
				render(
					<Table columnsVisibility={false} nestedKey="children">
						<Head items={columns}>
							{(column) => (
								<Cell key={column.id}>{column.name}</Cell>
							)}
						</Head>

						<Body defaultItems={itemsTree}>
							{(row) => (
								<Row items={columns}>
									{(column) => (
										<Cell key={`${row.id}:${column.id}`}>

											{/** @ts-ignore */}

											{row[column.id]}
										</Cell>
									)}
								</Row>
							)}
						</Body>
					</Table>
				);

				expect(
					document.body.querySelector(
						'.clay-keyboard-arrows-indicator'
					)
				).not.toBeInTheDocument();
			});

			it('renders the floating indicator with direction "all" when enabled', () => {
				render(
					<Table
						columnsVisibility={false}
						displayKeyboardArrowsIndicator
						nestedKey="children"
					>
						<Head items={columns}>
							{(column) => (
								<Cell key={column.id}>{column.name}</Cell>
							)}
						</Head>

						<Body defaultItems={itemsTree}>
							{(row) => (
								<Row items={columns}>
									{(column) => (
										<Cell key={`${row.id}:${column.id}`}>

											{/** @ts-ignore */}

											{row[column.id]}
										</Cell>
									)}
								</Row>
							)}
						</Body>
					</Table>
				);

				const indicator = document.body.querySelector(
					'.clay-keyboard-arrows-indicator'
				);

				expect(indicator).toBeInTheDocument();
				expect(indicator).toHaveClass('clay-keyboard-arrows-all');
				expect(indicator).toHaveClass(
					'clay-keyboard-arrows-indicator-floating'
				);
			});

			it('does not render the indicator when nestedKey is omitted', () => {
				render(
					<Table
						columnsVisibility={false}
						displayKeyboardArrowsIndicator
					>
						<Head items={columns}>
							{(column) => (
								<Cell key={column.id}>{column.name}</Cell>
							)}
						</Head>

						<Body defaultItems={itemsTree}>
							{(row) => (
								<Row items={columns}>
									{(column) => (
										<Cell key={`${row.id}:${column.id}`}>

											{/** @ts-ignore */}

											{row[column.id]}
										</Cell>
									)}
								</Row>
							)}
						</Body>
					</Table>
				);

				expect(
					document.body.querySelector(
						'.clay-keyboard-arrows-indicator'
					)
				).not.toBeInTheDocument();
			});
		});
	});
});
