/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {digitalSalesRoomPagesTest} from '../../../fixtures/digitalSalesRoomPagesTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';

export const test = mergeTests(
	digitalSalesRoomPagesTest,
	featureFlagsTest({
		'LPD-35443': {enabled: true},
	}),
	loginTest()
);

test(
	'Navigate between Overview and Timeline analytics tabs',
	{tag: '@LPD-86504'},
	async ({dsrAnalyticsPage, page}) => {
		await dsrAnalyticsPage.goToOverview();

		await expect(dsrAnalyticsPage.analyticsHeading).toBeVisible();
		await expect(dsrAnalyticsPage.overviewTab).toBeVisible();
		await expect(dsrAnalyticsPage.timelineTab).toBeVisible();

		await expect(dsrAnalyticsPage.roomStatistics).toBeVisible();
		await expect(dsrAnalyticsPage.visitsFrequencyFrame).toBeVisible();
		await expect(dsrAnalyticsPage.mostActiveVisitorsFrame).toBeVisible();

		await dsrAnalyticsPage.timelineTab.click();
		await page.waitForURL(/view-timeline/);

		await expect(dsrAnalyticsPage.analyticsHeading).toBeVisible();
		await expect(dsrAnalyticsPage.engagementTimelineFrame).toBeVisible();
		await expect(dsrAnalyticsPage.activityLogFrame).toBeVisible();
		await expect(dsrAnalyticsPage.visitsFrequencyFrame).not.toBeVisible();

		await dsrAnalyticsPage.overviewTab.click();
		await page.waitForURL(/view-overview/);

		await expect(dsrAnalyticsPage.roomStatistics).toBeVisible();
		await expect(
			dsrAnalyticsPage.engagementTimelineFrame
		).not.toBeVisible();
	}
);
