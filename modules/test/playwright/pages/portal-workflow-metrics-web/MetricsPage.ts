/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {PORTLET_URLS} from '../../utils/portletUrls';

export class MetricsPage {
	readonly page: Page;
	readonly totalPendingItems: Locator;

	constructor(page: Page) {
		this.page = page;

		this.totalPendingItems = this.page
			.getByRole('link', {name: 'Total Pending'})
			.and(this.page.locator('.process-tabs-summary-card'));
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

	async goToSLASettings(processName: string) {
		await expect(async () => {
			await this.goTo();

			await expect(
				this.page.getByRole('link', {exact: true, name: processName})
			).toBeVisible({timeout: 5_000});
		}).toPass({timeout: 60_000});

		await this.page
			.getByRole('link', {exact: true, name: processName})
			.click();

		await expect(
			this.page.locator('#headerKebab').getByRole('button')
		).toBeVisible();

		await this.page.locator('#headerKebab').getByRole('button').click();

		await this.page.getByRole('link', {name: 'SLA Settings'}).click();
	}
}
