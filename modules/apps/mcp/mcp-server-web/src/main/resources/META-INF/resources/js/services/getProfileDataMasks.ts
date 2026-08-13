/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {addParams} from 'frontend-js-web';

import {ProfileDataMask} from '../types';
import {toODataStringLiteral} from '../utils';
import ApiHelper, {RequestResult} from './ApiHelper';
import {PROFILE_DATA_MASKS_URL} from './constants';
import {fetchAllItems} from './fetchAllItems';

export interface ProfileDataMaskFilters {
	dataMaskExternalReferenceCode?: string;
	mcpServerProfileExternalReferenceCode?: string;
}

export function getProfileDataMasks(
	filters: ProfileDataMaskFilters = {}
): Promise<RequestResult<{items: ProfileDataMask[]; totalCount: number}>> {
	if (filters.dataMaskExternalReferenceCode) {
		return ApiHelper.get<{items: ProfileDataMask[]; totalCount: number}>(
			addParams(
				{
					filter: `dataMaskExternalReferenceCode eq ${toODataStringLiteral(
						filters.dataMaskExternalReferenceCode
					)}`,
					pageSize: '1',
				},
				PROFILE_DATA_MASKS_URL
			)
		);
	}

	if (filters.mcpServerProfileExternalReferenceCode) {
		const filter = `mcpServerProfileExternalReferenceCode eq ${toODataStringLiteral(
			filters.mcpServerProfileExternalReferenceCode
		)}`;

		return fetchAllItems<ProfileDataMask>((page, pageSize) =>
			addParams(
				{
					filter,
					page: String(page),
					pageSize: String(pageSize),
					sort: 'executionOrder:asc',
				},
				PROFILE_DATA_MASKS_URL
			)
		);
	}

	return ApiHelper.get<{items: ProfileDataMask[]; totalCount: number}>(
		addParams({}, PROFILE_DATA_MASKS_URL)
	);
}
