/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {addParams} from 'frontend-js-web';

import {toODataStringLiteral} from '../utils';
import ApiHelper, {RequestResult} from './ApiHelper';

const PROFILE_DATA_MASKS_URL = '/o/mcp/server-profile-data-masks';

export interface ProfileDataMaskAssociation {
	dataMaskExternalReferenceCode?: string;
	mcpServerProfileExternalReferenceCode?: string;
}

export function getProfileDataMasks(
	dataMaskExternalReferenceCode?: string
): Promise<
	RequestResult<{items: ProfileDataMaskAssociation[]; totalCount: number}>
> {
	const params: Record<string, string> = {};

	if (dataMaskExternalReferenceCode) {
		params.filter = `dataMaskExternalReferenceCode eq ${toODataStringLiteral(
			dataMaskExternalReferenceCode
		)}`;
		params.pageSize = '1';
	}

	return ApiHelper.get<{
		items: ProfileDataMaskAssociation[];
		totalCount: number;
	}>(addParams(params, PROFILE_DATA_MASKS_URL));
}
