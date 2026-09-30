/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page, expect} from '@playwright/test';

import {ApiHelpers} from '../../helpers/ApiHelpers';
import {clickAndExpectToBeVisible} from '../../utils/clickAndExpectToBeVisible';
import {NotificationsPage} from '../notifications-web/NotificationsPage';
import {GlobalMenuPage} from '../product-navigation-applications-menu/GlobalMenuPage';

export class VirtualInstancesPage {
	private addInstanceFrame: FrameLocator;
	private copyInstanceFrame: FrameLocator;
	private importInstanceFrame: FrameLocator;

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
	readonly exportInstanceConfirmButton: Locator;
	readonly importInstanceErrorMessage: Locator;
	readonly importInstanceNameField: Locator;
	readonly importInstanceSchemaNameField: Locator;
	readonly importInstanceSubmitButton: Locator;
	readonly importInstanceVirtualHostField: Locator;
	readonly importInstanceWebIdField: Locator;
	readonly globalMenuPage: GlobalMenuPage;
	readonly errorMessage: Locator;
	readonly errorMessageScreenName: Locator;
	readonly errorMessageEmailAddress: Locator;
	readonly errorMessagePassword: Locator;
	readonly newVirtualInstanceButton: Locator;
	readonly page: Page;

	constructor(page: Page) {
		this.addInstanceFrame = page.frameLocator(
			'iframe[title="Add Instance"]'
		);
		this.copyInstanceFrame = page.frameLocator(
			'iframe[title="Copy Instance"]'
		);
		this.importInstanceFrame = page.frameLocator(
			'iframe[title="Import Instance"]'
		);

		this.addInstanceActive = this.addInstanceFrame.getByText('Active');
		this.addInstanceAddButton = page
			.getByRole('dialog', {name: 'Add Instance'})
			.getByRole('button', {exact: true, name: 'Add'});
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
		this.exportInstanceConfirmButton = page
			.getByRole('dialog', {name: 'Export Instance'})
			.getByRole('button', {exact: true, name: 'Export'});
		this.importInstanceErrorMessage = this.importInstanceFrame.getByText(
			'Please enter a valid schema name'
		);
		this.importInstanceNameField = this.importInstanceFrame.getByLabel(
			'Name',
			{exact: true}
		);
		this.importInstanceSchemaNameField =
			this.importInstanceFrame.getByLabel('Schema Name');
		this.importInstanceSubmitButton = page
			.getByRole('dialog', {name: 'Import Instance'})
			.getByRole('button', {exact: true, name: 'Import'});
		this.importInstanceVirtualHostField =
			this.importInstanceFrame.getByLabel('Virtual Host');
		this.importInstanceWebIdField =
			this.importInstanceFrame.getByLabel('Web ID');
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
		this.newVirtualInstanceButton = page
			.locator('[data-qa-id="creationMenuNewButton"]')
			.filter({visible: true});
		this.page = page;
	}

	async addNewVirtualInstance(
		name: string,
		active = true,
		maxUsers = '0',
		virtualInstanceInitializer = ''
	) {
		await this.globalMenuPage.goToHome();
		await this.globalMenuPage.goToControlPanel('Virtual Instances');
		await this.clickAddInstance();

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

		await this.submitAddInstanceForm();

		// Only wait for Virtual Instance creation if there are no errors

		if (await this.errorMessage.isHidden()) {
			await expect(this.creationStartedMessage(name)).toBeVisible();

			await this.waitForCreationNotification(name);

			await this.goto();
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
		await this.clickAddInstance();

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

		await this.submitAddInstanceForm();

		await expect(this.errorMessageScreenName).toBeVisible();
		await expect(this.errorMessageEmailAddress).toBeVisible();
		await expect(this.errorMessagePassword).toBeVisible();

		await this.addInstanceScreenNameField.fill(screenName);
		await this.addInstanceEmailAddressField.fill(emailAddress);
		await this.addInstancePasswordField.fill(password);

		await this.submitAddInstanceForm();

		await expect(this.creationStartedMessage(name)).toBeVisible();

		await this.waitForCreationNotification(name);
	}

	private async clickAddInstance() {
		const addMenuItem = this.page.getByRole('menuitem', {
			exact: true,
			name: 'Add',
		});

		await clickAndExpectToBeVisible({
			target: addMenuItem.or(this.addInstanceAddButton),
			trigger: this.newVirtualInstanceButton,
		});

		if (await addMenuItem.isVisible()) {
			await addMenuItem.click();
		}
	}

	copyInstanceSuccessMessage(webId: string) {
		return this.page.getByText(`The instance was copied to ${webId}.`);
	}

	creationStartedMessage(name: string) {
		return this.page.getByText(
			`The instance ${name} is being created. You will be notified when it finishes.`
		);
	}

	deletionStartedMessage(name: string) {
		return this.page.getByText(
			`The instance ${name} is being deleted. You will be notified when it finishes.`
		);
	}

	exportStartedMessage(name: string) {
		return this.page.getByText(
			`The instance ${name} is being exported. You will be notified when it finishes.`
		);
	}

	async deleteVirtualInstance(name: string) {
		await this.globalMenuPage.goToControlPanel('Virtual Instances');

		const row = this.page.getByRole('row').filter({hasText: name});

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {name: 'Delete'}),
			trigger: row.getByRole('button', {name: 'Show Actions'}),
		});

		const confirmButton = this.page.getByRole('button', {name: 'Delete'});

		await confirmButton.waitFor();
		await confirmButton.click();

		await expect(this.deletionStartedMessage(name)).toBeVisible();

		await expect(row).toBeVisible();

		await this.waitForVirtualInstance(name, false);

		await this.goto();

		await expect(row).toBeHidden();
	}

	private async submitAddInstanceForm() {
		await Promise.all([
			this.page.waitForResponse(
				(response) => response.url().includes('add_instance'),
				{timeout: 180 * 1000}
			),
			this.addInstanceAddButton.click(),
		]);

		await this.page.waitForTimeout(1000);
	}

	async waitForCreationNotification(name: string) {
		const notificationsPage = new NotificationsPage(this.page);

		await expect(async () => {
			await notificationsPage.goto();

			await expect(
				notificationsPage.getNotificationByTitle(
					`The instance ${name} was created.`
				)
			).toBeVisible({timeout: 10 * 1000});
		}).toPass({timeout: 300 * 1000});
	}

	async waitForExportNotification(name: string) {
		const notificationsPage = new NotificationsPage(this.page);

		let schemaName = '';

		await expect(async () => {
			await notificationsPage.goto();

			const notification = notificationsPage.getNotificationByTitle(
				`The instance ${name} was exported.`
			);

			await expect(notification).toBeVisible({timeout: 10 * 1000});

			const body = await notification.innerText();

			const [, matchedSchemaName] =
				body.match(/schema\s+(lexported_\d+)/) || [];

			expect(matchedSchemaName).toBeTruthy();

			schemaName = matchedSchemaName;
		}).toPass({timeout: 300 * 1000});

		return schemaName;
	}

	async waitForVirtualInstance(name: string, exists: boolean) {
		const apiHelpers = new ApiHelpers(this.page);

		const headlessPortalInstance = apiHelpers.headlessPortalInstance;

		await expect
			.poll(
				async () => {
					const portalInstances =
						await headlessPortalInstance.getVirtualInstances();

					return portalInstances.some(
						(portalInstance) =>
							portalInstance.portalInstanceId === name
					);
				},
				{intervals: [1000], timeout: 180 * 1000}
			)
			.toBe(exists);
	}

	async startVirtualInstanceExport(name: string) {
		await this.goto();

		const row = this.page.getByRole('row').filter({hasText: name});

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {
				exact: true,
				name: 'Export',
			}),
			trigger: row.getByRole('button', {name: 'Show Actions'}),
		});

		await this.exportInstanceConfirmButton.click();

		await expect(this.exportStartedMessage(name)).toBeVisible();
	}

	async exportVirtualInstance(name: string) {
		await this.startVirtualInstanceExport(name);

		return this.waitForExportNotification(name);
	}

	async goto() {
		await this.globalMenuPage.goToControlPanel('Virtual Instances');
	}

	importInstanceSuccessMessage(webId: string) {
		return this.page.getByText(`The instance was imported to ${webId}.`);
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

	async openImportVirtualInstanceModal() {
		await this.goto();

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {
				exact: true,
				name: 'Import',
			}),
			trigger: this.newVirtualInstanceButton,
		});

		// Sometimes the frame loads slowly

		await this.page.waitForTimeout(1000);
	}

	async submitCopyVirtualInstance({
		destinationCompanyId,
		name,
		timeout = 30 * 1000,
		virtualHost,
		webId,
	}: {
		destinationCompanyId: string;
		name: string;
		timeout?: number;
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
				(response) =>
					response.url().includes('copy_db_partition_company'),
				{timeout}
			),
			this.copyInstanceSubmitButton.click(),
		]);

		await this.page.waitForTimeout(1000);
	}

	async submitImportVirtualInstance({
		name = '',
		schemaName,
		timeout = 30 * 1000,
		virtualHost = '',
		webId = '',
	}: {
		name?: string;
		schemaName: string;
		timeout?: number;
		virtualHost?: string;
		webId?: string;
	}) {
		await this.importInstanceSchemaNameField.fill(schemaName);
		await this.importInstanceNameField.fill(name);
		await this.importInstanceVirtualHostField.fill(virtualHost);
		await this.importInstanceWebIdField.fill(webId);

		await Promise.all([
			this.page.waitForResponse(
				(response) =>
					response.url().includes('add_db_partition_company'),
				{timeout}
			),
			this.importInstanceSubmitButton.click(),
		]);

		await this.page.waitForTimeout(1000);
	}
}
