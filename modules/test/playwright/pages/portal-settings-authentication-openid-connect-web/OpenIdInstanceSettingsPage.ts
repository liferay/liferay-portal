/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {CustomClaim} from '../../tests/openid-link/main/helpers/CustomClaimHelper';
import {clickAndExpectToBeVisible} from '../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../utils/getRandomString';
import {waitForAlert} from '../../utils/waitForAlert';
import {InstanceSettingsPage} from '../configuration-admin-web/InstanceSettingsPage';

export class OpenIdInstanceSettingsPage {
	readonly addButton: Locator;
	readonly customClaimField: Locator;
	readonly discoveryEndpointField: Locator;
	readonly enabledCheckbox: Locator;
	readonly instanceSettingsPage: InstanceSettingsPage;
	readonly openIDConnectClientIDField: Locator;
	readonly openIDConnectClientSecret: Locator;
	readonly openIdConnectMenuItem: Locator;
	readonly openIDConnectProviderConnection: Locator;
	readonly page: Page;
	readonly providerNameField: Locator;
	readonly saveButton: Locator;
	readonly userCustomFieldsSelect: Locator;

	constructor(page: Page) {
		this.addButton = page.getByRole('link', {name: 'Add'});
		this.customClaimField = page.getByLabel('Custom Claim');
		this.discoveryEndpointField = page.getByLabel(
			'Discovery Endpoint Set the'
		);
		this.enabledCheckbox = page.getByText(' Enabled ');
		this.instanceSettingsPage = new InstanceSettingsPage(page);
		this.openIDConnectClientIDField = page.getByLabel(
			'OpenID Connect Client ID'
		);
		this.openIDConnectClientSecret = page.getByLabel(
			'OpenID Connect Client Secret'
		);
		this.openIdConnectMenuItem = page.getByRole('menuitem', {
			exact: true,
			name: 'OpenID Connect',
		});
		this.openIDConnectProviderConnection = page.getByRole('menuitem', {
			name: 'OpenID Connect Provider Connection',
		});
		this.page = page;
		this.providerNameField = page.getByLabel('Provider Name');
		this.saveButton = page.getByRole('button', {name: 'Save'});
		this.userCustomFieldsSelect = page.getByLabel('User Custom Fields');
	}

	async addOpenIDConnectProviderConnectionConfiguration(
		providerName: string,
		openIdProvider: string,
		customClaim?: CustomClaim
	) {
		await this.clickOpenIDConnectProviderConnectionMenuItem();
		await this.addButton.click();
		await this.providerNameField.fill(providerName);
		await this.discoveryEndpointField.fill(openIdProvider);
		await this.openIDConnectClientIDField.fill(getRandomString());
		await this.openIDConnectClientSecret.fill(getRandomString());

		if (customClaim) {
			await this.userCustomFieldsSelect.selectOption({
				label: customClaim.expandoColumnName,
			});
			await this.customClaimField.fill(
				customClaim.oidcProviderCustomClaim
			);
		}

		await this.saveButton.click();
		await waitForAlert(this.page);
	}

	async clickOpenIDConnectProviderConnectionMenuItem() {
		await this.openIDConnectProviderConnection.click();
	}

	async clickSetupOpenIdConnectionMenuItem() {
		await this.openIdConnectMenuItem.click();
	}

	async disableOpenIDConnect() {
		await this.clickSetupOpenIdConnectionMenuItem();
		await this.enabledCheckbox.uncheck();
		await this.saveButton.click();
		await waitForAlert(this.page);
	}

	async enableOpenIDConnect() {
		await this.clickSetupOpenIdConnectionMenuItem();
		await this.enabledCheckbox.check();
		await this.saveButton.click();
		await waitForAlert(this.page);
	}

	async goto() {
		await this.instanceSettingsPage.goToSSO();
	}

	async removeOpenIDConnectProviderConnectionConfiguration(
		providerName: string
	) {
		await this.clickOpenIDConnectProviderConnectionMenuItem();
		await this.page.waitForLoadState('networkidle');
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('link', {
				name: 'Delete',
			}),
			trigger: this.page
				.getByRole('row', {
					name: providerName,
				})
				.locator('div')
				.first()
				.locator('a')
				.first(),
		});
		await waitForAlert(this.page);
	}
}
