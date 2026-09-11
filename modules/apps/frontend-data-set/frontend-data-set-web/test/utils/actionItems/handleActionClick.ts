/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import handleActionClick from '../../../src/main/resources/META-INF/resources/utils/actionItems/handleActionClick';
import recentlyVisited from '../../../src/main/resources/META-INF/resources/utils/recentlyVisited';
import {IItemsActions} from '../../../src/main/resources/META-INF/resources/utils/types';

const FDS_NAME = 'FDS_NAME';

const ITEM_DATA = {id: 1, title: 'Nike Air Force One'};

const VIEW_ACTION: IItemsActions = {
	href: '/products/{id}',
	label: 'View Details',
	target: 'link',
};

describe('handleActionClick', () => {
	afterEach(() => {
		recentlyVisited.clear(FDS_NAME);
	});

	function clickAction({
		action = VIEW_ACTION,
		itemData = ITEM_DATA as any,
		searchSuggestionsEnabled = true,
	} = {}) {
		handleActionClick({
			action,
			event: {preventDefault: jest.fn()} as any,
			executeAsyncItemAction: jest.fn(),
			fdsName: FDS_NAME,
			highlightItems: jest.fn(),
			itemData,
			itemId: ITEM_DATA.id,
			items: [itemData],
			loadData: jest.fn(),
			onActionDropdownItemClick: jest.fn(),
			onInfoPanelToggleButtonClick: jest.fn(),
			openModal: jest.fn(),
			openSidePanel: jest.fn(),
			searchSuggestionsEnabled,
			toggleItemInlineEdit: jest.fn(),
		});
	}

	it('remembers the item behind an action the browser follows', () => {
		clickAction();

		expect(recentlyVisited.get(FDS_NAME)).toEqual([
			{href: '/products/1', label: ITEM_DATA.title},
		]);
	});

	it('remembers the item behind an action opening another window', () => {
		window.open = jest.fn();

		clickAction({action: {...VIEW_ACTION, target: 'blank'}});

		expect(recentlyVisited.get(FDS_NAME)).toEqual([
			{href: '/products/1', label: ITEM_DATA.title},
		]);
	});

	it('names the item after a title the server sent as a map of locales', () => {
		clickAction({
			itemData: {id: 1, title: {en_US: 'Nike Air Force One'}},
		});

		expect(recentlyVisited.get(FDS_NAME)).toEqual([
			{href: '/products/1', label: 'Nike Air Force One'},
		]);
	});

	it('names the item after the translation the server sent alongside it', () => {
		clickAction({
			itemData: {
				id: 1,
				title: 'Zapatilla',
				title_i18n: {en_US: 'Nike Air Force One'},
			},
		});

		expect(recentlyVisited.get(FDS_NAME)).toEqual([
			{href: '/products/1', label: 'Nike Air Force One'},
		]);
	});

	it('names the item after its own label rather than after the action', () => {
		clickAction({itemData: {id: 1, name: 'Nike Air Force One'}});

		expect(recentlyVisited.get(FDS_NAME)).toEqual([
			{href: '/products/1', label: 'Nike Air Force One'},
		]);
	});

	it('falls back to the action label for an item with neither title nor name', () => {
		clickAction({itemData: {id: 1}});

		expect(recentlyVisited.get(FDS_NAME)).toEqual([
			{href: '/products/1', label: VIEW_ACTION.label},
		]);
	});

	it('remembers nothing about an action that opens a side panel', () => {
		clickAction({action: {...VIEW_ACTION, target: 'sidePanel'}});

		expect(recentlyVisited.get(FDS_NAME)).toEqual([]);
	});

	it('remembers nothing about an action that opens the info panel', () => {
		clickAction({action: {...VIEW_ACTION, target: 'infoPanel'}});

		expect(recentlyVisited.get(FDS_NAME)).toEqual([]);
	});

	it('remembers nothing when the Data Set keeps no search history', () => {
		clickAction({searchSuggestionsEnabled: false});

		expect(recentlyVisited.get(FDS_NAME)).toEqual([]);
	});
});
