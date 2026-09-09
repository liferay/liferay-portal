/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';

import {
	anchoredScroll,
	coverScale,
	imageTransform,
} from '../../src/main/resources/META-INF/resources/js/imaging/geometry';
import {initialEditState} from '../../src/main/resources/META-INF/resources/js/state/editorReducer';

describe('coverScale', () => {
	it('is neutral without an angle', () => {
		expect(coverScale(1600, 1000, 0)).toBe(1);
	});

	it('grows the image so a rotated frame stays covered', () => {
		expect(coverScale(1000, 1000, 45)).toBeCloseTo(Math.SQRT2, 4);
		expect(coverScale(1000, 1000, -45)).toBeCloseTo(Math.SQRT2, 4);
	});

	it('grows monotonically with the angle', () => {
		const small = coverScale(1600, 1000, 5);
		const large = coverScale(1600, 1000, 20);

		expect(small).toBeGreaterThan(1);
		expect(large).toBeGreaterThan(small);
	});
});

describe('imageTransform', () => {
	it('is undefined when nothing is rotated', () => {
		expect(imageTransform(initialEditState(1600, 1000))).toBeUndefined();
	});

	it('mirrors before the quarter turn', () => {
		const transform = imageTransform({
			...initialEditState(1600, 1000),
			flipHorizontal: true,
			rotation: 90,
		});

		expect(transform).toBe(
			'translate(1000 0) scale(-1 1) translate(1000 0) rotate(90)'
		);
	});

	it('combines the straighten angle with the quarter turns', () => {
		const transform = imageTransform({
			...initialEditState(1600, 1000),
			angle: 8,
			rotation: 90,
		});

		expect(transform).toContain('rotate(8');
		expect(transform).toContain('rotate(90)');
		expect(transform).toContain('scale(');
	});
});

describe('anchoredScroll', () => {
	const padding = 48;

	it('keeps the point under the anchor in place when zooming in', () => {
		const scroll = anchoredScroll({
			anchor: {x: 124, y: 74},
			next: 2,
			padding,
			scroll: {left: 0, top: 0},
			zoom: 1,
		});

		expect(scroll.left).toBe(24 + 200 - 124);
		expect(scroll.top).toBe(24 + 100 - 74);
	});

	it('is the inverse of itself when zooming back out', () => {
		const anchor = {x: 310, y: 180};
		const first = anchoredScroll({
			anchor,
			next: 2,
			padding,
			scroll: {left: 40, top: 20},
			zoom: 1,
		});

		const back = anchoredScroll({
			anchor,
			next: 1,
			padding,
			scroll: first,
			zoom: 2,
		});

		expect(back.left).toBeCloseTo(40);
		expect(back.top).toBeCloseTo(20);
	});

	it('leaves the scroll alone when the zoom does not change', () => {
		const scroll = anchoredScroll({
			anchor: {x: 200, y: 120},
			next: 1.5,
			padding,
			scroll: {left: 90, top: 60},
			zoom: 1.5,
		});

		expect(scroll.left).toBeCloseTo(90);
		expect(scroll.top).toBeCloseTo(60);
	});
});
