/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fetch} from 'frontend-js-web';

import {getExistingTags} from '../../../../src/main/resources/META-INF/resources/js/Categorization/services/getExistingTags';

jest.mock('frontend-js-web', () => ({fetch: jest.fn()}));

const mockFetch = fetch as jest.MockedFunction<typeof fetch>;

function page(items: unknown[], lastPage = 1) {
	return {
		json: () => Promise.resolve({items, lastPage}),
		ok: true,
	};
}

describe('getExistingTags', () => {
	beforeEach(() => {
		mockFetch.mockReset();

		global.Liferay = {
			...global.Liferay,
			ThemeDisplay: {
				...global.Liferay?.ThemeDisplay,
				getPortalURL: () => 'http://localhost:8080',
			},
		} as never;
	});

	it('queries the site keywords endpoint and maps to names', async () => {
		mockFetch.mockResolvedValue(
			page([{name: 'Japan'}, {name: 'Travel'}]) as never
		);

		const result = await getExistingTags({
			cmsGroupId: 20124,
			scopeId: 555,
		});

		expect(result).toEqual(['Japan', 'Travel']);

		const calledURL = decodeURIComponent(
			mockFetch.mock.calls[0][0] as string
		);

		expect(calledURL).toContain('/sites/555/keywords');
	});

	it('uses the cmsGroupId with a groupIds filter for a negative scope', async () => {
		mockFetch.mockResolvedValue(page([]) as never);

		await getExistingTags({cmsGroupId: 20124, scopeId: -1});

		const calledURL = decodeURIComponent(
			mockFetch.mock.calls[0][0] as string
		);

		expect(calledURL).toContain('/sites/20124/keywords');
		expect(calledURL).toContain("groupIds in ('-1')");
	});
});
