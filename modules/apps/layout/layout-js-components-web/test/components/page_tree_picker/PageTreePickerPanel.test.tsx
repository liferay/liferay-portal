/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fireEvent, render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import '@testing-library/jest-dom';
import {openToast} from 'frontend-js-components-web';

import PageTreePickerPanel from '../../../src/main/resources/META-INF/resources/js/components/page_tree_picker/PageTreePickerPanel';
import {
	PageTreePickerDataSource,
	PageTreePickerItem,
} from '../../../src/main/resources/META-INF/resources/js/types/PageTreePicker';
import checkAccessibility from '../../__lib__/checkAccessibility';

jest.mock('frontend-js-components-web', () => ({
	...(jest.requireActual('frontend-js-components-web') as object),
	openToast: jest.fn(),
}));

const PAGE_TITLES: Record<string, string> = {
	'about-us': 'About Us',
	'contact': 'Contact',
	'laptops': 'Laptops',
	'phones': 'Phones',
	'products': 'Products',
	'tablets': 'Tablets',
};

function createPage(
	id: string,
	hasChildren: boolean = false
): PageTreePickerItem<string> {
	return {
		hasChildren,
		id,
		label: PAGE_TITLES[id],
		page: id,
		path: ['Site', PAGE_TITLES[id]],
	};
}

const DATA_SOURCE: PageTreePickerDataSource<string> = {
	getChildren: () =>
		Promise.resolve({
			items: [createPage('products', true), createPage('about-us')],
			totalCount: 2,
		}),
	search: (query) => {
		const items = [
			createPage('products', true),
			createPage('about-us'),
		].filter((item) =>
			item.label.toLowerCase().includes(query.toLowerCase())
		);

		return Promise.resolve({items, totalCount: items.length});
	},
};

async function renderPageTreePickerPanel({onSelectionChange = () => {}} = {}) {
	const result = render(
		<PageTreePickerPanel
			dataSource={DATA_SOURCE}
			onSelectionChange={onSelectionChange}
		/>
	);

	await screen.findByText('Products');

	return result;
}

describe('PageTreePickerPanel', () => {
	beforeEach(() => {
		jest.clearAllMocks();

		(Liferay.Language.get as jest.Mock).mockImplementation(
			(key: string) => {
				if (key === 'x-item-selected') {
					return '{0} item-selected';
				}

				if (key === 'x-items-selected') {
					return '{0} items-selected';
				}

				return key;
			}
		);
	});

	afterEach(() => {
		jest.restoreAllMocks();

		(Liferay.Language.get as jest.Mock).mockImplementation(
			(key: string) => key
		);
	});

	it('renders the tree', async () => {
		await renderPageTreePickerPanel();

		expect(screen.getByText('About Us')).toBeInTheDocument();
	});

	it('replaces the tree with highlighted search results', async () => {
		await renderPageTreePickerPanel();

		await userEvent.type(screen.getByRole('textbox'), 'About Us');

		expect(
			await screen.findByText('About Us', {selector: 'mark'})
		).toBeInTheDocument();

		expect(screen.queryByRole('treeitem')).not.toBeInTheDocument();
		expect(screen.getByText('Site')).toBeInTheDocument();
	});

	it('hides the tree as soon as the user starts a search', async () => {
		const search = jest.spyOn(DATA_SOURCE, 'search');

		await renderPageTreePickerPanel();

		await userEvent.type(screen.getByRole('textbox'), 'About Us');

		expect(screen.queryByRole('treeitem')).not.toBeInTheDocument();
		expect(search).not.toHaveBeenCalled();

		expect(
			await screen.findByText('About Us', {selector: 'mark'})
		).toBeInTheDocument();

		expect(search).toHaveBeenCalledTimes(1);

		search.mockRestore();
	});

	it('reports the selection made from the search results', async () => {
		const onSelectionChange = jest.fn();

		await renderPageTreePickerPanel({onSelectionChange});

		await userEvent.type(screen.getByRole('textbox'), 'About Us');

		await screen.findByText('About Us', {selector: 'mark'});

		await userEvent.click(screen.getByRole('checkbox'));

		await waitFor(() =>
			expect(onSelectionChange).toHaveBeenLastCalledWith(
				[
					{
						excluded: false,
						includeDescendants: false,
						item: expect.objectContaining({id: 'about-us'}),
					},
				],
				expect.any(Array)
			)
		);
	});

	it('reports a failed count instead of showing an inexact one', async () => {
		render(
			<PageTreePickerPanel
				dataSource={{
					getChildren: DATA_SOURCE.getChildren,
					getSubtreeCount: () => Promise.reject(new Error()),
					search: DATA_SOURCE.search,
				}}
			/>
		);

		await screen.findByText('Products');

		fireEvent.click(screen.getByRole('checkbox', {name: 'Products'}), {
			shiftKey: true,
		});

		await waitFor(() =>
			expect(openToast).toHaveBeenCalledWith(
				expect.objectContaining({type: 'danger'})
			)
		);

		expect(screen.queryByText(/item-selected/)).not.toBeInTheDocument();
		expect(screen.queryByText('nothing-selected')).not.toBeInTheDocument();
	});

	it('lets the consumer handle load errors instead of showing a toast', async () => {
		const error = new Error();
		const onError = jest.fn();

		render(
			<PageTreePickerPanel
				dataSource={{
					getChildren: () => Promise.reject(error),
					search: DATA_SOURCE.search,
				}}
				onError={onError}
			/>
		);

		await waitFor(() => expect(onError).toHaveBeenCalledWith(error));

		expect(openToast).not.toHaveBeenCalled();
	});

	it('shows the selection count while pages are selected', async () => {
		await renderPageTreePickerPanel();

		await userEvent.click(screen.getByRole('checkbox', {name: 'About Us'}));

		expect(screen.getByText('1 item-selected')).toBeInTheDocument();

		await userEvent.click(screen.getByRole('checkbox', {name: 'About Us'}));

		await waitFor(() =>
			expect(screen.queryByText(/item-selected/)).not.toBeInTheDocument()
		);
	});

	it('has no accessibility violations', async () => {
		const {container} = await renderPageTreePickerPanel();

		await checkAccessibility({
			bestPractices: true,
			context: {
				exclude: ['.component-expander', '[aria-owns]'],
				include: [container],
			},
		});
	});
});
