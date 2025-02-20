/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page} from '@playwright/test';

import {UIElementsPage} from '../uielements/UIElementsPage';

export class SitesPage {
	readonly page: Page;

	readonly addButton: Locator;
	readonly addSiteButton: Locator;
	readonly addSiteIFrame: FrameLocator;
	readonly customSiteTemplatesItem: Locator;
	readonly defaultPagesAsPrivateCheck: Locator;
	readonly nameBox: Locator;
	readonly uiElementsPage: UIElementsPage;

	constructor(page: Page) {
		this.page = page;

		this.addButton = page
			.frameLocator('iframe[title="Add Site"]')
			.getByRole('button', {name: 'Add'});
		this.addSiteButton = page.getByRole('link', {name: 'Add Site'});
		this.customSiteTemplatesItem = page.getByRole('menuitem', {
			name: 'Custom Site Templates',
		});
		this.addSiteIFrame = page.frameLocator('iframe[title="Add Site"]');
		this.nameBox = this.addSiteIFrame.getByLabel('Name Required');
		this.defaultPagesAsPrivateCheck = page
			.frameLocator('iframe[title="Add Site"]')
			.getByLabel(
				'Create default pages as private (available only to members). If unchecked, they will be public (available to anyone).'
			);
		this.nameBox = page
			.frameLocator('iframe[title="Add Site"]')
			.getByLabel('Name Required');
		this.uiElementsPage = new UIElementsPage(page);
	}

	async createSiteFromTemplate({
		defaultPagesAsPrivate = false,
		siteName,
		templateName,
	}: {
		defaultPagesAsPrivate?: boolean;
		siteName: string;
		templateName: string;
	}): Promise<string> {
		await this.addSiteButton.click();
		await this.customSiteTemplatesItem.click();
		await this.page
			.getByRole('button', {name: `Select Template: ${templateName}`})
			.click();
		await this.nameBox.fill(siteName);

		if (defaultPagesAsPrivate) {
			await this.defaultPagesAsPrivateCheck.check();
		}

		await this.addButton.click();
		await this.page.waitForURL(/(.)settings(.)/);
		await this.page.getByRole('link', {name: 'Site Configuration'}).click();
		await this.page.getByLabel('Site ID').waitFor({state: 'visible'});
		const siteId = await this.page
			.getByLabel('Site ID')
			.getAttribute('value');

		return siteId as string;
	}
}
