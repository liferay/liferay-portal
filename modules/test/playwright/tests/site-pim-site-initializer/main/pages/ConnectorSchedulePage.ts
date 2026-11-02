/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

export class ConnectorSchedulePage {
	readonly backLink: Locator;
	readonly cronExpressionInput: Locator;
	readonly latestLogLink: Locator;
	readonly latestLogStatus: Locator;
	readonly logError: Locator;
	readonly logOutput: Locator;
	readonly logsTab: Locator;
	readonly page: Page;

	constructor(page: Page) {
		this.backLink = page.getByRole('link', {exact: true, name: 'Back'});
		this.cronExpressionInput = page.getByLabel('Cron Expression', {
			exact: true,
		});
		this.latestLogLink = page
			.locator('td.lfr-start-date-column')
			.first()
			.getByRole('link');
		this.latestLogStatus = page.locator('td.lfr-status-column').first();
		this.logError = page
			.locator('.row:not(:has(.row))', {
				has: page.getByText('Error', {exact: true}),
			})
			.locator('pre');
		this.logOutput = page
			.locator('.row:not(:has(.row))', {
				has: page.getByText('Output', {exact: true}),
			})
			.locator('pre');
		this.logsTab = page.getByRole('link', {exact: true, name: 'Logs'});
		this.page = page;
	}

	async openLatestLog(status: string) {
		await this.logsTab.click();

		await this.page.waitForURL(/screenNavigationCategoryKey=logs/);

		await expect(async () => {
			await this.page.reload();

			await expect(this.latestLogStatus).toHaveText(status, {
				timeout: 2000,
			});
		}).toPass({timeout: 60000});

		await this.latestLogLink.click();
	}
}
