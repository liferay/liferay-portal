/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {addParams} from 'frontend-js-web';

import {ProfileTool} from '../types';
import {RequestResult} from './ApiHelper';
import {PROFILE_TOOLS_URL} from './constants';
import {fetchAllItems} from './fetchAllItems';

export function getProfileTools(
	profileExternalReferenceCode: string
): Promise<RequestResult<{items: ProfileTool[]; totalCount: number}>> {
	const filter = `r_mcpServerProfileToTools_l_mcpServerProfileERC eq '${profileExternalReferenceCode}'`;

	return fetchAllItems<ProfileTool>((page, pageSize) =>
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
