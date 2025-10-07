/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {PORTLET_URLS} from '../../../../utils/portletUrls';

export class ExportPage {
	readonly page: Page;
	readonly newButton: Locator;
	constructor(page: Page) {
		this.page = page;
		this.newButton = page.getByRole('link', {
			name: 'Add FreeMarker (.ftl)',
		});
	}

	async exportPages() {
		await this.page.getByRole('link', {name: 'Custom Export'}).click();
		await this.page.getByRole('button', {name: 'Export'}).click();

		for (const processResult of await this.page
			.getByTestId('processResult')
			.all()) {
			await expect(processResult.getByText('Successful')).toBeVisible({
				timeout: 60 * 1000,
			});
		}
	}

	async goto(siteKey: string) {
		await this.page.goto(`/group${siteKey}${PORTLET_URLS.export}`, {
			waitUntil: 'domcontentloaded',
		});
	}
}
