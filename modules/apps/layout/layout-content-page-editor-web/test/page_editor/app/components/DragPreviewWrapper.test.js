/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	getIcon,
	getLabel,
} from '../../../../src/main/resources/META-INF/resources/page_editor/app/components/DragPreviewWrapper';

const fragments = [
	{
		fragmentEntries: [
			{
				fragmentEntryKey: 'fragment01',
				icon: 'icon01',
				name: 'name01',
				type: 'type01',
			},
			{
				fragmentEntryKey: 'fragment02',
				icon: 'icon02',
				name: 'name02',
			},
		],
	},
];

const getWidgets = () => [
	{
		portlets: [
			{
				instanceable: false,
				portletId: 'portlet01',
			},
			{
				instanceable: true,
				portletId: 'portlet02',
			},
		],
	},
];

const fragmentEntryLinks = {
	fragmentEntryLink01: {
		fragmentEntryKey: 'fragment02',
	},
	fragmentEntryLink02: {
		portletId: 'portlet01',
	},
	fragmentEntryLink03: {
		portletId: 'portlet02',
	},
};

const item = {
	config: {fragmentEntryLinkId: 'fragmentEntryLink01'},
	name: 'My Item',
	type: 'fragment',
};

describe('DragPreviewWrapper', () => {
	describe('getIcon', () => {
		it('returns the corresponding item icon when the item has fragment type', () => {
			expect(
				getIcon({
					activeItemIds: ['item01'],
					fragmentEntryLinks,
					fragments,
					getWidgets,
					item,
				})
			).toBe('icon02');
		});

		it('returns the corresponding item icon when the item has no fragment type', () => {
			expect(
				getIcon({
					activeItemIds: ['item01'],
					fragmentEntryLinks,
					fragments,
					getWidgets,
					item: {...item, type: 'type01'},
				})
			).toBe('icon01');
		});

		it('returns the square-hole icon when the item is a non instanceable widget', () => {
			expect(
				getIcon({
					activeItemIds: ['item01'],
					fragmentEntryLinks,
					fragments,
					getWidgets,
					item: {
						...item,
						config: {fragmentEntryLinkId: 'fragmentEntryLink02'},
					},
				})
			).toBe('square-hole');
		});

		it('returns the square-hole-multi icon when the item is a instanceable widget', () => {
			expect(
				getIcon({
					activeItemIds: ['item01'],
					fragmentEntryLinks,
					fragments,
					getWidgets,
					item: {
						...item,
						config: {fragmentEntryLinkId: 'fragmentEntryLink03'},
					},
				})
			).toBe('square-hole-multi');
		});

		it('returns null when multiple items are selected', () => {
			expect(
				getIcon({
					activeItemIds: ['item01', 'item02'],
					fragmentEntryLinks,
					fragments,
					getWidgets,
					item,
				})
			).toBe(null);
		});
	});

	describe('getLabel', () => {
		it('returns the corresponding label', () => {
			expect(
				getLabel({
					activeItemIds: ['item01'],
					item,
				})
			).toBe('My Item');
		});

		it('returns x items when multiple items are selected', () => {
			expect(
				getLabel({
					activeItemIds: ['item01', 'item02'],
					item,
				})
			).toBe('2-items');
		});
	});
});
