/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Colors, MetricName} from '../../../types/global';
import {toUnix} from '../../../utils/date';
import {AssetMetricComplement} from '../../../utils/metrics';
import {FormattedData, formatter} from '../../metrics/utils';
import {Data} from './InteractionsByPage';

export enum InteractionsByPageDataKey {
	Page1 = 'PAGE_1_DATA_KEY',
	Page2 = 'PAGE_2_DATA_KEY',
	Page3 = 'PAGE_3_DATA_KEY',
	AxisX = 'x',
	AxisY = 'y',
}

interface FormatData extends AssetMetricComplement {
	data: Data;
	metricName: MetricName;
}

export function formatInteractionsByPageData({
	data: initialData,
	metricName,
	metricType,
}: FormatData): FormattedData {
	const selectedMetric = initialData.assetAppearsOnHistograms.find(
		({metricName: currentMetricName}) => metricName === currentMetricName
	);

	const page1 = selectedMetric?.appearsOnHistograms?.[0];
	const page2 = selectedMetric?.appearsOnHistograms?.[1];
	const page3 = selectedMetric?.appearsOnHistograms?.[2];

	const data = {
		...(page1 && {
			[InteractionsByPageDataKey.Page1]: {
				color: Colors.Blue,
				format: formatter(metricType),
				title: page1?.pageTitle,
				total: formatter(metricType)(page1?.totalValue ?? 0),
				url: page1?.canonicalUrl,
			},
		}),
		...(page2 && {
			[InteractionsByPageDataKey.Page2]: {
				color: Colors.Red,
				format: formatter(metricType),
				title: page2?.pageTitle,
				total: formatter(metricType)(page2?.totalValue ?? 0),
				url: page2?.canonicalUrl,
			},
		}),
		...(page3 && {
			[InteractionsByPageDataKey.Page3]: {
				color: Colors.Orange,
				format: formatter(metricType),
				title: page3?.pageTitle,
				total: formatter(metricType)(page3?.totalValue ?? 0),
				url: page3?.canonicalUrl,
			},
		}),
		[InteractionsByPageDataKey.AxisX]: {
			title: Liferay.Language.get('x'),
			total: 0,
		},
		[InteractionsByPageDataKey.AxisY]: {
			title: Liferay.Language.get('y'),
			total: 0,
		},
	};

	if (page1?.metrics.length) {
		const page1Data = page1?.metrics.map(({value}) => value);
		const page2Data = page2?.metrics.map(({value}) => value);
		const page3Data = page3?.metrics.map(({value}) => value);

		const axisXData = page1.metrics.map(({valueKey}) => toUnix(valueKey));

		const combinedData = [];

		for (let i = 0; i < axisXData.length; i++) {
			combinedData.push({
				[InteractionsByPageDataKey.AxisX]: axisXData[i],
				[InteractionsByPageDataKey.AxisY]: null,
				[InteractionsByPageDataKey.Page1]: page1Data?.[i] ?? null,
				[InteractionsByPageDataKey.Page2]: page2Data?.[i] ?? null,
				[InteractionsByPageDataKey.Page3]: page3Data?.[i] ?? null,
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
