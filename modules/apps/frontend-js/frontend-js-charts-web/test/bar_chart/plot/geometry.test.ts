/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	getStackedBarChartGeometry,
	stackedSegmentPath,
} from '../../../src/main/resources/META-INF/resources/js/bar_chart/plot/geometry';

describe('stackedSegmentPath', () => {
	it('draws a plain rectangle when neither side is rounded', () => {
		const path = stackedSegmentPath(0, 0, 10, 8, 4, false, false);

		expect(path).not.toContain('A');
		expect(path).toBe('M 0 0 H 10 V 8 H 0 V 0 Z');
	});

	it('rounds all four corners for a lone segment', () => {
		const path = stackedSegmentPath(0, 0, 10, 8, 4, true, true);

		expect(path.match(/A /g)).toHaveLength(4);
	});

	it('rounds only the left corners when roundRight is false', () => {
		const path = stackedSegmentPath(0, 0, 10, 8, 4, true, false);

		expect(path.match(/A /g)).toHaveLength(2);
	});

	it('caps the corner radius at half the shorter side', () => {

		// A 4px tall segment cannot honor a 10px radius; the cap is height / 2.

		const path = stackedSegmentPath(0, 0, 40, 4, 10, true, true);

		expect(path).toContain('A 2 2');
	});
});

describe('getStackedBarChartGeometry', () => {
	const DATA = [
		{label: 'A', value: 1},
		{label: 'B', value: 3},
	];

	const OPTIONS = {
		data: DATA,
		height: 48,
		rounded: false,
		size: 'inline' as const,
		width: 204,
	};

	it('produces one segment per datum', () => {
		const {segments} = getStackedBarChartGeometry(OPTIONS);

		expect(segments).toHaveLength(DATA.length);
	});

	it('rounds only the outer corners of the row', () => {
		const {segments} = getStackedBarChartGeometry(OPTIONS);

		expect(segments[0].roundLeft).toBe(true);
		expect(segments[0].roundRight).toBe(false);
		expect(segments[1].roundLeft).toBe(false);
		expect(segments[1].roundRight).toBe(true);
	});

	it('sizes each segment to its share of the total', () => {
		const {segments} = getStackedBarChartGeometry(OPTIONS);

		// B (value 3) is three times as wide as A (value 1).

		expect(segments[1].width).toBeCloseTo(segments[0].width * 3);
	});

	it('lays the segments left to right without overlap', () => {
		const {segments} = getStackedBarChartGeometry(OPTIONS);

		expect(segments[1].x).toBeGreaterThan(
			segments[0].x + segments[0].width
		);
	});
});
