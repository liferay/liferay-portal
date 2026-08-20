/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {addParams} from 'frontend-js-web';

import {NormalizedDateFilter} from '../components/date_filter';
import {Preview} from '../types/exportImportPreview';
import ApiHelper, {RequestResult} from './ApiHelper';

export interface PreviewParams {
	query?: NormalizedDateFilter;
	url: string;
}

export function getPreview({
	query = {},
	url,
}: PreviewParams): Promise<RequestResult<Preview>> {
	const params: Record<string, string> = {};

	Object.entries(query).forEach(([key, value]) => {
		if (value !== undefined && value !== null && value !== '') {
			params[key] = String(value);
		}
	});

	return ApiHelper.get<Preview>(
		Object.keys(params).length ? addParams(params, url) : url
	);
}
