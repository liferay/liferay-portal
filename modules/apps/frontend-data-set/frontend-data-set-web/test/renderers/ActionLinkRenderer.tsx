/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom';
import userEvent from '@testing-library/user-event';

import FrontendDataSetContext from '../../src/main/resources/META-INF/resources/FrontendDataSetContext';
import ActionLinkRenderer from '../../src/main/resources/META-INF/resources/renderers/ActionLinkRenderer';
import recentlyVisited from '../../src/main/resources/META-INF/resources/utils/recentlyVisited';
import {IItemsActions} from '../../src/main/resources/META-INF/resources/utils/types';
import ViewsContext from '../../src/main/resources/META-INF/resources/views/ViewsContext';

jest.mock('frontend-js-components-web', () => ({
	...(jest.requireActual('frontend-js-components-web') as object),
	openConfirmModal: jest.fn(({onConfirm}) => onConfirm(true)),
}));

jest.mock('frontend-js-web', () => ({
	...(jest.requireActual('frontend-js-web') as object),
	navigate: jest.fn(),
}));

const FDS_NAME = 'FDS_NAME';

const ITEM_DATA = {id: 1};

const VIEW_ACTION: IItemsActions = {
	href: '/blogs/{id}',
	label: 'View',
	target: 'link',
};

describe('ActionLinkRenderer', () => {
	afterEach(() => {
		recentlyVisited.clear(FDS_NAME);
	});

	function renderActionLinkRenderer({
		action = VIEW_ACTION,
		searchSuggestionsEnabled = true,
		value = 'Blogs' as number | string | null,
	} = {}) {
		render(
			<FrontendDataSetContext.Provider
				value={
					{
						highlightItems: jest.fn(),
						id: FDS_NAME,
						openSidePanel: jest.fn(),
						searchSuggestionsEnabled,
					} as any
				}
			>
				<ActionLinkRenderer
					actions={[action]}
					itemData={ITEM_DATA}
					itemId={ITEM_DATA.id}
					value={value}
				/>
			</FrontendDataSetContext.Provider>
		);

		return screen.getByRole('link');
	}

	it('remembers the item the user navigates to', async () => {
		await userEvent.click(renderActionLinkRenderer());

		expect(recentlyVisited.get(FDS_NAME)).toEqual([
			{href: '/blogs/1', label: 'Blogs'},
		]);
	});

	it('remembers the item under its own title rather than under the cell text', async () => {
		render(
			<FrontendDataSetContext.Provider
				value={{id: FDS_NAME, searchSuggestionsEnabled: true} as any}
			>
				<ActionLinkRenderer
					actions={[VIEW_ACTION]}
					itemData={{...ITEM_DATA, title: {en_US: 'Blogs Entry'}}}
					itemId={ITEM_DATA.id}
					value={ITEM_DATA.id}
				/>
			</FrontendDataSetContext.Provider>
		);

		await userEvent.click(screen.getByRole('link'));

		expect(recentlyVisited.get(FDS_NAME)).toEqual([
			{href: '/blogs/1', label: 'Blogs Entry'},
		]);
	});

	it('remembers the item under the field the view names its rows by', async () => {
		render(
			<ViewsContext.Provider
				value={
					[
						{
							activeView: {
								schema: {accessibleNameField: 'reference'},
							},
						},
					] as any
				}
			>
				<FrontendDataSetContext.Provider
					value={
						{id: FDS_NAME, searchSuggestionsEnabled: true} as any
					}
				>
					<ActionLinkRenderer
						actions={[VIEW_ACTION]}
						itemData={{...ITEM_DATA, reference: 'BLOGS-1'}}
						itemId={ITEM_DATA.id}
						value="Blogs"
					/>
				</FrontendDataSetContext.Provider>
			</ViewsContext.Provider>
		);

		await userEvent.click(screen.getByRole('link'));

		expect(recentlyVisited.get(FDS_NAME)).toEqual([
			{href: '/blogs/1', label: 'BLOGS-1'},
		]);
	});

	it('remembers the item under the action label when the cell has no value', async () => {
		await userEvent.click(renderActionLinkRenderer({value: null}));

		expect(recentlyVisited.get(FDS_NAME)).toEqual([
			{href: '/blogs/1', label: 'View'},
		]);
	});

	it('remembers the item once the user confirms the action', async () => {
		await userEvent.click(
			renderActionLinkRenderer({
				action: {
					...VIEW_ACTION,
					data: {confirmationMessage: 'are-you-sure'},
				},
			})
		);

		expect(recentlyVisited.get(FDS_NAME)).toEqual([
			{href: '/blogs/1', label: 'Blogs'},
		]);
	});

	it('remembers the item behind a target the renderer leaves to the browser', async () => {
		await userEvent.click(
			renderActionLinkRenderer({
				action: {...VIEW_ACTION, target: 'blank'},
			})
		);

		expect(recentlyVisited.get(FDS_NAME)).toEqual([
			{href: '/blogs/1', label: 'Blogs'},
		]);
	});

	it('remembers nothing about an action that runs a callback', async () => {
		await userEvent.click(
			renderActionLinkRenderer({
				action: {...VIEW_ACTION, onClick: jest.fn()},
			})
		);

		expect(recentlyVisited.get(FDS_NAME)).toEqual([]);
	});

	it('remembers nothing about an action that opens a side panel', async () => {
		await userEvent.click(
			renderActionLinkRenderer({
				action: {...VIEW_ACTION, target: 'sidePanel'},
			})
		);

		expect(recentlyVisited.get(FDS_NAME)).toEqual([]);
	});

	it('remembers nothing when the Data Set keeps no search history', async () => {
		await userEvent.click(
			renderActionLinkRenderer({searchSuggestionsEnabled: false})
		);

		expect(recentlyVisited.get(FDS_NAME)).toEqual([]);
	});
});
