/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {fireEvent, render} from '@testing-library/react';
import React from 'react';

import {LAYOUT_DATA_ITEM_TYPES} from '../../../../../../src/main/resources/META-INF/resources/page_editor/app/config/constants/layoutDataItemTypes';
import {
	useHoverItem,
	useSelectItem,
} from '../../../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/ControlsContext';
import {StoreAPIContextProvider} from '../../../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/StoreContext';
import FragmentEntryLinksWithComments from '../../../../../../src/main/resources/META-INF/resources/page_editor/plugins/comments/components/FragmentEntryLinksWithComments';

jest.mock(
	'../../../../../../src/main/resources/META-INF/resources/page_editor/plugins/comments/components/NoCommentsMessage',
	() => () => <h1>no-comments-message</h1>
);

jest.mock(
	'../../../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/ControlsContext',
	() => {
		const hoverItem = jest.fn();
		const selectItem = jest.fn();

		return {
			useHoverItem: () => hoverItem,
			useSelectItem: () => selectItem,
		};
	}
);

const NO_COMMENTS_STATE = {
	layoutData: {deletedItems: [], items: {}},
};

const COMMENTS_STATE = {
	fragmentEntryLinks: {
		'sandro-fragment': {
			comments: [{resolved: false}],
			fragmentEntryLinkId: 'sandro-fragment',
			name: 'Sandro Fragment',
		},

		'veronica-fragment': {
			comments: [{resolved: false}],
			fragmentEntryLinkId: 'veronica-fragment',
			name: 'Verónica Fragment',
		},

		'victor-fragment': {
			comments: [{resolved: true}],
			fragmentEntryLinkId: 'victor-fragment',
			name: 'Víctor Fragment',
		},
	},

	layoutData: {
		deletedItems: [],
		items: {
			'sandro-item': {
				config: {fragmentEntryLinkId: 'sandro-fragment'},
				itemId: 'sandro-item',
				type: LAYOUT_DATA_ITEM_TYPES.fragment,
			},

			'veronica-item': {
				config: {fragmentEntryLinkId: 'veronica-fragment'},
				itemId: 'veronica-item',
				type: LAYOUT_DATA_ITEM_TYPES.fragment,
			},

			'victor-item': {
				config: {fragmentEntryLinkId: 'victor-fragment'},
				itemId: 'victor-item',
				type: LAYOUT_DATA_ITEM_TYPES.fragment,
			},
		},
	},
};

const renderComponent = (state, dispatch) =>
	render(
		<StoreAPIContextProvider dispatch={dispatch} getState={() => state}>
			<FragmentEntryLinksWithComments />
		</StoreAPIContextProvider>
	);

describe('FragmentEntryLinksWithComments', () => {
	it('shows a NoCommentsMessage if there are no comments', () => {
		const {getByText} = renderComponent(NO_COMMENTS_STATE);
		expect(getByText('no-comments-message')).toBeInTheDocument();
	});

	it('shows a list of comments', () => {
		const {getByText, queryByText} = renderComponent(COMMENTS_STATE);

		expect(getByText('Sandro Fragment')).toBeInTheDocument();
		expect(getByText('Verónica Fragment')).toBeInTheDocument();
		expect(queryByText('Víctor Fragment')).not.toBeInTheDocument();
	});

	it('includes resolved comments if showResolvedComments is true', () => {
		const {getByText} = renderComponent({
			...COMMENTS_STATE,
			showResolvedComments: true,
		});

		expect(getByText('Sandro Fragment')).toBeInTheDocument();
		expect(getByText('Verónica Fragment')).toBeInTheDocument();
		expect(getByText('Víctor Fragment')).toBeInTheDocument();
	});

	it('sets a fragment to hovered on focus', async () => {
		const hoverItem = useHoverItem();

		const {getAllByRole} = renderComponent({
			...COMMENTS_STATE,
			showResolvedComments: true,
		});

		const [sandroFragment] = getAllByRole('button', {
			name: 'show-comments',
		});

		fireEvent.focus(sandroFragment);

		expect(sandroFragment).toHaveTextContent('Sandro Fragment');
		expect(hoverItem).toHaveBeenCalledWith('sandro-item');
	});

	it('sets a fragment to hovered on mouseover', async () => {
		const hoverItem = useHoverItem();

		const {getAllByRole} = renderComponent({
			...COMMENTS_STATE,
			showResolvedComments: true,
		});

		const [sandroFragment] = getAllByRole('button', {
			name: 'show-comments',
		});

		fireEvent.mouseOver(sandroFragment);

		expect(sandroFragment).toHaveTextContent('Sandro Fragment');
		expect(hoverItem).toHaveBeenCalledWith('sandro-item');
	});

	it('sets a fragment to not hovered on mouseout', async () => {
		const hoverItem = useHoverItem();

		const {getAllByRole} = renderComponent({
			...COMMENTS_STATE,
			showResolvedComments: true,
		});

		const [sandroFragment] = getAllByRole('button', {
			name: 'show-comments',
		});

		fireEvent.mouseOut(sandroFragment);

		expect(sandroFragment).toHaveTextContent('Sandro Fragment');
		expect(hoverItem).toHaveBeenCalledWith(null);
	});

	it('sets a fragment to selected on click', async () => {
		const selectItem = useSelectItem();

		const {getAllByRole} = renderComponent({
			...COMMENTS_STATE,
			showResolvedComments: true,
		});

		const [sandroFragment] = getAllByRole('button', {
			name: 'show-comments',
		});

		fireEvent.click(sandroFragment);

		expect(sandroFragment).toHaveTextContent('Sandro Fragment');
		expect(selectItem).toHaveBeenCalledWith('sandro-item');
	});
});
