/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

export class DSRAnalyticsPage {
	readonly activityLogFrame: Locator;
	readonly analyticsHeading: Locator;
	readonly engagementTimelineFrame: Locator;
	readonly mostActiveVisitorsFrame: Locator;
	readonly overviewTab: Locator;
	readonly page: Page;
	readonly roomStatistics: Locator;
	readonly timelineTab: Locator;
	readonly visitsFrequencyFrame: Locator;

	constructor(page: Page) {
		this.activityLogFrame = page
			.locator('.analytics-frame')
			.filter({hasText: 'Activity Log'});
		this.analyticsHeading = page.getByRole('heading', {
			exact: true,
			level: 2,
			name: 'Analytics',
		});
		this.engagementTimelineFrame = page
			.locator('.analytics-frame')
			.filter({hasText: 'Engagement Timeline'});
		this.mostActiveVisitorsFrame = page
			.locator('.analytics-frame')
			.filter({hasText: 'Most Active Visitors'});
		this.overviewTab = page.getByRole('button', {
			exact: true,
			name: 'Overview',
		});
		this.page = page;
		this.roomStatistics = page.locator('.room-statistics-container');
		this.timelineTab = page.getByRole('button', {
			exact: true,
			name: 'Timeline',
		});
		this.visitsFrequencyFrame = page
			.locator('.analytics-frame')
			.filter({hasText: 'Average Visits'});
	}

	async goToOverview() {
		await this.page.goto('/web/dsr/analytics/view-overview');
	}
}
