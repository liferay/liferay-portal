/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import TopperItemActions from '../../../../../src/main/resources/META-INF/resources/page_editor/app/components/topper/TopperItemActions';
import {LAYOUT_DATA_ITEM_TYPES} from '../../../../../src/main/resources/META-INF/resources/page_editor/app/config/constants/layoutDataItemTypes';
import {
	ClipboardContextProvider,
	useSetClipboard,
} from '../../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/ClipboardContext';
import {StoreAPIContextProvider} from '../../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/StoreContext';
import deleteItem from '../../../../../src/main/resources/META-INF/resources/page_editor/app/thunks/deleteItem';
import pasteItems from '../../../../../src/main/resources/META-INF/resources/page_editor/app/thunks/pasteItems';

jest.mock(
	'../../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/ClipboardContext',
	() => {
		const setClipboard = jest.fn();

		return {
			...jest.requireActual(
				'../../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/ClipboardContext'
			),
			useClipboard: () => ['itemId2'],
			useSetClipboard: () => setClipboard,
		};
	}
);

jest.mock(
	'../../../../../src/main/resources/META-INF/resources/page_editor/app/thunks/deleteItem',
	() => jest.fn()
);

jest.mock(
	'../../../../../src/main/resources/META-INF/resources/page_editor/app/thunks/pasteItems',
	() => jest.fn()
);

const LAYOUT_DATA = {
	items: {
		itemId1: {
			children: [],
			config: {styles: {}},
			itemId: 'itemId1',
			parentId: null,
			type: LAYOUT_DATA_ITEM_TYPES.row,
		},
		itemId2: {
			children: [],
			config: {styles: {}},
			itemId: 'itemId2',
			parentId: null,
			type: LAYOUT_DATA_ITEM_TYPES.row,
		},
		itemId3: {
			children: [],
			config: {styles: {}},
			itemId: 'itemId3',
			parentId: null,
			type: LAYOUT_DATA_ITEM_TYPES.container,
		},
	},
};

const renderTopperItemActions = ({
	canManageFragments = true,
	isDisabled = false,
	itemId = 'itemId1',
	layoutData = LAYOUT_DATA,
} = {}) => {
	const item = layoutData.items[itemId];

	return render(
		<StoreAPIContextProvider
			getState={() => ({
				fragmentEntryLinks: {},
				layoutData,
				permissions: {
					MANAGE_FRAGMENT_ENTRIES: canManageFragments,
				},
			})}
		>
			<ClipboardContextProvider>
				<TopperItemActions disabled={isDisabled} item={item} />
			</ClipboardContextProvider>
		</StoreAPIContextProvider>
	);
};

describe('TopperItemActions', () => {
	it('does not open TopperItemActions if disabled', async () => {
		const {baseElement} = renderTopperItemActions({isDisabled: true});

		expect(baseElement.querySelector('.dropdown')).toBeInTheDocument();

		expect(baseElement.querySelector('.dropdown-toggle')).toHaveAttribute(
			'disabled'
		);
	});

	it('opens TopperItemActions if not disabled', async () => {
		const {baseElement} = renderTopperItemActions();
		await userEvent.click(baseElement.querySelector('.dropdown-toggle'));

		expect(
			baseElement.querySelector('.dropdown-menu.show')
		).toBeInTheDocument();
	});

	it('calls setClipboard and deleteItem when Cut action is pressed', async () => {
		const setClipboard = useSetClipboard();

		renderTopperItemActions();

		await userEvent.click(screen.getByText('cut'));

		expect(deleteItem).toBeCalledWith(
			expect.objectContaining({
				itemIds: ['itemId1'],
			})
		);

		expect(setClipboard).toBeCalledWith(
			expect.objectContaining(['itemId1'])
		);
	});

	it('calls setClipboard when Copy action is pressed', async () => {
		const setClipboard = useSetClipboard();

		renderTopperItemActions();

		await userEvent.click(screen.getByText('copy'));

		expect(setClipboard).toBeCalledWith(
			expect.objectContaining(['itemId1'])
		);
	});

	it('calls pasteItem when Paste action is pressed', async () => {
		renderTopperItemActions({itemId: 'itemId3'});

		await userEvent.click(screen.getByText('paste'));

		expect(pasteItems).toBeCalledWith(
			expect.objectContaining({
				clipboard: ['itemId2'],
				parentItemId: 'itemId3',
			})
		);
	});

	it('does not render save composition action if user does not have the correct permission', async () => {
		renderTopperItemActions({canManageFragments: false, itemId: 'itemId3'});

		expect(screen.queryByText('save-composition')).not.toBeInTheDocument();
	});
});
