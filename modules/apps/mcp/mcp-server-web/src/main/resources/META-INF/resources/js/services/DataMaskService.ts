/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {addParams} from 'frontend-js-web';

import {DataMask, DataMaskPayload} from '../types/DataMask';
import ApiHelper from './ApiHelper';
import {fetchAllItems} from './fetchAllItems';

const DATA_MASKS_URL = '/o/data-masks';

function deleteDataMask(id: number) {
	return ApiHelper.del<void>(`${DATA_MASKS_URL}/${id}`);
}

function getDataMask(id: number) {
	return ApiHelper.get<DataMask>(`${DATA_MASKS_URL}/${id}`);
}

function getDataMasks() {
	return fetchAllItems<DataMask>((page, pageSize) =>
		addParams(
			{page: String(page), pageSize: String(pageSize)},
			DATA_MASKS_URL
		)
	);
}

function patchDataMask(id: number, payload: DataMaskPayload) {
	return ApiHelper.patch<DataMask>(`${DATA_MASKS_URL}/${id}`, payload);
}

function postDataMask(payload: DataMaskPayload) {
	return ApiHelper.post<DataMask>(DATA_MASKS_URL, payload);
}

export default {
	deleteDataMask,
	getDataMask,
	getDataMasks,
	patchDataMask,
	postDataMask,
};
