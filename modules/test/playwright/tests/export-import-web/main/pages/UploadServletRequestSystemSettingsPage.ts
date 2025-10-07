/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {SystemSettingsPage} from '../../../../pages/configuration-admin-web/SystemSettingsPage';

export class UploadServletRequestSystemSettingsPage {
	readonly page: Page;
	readonly systemSettingPage: SystemSettingsPage;
	readonly overallMaximumUploadRequestSize: Locator;

	constructor(page: Page) {
		this.page = page;
		this.systemSettingPage = new SystemSettingsPage(page);
		this.overallMaximumUploadRequestSize =
			this.systemSettingPage.page.getByLabel(
				'Overall Maximum Upload Request Size'
			);
	}

	async goto() {
		await this.systemSettingPage.goToSystemSetting(
			'Infrastructure',
			'Upload Servlet Request'
		);
	}

	async getOverallMaximumUploadRequestSize(): Promise<string> {
		return this.overallMaximumUploadRequestSize.inputValue();
	}

	async setOverallMaximumUploadRequestSize({size}: {size: string}) {
		await this.overallMaximumUploadRequestSize.fill(size);

		await this.systemSettingPage.saveButton.click();
	}
}
