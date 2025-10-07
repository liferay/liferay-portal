/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page} from '@playwright/test';

import {InstanceSettingsPage} from '../../../../pages/configuration-admin-web/InstanceSettingsPage';

export class ExportImportStagingInstanceSettingsPage {
	readonly page: Page;
	readonly instanceSettingsPage: InstanceSettingsPage;

	constructor(page: Page) {
		this.page = page;
		this.instanceSettingsPage = new InstanceSettingsPage(page);
	}

	async goto() {
		await this.instanceSettingsPage.goToInstanceSetting(
			'Infrastructure',
			'Export/Import, Staging'
		);

		await this.page.waitForLoadState();
	}

	async checkConfigurationOption({
		checked,
		label,
	}: {
		checked: boolean;
		label: string;
	}) {
		await this.instanceSettingsPage.checkOption(label, checked);
		await this.instanceSettingsPage.saveButton.click();
	}
}
