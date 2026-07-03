/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {ServerAdministrationPage} from '../../../../pages/server-admin-web/ServerAdministrationPage';
import {waitForPageToBeLoaded} from '../../../../utils/waitForPageToBeLoaded';

export class DocumentMigrationPage {
	readonly page: Page;
	readonly serverAdministrationPage: ServerAdministrationPage;
	readonly tabLink: Locator;

	constructor(page: Page) {
		this.page = page;

		this.serverAdministrationPage = new ServerAdministrationPage(page);
		this.tabLink = page.getByRole('link', {
			exact: true,
			name: 'Document Migration',
		});
	}

	async goto() {
		await this.serverAdministrationPage.goto('document-migration');

		await waitForPageToBeLoaded(this.page);
	}
}
