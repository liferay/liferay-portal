/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../utils/getRandomString';
import {waitForAlert} from '../../utils/waitForAlert';
import {InstanceSettingsPage} from '../configuration-admin-web/InstanceSettingsPage';

export class OpenIdInstanceSettingsPage {
	readonly page: Page;
	readonly instanceSettingsPage: InstanceSettingsPage;
	readonly openIdConnectMenuItem: Locator;
	readonly enabledCheckbox: Locator;
	readonly saveButton: Locator;
	readonly openIDConnectProviderConnection: Locator;
	readonly addButton: Locator;
	readonly providerNameField: Locator;
	readonly discoveryEndpointField: Locator;
	readonly openIDConnectClientIDField: Locator;
	readonly openIDConnectClientSecret: Locator;

	constructor(page: Page) {
		this.page = page;
		this.instanceSettingsPage = new InstanceSettingsPage(page);
		this.openIdConnectMenuItem = page.getByRole('menuitem', {
			exact: true,
			name: 'OpenID Connect',
		});
		this.enabledCheckbox = page.getByText(' Enabled ');
		this.saveButton = page.getByRole('button', {name: 'Save'});
		this.openIDConnectProviderConnection = page.getByRole('menuitem', {
			name: 'OpenID Connect Provider Connection',
		});
		this.addButton = page.getByRole('link', {name: 'Add'});
		this.providerNameField = page.getByLabel('Provider Name');
		this.discoveryEndpointField = page.getByLabel(
			'Discovery Endpoint Set the'
		);
		this.openIDConnectClientIDField = page.getByLabel(
			'OpenID Connect Client ID'
		);
		this.openIDConnectClientSecret = page.getByLabel(
			'OpenID Connect Client Secret'
		);
	}

	async goto() {
		await this.instanceSettingsPage.goToSSO();
	}

	async clickSetupOpenIdConnectionMenuItem() {
		await this.openIdConnectMenuItem.click();
	}

	async clickOpenIDConnectProviderConnectionMenuItem() {
		await this.openIDConnectProviderConnection.click();
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

	async AddOpenIDConnectProviderConnectionConfiguration(
		providerName: string,
		openIdProvider: string
	) {
		await this.clickOpenIDConnectProviderConnectionMenuItem();
		await this.addButton.click();
		await this.providerNameField.fill(providerName);
		await this.discoveryEndpointField.fill(openIdProvider);
		await this.openIDConnectClientIDField.fill(getRandomString());
		await this.openIDConnectClientSecret.fill(getRandomString());
		await this.saveButton.click();
		await waitForAlert(this.page);
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
