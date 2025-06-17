/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {PORTLET_URLS} from '../../utils/portletUrls';

export class MetricsPage {
	readonly page: Page;
	readonly totalPendingItems: Locator;

	constructor(page: Page) {
		this.page = page;
		this.totalPendingItems = this.page
			.getByRole('link', {name: 'Total Pending'})
			.first();
	}

	async chooseProcess(processName: string) {
		await this.page.getByRole('link', {name: processName}).click();
	}

	async viewAllPendingItems() {
		await this.totalPendingItems.click();
	}

	async goTo(siteUrl?: Site['friendlyUrlPath']) {
		await this.page.goto(
			`/group${siteUrl || '/guest'}${PORTLET_URLS.workflowMetrics}`,
			{waitUntil: 'load'}
		);
	}
}
