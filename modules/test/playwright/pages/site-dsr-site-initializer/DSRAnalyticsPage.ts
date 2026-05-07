/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

export class DSRAnalyticsPage {
	readonly activityLogFrame: Locator;
	readonly analyticsHeading: Locator;
	readonly dateRangePicker: Locator;
	readonly dateRangePresetSelect: Locator;
	readonly engagementTimelineFrame: Locator;
	readonly filtersToolbar: Locator;
	readonly latestActivityFrame: Locator;
	readonly mostActiveVisitorsFrame: Locator;
	readonly mostEngagedDocumentsFrame: Locator;
	readonly overviewTab: Locator;
	readonly page: Page;
	readonly recentEngagementFrame: Locator;
	readonly roomFilterMenu: Locator;
	readonly roomFilterTrigger: Locator;
	readonly roomStatistics: Locator;
	readonly timelineTab: Locator;
	readonly userFilterMenu: Locator;
	readonly userFilterTrigger: Locator;
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
		this.dateRangePicker = page.locator('#dsrEngagementRange');
		this.dateRangePresetSelect = page.locator(
			'.dsr-date-range-preset-select'
		);
		this.engagementTimelineFrame = page
			.locator('.analytics-frame')
			.filter({hasText: 'Engagement Timeline'});
		this.filtersToolbar = page.locator('.analytics-filters-toolbar');
		this.latestActivityFrame = page
			.locator('.analytics-frame')
			.filter({hasText: 'Latest Activity'});
		this.mostActiveVisitorsFrame = page
			.locator('.analytics-frame')
			.filter({hasText: 'Most Active Visitors'});
		this.mostEngagedDocumentsFrame = page
			.locator('.analytics-frame')
			.filter({hasText: 'Most Engaged Documents'});
		this.overviewTab = page.getByRole('button', {
			exact: true,
			name: 'Overview',
		});
		this.page = page;
		this.recentEngagementFrame = page
			.locator('.analytics-frame')
			.filter({hasText: 'Recent Engagement'});
		this.roomFilterMenu = page.locator('.dropdown-menu.show');
		this.roomFilterTrigger = page.locator('.room-filter-button');
		this.roomStatistics = page.locator('.room-statistics-container');
		this.timelineTab = page.getByRole('button', {
			exact: true,
			name: 'Timeline',
		});
		this.userFilterMenu = page.locator('.dropdown-menu.show');
		this.userFilterTrigger = page.locator('.user-filter-button');
		this.visitsFrequencyFrame = page
			.locator('.analytics-frame')
			.filter({hasText: 'Visits Frequency'});
	}

	async goToOverview() {
		await this.page.goto('/web/dsr/analytics/view-overview');
	}

	async goToTimeline() {
		await this.page.goto('/web/dsr/analytics/view-timeline');
	}

	async openRoomFilter() {
		await this.roomFilterTrigger.click();
	}

	async openUserFilter() {
		await this.userFilterTrigger.click();
	}

	roomFilterOption(name: string): Locator {
		return this.roomFilterMenu
			.getByRole('menuitem')
			.filter({hasText: name});
	}

	async selectDateRangePreset(value: string) {
		await this.dateRangePresetSelect.selectOption(value);
	}

	async selectRoom(name: string) {
		await this.openRoomFilter();
		await this.roomFilterOption(name).click();
	}

	async selectUser(name: string) {
		await this.openUserFilter();
		await this.userFilterOption(name).click();
	}

	userFilterOption(name: string): Locator {
		return this.userFilterMenu
			.getByRole('menuitem')
			.filter({hasText: name});
	}
}
