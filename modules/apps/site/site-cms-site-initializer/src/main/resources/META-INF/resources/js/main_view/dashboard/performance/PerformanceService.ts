/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {RangeSelectors} from '@liferay/analytics-reports-js-components-web';

import ApiHelper from '../../../common/services/ApiHelper';
import {
	AssetConsumption,
	ConnectionInfo,
	HistogramMetric,
	MetricType,
	OverviewMetrics,
	PerformanceMetric,
	TopAssets,
} from './types';

const BASE_URL = '/o/analytics-cms-rest/v1.0';

function buildQuery(params: Record<string, unknown>): string {
	const searchParams = new URLSearchParams();

	for (const [key, value] of Object.entries(params)) {
		if (value === undefined || value === null || value === '') {
			continue;
		}

		if (Array.isArray(value)) {
			for (const item of value) {
				searchParams.append(key, String(item));
			}
		}
		else {
			searchParams.append(key, String(value));
		}
	}

	const query = searchParams.toString();

	return query ? `?${query}` : '';
}

async function getOverviewMetrics({
	cmpProjectIds,
	depotEntryIds,
	rangeKey,
}: {
	cmpProjectIds?: string[];
	depotEntryIds?: string[];
	rangeKey: RangeSelectors;
}) {
	return ApiHelper.get<OverviewMetrics>(
		`${BASE_URL}/performance-overview-metric${buildQuery({
			cmpProjectIds,
			depotEntryIds,
			rangeKey,
		})}`
	);
}

async function getConnectionInfo({
	depotEntryGroupId,
}: {
	depotEntryGroupId: number;
}) {
	return ApiHelper.get<ConnectionInfo>(
		`${BASE_URL}/connection-info${buildQuery({depotEntryGroupId})}`
	);
}

async function getHistogramMetric({
	cmpProjectIds,
	depotEntryIds,
	rangeKey,
	selectedMetric,
}: {
	cmpProjectIds?: string[];
	depotEntryIds?: string[];
	rangeKey: RangeSelectors;
	selectedMetric: MetricType;
}) {
	return ApiHelper.get<HistogramMetric>(
		`${BASE_URL}/performance-histogram-metric${buildQuery({
			cmpProjectIds,
			depotEntryIds,
			rangeKey,
			selectedMetric,
		})}`
	);
}

async function getMetric({
	cmpProjectIds,
	depotEntryIds,
	groupBy,
	metricType,
	rangeKey,
}: {
	cmpProjectIds?: string[];
	depotEntryIds?: string[];
	groupBy: 'categories' | 'location';
	metricType: MetricType;
	rangeKey: RangeSelectors;
}) {
	return ApiHelper.get<PerformanceMetric>(
		`${BASE_URL}/performance-metric${buildQuery({
			cmpProjectIds,
			depotEntryIds,
			groupBy,
			metricType,
			rangeKey,
		})}`
	);
}

async function getAssetConsumption({
	categoryId,
	cmpProjectIds,
	depotEntryIds,
	groupBy,
	page,
	pageSize,
	rangeKey,
	structureId,
	tagId,
	vocabularyId,
}: {
	categoryId?: string;
	cmpProjectIds?: string[];
	depotEntryIds?: string[];
	groupBy: 'category' | 'structure' | 'tag' | 'vocabulary';
	page?: number;
	pageSize?: number;
	rangeKey: RangeSelectors;
	structureId?: string;
	tagId?: string;
	vocabularyId?: string;
}) {
	return ApiHelper.get<AssetConsumption>(
		`${BASE_URL}/performance-asset-consumption${buildQuery({
			categoryId,
			cmpProjectIds,
			depotEntryIds,
			groupBy,
			page,
			pageSize,
			rangeKey,
			structureId,
			tagId,
			vocabularyId,
		})}`
	);
}

async function getTopAssets({
	cmpProjectIds,
	depotEntryIds,
	page,
	pageSize,
	rangeKey,
	search,
	sort,
}: {
	cmpProjectIds?: string[];
	depotEntryIds?: string[];
	page?: number;
	pageSize?: number;
	rangeKey: RangeSelectors;
	search?: string;
	sort?: string;
}) {
	return ApiHelper.get<TopAssets>(
		`${BASE_URL}/performance-top-asset${buildQuery({
			cmpProjectIds,
			depotEntryIds,
			page,
			pageSize,
			rangeKey,
			search,
			sort,
		})}`
	);
}

function getMetricExportURL({
	cmpProjectIds,
	depotEntryIds,
	groupBy,
	metricType,
	rangeKey,
}: {
	cmpProjectIds?: string[];
	depotEntryIds?: string[];
	groupBy: 'categories' | 'location';
	metricType: MetricType;
	rangeKey: RangeSelectors;
}) {
	return `${BASE_URL}/performance-metric/export${buildQuery({
		cmpProjectIds,
		depotEntryIds,
		groupBy,
		metricType,
		rangeKey,
	})}`;
}

function getTopAssetsExportURL({
	cmpProjectIds,
	depotEntryIds,
	rangeKey,
	search,
	sort,
}: {
	cmpProjectIds?: string[];
	depotEntryIds?: string[];
	rangeKey: RangeSelectors;
	search?: string;
	sort?: string;
}) {
	return `${BASE_URL}/performance-top-asset/export${buildQuery({
		cmpProjectIds,
		depotEntryIds,
		rangeKey,
		search,
		sort,
	})}`;
}

export default {
	getAssetConsumption,
	getConnectionInfo,
	getHistogramMetric,
	getMetric,
	getMetricExportURL,
	getOverviewMetrics,
	getTopAssets,
	getTopAssetsExportURL,
};
