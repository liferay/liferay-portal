/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {addParams} from 'frontend-js-web';

import {Site} from '../types/exportImportPreview';
import ApiHelper, {RequestResult} from './ApiHelper';

export interface SitesPage {
	items: Site[];
	lastPage: number;
	page: number;
	pageSize: number;
	totalCount: number;
}

export function getExportPreviewSitesCount(
	url: string
): Promise<RequestResult<SitesPage>> {
	return ApiHelper.get<SitesPage>(addParams({page: '1', pageSize: '1'}, url));
}
