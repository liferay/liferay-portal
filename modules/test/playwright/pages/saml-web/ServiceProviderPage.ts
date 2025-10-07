/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {ApplicationsMenuPage} from '../product-navigation-applications-menu/ApplicationsMenuPage';

export class ServiceProviderPage {
	readonly allowShowingTheLoginPortlet: Locator;
	readonly applicationsMenuPage: ApplicationsMenuPage;
	readonly clockSkew: Locator;
	readonly ldapImportEnabled: Locator;
	readonly page: Page;
	readonly requireAssertionSignature: Locator;
	readonly saveButton: Locator;
	readonly signAuthnRequests: Locator;
	readonly signMetadata: Locator;
	readonly sslRequired: Locator;
	readonly successMessage: Locator;

	constructor(page: Page) {
		this.allowShowingTheLoginPortlet = page.getByText(
			'Allow showing the login portlet'
		);
		this.applicationsMenuPage = new ApplicationsMenuPage(page);
		this.clockSkew = page.getByLabel('Clock Skew');
		this.ldapImportEnabled = page.getByText('LDAP Import Enabled');
		this.page = page;
		this.requireAssertionSignature = page.getByText(
			'Require Assertion Signature'
		);
		this.saveButton = page.getByRole('button', {name: 'Save'});
		this.signAuthnRequests = page.getByLabel('Sign Authn Requests');
		this.signMetadata = page.getByText('Sign Metadata?');
		this.sslRequired = page.getByText('SSL Required');
		this.successMessage = page.getByText(
			'Your request completed successfully'
		);
	}

	async goTo(forceReload = false) {
		await this.applicationsMenuPage.goToSamlAdmin(forceReload);
		await this.page
			.getByRole('tab', {exact: true, name: 'Service Provider'})
			.click();

		await this.signMetadata.waitFor();
	}
}
