/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';
import {PORTLET_URLS} from '../../../../utils/portletUrls';
import {DataSetPage} from '../../../site-cms-site-initializer/main/pages/DataSetPage';

export class ProductsPage {
	readonly dataSetFragmentPage: DataSetPage;
	readonly generalTab: Locator;
	readonly loadingAnimation: Locator;
	readonly newButton: Locator;
	readonly page: Page;
	readonly saveButton: Locator;
	readonly spaceDialog: Locator;
	readonly spaceSelect: Locator;

	constructor(page: Page) {
		this.dataSetFragmentPage = new DataSetPage(page);
		this.generalTab = page.getByRole('tab', {name: 'General'});
		this.loadingAnimation = page.locator('.loading-animation').nth(0);
		this.newButton = page.getByTestId('fdsCreationActionButton');
		this.page = page;
		this.saveButton = page.getByRole('button', {name: 'Save'});
		this.spaceDialog = page.getByRole('dialog');
		this.spaceSelect = this.spaceDialog.getByLabel('Space');
	}

	async deleteProduct(name: string) {
		this.page.once('dialog', (dialog) => dialog.accept());

		await this.dataSetFragmentPage.execItemAction({
			action: 'Delete',
			filter: name,
		});

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

	async openNewProductEditor(space: string = 'Default') {
		await this.newButton.click();

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

	async openProductEditor(name: string) {
		await this.getProduct(name).click();

		await this.generalTab.waitFor();

		await this.loadingAnimation.waitFor({state: 'hidden'});
	}
}
