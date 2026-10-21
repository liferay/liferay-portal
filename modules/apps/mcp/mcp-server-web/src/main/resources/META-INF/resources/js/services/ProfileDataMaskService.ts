/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {addParams} from 'frontend-js-web';

import {ProfileDataMask} from '../types/ProfileDataMask';
import toODataStringLiteral from '../utils/toODataStringLiteral';
import ApiHelper from './ApiHelper';
import {fetchAllItems} from './fetchAllItems';

const PROFILE_DATA_MASKS_URL = '/o/mcp/server-profile-data-masks';

export interface ProfileDataMaskFilters {
	dataMaskExternalReferenceCode?: string;
	mcpServerProfileExternalReferenceCode?: string;
}

function deleteProfileDataMask(id: number) {
	return ApiHelper.del<void>(`${PROFILE_DATA_MASKS_URL}/${id}`);
}

function getProfileDataMasks(filters: ProfileDataMaskFilters = {}) {
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

function patchProfileDataMask(id: number, payload: Partial<ProfileDataMask>) {
	return ApiHelper.patch<ProfileDataMask>(
		`${PROFILE_DATA_MASKS_URL}/${id}`,
		payload
	);
}

function postProfileDataMask(payload: ProfileDataMask) {
	return ApiHelper.post<ProfileDataMask>(PROFILE_DATA_MASKS_URL, payload);
}

export default {
	deleteProfileDataMask,
	getProfileDataMasks,
	patchProfileDataMask,
	postProfileDataMask,
};
