/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {cleanup, fireEvent, render, waitFor} from '@testing-library/react';
import React from 'react';

import {Option, Picker} from '../index';

describe('Picker search', () => {
	afterEach(cleanup);

	const items = [
		'Apple',
		'Banana',
		'Blueberry',
		'Cherry',
		'Grape',
		'Lemon',
		'Lime',
		'Mango',
		'Orange',
		'Peach',
		'Pear',
		'Plum',
	];

	it('does not render search input when props are not provided', () => {
		const {getByRole, queryByPlaceholderText} = render(
			<Picker items={items}>
				{(item) => <Option key={item}>{item}</Option>}
			</Picker>
		);

		fireEvent.click(getByRole('combobox'));

		expect(queryByPlaceholderText('Search')).not.toBeInTheDocument();
	});

	it('renders search input when searchableThreshold is provided and exceeded', () => {
		const {getByPlaceholderText, getByRole} = render(
			<Picker items={items} searchableThreshold={5}>
				{(item) => <Option key={item}>{item}</Option>}
			</Picker>
		);

		fireEvent.click(getByRole('combobox'));

		expect(getByPlaceholderText('Search')).toBeInTheDocument();
	});

	it('does not render search input when searchableThreshold is provided but not exceeded', () => {
		const {getByRole, queryByPlaceholderText} = render(
			<Picker items={['Apple', 'Banana']} searchableThreshold={5}>
				{(item) => <Option key={item}>{item}</Option>}
			</Picker>
		);

		fireEvent.click(getByRole('combobox'));

		expect(queryByPlaceholderText('Search')).not.toBeInTheDocument();
	});

	it('renders search input when searchable prop is true, even if items are below threshold', () => {
		const {getByPlaceholderText, getByRole} = render(
			<Picker items={['Apple']} searchable>
				{(item) => <Option key={item}>{item}</Option>}
			</Picker>
		);

		fireEvent.click(getByRole('combobox'));

		expect(getByPlaceholderText('Search')).toBeInTheDocument();
	});

	it('does not render search input when searchable prop is false, even if items are above threshold', () => {
		const {getByRole, queryByPlaceholderText} = render(
			<Picker items={items} searchable={false} searchableThreshold={5}>
				{(item) => <Option key={item}>{item}</Option>}
			</Picker>
		);

		fireEvent.click(getByRole('combobox'));

		expect(queryByPlaceholderText('Search')).not.toBeInTheDocument();
	});

	it('filters items correctly based on search value', async () => {
		const {getAllByRole, getByPlaceholderText, getByRole} = render(
			<Picker items={items} searchable>
				{(item) => <Option key={item}>{item}</Option>}
			</Picker>
		);

		fireEvent.click(getByRole('combobox'));

		const searchInput = getByPlaceholderText('Search');

		fireEvent.change(searchInput, {target: {value: 'Blue'}});

		await waitFor(() => {
			const filteredItems = getAllByRole('option');
			expect(filteredItems).toHaveLength(1);
			expect(filteredItems[0]).toHaveTextContent('Blueberry');
		});
	});

	it('displays empty state message when no items match', async () => {
		const {getAllByText, getByPlaceholderText, getByRole, queryByRole} =
			render(
				<Picker items={items} searchable>
					{(item) => <Option key={item}>{item}</Option>}
				</Picker>
			);

		fireEvent.click(getByRole('combobox'));

		const searchInput = getByPlaceholderText('Search');

		fireEvent.change(searchInput, {target: {value: 'Zzz'}});

		await waitFor(() => {
			expect(queryByRole('option')).not.toBeInTheDocument();
			expect(getAllByText('No results found').length).toBeGreaterThan(0);
		});
	});

	it('filters items correctly based on filterKey', async () => {
		const objectItems = [
			{id: 1, label: 'Apple'},
			{id: 2, label: 'Banana'},
		];
		const {getAllByRole, getByPlaceholderText, getByRole} = render(
			<Picker filterKey="label" items={objectItems} searchable>
				{(item) => <Option key={item.id}>{item.label}</Option>}
			</Picker>
		);

		fireEvent.click(getByRole('combobox'));

		const searchInput = getByPlaceholderText('Search');

		fireEvent.change(searchInput, {target: {value: 'Ban'}});

		await waitFor(() => {
			const filteredItems = getAllByRole('option');
			expect(filteredItems).toHaveLength(1);
			expect(filteredItems[0]).toHaveTextContent('Banana');
		});
	});

	it('focuses search input automatically when opened', () => {
		const {getByPlaceholderText, getByRole} = render(
			<Picker items={items} searchable>
				{(item) => <Option key={item}>{item}</Option>}
			</Picker>
		);

		fireEvent.click(getByRole('combobox'));

		const searchInput = getByPlaceholderText('Search');

		expect(document.activeElement).toBe(searchInput);
	});

	it('navigates through filtered items using arrow keys from search input', async () => {
		const {getByPlaceholderText, getByRole} = render(
			<Picker items={items} searchable>
				{(item) => <Option key={item}>{item}</Option>}
			</Picker>
		);

		const combobox = getByRole('combobox');

		fireEvent.click(combobox);

		const searchInput = getByPlaceholderText('Search');

		fireEvent.change(searchInput, {target: {value: 'P'}}); // Apple, Grape, Peach, Pear, Plum

		// activeDescendant should be the first item (Apple)

		await waitFor(() =>
			expect(combobox).toHaveAttribute('aria-activedescendant', 'Apple')
		);

		fireEvent.keyDown(searchInput, {key: 'ArrowDown'});
		expect(combobox).toHaveAttribute('aria-activedescendant', 'Grape');

		fireEvent.keyDown(searchInput, {key: 'ArrowDown'});
		expect(combobox).toHaveAttribute('aria-activedescendant', 'Peach');

		fireEvent.keyDown(searchInput, {key: 'ArrowDown'});
		expect(combobox).toHaveAttribute('aria-activedescendant', 'Pear');

		fireEvent.keyDown(searchInput, {key: 'ArrowDown'});
		expect(combobox).toHaveAttribute('aria-activedescendant', 'Plum');
	});

	it('selects an item using Enter key from search input', async () => {
		const onSelectionChange = jest.fn();
		const {getByPlaceholderText, getByRole} = render(
			<Picker
				items={items}
				onSelectionChange={onSelectionChange}
				searchable
			>
				{(item) => <Option key={item}>{item}</Option>}
			</Picker>
		);

		const combobox = getByRole('combobox');

		fireEvent.click(combobox);

		const searchInput = getByPlaceholderText('Search');

		fireEvent.change(searchInput, {target: {value: 'Mango'}});

		await waitFor(() =>
			expect(combobox).toHaveAttribute('aria-activedescendant', 'Mango')
		);

		fireEvent.keyDown(searchInput, {key: 'Enter'});

		expect(onSelectionChange).toHaveBeenCalledWith('Mango');
		expect(combobox).toHaveTextContent('Mango');
	});

	it('preserves the trigger label of the selected item when the search filter excludes it', async () => {
		const {getByPlaceholderText, getByRole, queryByRole} = render(
			<Picker defaultSelectedKey="Mango" items={items} searchable>
				{(item) => <Option key={item}>{item}</Option>}
			</Picker>
		);

		const combobox = getByRole('combobox');

		expect(combobox).toHaveTextContent('Mango');

		fireEvent.click(combobox);

		const searchInput = getByPlaceholderText('Search');

		fireEvent.change(searchInput, {target: {value: 'Apple'}});

		await waitFor(() => {
			expect(
				queryByRole('option', {name: 'Mango'})
			).not.toBeInTheDocument();
		});

		expect(combobox).toHaveTextContent('Mango');
	});

	it('renders search input with correct accessibility attributes', () => {
		const {getByPlaceholderText, getByRole} = render(
			<Picker items={items} searchable>
				{(item) => <Option key={item}>{item}</Option>}
			</Picker>
		);

		fireEvent.click(getByRole('combobox'));

		const searchInput = getByPlaceholderText('Search');

		expect(searchInput).toHaveAttribute('aria-autocomplete', 'list');
		expect(searchInput).toHaveAttribute('aria-controls');
		expect(searchInput).toHaveAttribute('aria-activedescendant');
	});
});
