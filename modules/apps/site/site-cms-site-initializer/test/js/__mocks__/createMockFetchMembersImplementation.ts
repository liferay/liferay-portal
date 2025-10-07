/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	UserAccount,
	UserGroup,
} from '../../../src/main/resources/META-INF/resources/js/common/types/UserAccount';
import {mockFetch} from './frontend-js-web';

const mockFetchImplementation = mockFetch as jest.Mock<Promise<Response>>;

export function createMockFetchMembersImplementation(data: {
	groups?: UserGroup[];
	users?: UserAccount[];
}) {
	mockFetchImplementation.mockImplementation(async (url: string) => {
		const parsedUrl = new URL(url);
		const path = parsedUrl.pathname;
		const filterParam = parsedUrl.searchParams.get('filter');
		const searchParam = parsedUrl.searchParams.get('search');

		let itemsToReturn: (UserGroup | UserAccount)[] = [];
		let filterKey = '';

		if (path.includes('/user-accounts') && data.users) {
			itemsToReturn = data.users;
			filterKey = 'id';
		}
		else if (path.includes('/user-groups') && data.groups) {
			itemsToReturn = data.groups;
			filterKey = 'userGroupId';
		}

		if (filterParam) {
			const decodedFilter = decodeURIComponent(filterParam);

			const excludeIds = decodedFilter
				.split(' and ')
				.map((part) => {
					const match = part.match(
						new RegExp(`${filterKey} ne '([^']+)'`)
					);

					return match ? match[1] : null;
				})
				.filter(Boolean);

			itemsToReturn = itemsToReturn.filter(
				(item) => !excludeIds.includes(item.id.toString())
			);
		}

		if (searchParam) {
			itemsToReturn = itemsToReturn.filter((item) =>
				item.name.toLowerCase().includes(searchParam.toLowerCase())
			);
		}

		return {
			headers: new Headers([['Content-Type', 'application/json']]),
			json: async () => ({items: itemsToReturn}),
		} as Response;
	});
}
