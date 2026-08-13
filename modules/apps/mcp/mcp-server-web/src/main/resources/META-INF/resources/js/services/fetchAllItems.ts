/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ApiHelper, {RequestResult} from './ApiHelper';

const PAGE_SIZE = 200;

export async function fetchAllItems<T>(
	buildURL: (page: number, pageSize: number) => string
): Promise<RequestResult<{items: T[]; totalCount: number}>> {
	const items: T[] = [];

	let lastPage = 1;
	let page = 1;
	let totalCount = 0;

	while (page <= lastPage) {
		const result = await ApiHelper.get<{
			items: T[];
			lastPage: number;
			totalCount: number;
		}>(buildURL(page, PAGE_SIZE));

		if (result.error) {
			return result;
		}

		items.push(...(result.data?.items ?? []));

		lastPage = result.data?.lastPage ?? 1;
		totalCount = result.data?.totalCount ?? 0;
		page += 1;
	}

	return {data: {items, totalCount}, error: null};
}
