/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Profile} from '../types';
import ApiHelper, {RequestResult} from './ApiHelper';
import {PROFILES_URL} from './constants';

export function getProfile(
	externalReferenceCode: string
): Promise<RequestResult<Profile>> {
	return ApiHelper.get<Profile>(
		`${PROFILES_URL}/by-external-reference-code/${externalReferenceCode}`
	);
}
