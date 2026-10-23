/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {addParams} from 'frontend-js-web';

import {ProfileTool, ProfileToolPayload} from '../types/ProfileTool';
import toODataStringLiteral from '../utils/toODataStringLiteral';
import ApiHelper from './ApiHelper';
import {fetchAllItems} from './fetchAllItems';

const PROFILE_TOOLS_URL = '/o/mcp/server-profile-tools';

function deleteProfileTool(externalReferenceCode: string) {
	return ApiHelper.del<void>(
		`${PROFILE_TOOLS_URL}/by-external-reference-code/${encodeURIComponent(
			externalReferenceCode
		)}`
	);
}

function getProfileTools(profileExternalReferenceCode: string) {
	const filter = `r_mcpServerProfileToTools_l_mcpServerProfileERC eq ${toODataStringLiteral(profileExternalReferenceCode)}`;

	return fetchAllItems<
		Pick<ProfileTool, 'externalReferenceCode' | 'toolName' | 'toolSetName'>
	>((page, pageSize) =>
		addParams(
			{
				fields: 'externalReferenceCode,toolName,toolSetName',
				filter,
				page: String(page),
				pageSize: String(pageSize),
				sort: 'toolName:asc',
			},
			PROFILE_TOOLS_URL
		)
	);
}

function patchProfileTool(
	externalReferenceCode: string,
	payload: Partial<ProfileTool>
) {
	return ApiHelper.patch<ProfileTool>(
		`${PROFILE_TOOLS_URL}/by-external-reference-code/${encodeURIComponent(
			externalReferenceCode
		)}`,
		payload
	);
}

function postProfileTool(payload: ProfileToolPayload) {
	return ApiHelper.post<ProfileTool>(PROFILE_TOOLS_URL, payload);
}

export default {
	deleteProfileTool,
	getProfileTools,
	patchProfileTool,
	postProfileTool,
};
