/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

type ShouldIgnoreParamFn = (value: string | string[]) => boolean;

function formatQueryParam(
	param: string,
	value?: string | string[],
	shouldIgnoreParam?: ShouldIgnoreParamFn
) {
	if (!value?.length || shouldIgnoreParam?.(value)) {
		return;
	}

	if (Array.isArray(value)) {
		return `${encodeURIComponent(param)}=${value.join(',')}`;
	}

	return `${encodeURIComponent(param)}=${encodeURIComponent(value)}`;
}

function buildQueryString(
	params: Record<string, string | string[]>,
	options?: {
		shouldIgnoreParam?: ShouldIgnoreParamFn;
	}
) {
	const queryParams = Object.keys(params).sort();

	const queryString = queryParams
		.map((key) =>
			formatQueryParam(key, params[key], options?.shouldIgnoreParam)
		)
		.filter(Boolean)
		.join('&');

	return `?${queryString}`;
}

export {buildQueryString};
