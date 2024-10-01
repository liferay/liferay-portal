/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import {PORTLET_URLS} from '../../../utils/portletUrls';
import {waitForAlert} from '../../../utils/waitForAlert';

export class NavigationMenusPage {
	readonly page: Page;

	readonly addItemButton: Locator;
	readonly newButton: Locator;

	constructor(page: Page) {
		this.page = page;

		this.addItemButton = page.getByLabel('Add Menu Item');
		this.newButton = page.getByRole('button', {name: 'Add'});
	}

	async goto(siteUrl?: Site['friendlyUrlPath']) {
		await this.page.goto(
			`/group${siteUrl || '/guest'}${PORTLET_URLS.navigationMenus}`
		);
	}

	async createNavigationMenu(name: string) {
		await this.newButton.click();

		const input = this.page.getByPlaceholder('Name');

		await input.waitFor();

		await input.fill(name);

		await this.page.getByRole('button', {name: 'Save'}).click();

		await waitForAlert(this.page);
	}

	async openAddPageModal() {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {name: 'Page'}),
			trigger: this.addItemButton,
		});

		const modal = this.page.frameLocator('iframe[title="Select Pages"]');

		await modal.getByPlaceholder('Search').waitFor();
	}
}
