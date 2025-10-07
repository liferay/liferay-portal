/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {TSpConnection} from '../../helpers/SamlProviderConnectionHelper';
import {clickAndExpectToBeVisible} from '../../utils/clickAndExpectToBeVisible';
import {ApplicationsMenuPage} from '../product-navigation-applications-menu/ApplicationsMenuPage';

export class ServiceProviderConnectionsPage {
	readonly addServiceProviderConnectionButton: Locator;
	readonly applicationsMenuPage: ApplicationsMenuPage;
	readonly assertionLifetimeField: Locator;
	readonly attributesEnabledToggle: Locator;
	readonly attributesField: Locator;
	readonly attributesNamespaceEnabledToggle: Locator;
	readonly enabledField: Locator;
	readonly entityIdField: Locator;
	readonly forceEncryptionToggle: Locator;
	readonly keepAliveUrlField: Locator;
	readonly metadataUrlField: Locator;
	readonly nameField: Locator;
	readonly nameIdentifierAttributeNameField: Locator;
	readonly nameIdentifierFormatField: Locator;
	readonly page: Page;
	readonly saveButton: Locator;
	readonly serviceProviderConnectionsTab: Locator;
	readonly serviceProviderConnectionsTable: Locator;
	readonly successMessage: Locator;

	constructor(page: Page) {
		this.addServiceProviderConnectionButton = page.getByRole('button', {
			name: 'Add Service Provider',
		});
		this.applicationsMenuPage = new ApplicationsMenuPage(page);
		this.assertionLifetimeField = page.getByLabel('Assertion Lifetime');
		this.attributesEnabledToggle = page.getByText('Attributes Enabled', {
			exact: true,
		});
		this.attributesField = page
			.getByRole('group', {name: 'Attributes'})
			.getByRole('textbox');
		this.attributesNamespaceEnabledToggle = page.getByText(
			'Attributes Namespace Enabled'
		);
		this.enabledField = page.getByText('Enabled', {exact: true});
		this.entityIdField = page.getByLabel('Entity ID');
		this.forceEncryptionToggle = page.getByText('Force Encryption', {
			exact: true,
		});
		this.keepAliveUrlField = page
			.getByRole('group', {name: 'Keep Alive'})
			.getByRole('textbox');
		this.metadataUrlField = page.getByLabel('Metadata URL', {exact: true});
		this.nameField = page.getByLabel('Name').first();
		this.nameIdentifierAttributeNameField = page.getByLabel(
			'Name Identifier Attribute Name'
		);
		this.nameIdentifierFormatField = page.getByLabel(
			'Name Identifier Format'
		);
		this.page = page;
		this.saveButton = page.getByRole('button', {name: 'Save'});
		this.serviceProviderConnectionsTab = page.getByRole('tab', {
			name: 'Service Provider Connections',
		});
		this.serviceProviderConnectionsTable = page.locator(
			'#_com_liferay_saml_web_internal_portlet_SamlAdminPortlet_samlIdpSpConnectionsSearchContainer'
		);
		this.successMessage = page.getByText(
			'Your request completed successfully'
		);
	}

	async addServiceProviderConnection(
		spConnection: TSpConnection,
		deleteExistingConnection = true
	) {
		if (deleteExistingConnection) {
			const row = await this.page.getByRole('row').filter({
				hasText: spConnection.spName,
			});

			if (await row.isVisible()) {
				await this._deleteServiceProviderConnection(
					spConnection.spName
				);
			}
		}

		await this.addServiceProviderConnectionButton.click();

		await this.populateAndSaveServiceProviderConnectionDetails(
			spConnection
		);
	}

	async deleteServiceProviderConnection(name: string) {
		await this._deleteServiceProviderConnection(name);
	}

	async deleteServiceProviderConnections() {
		await this.page.waitForTimeout(1000);

		const row = await this.serviceProviderConnectionsTable
			.getByRole('row')
			.last();

		while (await row.isVisible()) {
			this.page.once('dialog', (dialog) => {
				dialog.accept();
			});

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: this.page.getByRole('link', {name: 'Delete'}),
				trigger: row.locator('.dropdown-toggle'),
			});

			await expect(await this.successMessage).toBeVisible();

			// Prevent the above expect from passing due to previous success

			await this.page.getByLabel('Close').click();
		}
	}

	async editServiceProviderConnection(spConnection: TSpConnection) {
		const row = await this.page.getByRole('row').filter({
			hasText: spConnection.spName,
		});

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {name: 'Edit'}),
			trigger: row.locator('.dropdown-toggle'),
		});

		await this.populateAndSaveServiceProviderConnectionDetails(
			spConnection
		);
	}

	async goTo(forceReload = false) {
		if (
			forceReload ||
			(await this.serviceProviderConnectionsTab.isHidden())
		) {
			await this.applicationsMenuPage.goToSamlAdmin(forceReload);
		}

		await this.serviceProviderConnectionsTab.click();
		await expect(
			await this.addServiceProviderConnectionButton
		).toBeVisible();
	}

	private async _deleteServiceProviderConnection(name: string) {
		this.page.once('dialog', (dialog) => {
			dialog.accept();
		});

		const row = await this.page.getByRole('row').filter({hasText: name});

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('link', {name: 'Delete'}),
			trigger: row.locator('.dropdown-toggle'),
		});

		await expect(await this.successMessage).toBeVisible();

		await this.page.getByLabel('Close').click();
	}

	private async populateAndSaveServiceProviderConnectionDetails(
		spConnection: TSpConnection
	) {
		await this.nameField.fill(spConnection.spName);

		if (spConnection.assertionLifetime) {
			await this.assertionLifetimeField.fill(
				spConnection.assertionLifetime
			);
		}

		if (spConnection.attributes) {
			await this.attributesField.fill(spConnection.attributes);
		}

		if (spConnection.attributesEnabled !== undefined) {
			await this.attributesEnabledToggle.setChecked(
				spConnection.attributesEnabled
			);
		}

		if (spConnection.attributesNamespaceEnabled !== undefined) {
			await this.attributesNamespaceEnabledToggle.setChecked(
				spConnection.attributesNamespaceEnabled
			);
		}

		if (spConnection.enabled !== undefined) {
			await this.enabledField.setChecked(spConnection.enabled);
		}

		if (spConnection.entityId) {
			await this.entityIdField.fill(spConnection.entityId);
		}

		if (spConnection.forceEncrytion !== undefined) {
			await this.forceEncryptionToggle.setChecked(
				spConnection.forceEncrytion
			);
		}

		if (spConnection.keepAliveUrl) {
			await this.keepAliveUrlField.fill(spConnection.keepAliveUrl);
		}

		if (spConnection.metadataURL) {
			await this.metadataUrlField.fill(spConnection.metadataURL);
		}

		if (spConnection.nameIdentifierAttributeName) {
			await this.nameIdentifierAttributeNameField.fill(
				spConnection.nameIdentifierAttributeName
			);
		}

		if (spConnection.nameIdentifierFormat) {
			await this.nameIdentifierFormatField.selectOption(
				spConnection.nameIdentifierFormat
			);
		}

		await this.saveButton.click();
		await expect(await this.successMessage).toBeVisible();
		await this.page.getByLabel('Close').click();
	}
}
