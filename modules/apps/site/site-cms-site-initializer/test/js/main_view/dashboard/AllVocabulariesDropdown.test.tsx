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
import {AllVocabulariesDropdown} from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/components/AllVocabulariesDropdown';
import {Item} from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/components/FilterDropdown';
import {initialFilters} from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/components/InventoryAnalysisCard';

const WrappedComponent = ({
	onSelectItem,
}: {
	onSelectItem: (item: Item) => void;
}) => {
	const [selectedItem, setSelectedItem] = React.useState<Item>(
		initialFilters.vocabulary
	);

	return (
		<ViewDashboardContextProvider value={{}}>
			<AllVocabulariesDropdown
				item={selectedItem}
				onSelectItem={(item) => {
					setSelectedItem(item);
					onSelectItem(item);
				}}
			/>
		</ViewDashboardContextProvider>
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

		const vocabulariesDropdownButton = screen.getByRole('button', {
			name: 'all-vocabularies',
		});

		expect(vocabulariesDropdownButton).toBeInTheDocument();

		fireEvent.click(vocabulariesDropdownButton);

		expect(screen.queryByText('filter-by-vocabulary')).toBeInTheDocument();

		expect(screen.queryByPlaceholderText('search')).toBeInTheDocument();

		await waitForElementToBeRemoved(() => screen.getByTestId('loading'));

		expect(screen.getAllByRole('menuitem').length).toBe(1);

		const menuitem = screen.getByRole('menuitem', {
			name: 'all-vocabularies',
		});

		expect(menuitem).toBeInTheDocument();

		fireEvent.click(menuitem);

		expect(onSelectItem).toHaveBeenCalledTimes(1);

		expect(onSelectItem).toHaveBeenCalledWith({
			label: 'all-vocabularies',
			value: 'all',
		});
	});

	it('renders a vocabulary list', async () => {
		jest.spyOn(ApiHelper, 'get').mockResolvedValue({
			data: mockVocabularyApiResponse,
			error: null,
		});

		render(<WrappedComponent onSelectItem={jest.fn()} />);

		const vocabulariesDropdownButton = screen.getByRole('button', {
			name: 'all-vocabularies',
		});

		fireEvent.click(vocabulariesDropdownButton);

		await waitForElementToBeRemoved(() => screen.getByTestId('loading'));

		expect(screen.getAllByRole('menuitem').length).toBe(3);

		expect(
			screen.queryByRole('menuitem', {name: 'all-vocabularies'})
		).toBeInTheDocument();

		expect(
			screen.queryByRole('menuitem', {name: 'vocabulary 01'})
		).toBeInTheDocument();

		expect(
			screen.queryByRole('menuitem', {name: 'vocabulary 02'})
		).toBeInTheDocument();
	});

	it('search by a vocabulary and returns a filtered result', async () => {
		jest.useFakeTimers();

		jest.spyOn(ApiHelper, 'get').mockResolvedValue({
			data: mockVocabularyApiResponse,
			error: null,
		});

		render(<WrappedComponent onSelectItem={jest.fn()} />);

		const vocabulariesDropdownButton = screen.getByRole('button', {
			name: 'all-vocabularies',
		});

		fireEvent.click(vocabulariesDropdownButton);

		await waitForElementToBeRemoved(() => screen.getByTestId('loading'));

		expect(screen.getAllByRole('menuitem').length).toBe(3);

		jest.spyOn(ApiHelper, 'get').mockResolvedValue({
			data: {
				items: [
					{
						id: '02',
						name: 'vocabulary 02',
					},
				],
			},
			error: null,
		});

		await act(async () => {
			fireEvent.change(screen.getByPlaceholderText('search'), {
				target: {
					value: 'vocabulary 02',
				},
			});

			jest.advanceTimersByTime(300);
		});

		await waitFor(() => {
			expect(screen.getAllByRole('menuitem').length).toBe(1);

			expect(
				screen.queryByRole('menuitem', {name: 'vocabulary 02'})
			).toBeInTheDocument();
		});

		expect(
			screen.queryByRole('menuitem', {name: 'all-vocabularies'})
		).not.toBeInTheDocument();

		expect(
			screen.queryByRole('menuitem', {name: 'vocabulary 01'})
		).not.toBeInTheDocument();

		jest.useRealTimers();
	});

	it('search by a vocabulary and returns a empty result', async () => {
		jest.useFakeTimers();

		jest.spyOn(ApiHelper, 'get').mockResolvedValue({
			data: mockVocabularyApiResponse,
			error: null,
		});

		render(<WrappedComponent onSelectItem={jest.fn()} />);

		const vocabulariesDropdownButton = screen.getByRole('button', {
			name: 'all-vocabularies',
		});

		fireEvent.click(vocabulariesDropdownButton);

		await waitForElementToBeRemoved(() => screen.getByTestId('loading'));

		expect(screen.getAllByRole('menuitem').length).toBe(3);

		jest.spyOn(ApiHelper, 'get').mockResolvedValue({
			data: {items: []},
			error: null,
		});

		await act(async () => {
			fireEvent.change(screen.getByPlaceholderText('search'), {
				target: {value: 'empty?'},
			});

			jest.advanceTimersByTime(300);
		});

		await waitFor(() => {
			expect(screen.getAllByRole('menuitem').length).toBe(1);

			expect(
				screen.queryByRole('menuitem', {name: 'all-vocabularies'})
			).not.toBeInTheDocument();

			expect(
				screen.queryByRole('menuitem', {
					name: 'no-filters-were-found',
				})
			).toBeInTheDocument();
		});

		jest.useRealTimers();
	});

	it('selects a new vocabulary', async () => {
		jest.spyOn(ApiHelper, 'get').mockResolvedValue({
			data: mockVocabularyApiResponse,
			error: null,
		});

		render(<WrappedComponent onSelectItem={() => {}} />);

		expect(screen.getByTestId('vocabularies')).toHaveTextContent(
			'all-vocabularies'
		);

		fireEvent.click(screen.getByTestId('vocabularies'));

		await waitForElementToBeRemoved(() => screen.getByTestId('loading'));

		fireEvent.click(screen.getByRole('menuitem', {name: 'vocabulary 02'}));

		expect(screen.getByTestId('vocabularies')).toHaveTextContent(
			'vocabulary 02'
		);
	});
});
