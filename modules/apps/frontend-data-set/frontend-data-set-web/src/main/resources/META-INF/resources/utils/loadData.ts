/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fetch} from 'frontend-js-web';

import {DEFAULT_FETCH_HEADERS} from '../constants';
import {TSort} from './types';

function createOdataFilter(filters: Array<string>): string {
	return filters.map((filter: string) => `(${filter})`).join(' and ');
}

function getFiltersString(
	odataFiltersStrings: Array<any>,
	providedFilters: string | null
): string {
	let filtersString = '';

	if (providedFilters) {
		filtersString += providedFilters;
	}

	if (providedFilters && odataFiltersStrings.length) {
		filtersString += ' and ';
	}

	if (odataFiltersStrings.length) {
		filtersString += createOdataFilter(odataFiltersStrings);
	}

	return filtersString;
}

export async function loadData({
	additionalAPIURLParameters,
	apiURL,
	currentURL,
	delta,
	odataFiltersStrings = [],
	page = 1,
	searchParam,
	sorts,
}: {
	additionalAPIURLParameters?: string;
	apiURL: string;
	currentURL?: string;
	delta?: number;
	odataFiltersStrings?: Array<string>;
	page?: number;
	searchParam?: string;
	sorts?: TSort[];
}) {
	const fullUrl = apiURL.startsWith('/')
		? Liferay.ThemeDisplay.getPortalURL() +
			Liferay.ThemeDisplay.getPathContext() +
			apiURL
		: apiURL;

	const url = new URL(fullUrl);

	if (currentURL) {
		url.searchParams.set('currentURL', currentURL);
	}

	const providedFilters = url.searchParams.get('filter');

	url.searchParams.delete('filter');

	if (providedFilters || odataFiltersStrings.length) {
		url.searchParams.append(
			'filter',
			getFiltersString(odataFiltersStrings, providedFilters)
		);
	}

	if (Liferay.ThemeDisplay.isImpersonated()) {
		url.searchParams.append('doAsUserId', Liferay.ThemeDisplay.getUserId());
	}

	if (page) {
		url.searchParams.append('page', page.toString());
	}

	if (delta) {
		url.searchParams.append('pageSize', delta.toString());
	}

	if (searchParam) {
		url.searchParams.append('search', searchParam);
	}

	if (sorts && sorts.length) {
		const updatedSorts = sorts.map((sort: TSort) => {
			const key = sort.key?.includes(',')
				? sort.key.split(',')[0]
				: sort.key;

			return {
				...sort,
				key,
			};
		});

		url.searchParams.set(
			'sort',
			updatedSorts
				.map((item) => `${item.key}:${item.direction}`)
				.join(',')
		);
	}

	if (additionalAPIURLParameters) {
		const additionalAPIURLParametersArray =
			additionalAPIURLParameters.split('&');

		additionalAPIURLParametersArray.forEach((parameter) => {
			const [key, value] = parameter.split('=');

			const existingFilter = url.searchParams.get('filter');
			const existingNestedFields = url.searchParams.get('nestedFields');
			const existingNestedFieldsDepth =
				url.searchParams.get('nestedFieldsDepth');
			const existingSort = url.searchParams.get('sort');

			if (key === 'filter' && existingFilter) {
				url.searchParams.set(
					'filter',
					`(${existingFilter}) and (${value})`
				);
			}
			else if (key === 'sort' && existingSort) {
				const newSortParams: Array<string> = [];

				const existingSortArray = existingSort.split(',');

				const existingSortParamFields = existingSortArray.map(
					(sort) => sort.split(':')[0]
				);

				const additionalAPIURLParametersSortValueArray =
					value.split(',');

				additionalAPIURLParametersSortValueArray.forEach(
					(additionalAPIURLParametersSortValueItem) => {
						if (
							!existingSortParamFields.includes(
								additionalAPIURLParametersSortValueItem.split(
									':'
								)[0]
							)
						) {
							newSortParams.push(
								additionalAPIURLParametersSortValueItem
							);
						}
					}
				);

				url.searchParams.set(
					'sort',
					existingSortArray.concat(newSortParams).join(',')
				);
			}
			else if (key === 'nestedFields' && existingNestedFields) {
				const existingNestedFieldsArray =
					existingNestedFields.split(',');
				const additionalAPIURLParametersNestedFieldsValueArray =
					value.split(',');

				const newNestedFields = [...existingNestedFieldsArray];

				additionalAPIURLParametersNestedFieldsValueArray.forEach(
					(additionalAPIURLParametersNestedFieldsItem) => {
						if (
							!newNestedFields.includes(
								additionalAPIURLParametersNestedFieldsItem
							)
						) {
							newNestedFields.push(
								additionalAPIURLParametersNestedFieldsItem
							);
						}
					}
				);

				url.searchParams.set('nestedFields', newNestedFields.join(','));
			}
			else if (
				key === 'nestedFieldsDepth' &&
				existingNestedFieldsDepth
			) {
				const existingNestedFieldsDepthValue = Number(
					existingNestedFieldsDepth
				);
				const additionalAPIURLParametersNestedFieldsDepthValue =
					Number(value);

				if (
					!isNaN(existingNestedFieldsDepthValue) &&
					!isNaN(additionalAPIURLParametersNestedFieldsDepthValue)
				) {
					url.searchParams.set(
						key,
						existingNestedFieldsDepthValue >=
							additionalAPIURLParametersNestedFieldsDepthValue
							? existingNestedFieldsDepth
							: value
					);
				}
				else if (
					!isNaN(additionalAPIURLParametersNestedFieldsDepthValue)
				) {
					url.searchParams.append(key, existingNestedFieldsDepth);
				}
				else {
					url.searchParams.append(key, value);
				}
			}
			else {
				url.searchParams.append(key, value);
			}
		});
	}

	const response = await fetch(url.toString(), {
		headers: DEFAULT_FETCH_HEADERS,
		method: 'GET',
	});

	const responseJSON = await response.json();

	return {
		data: responseJSON,
		ok: response.ok,
		status: response.status,
	};
}
