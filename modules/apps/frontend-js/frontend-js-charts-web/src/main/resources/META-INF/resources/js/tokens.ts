/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export const CHART_FAMILY_ORDER = [
	'blue',
	'yellow',
	'red',
	'green',
	'purple',
	'teal',
	'pink',
	'orange',
	'cyan',
	'indigo',
] as const;

type ChartFamily = (typeof CHART_FAMILY_ORDER)[number];

export const CHART_FAMILY_CLAY_PALETTE: Record<ChartFamily, string> = {
	blue: '--primary-l0',
	cyan: '--cyan-l3',
	green: '--green-l4',
	indigo: '--indigo-l3',
	orange: '--orange-l3',
	pink: '--pink-l2',
	purple: '--purple-l1',
	red: '--red-l2',
	teal: '--teal-l2',
	yellow: '--yellow-l2',
};

export const CHART_FAMILY_CLAY_PALETTE_EXTENDED: ReadonlyArray<string> = [
	'--blue-d2',
	'--yellow-d2',
	'--red-d2',
	'--green-d2',
	'--purple-d2',
	'--teal-d2',
	'--pink-d2',
	'--orange-d2',
	'--cyan-d2',
	'--indigo-d2',
];
