/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fetch} from 'frontend-js-web';

const PAGE_SIZE = 100;

interface TaxonomyPage<T> {
	items?: T[];
	lastPage?: number;
}

export async function getTaxonomyItems<T>(
	url: string,
	max: number
): Promise<T[]> {
	const separator = url.includes('?') ? '&' : '?';

	const items: T[] = [];

	let lastPage = 1;
	let page = 1;

	do {
		const response = await fetch(
			`${url}${separator}page=${page}&pageSize=${PAGE_SIZE}`,
			{headers: new Headers({Accept: 'application/json'})}
		);

		if (!response.ok) {
			throw new Error(
				`Unable to fetch taxonomy items: ${response.statusText}`
			);
		}

		const data: TaxonomyPage<T> = await response.json();

		items.push(...(data.items ?? []));

		lastPage = data.lastPage ?? page;

		page += 1;
	} while (items.length < max && page <= lastPage);

	if (items.length > max) {
		console.warn(
			`getTaxonomyItems: capping candidate set at ${max} (received ${items.length}).`
		);
	}

	return items.slice(0, max);
}
