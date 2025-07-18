/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export enum TrendClassification {
	Negative = 'NEGATIVE',
	Neutral = 'NEUTRAL',
	Positive = 'POSITIVE',
}

export function getStatsColor(
	trendClassification: TrendClassification
): 'danger' | 'success' | 'secondary' {
	if (trendClassification === TrendClassification.Negative) {
		return 'danger';
	}
	else if (trendClassification === TrendClassification.Positive) {
		return 'success';
	}

	return 'secondary';
}

export function getStatsIcon(trendPercentage: number) {
	if (trendPercentage > 0) {
		return 'caret-top';
	}
	else if (trendPercentage < 0) {
		return 'caret-bottom';
	}

	return null;
}
