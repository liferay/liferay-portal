/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {getMovementText} from '../../../src/main/resources/META-INF/resources/js/site_navigation_menu_editor/components/KeyboardMovementText';
import getFlatItems from '../../../src/main/resources/META-INF/resources/js/site_navigation_menu_editor/utils/getFlatItems';

jest.mock('frontend-js-web', () => ({
	...jest.requireActual('frontend-js-web'),
	sub: jest.fn((langKey, arg) => langKey.replace('x', arg)),
}));

const ITEMS = getFlatItems([
	{
		children: [
			{
				children: [],
				parentSiteNavigationMenuItemId: 'parent',
				siteNavigationMenuItemId: 'child1',
				title: 'Child 1',
			},
			{
				children: [],
				parentSiteNavigationMenuItemId: 'parent',
				siteNavigationMenuItemId: 'child2',
				title: 'Child 2',
			},
			{
				children: [],
				parentSiteNavigationMenuItemId: 'parent',
				siteNavigationMenuItemId: 'child3',
				title: 'Child 3',
			},
			{
				children: [],
				parentSiteNavigationMenuItemId: 'parent',
				siteNavigationMenuItemId: 'child4',
				title: 'Child 4',
			},
		],
		parentSiteNavigationMenuItemId: '0',
		siteNavigationMenuItemId: 'parent',
		title: 'Parent',
	},
]);

describe('KeyboardMovementText', () => {
	it('explains how to use the keys when activating it', () => {
		const text = getMovementText(
			{
				eventKey: 'Enter',
				menuItemTitle: 'pagina',
				menuItemType: 'layout',
			},
			ITEMS
		);

		expect(text).toBe(
			'use-up-and-down-arrows-to-move-pagina (layout)-and-press-enter-to-place-it-in-desired-position'
		);
	});

	it('explains that is it going to be placed as a child of another item', () => {
		const text = getMovementText(
			{
				menuItemTitle: 'pagina',
				menuItemType: 'layout',
				parentSiteNavigationMenuItemId: 'child1',
			},
			ITEMS
		);

		expect(text).toBe('targeting-inside-of-Child 1');
	});

	it('explains that is it going to be placed on top of another item', () => {
		const text = getMovementText(
			{
				menuItemTitle: 'pagina',
				menuItemType: 'layout',
				order: 0,
				parentSiteNavigationMenuItemId: 'parent',
			},
			ITEMS
		);

		expect(text).toBe('targeting-top,Child 1-of-x');
	});

	it('explains that is it going to be placed on the bottom of another item', () => {
		const text = getMovementText(
			{
				menuItemTitle: 'pagina',
				menuItemType: 'layout',
				order: 1,
				parentSiteNavigationMenuItemId: 'parent',
			},
			ITEMS
		);

		expect(text).toBe('targeting-bottom,Child 1-of-x');
	});
});
