/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fetch} from 'frontend-js-web';

import {DEFAULT_FETCH_HEADERS, PAGE_SIZE} from './constants';
import openDefaultFailureToast from './openDefaultFailureToast';
import {IPickList} from './types';

export default async function getAllPicklists(
	page: number = 1,
	items: IPickList[] = []
) {
	const response = await fetch(
		`/o/headless-admin-list-type/v1.0/list-type-definitions?pageSize=${PAGE_SIZE}&page=${page}`,
		{
			headers: DEFAULT_FETCH_HEADERS,
		}
	);

	if (!response.ok) {
		openDefaultFailureToast();

		return [];
	}

	const responseJSON = await response.json();

	items = [...items, ...responseJSON.items];

	if (responseJSON.lastPage > page) {
		items = await getAllPicklists(page + 1, items);
	}

	return items;
}
