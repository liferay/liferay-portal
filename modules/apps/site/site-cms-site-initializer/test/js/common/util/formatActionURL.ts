/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import formatActionURL from '../../../../src/main/resources/META-INF/resources/js/common/utils/formatActionURL';

const specialChars = 'http://foo.bar?param=%áàäâ^/#{2}/ç';

const item = {
	id: 1235,
	name: '#test_item_name',
	urls: {
		simple: 'https://www.liferay.com',
		specialChars,
		specialCharsEncoded: encodeURI(specialChars),
	},
};

const encodedItem = {
	id: encodeURIComponent(item.id),
	name: encodeURIComponent(item.name),
	urls: {
		simple: encodeURIComponent(item.urls.simple),
		specialChars: encodeURIComponent(item.urls.specialChars),
		specialCharsEncoded: encodeURIComponent(item.urls.specialCharsEncoded),
	},
};

const assertActionURL = (url: string, expected = url) => {
	expect(formatActionURL(item, url)).toEqual(expected);
};

describe('formatActionURL helper. No interpolation', () => {
	it('No URL is provided: returns an empty string', () => {
		assertActionURL('', '');
	});

	it('URL is returned as provided', () => {
		assertActionURL(item.urls.simple);
	});

	it('No item is provided: returns URL as provided', () => {
		expect(formatActionURL(null, item.urls.simple)).toEqual(
			item.urls.simple
		);
	});
});

describe('formatActionURL helper. Returns the URL with encoded interpolated values', () => {
	it('path interpolation', () => {
		assertActionURL('/o/{id}', `/o/${encodedItem.id}`);

		assertActionURL(
			'/o/{name}?p=á#ha$h',
			`/o/${encodedItem.name}?p=á#ha$h`
		);
	});

	it('query interpolation', () => {
		assertActionURL('/o?p={id}', `/o?p=${encodedItem.id}`);

		assertActionURL(
			'/o?p={urls.specialChars}#ha$h',
			`/o?p=${encodedItem.urls.specialChars}#ha$h`
		);

		assertActionURL(
			'/o?p={urls.specialCharsEncoded}#ha$h',
			`/o?p=${encodedItem.urls.specialCharsEncoded}#ha$h`
		);
	});

	it('hash interpolation', () => {
		assertActionURL('/o#{id}', `/o#${encodedItem.id}`);

		assertActionURL('/o#{name}', `/o#${encodedItem.name}`);

		assertActionURL(
			'/o#{urls.specialChars}',
			`/o#${encodedItem.urls.specialChars}`
		);
	});

	it('multiple interpolation', () => {
		assertActionURL(
			'/{id}/{urls.specialCharsEncoded}?p={urls.specialChars}#{name}',
			`/${encodedItem.id}/${encodedItem.urls.specialCharsEncoded}?p=${encodedItem.urls.specialChars}#${encodedItem.name}`
		);
	});
});
