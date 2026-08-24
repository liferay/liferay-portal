/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {ServerAdministrationPage} from '../../../../pages/server-admin-web/ServerAdministrationPage';
import {waitForPageToBeLoaded} from '../../../../utils/waitForPageToBeLoaded';

export class DatabaseMigrationPage {
	readonly description: Locator;
	readonly errorAlert: Locator;
	readonly exportButton: Locator;
	readonly exportFilesPathInput: Locator;
	readonly page: Page;
	readonly serverAdministrationPage: ServerAdministrationPage;
	readonly successToast: Locator;
	readonly tabLink: Locator;

	constructor(page: Page) {
		this.page = page;

		this.description = page.locator('.sheet-text');
		this.errorAlert = page.locator('.alert-danger[role="alert"]');
		this.exportButton = page.getByRole('button', {
			exact: true,
			name: 'Export',
		});
		this.exportFilesPathInput = page.getByLabel('Export Files Path');
		this.serverAdministrationPage = new ServerAdministrationPage(page);
		this.successToast = page.locator('.alert-success');
		this.tabLink = page.getByRole('link', {
			exact: true,
			name: 'Database Migration',
		});
	}

	async exportSchema(exportFilesPath: string) {
		await this.exportFilesPathInput.fill(exportFilesPath);

		await this.exportButton.click();
	}

	async goto() {
		await this.serverAdministrationPage.goto('database-migration');

		await waitForPageToBeLoaded(this.page);

		await expect(this.exportFilesPathInput).toBeVisible();
	}
}
