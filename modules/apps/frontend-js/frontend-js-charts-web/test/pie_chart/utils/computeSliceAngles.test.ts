/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {computeSliceAngles} from '../../../src/main/resources/META-INF/resources/js/pie_chart/utils/computeSliceAngles';

describe('computeSliceAngles', () => {
	it('starts the first slice at the top of the circle', () => {
		const angles = computeSliceAngles({
			precedingTotal: 0,
			total: 2,
			value: 1,
		});

		expect(angles.startAngle).toBeCloseTo(-Math.PI / 2);
		expect(angles.sweepAngle).toBeCloseTo(Math.PI);
		expect(angles.endAngle).toBeCloseTo(Math.PI / 2);
	});

	it('offsets a later slice by its preceding total', () => {
		const angles = computeSliceAngles({
			precedingTotal: 1,
			total: 2,
			value: 1,
		});

		expect(angles.startAngle).toBeCloseTo(Math.PI / 2);
		expect(angles.endAngle).toBeCloseTo((3 * Math.PI) / 2);
	});

	it('treats a negative value as a zero sweep', () => {
		const angles = computeSliceAngles({
			precedingTotal: 0,
			total: 1,
			value: -5,
		});

		expect(angles.sweepAngle).toBe(0);
	});
});
