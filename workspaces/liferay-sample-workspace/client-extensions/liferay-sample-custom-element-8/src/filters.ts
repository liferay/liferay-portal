/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

// The filters the "Delegated Filters" data set declares, repeated here by
// hand.

export interface FilterOption {
	label: string;
	value: string;
}

export interface FilterDefinition {
	entityFieldType: 'collection-integer' | 'string';
	id: string;
	label: string;

	/**
	 * Whether the filter keeps more than one option at a time. A single
	 * selection filter replaces what is picked rather than adding to it.
	 */
	multiple: boolean;
	options: Array<FilterOption>;
}

export type Selections = Readonly<Record<string, ReadonlyArray<string>>>;

export const MANUAL_FILTER_ID = 'manual';

export interface FilterState {
	expression: string;
	manual: boolean;
	selections: Selections;
}

export const EMPTY_FILTER_STATE: FilterState = {
	expression: '',
	manual: false,
	selections: {},
};

export const FILTERS: Array<FilterDefinition> = [
	{
		entityFieldType: 'string',
		id: 'color',
		label: 'Color',
		multiple: true,
		options: [
			{label: 'Blue', value: 'Blue'},
			{label: 'Green', value: 'Green'},
			{label: 'Red', value: 'Red'},
			{label: 'Yellow', value: 'Yellow'},
		],
	},
	{
		entityFieldType: 'string',
		id: 'size',
		label: 'Size',
		multiple: false,
		options: [
			{label: 'Tiny', value: 'Tiny'},
			{label: 'Small', value: 'Small'},
			{label: 'Medium', value: 'Medium'},
			{label: 'Large', value: 'Large'},
			{label: 'Huge', value: 'Huge'},
			{label: 'Gargantuan', value: 'Gargantuan'},
		],
	},
	{
		entityFieldType: 'collection-integer',
		id: 'status',
		label: 'Status',
		multiple: true,
		options: [
			{label: 'Approved', value: '0'},
			{label: 'Pending', value: '1'},
			{label: 'Draft', value: '2'},
		],
	},
];

/**
 * The OData the data set would have produced for the same selection, so that
 * taking the filtering over does not change what the request means.
 */
export function getOdataFilterString(
	{entityFieldType, id, multiple}: FilterDefinition,
	values: ReadonlyArray<string>
): string {
	if (!values.length) {
		return '';
	}

	const literals = values.map((value) =>
		entityFieldType === 'collection-integer'
			? value
			: `'${value.replace(/'/g, "''")}'`
	);

	if (entityFieldType === 'collection-integer') {
		return `${id}/any(x:${literals
			.map((literal) => `(x eq ${literal})`)
			.join(' or ')})`;
	}

	if (values.length === 1 && !multiple) {
		return `${id} eq ${literals[0]}`;
	}

	return `${id} in (${literals.join(', ')})`;
}

export function getSelectedValues(
	selections: Selections,
	filterId: string
): ReadonlyArray<string> {
	return selections[filterId] ?? [];
}

/**
 * Adds or removes one option, replacing the selection outright when the
 * filter keeps a single value.
 */
export function toggleOption(
	selections: Selections,
	{id, multiple}: FilterDefinition,
	value: string
): Selections {
	const values = getSelectedValues(selections, id);

	if (values.includes(value)) {
		return {...selections, [id]: values.filter((each) => each !== value)};
	}

	return {...selections, [id]: multiple ? [...values, value] : [value]};
}

/**
 * The filter state the data set restored, less anything this element cannot
 * draw.
 */
export function getValidFilterState(connectionState: unknown): FilterState {
	if (!connectionState || typeof connectionState !== 'object') {
		return EMPTY_FILTER_STATE;
	}

	const {expression, selections} = connectionState as Record<string, unknown>;

	if (typeof expression === 'string') {
		return {expression, manual: true, selections: {}};
	}

	if (!selections || typeof selections !== 'object') {
		return EMPTY_FILTER_STATE;
	}

	const validSelections: Record<string, ReadonlyArray<string>> = {};

	Object.entries(selections as Record<string, unknown>).forEach(
		([filterId, values]) => {
			const filterDefinition = FILTERS.find(
				(filterDefinition) => filterDefinition.id === filterId
			);

			if (!filterDefinition || !Array.isArray(values)) {
				return;
			}

			const validValues = values.filter((value): value is string =>
				filterDefinition.options.some(
					(option) => option.value === value
				)
			);

			if (validValues.length) {
				validSelections[filterId] = filterDefinition.multiple
					? validValues
					: validValues.slice(0, 1);
			}
		}
	);

	return {expression: '', manual: false, selections: validSelections};
}

export function getConnectionState(filterState: FilterState): unknown {
	return filterState.manual
		? {expression: filterState.expression}
		: {selections: filterState.selections};
}

/**
 * The expressions the given filter state sends to the data set, one per
 * filter in play. A manually typed expression is the whole filter, so it
 * travels alone.
 */
export function getFilters(
	filterState: FilterState
): Array<{id: string; odataFilterString: string}> {
	if (filterState.manual) {
		return filterState.expression
			? [
					{
						id: MANUAL_FILTER_ID,
						odataFilterString: filterState.expression,
					},
				]
			: [];
	}

	return FILTERS.map((filterDefinition) => ({
		id: filterDefinition.id,
		odataFilterString: getOdataFilterString(
			filterDefinition,
			getSelectedValues(filterState.selections, filterDefinition.id)
		),
	}));
}

export function getOptionLabels(
	{options}: FilterDefinition,
	values: ReadonlyArray<string>
): Array<string> {
	return values.map(
		(value) =>
			options.find((option) => option.value === value)?.label ?? value
	);
}
