/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ProfileDataMask} from '../types';
import ApiHelper, {RequestResult} from './ApiHelper';
import {PROFILE_DATA_MASKS_URL} from './constants';

export function patchProfileDataMask(
	id: number,
	payload: Partial<ProfileDataMask>
): Promise<RequestResult<ProfileDataMask>> {
	return ApiHelper.patch<ProfileDataMask>(
		`${PROFILE_DATA_MASKS_URL}/${id}`,
		payload
	);
}
