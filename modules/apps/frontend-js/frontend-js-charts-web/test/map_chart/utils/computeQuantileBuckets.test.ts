/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {MapDatum} from '../../../src/main/resources/META-INF/resources/js/map_chart/types/MapDatum';
import {
	computeQuantileBuckets,
	getEffectiveBucketCount,
} from '../../../src/main/resources/META-INF/resources/js/map_chart/utils/computeQuantileBuckets';

const SINGLE_VALUE_DATA: MapDatum[] = [
	{country: 'FR', label: 'France', value: 10},
	{country: 'DE', label: 'Germany', value: 10},
	{country: 'US', label: 'United States', value: 10},
];

const MULTI_VALUE_DATA: MapDatum[] = [
	{country: 'FR', label: 'France', value: 10},
	{country: 'DE', label: 'Germany', value: 40},
	{country: 'US', label: 'United States', value: 90},
];

describe('getEffectiveBucketCount', () => {
	it('returns zero for an empty data array', () => {
		expect(getEffectiveBucketCount([], 5)).toBe(0);
	});

	it('clamps to the number of distinct values when data has a single distinct value', () => {
		expect(getEffectiveBucketCount(SINGLE_VALUE_DATA, 5)).toBe(1);
	});

	it('clamps to the requested steps when distinct values exceed it', () => {
		expect(getEffectiveBucketCount(MULTI_VALUE_DATA, 2)).toBe(2);
	});

	it('clamps to the distinct value count when steps exceed it', () => {
		expect(getEffectiveBucketCount(MULTI_VALUE_DATA, 6)).toBe(3);
	});
});

describe('computeQuantileBuckets', () => {
	it('returns an empty array for an empty data array', () => {
		const bucketCount = getEffectiveBucketCount([], 5);

		expect(computeQuantileBuckets([], bucketCount)).toEqual([]);
	});

	it('assigns every datum to bucket zero when there is a single distinct value', () => {
		const bucketCount = getEffectiveBucketCount(SINGLE_VALUE_DATA, 5);
		const buckets = computeQuantileBuckets(SINGLE_VALUE_DATA, bucketCount);

		expect(bucketCount).toBe(1);
		expect(buckets).toEqual([0, 0, 0]);
	});

	it('splits a multi-value dataset into the effective bucket count', () => {
		const bucketCount = getEffectiveBucketCount(MULTI_VALUE_DATA, 3);
		const buckets = computeQuantileBuckets(MULTI_VALUE_DATA, bucketCount);

		expect(bucketCount).toBe(3);
		expect(buckets).toEqual([0, 1, 2]);

		buckets.forEach((bucket) => {
			expect(bucket).toBeGreaterThanOrEqual(0);
			expect(bucket).toBeLessThan(bucketCount);
		});
	});
});
