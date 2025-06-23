/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {State} from '@liferay/frontend-js-state-web';
import {fireEvent, render, screen} from '@testing-library/react';
import React from 'react';

import {pageContentsAtom} from '../../../../src/main/resources/META-INF/resources/page_editor/app/utils/usePageContents';
import ItemSelector from '../../../../src/main/resources/META-INF/resources/page_editor/common/components/ItemSelector';
import {openItemSelector} from '../../../../src/main/resources/META-INF/resources/page_editor/common/openItemSelector';

jest.mock(
	'../../../../src/main/resources/META-INF/resources/page_editor/app/config/index',
	() => ({
		config: {
			infoItemSelectorUrl: 'infoItemSelectorUrl',
			portletNamespace: 'portletNamespace',
		},
	})
);

jest.mock(
	'../../../../src/main/resources/META-INF/resources/page_editor/common/openItemSelector',
	() => ({
		openItemSelector: jest.fn(() => {}),
	})
);

function renderItemSelector({
	pageContents = [],
	selectedItemClassPK = '',
	selectedItemTitle = '',
}) {
	State.writeAtom(pageContentsAtom, {
		data: pageContents,
		status: 'saved',
	});

	return render(
		<ItemSelector
			label="itemSelectorLabel"
			onItemSelect={() => {}}
			selectedItem={
				selectedItemTitle
					? {
							classPK: selectedItemClassPK,
							title: selectedItemTitle,
						}
					: null
			}
			transformValueCallback={() => {}}
		/>
	);
}

describe('ItemSelector', () => {
	beforeEach(() => {
		State.writeAtom(pageContentsAtom, {
			data: [],
			status: 'idle',
		});
	});

	afterEach(() => {
		openItemSelector.mockClear();
	});

	it('renders correctly', () => {
		renderItemSelector({});

		expect(screen.getByText('itemSelectorLabel')).toBeInTheDocument();
	});

	it('renders the placeholder correctly', () => {
		renderItemSelector({});

		expect(
			screen.getByPlaceholderText('no-itemSelectorLabel-selected')
		).toBeInTheDocument();
	});

	it('renders the aria label button correctly when no item is selected', () => {
		renderItemSelector({});

		expect(
			screen.getByLabelText('select-itemSelectorLabel')
		).toBeInTheDocument();
	});

	it('renders the aria label button correctly when an item is selected', () => {
		const selectedItemTitle = 'itemTitle';

		renderItemSelector({selectedItemTitle});

		expect(
			screen.getByLabelText('change-itemSelectorLabel')
		).toBeInTheDocument();
	});

	it('shows selected item title correctly when receiving it in props', () => {
		const selectedItemTitle = 'itemTitle';

		renderItemSelector({
			selectedItemTitle,
		});

		expect(screen.getByLabelText('itemSelectorLabel')).toHaveValue(
			selectedItemTitle
		);
	});

	it('does not show any title when not receiving it in props', () => {
		renderItemSelector({});

		expect(screen.getByLabelText('itemSelectorLabel')).toBeEmpty();
	});

	it('calls openItemSelector when there are not mapping items and plus button is clicked', () => {
		renderItemSelector({});

		fireEvent.click(screen.getByLabelText('select-itemSelectorLabel'));

		expect(openItemSelector).toBeCalled();
	});

	it('shows recent items dropdown instead of calling openItemSelector when there are mapping items', () => {
		const pageContents = [
			{classNameId: '001', classPK: '002', title: 'Mapped Item Title'},
		];

		renderItemSelector({
			pageContents,
		});

		fireEvent.click(screen.getByLabelText('select-itemSelectorLabel'));

		expect(screen.getByText('Mapped Item Title')).toBeInTheDocument();

		expect(openItemSelector).not.toBeCalled();
	});

	it('does not show recent collection page contents in the recent items', () => {
		const pageContents = [
			{classNameId: '001', classPK: '002', title: 'Mapped Item Title'},
			{
				classNameId: '29536',
				classPK: '33175',
				subtype: 'Web Content Article - Basic Web Content',
				title: 'Mapped Collection',
				type: Liferay.Language.get('collection'),
			},
		];

		renderItemSelector({
			pageContents,
		});

		fireEvent.click(screen.getByLabelText('select-itemSelectorLabel'));

		expect(screen.getByText('Mapped Item Title')).toBeInTheDocument();
		expect(screen.queryByText('Mapped Collection')).not.toBeInTheDocument();

		expect(openItemSelector).not.toBeCalled();
	});

	it('removes selected item correctly when clear button is clicked', () => {
		const selectedItemTitle = 'itemTitle';

		renderItemSelector({
			selectedItemTitle,
		});

		fireEvent.click(screen.getByText('remove-itemSelectorLabel'));

		expect(screen.getByLabelText('itemSelectorLabel')).toBeEmpty();
	});

	it('adds addItem content-related option if possible', () => {
		renderItemSelector({
			pageContents: [
				{
					actions: {
						addItems: [
							{
								href: 'http://me.local/addItemOneURL',
								label: 'Add Item One',
							},
						],
					},
					classPK: 'sampleItem-classPK',
					title: 'itemTitle',
				},
			],
			selectedItemClassPK: 'sampleItem-classPK',
			selectedItemTitle: 'itemTitle',
		});

		const addSubMenuButton = screen.getByText('add-items');

		expect(addSubMenuButton).toBeInTheDocument();
		expect(addSubMenuButton.tagName).toBe('BUTTON');

		const addItemLink = screen.getByText('Add Item One');

		expect(addItemLink).toBeInTheDocument();
		expect(addItemLink.href).toBe('http://me.local/addItemOneURL');
	});

	it('adds editURL content-related option if possible', () => {
		renderItemSelector({
			pageContents: [
				{
					actions: {editURL: 'http://me.local/editURL'},
					classPK: 'sampleItem-classPK',
					title: 'itemTitle',
				},
			],
			selectedItemClassPK: 'sampleItem-classPK',
			selectedItemTitle: 'itemTitle',
		});

		const editItemLink = screen.getByText('edit-itemSelectorLabel');

		expect(editItemLink).toBeInTheDocument();
		expect(editItemLink.href).toBe('http://me.local/editURL');
	});

	it('adds permissionsURL content-related option if possible', () => {
		renderItemSelector({
			pageContents: [
				{
					actions: {permissionsURL: 'http://me.local/permissionsURL'},
					classPK: 'sampleItem-classPK',
					title: 'itemTitle',
				},
			],
			selectedItemClassPK: 'sampleItem-classPK',
			selectedItemTitle: 'itemTitle',
		});

		const editItemButton = screen.getByText(
			'edit-itemSelectorLabel-permissions'
		);

		expect(editItemButton).toBeInTheDocument();
		expect(editItemButton.tagName).toBe('BUTTON');
	});

	it('adds viewItemsURL content-related option if possible', () => {
		renderItemSelector({
			pageContents: [
				{
					actions: {viewItemsURL: 'http://me.local/viewItemsURL'},
					classPK: 'sampleItem-classPK',
					title: 'itemTitle',
				},
			],
			selectedItemClassPK: 'sampleItem-classPK',
			selectedItemTitle: 'itemTitle',
		});

		const viewItemsButton = screen.getByText('view-items');

		expect(viewItemsButton).toBeInTheDocument();
		expect(viewItemsButton.tagName).toBe('BUTTON');
	});

	it('adds viewUsagesURL content-related option if possible', () => {
		renderItemSelector({
			pageContents: [
				{
					actions: {viewUsagesURL: 'http://me.local/viewUsagesURL'},
					classPK: 'sampleItem-classPK',
					title: 'itemTitle',
				},
			],
			selectedItemClassPK: 'sampleItem-classPK',
			selectedItemTitle: 'itemTitle',
		});

		const viewUsagesButton = screen.getByText(
			'view-itemSelectorLabel-usages'
		);

		expect(viewUsagesButton).toBeInTheDocument();
		expect(viewUsagesButton.tagName).toBe('BUTTON');
	});
});
