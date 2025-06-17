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
import {AllTagsDropdown} from '../../../../src/main/resources/META-INF/resources/js/main/dashboard/components/AllTagsDropdown';
import {Item} from '../../../../src/main/resources/META-INF/resources/js/main/dashboard/components/FilterDropdown';
import {initialTag} from '../../../../src/main/resources/META-INF/resources/js/main/dashboard/components/InventoryAnalysisCard';

const mockTags = (items: {id: string; name: string}[] = []) => {
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
	const [selectedItem, setSelectedItem] = React.useState<Item>(initialTag);

	return (
		<ViewDashboardContextProvider value={{}}>
			<AllTagsDropdown
				item={selectedItem}
				onSelectItem={(item) => {
					setSelectedItem(item);
					onSelectItem(item);
				}}
			/>
		</ViewDashboardContextProvider>
	);
};

describe('[CMS Dashboard] Components: AllTagsDropdown', () => {
	afterEach(() => {
		jest.clearAllMocks();
	});

	it('renders correctly', async () => {
		mockTags();

		const onSelectItem = jest.fn();

		render(<WrappedComponent onSelectItem={onSelectItem} />);

		const tagsDropdownButton = screen.getByRole('button', {
			name: 'all-tags',
		});

		expect(tagsDropdownButton).toBeInTheDocument();

		fireEvent.click(tagsDropdownButton);

		expect(screen.queryByText('filter-by-tag')).toBeInTheDocument();

		expect(screen.queryByPlaceholderText('search')).toBeInTheDocument();

		await waitForElementToBeRemoved(() => screen.getByTestId('loading'));

		expect(screen.getAllByRole('menuitem').length).toBe(1);

		const menuitem = screen.getByRole('menuitem', {
			name: 'all-tags',
		});

		expect(menuitem).toBeInTheDocument();

		fireEvent.click(menuitem);

		expect(onSelectItem).toHaveBeenCalledTimes(1);

		expect(onSelectItem).toHaveBeenCalledWith({
			label: 'all-tags',
			value: 'all',
		});
	});

	it('renders a tag list', async () => {
		mockTags([
			{
				id: '01',
				name: 'tag 01',
			},
			{
				id: '02',
				name: 'tag 02',
			},
		]);

		render(<WrappedComponent onSelectItem={jest.fn()} />);

		const tagsDropdownButton = screen.getByRole('button', {
			name: 'all-tags',
		});

		fireEvent.click(tagsDropdownButton);

		await waitForElementToBeRemoved(() => screen.getByTestId('loading'));

		expect(screen.getAllByRole('menuitem').length).toBe(3);

		expect(
			screen.queryByRole('menuitem', {name: 'all-tags'})
		).toBeInTheDocument();

		expect(
			screen.queryByRole('menuitem', {name: 'tag 01'})
		).toBeInTheDocument();

		expect(
			screen.queryByRole('menuitem', {name: 'tag 02'})
		).toBeInTheDocument();
	});

	xit('search by a tag and returns a filtered result', async () => {
		mockTags([
			{id: '01', name: 'tag 01'},
			{id: '02', name: 'tag 02'},
		]);

		render(<WrappedComponent onSelectItem={jest.fn()} />);

		const tagsDropdownButton = screen.getByRole('button', {
			name: 'all-tags',
		});

		fireEvent.click(tagsDropdownButton);

		await waitForElementToBeRemoved(() => screen.getByTestId('loading'));

		expect(screen.getAllByRole('menuitem').length).toBe(3);

		mockTags([{id: '02', name: 'tag 02'}]);

		await userEvent.type(screen.getByPlaceholderText('search'), 'tag 02');

		await waitFor(
			() => {
				expect(screen.getAllByRole('menuitem').length).toBe(1);

				expect(
					screen.queryByRole('menuitem', {name: 'all-tags'})
				).not.toBeInTheDocument();

				expect(
					screen.queryByRole('menuitem', {name: 'tag 01'})
				).not.toBeInTheDocument();

				expect(
					screen.queryByRole('menuitem', {name: 'tag 02'})
				).toBeInTheDocument();
			},
			{timeout: 100}
		);
	});

	xit('search by a tag and returns a empty result', async () => {
		mockTags([
			{id: '01', name: 'tag 01'},
			{id: '02', name: 'tag 02'},
		]);

		render(<WrappedComponent onSelectItem={jest.fn()} />);

		const tagsDropdownButton = screen.getByRole('button', {
			name: 'all-tags',
		});

		fireEvent.click(tagsDropdownButton);

		await waitForElementToBeRemoved(() => screen.getByTestId('loading'));

		expect(screen.getAllByRole('menuitem').length).toBe(3);

		mockTags();

		await userEvent.type(screen.getByPlaceholderText('search'), 'empty?');

		await waitFor(
			() => {
				expect(screen.getAllByRole('menuitem').length).toBe(1);

				expect(
					screen.queryByRole('menuitem', {name: 'all-tags'})
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

	it('selects a new tag', async () => {
		mockTags([
			{id: '01', name: 'tag 01'},
			{id: '02', name: 'tag 02'},
		]);

		render(<WrappedComponent onSelectItem={() => {}} />);

		expect(screen.getByTestId('tags')).toHaveTextContent('all-tags');

		fireEvent.click(screen.getByTestId('tags'));

		await waitForElementToBeRemoved(() => screen.getByTestId('loading'));

		fireEvent.click(screen.getByRole('menuitem', {name: 'tag 02'}));

		expect(screen.getByTestId('tags')).toHaveTextContent('tag 02');
	});
});
