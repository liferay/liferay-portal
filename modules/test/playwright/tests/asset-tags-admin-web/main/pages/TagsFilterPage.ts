/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Page} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';
import {waitForAlert} from '../../../../utils/waitForAlert';

export class TagsFilterPage {
	readonly page: Page;

	readonly configurationIframe: FrameLocator;

	constructor(page: Page) {
		this.page = page;

		this.configurationIframe = this.page.frameLocator(
			'iframe[title*="Configuration"]'
		);
	}

	async changeDisplayTemplate(type: 'Cloud' | 'Number') {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.configurationIframe.getByText(type, {
				exact: true,
			}),
			trigger: this.configurationIframe.getByLabel('Display Template'),
		});
	}

	async saveAndClose() {
		await this.configurationIframe
			.getByRole('button', {name: 'Save'})
			.click();

		await waitForAlert(
			this.configurationIframe,
			'Success:You have successfully updated the setup.'
		);

		await this.page
			.locator('.modal-header')
			.getByLabel('Close', {exact: true})
			.click();
	}
}
