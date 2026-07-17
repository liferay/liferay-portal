/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../utils/clickAndExpectToBeVisible';
import {GlobalMenuPage} from '../product-navigation-applications-menu/GlobalMenuPage';

export class AuthServerLocalMetadatasPage {
	readonly addOAuthAuthorizationServerButton: Locator;
	readonly globalMenuPage: GlobalMenuPage;
	readonly authServerLocalMetadataTab: Locator;
	readonly authorizationEndpoint: Locator;
	readonly enabledField: Locator;
	readonly grantTypes: Locator;
	readonly issuer: Locator;
	readonly jwkUri: Locator;
	readonly oAuthAuthorizatoinServerTab: Locator;
	readonly oAuthAuthorizatoinServerTable: Locator;
	readonly openIdConfigurationTab: Locator;
	readonly page: Page;
	readonly saveButton: Locator;
	readonly subjectTypesSupported: Locator;
	readonly supportedScopes: Locator;
	readonly successMessage: Locator;
	readonly tokenEndpoint: Locator;
	readonly userinfoEnpoint: Locator;
	readonly urlErrorMessage: Locator;

	constructor(page: Page) {
		this.addOAuthAuthorizationServerButton = page.getByRole('link', {
			name: 'Add OAuth Authorization',
		});
		this.globalMenuPage = new GlobalMenuPage(page);
		this.authServerLocalMetadataTab = page.getByRole('link', {
			name: 'Auth Server Local Metadata',
		});
		this.authorizationEndpoint = page.getByLabel('Authorization Endpoint');
		this.enabledField = page.getByText('Enabled', {exact: true});
		this.issuer = page.getByLabel('Issuer Required Issuer must');
		this.grantTypes = page.getByLabel('Grant Types');
		this.jwkUri = page.getByLabel('JWK URI');
		this.oAuthAuthorizatoinServerTab = page.getByRole('link', {
			exact: true,
			name: 'OAuth Authorization Server',
		});
		this.oAuthAuthorizatoinServerTable = page.locator(
			'#_com_liferay_oauth_client_admin_web_internal_portlet_OAuthClientAdminPortlet_oAuthClientASLocalMetadataSearchContainer'
		);
		this.openIdConfigurationTab = page.getByRole('link', {
			exact: true,
			name: 'OpenId Configuration',
		});
		this.page = page;
		this.saveButton = page.getByRole('button', {name: 'Save'});
		this.subjectTypesSupported = page.getByLabel('Subject Types Supported');
		this.successMessage = page.getByText(
			'Your request completed successfully'
		);
		this.supportedScopes = page.getByLabel('Supported Scopes');
		this.tokenEndpoint = page.getByLabel('Token Endpoint');
		this.userinfoEnpoint = page.getByLabel('Userinfo enpoint');
		this.urlErrorMessage = page.getByText('Close Error: The URL is not a');
	}

	async addAuthServerLocalMetadata(
		issuer: string,
		{
			expectedMessage,
			supportedScopes,
		}: {expectedMessage?: string; supportedScopes?: string} = {}
	) {
		await this.addOAuthAuthorizationServerButton.click();

		await this.issuer.fill(issuer);

		if (supportedScopes !== undefined) {
			await this.supportedScopes.fill(supportedScopes);
		}

		await this.saveButton.click();

		if (expectedMessage !== undefined) {
			await expect(
				await this.page.getByText(expectedMessage)
			).toBeVisible();

			if (
				await this.page
					.locator('#ToastAlertContainer')
					.getByLabel('Close')
					.isVisible()
			) {
				await this.page
					.locator('#ToastAlertContainer')
					.getByLabel('Close')
					.click();
			}

			await this.page.getByRole('button', {name: 'Cancel'}).click();
		}
		else {
			await expect(await this.successMessage).toBeVisible();
			await this.page.locator('.alert').getByLabel('Close').click();
		}
	}

	async deleteAuthServerLocalMetadata() {
		await this.oAuthAuthorizatoinServerTable.waitFor();

		const row = await this.oAuthAuthorizatoinServerTable
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

			await this.page.locator('.alert').getByLabel('Close').click();
		}
	}

	async goTo() {
		if (await this.oAuthAuthorizatoinServerTab.isHidden()) {
			await this.globalMenuPage.goToControlPanel(
				'OAuth Client Administration'
			);
		}

		await this.authServerLocalMetadataTab.click();

		await this.oAuthAuthorizatoinServerTab.click();

		await expect(
			await this.addOAuthAuthorizationServerButton
		).toBeVisible();
	}

	async openAuthServerLocalMetadata(wellKnownURI: string) {
		await clickAndExpectToBeVisible({
			target: this.supportedScopes,
			trigger: this.page.getByRole('link', {name: wellKnownURI}),
		});
	}
}
