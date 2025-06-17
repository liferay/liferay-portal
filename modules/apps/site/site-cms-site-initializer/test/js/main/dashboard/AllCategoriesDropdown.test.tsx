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
import React from 'react';

import {ViewDashboardContextProvider} from '../../../../src/main/resources/META-INF/resources/js/main/dashboard/ViewDashboardContext';
import {
	AllCategoriesDropdown,
	CategoryData,
} from '../../../../src/main/resources/META-INF/resources/js/main/dashboard/components/AllCategoriesDropdown';
import {Item} from '../../../../src/main/resources/META-INF/resources/js/main/dashboard/components/FilterDropdown';

const mockCategories = (items: CategoryData[] = []) => {
	global.fetch = jest.fn().mockReturnValue({
		json: jest.fn().mockReturnValue({items}),
		ok: true,
	});
};

const WrappedComponent = ({
	onSelectItem,
}: {
	onSelectItem: (item: Item) => void;
}) => (
	<ViewDashboardContextProvider value={{}}>
		<AllCategoriesDropdown
			item={{
				label: Liferay.Language.get('all-categories'),
				value: 'all',
			}}
			onSelectItem={onSelectItem}
		/>
	</ViewDashboardContextProvider>
);

describe('[CMS Dashboard] Components: AllCategoriesDropdown', () => {
	afterEach(() => {
		jest.clearAllMocks();
	});

	it('renders correctly', async () => {
		mockCategories();

		const onSelectItem = jest.fn();

		render(<WrappedComponent onSelectItem={onSelectItem} />);

		const button = screen.getByRole('button', {
			name: 'all-categories',
		});

		expect(button).toBeInTheDocument();

		fireEvent.click(button);

		expect(screen.queryByText('filter-by-category')).toBeInTheDocument();

		expect(screen.queryByPlaceholderText('search')).toBeInTheDocument();

		await waitForElementToBeRemoved(() => screen.getByTestId('loading'));

		expect(screen.getAllByRole('menuitem').length).toBe(1);

		const menuitem = screen.getByRole('menuitem', {
			name: 'all-categories',
		});

		expect(menuitem).toBeInTheDocument();

		fireEvent.click(menuitem);

		expect(onSelectItem).toHaveBeenCalledTimes(1);
		expect(onSelectItem).toHaveBeenCalledWith({
			label: 'all-categories',
			value: 'all',
		});
	});

	it('renders a vocabulary list in the dropdown if there is a numberOfTaxonomyCategories > 0', async () => {
		mockCategories([
			{
				assetLibraries: [{id: -1}],
				id: 1,
				name: 'vocabulary 01',
				numberOfTaxonomyCategories: 1,
			},
			{
				assetLibraries: [{id: -1}],
				id: 2,
				name: 'vocabulary 02',
				numberOfTaxonomyCategories: 1,
			},
		]);

		render(<WrappedComponent onSelectItem={jest.fn()} />);

		const button = screen.getByRole('button', {
			name: 'all-categories',
		});

		fireEvent.click(button);

		await waitForElementToBeRemoved(() => screen.getByTestId('loading'));

		expect(screen.getAllByRole('menuitem').length).toBe(3);

		expect(
			screen.queryByRole('menuitem', {name: 'all-categories'})
		).toBeInTheDocument();

		expect(
			screen.queryByRole('menuitem', {name: 'vocabulary 01'})
		).toBeInTheDocument();

		expect(
			screen.queryByRole('menuitem', {name: 'vocabulary 02'})
		).toBeInTheDocument();
	});

	it('does not render a vocabulary list in the dropdown if there is a numberOfTaxonomyCategories === 0', async () => {
		mockCategories([
			{
				assetLibraries: [{id: -1}],
				id: 1,
				name: 'vocabulary 01',
				numberOfTaxonomyCategories: 0,
			},
			{
				assetLibraries: [{id: -1}],
				id: 2,
				name: 'vocabulary 02',
				numberOfTaxonomyCategories: 0,
			},
		]);

		render(<WrappedComponent onSelectItem={jest.fn()} />);

		const button = screen.getByRole('button', {
			name: 'all-categories',
		});

		fireEvent.click(button);

		await waitForElementToBeRemoved(() => screen.getByTestId('loading'));

		expect(screen.getAllByRole('menuitem').length).toBe(1);

		expect(
			screen.queryByRole('menuitem', {name: 'all-categories'})
		).toBeInTheDocument();
	});

	xit('navigates to drill down and selects a category', async () => {
		const onSelectItem = jest.fn();

		mockCategories([
			{
				assetLibraries: [{id: -1}],
				id: 1,
				name: 'vocabulary 01',
				numberOfTaxonomyCategories: 1,
			},
		]);

		render(<WrappedComponent onSelectItem={onSelectItem} />);

		const button = screen.getByRole('button', {
			name: 'all-categories',
		});

		fireEvent.click(button);

		await waitForElementToBeRemoved(() => screen.getByTestId('loading'));

		expect(screen.getAllByRole('menuitem').length).toBe(2);

		mockCategories([
			{
				assetLibraries: [{id: -1}],
				id: 101,
				name: 'category 01',
				numberOfTaxonomyCategories: 0,
				parentTaxonomyVocabulary: {
					id: 1,
					name: 'parent vocabulary 01',
				},
			},
			{
				assetLibraries: [{id: -1}],
				id: 202,
				name: 'category 02',
				numberOfTaxonomyCategories: 0,
				parentTaxonomyVocabulary: {
					id: 2,
					name: 'parent vocabulary 02',
				},
			},
		]);

		fireEvent.click(screen.getByRole('menuitem', {name: 'vocabulary 01'}));

		expect(onSelectItem).toHaveBeenCalledTimes(0);

		await waitFor(
			() => {
				expect(screen.getAllByRole('menuitem').length).toBe(2);

				expect(
					screen.getByText('parent vocabulary 01')
				).toBeInTheDocument();

				expect(
					screen.getByRole('menuitem', {name: 'category 01'})
				).toBeInTheDocument();

				expect(
					screen.getByRole('menuitem', {name: 'category 02'})
				).toBeInTheDocument();

				fireEvent.click(
					screen.getByRole('menuitem', {name: 'category 01'})
				);

				expect(onSelectItem).toHaveBeenCalledTimes(1);

				expect(onSelectItem).toHaveBeenCalledWith({
					label: 'category 01',
					value: '101',
				});
			},
			{timeout: 100}
		);
	});

	xit('navigates to drill down, renders a category list and go back to the vocabulary list', async () => {
		const onSelectItem = jest.fn();

		mockCategories([
			{
				assetLibraries: [{id: -1}],
				id: 1,
				name: 'vocabulary 01',
				numberOfTaxonomyCategories: 1,
			},
		]);

		render(<WrappedComponent onSelectItem={onSelectItem} />);

		const button = screen.getByRole('button', {
			name: 'all-categories',
		});

		fireEvent.click(button);

		await waitForElementToBeRemoved(() => screen.getByTestId('loading'));

		expect(screen.getAllByRole('menuitem').length).toBe(2);

		mockCategories([
			{
				assetLibraries: [{id: -1}],
				id: 101,
				name: 'category 01',
				numberOfTaxonomyCategories: 0,
				parentTaxonomyVocabulary: {
					id: 1,
					name: 'parent vocabulary 01',
				},
			},
			{
				assetLibraries: [{id: -1}],
				id: 202,
				name: 'category 02',
				numberOfTaxonomyCategories: 0,
				parentTaxonomyVocabulary: {
					id: 2,
					name: 'parent vocabulary 02',
				},
			},
		]);

		fireEvent.click(screen.getByRole('menuitem', {name: 'vocabulary 01'}));

		expect(onSelectItem).toHaveBeenCalledTimes(0);

		await waitFor(
			() => {
				expect(
					screen.queryByText('filter-by-category')
				).not.toBeInTheDocument();

				expect(screen.getAllByRole('menuitem').length).toBe(2);

				expect(
					screen.getByText('parent vocabulary 01')
				).toBeInTheDocument();

				expect(
					screen.getByRole('menuitem', {name: 'category 01'})
				).toBeInTheDocument();

				expect(
					screen.getByRole('menuitem', {name: 'category 02'})
				).toBeInTheDocument();

				fireEvent.click(screen.getByTestId('cancel-button'));

				expect(onSelectItem).toHaveBeenCalledTimes(0);

				expect(screen.getAllByRole('menuitem').length).toBe(2);

				expect(
					screen.queryByText('filter-by-category')
				).toBeInTheDocument();

				expect(
					screen.getByRole('menuitem', {name: 'vocabulary 01'})
				).toBeInTheDocument();
			},
			{timeout: 100}
		);
	});
});
