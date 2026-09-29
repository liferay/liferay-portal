/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ElementVariation} from './elementVariationsReducer';

type IssueValue = 'missing-audience' | 'missing-page-element';

export type Filter = {exclude: boolean} & (
	| {type: 'audience'; values: string[]}
	| {type: 'issue'; values: IssueValue[]}
	| {type: 'status'; values: Array<'disabled' | 'enabled'>}
	| {type: 'type'; values: Array<'hide-element' | 'html' | 'javascript'>}
);

export type FilterType = Filter['type'];

export const FILTER_TYPES: FilterType[] = [
	'audience',
	'issue',
	'status',
	'type',
];

const PREVIEW_VALUES_COUNT = 3;

type Option = {label: string; value: string};

export function hasValueInAnyLanguage(
	localizedValue: Record<string, string>
): boolean {
	return Object.values(localizedValue).some(Boolean);
}

export function getElementVariationIssues(
	elementVariation: ElementVariation,
	editableElementOptions: Option[]
): IssueValue[] {
	const issues: IssueValue[] = [];

	if (!elementVariation.audienceEntryERCs.length) {
		issues.push('missing-audience');
	}

	if (
		!editableElementOptions.some(
			(editableElementOption) =>
				editableElementOption.value === elementVariation.targetElement
		)
	) {
		issues.push('missing-page-element');
	}

	return issues;
}

export function getFilterLabel(type: FilterType): string {
	if (type === 'audience') {
		return Liferay.Language.get('audience');
	}

	if (type === 'issue') {
		return Liferay.Language.get('issue');
	}

	if (type === 'status') {
		return Liferay.Language.get('status');
	}

	return Liferay.Language.get('type');
}

export function getFilterOptions(
	type: FilterType,
	audiences: Option[]
): Option[] {
	if (type === 'audience') {
		return audiences;
	}

	if (type === 'issue') {
		return [
			{
				label: Liferay.Language.get('missing-audience'),
				value: 'missing-audience',
			},
			{
				label: Liferay.Language.get('missing-page-element'),
				value: 'missing-page-element',
			},
		];
	}

	if (type === 'status') {
		return [
			{label: Liferay.Language.get('enabled'), value: 'enabled'},
			{label: Liferay.Language.get('disabled'), value: 'disabled'},
		];
	}

	return [
		{label: Liferay.Language.get('html'), value: 'html'},
		{label: Liferay.Language.get('javascript'), value: 'javascript'},
		{label: Liferay.Language.get('hide-element'), value: 'hide-element'},
	];
}

export function getFilterText(
	filter: Filter,
	audiences: Option[]
): {hiddenCount: number; label: string} {
	const options = getFilterOptions(filter.type, audiences);

	const filterValues: string[] = filter.values;

	const labels = filterValues
		.map((value) => options.find((option) => option.value === value)?.label)
		.filter(Boolean);

	const values = labels.slice(0, PREVIEW_VALUES_COUNT).join(', ');

	return {
		hiddenCount: Math.max(labels.length - PREVIEW_VALUES_COUNT, 0),
		label: filter.exclude
			? `(${Liferay.Language.get('exclude')}) ${values}`
			: values,
	};
}

function getVariationValues({
	editableElementOptions,
	elementVariation,
	type,
}: {
	editableElementOptions: Option[];
	elementVariation: ElementVariation;
	type: FilterType;
}): string[] {
	if (type === 'audience') {
		return elementVariation.audienceEntryERCs;
	}

	if (type === 'issue') {
		return getElementVariationIssues(
			elementVariation,
			editableElementOptions
		);
	}

	if (type === 'status') {
		return [elementVariation.active ? 'enabled' : 'disabled'];
	}

	const values = [];

	if (hasValueInAnyLanguage(elementVariation.html)) {
		values.push('html');
	}

	if (hasValueInAnyLanguage(elementVariation.js)) {
		values.push('javascript');
	}

	if (elementVariation.hide) {
		values.push('hide-element');
	}

	return values;
}

function getVariationText({
	audiences,
	editableElementOptions,
	elementVariation,
}: {
	audiences: Option[];
	editableElementOptions: Option[];
	elementVariation: ElementVariation;
}): string {
	const elementLabel =
		editableElementOptions.find(
			(option) => option.value === elementVariation.targetElement
		)?.label ?? elementVariation.targetElement;

	const audienceLabels = elementVariation.audienceEntryERCs.map(
		(audienceEntryERC) =>
			audiences.find((audience) => audience.value === audienceEntryERC)
				?.label
	);

	const typeOptions = getFilterOptions('type', audiences);

	const typeLabels = getVariationValues({
		editableElementOptions,
		elementVariation,
		type: 'type',
	}).map(
		(value) => typeOptions.find((option) => option.value === value)?.label
	);

	const statusLabel = elementVariation.active
		? ''
		: Liferay.Language.get('disabled');

	const labels = [
		elementVariation.name,
		elementLabel,
		...audienceLabels,
		...typeLabels,
		statusLabel,
	];

	return labels.filter(Boolean).join(' ').toLowerCase();
}

export function getFilteredVariations({
	audiences,
	editableElementOptions,
	elementVariations,
	filters,
	searchTerm,
}: {
	audiences: Option[];
	editableElementOptions: Option[];
	elementVariations: ElementVariation[];
	filters: Filter[];
	searchTerm: string;
}): ElementVariation[] {
	const term = searchTerm.trim().toLowerCase();

	return elementVariations.filter((elementVariation) => {
		const matchesFilters = filters.every((filter) => {
			const values = getVariationValues({
				editableElementOptions,
				elementVariation,
				type: filter.type,
			});

			const matches = filter.values.some((value) =>
				values.includes(value)
			);

			return filter.exclude ? !matches : matches;
		});

		if (!matchesFilters || !term) {
			return matchesFilters;
		}

		return getVariationText({
			audiences,
			editableElementOptions,
			elementVariation,
		}).includes(term);
	});
}
