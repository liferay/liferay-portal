/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page, expect} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';
import {PORTLET_URLS} from '../../../../utils/portletUrls';
import {waitForAlert} from '../../../../utils/waitForAlert';

export class SitesAdminPage {
	readonly page: Page;

	private readonly addSiteIFrame: FrameLocator;
	private readonly blankSiteButton: Locator;
	private readonly searchButton: Locator;
	private readonly searchInput: Locator;

	constructor(page: Page) {
		this.page = page;

		this.addSiteIFrame = this.page.frameLocator('iframe[title="Add Site"]');

		this.blankSiteButton = this.page.getByRole('button', {
			name: 'Select Template: Blank Site',
		});
		this.searchButton = this.page.getByLabel('Search for', {exact: true});
		this.searchInput = this.page.getByPlaceholder('Search for');
	}

	async addBlankSite(siteName: string, closeModal: boolean = false) {
		await this.blankSiteButton.click();

		await this.addSiteIFrame.getByLabel('Name').fill(siteName);

		await this.addSiteIFrame.getByRole('button', {name: 'Add'}).click();

		await expect(
			this.addSiteIFrame.getByText(
				'The creation of the site may take some time. Closing the window will not cancel the process.'
			)
		).toBeVisible();

		if (closeModal) {
			await this.page.locator('.modal').getByLabel('Close').click();

			await expect(
				this.page.getByRole('heading', {name: 'Provided by Liferay'})
			).toBeVisible();

			// Wait for site to finish being created

			await this.page.waitForTimeout(1000);
		}
		else {
			await waitForAlert(
				this.page,
				'Success: Site was successfully added.'
			);
		}
	}

	async addChildSite(childSiteName: string, parentSiteName: string) {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {
				name: 'Add Child Site',
			}),
			trigger: this.page
				.getByRole('row', {name: parentSiteName})
				.getByLabel('Show Actions'),
		});

		await this.addBlankSite(childSiteName);
	}

	async deactivateSite(siteName: string) {
		this.page.once('dialog', async (dialog) => {
			await dialog.accept();
		});

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {
				name: 'Deactivate',
			}),
			trigger: this.page
				.getByRole('row', {name: siteName})
				.getByLabel('Show Actions'),
		});

		await waitForAlert(this.page);
	}

	async assertActions(
		siteName: string,
		allowedActions = [],
		disallowedActions = []
	) {
		await this.page
			.getByRole('row', {name: siteName})
			.getByLabel('Show Actions')
			.click();

		for (const allowedAction of allowedActions) {
			await expect(
				this.page.getByRole('menuitem', {name: allowedAction})
			).toBeVisible();
		}

		for (const disallowedAction of disallowedActions) {
			await expect(
				this.page.getByRole('menuitem', {name: disallowedAction})
			).not.toBeVisible();
		}

		await this.page.keyboard.press('Escape');
	}

	async deleteSite(siteName: string) {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {
				name: 'Delete',
			}),
			trigger: this.page
				.getByRole('row', {name: siteName})
				.getByLabel('Show Actions'),
		});

		await this.page.locator('.modal-footer').getByText('Delete').click();

		await waitForAlert(this.page);
	}

	async goto(siteUrl?: Site['friendlyUrlPath']) {
		await this.page.goto(
			`/group${siteUrl || '/guest'}${PORTLET_URLS.sites}`
		);
	}

	async searchSite(keywords: string) {
		await this.searchInput.click();
		await this.searchInput.clear();
		await this.searchInput.fill(keywords);

		await this.searchButton.click();

		await this.page.getByText('Search Results').waitFor();
	}

	async viewChildSites(parentSiteName: string) {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {
				name: 'View Child Sites',
			}),
			trigger: this.page
				.getByRole('row', {name: parentSiteName})
				.getByLabel('Show Actions'),
		});
	}
}
