/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

/**
 * Cold-to-warm categorical ramp for choropleth buckets, sourced from the shared
 * Clay chart color tokens so the map honors the same palette (and Style Book
 * overrides) as the other charts. The tokens are ordered cyan, teal, green,
 * yellow, orange, red to keep a perceptual cold-to-warm gradient across the
 * buckets; bucket counts below the ramp length are mapped onto an evenly spaced
 * subset using the same rounding as `getBlueSchemeColor`, so a 2 or 3 bucket
 * map still spans the full range instead of clustering at one end.
 */
const CATEGORICAL_RAMP: ReadonlyArray<string> = [
	'--chart-color-6',
	'--chart-color-4',
	'--chart-color-9',
	'--chart-color-1',
	'--chart-color-3',
	'--chart-color-7',
];

export function getCategoricalSchemeColor(
	bucketCount: number,
	bucketIndex: number
): string {
	if (bucketCount <= 1) {
		return `var(${CATEGORICAL_RAMP[0]})`;
	}

	const rampIndex = Math.round(
		(bucketIndex * (CATEGORICAL_RAMP.length - 1)) / (bucketCount - 1)
	);

	return `var(${CATEGORICAL_RAMP[rampIndex]})`;
}
