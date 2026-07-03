/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {RangeSelectors} from '@liferay/analytics-reports-js-components-web';

import ApiHelper from '../../../../src/main/resources/META-INF/resources/js/common/services/ApiHelper';
import PerformanceService from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/performance/PerformanceService';

describe('PerformanceService', () => {
	afterEach(() => {
		jest.clearAllMocks();
	});

	it('builds the overview metric request omitting empty depotEntryIds', async () => {
		const get = jest
			.spyOn(ApiHelper, 'get')
			.mockResolvedValue({data: null, error: null});

		await PerformanceService.getOverviewMetrics({
			rangeKey: RangeSelectors.Last7Days,
		});

		expect(get).toHaveBeenCalledWith(
			'/o/analytics-cms-rest/v1.0/performance-overview-metric?rangeKey=7'
		);
	});

	it('repeats depotEntryIds as separate query parameters', async () => {
		const get = jest
			.spyOn(ApiHelper, 'get')
			.mockResolvedValue({data: null, error: null});

		await PerformanceService.getMetric({
			depotEntryIds: ['1', '2'],
			groupBy: 'location',
			metricType: 'viewsMetric',
			rangeKey: RangeSelectors.Last30Days,
		});

		expect(get).toHaveBeenCalledWith(
			'/o/analytics-cms-rest/v1.0/performance-metric?depotEntryIds=1&depotEntryIds=2&groupBy=location&metricType=viewsMetric&rangeKey=30'
		);
	});

	it('builds the metric export URL', () => {
		expect(
			PerformanceService.getMetricExportURL({
				groupBy: 'categories',
				metricType: 'viewsMetric',
				rangeKey: RangeSelectors.Last7Days,
			})
		).toBe(
			'/o/analytics-cms-rest/v1.0/performance-metric/export?groupBy=categories&metricType=viewsMetric&rangeKey=7'
		);
	});

	it('builds the top assets export URL with sort', () => {
		expect(
			PerformanceService.getTopAssetsExportURL({
				rangeKey: RangeSelectors.Last7Days,
				sort: 'engagementMetric:desc',
			})
		).toBe(
			'/o/analytics-cms-rest/v1.0/performance-top-asset/export?rangeKey=7&sort=engagementMetric%3Adesc'
		);
	});
});
