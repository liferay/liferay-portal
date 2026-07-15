/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

export class DigitalSalesRoomSettingsPage {
	readonly externalReferenceCodeInput: Locator;
	readonly friendlyURLInput: Locator;
	readonly headerTitle: Locator;
	readonly nameInput: Locator;
	readonly page: Page;
	readonly saveButton: Locator;
	readonly siteIdValue: Locator;

	constructor(page: Page) {
		this.externalReferenceCodeInput = page.locator(
			'[name="externalReferenceCode"]'
		);
		this.friendlyURLInput = page.locator('[name="friendlyURL"]');
		this.headerTitle = page.locator('.top-bar h1');
		this.nameInput = page.locator('[name="name"]');
		this.page = page;
		this.saveButton = page.getByRole('button', {name: 'Save'});
		this.siteIdValue = page.locator('[name="siteId"]');
	}

	async updateRoomSettings({
		externalReferenceCode,
		friendlyURL,
		name,
	}: {
		externalReferenceCode: string;
		friendlyURL: string;
		name: string;
	}) {
		await this.nameInput.fill(name);
		await this.externalReferenceCodeInput.fill(externalReferenceCode);
		await this.friendlyURLInput.fill(friendlyURL);

		await this.saveButton.click();
	}
}
