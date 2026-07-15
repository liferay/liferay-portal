/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {TrendClassification} from '@liferay/analytics-reports-js-components-web';

import type {AdditionalProps} from '../../props_transformer/AssetsFDSPropsTransformer';

export type DashboardAdditionalProps = Pick<
	AdditionalProps,
	| 'autocompleteURL'
	| 'breadcrumbProps'
	| 'candidateAssetLibraries'
	| 'cmsGroupId'
	| 'collaboratorURLs'
	| 'contentViewURL'
	| 'fileMimeTypeCssClasses'
	| 'fileMimeTypeIcons'
	| 'objectDefinitionCssClasses'
	| 'objectDefinitionIcons'
> & {
	commentsProps: {
		addCommentURL: string;
		deleteCommentURL: string;
		editCommentURL: string;
		editorConfig: unknown;
		getCommentsURL: string;
	};
};

export type MetricType =
	| 'downloadsMetric'
	| 'impressionsMetric'
	| 'readsMetric'
	| 'viewsMetric';

export type Trend = {
	classification: TrendClassification;
	percentage: number;
};

export type OverviewMetric = {
	metricType: MetricType;
	previousValue: number;
	trend: Trend;
	value: number;
};

export type OverviewMetrics = {
	downloadsMetric: OverviewMetric;
	impressionsMetric: OverviewMetric;
	readsMetric: OverviewMetric;
	viewsMetric: OverviewMetric;
};

export type MetricItem = {
	previousValue: number;
	value: number;
	valueKey: string;
};

export type PerformanceMetric = {
	metricType: MetricType;
	metrics: MetricItem[];
};

export type AssetConsumptionItem = {
	count: number;
	key: string;
	title: string;
};

export type AssetConsumption = {
	performanceAssetConsumptionItems: AssetConsumptionItem[];
	performanceAssetConsumptionItemsCount: number;
	totalCount: number;
};

export type TopAssetItem = {
	downloads: number;
	engagement: number;
	impressions: number;
	mimeType: string;
	title: string;
	trend: Trend;
	views: number;
};

export type TopAssets = {
	lastPage: number;
	page: number;
	pageSize: number;
	performanceTopAssetItems: TopAssetItem[];
	totalCount: number;
};
