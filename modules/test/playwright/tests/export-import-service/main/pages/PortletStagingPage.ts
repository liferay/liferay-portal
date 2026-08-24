/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';

export class PortletStagingPage {
	readonly page: Page;
	readonly publishStagingIframe: FrameLocator;
	readonly publishSuccessStatus: Locator;
	readonly publishToLiveButton: Locator;

	constructor(page: Page) {
		this.page = page;
		this.publishStagingIframe = this.page.frameLocator(
			'iframe[title="Staging"]'
		);

		this.publishSuccessStatus = this.publishStagingIframe
			.locator('[data-qa-id="row"]')
			.first()
			.getByText('Successful');
		this.publishToLiveButton = this.publishStagingIframe.getByRole(
			'button',
			{
				name: 'Publish to Live',
			}
		);
	}

	async openIframe() {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {name: 'Staging'}),
			trigger: this.page.getByLabel('Options'),
		});

		await this.publishStagingIframe
			.getByRole('link', {name: 'New Publish Process'})
			.waitFor();
	}

	async closeIframe() {
		await this.page.getByRole('dialog').getByLabel('close').click();
	}

	async publishToLive() {
		this.page.once('dialog', async (dialog) => {
			await dialog.accept();
		});
		await this.publishToLiveButton.click();

		await this.publishSuccessStatus.waitFor({timeout: 30000});
		await this.closeIframe();
	}
}
