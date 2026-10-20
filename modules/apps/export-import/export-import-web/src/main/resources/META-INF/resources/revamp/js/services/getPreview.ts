/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {addParams} from 'frontend-js-web';

import {Preview} from '../types/exportImportPreview';
import {DateFilter} from '../types/exportImportProcess';
import ApiHelper, {RequestResult} from './ApiHelper';

export interface PreviewParams {
	dateFilter?: DateFilter;
	url: string;
}

export function getPreview({
	dateFilter,
	url,
}: PreviewParams): Promise<RequestResult<Preview>> {
	if (!dateFilter) {
		return ApiHelper.get<Preview>(url);
	}

	const {type, ...dateFilterParams} = dateFilter;

	const params: Record<string, string> = {dateFilterType: type};

	Object.entries(dateFilterParams).forEach(([key, value]) => {
		if (value !== undefined && value !== null && value !== '') {
			params[key] = String(value);
		}
	});

	return ApiHelper.get<Preview>(addParams(params, url));
}
