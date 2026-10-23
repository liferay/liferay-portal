/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Profile, ProfilePayload} from '../types/Profile';
import ApiHelper from './ApiHelper';

const PROFILES_URL = '/o/mcp/server-profiles';

function deleteProfile(externalReferenceCode: string) {
	return ApiHelper.del<void>(
		`${PROFILES_URL}/by-external-reference-code/${encodeURIComponent(externalReferenceCode)}`
	);
}

function getProfile(externalReferenceCode: string) {
	return ApiHelper.get<Profile>(
		`${PROFILES_URL}/by-external-reference-code/${encodeURIComponent(externalReferenceCode)}`
	);
}

function patchProfile(externalReferenceCode: string, payload: ProfilePayload) {
	return ApiHelper.patch<Profile>(
		`${PROFILES_URL}/by-external-reference-code/${encodeURIComponent(externalReferenceCode)}`,
		payload
	);
}

function postProfile(payload: ProfilePayload) {
	return ApiHelper.post<Profile>(PROFILES_URL, payload);
}

export default {
	deleteProfile,
	getProfile,
	patchProfile,
	postProfile,
};
