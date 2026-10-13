/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page, expect} from '@playwright/test';

import {waitForAlert} from '../../utils/waitForAlert';

export class DepotSitesPage {
	readonly page: Page;

	readonly addButton: Locator;
	readonly noConnectedSitesMessage: Locator;
	readonly selectSiteFrame: FrameLocator;

	constructor(page: Page) {
		this.page = page;

		this.addButton = page.getByRole('button', {exact: true, name: 'Add'});
		this.noConnectedSitesMessage = page.getByText(
			'No sites are connected yet.'
		);
		this.selectSiteFrame = page.frameLocator('iframe[title="Select Site"]');
	}

	async connectSite(siteName: string) {
		await this.openSelectSite();

		await this.selectSiteFrame
			.getByRole('link', {exact: true, name: 'All Sites'})
			.click();

		// Child sites are only listed by a search

		await this.selectSiteFrame
			.getByRole('searchbox', {name: 'Search for:'})
			.fill(siteName);

		await this.selectSiteFrame
			.getByRole('searchbox', {name: 'Search for:'})
			.press('Enter');

		await this.selectSiteFrame
			.getByRole('link', {exact: true, name: siteName})
			.click();

		await waitForAlert(this.page);

		await expect(this.getSiteRow(siteName)).toBeVisible();
	}

	async disconnectSite(siteName: string) {
		await this.getSiteRow(siteName).getByRole('button').click();

		let confirmMessage = '';

		this.page.once('dialog', async (dialog) => {
			confirmMessage = dialog.message();

			await dialog.accept();
		});

		await this.page.getByRole('menuitem', {name: 'Disconnect'}).click();

		await waitForAlert(this.page);

		return confirmMessage;
	}

	getSiteRow(siteName: string) {
		return this.page.getByRole('row').filter({
			has: this.page.getByRole('cell', {exact: true, name: siteName}),
		});
	}

	async goto(depotEntryId: number | string) {
		await this.page.goto(
			`/group/asset-library-${depotEntryId}/~/control_panel/manage?p_p_id=com_liferay_depot_web_portlet_DepotSettingsPortlet&_com_liferay_depot_web_portlet_DepotSettingsPortlet_screenNavigationEntryKey=sites`
		);
	}

	async openSelectSite() {
		await this.addButton.click();

		await expect(
			this.selectSiteFrame.getByRole('link', {
				exact: true,
				name: 'Recent',
			})
		).toBeVisible();
	}
}
