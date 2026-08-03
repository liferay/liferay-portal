/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {addParams} from 'frontend-js-web';

import {ProfileDataMask} from '../types';
import {toODataStringLiteral} from '../utils';
import ApiHelper, {RequestResult} from './ApiHelper';
import {PROFILE_DATA_MASKS_URL} from './constants';

export interface ProfileDataMaskFilters {
	dataMaskExternalReferenceCode?: string;
	mcpServerProfileExternalReferenceCode?: string;
}

export function getProfileDataMasks(
	filters: ProfileDataMaskFilters = {}
): Promise<RequestResult<{items: ProfileDataMask[]; totalCount: number}>> {
	const params: Record<string, string> = {};

	if (filters.dataMaskExternalReferenceCode) {
		params.filter = `dataMaskExternalReferenceCode eq ${toODataStringLiteral(
			filters.dataMaskExternalReferenceCode
		)}`;
		params.pageSize = '1';
	}
	else if (filters.mcpServerProfileExternalReferenceCode) {
		params.filter = `mcpServerProfileExternalReferenceCode eq ${toODataStringLiteral(
			filters.mcpServerProfileExternalReferenceCode
		)}`;
		params.pageSize = '200';
		params.sort = 'executionOrder:asc';
	}

	return ApiHelper.get<{
		items: ProfileDataMask[];
		totalCount: number;
	}>(addParams(params, PROFILE_DATA_MASKS_URL));
}
