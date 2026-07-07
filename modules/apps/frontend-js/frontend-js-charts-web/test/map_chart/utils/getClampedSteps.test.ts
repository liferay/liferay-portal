/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {getClampedSteps} from '../../../src/main/resources/META-INF/resources/js/map_chart/utils/getClampedSteps';

describe('getClampedSteps', () => {
	it('falls back to the default when steps is NaN', () => {
		expect(getClampedSteps(NaN)).toBe(5);
	});

	it('falls back to the default when steps is Infinity', () => {
		expect(getClampedSteps(Infinity)).toBe(5);
	});

	it('falls back to the default when steps is negative Infinity', () => {
		expect(getClampedSteps(-Infinity)).toBe(5);
	});

	it('clamps a finite value below the minimum', () => {
		expect(getClampedSteps(1)).toBe(2);
	});

	it('clamps a finite value above the maximum', () => {
		expect(getClampedSteps(100)).toBe(6);
	});

	it('passes through a finite value within range', () => {
		expect(getClampedSteps(4)).toBe(4);
	});
});
