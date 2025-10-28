/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {act, fireEvent, render, screen} from '@testing-library/react';
import React from 'react';
import {DndProvider} from 'react-dnd';
import {HTML5Backend} from 'react-dnd-html5-backend';

import TabCollection from '../../../../../../src/main/resources/META-INF/resources/page_editor/plugins/fragments_and_widgets/components/TabCollection';

const COLLECTION = {
	children: [
		{
			data: {
				fragmentEntryKey: 'container',
			},
			highlighted: false,
			icon: 'container',
			itemId: 'container',
			label: 'Container',
			type: 'container',
		},
		{
			data: {
				fragmentEntryKey: 'row',
			},
			highlighted: false,
			icon: 'table',
			itemId: 'row',
			label: 'Grid',
			type: 'row',
		},
	],
	collectionId: 'layout-elements',
	label: 'Layout Elements',
};

const renderComponent = ({initialOpen = true} = {}) => {
	return render(
		<DndProvider backend={HTML5Backend}>
			<div role="menubar">
				<TabCollection
					collection={COLLECTION}
					displayStyle="list"
					initialOpen={initialOpen}
				/>
			</div>
		</DndProvider>
	);
};

describe('TabCollection', () => {
	it('renders the list of fragments', () => {
		renderComponent();

		expect(screen.getByText('Container')).toBeInTheDocument();
		expect(screen.getByText('Grid')).toBeInTheDocument();
	});

	it('moves to next item when pressing down arrow with an item focused', async () => {
		renderComponent();

		const items = screen.getAllByRole('menuitem');

		const firstItem = items[0];
		const secondItem = items[1];

		act(() => firstItem.focus());

		await fireEvent(
			firstItem,
			new KeyboardEvent('keydown', {
				code: 'ArrowDown',
			})
		);

		expect(secondItem).toHaveFocus();
	});

	it('moves to previous item when pressing up arrow with an item focused', async () => {
		renderComponent();

		const items = screen.getAllByRole('menuitem');

		const firstItem = items[0];
		const secondItem = items[1];

		act(() => secondItem.focus());

		await fireEvent(
			secondItem,
			new KeyboardEvent('keydown', {
				code: 'ArrowUp',
			})
		);

		expect(firstItem).toHaveFocus();
	});

	it('moves to add button when pressing right arrow with a fragment focused', async () => {
		renderComponent();

		const items = screen.getAllByRole('menuitem');

		const secondItem = items[1];
		const addButton = secondItem.querySelector(
			'.page-editor__fragments-widgets__tab__add-button'
		);

		act(() => secondItem.focus());

		await fireEvent(
			secondItem,
			new KeyboardEvent('keydown', {
				code: 'ArrowRight',
			})
		);

		expect(addButton).toHaveFocus();
	});

	it('opens collapse when pressing right arrow', async () => {
		renderComponent({initialOpen: false});

		const items = screen.getAllByRole('menuitem');

		const firstItem = items[0];

		act(() => firstItem.focus());

		await fireEvent(
			firstItem,
			new KeyboardEvent('keydown', {
				code: 'ArrowRight',
			})
		);

		expect(firstItem).toHaveAttribute('aria-expanded', 'true');
	});

	it('closes collapse when pressing left arrow', async () => {
		renderComponent();

		const items = screen.getAllByRole('menuitem');

		const firstItem = items[0];

		act(() => firstItem.focus());

		await fireEvent(
			firstItem,
			new KeyboardEvent('keydown', {
				code: 'ArrowLeft',
			})
		);

		expect(firstItem).toHaveAttribute('aria-expanded', 'false');
	});
});
