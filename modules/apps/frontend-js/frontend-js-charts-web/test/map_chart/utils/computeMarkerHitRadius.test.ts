/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {computeMarkerHitRadius} from '../../../src/main/resources/META-INF/resources/js/map_chart/utils/computeMarkerHitRadius';

describe('computeMarkerHitRadius', () => {
	it('falls back to the given radius when the rendered width is unknown', () => {
		expect(computeMarkerHitRadius(5, undefined, 558)).toBe(5);
	});

	it('falls back to the given radius when the rendered width is zero', () => {
		expect(computeMarkerHitRadius(5, 0, 558)).toBe(5);
	});

	it('converts a fixed on-screen pixel target into viewBox units', () => {
		expect(computeMarkerHitRadius(2, 300, 558)).toBeCloseTo(
			(12 / 300) * 558
		);
	});

	it('shrinks the hit radius in viewBox units as the viewBox narrows on zoom', () => {
		const worldHitRadius = computeMarkerHitRadius(5, 300, 558);
		const zoomedHitRadius = computeMarkerHitRadius(5, 300, 200);

		expect(zoomedHitRadius).toBeLessThan(worldHitRadius);
	});
});
