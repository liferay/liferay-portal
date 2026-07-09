/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {getBlueSchemeColor} from '../../../src/main/resources/META-INF/resources/js/map_chart/utils/blueSchemeColors';

describe('getBlueSchemeColor', () => {
	it('returns the lightest ramp step when there is a single step', () => {
		expect(getBlueSchemeColor(1, 0)).toBe('var(--chart-blue-l4)');
	});

	it('returns the lightest ramp step for the lowest bucket', () => {
		expect(getBlueSchemeColor(5, 0)).toBe('var(--chart-blue-l4)');
	});

	it('returns the darkest ramp step for the highest bucket', () => {
		expect(getBlueSchemeColor(5, 4)).toBe('var(--chart-blue-d4)');
	});

	it('spreads intermediate buckets evenly across the ramp', () => {
		expect(getBlueSchemeColor(5, 2)).toBe('var(--chart-color-2)');
	});

	it('resolves the darkest step even when the requested steps exceed the ramp length', () => {
		expect(getBlueSchemeColor(9, 8)).toBe('var(--chart-blue-d4)');
	});
});
