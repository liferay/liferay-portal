/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../utils/clickAndExpectToBeVisible';
import {PORTLET_URLS} from '../../utils/portletUrls';
import {waitForAlert} from '../../utils/waitForAlert';
import {SystemSettingsPage} from '../configuration-admin-web/SystemSettingsPage';

export class RedirectPage {
	readonly createButton: Locator;
	readonly destinationURL: Locator;
	readonly destinationURLErrorMessage: Locator;
	readonly expirationDate: Locator;
	readonly page: Page;
	readonly pattern: Locator;
	readonly patternLink: Locator;
	readonly redirectChainModal: Locator;
	readonly saveButton: Locator;
	readonly sourceURL: Locator;
	readonly systemSettingsPage: SystemSettingsPage;
	readonly type: Locator;

	constructor(page: Page) {
		this.createButton = page.getByRole('button', {name: 'Create'});
		this.destinationURL = page.getByLabel('Destination URL');
		this.destinationURLErrorMessage = page.getByText(
			'This URL is not supported'
		);
		this.expirationDate = page.getByLabel('Expiration Date');
		this.page = page;
		this.pattern = page.getByLabel('Pattern', {exact: true});
		this.patternLink = page.getByRole('link', {name: 'Patterns'});
		this.redirectChainModal = page.getByRole('dialog');
		this.saveButton = page.getByRole('button', {name: 'Save'});
		this.sourceURL = page.getByLabel('Source URL');
		this.systemSettingsPage = new SystemSettingsPage(page);
		this.type = page.getByLabel('Type');
	}

	async addRedirect(
		sourceURL: string,
		destinationURL: string,
		permanent: boolean,
		expirationDate?: string
	) {
		await this.page.getByRole('link', {name: 'Add'}).click();

		await this.fillRedirectDetails(
			sourceURL,
			destinationURL,
			permanent,
			expirationDate
		);

		await this.createButton.click();

		await waitForAlert(this.page);
	}

	async addRedirectPattern(
		pattern: string,
		destinationURL: string,
		isSuccessful: boolean = true
	) {
		await this.patternLink.click();

		await this.pattern.fill(pattern);
		await this.destinationURL.fill(destinationURL);

		await this.saveButton.click();

		if (isSuccessful) {
			await waitForAlert(this.page);
		}
	}

	async assertDestinationURLValidation(destinationURL: string) {
		await this.fillRedirectDetails('source-page', destinationURL, false);

		await this.createButton.click();

		await expect(this.destinationURLErrorMessage).toBeVisible();
	}

	async configureRedirectNotFound(enabled: boolean) {
		await this.systemSettingsPage.goToSystemSetting('Pages', 'Redirection');

		if (enabled) {
			await this.page.getByLabel('Enabled').check();
		}
		else {
			await this.page.getByLabel('Enabled').uncheck();
		}

		await this.page.getByRole('button', {name: /save|update/i}).click();

		await waitForAlert(this.page);
	}

	async deleteRedirect(currentSourceURL: string) {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {name: 'Delete'}),
			trigger: this.page
				.getByRole('row', {name: currentSourceURL})
				.getByLabel('Show Actions'),
		});

		await waitForAlert(this.page);
	}

	async editRedirect(
		currentSourceURL: string,
		sourceURL: string,
		destinationURL: string,
		permanent: boolean,
		expirationDate?: string
	) {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {name: 'Edit'}),
			trigger: this.page
				.getByRole('row', {name: currentSourceURL})
				.getByLabel('Show Actions'),
		});

		await this.fillRedirectDetails(
			sourceURL,
			destinationURL,
			permanent,
			expirationDate
		);

		await this.saveButton.click();

		await this.page.getByText(sourceURL).waitFor();
	}

	async fillRedirectDetails(
		sourceURL: string,
		destinationURL: string,
		permanent: boolean,
		expirationDate?: string
	) {
		await this.sourceURL.fill(sourceURL);
		await this.destinationURL.fill(destinationURL);

		if (permanent) {
			await this.type.selectOption('true');
		}

		if (expirationDate) {
			await this.expirationDate.click();

			await this.expirationDate.pressSequentially(expirationDate);
		}
	}

	async goto(siteUrl?: Site['friendlyUrlPath']) {
		await this.page.goto(
			`/group${siteUrl || '/guest'}${PORTLET_URLS.redirect}`
		);
	}

	async updateReferences(updateReferences: boolean = true) {
		await this.redirectChainModal.waitFor({state: 'visible'});

		if (!updateReferences) {
			await this.redirectChainModal
				.getByLabel('Update References')
				.click();
		}

		await this.redirectChainModal
			.getByRole('button', {name: 'Create'})
			.click();
	}
}
