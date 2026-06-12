/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ApiHelper, {RequestResult} from './ApiHelper';

const PROFILES_URL = '/o/mcp/server-profiles';

export function getProfile(
	profileId: number
): Promise<RequestResult<{name?: string}>> {
	return ApiHelper.get<{name?: string}>(`${PROFILES_URL}/${profileId}`);
}
