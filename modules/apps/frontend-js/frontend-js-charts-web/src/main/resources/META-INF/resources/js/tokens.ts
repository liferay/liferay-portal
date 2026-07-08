/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export const CHART_FAMILY_ORDER = [
	'yellow',
	'blue',
	'orange',
	'teal',
	'pink',
	'cyan',
	'red',
	'purple',
	'green',
	'indigo',
] as const;

type ChartFamily = (typeof CHART_FAMILY_ORDER)[number];

// Every family resolves from the shared Clay chart color tokens
// (`--chart-color-1` through `--chart-color-10`), following the categorical
// order above, so the palette stays themeable and Style Book overridable. Clay
// defines all of these custom properties on `:root`, so no fallbacks are
// needed. Charts with more than ten series cycle back through the same tokens.

export const CHART_FAMILY_CLAY_PALETTE: Record<ChartFamily, string> = {
	blue: 'var(--chart-color-2)',
	cyan: 'var(--chart-color-6)',
	green: 'var(--chart-color-9)',
	indigo: 'var(--chart-color-10)',
	orange: 'var(--chart-color-3)',
	pink: 'var(--chart-color-5)',
	purple: 'var(--chart-color-8)',
	red: 'var(--chart-color-7)',
	teal: 'var(--chart-color-4)',
	yellow: 'var(--chart-color-1)',
};
