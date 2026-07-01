/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {SliceAngles} from '../../../src/main/resources/META-INF/resources/js/pie_chart/types/SliceAngles';
import {getPieChartSlicePathFactory} from '../../../src/main/resources/META-INF/resources/js/pie_chart/utils/getPieChartSlicePathFactory';

const CENTER_X = 50;
const CENTER_Y = 50;
const OUTER_RADIUS = 40;
const INNER_RADIUS = 20;

const CIRCLE_START_ANGLE = -Math.PI / 2;

const FULL_CIRCLE_RADIANS = Math.PI * 2;

function buildSliceAngles({
	precedingTotal,
	total,
	value,
}: {
	precedingTotal: number;
	total: number;
	value: number;
}): SliceAngles {
	const startAngle =
		CIRCLE_START_ANGLE + (precedingTotal / total) * FULL_CIRCLE_RADIANS;
	const sweepAngle = (Math.max(0, value) / total) * FULL_CIRCLE_RADIANS;
	const endAngle = startAngle + sweepAngle;

	return {endAngle, startAngle, sweepAngle};
}

describe('getPieChartSlicePathFactory', () => {
	describe('wedge shaped', () => {
		it('draws a solid full circle for a single full slice', () => {
			const pathFactory = getPieChartSlicePathFactory({
				centerX: CENTER_X,
				centerY: CENTER_Y,
				innerRadius: 0,
				outerRadius: OUTER_RADIUS,
			});

			const d = pathFactory(
				buildSliceAngles({precedingTotal: 0, total: 1, value: 1})
			);

			expect(d.startsWith('M 50 50 L')).toBe(false);
			expect((d.match(/Z/g) ?? []).length).toBe(1);
		});

		it('builds a wedge path for a slice of a multi-slice chart', () => {
			const pathFactory = getPieChartSlicePathFactory({
				centerX: CENTER_X,
				centerY: CENTER_Y,
				innerRadius: 0,
				outerRadius: OUTER_RADIUS,
			});

			const d = pathFactory(
				buildSliceAngles({precedingTotal: 0, total: 2, value: 1})
			);

			expect(d.startsWith('M 50 50 L')).toBe(true);
		});

		it('offsets a slice start by the preceding total', () => {
			const pathFactory = getPieChartSlicePathFactory({
				centerX: CENTER_X,
				centerY: CENTER_Y,
				innerRadius: 0,
				outerRadius: OUTER_RADIUS,
			});

			const d = pathFactory(
				buildSliceAngles({precedingTotal: 1, total: 2, value: 1})
			);

			expect(d).toContain('L 50 90');
			expect(d).not.toContain('L 50 10');
		});
	});

	describe('ring shaped', () => {
		it('draws a full circle with an inner ring for a single full slice', () => {
			const pathFactory = getPieChartSlicePathFactory({
				centerX: CENTER_X,
				centerY: CENTER_Y,
				innerRadius: INNER_RADIUS,
				outerRadius: OUTER_RADIUS,
			});

			const d = pathFactory(
				buildSliceAngles({precedingTotal: 0, total: 1, value: 1})
			);

			expect((d.match(/Z/g) ?? []).length).toBe(2);
			expect(d).toContain('20 20');
		});

		it('builds a ring segment path for a slice of a multi-slice chart', () => {
			const pathFactory = getPieChartSlicePathFactory({
				centerX: CENTER_X,
				centerY: CENTER_Y,
				innerRadius: INNER_RADIUS,
				outerRadius: OUTER_RADIUS,
			});

			const d = pathFactory(
				buildSliceAngles({precedingTotal: 0, total: 2, value: 1})
			);

			expect(d.startsWith('M 50 50 L')).toBe(false);
			expect(d).toContain('L');
		});

		it('offsets a slice start by the preceding total', () => {
			const pathFactory = getPieChartSlicePathFactory({
				centerX: CENTER_X,
				centerY: CENTER_Y,
				innerRadius: INNER_RADIUS,
				outerRadius: OUTER_RADIUS,
			});

			const d = pathFactory(
				buildSliceAngles({precedingTotal: 1, total: 2, value: 1})
			);

			expect(d).toContain('L');
		});
	});
});
