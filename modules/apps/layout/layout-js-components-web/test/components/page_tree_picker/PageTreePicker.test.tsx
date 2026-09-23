/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fireEvent, render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import '@testing-library/jest-dom';

import PageTreePickerPanel from '../../../src/main/resources/META-INF/resources/js/components/page_tree_picker/PageTreePickerPanel';
import {
	PageTreePickerDataSource,
	PageTreePickerItem,
	PageTreePickerSelectionEntry,
} from '../../../src/main/resources/META-INF/resources/js/types/PageTreePicker';
import checkAccessibility from '../../__lib__/checkAccessibility';

const PAGE_SIZE = 2;

const PAGE_TITLES: Record<string, string> = {
	'about-us': 'About Us',
	'contact': 'Contact',
	'laptops': 'Laptops',
	'phones': 'Phones',
	'products': 'Products',
	'tablets': 'Tablets',
};

function expandPage(label: string) {
	const treeItem = screen
		.getByText(label)
		.closest('[role="treeitem"]') as HTMLElement;

	fireEvent.click(
		treeItem.querySelector('.component-expander') as HTMLElement
	);
}

function createPage(
	id: string,
	hasChildren: boolean = false
): PageTreePickerItem<string> {
	return {hasChildren, id, label: PAGE_TITLES[id], page: id};
}

function createDataSource(
	childrenByParentId: Record<string, Array<PageTreePickerItem<string>>>
): PageTreePickerDataSource<string> {
	return {
		getChildren: (parentPageTreePickerItem, page) => {
			const items =
				childrenByParentId[parentPageTreePickerItem?.id ?? 'root'] ??
				[];

			return Promise.resolve({
				items: items.slice((page - 1) * PAGE_SIZE, page * PAGE_SIZE),
				totalCount: items.length,
			});
		},
		getSubtreeCount: () => Promise.resolve(0),
		resolveItems: (items) => Promise.resolve(items),
		search: (query, page) => {
			const items = Object.values(childrenByParentId)
				.flat()
				.filter((item) =>
					item.label.toLowerCase().includes(query.toLowerCase())
				);

			return Promise.resolve({
				items: items.slice((page - 1) * PAGE_SIZE, page * PAGE_SIZE),
				totalCount: items.length,
			});
		},
	};
}

function createDefaultChildrenByParentId() {
	return {
		products: [createPage('phones'), createPage('laptops')],
		root: [createPage('products', true), createPage('about-us')],
	};
}

async function renderPageTreePicker({
	childrenByParentId = createDefaultChildrenByParentId(),
	onSelectionChange = () => {},
	...props
}: {
	childrenByParentId?: Record<string, Array<PageTreePickerItem<string>>>;
	onSelectionChange?: (
		entries: Array<PageTreePickerSelectionEntry<string>>
	) => void;
} & Partial<React.ComponentProps<typeof PageTreePickerPanel<string>>> = {}) {
	const result = render(
		<PageTreePickerPanel
			dataSource={createDataSource(childrenByParentId)}
			onSelectionChange={(entries) => onSelectionChange(entries)}
			{...props}
		/>
	);

	await screen.findByText('Products');

	return result;
}

describe('PageTreePicker', () => {
	beforeEach(() => {
		jest.clearAllMocks();
	});

	it('renders the root pages', async () => {
		await renderPageTreePicker();

		expect(screen.getByText('Products')).toBeInTheDocument();
		expect(screen.getByText('About Us')).toBeInTheDocument();
	});

	it('loads the child pages when a page is expanded', async () => {
		await renderPageTreePicker();

		expandPage('Products');

		expect(await screen.findByText('Phones')).toBeInTheDocument();
		expect(screen.getByText('Laptops')).toBeInTheDocument();
	});

	it('emits a single entry without descendants on a plain selection', async () => {
		const onSelectionChange = jest.fn();

		await renderPageTreePicker({onSelectionChange});

		await userEvent.click(screen.getByRole('checkbox', {name: 'About Us'}));

		await waitFor(() =>
			expect(onSelectionChange).toHaveBeenLastCalledWith([
				{
					excluded: false,
					includeDescendants: false,
					item: createPage('about-us'),
				},
			])
		);
	});

	it('checks the lazily loaded child pages of a shift selected page', async () => {
		await renderPageTreePicker();

		fireEvent.click(screen.getByRole('checkbox', {name: 'Products'}), {
			shiftKey: true,
		});

		expandPage('Products');

		await screen.findByText('Phones');

		expect(screen.getByRole('checkbox', {name: 'Phones'})).toBeChecked();
		expect(screen.getByRole('checkbox', {name: 'Laptops'})).toBeChecked();
	});

	it('selects the loaded child pages with a shift selection', async () => {
		const onSelectionChange = jest.fn();

		await renderPageTreePicker({onSelectionChange});

		expandPage('Products');

		await screen.findByText('Phones');

		fireEvent.click(screen.getByRole('checkbox', {name: 'Products'}), {
			shiftKey: true,
		});

		await waitFor(() =>
			expect(screen.getByRole('checkbox', {name: 'Phones'})).toBeChecked()
		);

		expect(screen.getByRole('checkbox', {name: 'Laptops'})).toBeChecked();

		expect(onSelectionChange).toHaveBeenLastCalledWith([
			{
				excluded: false,
				includeDescendants: true,
				item: expect.objectContaining({id: 'products'}),
			},
		]);
	});

	it('marks a collapsed page as including its descendant pages', async () => {
		const onSelectionChange = jest.fn();

		await renderPageTreePicker({onSelectionChange});

		fireEvent.click(screen.getByRole('checkbox', {name: 'Products'}), {
			shiftKey: true,
		});

		await waitFor(() =>
			expect(onSelectionChange).toHaveBeenLastCalledWith([
				{
					excluded: false,
					includeDescendants: true,
					item: expect.objectContaining({id: 'products'}),
				},
			])
		);
	});

	it('keeps the child pages selected when a shift selected page is deselected', async () => {
		const onSelectionChange = jest.fn();

		await renderPageTreePicker({onSelectionChange});

		expandPage('Products');

		await screen.findByText('Phones');

		fireEvent.click(screen.getByRole('checkbox', {name: 'Products'}), {
			shiftKey: true,
		});

		await waitFor(() =>
			expect(screen.getByRole('checkbox', {name: 'Phones'})).toBeChecked()
		);

		await userEvent.click(screen.getByRole('checkbox', {name: 'Products'}));

		await waitFor(() =>
			expect(
				screen.getByRole('checkbox', {name: 'Products'})
			).not.toBeChecked()
		);

		expect(screen.getByRole('checkbox', {name: 'Phones'})).toBeChecked();
		expect(screen.getByRole('checkbox', {name: 'Laptops'})).toBeChecked();

		expect(onSelectionChange).toHaveBeenLastCalledWith([
			{
				excluded: false,
				includeDescendants: true,
				item: expect.objectContaining({id: 'products'}),
			},
			{
				excluded: true,
				includeDescendants: false,
				item: expect.objectContaining({id: 'products'}),
			},
		]);
	});

	it('excludes a deselected page while its parent page tree stays selected', async () => {
		const onSelectionChange = jest.fn();

		await renderPageTreePicker({onSelectionChange});

		expandPage('Products');

		await screen.findByText('Phones');

		fireEvent.click(screen.getByRole('checkbox', {name: 'Products'}), {
			shiftKey: true,
		});

		await waitFor(() =>
			expect(screen.getByRole('checkbox', {name: 'Phones'})).toBeChecked()
		);

		await userEvent.click(screen.getByRole('checkbox', {name: 'Phones'}));

		await waitFor(() =>
			expect(
				screen.getByRole('checkbox', {name: 'Phones'})
			).not.toBeChecked()
		);

		expect(screen.getByRole('checkbox', {name: 'Products'})).toBeChecked();
		expect(screen.getByRole('checkbox', {name: 'Laptops'})).toBeChecked();

		expect(onSelectionChange).toHaveBeenLastCalledWith([
			{
				excluded: false,
				includeDescendants: true,
				item: expect.objectContaining({id: 'products'}),
			},
			{
				excluded: true,
				includeDescendants: false,
				item: expect.objectContaining({id: 'phones'}),
			},
		]);
	});

	it('selects one page at a time in the single selection mode', async () => {
		const onItemSelect = jest.fn();
		const onSelectionChange = jest.fn();

		await renderPageTreePicker({
			onItemSelect,
			onSelectionChange,
			selectionMode: 'single',
		});

		expect(screen.queryByRole('checkbox')).not.toBeInTheDocument();

		await userEvent.click(screen.getByText('Products'));

		expect(onItemSelect).toHaveBeenLastCalledWith(
			expect.objectContaining({id: 'products'})
		);

		await userEvent.click(screen.getByText('About Us'));

		expect(onItemSelect).toHaveBeenLastCalledWith(
			expect.objectContaining({id: 'about-us'})
		);

		await waitFor(() =>
			expect(onSelectionChange).toHaveBeenLastCalledWith([
				{
					excluded: false,
					includeDescendants: false,
					item: expect.objectContaining({id: 'about-us'}),
				},
			])
		);
	});

	it('does not highlight a checked page in the multiple selection mode', async () => {
		const {container} = await renderPageTreePicker();

		await userEvent.click(screen.getByRole('checkbox', {name: 'Products'}));

		expect(screen.getByRole('checkbox', {name: 'Products'})).toBeChecked();

		expect(container.querySelector('.treeview-link.active')).toBeNull();
	});

	it('renders a disabled page as disabled', async () => {
		const childrenByParentId = createDefaultChildrenByParentId();

		childrenByParentId.root = [
			createPage('products', true),
			{...createPage('contact'), disabled: true},
		];

		await renderPageTreePicker({childrenByParentId});

		expect(
			screen.getByText('Contact').closest('[role="treeitem"]')
		).toHaveClass('disabled');

		expect(
			screen.queryByRole('checkbox', {name: 'Contact'})
		).not.toBeInTheDocument();
	});

	it('renders the badge and the title of a page', async () => {
		const badgedItem = {
			...createPage('contact'),
			badge: {label: 'Restricted Page', symbol: 'password-policies'},
			title: '/c',
		};

		const childrenByParentId = createDefaultChildrenByParentId();

		childrenByParentId.root = [createPage('products', true), badgedItem];

		await renderPageTreePicker({childrenByParentId});

		expect(await screen.findByTitle('/c')).toHaveTextContent('Contact');

		expect(screen.getByTitle('Restricted Page')).toBeInTheDocument();
	});

	it('notifies when the last page is deselected', async () => {
		const onSelectionChange = jest.fn();

		await renderPageTreePicker({onSelectionChange});

		await userEvent.click(screen.getByRole('checkbox', {name: 'About Us'}));

		await userEvent.click(screen.getByRole('checkbox', {name: 'About Us'}));

		await waitFor(() =>
			expect(onSelectionChange).toHaveBeenLastCalledWith([])
		);
	});

	it('loads more child pages on demand', async () => {
		await renderPageTreePicker({
			childrenByParentId: {
				products: [
					createPage('phones'),
					createPage('laptops'),
					createPage('tablets'),
				],
				root: [createPage('products', true)],
			},
		});

		expandPage('Products');

		await screen.findByText('Laptops');

		expect(screen.queryByText('Tablets')).not.toBeInTheDocument();

		await userEvent.click(
			screen.getByRole('button', {name: 'load-more-results'})
		);

		expect(await screen.findByText('Tablets')).toBeInTheDocument();

		expect(
			screen.queryByRole('button', {name: 'load-more-results'})
		).not.toBeInTheDocument();
	});

	it('loads more root pages on demand', async () => {
		await renderPageTreePicker({
			childrenByParentId: {
				root: [
					createPage('products'),
					createPage('about-us'),
					createPage('contact'),
				],
			},
		});

		expect(screen.queryByText('Contact')).not.toBeInTheDocument();

		await userEvent.click(
			screen.getByRole('button', {name: 'load-more-results'})
		);

		expect(await screen.findByText('Contact')).toBeInTheDocument();
	});

	it('has no accessibility violations', async () => {
		const {container} = await renderPageTreePicker();

		await checkAccessibility({
			bestPractices: true,
			context: {
				exclude: ['.component-expander', '[aria-owns]'],
				include: [container],
			},
		});
	});
});
