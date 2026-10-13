/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';
import {PORTLET_URLS} from '../../../../utils/portletUrls';
import {DataSetPage} from '../../../site-cms-site-initializer/main/pages/DataSetPage';

export class ProductsPage {
	readonly dataSetFragmentPage: DataSetPage;
	readonly deleteConfirmationButton: Locator;
	readonly generalTab: Locator;
	readonly loadingAnimation: Locator;
	readonly newButton: Locator;
	readonly page: Page;
	readonly recycleBinLink: Locator;
	readonly saveButton: Locator;
	readonly searchInput: Locator;
	readonly spaceDialog: Locator;
	readonly spaceSelect: Locator;
	readonly successAlert: Locator;
	readonly undoButton: Locator;

	constructor(page: Page) {
		this.dataSetFragmentPage = new DataSetPage(page);
		this.deleteConfirmationButton = page
			.locator('.modal-content')
			.getByRole('button', {name: 'Delete'});
		this.generalTab = page.getByRole('tab', {name: 'General'});
		this.loadingAnimation = page.locator('.loading-animation').nth(0);
		this.newButton = page.getByTestId('fdsCreationActionButton');
		this.page = page;
		this.recycleBinLink = page.getByRole('link', {name: 'Recycle Bin'});
		this.saveButton = page.getByRole('button', {name: 'Save'});
		this.searchInput = page.getByRole('searchbox', {name: 'Search'});
		this.spaceDialog = page.getByRole('dialog');
		this.spaceSelect = this.spaceDialog.getByLabel('Space');
		this.successAlert = page.locator('.alert-success');
		this.undoButton = page.getByRole('button', {name: 'Undo'});
	}

	async deleteProduct(name: string) {
		await this.dataSetFragmentPage.execItemAction({
			action: 'Delete',
			filter: name,
		});

		await this.deleteConfirmationButton
			.or(this.successAlert)
			.first()
			.waitFor();

		if (await this.deleteConfirmationButton.isVisible()) {
			await this.deleteConfirmationButton.click();
		}

		await this.getProduct(name).waitFor({state: 'hidden'});
	}

	getProduct(name: string) {
		return this.dataSetFragmentPage.getRow(name).getByRole('link', {name});
	}

	getSpaceOption(space: string) {
		return this.page.getByRole('option', {name: space});
	}

	async goto() {
		await this.page.goto(PORTLET_URLS.pimProducts);

		await this.newButton.waitFor({state: 'visible'});
	}

	async openNewProductEditor(space: string = 'Default', type?: string) {
		if (type) {
			const menuItem = this.page.getByRole('menuitem', {
				exact: true,
				name: type,
			});

			await expect(async () => {
				if (!(await menuItem.isVisible())) {
					await this.newButton.click({timeout: 1000});
				}

				await expect(menuItem).toBeVisible({timeout: 2000});
			}).toPass({timeout: 20000});

			await menuItem.click();
		}
		else {
			await this.newButton.click();
		}

		const shown = await Promise.race([
			this.generalTab
				.waitFor({state: 'visible'})
				.then(() => 'contentEditor'),
			this.spaceDialog
				.waitFor({state: 'visible'})
				.then(() => 'spaceSelector'),
		]);

		if (shown === 'spaceSelector') {
			await clickAndExpectToBeVisible({
				autoClick: true,
				target: this.getSpaceOption(space),
				trigger: this.spaceSelect,
			});

			await this.saveButton.click();

			await this.generalTab.waitFor();
		}

		await this.loadingAnimation.waitFor({state: 'hidden'});
	}

	async search(value: string) {
		await this.searchInput.fill(value);

		await this.searchInput.press('Enter');

		await expect(
			this.page.locator('.search-resume-label', {
				has: this.page.locator('strong', {hasText: value}),
			})
		).toBeVisible();
	}

	async selectProducts(names: string[]) {
		for (const name of names) {
			await this.dataSetFragmentPage
				.getRow(name)
				.getByRole('checkbox')
				.check();
		}
	}

	async openProductEditor(name: string) {
		await this.getProduct(name).click();

		await this.generalTab.waitFor();

		await this.loadingAnimation.waitFor({state: 'hidden'});
	}
}
