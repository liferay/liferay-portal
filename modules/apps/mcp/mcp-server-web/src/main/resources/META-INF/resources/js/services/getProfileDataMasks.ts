/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ApiHelper, {RequestResult} from './ApiHelper';

const PROFILE_DATA_MASKS_URL = '/o/mcp/server-profile-data-masks';

export interface ProfileDataMaskAssociation {
	dataMaskExternalReferenceCode?: string;
	mcpServerProfileExternalReferenceCode?: string;
}

export function getProfileDataMasks(): Promise<
	RequestResult<{items: ProfileDataMaskAssociation[]}>
> {
	return ApiHelper.get<{items: ProfileDataMaskAssociation[]}>(
		`${PROFILE_DATA_MASKS_URL}?pageSize=200`
	);
}
