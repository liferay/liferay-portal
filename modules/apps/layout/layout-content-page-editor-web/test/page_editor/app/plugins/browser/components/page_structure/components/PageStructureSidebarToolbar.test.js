/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import {LAYOUT_DATA_ITEM_TYPES} from '../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/config/constants/layoutDataItemTypes';
import {VIEWPORT_SIZES} from '../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/config/constants/viewportSizes';
import {
	ClipboardContextProvider,
	useSetClipboard,
} from '../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/ClipboardContext';
import {useSetMovementSources} from '../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/KeyboardMovementContext';
import deleteItem from '../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/thunks/deleteItem';
import duplicateItem from '../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/thunks/duplicateItem';
import updateItemStyle from '../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/utils/updateItemStyle';
import PageStructureSidebarToolbar from '../../../../../../../../src/main/resources/META-INF/resources/page_editor/plugins/browser/components/page_structure/components/PageStructureSidebarToolbar';
import StoreMother from '../../../../../../../../src/main/resources/META-INF/resources/page_editor/test_utils/StoreMother';

jest.mock(
	'../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/ClipboardContext',
	() => {
		const setClipboard = jest.fn();

		return {
			...jest.requireActual(
				'../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/ClipboardContext'
			),
			useSetClipboard: () => setClipboard,
		};
	}
);

jest.mock(
	'../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/utils/updateItemStyle',
	() => jest.fn()
);

jest.mock(
	'../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/thunks/deleteItem',
	() => jest.fn()
);

jest.mock(
	'../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/thunks/duplicateItem',
	() => jest.fn()
);

jest.mock(
	'../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/KeyboardMovementContext',
	() => {
		const setMovementSources = jest.fn();

		return {
			useSetMovementSources: () => setMovementSources,
		};
	}
);

const renderComponent = ({
	activeItemIds = [],
	viewportSize = VIEWPORT_SIZES.desktop,
} = {}) =>
	render(
		<StoreMother.Component
			getState={() => ({
				fragmentEntryLinks: {
					fragment01: {editableValues: {}},
					fragment02: {editableValues: {}},
					fragment03: {editableValues: {}},
				},
				layoutData: {
					items: {
						fragment01: {
							children: [],
							config: {
								fragmentEntryLinkId: 'fragment01',
								styles: {display: 'block'},
							},
							itemId: 'fragment01',
							parentId: 'root',
							type: LAYOUT_DATA_ITEM_TYPES.fragment,
						},
						fragment02: {
							children: [],
							config: {
								fragmentEntryLinkId: 'fragment02',
								styles: {display: 'none'},
							},
							itemId: 'fragment02',
							parentId: 'root',
							type: LAYOUT_DATA_ITEM_TYPES.fragment,
						},
						fragment03: {
							children: [],
							config: {
								fragmentEntryLinkId: 'fragment03',
								styles: {display: 'block'},
							},
							itemId: 'fragment03',
							parentId: 'root',
							type: LAYOUT_DATA_ITEM_TYPES.fragment,
						},
						root: {
							children: ['fragment01', 'fragment02'],
							itemId: 'root',
							parentId: null,
							type: LAYOUT_DATA_ITEM_TYPES.root,
						},
					},
				},
				selectedViewportSize: viewportSize,
			})}
		>
			<ClipboardContextProvider>
				<PageStructureSidebarToolbar activeItemIds={activeItemIds} />
			</ClipboardContextProvider>
		</StoreMother.Component>
	);

describe('PageStructureSidebarToolbar', () => {
	beforeEach(() => {
		jest.clearAllMocks();
	});

	it('shows the number of selected items', () => {
		renderComponent({
			activeItemIds: ['fragment01', 'fragment02'],
		});

		expect(screen.getByText('2-items-selected')).toBeInTheDocument();
	});

	it('calls deleteItem when Delete action is pressed', async () => {
		renderComponent({
			activeItemIds: ['fragment01', 'fragment02'],
		});

		await userEvent.click(screen.getByText('delete'));

		expect(deleteItem).toBeCalledWith(
			expect.objectContaining({
				itemIds: ['fragment01', 'fragment02'],
			})
		);
	});

	it('calls duplicateItem when Duplicate action is pressed', async () => {
		renderComponent({
			activeItemIds: ['fragment01', 'fragment03'],
		});

		await userEvent.click(screen.getByText('duplicate'));

		expect(duplicateItem).toBeCalledWith(
			expect.objectContaining({
				itemIds: ['fragment01', 'fragment03'],
			})
		);
	});

	it('calls updateItemStyle when Hide Fragments action is pressed', async () => {
		renderComponent({
			activeItemIds: ['fragment03', 'fragment02'],
		});

		await userEvent.click(screen.getByText('hide-fragments'));

		expect(updateItemStyle).toBeCalledWith(
			expect.objectContaining({
				itemIds: ['fragment03', 'fragment02'],
				styleName: 'display',
				styleValue: 'none',
			})
		);
	});

	it('calls useSetMovementSources when Move x Items action is pressed', async () => {
		renderComponent({
			activeItemIds: ['fragment01', 'fragment02'],
		});

		await userEvent.click(screen.getByText('move-2-items'));

		expect(useSetMovementSources()).toBeCalledWith([
			{isWidget: false, itemId: 'fragment01', type: 'fragment'},
			{isWidget: false, itemId: 'fragment02', type: 'fragment'},
		]);
	});

	it('does not show the button when it is a viewport other than desktop', () => {
		renderComponent({
			activeItemIds: ['fragment01', 'fragment02'],
			viewportSize: VIEWPORT_SIZES.tablet,
		});

		expect(
			screen.queryByLabelText('actions-for-selected-items')
		).not.toBeInTheDocument();
	});

	it('renders "Show Fragments" when the first element is hidden', () => {
		renderComponent({
			activeItemIds: ['fragment02', 'fragment01'],
		});

		expect(screen.getByText('show-fragments')).toBeInTheDocument();
	});

	it('calls deleteItem when Delete action is pressed', async () => {
		renderComponent({
			activeItemIds: ['fragment01', 'fragment02'],
		});

		await userEvent.click(screen.getByText('delete'));

		expect(deleteItem).toBeCalledWith(
			expect.objectContaining({
				itemIds: ['fragment01', 'fragment02'],
			})
		);
	});

	it('calls setClipboard and deleteItem when Cut action is pressed', async () => {
		const setClipboard = useSetClipboard();

		renderComponent({
			activeItemIds: ['fragment01', 'fragment02'],
		});

		await userEvent.click(screen.getByText('cut'));

		expect(deleteItem).toBeCalledWith(
			expect.objectContaining({
				itemIds: ['fragment01', 'fragment02'],
			})
		);

		expect(setClipboard).toBeCalledWith(
			expect.objectContaining(['fragment01', 'fragment02'])
		);
	});

	it('calls setClipboard when Copy action is pressed', async () => {
		const setClipboard = useSetClipboard();

		renderComponent({
			activeItemIds: ['fragment01', 'fragment02'],
		});

		await userEvent.click(screen.getByText('copy'));

		expect(setClipboard).toBeCalledWith(
			expect.objectContaining(['fragment01', 'fragment02'])
		);
	});

	it('do not allow the Paste action on multiple selections', () => {
		renderComponent({
			activeItemIds: ['fragment01', 'fragment02'],
		});

		expect(screen.queryByText('paste')).not.toBeInTheDocument();
	});
});
