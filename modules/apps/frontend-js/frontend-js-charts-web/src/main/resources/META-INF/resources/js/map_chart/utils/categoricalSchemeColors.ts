/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

/**
 * Cold-to-warm categorical ramp. The ramp holds one entry per supported
 * bucket count (2-6) so every count maps to a unique color; bucket counts
 * below the ramp length are mapped onto an evenly spaced subset using the
 * same rounding used by `getBlueSchemeColor`, so a 2 or 3 bucket map still
 * spans the full cyan-to-red range instead of clustering at one end. `teal`
 * sits between `cyan` and `green` to keep the cyan-to-red endpoints intact
 * while giving the 6 bucket case a sixth distinct hue.
 */
const CATEGORICAL_RAMP: ReadonlyArray<string> = [
	'--cyan-l3',
	'--teal-l2',
	'--green-l4',
	'--yellow-l2',
	'--orange-l3',
	'--red-l2',
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
