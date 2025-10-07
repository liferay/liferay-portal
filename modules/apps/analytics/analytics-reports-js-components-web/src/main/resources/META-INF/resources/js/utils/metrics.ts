/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ColorType} from '@clayui/core/lib/typography/Text';

import {AssetTypes, MetricName, MetricType} from '../types/global';

type AssetMetrics = {
	[key in AssetTypes]: MetricName[];
};

export const assetMetrics: AssetMetrics = {
	[AssetTypes.Blog]: [MetricName.Views, MetricName.Comments],
	[AssetTypes.Content]: [MetricName.Impressions, MetricName.Views],
	[AssetTypes.Document]: [
		MetricName.Downloads,
		MetricName.Impressions,
		MetricName.Comments,
	],
	[AssetTypes.Files]: [
		MetricName.Impressions,
		MetricName.Views,
		MetricName.Downloads,
	],
	[AssetTypes.Undefined]: [],
	[AssetTypes.WebContent]: [MetricName.Views],
};

export const metricNameByType = {
	[MetricType.Comments]: MetricName.Comments,
	[MetricType.Downloads]: MetricName.Downloads,
	[MetricType.Impressions]: MetricName.Impressions,
	[MetricType.Undefined]: MetricName.Undefined,
	[MetricType.Views]: MetricName.Views,
};

export type AssetMetricComplement = {
	interactionsByPageTooltipTitle: string;
	metricType: 'percentage' | 'number' | 'long' | 'undefined';
	visitorsBehaviorTooltipTitle: string;
};

export const assetContent: {
	[key in MetricName]: AssetMetricComplement;
} = {
	[MetricName.Comments]: {
		interactionsByPageTooltipTitle: Liferay.Language.get(
			'comments-by-top-pages'
		),
		metricType: 'number',
		visitorsBehaviorTooltipTitle: Liferay.Language.get('total-comments'),
	},
	[MetricName.Downloads]: {
		interactionsByPageTooltipTitle: Liferay.Language.get(
			'downloads-by-top-pages'
		),
		metricType: 'number',
		visitorsBehaviorTooltipTitle: Liferay.Language.get('total-downloads'),
	},
	[MetricName.Impressions]: {
		interactionsByPageTooltipTitle: Liferay.Language.get(
			'impressions-by-top-pages'
		),
		metricType: 'number',
		visitorsBehaviorTooltipTitle: Liferay.Language.get('total-impressions'),
	},
	[MetricName.Views]: {
		interactionsByPageTooltipTitle:
			Liferay.Language.get('views-by-top-pages'),
		metricType: 'number',
		visitorsBehaviorTooltipTitle: Liferay.Language.get('total-views'),
	},
	[MetricName.Undefined]: {
		interactionsByPageTooltipTitle: 'undefined',
		metricType: 'undefined',
		visitorsBehaviorTooltipTitle: 'undefined',
	},
};

export enum TrendClassification {
	Negative = 'NEGATIVE',
	Neutral = 'NEUTRAL',
	Positive = 'POSITIVE',
}

export function getStatsColor(trendClassification: TrendClassification) {
	const map = {
		[TrendClassification.Negative]: 'danger',
		[TrendClassification.Neutral]: 'secondary',
		[TrendClassification.Positive]: 'success',
	};

	return map[trendClassification] as ColorType;
}

export function getStatsIcon(trendPercentage: number) {
	if (trendPercentage > 0) {
		return 'caret-top';
	}
	else if (trendPercentage < 0) {
		return 'caret-bottom';
	}

	return '';
}
