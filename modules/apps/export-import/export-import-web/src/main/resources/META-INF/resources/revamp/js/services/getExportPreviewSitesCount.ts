/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {addParams} from 'frontend-js-web';

import {PreviewSite} from '../types/exportImportPreview';
import ApiHelper, {RequestResult} from './ApiHelper';

export interface PreviewSitesPage {
	items: PreviewSite[];
	lastPage: number;
	page: number;
	pageSize: number;
	totalCount: number;
}

/**
 * Asks only how many sites there are. The dialog reads the sites themselves,
 * but the row that opens it shows the total before it is opened.
 */
export function getExportPreviewSitesCount(
	url: string
): Promise<RequestResult<PreviewSitesPage>> {
	return ApiHelper.get<PreviewSitesPage>(
		addParams({page: '1', pageSize: '1'}, url)
	);
}
