/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page, expect} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../utils/clickAndExpectToBeVisible';
import {GlobalMenuPage} from '../product-navigation-applications-menu/GlobalMenuPage';

export class VirtualInstancesPage {
	private addInstanceFrame: FrameLocator;
	private copyInstanceFrame: FrameLocator;

	readonly addInstanceActive: Locator;
	readonly addInstanceAddButton: Locator;
	readonly addInstanceEmailAddressField: Locator;
	readonly addInstanceMailDomain: Locator;
	readonly addInstanceMaxUsers: Locator;
	readonly addInstancePasswordField: Locator;
	readonly addInstanceScreenNameField: Locator;
	readonly addInstanceVirtualHost: Locator;
	readonly addInstanceVirtualInstanceInitializer: Locator;
	readonly addInstanceWebIdField: Locator;
	readonly copyInstanceCancelButton: Locator;
	readonly copyInstanceDestinationCompanyIdField: Locator;
	readonly copyInstanceErrorMessage: Locator;
	readonly copyInstanceNameField: Locator;
	readonly copyInstanceSubmitButton: Locator;
	readonly copyInstanceVirtualHostField: Locator;
	readonly copyInstanceWebIdField: Locator;
	readonly globalMenuPage: GlobalMenuPage;
	readonly errorMessage: Locator;
	readonly errorMessageScreenName: Locator;
	readonly errorMessageEmailAddress: Locator;
	readonly errorMessagePassword: Locator;
	readonly newVirtualInstanceButton: Locator;
	readonly page: Page;
	readonly successMessage: Locator;

	constructor(page: Page) {
		this.addInstanceFrame = page.frameLocator(
			'iframe[title="Add Instance"]'
		);
		this.copyInstanceFrame = page.frameLocator(
			'iframe[title="Copy Instance"]'
		);

		this.addInstanceActive = this.addInstanceFrame.getByText('Active');
		this.addInstanceAddButton = page.getByText('Add', {exact: true});
		this.addInstanceEmailAddressField =
			this.addInstanceFrame.getByLabel('Email Address');
		this.addInstanceMailDomain =
			this.addInstanceFrame.getByLabel('Mail Domain');
		this.addInstanceMaxUsers =
			this.addInstanceFrame.getByLabel('Max Users');
		this.addInstancePasswordField =
			this.addInstanceFrame.getByLabel('Password');
		this.addInstanceScreenNameField =
			this.addInstanceFrame.getByLabel('Screen Name');
		this.addInstanceVirtualHost =
			this.addInstanceFrame.getByLabel('Virtual Host');
		this.addInstanceVirtualInstanceInitializer =
			this.addInstanceFrame.getByLabel('Virtual Instance Initializer');
		this.addInstanceWebIdField = this.addInstanceFrame.getByLabel('Web ID');
		this.copyInstanceCancelButton = page
			.getByRole('dialog', {name: 'Copy Instance'})
			.getByRole('button', {exact: true, name: 'Cancel'});
		this.copyInstanceDestinationCompanyIdField =
			this.copyInstanceFrame.getByLabel('Destination Company ID');
		this.copyInstanceErrorMessage = this.copyInstanceFrame.getByText(
			'Please enter a valid destination company ID'
		);
		this.copyInstanceNameField = this.copyInstanceFrame.getByLabel('Name');
		this.copyInstanceSubmitButton = page
			.getByRole('dialog', {name: 'Copy Instance'})
			.getByRole('button', {exact: true, name: 'Copy'});
		this.copyInstanceVirtualHostField =
			this.copyInstanceFrame.getByLabel('Virtual Host');
		this.copyInstanceWebIdField =
			this.copyInstanceFrame.getByLabel('Web ID');
		this.globalMenuPage = new GlobalMenuPage(page);
		this.errorMessage = this.addInstanceFrame.getByText(
			'Error:Please enter a valid'
		);
		this.errorMessageEmailAddress = this.addInstanceFrame.getByText(
			'The Email Address field is required'
		);
		this.errorMessagePassword = this.addInstanceFrame.getByText(
			'The Password field is required'
		);
		this.errorMessageScreenName = this.addInstanceFrame.getByText(
			'The Screen Name field is required'
		);
		this.newVirtualInstanceButton = page.getByRole('button', {name: 'Add'});
		this.page = page;
		this.successMessage = page.getByText(
			'Your request completed successfully'
		);
	}

	async addNewVirtualInstance(
		name: string,
		active = true,
		maxUsers = '0',
		virtualInstanceInitializer = ''
	) {
		await this.globalMenuPage.goToHome();
		await this.globalMenuPage.goToControlPanel('Virtual Instances');
		await this.newVirtualInstanceButton.click();

		// Sometimes the frame loads slowly

		await this.page.waitForTimeout(1000);

		await this.addInstanceWebIdField.fill(name);
		await this.addInstanceVirtualHost.fill(name);
		await this.addInstanceMailDomain.fill(name + '.com');
		await this.addInstanceMaxUsers.fill(maxUsers);
		await this.addInstanceActive.setChecked(active);
		await this.addInstanceVirtualInstanceInitializer.selectOption(
			virtualInstanceInitializer
		);

		await Promise.all([
			this.addInstanceAddButton.click(),
			this.page.waitForResponse((response) =>
				response.url().includes('add_instance')
			),
		]);

		await this.page.waitForTimeout(1000);

		// Only wait for Virtual Instance creation if there are no errors

		if (await this.errorMessage.isHidden()) {
			await expect(await this.successMessage).toBeVisible({
				timeout: 180 * 1000,
			});
			await this.page.locator('.alert').getByLabel('Close').click();
		}
	}

	async addNewVirtualInstanceAndSetupAdminUser(
		name: string,
		screenName: string,
		emailAddress: string,
		password: string,
		active = true,
		maxUsers = '0',
		virtualInstanceInitializer = ''
	) {
		await this.globalMenuPage.goToControlPanel('Virtual Instances');
		await this.newVirtualInstanceButton.click();

		// Sometimes the frame loads slowly

		await this.page.waitForTimeout(1000);

		await this.addInstanceWebIdField.fill(name);
		await this.addInstanceVirtualHost.fill(name);
		await this.addInstanceMailDomain.fill(name + '.com');
		await this.addInstanceMaxUsers.fill(maxUsers);
		await this.addInstanceActive.setChecked(active);
		await this.addInstanceVirtualInstanceInitializer.selectOption(
			virtualInstanceInitializer
		);

		await Promise.all([
			this.addInstanceAddButton.click(),
			this.page.waitForResponse((response) =>
				response.url().includes('add_instance')
			),
		]);
		await this.page.waitForTimeout(1000);

		await expect(this.errorMessageScreenName).toBeVisible();
		await expect(this.errorMessageEmailAddress).toBeVisible();
		await expect(this.errorMessagePassword).toBeVisible();

		await this.addInstanceScreenNameField.fill(screenName);
		await this.addInstanceEmailAddressField.fill(emailAddress);
		await this.addInstancePasswordField.fill(password);

		await Promise.all([
			this.addInstanceAddButton.click(),
			this.page.waitForResponse((response) =>
				response.url().includes('add_instance')
			),
		]);

		await this.page.waitForTimeout(1000);
	}

	async deleteVirtualInstance(name: string) {
		await this.globalMenuPage.goToControlPanel('Virtual Instances');

		const row = await this.page.getByRole('row').filter({hasText: name});

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {name: 'Delete'}),
			trigger: row.getByRole('button', {name: 'Show Actions'}),
		});

		await this.page.getByRole('button', {name: 'Delete'}).waitFor();

		await this.page.getByRole('button', {name: 'Delete'}).click();
	}

	async goto() {
		await this.globalMenuPage.goToControlPanel('Virtual Instances');
	}

	async openCopyVirtualInstanceModal(name: string) {
		await this.globalMenuPage.goToControlPanel('Virtual Instances');

		const row = await this.page.getByRole('row').filter({hasText: name});

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {name: 'Copy'}),
			trigger: row.getByRole('button', {name: 'Show Actions'}),
		});

		// Sometimes the frame loads slowly

		await this.page.waitForTimeout(1000);
	}

	async submitCopyVirtualInstance({
		destinationCompanyId,
		name,
		virtualHost,
		webId,
	}: {
		destinationCompanyId: string;
		name: string;
		virtualHost: string;
		webId: string;
	}) {
		await this.copyInstanceNameField.fill(name);
		await this.copyInstanceVirtualHostField.fill(virtualHost);
		await this.copyInstanceWebIdField.fill(webId);
		await this.copyInstanceDestinationCompanyIdField.fill(
			destinationCompanyId
		);

		await Promise.all([
			this.page.waitForResponse(
				(response) => response.url().includes('copy_instance'),
				{timeout: 30 * 1000}
			),
			this.copyInstanceSubmitButton.click(),
		]);

		await this.page.waitForTimeout(1000);
	}
}
