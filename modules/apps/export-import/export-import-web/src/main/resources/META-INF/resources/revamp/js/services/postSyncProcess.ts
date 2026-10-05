/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {SyncProcessRequest} from '../types/exportImportProcess';
import ApiHelper, {RequestResult} from './ApiHelper';

export interface SyncProcessParams {
	syncProcessRequest: SyncProcessRequest;
	url: string;
}

export function postSyncProcess({
	syncProcessRequest,
	url,
}: SyncProcessParams): Promise<RequestResult<void>> {
	return ApiHelper.post<void>(url, syncProcessRequest);
}
