/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	FromUserAgentApplication,
	OAuth2Client,
} from '../../src/main/resources/META-INF/resources/js/client/OAuth2Client';

const fetchMock = fetch as any;

describe('FromUserAgentApplication', () => {
	it('creates a client for a readable application', async () => {
		fetchMock.mockResponseOnce(
			JSON.stringify({
				client_id: 'client-id',
				homePageURL: 'http://localhost:8080',
			})
		);

		await expect(FromUserAgentApplication('erc')).resolves.toBeInstanceOf(
			OAuth2Client
		);
	});

	it('encodes the external reference code in the lookup', async () => {
		fetchMock.mockResponseOnce(
			JSON.stringify({
				client_id: 'client-id',
				homePageURL: 'http://localhost:8080',
			})
		);

		await FromUserAgentApplication('a&b#c');

		expect(fetchMock).toHaveBeenCalledWith(
			expect.stringContaining('externalReferenceCode=a%26b%23c')
		);
	});

	it('names the cause when the application endpoint fails', async () => {
		fetchMock.mockResponseOnce('', {status: 500});

		await expect(FromUserAgentApplication('erc')).rejects.toThrow(
			'Unable to get the application erc because the request failed with status 500'
		);
	});

	it('names the cause when the application is not readable', async () => {
		fetchMock.mockResponseOnce('', {status: 400});

		await expect(FromUserAgentApplication('erc')).rejects.toThrow(
			'No Application User Agent profile found for erc'
		);
	});
});
