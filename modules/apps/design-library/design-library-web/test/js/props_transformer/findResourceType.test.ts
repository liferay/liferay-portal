/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import findResourceType from '../../../src/main/resources/META-INF/resources/js/props_transformer/findResourceType';

const LAYOUT_CLASS_NAME =
	'com.liferay.layout.page.template.model.LayoutPageTemplateEntry';

const MASTER = {
	color: 'purple',
	defaultActionId: 'edit',
	entryClassName: LAYOUT_CLASS_NAME,
	key: 'master',
	label: 'Master',
	symbol: 'page',
	type: '3',
};

const DISPLAY_PAGE = {
	...MASTER,
	key: 'display-page',
	label: 'Display Page',
	type: '1',
};

const STYLE_BOOK = {
	color: 'purple',
	defaultActionId: 'edit',
	entryClassName: 'com.liferay.style.book.model.StyleBookEntry',
	key: 'style-book',
	label: 'Style Book',
	symbol: 'book',
};

describe('findResourceType', () => {
	it('returns undefined without item data', () => {
		expect(findResourceType([STYLE_BOOK])).toBeUndefined();
	});

	it('returns undefined without an entry class name', () => {
		expect(findResourceType([STYLE_BOOK], {})).toBeUndefined();
	});

	it('matches on entry class name alone when no type is declared', () => {
		expect(
			findResourceType([STYLE_BOOK], {
				entryClassName: STYLE_BOOK.entryClassName,
			})?.key
		).toBe('style-book');
	});

	it('ignores a row type when the resource type declares none', () => {
		expect(
			findResourceType([STYLE_BOOK], {
				entryClassName: STYLE_BOOK.entryClassName,
				type: '7',
			})?.key
		).toBe('style-book');
	});

	it('distinguishes types sharing an entry class name', () => {
		expect(
			findResourceType([MASTER, DISPLAY_PAGE], {
				entryClassName: LAYOUT_CLASS_NAME,
				type: '1',
			})?.key
		).toBe('display-page');
	});

	it('compares the discriminator as a string', () => {
		expect(
			findResourceType([MASTER, DISPLAY_PAGE], {
				entryClassName: LAYOUT_CLASS_NAME,
				type: 3,
			})?.key
		).toBe('master');
	});

	it('returns undefined when no type claims the row', () => {
		expect(
			findResourceType([MASTER], {
				entryClassName: LAYOUT_CLASS_NAME,
				type: '9',
			})
		).toBeUndefined();
	});
});
