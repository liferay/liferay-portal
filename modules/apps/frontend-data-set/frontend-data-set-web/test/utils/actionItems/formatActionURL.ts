/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import formatActionURL from '../../../src/main/resources/META-INF/resources/utils/actionItems/formatActionURL';

const specialChars = 'http://foo.bar?param=%áàäâ^/#{2}/ç';

const item = {
	id: 1235,
	name: '#test_item_name',
	urls: {
		path: '/documents/34879/34881/%23small_pexels-photo-1000498.jpeg/2d193f5d-5b3f-1f7a-c221-8392333f56ba?version=1.0&t=1744616121055&download=true&objectDefinitionExternalReferenceCode=L_CMS_BASIC_DOCUMENT&objectEntryExternalReferenceCode=07dd3433-04b4-eec7-2d21-be307eb0a06c',
		simple: 'https://www.liferay.com',
		specialChars,
		specialCharsEncoded: encodeURI(specialChars),
		withBackURL: '/web/page?backURL=http://foo.bar',
		withPPId: '/web/page?p_p_id=foo',
		withRedirect: '/web/page?redirect=foo',
	},
};

const encodedItem = {
	id: encodeURIComponent(item.id),
	name: encodeURIComponent(item.name),
	urls: {
		path: encodeURIComponent(item.urls.path),
		simple: encodeURIComponent(item.urls.simple),
		specialChars: encodeURIComponent(item.urls.specialChars),
		specialCharsEncoded: encodeURIComponent(item.urls.specialCharsEncoded),
		withBackURL: encodeURIComponent(item.urls.withBackURL),
		withPPId: encodeURIComponent(item.urls.withPPId),
		withRedirect: encodeURIComponent(item.urls.withRedirect),
	},
};

const assertActionURL = (
	url: string | undefined,
	target: string,
	expected = url
) => {
	expect(formatActionURL(url, item, target)).toEqual(expected);
};

const assertActionLinkURL = (url: string | undefined, expected?: string) => {
	assertActionURL(url, 'link', expected);
};

const assertActionNonLinkURL = (url: string | undefined, expected?: string) => {
	assertActionURL(url, 'modal', expected);
};

describe('formatActionURL helper. No interpolation', () => {
	it('No URL is provided: returns an empty string', () => {
		assertActionLinkURL(undefined, '');
	});

	it('URL is returned as provided', () => {
		assertActionLinkURL(item.urls.simple);
	});

	it('Modifies preexisting redirect parameter to use the actual URL', () => {
		assertActionLinkURL(
			'/test?p_p_id=random&_random_redirect=http://www.somewhere.com',
			`/test?p_p_id=random&_random_redirect=${encodeURIComponent(
				'http://localhost/'
			)}`
		);
	});

	it('Modifies preexisting portlet_ns_backURL parameter to use the actual URL', () => {
		assertActionLinkURL(
			'/test?p_p_id=random&_random_backURL=http://www.somewhere.com',
			`/test?p_p_id=random&_random_backURL=${encodeURIComponent(
				'http://localhost/'
			)}`
		);
	});

	it('Modifies any _backURL parameter to use the actual URL', () => {
		assertActionLinkURL(
			'/test?backURL=http://www.somewhere.com',
			`/test?backURL=${encodeURIComponent('http://localhost/')}`
		);
	});

	it('Adds the _redirect and _backURL parameters to use the actual URL if the url includes a p_p_id parameter', () => {
		assertActionLinkURL(
			'/test?p_p_id=random',
			`/test?p_p_id=random&_random_redirect=${encodeURIComponent(
				'http://localhost/'
			)}&_random_backURL=${encodeURIComponent('http://localhost/')}`
		);
	});

	it('Does not change the _redirect and _backURL parameters when target is not "link"', () => {
		assertActionNonLinkURL(
			'/test?p_p_id=random&_random_backURL=http://www.somewhere.com'
		);
	});

	it('Does not add the _redirect and _backURL parameters when target is not "link"', () => {
		assertActionNonLinkURL('/test?p_p_id=random');
	});
});

describe('formatActionURL helper. Full interpolation', () => {
	it('Target is not "link", returns item data as it comes', () => {
		assertActionNonLinkURL('{urls.path}', item.urls.path);

		assertActionNonLinkURL('{urls.simple}', item.urls.simple);

		assertActionNonLinkURL('{urls.specialChars}', item.urls.specialChars);

		assertActionNonLinkURL(
			'{urls.specialCharsEncoded}',
			item.urls.specialCharsEncoded
		);

		assertActionNonLinkURL('{urls.withBackURL}', item.urls.withBackURL);

		assertActionNonLinkURL('{urls.withPPId}', item.urls.withPPId);
	});

	it('Target is "link", returns item data as it comes', () => {
		assertActionLinkURL('{urls.path}', item.urls.path);

		assertActionLinkURL('{urls.simple}', item.urls.simple);

		assertActionLinkURL('{urls.specialChars}', item.urls.specialChars);

		assertActionLinkURL(
			'{urls.specialCharsEncoded}',
			item.urls.specialCharsEncoded
		);

		assertActionLinkURL('{urls.withBackURL}', item.urls.withBackURL);

		assertActionLinkURL('{urls.withPPId}', item.urls.withPPId);
	});
});

describe('formatActionURL helper. Partial interpolation. Target is "link". Returns the URL with encoded interpolated values', () => {
	it('path interpolation', () => {
		assertActionLinkURL('/o/{id}', `/o/${encodedItem.id}`);

		assertActionLinkURL(
			'/o/{name}?p=á#ha$h',
			`/o/${encodedItem.name}?p=á#ha$h`
		);

		// no backURL replacement is expected in path interpolation

		assertActionLinkURL(
			'/o/{urls.withBackURL}?p=á#ha$h',
			`/o/${encodedItem.urls.withBackURL}?p=á#ha$h`
		);
	});

	it('query interpolation', () => {
		assertActionLinkURL('/o?p={id}', `/o?p=${encodedItem.id}`);

		assertActionLinkURL(
			'/o?p={urls.specialChars}#ha$h',
			`/o?p=${encodedItem.urls.specialChars}#ha$h`
		);

		assertActionLinkURL(
			'/o?p={urls.specialCharsEncoded}#ha$h',
			`/o?p=${encodedItem.urls.specialCharsEncoded}#ha$h`
		);

		// no p_p_ID replacement is expected target is not link

		assertActionLinkURL(
			'/o/?p={urls.withPPId}#ha$h',
			`/o/?p=${encodedItem.urls.withPPId}#ha$h`
		);
	});

	it('hash interpolation', () => {
		assertActionLinkURL('/o#{id}', `/o#${encodedItem.id}`);

		assertActionLinkURL('/o#{name}', `/o#${encodedItem.name}`);

		assertActionLinkURL(
			'/o#{urls.specialChars}',
			`/o#${encodedItem.urls.specialChars}`
		);

		// no redirect replacement is expected in hash interpolation

		assertActionLinkURL(
			'/o#{urls.withRedirect}#ha$h',
			`/o#${encodedItem.urls.withRedirect}#ha$h`
		);
	});

	it('multiple interpolation', () => {
		assertActionLinkURL(
			'/{id}/{urls.specialCharsEncoded}?p={urls.specialChars}#{name}',
			`/${encodedItem.id}/${encodedItem.urls.specialCharsEncoded}?p=${encodedItem.urls.specialChars}#${encodedItem.name}`
		);

		// backURL replacement is expected (param value is not interpolated)

		assertActionLinkURL(
			'/{id}/{urls.withRedirect}?backURL=foo&p={urls.specialChars}#{name}',
			`/${encodedItem.id}/${encodedItem.urls.withRedirect}?backURL=${encodeURIComponent(
				'http://localhost/'
			)}&p=${encodedItem.urls.specialChars}#${encodedItem.name}`
		);

		// backURL replacement is expected (param value is interpolated)

		assertActionLinkURL(
			'/{id}/{urls.withRedirect}?backURL={urls.specialChars}#{name}',
			`/${encodedItem.id}/${encodedItem.urls.withRedirect}?backURL=${encodeURIComponent(
				'http://localhost/'
			)}#${encodedItem.name}`
		);
	});
});

describe('formatActionURL helper. Partial interpolation. Target is not "link". Returns the URL with encoded interpolated values', () => {
	it('path interpolation', () => {
		assertActionNonLinkURL('/o/{id}', `/o/${encodedItem.id}`);

		assertActionNonLinkURL(
			'/o/{name}?p=á#ha$h',
			`/o/${encodedItem.name}?p=á#ha$h`
		);

		// no backURL replacement is expected when target is not link

		assertActionNonLinkURL(
			'/o/{urls.withBackURL}?p=á#ha$h',
			`/o/${encodedItem.urls.withBackURL}?p=á#ha$h`
		);
	});

	it('query interpolation', () => {
		assertActionNonLinkURL('/o?p={id}', `/o?p=${encodedItem.id}`);

		assertActionNonLinkURL(
			'/o?p={urls.specialChars}#ha$h',
			`/o?p=${encodedItem.urls.specialChars}#ha$h`
		);

		assertActionNonLinkURL(
			'/o?p={urls.specialCharsEncoded}#ha$h',
			`/o?p=${encodedItem.urls.specialCharsEncoded}#ha$h`
		);

		// no p_p_ID replacement is expected target is not link

		assertActionNonLinkURL(
			'/o/?p={urls.withPPId}#ha$h',
			`/o/?p=${encodedItem.urls.withPPId}#ha$h`
		);
	});

	it('hash interpolation', () => {
		assertActionNonLinkURL('/o#{id}', `/o#${encodedItem.id}`);

		assertActionNonLinkURL('/o#{name}', `/o#${encodedItem.name}`);

		assertActionNonLinkURL(
			'/o#{urls.specialChars}',
			`/o#${encodedItem.urls.specialChars}`
		);

		// no redirect replacement is expected when target is not link

		assertActionNonLinkURL(
			'/o#{urls.withRedirect}#ha$h',
			`/o#${encodedItem.urls.withRedirect}#ha$h`
		);
	});

	it('multiple interpolation', () => {
		assertActionNonLinkURL(
			'/{id}/{urls.specialCharsEncoded}?p={urls.specialChars}#{name}',
			`/${encodedItem.id}/${encodedItem.urls.specialCharsEncoded}?p=${encodedItem.urls.specialChars}#${encodedItem.name}`
		);

		// no backURL replacement is expected when target is not link (param value is not interpolated)

		assertActionNonLinkURL(
			'/{id}/{urls.withRedirect}?backURL=foo&p={urls.specialChars}#{name}',
			`/${encodedItem.id}/${encodedItem.urls.withRedirect}?backURL=foo&p=${encodedItem.urls.specialChars}#${encodedItem.name}`
		);

		// no backURL replacement is expected when target is not link (param value is interpolated)

		assertActionNonLinkURL(
			'/{id}/{urls.withRedirect}?backURL={urls.specialChars}#{name}',
			`/${encodedItem.id}/${encodedItem.urls.withRedirect}?backURL=${encodedItem.urls.specialChars}#${encodedItem.name}`
		);
	});
});
