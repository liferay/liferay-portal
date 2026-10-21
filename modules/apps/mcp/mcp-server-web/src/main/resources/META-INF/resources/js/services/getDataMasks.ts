/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {addParams} from 'frontend-js-web';

import {DataMask} from '../types';
import {DATA_MASKS_URL} from './constants';
import {fetchAllItems} from './fetchAllItems';

export function getDataMasks() {
	return fetchAllItems<DataMask>((page, pageSize) =>
		addParams(
			{page: String(page), pageSize: String(pageSize)},
			DATA_MASKS_URL
		)
	);
}
