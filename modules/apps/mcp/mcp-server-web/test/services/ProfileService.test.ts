/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import fetch from 'jest-fetch-mock';

import ProfileService from '../../src/main/resources/META-INF/resources/js/services/ProfileService';

describe('ProfileService', () => {
	it('encodes the profile external reference code in the path', async () => {
		fetch.mockResponseOnce(JSON.stringify({}));

		await ProfileService.getProfile('a/b c');

		expect(fetch.mock.calls[0][0]).toBe(
			'/o/mcp/server-profiles/by-external-reference-code/a%2Fb%20c'
		);
	});
});
