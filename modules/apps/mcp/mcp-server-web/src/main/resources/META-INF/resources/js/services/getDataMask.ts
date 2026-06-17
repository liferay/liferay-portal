/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {DataMask} from '../types';
import ApiHelper, {RequestResult} from './ApiHelper';
import {DATA_MASKS_URL} from './dataMasksURL';

export function getDataMask(id: number): Promise<RequestResult<DataMask>> {
	return ApiHelper.get<DataMask>(`${DATA_MASKS_URL}/${id}`);
}
