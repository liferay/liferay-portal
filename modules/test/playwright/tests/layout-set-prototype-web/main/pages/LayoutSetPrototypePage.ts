/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {liferayConfig} from '../../../../liferay.config';
import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';
import {reloadUntilVisible} from '../../../../utils/reloadUntilVisible';

export class LayoutSetPrototypePage {
	readonly activateMenuItem: Locator;
	readonly addLink: Locator;
	readonly executeSyncMenuItem: Locator;
	readonly homePageLink: Locator;
	readonly nameBox: Locator;
	readonly notificationsButton: Locator;
	readonly page: Page;
	readonly saveButton: Locator;

	constructor(page: Page) {
		this.page = page;

		this.activateMenuItem = page.getByRole('menuitem', {name: 'Activate'});
		this.addLink = page.getByRole('link', {name: 'Add'});
		this.executeSyncMenuItem = page.getByRole('menuitem', {
			name: 'Execute Site Template Sync',
		});
		this.homePageLink = page.getByLabel('Home', {exact: true});
		this.nameBox = page.getByPlaceholder('Name');
		this.notificationsButton = page.getByLabel('New Notification');
		this.saveButton = page.getByRole('button', {name: 'Save'});
	}

	async addSiteTemplate(templateName: string) {
		await this.addLink.click();
		await this.nameBox.click();
		await this.nameBox.fill(templateName);
		await this.saveButton.click();
	}

	async checkIfWebContentAddedToHome(
		siteName: string,
		webContentBody: string
	) {
		await this.page.goto(
			liferayConfig.environment.baseUrl + `/web/${siteName}`
		);
		const myLocator = this.page.getByRole('link', {
			name: `Go to ${siteName}`,
		});
		await reloadUntilVisible({
			myLocator,
			page: this.page,
		});
		await this.page
			.getByText(webContentBody)
			.waitFor({state: 'visible', timeout: 3000});
		await this.page.getByText(webContentBody).isVisible();
	}

	async checkIfWebContentAdded(
		siteName: string,
		webContentName: string,
		webContentBody: string
	) {
		await this.page.goto(
			liferayConfig.environment.baseUrl + `/group/${siteName}`
		);
		const myLocator = this.page.getByText(webContentName);
		await reloadUntilVisible({
			myLocator,
			page: this.page,
		});
		await this.page
			.getByRole('menuitem', {name: webContentName})
			.waitFor({state: 'visible'});
		await this.page.getByRole('menuitem', {name: webContentName}).click();
		await this.page.getByText(webContentBody).waitFor({state: 'visible'});
		await this.page.getByText(webContentBody).isVisible();
	}

	async executeSync(templateName: string) {
		await clickAndExpectToBeVisible({
			target: this.executeSyncMenuItem,
			trigger: this.rowActions(templateName),
		});

		await this.executeSyncMenuItem.click();

		await this.page
			.getByRole('button', {exact: true, name: 'Sync'})
			.click();

		await this.page
			.getByRole('dialog')
			.getByRole('button', {name: 'Sync Sites'})
			.click();

		await expect(
			this.page.getByText(
				`The sync of the site template ${templateName} started. You will receive a notification when the process is complete.`
			)
		).toBeVisible();
	}

	async executeSyncAndWaitForSuccess(templateName: string) {
		await this.executeSync(templateName);

		await this.waitForSyncSuccessNotification(templateName);
	}

	async getSiteTemplateUrl(templateName: string) {
		return await this.page.getByText(templateName).getAttribute('href');
	}

	rowActions(templateName: string): Locator {
		return this.page
			.locator('tr', {hasText: templateName})
			.getByLabel('Actions');
	}

	async waitForSyncSuccessNotification(templateName: string) {
		await expect(async () => {
			await this.page.reload();

			await this.notificationsButton.click({timeout: 100});

			await expect(
				this.page.getByText(
					`The sync of the site template ${templateName} finished successfully.`,
					{exact: true}
				)
			).toBeVisible({timeout: 100});
		}).toPass();
	}
}
