/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {TIdpConnection} from '../../helpers/SamlProviderConnectionHelper';
import {clickAndExpectToBeVisible} from '../../utils/clickAndExpectToBeVisible';
import {ApplicationsMenuPage} from '../product-navigation-applications-menu/ApplicationsMenuPage';

export interface AttributeMapping {
	attributeMappingType:
		| 'Basic User Fields'
		| 'User Custom Fields'
		| 'User Memberships';
	samlAttribute: string;
	useToMatchUsers?: boolean;
	userFieldExpression: string;
}

export class IdentityProviderConnectionsPage {
	readonly addIdentityProviderConnectionButton: Locator;
	readonly applicationsMenuPage: ApplicationsMenuPage;
	readonly basicUserFields: Locator;
	readonly clockSkewField: Locator;
	readonly enabledField: Locator;
	readonly entityIdField: Locator;
	readonly forceAuthnToggle: Locator;
	readonly identityProviderConnectionsTab: Locator;
	readonly identityProviderConnectionsTable: Locator;
	readonly keepAliveUrlField: Locator;
	readonly metadataUrlField: Locator;
	readonly nameField: Locator;
	readonly nameIdentifierFormatField: Locator;
	readonly page: Page;
	readonly saveButton: Locator;
	readonly successMessage: Locator;
	readonly unknownUsersAreStrangersToggle: Locator;
	readonly userCustomFields: Locator;
	readonly userMembershipsFields: Locator;

	constructor(page: Page) {
		this.addIdentityProviderConnectionButton = page.getByRole('button', {
			name: 'Add Identity Provider',
		});
		this.applicationsMenuPage = new ApplicationsMenuPage(page);
		this.basicUserFields = page.getByText('Basic User Fields');
		this.clockSkewField = page.getByLabel('Clock Skew');
		this.enabledField = page.getByText('Enabled', {exact: true});
		this.entityIdField = page.getByLabel('Entity ID');
		this.forceAuthnToggle = page.getByText('Force Authn');
		this.identityProviderConnectionsTab = page.getByRole('tab', {
			name: 'Identity Provider Connections',
		});
		this.identityProviderConnectionsTable = page.locator(
			'#_com_liferay_saml_web_internal_portlet_SamlAdminPortlet_samlSpIdpConnectionsSearchContainer'
		);
		this.keepAliveUrlField = page
			.getByRole('group', {name: 'Keep Alive'})
			.getByRole('textbox');
		this.metadataUrlField = page.getByLabel('Metadata URL', {exact: true});
		this.nameField = page.getByLabel('Name').first();
		this.nameIdentifierFormatField = page.getByLabel(
			'Name Identifier Format'
		);
		this.page = page;
		this.saveButton = page.getByRole('button', {name: 'Save'});
		this.successMessage = page.getByText(
			'Your request completed successfully'
		);
		this.unknownUsersAreStrangersToggle = page.getByText(
			'Unknown Users Are Strangers'
		);
		this.userCustomFields = page.getByText('User Custom Fields');
		this.userMembershipsFields = page.getByText('User Memberships');
	}

	async addIdentityProviderConnection(
		idpConnection: TIdpConnection,
		deleteExistingConnection = true
	) {
		if (deleteExistingConnection) {
			const row = await this.page.getByRole('row').filter({
				hasText: idpConnection.idpName,
			});

			if (await row.isVisible()) {
				await this._deleteIdentityProviderConnection(
					idpConnection.idpName
				);
			}
		}

		await this.addIdentityProviderConnectionButton.click();

		await this.populateAndSaveIdentityProviderConnectionDetails(
			idpConnection
		);
	}

	async deleteIdentityProviderConnection(name: string) {
		await this._deleteIdentityProviderConnection(name);
	}

	async deleteIdentityProviderConnections() {
		await this.page.waitForTimeout(1000);

		const row = await this.identityProviderConnectionsTable
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

	async editIdentityProviderConnection(
		idpConnection: TIdpConnection,
		expectedMessage?: string
	) {
		const row = await this.page.getByRole('row').filter({
			hasText: idpConnection.idpName,
		});

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {name: 'Edit'}),
			trigger: row.locator('.dropdown-toggle'),
		});

		await this.populateAndSaveIdentityProviderConnectionDetails(
			idpConnection,
			expectedMessage
		);
	}

	async goTo(forceReload = false) {
		if (
			forceReload ||
			(await this.identityProviderConnectionsTab.isHidden())
		) {
			await this.applicationsMenuPage.goToSamlAdmin(forceReload);
		}

		await this.identityProviderConnectionsTab.click();
		await expect(
			await this.addIdentityProviderConnectionButton
		).toBeVisible();
	}

	async _deleteIdentityProviderConnection(name: string) {
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

	private async populateAndSaveIdentityProviderConnectionDetails(
		idpConnection: TIdpConnection,
		expectedMessage?: string
	) {
		await this.nameField.fill(idpConnection.idpName);

		if (idpConnection.clockSkew) {
			await this.clockSkewField.fill(idpConnection.clockSkew);
		}

		if (idpConnection.enabled !== undefined) {
			await this.enabledField.setChecked(idpConnection.enabled);
		}

		if (idpConnection.entityId) {
			await this.entityIdField.fill(idpConnection.entityId);
		}

		if (idpConnection.forceAuthn !== undefined) {
			await this.forceAuthnToggle.setChecked(idpConnection.forceAuthn);
		}

		if (idpConnection.keepAliveUrl) {
			await this.keepAliveUrlField.fill(idpConnection.keepAliveUrl);
		}

		if (idpConnection.metadataURL) {
			await this.metadataUrlField.fill(idpConnection.metadataURL);
		}

		if (idpConnection.nameIdentifierFormat) {
			await this.nameIdentifierFormatField.selectOption(
				idpConnection.nameIdentifierFormat
			);
		}

		if (idpConnection.unknownUsersAreStrangers !== undefined) {
			await this.unknownUsersAreStrangersToggle.setChecked(
				idpConnection.unknownUsersAreStrangers
			);
		}

		if (idpConnection.userResolution !== undefined) {
			if (idpConnection.userResolution === 'attribute') {
				await this.page
					.getByText('Match Using a Specific SAML')
					.setChecked(true);
			}
			else if (idpConnection.userResolution === 'dynamic') {
				await this.page
					.getByText('Match a User Field Chosen')
					.setChecked(true);
			}
			else {
				await this.page.getByText('No Matching').setChecked(true);
			}
		}

		if (idpConnection.attributeMappings) {
			for (const attributeMapping of idpConnection.attributeMappings) {
				const attributeMappingLocator = this.page.getByText(
					`${attributeMapping.attributeMappingType} Undo`
				);

				// Always add a new row so we don't overwrite existing entries

				await attributeMappingLocator
					.getByRole('button', {name: 'Add'})
					.last()
					.click();

				await attributeMappingLocator
					.getByText('SAML Attribute')
					.last()
					.fill(attributeMapping.samlAttribute);

				await attributeMappingLocator
					.getByText('User Field Expression')
					.last()
					.selectOption(attributeMapping.userFieldExpression);

				await attributeMappingLocator
					.getByText('Use to Match Users')
					.last()
					.setChecked(attributeMapping.useToMatchUsers === true);
			}
		}

		await this.saveButton.click();

		if (expectedMessage !== undefined) {
			await expect(
				await this.page.getByText(expectedMessage)
			).toBeVisible();
		}
		else {
			await expect(await this.successMessage).toBeVisible();

			await this.page.getByLabel('Close').click();
		}
	}
}
