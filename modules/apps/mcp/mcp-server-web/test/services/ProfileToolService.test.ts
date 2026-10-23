/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import fetch from 'jest-fetch-mock';

import ProfileToolService from '../../src/main/resources/META-INF/resources/js/services/ProfileToolService';

describe('ProfileToolService', () => {
	it('escapes the quotes of the profile external reference code in the filter', async () => {
		fetch.mockResponseOnce(
			JSON.stringify({items: [], lastPage: 1, totalCount: 0})
		);

		await ProfileToolService.getProfileTools("O'BRIEN");

		const url = new URL(
			fetch.mock.calls[0][0] as string,
			'http://localhost'
		);

		expect(url.searchParams.get('filter')).toBe(
			"r_mcpServerProfileToTools_l_mcpServerProfileERC eq 'O''BRIEN'"
		);
	});
});
