/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';

import {analyticsCloudStubTest} from '../../../fixtures/analyticsCloudStubTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {SITE_CMS_SPACE_NAME} from '../../setup/site-cms-site/constants/space';
import {cmsPagesTest} from './fixtures/cmsPagesTest';

const ASSET_CONSUMPTION = {
	performanceAssetConsumptionItems: [
		{count: 150, key: 'basic-web-content', title: 'Basic Web Content'},
		{count: 50, key: 'blog', title: 'Blog'},
	],
	performanceAssetConsumptionItemsCount: 2,
	totalCount: 200,
};

const CATEGORIES_METRIC = {
	metricType: 'viewsMetric',
	metrics: [
		{previousValue: 500, value: 800, valueKey: 'Technology'},
		{previousValue: 600, value: 700, valueKey: 'Sales'},
	],
};

const LOCATION_METRIC = {
	metricType: 'viewsMetric',
	metrics: [
		{previousValue: 700, value: 900, valueKey: 'United States'},
		{previousValue: 400, value: 600, valueKey: 'Spain'},
	],
};

const OVERVIEW_METRICS = {
	downloadsMetric: {
		metricType: 'downloadsMetric',
		previousValue: 40,
		trend: {classification: 'POSITIVE', percentage: 30},
		value: 52,
	},
	impressionsMetric: {
		metricType: 'impressionsMetric',
		previousValue: 762,
		trend: {classification: 'POSITIVE', percentage: 25.2},
		value: 954,
	},
	readsMetric: {
		metricType: 'readsMetric',
		previousValue: 80,
		trend: {classification: 'NEGATIVE', percentage: -25},
		value: 60,
	},
	viewsMetric: {
		metricType: 'viewsMetric',
		previousValue: 500,
		trend: {classification: 'NEUTRAL', percentage: 0},
		value: 500,
	},
};

async function mockAnalyticsEndpoints(page: Page) {
	await page.route(
		'**/o/analytics-cms-rest/v1.0/performance-asset-consumption*',
		async (route) => {
			await route.fulfill({
				body: JSON.stringify(ASSET_CONSUMPTION),
				contentType: 'application/json',
			});
		}
	);

	await page.route(
		'**/o/analytics-cms-rest/v1.0/performance-metric*',
		async (route) => {
			const url = new URL(route.request().url());

			await route.fulfill({
				body: JSON.stringify(
					url.searchParams.get('groupBy') === 'location'
						? LOCATION_METRIC
						: CATEGORIES_METRIC
				),
				contentType: 'application/json',
			});
		}
	);

	await page.route(
		'**/o/analytics-cms-rest/v1.0/performance-overview-metric*',
		async (route) => {
			await route.fulfill({
				body: JSON.stringify(OVERVIEW_METRICS),
				contentType: 'application/json',
			});
		}
	);
}

const test = mergeTests(
	analyticsCloudStubTest,
	cmsPagesTest,
	featureFlagsTest({
		'LPD-58315': {enabled: true},
	}),
	loginTest()
);

test(
	'renders the metrics returned by the analytics endpoints',
	{tag: '@LPD-98182'},
	async ({page, performanceDashboardPage}) => {
		await mockAnalyticsEndpoints(page);

		await performanceDashboardPage.goto();

		const impressionsCard =
			performanceDashboardPage.getMetricCard('Impressions');

		await expect(impressionsCard).toContainText('954');
		await expect(impressionsCard).toContainText('25.2%');

		await expect(
			performanceDashboardPage.viewsByLocationCard
		).toContainText('United States');

		await expect(
			performanceDashboardPage.viewsByCategorizationCard
		).toContainText('Technology');

		const assetConsumptionCard =
			performanceDashboardPage.assetConsumptionCard;

		await expect(assetConsumptionCard).toContainText('Basic Web Content');
		await expect(assetConsumptionCard).toContainText('75.00%');
		await expect(assetConsumptionCard).toContainText('25.00%');
	}
);

test(
	'sends the selected range and space in the request params',
	{tag: '@LPD-98182'},
	async ({page, performanceDashboardPage}) => {
		await mockAnalyticsEndpoints(page);

		await performanceDashboardPage.goto();

		const rangeRequest = page.waitForRequest(
			(request) =>
				request
					.url()
					.includes(
						'/o/analytics-cms-rest/v1.0/performance-overview-metric'
					) && request.url().includes('rangeKey=30')
		);

		await performanceDashboardPage.selectRange('30');

		await rangeRequest;

		const spaceRequest = page.waitForRequest(
			(request) =>
				request
					.url()
					.includes(
						'/o/analytics-cms-rest/v1.0/performance-overview-metric'
					) && request.url().includes('depotEntryIds=')
		);

		await performanceDashboardPage.selectSpace(SITE_CMS_SPACE_NAME);

		await spaceRequest;
	}
);
