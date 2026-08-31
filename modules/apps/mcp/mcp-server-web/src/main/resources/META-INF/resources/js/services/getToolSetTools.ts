/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {addParams} from 'frontend-js-web';

import {ToolSummary} from '../types';
import {RequestResult} from './ApiHelper';
import {TOOL_SETS_URL} from './constants';
import {fetchAllItems} from './fetchAllItems';

export async function getToolSetTools(
	toolSetName: string
): Promise<RequestResult<ToolSummary[]>> {
	const {data, error} = await fetchAllItems<ToolSummary>((page, pageSize) =>
		addParams(
			{fields: 'name', page: String(page), pageSize: String(pageSize)},
			`${TOOL_SETS_URL}/${encodeURIComponent(toolSetName)}/tool-summaries`
		)
	);

	if (error) {
		return {data: null, error};
	}

	return {data: data?.items ?? [], error: null};
}
