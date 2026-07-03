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
	blue: 'var(--primary-l0, light-dark(#5791ff, #0f62ff))',
	cyan: 'var(--cyan-l3, light-dark(#6cf, #006699))',
	green: 'var(--green-l4, light-dark(#9de963, #22430a))',
	indigo: 'var(--indigo-l3, light-dark(#b2baff, #0019fa))',
	orange: 'var(--orange-l3, light-dark(#ffa166, #8f3700))',
	pink: 'var(--pink-l2, light-dark(#ff80c8, #fa008e))',
	purple: 'var(--purple-l1, light-dark(#bf66ff, #b54dff))',
	red: 'var(--red-l2, light-dark(#f66, #f50000))',
	teal: 'var(--teal-l2, light-dark(#42d7be, #1b7e6e))',
	yellow: 'var(--yellow-l2, light-dark(#ffd666, #b88600))',
};

export const CHART_FAMILY_CLAY_PALETTE_EXTENDED: ReadonlyArray<string> = [
	'var(--blue-d2, light-dark(#005fcc, #94c4ff))',
	'var(--yellow-d2, light-dark(#cc9600, #ffdc7a))',
	'var(--red-d2, light-dark(#b30000, #ffb2b2))',
	'var(--green-d2, light-dark(#2e590d, #9ae85f))',
	'var(--purple-d2, light-dark(#9500ff, #e1b8ff))',
	'var(--teal-d2, light-dark(#125449, #7ce4d2))',
	'var(--pink-d2, light-dark(#b30065, #ffb2de))',
	'var(--orange-d2, light-dark(#993b00, #ffa770))',
	'var(--cyan-d2, light-dark(#005580, #70cfff))',
	'var(--indigo-d2, light-dark(#1a30ff, #bdc3ff))',
];
