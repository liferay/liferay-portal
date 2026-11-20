/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {isolatedChannelTest} from '../../../fixtures/isolatedChannelTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginAnalyticsCloudTest} from '../../../fixtures/loginAnalyticsCloudTest';
import {loginTest} from '../../../fixtures/loginTest';
import getRandomString from '../../../utils/getRandomString';
import {syncAnalyticsCloudViaAPI} from '../../analytics-settings-web/main/utils/analytics-settings';
import {blogsPagesTest} from '../../blogs-web/main/fixtures/blogsPagesTest';
import {contentDashboardPagesTest} from '../../content-dashboard-web/main/fixtures/contentDashboardPagesTest';
import {
	createIndividuals,
	generateIndividual,
} from '../../osb-faro-web/main/utils/individuals';

export const test = mergeTests(
	apiHelpersTest,
	blogsPagesTest,
	contentDashboardPagesTest,
	dataApiHelpersTest,
	isolatedChannelTest,
	isolatedSiteTest,
	loginAnalyticsCloudTest(),
	loginTest()
);

type Metric = {
	expectedValue: number;
	metricName: string;
};

async function expectMatchMetricValues(metrics: Metric[], page: Page) {
	for (const {expectedValue, metricName} of metrics) {
		const metric = page.getByTestId(`overview__${metricName}-metric`);

		await expect(metric.locator('.overview-metric__value')).toHaveText(
			String(expectedValue)
		);
	}
}

const assetTitle = getRandomString();

let assetId = null;
let individualIdentities = null;

test.beforeEach(async ({analyticsChannel, apiHelpers, page, project, site}) => {
	let individuals = null;

	await syncAnalyticsCloudViaAPI({
		apiHelpers,
		channel: analyticsChannel,
		project,
		siteId: Number(site.id),
	});

	await test.step('Create Individuals', async () => {
		individuals = [
			generateIndividual({
				name: 'userA',
			}),
		];

		await createIndividuals({
			apiHelpers,
			individuals,
		});

		individualIdentities = individuals.map(({id}) => ({
			createDate: new Date().toISOString(),
			id,
			individualId: id,
		}));

		const anonymousIndividual = {
			createDate: new Date().toISOString(),
			id: getRandomString(),
			individualId: null,
		};

		individualIdentities.push(anonymousIndividual);

		await apiHelpers.jsonWebServicesOSBAsah.createIdentities(
			individualIdentities
		);
	});

	await test.step('Create a Blog', async () => {
		const {id} = await apiHelpers.headlessDelivery.postBlog(site.id, {
			headline: assetTitle,
		});

		assetId = id;

		await page.waitForTimeout(1000);
	});
});

test('Overview Metrics - User is able to filter Overview Metric by individuals', async ({
	analyticsChannel: channel,
	apiHelpers,
	contentDashboardPage,
	page,
	site,
}) => {
	await test.step('Generate Blog events for known / anonymous individuals', async () => {
		const date = new Date();

		date.setDate(date.getDate() - 3);

		const blogEvents = individualIdentities.map((identity) => ({
			assetId,
			assetTitle,
			canonicalUrl: 'https://www.liferay.com',
			channelId: channel.id,
			clicks: 1,
			comments: 1,
			eventDate: date.toISOString(),
			ratings: 5,
			ratingsScore: 5,
			readTime: 1,
			sessions: 1,
			userId: identity.id,
			views: 1,
		}));

		await apiHelpers.jsonWebServicesOSBAsah.createBlogsDaily(blogEvents);

		await page.waitForTimeout(1000);
	});

	await test.step('Go to Content Dashboard > Select Blog > Go to Performance Tab', async () => {
		await contentDashboardPage.goToCurrentTab({
			assetTitle,
			siteUrl: site.friendlyUrlPath,
			tabName: 'Performance',
		});
	});

	await test.step('User is able to see metrics by all individuals (all individuals filter selected by default)', async () => {
		const metrics = [
			{
				expectedValue: 2,
				metricName: 'views',
			},
			{
				expectedValue: 2,
				metricName: 'comments',
			},
		];

		await expectMatchMetricValues(metrics, page);
	});

	await test.step('Change individuals filter to filter by known individuals', async () => {
		await page.getByTestId('individuals').click();

		await page.getByTestId('filter-item-KNOWN').click();

		await page.waitForTimeout(1000);

		const metrics = [
			{
				expectedValue: 1,
				metricName: 'views',
			},
			{
				expectedValue: 1,
				metricName: 'comments',
			},
		];

		await expectMatchMetricValues(metrics, page);
	});

	await test.step('Change individuals filter to filter by anonymous individuals', async () => {
		await page.getByTestId('individuals').click();

		await page.getByTestId('filter-item-UNKNOWN').click();

		await page.waitForTimeout(1000);

		const metrics = [
			{
				expectedValue: 1,
				metricName: 'views',
			},
			{
				expectedValue: 1,
				metricName: 'comments',
			},
		];

		await expectMatchMetricValues(metrics, page);
	});
});

test('Overview Metrics - User is able to filter Overview Metric by range selectors', async ({
	analyticsChannel: channel,
	apiHelpers,
	contentDashboardPage,
	page,
	site,
}) => {
	await test.step('Generate Blog events for known / anonymous individuals', async () => {
		const rangeSelectors = [7, 28, 30, 90];

		const datesByRangeSelectors = rangeSelectors.map((rangeSelector) => {
			const date = new Date();
			date.setDate(date.getDate() - rangeSelector);

			return date;
		});

		const blogEvents = [];

		datesByRangeSelectors.map((date) => {
			individualIdentities.map((identity) => {
				blogEvents.push({
					assetId,
					assetTitle,
					canonicalUrl: 'https://www.liferay.com',
					channelId: channel.id,
					clicks: 1,
					comments: 1,
					eventDate: date.toISOString(),
					ratings: 5,
					ratingsScore: 5,
					readTime: 1,
					sessions: 1,
					userId: identity.id,
					views: 1,
				});
			});
		});

		await apiHelpers.jsonWebServicesOSBAsah.createBlogsDaily(blogEvents);
	});

	await test.step('Go to Content Dashboard > Select Blog > Go to Performance Tab', async () => {
		await contentDashboardPage.goToCurrentTab({
			assetTitle,
			siteUrl: site.friendlyUrlPath,
			tabName: 'Performance',
		});
	});

	await test.step('Change individuals filter to filter by known individuals to last 90 days', async () => {
		await page.getByTestId('rangeSelectors').click();

		await page.getByTestId('filter-item-90').click();

		await page.waitForTimeout(1000);

		const metrics = [
			{
				expectedValue: 8,
				metricName: 'views',
			},
			{
				expectedValue: 8,
				metricName: 'comments',
			},
		];

		await expectMatchMetricValues(metrics, page);
	});

	await test.step('Change individuals filter to filter by known individuals to last 30 days', async () => {
		await page.getByTestId('rangeSelectors').click();

		await page.getByTestId('filter-item-30').click();

		await page.waitForTimeout(1000);

		const metrics = [
			{
				expectedValue: 6,
				metricName: 'views',
			},
			{
				expectedValue: 6,
				metricName: 'comments',
			},
		];

		await expectMatchMetricValues(metrics, page);
	});

	await test.step('Change individuals filter to filter by known individuals to last 28 days', async () => {
		await page.getByTestId('rangeSelectors').click();

		await page.getByTestId('filter-item-28').click();

		await page.waitForTimeout(1000);

		const metrics = [
			{
				expectedValue: 4,
				metricName: 'views',
			},
			{
				expectedValue: 4,
				metricName: 'comments',
			},
		];

		await expectMatchMetricValues(metrics, page);
	});

	await test.step('Change individuals filter to filter by known individuals to last 7 days', async () => {
		await page.getByTestId('rangeSelectors').click();

		await page.getByTestId('filter-item-7').click();

		await page.waitForTimeout(1000);

		const metrics = [
			{
				expectedValue: 2,
				metricName: 'views',
			},
			{
				expectedValue: 2,
				metricName: 'comments',
			},
		];

		await expectMatchMetricValues(metrics, page);
	});
});
