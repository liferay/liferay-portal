/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ApiHelper from './ApiHelper';
import {PROFILES_URL} from './constants';

export function deleteProfile(externalReferenceCode: string) {
	return ApiHelper.del<void>(
		`${PROFILES_URL}/by-external-reference-code/${externalReferenceCode}`
	);
}
