/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ApiHelper} from '@liferay/site-cms-site-initializer';
import {openToast} from 'frontend-js-components-web';
import {fetch} from 'frontend-js-web';

import {TAnalyticsFilterValue} from '../../main_view/analytics/types';

const ANALYTICS_BASE_URL = '/o/site-dsr-analytics-rest/v1.0';
const STORE_ANALYTICS_FILTERS_URL = '/dsr/analytics/store_filters';

type TParams = Record<string, unknown>;

function _appendParam(
	searchParams: URLSearchParams,
	key: string,
	value: unknown
) {
	if (value === null || value === undefined || value === '') {
		return;
	}

	if (Array.isArray(value)) {
		value.forEach((item) => _appendParam(searchParams, key, item));

		return;
	}

	searchParams.append(key, String(value));
}

async function get<T>(path: string, params: TParams = {}): Promise<T> {
	const searchParams = new URLSearchParams();

	Object.entries(params).forEach(([key, value]) =>
		_appendParam(searchParams, key, value)
	);

	const queryString = searchParams.toString();
	const url = `${ANALYTICS_BASE_URL}${path}${queryString ? `?${queryString}` : ''}`;

	const {data, error} = await ApiHelper.get<T>(url);

	if (error) {
		openToast({
			message: Liferay.Language.get('unexpected-error'),
			type: 'danger',
		});

		throw new Error(error);
	}

	return data as T;
}

function storeFilters(filters: TAnalyticsFilterValue) {
	const url = new URL(
		Liferay.ThemeDisplay.getPathMain() + STORE_ANALYTICS_FILTERS_URL,
		Liferay.ThemeDisplay.getPortalURL()
	);

	url.searchParams.append('filters', JSON.stringify(filters));

	return fetch(url.pathname + url.search, {
		body: JSON.stringify({}),
		headers: new Headers({
			'Accept': 'application/json',
			'Accept-Language': Liferay.ThemeDisplay.getBCP47LanguageId(),
			'Content-Type': 'application/json',
		}),
		method: 'POST',
	});
}

export default {get, storeFilters};
