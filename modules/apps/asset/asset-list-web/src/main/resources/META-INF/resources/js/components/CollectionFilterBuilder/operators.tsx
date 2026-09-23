/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import type {FilterOperator, FilterProperty} from './types';

/**
 * Tags, categories, and keywords apply to every item type. On the server,
 * they all go into an AssetEntryQuery object, which has one field for each
 * operator and quantifier pair. Each filter sets its pair's field, so a
 * second filter with the same pair overwrites the first instead of narrowing
 * the results.
 *
 * ConditionBuilder reads these names to stop a combination being used twice.
 */
export const ASSET_FIELDS: FilterProperty[] = [
	{
		label: Liferay.Language.get('tags'),
		name: 'assetTags',
		type: 'asset-tags',
	},
	{
		label: Liferay.Language.get('categories'),
		name: 'assetCategories',
		type: 'asset-categories',
	},
	{
		label: Liferay.Language.get('keywords'),
		name: 'keywords',
		type: 'text',
	},
];

export const ASSET_FIELD_NAMES = new Set(ASSET_FIELDS.map(({name}) => name));

// Booleans use an implicit `eq` operator (auto-set when the field is selected
// in ConditionBuilder) and render their value as a single Is True / Is False
// picker via DefaultValueInput. No explicit operator picker is needed.

const BOOLEAN_OPERATORS: FilterOperator[] = [];

const COMPARISON_OPERATORS: FilterOperator[] = [
	{label: Liferay.Language.get('equals'), value: 'eq'},
	{label: Liferay.Language.get('not-equals'), value: 'not-eq'},
	{label: Liferay.Language.get('greater-than'), value: 'gt'},
	{label: Liferay.Language.get('greater-than-or-equals'), value: 'ge'},
	{label: Liferay.Language.get('less-than'), value: 'lt'},
	{label: Liferay.Language.get('less-than-or-equals'), value: 'le'},
	{label: Liferay.Language.get('between'), value: 'between'},
];

const DEFAULT_OPERATORS: FilterOperator[] = [
	{label: Liferay.Language.get('contains'), value: 'contains'},
	{label: Liferay.Language.get('does-not-contain'), value: 'not-contains'},
];

const EQUALITY_OPERATORS: FilterOperator[] = [
	{label: Liferay.Language.get('equals'), value: 'eq'},
	{label: Liferay.Language.get('not-equals'), value: 'not-eq'},
];

export function getCollectionOperators(
	property: FilterProperty
): FilterOperator[] {
	switch (property.type) {
		case 'boolean':
			return BOOLEAN_OPERATORS;
		case 'date':
		case 'date-time':
		case 'decimal':
		case 'integer':
		case 'numeric':
			return COMPARISON_OPERATORS;
		case 'keyword':
			return EQUALITY_OPERATORS;
		case 'picklist':
		case 'text':
		default:
			return DEFAULT_OPERATORS;
	}
}

const QUANTIFIER_OPTIONS: FilterOperator[] = [
	{label: Liferay.Language.get('any-of-the-following'), value: 'any'},
	{label: Liferay.Language.get('all-of-the-following'), value: 'all'},
];

export function getCollectionQuantifierOptions(
	property: FilterProperty
): FilterOperator[] | null {
	switch (property.type) {
		case 'asset-categories':
		case 'asset-tags':
		case 'picklist':
		case 'text':
			return QUANTIFIER_OPTIONS;
		default:
			return null;
	}
}

export const RELATIVE_DATE_OPTIONS: FilterOperator[] = [
	{label: Liferay.Language.get('now'), value: 'now'},
	{label: Liferay.Language.get('past-day'), value: 'past-day'},
	{label: Liferay.Language.get('past-24-hours'), value: 'past-24-hours'},
	{label: Liferay.Language.get('past-week'), value: 'past-week'},
	{label: Liferay.Language.get('past-month'), value: 'past-month'},
	{label: Liferay.Language.get('past-year'), value: 'past-year'},
	{label: Liferay.Language.get('next-month'), value: 'next-month'},
	{label: Liferay.Language.get('last-year'), value: 'last-year'},
];

export const RELATIVE_DATE_VALUES = RELATIVE_DATE_OPTIONS.map(
	(option) => option.value
);
