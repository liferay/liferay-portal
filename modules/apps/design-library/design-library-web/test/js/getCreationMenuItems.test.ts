/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import getCreationMenuItems from '../../src/main/resources/META-INF/resources/js/getCreationMenuItems';

const mockOpenCreationModal = jest.fn();

jest.mock(
	'../../src/main/resources/META-INF/resources/js/openCreationModal',
	() => ({
		__esModule: true,
		default: (item: unknown) => mockOpenCreationModal(item),
	})
);

const STYLE_BOOK_CREATION_ITEM = {
	id: 'add-style-book',
	label: 'new-style-book',
	module: 'http://localhost/style-book-web',
	moduleProps: {},
};

const STYLE_BOOK = {
	color: 'purple',
	creationItems: [STYLE_BOOK_CREATION_ITEM],
	defaultActionId: 'edit',
	entryClassName: 'com.liferay.style.book.model.StyleBookEntry',
	key: 'style-book',
	label: 'Style Book',
	symbol: 'book',
};

const STYLE_BOOK_WITHOUT_CREATION_ITEMS = {
	color: 'purple',
	defaultActionId: 'edit',
	entryClassName: 'com.liferay.style.book.model.StyleBookEntry',
	key: 'style-book',
	label: 'Style Book',
	symbol: 'book',
};

const FRAGMENT = {
	color: 'pink',
	creationItems: [
		{
			id: 'add-basic-fragment',
			label: 'new-basic-fragment',
			module: 'http://localhost/fragment-web',
			moduleProps: {},
		},
		{
			id: 'add-fragment-set',
			label: 'new-fragment-set',
			module: 'http://localhost/fragment-web',
			moduleProps: {},
		},
	],
	defaultActionId: 'view',
	entryClassName: 'com.liferay.fragment.model.FragmentCollection',
	key: 'fragment',
	label: 'Fragment Set',
	symbol: 'cards2',
};

describe('getCreationMenuItems', () => {
	beforeEach(() => {
		jest.clearAllMocks();
	});

	it('returns no items without resource types', () => {
		expect(getCreationMenuItems([])).toEqual([]);
	});

	it('returns no items when no type contributes creation items', () => {
		expect(
			getCreationMenuItems([STYLE_BOOK_WITHOUT_CREATION_ITEMS])
		).toEqual([]);
	});

	it('flattens the items of every type in contributor order', () => {
		expect(
			getCreationMenuItems([STYLE_BOOK, FRAGMENT]).map(
				(item) => item.label
			)
		).toEqual(['new-style-book', 'new-basic-fragment', 'new-fragment-set']);
	});

	it('skips a type without creation items but keeps the others', () => {
		expect(
			getCreationMenuItems([
				STYLE_BOOK_WITHOUT_CREATION_ITEMS,
				FRAGMENT,
			]).map((item) => item.label)
		).toEqual(['new-basic-fragment', 'new-fragment-set']);
	});

	it('opens the creation modal for the clicked item', () => {
		const [styleBookItem] = getCreationMenuItems([STYLE_BOOK]);

		styleBookItem.onClick();

		expect(mockOpenCreationModal).toHaveBeenCalledWith(
			STYLE_BOOK_CREATION_ITEM
		);
	});
});
