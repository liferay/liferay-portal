/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {getCategoricalSchemeColor} from '../../../src/main/resources/META-INF/resources/js/map_chart/utils/categoricalSchemeColors';

describe('getCategoricalSchemeColor', () => {
	it('returns the first ramp color when there is a single bucket', () => {
		expect(getCategoricalSchemeColor(1, 0)).toBe('var(--chart-color-6)');
	});

	it('returns the first ramp color for the lowest bucket', () => {
		expect(getCategoricalSchemeColor(6, 0)).toBe('var(--chart-color-6)');
	});

	it('returns the last ramp color for the highest bucket', () => {
		expect(getCategoricalSchemeColor(6, 5)).toBe('var(--chart-color-7)');
	});

	it('spans the full ramp even when bucket count is below the ramp length', () => {
		expect(getCategoricalSchemeColor(2, 0)).toBe('var(--chart-color-6)');
		expect(getCategoricalSchemeColor(2, 1)).toBe('var(--chart-color-7)');
	});
});
