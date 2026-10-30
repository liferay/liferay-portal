/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';

export class CommerceAdminChannelDetailsTypePage {
	readonly page: Page;
	readonly selectSiteButton: Locator;
	readonly selectSiteModal: Locator;
	readonly selectSiteModalSearchInput: Locator;
	readonly selectSiteModalSelectButton: Locator;
	readonly selectSiteModalSite: (siteName: string) => Locator;
	readonly typeLinkTab: Locator;

	constructor(page: Page) {
		this.page = page;
		this.selectSiteButton = page.getByRole('button', {name: 'Select Site'});
		this.selectSiteModal = page.getByRole('dialog', {name: 'Select Site'});
		this.selectSiteModalSearchInput = this.selectSiteModal.getByRole(
			'searchbox',
			{name: 'Search'}
		);
		this.selectSiteModalSelectButton = this.selectSiteModal.getByRole(
			'button',
			{exact: true, name: 'Select'}
		);
		this.selectSiteModalSite = (siteName: string) =>
			this.selectSiteModal.getByRole('radio', {
				exact: true,
				name: `Select ${siteName}`,
			});
		this.typeLinkTab = page.getByRole('link', {name: 'Type'});
	}

	async selectSite(siteName: string) {
		await clickAndExpectToBeVisible({
			target: this.selectSiteModal,
			trigger: this.selectSiteButton,
		});

		await this.selectSiteModalSearchInput.fill(siteName);
		await this.selectSiteModalSearchInput.press('Enter');

		await this.selectSiteModalSite(siteName).check();

		await this.selectSiteModalSelectButton.click();

		await expect(this.selectSiteModal).toBeHidden();
	}
}
