/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {clickAndExpectToBeHidden} from '../../utils/clickAndExpectToBeHidden';
import {waitForAlert} from '../../utils/waitForAlert';
import {InstanceSettingsPage} from '../configuration-admin-web/InstanceSettingsPage';

export class FriendlyUrlInstanceSettingsPage {
	readonly page: Page;
	readonly saveButton: Locator;
	readonly instanceSettingsPage: InstanceSettingsPage;

	constructor(page: Page) {
		this.page = page;
		this.saveButton = page.getByRole('button', {name: 'Save'});
		this.instanceSettingsPage = new InstanceSettingsPage(page);
	}

	async goto() {
		await this.instanceSettingsPage.goToInstanceSetting(
			'SEO',
			'Friendly URL'
		);
	}

	async modifySeparator(testId: string, value: string) {
		await this.page.locator(`[data-testid='${testId}']`).click();
		await this.page.locator(`[data-testid='${testId}']`).fill(value);
		await this.saveButton.click();
		await waitForAlert(this.page);
	}

	async resetSeparator(testId: string) {
		await this.page.locator(`[data-testid='${testId}']`).click();
		await this.saveButton.click();
		await waitForAlert(this.page);
	}
}
