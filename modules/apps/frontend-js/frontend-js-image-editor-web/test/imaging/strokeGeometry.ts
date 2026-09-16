/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';

import {
	pointsBounds,
	pointsToPath,
	seededRandom,
	simplifyPoints,
	sketchyEllipsePath,
	sketchyRectPath,
} from '../../src/main/resources/META-INF/resources/js/imaging/strokeGeometry';

describe('the hand-drawn style', () => {
	it('wobbles the same way for the same seed, and differently for another', () => {
		const first = sketchyRectPath(10, 20, 300, 100, 42);

		expect(sketchyRectPath(10, 20, 300, 100, 42)).toBe(first);
		expect(sketchyRectPath(10, 20, 300, 100, 43)).not.toBe(first);

		expect(sketchyEllipsePath(100, 100, 50, 30, 7)).toBe(
			sketchyEllipsePath(100, 100, 50, 30, 7)
		);
	});

	it('closes both shapes into a smooth loop', () => {
		expect(sketchyRectPath(0, 0, 100, 100, 1)).toMatch(/^M.* C.* Z$/);
		expect(sketchyEllipsePath(50, 50, 50, 50, 1)).toMatch(/^M.* C.* Z$/);
	});

	it('stays within a hand-sized jitter of the ideal corners', () => {
		const path = sketchyRectPath(0, 0, 100, 50, 9);

		const [x, y] = path.slice(1).split(' ', 2).map(Number);

		expect(Math.abs(x)).toBeLessThanOrEqual(1.5);
		expect(Math.abs(y)).toBeLessThanOrEqual(1.5);
	});

	it('draws a seeded random in the unit interval', () => {
		const random = seededRandom(2026);

		for (let step = 0; step < 20; step++) {
			const value = random();

			expect(value).toBeGreaterThanOrEqual(0);
			expect(value).toBeLessThan(1);
		}
	});
});

describe('pointsToPath', () => {
	it('draws straight segments when not smoothing', () => {
		expect(pointsToPath([0, 0, 10, 0, 10, 10], false)).toBe(
			'M0 0 L10 0 L10 10'
		);
	});

	it('turns a lone point into a dot the linecap can round', () => {
		expect(pointsToPath([5, 5], true)).toBe('M5 5 l0.01 0');
	});

	it('draws nothing from no points', () => {
		expect(pointsToPath([], true)).toBe('');
	});
});

describe('simplifyPoints', () => {
	it('drops the points that lie within the tolerance of a straight run', () => {
		expect(
			simplifyPoints([0, 0, 10, 0.5, 20, 0, 30, 0.2, 40, 0], 1)
		).toEqual([0, 0, 40, 0]);
	});

	it('keeps the corners', () => {
		expect(simplifyPoints([0, 0, 10, 0, 20, 0, 20, 10, 20, 20], 1)).toEqual(
			[0, 0, 20, 0, 20, 20]
		);
	});

	it('returns a copy of anything too short to simplify', () => {
		const points = [1, 2, 3, 4];

		expect(simplifyPoints(points, 1)).toEqual(points);
		expect(simplifyPoints(points, 1)).not.toBe(points);
	});
});

describe('pointsBounds', () => {
	it('boxes the points', () => {
		expect(pointsBounds([10, 20, -5, 40, 30, 0])).toEqual({
			height: 40,
			width: 35,
			x: -5,
			y: 0,
		});
	});
});
