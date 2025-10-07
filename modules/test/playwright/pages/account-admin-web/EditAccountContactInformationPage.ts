/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

export class EditAccountContactInformationPage {
	readonly addEmailAddressesButton: Locator;
	readonly addPhoneNumbersButton: Locator;
	readonly addWebsitesButton: Locator;
	readonly facebookInput: Locator;
	readonly jabberInput: Locator;
	readonly page: Page;
	readonly phoneExtensionHeader: Locator;
	readonly phoneNumberHeader: Locator;
	readonly saveButton: Locator;
	readonly skypeInput: Locator;
	readonly smsInput: Locator;
	readonly twitterInput: Locator;

	constructor(page: Page) {
		this.addEmailAddressesButton = page.getByLabel('Add Email Addresses');
		this.addPhoneNumbersButton = page.getByLabel('Add Phone Numbers');
		this.addWebsitesButton = page.getByLabel('Add Websites');
		this.facebookInput = page.getByLabel('Facebook');
		this.jabberInput = page.getByLabel('Jabber');
		this.page = page;
		this.phoneExtensionHeader = page.getByRole('cell', {
			exact: true,
			name: 'Phone Extension',
		});
		this.phoneNumberHeader = page.getByRole('cell', {
			exact: true,
			name: 'Phone Number',
		});
		this.saveButton = page.getByRole('button', {name: 'Save'});
		this.skypeInput = page.getByLabel('Skype');
		this.smsInput = page.getByLabel('SMS');
		this.twitterInput = page.getByLabel('Twitter');
	}

	async updateContactInformation(
		facebookInput: string,
		jabberInput: string,
		skypeInput: string,
		smsInput: string,
		twitterInput: string
	) {
		await this.facebookInput.fill(facebookInput);
		await this.jabberInput.fill(jabberInput);
		await this.skypeInput.fill(skypeInput);
		await this.smsInput.fill(smsInput);
		await this.twitterInput.fill(twitterInput);
		await this.saveButton.click();
	}
}
