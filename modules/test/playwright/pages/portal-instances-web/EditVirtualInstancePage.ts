/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../utils/clickAndExpectToBeVisible';
import {GlobalMenuPage} from '../product-navigation-applications-menu/GlobalMenuPage';

export class EditVirtualInstancePage {
	readonly activeToggle: Locator;
	readonly emailAddressField: Locator;
	readonly globalMenuPage: GlobalMenuPage;
	readonly idField: Locator;
	readonly mailDomainField: Locator;
	readonly maxUsersField: Locator;
	readonly page: Page;
	readonly passwordField: Locator;
	readonly saveButton: Locator;
	readonly screenNameField: Locator;
	readonly successMessage: Locator;
	readonly virtualHostField: Locator;

	constructor(page: Page) {
		this.activeToggle = page.getByText('Active');
		this.globalMenuPage = new GlobalMenuPage(page);
		this.emailAddressField = page.getByLabel('Email Address');
		this.idField = page.getByLabel('ID', {exact: true});
		this.mailDomainField = page.getByLabel('Mail Domain');
		this.maxUsersField = page.getByLabel('Max Users');
		this.page = page;
		this.passwordField = page.getByLabel('Password');
		this.saveButton = page.getByRole('button', {name: 'Save'});
		this.screenNameField = page.getByLabel('Screen Name');
		this.successMessage = page.getByText(
			'Your request completed successfully'
		);
		this.virtualHostField = this.page.getByLabel('Virtual Host');
	}

	async editVirtualInstance(
		webId: string,
		active = true,
		mailDomain?: string,
		maxUsers?: string,
		virtualHost?: string
	) {
		await this.goto(webId);

		// ID field only exists when editing a virtual instance, use it to
		// verify the page has properly rendered and is ready for editing

		await this.idField.waitFor();

		await this.activeToggle.setChecked(active);

		if (mailDomain) {
			await this.mailDomainField.fill(mailDomain);
		}

		if (maxUsers) {
			await this.maxUsersField.fill(maxUsers);
		}

		if (virtualHost) {
			await this.virtualHostField.fill(virtualHost);
		}

		await this.saveButton.click();
		await expect(await this.successMessage).toBeVisible();
		await this.page.getByLabel('Close', {exact: true}).click();
	}

	async checkEditVirtualInstanceFields(webId: string) {
		await this.goto(webId);

		// ID field only exists when editing a virtual instance, use it to
		// verify the page has properly rendered and is ready for editing

		await this.idField.waitFor();

		await expect(this.activeToggle).toBeVisible;

		await expect(this.mailDomainField).toBeVisible;

		await expect(this.maxUsersField).toBeVisible;

		await expect(this.virtualHostField).toBeVisible;

		await expect(this.screenNameField).not.toBeVisible;
		await expect(this.emailAddressField).not.toBeVisible;
		await expect(this.passwordField).not.toBeVisible;
	}

	async goto(webId: string) {
		await this.globalMenuPage.goToControlPanel('Virtual Instances');

		const row = await this.page.getByRole('row').filter({hasText: webId});

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {name: 'Edit'}),
			trigger: row.getByRole('button', {name: 'Show Actions'}),
		});
	}
}
