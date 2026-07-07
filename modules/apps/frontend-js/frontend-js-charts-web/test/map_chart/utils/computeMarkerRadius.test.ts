/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {computeMarkerRadius} from '../../../src/main/resources/META-INF/resources/js/map_chart/utils/computeMarkerRadius';

describe('computeMarkerRadius', () => {
	it('keeps the base radius when the bounding box matches the world width', () => {
		expect(computeMarkerRadius(6, 558, 558)).toBe(6);
	});

	it('shrinks the radius proportionally to a narrower bounding box', () => {
		expect(computeMarkerRadius(6, 400, 558)).toBeCloseTo(6 * (400 / 558));
	});

	it('clamps the radius to a 0.45 floor for a very narrow bounding box', () => {
		expect(computeMarkerRadius(6, 100, 558)).toBeCloseTo(6 * 0.45);
	});

	it('caps the radius at the base for a wider-than-world bounding box', () => {
		expect(computeMarkerRadius(6, 1116, 558)).toBe(6);
	});

	it('falls back to the base radius when the world width is zero', () => {
		expect(computeMarkerRadius(6, 200, 0)).toBe(6);
	});
});
