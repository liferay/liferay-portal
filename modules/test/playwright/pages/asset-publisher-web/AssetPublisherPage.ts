/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page} from '@playwright/test';

import {waitForAlert} from '../../utils/waitForAlert';

export class AssetPublisherPage {
	readonly page: Page;

	readonly configurationIframe: FrameLocator;
	readonly itemSelector: Locator;

	constructor(page: Page) {
		this.page = page;

		this.configurationIframe = this.page.frameLocator(
			'iframe[title*="Configuration"]'
		);
		this.itemSelector = this.page.getByLabel('Select Items');
	}

	async changeAssetSelection(type: 'Collection' | 'Dynamic' | 'Manual') {
		await this.configurationIframe.getByLabel(type).click();

		await waitForAlert(
			this.configurationIframe,
			'Success:You have successfully updated the setup.'
		);
	}

	async createCollectionFromAssetPublisher(collectionName: string) {
		await this.configurationIframe
			.getByRole('button', {name: 'Create a collection from this'})
			.click();

		await this.configurationIframe
			.getByPlaceholder('Title')
			.fill(collectionName);

		await this.configurationIframe
			.getByLabel('Collection Title')
			.getByRole('button', {name: 'Save'})
			.click();

		await waitForAlert(
			this.configurationIframe,
			'Success:The collection was created successfully.'
		);
	}

	async addManualItem(type: string, itemName: string) {
		if (await this.itemSelector.isHidden({timeout: 2000})) {
			await this.page.getByRole('button', {name: 'Save'}).click();
			await waitForAlert(this.page);
		}
		await this.itemSelector.click();
		await this.page.getByRole('menuitem', {name: type}).click();

		await this.page
			.frameLocator(`iframe[title="Select ${type}"]`)
			.locator(
				'[id^="_com_liferay_item_selector_web_portlet_ItemSelectorPortlet_articles_"]'
			)
			.filter({hasText: itemName})
			.locator('.checkbox')
			.click();
		await this.page.getByRole('button', {name: 'Add'}).click();

		await waitForAlert(this.page);
	}

	async addFileFromAssetPublisher(fileName: string) {
		await this.page.getByLabel('Title Required').fill(fileName);

		await this.page.getByRole('button', {name: 'Publish'}).click();

		// Using first() locator instance of this one until LPD-41787 is fixed
		// await waitForAlert(this.page);

		await this.page
			.locator('.alert-success', {
				hasText: 'Success:Your request completed successfully.',
			})
			.first()
			.waitFor();
	}

	async saveConfiguration() {
		await this.configurationIframe
			.getByRole('button', {name: 'Save'})
			.click();

		await waitForAlert(
			this.configurationIframe,
			'Success:You have successfully updated the setup.'
		);
	}
}
