/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page, expect} from '@playwright/test';

import {clickAndExpectToBeHidden} from '../../utils/clickAndExpectToBeHidden';
import {clickAndExpectToBeVisible} from '../../utils/clickAndExpectToBeVisible';
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

	async assertCommunicationEnabled(
		fields = ['assetEntryId', 'categoryId', 'resetCur', 'tags', 'tag'],
		shouldBeEnabled = true
	) {
		const idPrefix =
			'_com_liferay_portlet_configuration_web_portlet_PortletConfigurationPortlet_lfr-prp-mapping-p_r_p_';

		for (const field of fields) {
			const locator = this.configurationIframe.locator(
				`[id="${idPrefix}${field}"]`
			);

			if (shouldBeEnabled) {
				await expect(locator).toBeEnabled();
			}
			else {
				await expect(locator).toBeDisabled();
			}
		}
	}

	async changeAssetSelection(type: 'Collection' | 'Dynamic' | 'Manual') {
		await this.openAssetSelectionTab();

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

	async closeConfiguration() {
		await this.page
			.locator('.modal-header')
			.getByLabel('Close', {exact: true})
			.click();
	}

	async openAssetSelectionTab() {
		const assetSelectionTab = this.configurationIframe.getByRole('tab', {
			name: 'Asset Selection',
		});

		await assetSelectionTab.waitFor({state: 'visible'});

		await assetSelectionTab.click();
	}

	async openDisplaySettingsTab() {
		await this.configurationIframe
			.getByRole('tab', {name: 'Display Settings'})
			.click();
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

	async selectCollectionProvider(name: string) {
		const collectionSelectorIframe = this.configurationIframe.frameLocator(
			'iframe[title="Select Collection"]'
		);

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: collectionSelectorIframe.getByRole('link', {
				name: 'Collection Providers',
			}),
			timeout: 2000,
			trigger: this.configurationIframe.getByRole('button', {
				exact: true,
				name: 'Select Collection',
			}),
		});

		await clickAndExpectToBeHidden({
			target: this.configurationIframe.locator('.modal-dialog'),
			timeout: 2000,
			trigger: collectionSelectorIframe.getByRole('button', {
				name: `Select ${name}`,
			}),
		});
	}

	async selectDisplayTemplate(label: string) {
		await this.configurationIframe.getByLabel('Display Template').click();

		await this.configurationIframe
			.getByRole('option', {name: label})
			.click();
	}
}
