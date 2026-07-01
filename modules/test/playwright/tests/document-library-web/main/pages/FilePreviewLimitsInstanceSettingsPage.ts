/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {InstanceSettingsPage} from '../../../../pages/configuration-admin-web/InstanceSettingsPage';
import {waitForAlert} from '../../../../utils/waitForAlert';

export class FilePreviewLimitsInstanceSettingsPage {
	readonly page: Page;
	readonly saveButton: Locator;
	readonly instanceSettingsPage: InstanceSettingsPage;

	constructor(page: Page) {
		this.page = page;
		this.saveButton = page
			.getByRole('button', {name: 'Save'})
			.or(page.getByRole('button', {name: 'Update'}));
		this.instanceSettingsPage = new InstanceSettingsPage(page);
	}

	async goto() {
		await this.instanceSettingsPage.goToInstanceSetting(
			'Documents and Media',
			'File Preview Limits'
		);
	}

	async resetToDefaultValues() {
		await this.instanceSettingsPage.resetInstanceSetting();
	}

	async save() {
		await this.saveButton.click();

		await waitForAlert(this.page);
	}
}
