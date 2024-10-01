/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {SiteSettingsPage} from '../../../pages/configuration-admin-web/SiteSettingsPage';
import {waitForAlert} from '../../../utils/waitForAlert';

export class FileSizeLimitsSiteSettingsPage {
	readonly page: Page;
	readonly saveButton: Locator;
	readonly siteSettingsPage: SiteSettingsPage;

	constructor(page: Page) {
		this.page = page;
		this.saveButton = page.getByRole('button', {name: 'Save'});
		this.siteSettingsPage = new SiteSettingsPage(page);
	}

	async goto(siteUrl?: Site['friendlyUrlPath']) {
		await this.siteSettingsPage.goToSiteSetting(
			'Documents and Media',
			'File Size Limits',
			siteUrl
		);
	}

	async modifyInputAndSave(label: string, value: string) {
		const inputField = this.page.getByLabel(label);
		await inputField.click();
		await inputField.fill(value);

		await this.saveButton.click();
		await waitForAlert(this.page);
	}
}
