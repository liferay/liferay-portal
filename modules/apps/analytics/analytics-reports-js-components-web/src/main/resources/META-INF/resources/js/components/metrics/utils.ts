/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {utcFormat} from 'd3';

import {Colors} from '../../types/global';
import {round, toThousands} from '../../utils/math';
import {AssetMetricComplement} from '../../utils/metrics';
import {RangeSelectors} from '../RangeSelectorsDropdown';

export type ChartData = {
	color?: Colors;
	format?: (value: any) => any;
	title: string;
	total: string | number;
	url?: string;
};

export type FormattedData = {
	combinedData: {[key in string]: number | string | null}[];
	data: {
		[key in string]: ChartData;
	};
	intervals: (number | null)[];
};

export function getFillOpacity<T>(id: T, hoveredItemId: T | null) {
	return hoveredItemId === id || !hoveredItemId ? 1 : 0.2;
}

export function formatter(type: AssetMetricComplement['metricType']) {
	if (type === 'percentage') {
		return (value: number) => `${round(value * 100)}%`;
	}

	if (type === 'number') {
		return (value: number) => `${toThousands(value)}`;
	}

	if (type === 'long') {
		return (value: number) => value.toFixed(1);
	}

	return (value: number) => value;
}

export function formatXAxisDate(
	dateKey: number,
	rangeSelector: RangeSelectors
) {
	let formatter = utcFormat('%b %-d');

	if (rangeSelector === RangeSelectors.Last24Hours) {
		formatter = utcFormat('%-I %p');
	}

	return formatter(dateKey as unknown as Date);
}

const tickIntervals = {
	[RangeSelectors.Last24Hours]: 4,
	[RangeSelectors.Yesterday]: 4,
	[RangeSelectors.Last7Days]: 7,
	[RangeSelectors.Last28Days]: 4,
	[RangeSelectors.Last30Days]: 4,
	[RangeSelectors.Last90Days]: 6,
	[RangeSelectors.CustomRange]: 0,
};

export function calculateTickIntervals(
	ticks: number[],
	rangeSelector: RangeSelectors
) {
	const interval = Math.floor(ticks.length / tickIntervals[rangeSelector]);

	return ticks.filter((_, index) => index % interval === 0);
}
