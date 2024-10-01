/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {PORTLET_URLS} from '../../../utils/portletUrls';
import {waitForAlert} from '../../../utils/waitForAlert';

export class StagingConfigurationPage {
	readonly page: Page;
	readonly localLiveRadio: Locator;
	readonly remoteLiveRadio: Locator;
	readonly saveButton: Locator;
	readonly confirmationOkBUtton: Locator;

	constructor(page: Page) {
		this.page = page;

		this.localLiveRadio = page.getByLabel('Local Live:');
		this.remoteLiveRadio = page.getByLabel('Remote Live:');
		this.saveButton = page.getByRole('button', {name: 'Save'});
		this.confirmationOkBUtton = page
			.getByRole('dialog')
			.getByRole('button', {exact: true, name: 'Ok'});
	}

	async gotoStagingConfiguration(siteUrl?: Site['friendlyUrlPath']) {
		await this.page.goto(
			`/group${siteUrl || '/guest'}${PORTLET_URLS.staging}`
		);
	}

	async enableLocalStaging() {
		await this.localLiveRadio.check();

		this.page.on('dialog', async (dialog) => {
			await dialog.accept();
		});

		await this.saveButton.click();

		await waitForAlert(this.page, `Local staging is successfully enabled.`);

		const succesfull = this.page.getByText('Successful');

		await succesfull.waitFor();
	}
}
