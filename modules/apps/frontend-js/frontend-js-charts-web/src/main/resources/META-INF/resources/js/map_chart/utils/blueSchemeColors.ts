/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

const BLUE_RAMP: ReadonlyArray<string> = [
	'--blue-l4',
	'--blue-l3',
	'--blue-l2',
	'--blue-l1',
	'--blue',
	'--blue-d1',
	'--blue-d2',
	'--blue-d3',
	'--blue-d4',
];

export function getBlueSchemeColor(steps: number, bucketIndex: number): string {
	if (steps <= 1) {
		return `var(${BLUE_RAMP[0]})`;
	}

	const rampIndex = Math.round(
		(bucketIndex * (BLUE_RAMP.length - 1)) / (steps - 1)
	);

	return `var(${BLUE_RAMP[rampIndex]})`;
}
