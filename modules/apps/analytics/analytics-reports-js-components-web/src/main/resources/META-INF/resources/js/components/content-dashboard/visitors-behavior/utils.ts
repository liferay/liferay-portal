/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Colors, MetricName} from '../../../types/global';
import {toUnix} from '../../../utils/date';
import {AssetMetricComplement} from '../../../utils/metrics';
import {FormattedData, formatter} from '../../metrics/utils';
import {Data, Histogram, PublishedVersionData} from './VisitorsBehavior';

export enum VisitorsBehaviorDataKey {
	Metric = 'METRIC_DATA_KEY',
	PublishedVersionData = 'PUBLISHED_VERSION_DATA_KEY',
	PublishedVersionValue = 'PUBLISHED_VERSION_VALUE_KEY',
	AxisX = 'x',
	AxisY = 'y',
}

interface FormatData extends AssetMetricComplement {
	data: Data;
	metricName: MetricName;
	publishedVersionData: PublishedVersionData | null;
}

export function getSelectedHistogram(data: Data, metricName: MetricName) {
	return data.histograms.find(
		({metricName: currentMetricName}) => metricName === currentMetricName
	);
}

export function formatVisitorsBehaviorData({
	data: initialData,
	metricName,
	metricType,
	publishedVersionData,
	visitorsBehaviorTooltipTitle,
}: FormatData): FormattedData {
	const selectedHistogram = getSelectedHistogram(initialData, metricName);

	const data = {
		[VisitorsBehaviorDataKey.Metric]: {
			color: Colors.Blue,
			format: formatter(metricType),
			title: visitorsBehaviorTooltipTitle,
			total: formatter(metricType)(selectedHistogram?.totalValue ?? 0),
		},
		[VisitorsBehaviorDataKey.PublishedVersionData]: {
			color: Colors.Black,
			format: formatter('long'),
			title: Liferay.Language.get('published-version'),
			total: formatter('number')(publishedVersionData?.total ?? 0),
		},
		[VisitorsBehaviorDataKey.PublishedVersionValue]: {
			title: Liferay.Language.get('published-version'),
			total: 0,
		},
		[VisitorsBehaviorDataKey.AxisX]: {
			title: Liferay.Language.get('x'),
			total: 0,
		},
		[VisitorsBehaviorDataKey.AxisY]: {
			title: Liferay.Language.get('y'),
			total: 0,
		},
	};

	if (selectedHistogram?.metrics.length) {
		const axisXData = selectedHistogram.metrics.map(({valueKey}) =>
			toUnix(valueKey)
		);

		const combinedData = [];

		const metricData = selectedHistogram.metrics.map(({value}) => value);

		for (let i = 0; i < axisXData.length; i++) {
			combinedData.push({
				[VisitorsBehaviorDataKey.AxisX]: axisXData[i],
				[VisitorsBehaviorDataKey.AxisY]: null,
				[VisitorsBehaviorDataKey.Metric]: metricData[i],
				[VisitorsBehaviorDataKey.PublishedVersionData]:
					publishedVersionData?.histogram?.[i] ? 0 : null,
				[VisitorsBehaviorDataKey.PublishedVersionValue]:
					publishedVersionData?.histogram?.[i] ?? null,
			});
		}

		return {
			combinedData,
			data,
			intervals: axisXData,
		};
	}

	return {
		combinedData: [],
		data,
		intervals: [],
	};
}

export function sortPublishedDates(dates: {date: string; version: string}[]) {
	return dates.sort((a, b) => {
		const versionA = parseFloat(a.version);
		const versionB = parseFloat(b.version);

		return versionA - versionB;
	});
}

export function mapPublishedDatesToHistogram(
	dates: {date: string; version: string}[],
	histogram: Histogram
) {
	const resultArray = Array(histogram.metrics.length).fill(null);

	const dateMap = new Map();

	dates.forEach(({date, version}) => {
		dateMap.set(date, version);
	});

	histogram.metrics.forEach((metric, index) => {
		const version = dateMap.get(metric.valueKey);

		if (version) {
			resultArray[index] = version;
		}
	});

	return resultArray;
}

export function formatPublishedDate(dateString: string) {
	const date = new Date(dateString);

	return new Date(
		Date.UTC(
			date.getUTCFullYear(),
			date.getUTCMonth(),
			date.getUTCDate(),
			date.getUTCHours(),
			0,
			0,
			0
		)
	)
		.toISOString()
		.slice(0, 16);
}
