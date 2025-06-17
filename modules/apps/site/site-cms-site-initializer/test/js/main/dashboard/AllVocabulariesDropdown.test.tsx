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
import {AllVocabulariesDropdown} from '../../../../src/main/resources/META-INF/resources/js/main/dashboard/components/AllVocabulariesDropdown';
import {Item} from '../../../../src/main/resources/META-INF/resources/js/main/dashboard/components/FilterDropdown';
import {initialVocabulary} from '../../../../src/main/resources/META-INF/resources/js/main/dashboard/components/InventoryAnalysisCard';

const mockVocabularies = (items: {id: string; name: string}[] = []) => {
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
		React.useState<Item>(initialVocabulary);

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
	afterEach(() => {
		jest.clearAllMocks();
	});

	it('renders correctly', async () => {
		mockVocabularies();

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
		mockVocabularies([
			{
				id: '01',
				name: 'vocabulary 01',
			},
			{
				id: '02',
				name: 'vocabulary 02',
			},
		]);

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

	xit('search by a vocabulary and returns a filtered result', async () => {
		mockVocabularies([
			{id: '01', name: 'vocabulary 01'},
			{id: '02', name: 'vocabulary 02'},
		]);

		render(<WrappedComponent onSelectItem={jest.fn()} />);

		const vocabulariesDropdownButton = screen.getByRole('button', {
			name: 'all-vocabularies',
		});

		fireEvent.click(vocabulariesDropdownButton);

		await waitForElementToBeRemoved(() => screen.getByTestId('loading'));

		expect(screen.getAllByRole('menuitem').length).toBe(3);

		mockVocabularies([{id: '02', name: 'vocabulary 02'}]);

		await userEvent.type(
			screen.getByPlaceholderText('search'),
			'vocabulary 02'
		);

		await waitFor(
			() => {
				expect(screen.getAllByRole('menuitem').length).toBe(1);

				expect(
					screen.queryByRole('menuitem', {name: 'all-vocabularies'})
				).not.toBeInTheDocument();

				expect(
					screen.queryByRole('menuitem', {name: 'vocabulary 01'})
				).not.toBeInTheDocument();

				expect(
					screen.queryByRole('menuitem', {name: 'vocabulary 02'})
				).toBeInTheDocument();
			},
			{timeout: 100}
		);
	});

	xit('search by a vocabulary and returns a empty result', async () => {
		mockVocabularies([
			{id: '01', name: 'vocabulary 01'},
			{id: '02', name: 'vocabulary 02'},
		]);

		render(<WrappedComponent onSelectItem={jest.fn()} />);

		const vocabulariesDropdownButton = screen.getByRole('button', {
			name: 'all-vocabularies',
		});

		fireEvent.click(vocabulariesDropdownButton);

		await waitForElementToBeRemoved(() => screen.getByTestId('loading'));

		expect(screen.getAllByRole('menuitem').length).toBe(3);

		mockVocabularies();

		await userEvent.type(screen.getByPlaceholderText('search'), 'empty?');

		await waitFor(
			() => {
				expect(screen.getAllByRole('menuitem').length).toBe(1);

				expect(
					screen.queryByRole('menuitem', {name: 'all-vocabularies'})
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

	it('selects a new vocabulary', async () => {
		mockVocabularies([
			{id: '01', name: 'vocabulary 01'},
			{id: '02', name: 'vocabulary 02'},
		]);

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
