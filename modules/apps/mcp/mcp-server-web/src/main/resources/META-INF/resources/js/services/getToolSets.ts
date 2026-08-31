/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {addParams} from 'frontend-js-web';

import {ToolSet} from '../types';
import {RequestResult} from './ApiHelper';
import {TOOL_SETS_URL} from './constants';
import {fetchAllItems} from './fetchAllItems';

export async function getToolSets(): Promise<RequestResult<ToolSet[]>> {
	const {data, error} = await fetchAllItems<ToolSet>((page, pageSize) =>
		addParams(
			{page: String(page), pageSize: String(pageSize)},
			TOOL_SETS_URL
		)
	);

	if (error) {
		return {data: null, error};
	}

	return {data: data?.items ?? [], error: null};
}
