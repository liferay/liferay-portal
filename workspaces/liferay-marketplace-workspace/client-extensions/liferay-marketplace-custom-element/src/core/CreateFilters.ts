/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FilterVariables, RendererFields} from '../schema/filters';
import SearchBuilder from './SearchBuilder';

type Filter = {
	[key: string]: string | number | string[] | number[];
};

type Value = string | number | boolean | null;

export default class CreateFilters {
	static createFilter({
		appliedFilter,
		defaultFilter,
		filterSchema,
	}: FilterVariables) {
		const _filter = defaultFilter ? [defaultFilter] : [];

		for (const key in appliedFilter) {
			let searchCondition = '';
			const rawValue = appliedFilter[key];

			if (!rawValue) {
				continue;
			}

			const schema = filterSchema.fields.find(
				({name}) => key === name
			) as RendererFields;

			const normalizeValue = (val: any) =>
				typeof val === 'object' && val !== null ? val.value : val;

			let value: any;
			if (Array.isArray(rawValue)) {
				value = rawValue.map(normalizeValue);
			}
			else {
				value = normalizeValue(rawValue);
			}

			const isFilterChanged =
				typeof value === 'string' &&
				(value.includes('false') || value.includes('No'));

			const removeQuoteMark =
				schema?.removeQuoteMark ||
				schema?.type === 'number' ||
				isFilterChanged;

			const customOperator = schema?.operator;

			if (customOperator && SearchBuilder[customOperator]) {
				const [filterKey] = key.split('|');
				const formattedKey = filterKey.replace('$', '');

				if (
					customOperator === 'lambda' ||
					customOperator === 'lambdaContains'
				) {
					const builderFn =
						customOperator === 'lambda'
							? SearchBuilder.lambda
							: SearchBuilder.lambdaContains;

					searchCondition = Array.isArray(value)
						? `(${value
								.map((filter) => builderFn(filterKey, filter))
								.join(' or ')})`
						: builderFn(filterKey, value);
				}
				else {
					const getOptionalSearchCondition = () => {
						if (schema?.optionalOperator === 'ne') {
							if (isFilterChanged) {
								return `not (${SearchBuilder[
									schema.optionalOperator
								](formattedKey, null)})`;
							}

							if (
								typeof value === 'string' &&
								value.includes('true')
							) {
								return SearchBuilder[schema.optionalOperator](
									formattedKey,
									null
								);
							}
						}

						return SearchBuilder[customOperator](
							formattedKey,
							value
						);
					};

					searchCondition = getOptionalSearchCondition();
				}
			}
			else {
				if (schema?.type === 'date-range' && Array.isArray(value)) {
					const [start, end] = (value as string[])[0].split(' - ');

					searchCondition = new SearchBuilder()
						.gt(key, `${start}T00:00:00Z`)
						.and()
						.lt(key, `${end}T23:59:59Z`)
						.build();
				}
				else {
					if (Array.isArray(value)) {
						searchCondition = SearchBuilder.in(key, value);
					}
					else {
						searchCondition = SearchBuilder.eq(key, value);
					}
				}
			}

			_filter.push(
				removeQuoteMark
					? searchCondition.replaceAll(`'`, '')
					: searchCondition
			);
		}

		return _filter.join(' and ');
	}

	static formatValuesToString(values: Value[]) {
		if (values) {
			return values
				.map((value) => `${value}`)
				.join(',')
				.trim();
		}

		return '';
	}

	static removeEmptyFilter(filter: Filter) {
		const cleanedFilter: Filter = {};

		for (const key in filter) {
			const value = filter[key];

			if (value === null) {
				continue;
			}

			if (typeof value === 'string' && value.trim() === '') {
				continue;
			}

			if (
				Array.isArray(value) &&
				value.some(
					(item: any) =>
						typeof item === 'object' && item?.value === ''
				)
			) {
				continue;
			}

			cleanedFilter[key] = value;
		}

		return cleanedFilter;
	}
}
