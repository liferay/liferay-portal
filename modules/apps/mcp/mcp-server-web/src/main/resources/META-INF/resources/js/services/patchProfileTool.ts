/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ProfileTool} from '../types';
import ApiHelper, {RequestResult} from './ApiHelper';
import {PROFILE_TOOLS_URL} from './constants';

export function patchProfileTool(
	externalReferenceCode: string,
	payload: Partial<ProfileTool>
): Promise<RequestResult<ProfileTool>> {
	return ApiHelper.patch<ProfileTool>(
		`${PROFILE_TOOLS_URL}/by-external-reference-code/${encodeURIComponent(
			externalReferenceCode
		)}`,
		payload
	);
}
