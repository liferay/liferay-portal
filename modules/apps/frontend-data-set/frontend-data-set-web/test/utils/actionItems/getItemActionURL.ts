/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import getItemActionURL from '../../../src/main/resources/META-INF/resources/utils/actionItems/getItemActionURL';
import {IItemsActions} from '../../../src/main/resources/META-INF/resources/utils/types';

const itemsActions: IItemsActions[] = [
	{
		data: {id: 'edit'},
		href: '/web/page/edit/{id}?redirect=http://localhost/web/page',
	},
	{
		data: {id: 'view'},
		href: '/web/page/view/{id}',
	},
	{
		data: {id: 'noHref'},
	},
];

const item = {id: 42};

describe('getItemActionURL', () => {
	afterEach(() => {
		window.history.replaceState({}, '', '/');
	});

	it('returns an empty string when no action matches the action ID', () => {
		expect(getItemActionURL(itemsActions, 'delete', item)).toBe('');
	});

	it('returns an empty string when the action has no href', () => {
		expect(getItemActionURL(itemsActions, 'noHref', item)).toBe('');
	});

	it('interpolates the item properties into the href', () => {
		expect(getItemActionURL(itemsActions, 'view', item)).toBe(
			'/web/page/view/42'
		);
	});

	it('rewrites the redirect parameter to the current location', () => {
		window.history.replaceState(
			{},
			'',
			'/web/page?foo_fdsConfig=(view:cards)'
		);

		const url = getItemActionURL(itemsActions, 'edit', item);

		const parsedURL = new URL(url, window.location.origin);

		expect(parsedURL.pathname).toBe('/web/page/edit/42');
		expect(parsedURL.searchParams.get('redirect')).toBe(
			window.location.href
		);
	});
});
