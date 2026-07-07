/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {MapDatum} from '../types/MapDatum';

function getDistinctSortedValues(data: MapDatum[]): number[] {
	const values = data.map((datum) => datum.value).sort((a, b) => a - b);

	return Array.from(new Set(values));
}

function getBucketBreakpoints(
	distinctValues: number[],
	bucketCount: number
): number[] {
	return Array.from({length: bucketCount - 1}, (_, breakpointIndex) => {
		const rank = breakpointIndex + 1;
		const valueIndex =
			Math.ceil((rank / bucketCount) * distinctValues.length) - 1;

		return distinctValues[Math.min(valueIndex, distinctValues.length - 1)];
	});
}

function getBucketIndex(value: number, breakpoints: number[]): number {
	return breakpoints.filter((breakpoint) => value > breakpoint).length;
}

export function getEffectiveBucketCount(
	data: MapDatum[],
	steps: number
): number {
	if (!data.length) {
		return 0;
	}

	const distinctValues = getDistinctSortedValues(data);

	return Math.max(1, Math.min(steps, distinctValues.length));
}

export function computeQuantileBuckets(
	data: MapDatum[],
	bucketCount: number
): number[] {
	if (bucketCount === 0) {
		return [];
	}

	if (bucketCount === 1) {
		return data.map(() => 0);
	}

	const distinctValues = getDistinctSortedValues(data);
	const breakpoints = getBucketBreakpoints(distinctValues, bucketCount);

	return data.map((datum) => getBucketIndex(datum.value, breakpoints));
}
