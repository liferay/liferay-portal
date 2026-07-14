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
import {AllTagsDropdown} from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/common/filters/AllTagsDropdown';
import {Item} from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/common/filters/FilterDropdown';
import {initialFilters} from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/common/filters/filters';

const WrappedComponent = ({
	onSelectItem,
}: {
	onSelectItem: (item: Item) => void;
}) => {
	const [selectedItem, setSelectedItem] = React.useState<Item>(
		initialFilters.tag
	);

	return (
		<AllTagsDropdown
			cmsGroupId="123"
			depotEntryId="all"
			item={selectedItem}
			onSelectItem={(item) => {
				setSelectedItem(item);
				onSelectItem(item);
			}}
		/>
	);
};

describe('[CMS Dashboard] Components: AllTagsDropdown', () => {
	const mockTagsApiResponse = {
		items: [
			{
				id: '01',
				name: 'tag 01',
			},
			{
				id: '02',
				name: 'tag 02',
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

		const trigger = screen.getByRole('combobox', {name: 'filter-by-tag'});

		expect(trigger).toHaveTextContent('all-tags');

		fireEvent.click(trigger);

		expect(screen.getByPlaceholderText('search')).toBeInTheDocument();

		const listbox = await screen.findByRole('listbox');

		const option = within(listbox).getByRole('option', {name: 'all-tags'});

		expect(within(listbox).getAllByRole('option')).toHaveLength(1);

		fireEvent.click(option);

		expect(onSelectItem).toHaveBeenCalledWith(
			expect.objectContaining({
				label: 'all-tags',
				value: 'all',
			})
		);
	});

	it('renders a tag list', async () => {
		jest.spyOn(ApiHelper, 'get').mockResolvedValue({
			data: mockTagsApiResponse,
			error: null,
		});

		render(<WrappedComponent onSelectItem={jest.fn()} />);

		fireEvent.click(screen.getByRole('combobox', {name: 'filter-by-tag'}));

		const listbox = await screen.findByRole('listbox');

		expect(
			await within(listbox).findByRole('option', {name: 'tag 01'})
		).toBeInTheDocument();

		expect(
			within(listbox).getByRole('option', {name: 'tag 02'})
		).toBeInTheDocument();

		expect(
			within(listbox).getByRole('option', {name: 'all-tags'})
		).toBeInTheDocument();

		expect(within(listbox).getAllByRole('option')).toHaveLength(3);
	});

	it('searches by tag name and returns a filtered result', async () => {
		jest.spyOn(ApiHelper, 'get').mockResolvedValue({
			data: mockTagsApiResponse,
			error: null,
		});

		render(<WrappedComponent onSelectItem={jest.fn()} />);

		fireEvent.click(screen.getByRole('combobox', {name: 'filter-by-tag'}));

		const listbox = await screen.findByRole('listbox');

		await within(listbox).findByRole('option', {name: 'tag 02'});

		fireEvent.change(screen.getByPlaceholderText('search'), {
			target: {value: 'tag 02'},
		});

		await waitFor(() =>
			expect(within(listbox).getAllByRole('option')).toHaveLength(1)
		);

		expect(
			within(listbox).getByRole('option', {name: 'tag 02'})
		).toBeInTheDocument();

		expect(
			within(listbox).queryByRole('option', {name: 'tag 01'})
		).not.toBeInTheDocument();

		expect(
			within(listbox).queryByRole('option', {name: 'all-tags'})
		).not.toBeInTheDocument();
	});

	it('shows a message when no tag matches the search', async () => {
		jest.spyOn(ApiHelper, 'get').mockResolvedValue({
			data: mockTagsApiResponse,
			error: null,
		});

		render(<WrappedComponent onSelectItem={jest.fn()} />);

		fireEvent.click(screen.getByRole('combobox', {name: 'filter-by-tag'}));

		const listbox = await screen.findByRole('listbox');

		await within(listbox).findByRole('option', {name: 'tag 02'});

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

	it('selects a new tag', async () => {
		jest.spyOn(ApiHelper, 'get').mockResolvedValue({
			data: mockTagsApiResponse,
			error: null,
		});

		render(<WrappedComponent onSelectItem={jest.fn()} />);

		const trigger = screen.getByRole('combobox', {name: 'filter-by-tag'});

		fireEvent.click(trigger);

		const listbox = await screen.findByRole('listbox');

		fireEvent.click(
			await within(listbox).findByRole('option', {name: 'tag 02'})
		);

		await waitFor(() => expect(trigger).toHaveTextContent('tag 02'));
	});
});
