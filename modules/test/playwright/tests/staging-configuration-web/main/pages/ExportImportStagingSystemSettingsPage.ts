/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {SystemSettingsPage} from '../../../../pages/configuration-admin-web/SystemSettingsPage';

export class ExportImportStagingSystemSettingsPage {
	readonly page: Page;
	readonly systemSettingsPage: SystemSettingsPage;
	readonly showAdvancedConfigurationByDefault: Locator;

	constructor(page: Page) {
		this.page = page;
		this.systemSettingsPage = new SystemSettingsPage(page);
		this.showAdvancedConfigurationByDefault =
			this.systemSettingsPage.page.getByLabel(
				'Show Advanced Staging Configuration by Default'
			);
	}

	async goto() {
		await this.systemSettingsPage.goToSystemSetting(
			'Infrastructure',
			'Export/Import, Staging'
		);
	}

	async checkShowAdvancedStagingConfiguration(checked: boolean) {
		await this.systemSettingsPage.checkOption(
			'Show Advanced Staging Configuration by Default',
			checked
		);
		await this.systemSettingsPage.saveAndWaitForAlert();
	}
}
