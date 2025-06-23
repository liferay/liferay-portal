/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {DndProvider} from 'react-dnd';
import {HTML5Backend} from 'react-dnd-html5-backend';

import '@testing-library/jest-dom/extend-expect';
import {act, fireEvent, render, screen} from '@testing-library/react';
import React from 'react';

import {StoreAPIContextProvider} from '../../../../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/StoreContext';
import {ReorderSetsModal} from '../../../../../../../src/main/resources/META-INF/resources/page_editor/plugins/fragments_and_widgets/components/reorder_sets_modal/ReorderSetsModal';

function dispatchKey(element, eventType, key) {
	act(() => {
		element.dispatchEvent(
			new KeyboardEvent(eventType, {
				bubbles: true,
				key,
			})
		);
	});
}

const renderComponent = (dispatch = () => {}) => {
	return render(
		<DndProvider backend={HTML5Backend}>
			<StoreAPIContextProvider
				dispatch={dispatch}
				getState={() => ({
					fragmentEntryLinks: [],
					fragments: [
						{
							fragmentCollectionId: 'highlighted',
							fragmentEntries: [
								{
									fragmentEntryKey: 'fragment-1',
									groupId: '0',
									icon: 'fragment-1-icon',
									imagePreviewURL: '/fragment-1-image.png',
									label: 'Fragment 1',
									name: 'Fragment 1',
									type: 1,
								},
							],
							name: 'Favorites',
						},
						{
							fragmentCollectionId: 'collection-1',
							fragmentEntries: [
								{
									fragmentEntryKey: 'fragment-1',
									groupId: '0',
									icon: 'fragment-1-icon',
									imagePreviewURL: '/fragment-1-image.png',
									label: 'Fragment 1',
									name: 'Fragment 1',
									type: 1,
								},
								{
									fragmentEntryKey: 'fragment-2',
									groupId: '0',
									icon: 'fragment-2-icon',
									imagePreviewURL: '/fragment-2-image.png',
									label: 'Fragment 2',
									name: 'Fragment 2',
									type: 1,
								},
							],
							name: 'Collection 1',
						},
						{
							fragmentCollectionId: 'collection-2',
							fragmentEntries: [
								{
									fragmentEntryKey: 'fragment-3',
									groupId: '0',
									icon: 'fragment-3-icon',
									imagePreviewURL: '/fragment-3-image.png',
									label: 'Fragment 3',
									name: 'Fragment 3',
									type: 1,
								},
								{
									fragmentEntryKey: 'fragment-4',
									groupId: '0',
									icon: 'fragment-4-icon',
									imagePreviewURL: '/fragment-4-image.png',
									label: 'Fragment 4',
									name: 'Fragment 4',
									type: 1,
								},
							],
							name: 'Collection 2',
						},
					],
					restrictedItemIds: new Set(),
					widgets: [
						{
							path: 'root--category-highlighted',
							portlets: [
								{
									portletId: 'portlet-1',
									title: 'Portlet 1',
								},
								{
									portletId: 'portlet-4',
									title: 'Portlet 4',
								},
							],
							title: 'Highlighted',
						},
						{
							path: 'category-1',
							portlets: [
								{
									portletId: 'portlet-1',
									title: 'Portlet 1',
								},
								{
									portletId: 'portlet-2',
									title: 'Portlet 2',
								},
							],
							title: 'Category 1',
						},
						{
							path: 'category-2',
							portlets: [
								{
									portletId: 'portlet-3',
									title: 'Portlet 3',
								},
								{
									portletId: 'portlet-4',
									title: 'Portlet 4',
								},
							],
							title: 'Category 2',
						},
					],
				})}
			>
				<ReorderSetsModal onCloseModal={() => {}} />
			</StoreAPIContextProvider>
		</DndProvider>
	);
};

describe('ReorderSetsModal', () => {
	beforeAll(() => {
		jest.useFakeTimers();
	});

	it('renders ReorderSetsModal component', () => {
		renderComponent();

		act(() => {
			jest.runAllTimers();
		});

		expect(screen.getByLabelText('reorder-sets')).toBeInTheDocument();
	});

	it('renders standard fragment sets', () => {
		renderComponent();

		act(() => {
			jest.runAllTimers();
		});

		expect(screen.getByText('Collection 1')).toBeInTheDocument();
		expect(screen.getByText('Collection 2')).toBeInTheDocument();
	});

	it('renders standard widget categories', () => {
		renderComponent();

		act(() => {
			jest.runAllTimers();
		});

		expect(screen.getByText('Category 1')).toBeInTheDocument();
		expect(screen.getByText('Category 2')).toBeInTheDocument();
	});

	it('does not list favorites fragment set', () => {
		renderComponent();

		act(() => {
			jest.runAllTimers();
		});

		expect(screen.queryByText('Favorites')).not.toBeInTheDocument();
	});

	it('does not list highlighted widget cagegory', () => {
		renderComponent();

		act(() => {
			jest.runAllTimers();
		});

		expect(screen.queryByText('Highlighted')).not.toBeInTheDocument();
	});

	it('does not send data to server if order has not been changed', () => {
		const mockDispatch = jest.fn((a) => {
			if (typeof a === 'function') {
				return a(mockDispatch);
			}
		});

		renderComponent(mockDispatch);

		act(() => {
			jest.runAllTimers();
		});

		fireEvent.click(screen.getByText('save'));

		expect(mockDispatch).not.toBeCalled();
	});

	it('allows focusing drag and drop icons', () => {
		const {getByRole} = renderComponent();

		act(() => jest.runAllTimers());

		const reorderButton = getByRole('button', {
			name: /reorder category 1/i,
		});

		reorderButton.focus();
		expect(document.activeElement).toBe(reorderButton);
	});

	it('moves the focus using arrow keys', async () => {
		const {getByRole} = renderComponent();

		act(() => jest.runAllTimers());

		const reorderButton = getByRole('button', {
			name: /reorder category 1/i,
		});

		reorderButton.focus();
		dispatchKey(reorderButton, 'keydown', 'ArrowDown');

		expect(document.activeElement).toBe(
			getByRole('button', {
				name: /reorder category 2/i,
			})
		);
	});

	it('allows moving items using keyboard', () => {
		const {getByRole, getByText} = renderComponent();

		act(() => jest.runAllTimers());

		const reorderButton = getByRole('button', {
			name: /reorder category 1/i,
		});

		reorderButton.focus();

		dispatchKey(reorderButton, 'keyup', 'Enter');
		expect(getByText(/use-up-and-down-arrows/i)).toBeInTheDocument();
		dispatchKey(reorderButton, 'keyup', 'ArrowDown');

		expect(
			screen.queryByText('targeting-top-of-Category 2')
		).toBeInTheDocument();

		dispatchKey(reorderButton, 'keyup', 'Enter');
		expect(
			getByText('Category 1-placed-on-top-of-Category 2')
		).toBeInTheDocument();
	});
});
