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
import {AllVocabulariesDropdown} from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/common/filters/AllVocabulariesDropdown';
import {Item} from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/common/filters/FilterDropdown';
import {initialFilters} from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/common/filters/filters';

const WrappedComponent = ({
	onSelectItem,
}: {
	onSelectItem: (item: Item) => void;
}) => {
	const [selectedItem, setSelectedItem] = React.useState<Item>(
		initialFilters.vocabulary
	);

	return (
		<AllVocabulariesDropdown
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

describe('[CMS Dashboard] Components: AllVocabulariesDropdown', () => {
	const mockVocabularyApiResponse = {
		items: [
			{
				id: '01',
				name: 'vocabulary 01',
			},
			{
				id: '02',
				name: 'vocabulary 02',
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
			name: 'filter-by-vocabulary',
		});

		expect(trigger).toHaveTextContent('all-vocabularies');

		fireEvent.click(trigger);

		expect(screen.getByPlaceholderText('search')).toBeInTheDocument();

		const listbox = await screen.findByRole('listbox');

		const option = within(listbox).getByRole('option', {
			name: 'all-vocabularies',
		});

		expect(within(listbox).getAllByRole('option')).toHaveLength(1);

		fireEvent.click(option);

		expect(onSelectItem).toHaveBeenCalledWith(
			expect.objectContaining({
				label: 'all-vocabularies',
				value: 'all',
			})
		);
	});

	it('renders a vocabulary list', async () => {
		jest.spyOn(ApiHelper, 'get').mockResolvedValue({
			data: mockVocabularyApiResponse,
			error: null,
		});

		render(<WrappedComponent onSelectItem={jest.fn()} />);

		fireEvent.click(
			screen.getByRole('combobox', {name: 'filter-by-vocabulary'})
		);

		const listbox = await screen.findByRole('listbox');

		expect(
			await within(listbox).findByRole('option', {name: 'vocabulary 01'})
		).toBeInTheDocument();

		expect(
			within(listbox).getByRole('option', {name: 'vocabulary 02'})
		).toBeInTheDocument();

		expect(
			within(listbox).getByRole('option', {name: 'all-vocabularies'})
		).toBeInTheDocument();

		expect(within(listbox).getAllByRole('option')).toHaveLength(3);
	});

	it('searches by vocabulary name and returns a filtered result', async () => {
		jest.spyOn(ApiHelper, 'get').mockResolvedValue({
			data: mockVocabularyApiResponse,
			error: null,
		});

		render(<WrappedComponent onSelectItem={jest.fn()} />);

		fireEvent.click(
			screen.getByRole('combobox', {name: 'filter-by-vocabulary'})
		);

		const listbox = await screen.findByRole('listbox');

		await within(listbox).findByRole('option', {name: 'vocabulary 02'});

		fireEvent.change(screen.getByPlaceholderText('search'), {
			target: {value: 'vocabulary 02'},
		});

		await waitFor(() =>
			expect(within(listbox).getAllByRole('option')).toHaveLength(1)
		);

		expect(
			within(listbox).getByRole('option', {name: 'vocabulary 02'})
		).toBeInTheDocument();

		expect(
			within(listbox).queryByRole('option', {name: 'vocabulary 01'})
		).not.toBeInTheDocument();

		expect(
			within(listbox).queryByRole('option', {name: 'all-vocabularies'})
		).not.toBeInTheDocument();
	});

	it('shows a message when no vocabulary matches the search', async () => {
		jest.spyOn(ApiHelper, 'get').mockResolvedValue({
			data: mockVocabularyApiResponse,
			error: null,
		});

		render(<WrappedComponent onSelectItem={jest.fn()} />);

		fireEvent.click(
			screen.getByRole('combobox', {name: 'filter-by-vocabulary'})
		);

		const listbox = await screen.findByRole('listbox');

		await within(listbox).findByRole('option', {name: 'vocabulary 02'});

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

	it('selects a new vocabulary', async () => {
		jest.spyOn(ApiHelper, 'get').mockResolvedValue({
			data: mockVocabularyApiResponse,
			error: null,
		});

		render(<WrappedComponent onSelectItem={jest.fn()} />);

		const trigger = screen.getByRole('combobox', {
			name: 'filter-by-vocabulary',
		});

		fireEvent.click(trigger);

		const listbox = await screen.findByRole('listbox');

		fireEvent.click(
			await within(listbox).findByRole('option', {name: 'vocabulary 02'})
		);

		await waitFor(() => expect(trigger).toHaveTextContent('vocabulary 02'));
	});
});
