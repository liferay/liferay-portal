/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

const BLUE_RAMP: ReadonlyArray<string> = [
	'--chart-blue-l4',
	'--chart-blue-l3',
	'--chart-blue-l2',
	'--chart-blue-l1',
	'--chart-color-2',
	'--chart-blue-d1',
	'--chart-blue-d2',
	'--chart-blue-d3',
	'--chart-blue-d4',
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
