/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {computePrecedingTotals} from '../../../src/main/resources/META-INF/resources/js/pie_chart/utils/computePrecedingTotals';

describe('computePrecedingTotals', () => {
	it('returns an empty array for no data', () => {
		expect(computePrecedingTotals([])).toEqual([]);
	});

	it('returns a single zero for one item', () => {
		const precedingTotals = computePrecedingTotals([
			{label: 'A', value: 5},
		]);

		expect(precedingTotals).toEqual([0]);
	});

	it('returns the running total preceding each item', () => {
		const precedingTotals = computePrecedingTotals([
			{label: 'A', value: 1},
			{label: 'B', value: 2},
			{label: 'C', value: 3},
		]);

		expect(precedingTotals).toEqual([0, 1, 3]);
	});

	it('clamps negative values to zero without decreasing later totals', () => {
		const precedingTotals = computePrecedingTotals([
			{label: 'A', value: 1},
			{label: 'B', value: -10},
			{label: 'C', value: 2},
		]);

		expect(precedingTotals).toEqual([0, 1, 1]);
	});
});
