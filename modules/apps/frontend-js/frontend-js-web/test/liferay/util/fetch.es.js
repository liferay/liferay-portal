/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import fetchWrapper from '../../../src/main/resources/META-INF/resources/liferay/util/fetch.es';

describe('Liferay.Util.fetch', () => {
	const externalOriginUrl = 'http://externalOriginUrl.com';
	const sameOriginUrl = window.location.origin + '/o/test';

	beforeEach(() => {
		fetch.mockResponse('');
	});

	it('applies default settings if none are given', () => {
		fetchWrapper(externalOriginUrl);

		const init = {
			headers: new Headers(),
		};

		expect(fetch).toHaveBeenCalledWith(externalOriginUrl, init);
	});

	it('adds auth-token and credentials if origin is the same', () => {
		fetchWrapper(sameOriginUrl);

		const mergedInit = {
			credentials: 'include',
			headers: new Headers({
				'x-csrf-token': 'default-mocked-auth-token',
			}),
		};

		expect(fetch).toHaveBeenCalledWith(sameOriginUrl, mergedInit);
	});

	it('overrides default auth-token', () => {
		fetchWrapper(sameOriginUrl, {
			headers: new Headers({
				'x-csrf-token': 'asdf',
			}),
		});

		const mergedInit = {
			credentials: 'include',
			headers: new Headers({
				'x-csrf-token': 'asdf',
			}),
		};

		expect(fetch).toHaveBeenCalledWith(sameOriginUrl, mergedInit);
	});

	it('overrides default settings with given settings', () => {
		const url = window.location.origin + '/o/test';

		const init = {
			credentials: 'omit',
			headers: {
				'x-csrf-token': 'efgh',
			},
		};

		fetchWrapper(url, init);

		const mergedInit = {
			credentials: 'omit',
			headers: new Headers({
				'x-csrf-token': 'efgh',
			}),
		};

		expect(fetch).toHaveBeenCalledWith(url, mergedInit);
	});

	it('overrides default settings with given settings', () => {
		const init = {
			credentials: 'omit',
			headers: {
				'x-csrf-token': 'efgh',
			},
		};

		fetchWrapper(externalOriginUrl, init);

		const mergedInit = {
			credentials: 'omit',
			headers: new Headers({
				'x-csrf-token': 'efgh',
			}),
		};

		expect(fetch).toHaveBeenCalledWith(externalOriginUrl, mergedInit);
	});

	it('merges default settings with given different settings', () => {
		const init = {
			headers: {
				'content-type': 'application/json',
			},
			method: 'GET',
		};

		fetchWrapper(externalOriginUrl, init);

		const mergedInit = {
			headers: new Headers({
				'content-type': 'application/json',
			}),
			method: 'GET',
		};

		expect(fetch).toHaveBeenCalledWith(externalOriginUrl, mergedInit);
	});

	it('sets given headers to lower-case before merging with defaults', () => {
		const init = {
			headers: {
				'Content-Type': 'application/json',
				'X-CSRF-token': 'efgh',
			},
		};

		fetchWrapper(externalOriginUrl, init);

		const mergedInit = {
			headers: new Headers({
				'content-type': 'application/json',
				'x-csrf-token': 'efgh',
			}),
		};

		expect(fetch).toHaveBeenCalledWith(externalOriginUrl, mergedInit);
	});

	it('merges given multiple headers, setting name to lower-case', () => {
		const init = {
			headers: {
				'Content-Type': 'application/json',
				'Content-type': 'multipart/form-data',
			},
		};

		fetchWrapper(externalOriginUrl, init);

		const mergedInit = {
			headers: new Headers({
				'content-type': 'application/json, multipart/form-data',
			}),
		};

		expect(fetch).toHaveBeenCalledWith(externalOriginUrl, mergedInit);
	});

	it('allows given headers to be an array of arrays', () => {
		const init = {
			headers: [
				['content-type', 'application/json'],
				['x-csrf-token', 'efgh'],
			],
		};

		fetchWrapper(externalOriginUrl, init);

		const mergedInit = {
			headers: new Headers({
				'content-type': 'application/json',
				'x-csrf-token': 'efgh',
			}),
		};

		expect(fetch).toHaveBeenCalledWith(externalOriginUrl, mergedInit);
	});

	it('allows given headers to be a Headers object', () => {
		const init = {
			headers: new Headers({
				'content-type': 'application/json',
				'x-csrf-token': 'efgh',
			}),
		};

		fetchWrapper(externalOriginUrl, init);

		const mergedInit = {
			headers: new Headers({
				'content-type': 'application/json',
				'x-csrf-token': 'efgh',
			}),
		};

		expect(fetch).toHaveBeenCalledWith(externalOriginUrl, mergedInit);
	});

	it('includes path context when it is defined on themeDisplay.getPathContext()', () => {
		const sameOriginUrl = '/o/api/something';

		const init = {
			credentials: 'include',
			headers: new Headers({
				'content-type': 'application/json',
				'x-csrf-token': 'efgh',
			}),
		};

		window.Liferay.ThemeDisplay = {
			getDoAsUserIdEncoded: () => '',
			getPathContext: () => '/myportal',
		};

		fetchWrapper(sameOriginUrl, init);

		expect(fetch).toHaveBeenCalledWith(`/myportal${sameOriginUrl}`, init);
	});

	it.each(['edit', 'preview'])(
		'adds the launch set headers when a launch set is in %s mode',
		(launchSetMode) => {
			window.Liferay.ThemeDisplay = {
				getDoAsUserIdEncoded: () => '',
				getLaunchSetId: () => '123',
				getLaunchSetMode: () => launchSetMode,
				getPathContext: () => '',
			};

			fetchWrapper(sameOriginUrl);

			const [, init] = fetch.mock.calls[fetch.mock.calls.length - 1];

			expect(init.headers.get('x-liferay-launch-set-id')).toBe('123');
			expect(init.headers.get('x-liferay-launch-set-mode')).toBe(
				launchSetMode
			);
		}
	);

	it('does not add the launch set headers outside of a launch set', () => {
		window.Liferay.ThemeDisplay = {
			getDoAsUserIdEncoded: () => '',
			getLaunchSetId: () => '0',
			getLaunchSetMode: () => '',
			getPathContext: () => '',
		};

		fetchWrapper(sameOriginUrl);

		const [, init] = fetch.mock.calls[fetch.mock.calls.length - 1];

		expect(init.headers.has('x-liferay-launch-set-id')).toBe(false);
		expect(init.headers.has('x-liferay-launch-set-mode')).toBe(false);
	});
});
