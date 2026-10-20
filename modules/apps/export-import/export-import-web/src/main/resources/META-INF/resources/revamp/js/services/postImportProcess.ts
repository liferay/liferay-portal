/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	ImportProcess,
	ImportProcessRequest,
	InstanceImportProcessRequest,
} from '../types/exportImportProcess';
import ApiHelper, {RequestResult} from './ApiHelper';

export interface ImportProcessParams {
	importProcessRequest: ImportProcessRequest | InstanceImportProcessRequest;
	url: string;
}

export function postImportProcess({
	importProcessRequest,
	url,
}: ImportProcessParams): Promise<RequestResult<ImportProcess>> {
	return ApiHelper.post<ImportProcess>(url, importProcessRequest);
}
