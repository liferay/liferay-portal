/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {addParams} from 'frontend-js-web';

import {Tool, ToolSet, ToolSummary} from '../types/Tool';
import ApiHelper from './ApiHelper';
import {fetchAllItems} from './fetchAllItems';

const TOOL_SETS_URL = '/o/mcp-server/v1.0/tool-sets';

function getTool(toolSetName: string, toolName: string) {
	return ApiHelper.get<Pick<Tool, 'outputSchema'>>(
		`${TOOL_SETS_URL}/${encodeURIComponent(toolSetName)}/tools/${encodeURIComponent(toolName)}?fields=outputSchema&nestedFields=outputSchema`
	);
}

async function getToolSets() {
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

async function getToolSetTools(toolSetName: string) {
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

export default {
	getTool,
	getToolSetTools,
	getToolSets,
};
