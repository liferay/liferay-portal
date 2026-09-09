/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';

import {anchoredScroll} from '../../src/main/resources/META-INF/resources/js/imaging/geometry';

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
