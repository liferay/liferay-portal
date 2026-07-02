/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {toPercent} from '../../../src/main/resources/META-INF/resources/js/pie_chart/utils/percent';

describe('toPercent', () => {
	it('formats a fractional share to one decimal place', () => {
		expect(toPercent(33, 100)).toBe('33.0');
	});

	it('rounds a repeating decimal to one decimal place', () => {
		expect(toPercent(1, 3)).toBe('33.3');
	});

	it('treats a NaN value as zero', () => {
		expect(toPercent(NaN, 100)).toBe('0.0');
	});

	it('treats an Infinity value as zero', () => {
		expect(toPercent(Infinity, 100)).toBe('0.0');
	});

	it('treats a NaN total as zero', () => {
		expect(toPercent(50, NaN)).toBe('0.0');
	});

	it('treats a non-positive total as zero', () => {
		expect(toPercent(50, 0)).toBe('0.0');
	});
});
