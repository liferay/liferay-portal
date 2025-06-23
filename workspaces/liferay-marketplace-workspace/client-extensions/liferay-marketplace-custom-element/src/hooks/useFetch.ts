/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import useSWR from 'swr';

type APIParametersOptions = {
	aggregationTerms?: string;
	customParams?: {[key: string]: unknown};
	fields?: string;
	filter?: string;
	nestedFields?: string;
	nestedFieldsDepth?: number | string;
	page?: number | string;
	pageSize?: number | string;
	sort?: string;
};

function getPageParameter(
	parameters: APIParametersOptions = {},
	baseURL?: string
) {
	const getBaseSearchParams = (resource?: string) => {
		if (resource && resource.includes('?')) {
			return resource.slice(resource.indexOf('?'));
		}
	};

	const searchParams = new URLSearchParams(getBaseSearchParams(baseURL));

	if (parameters.customParams) {
		parameters = {
			...parameters,
			...parameters.customParams,
		};

		delete parameters.customParams;
	}

	for (const key in parameters) {
		const value = (parameters as any)[key] as string | number | undefined;

		if (value) {
			searchParams.set(key, value.toString());
		}
	}

	return searchParams.toString();
}

const getBaseURL = (url: string | null, options?: APIParametersOptions) => {
	if (!url) {
		return null;
	}

	const searchParams = getPageParameter(options, url);

	let baseURL = url;

	if (url.includes('?')) {
		baseURL = url.slice(0, url.indexOf('?'));
	}

	if (searchParams.length) {
		baseURL += `?${searchParams}`;
	}

	return baseURL;
};

export function useFetch<Data = any, Error = any>(
	url: string | null,
	fetchParameters?: Record<string, any>
) {
	const {params} = fetchParameters ?? {};

	const {data, error, isLoading, isValidating, mutate} = useSWR<Data, Error>(
		() => getBaseURL(url, params)
	);

	return {
		data,
		error,
		isValidating,
		loading: isLoading,
		mutate,
		revalidate: () => mutate((response) => response, {revalidate: true}),
	};
}
