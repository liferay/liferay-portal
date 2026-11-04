/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {IBaseFilterState} from '@liferay/frontend-data-set-web';

export type QuickFilterUpdates = Record<
	string,
	IBaseFilterState['selectedData']
>;

type QuickFilterSelectedData = {
	exclude?: boolean;
	from?: {day: number; month: number; year: number} | null;
	selectedItems?: Array<{value: number | string}>;
	to?: {day: number; month: number; year: number} | null;
};

function isSameDateBound(
	actualBound?: QuickFilterSelectedData['from'],
	expectedBound?: QuickFilterSelectedData['from']
) {
	if (!actualBound || !expectedBound) {
		return !actualBound && !expectedBound;
	}

	return (
		actualBound.day === expectedBound.day &&
		actualBound.month === expectedBound.month &&
		actualBound.year === expectedBound.year
	);
}

function matchesSelectedData(
	actualData?: IBaseFilterState['selectedData'],
	expectedData?: IBaseFilterState['selectedData']
) {
	if (!actualData || !expectedData) {
		return false;
	}

	const actual = actualData as QuickFilterSelectedData;
	const expected = expectedData as QuickFilterSelectedData;

	if (Boolean(actual.exclude) !== Boolean(expected.exclude)) {
		return false;
	}

	if (expected.selectedItems) {
		const actualValues =
			actual.selectedItems?.map(({value}) => value) ?? [];
		const expectedValues = expected.selectedItems.map(({value}) => value);

		return (
			actualValues.length === expectedValues.length &&
			expectedValues.every((value) => actualValues.includes(value))
		);
	}

	return (
		isSameDateBound(actual.from, expected.from) &&
		isSameDateBound(actual.to, expected.to)
	);
}

function clearedFilter(filter: IBaseFilterState): IBaseFilterState {
	return {
		...filter,
		active: false,
		odataFilterString: undefined,
		selectedData: undefined,
	};
}

export function getActiveQuickFilter<T extends string>(
	filters: readonly IBaseFilterState[] = [],
	quickFilterUpdates: Record<T, () => QuickFilterUpdates>,
	ignoredFilterIds: string[] = []
): T | null {
	const activeFilters = filters.filter(
		({active, id}) => active && !ignoredFilterIds.includes(id)
	);

	if (!activeFilters.length) {
		return null;
	}

	for (const quickFilterType of Object.keys(quickFilterUpdates) as T[]) {
		const filterUpdates = quickFilterUpdates[quickFilterType]();

		const filterIds = Object.keys(filterUpdates);

		if (
			activeFilters.length === filterIds.length &&
			activeFilters.every((filter) =>
				matchesSelectedData(
					filter.selectedData,
					filterUpdates[filter.id]
				)
			)
		) {
			return quickFilterType;
		}
	}

	return null;
}

export function applyQuickFilterUpdates(
	filters: readonly IBaseFilterState[],
	filterUpdates: QuickFilterUpdates
): IBaseFilterState[] {
	return filters.map((filter) => {
		const selectedData = filterUpdates[filter.id];

		if (selectedData) {
			return {
				...filter,
				active: true,
				selectedData,
			};
		}

		return clearedFilter(filter);
	});
}
