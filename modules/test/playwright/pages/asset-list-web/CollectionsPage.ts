/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect} from '@playwright/test';

import {PORTLET_URLS} from '../../utils/portletUrls';
import {waitForAlert} from '../../utils/waitForAlert';

export class CollectionsPage {
	readonly page: Page;

	constructor(page: Page) {
		this.page = page;
	}

	async goto(siteUrl?: Site['friendlyUrlPath']) {
		await this.page.goto(
			`/group${siteUrl || '/guest'}${PORTLET_URLS.collections}`
		);

		await this.page.waitForLoadState('networkidle');
	}

	/**
	 * Creates a dynamic collection with the given name.
	 */

	async createWebContentDynamicCollection(name, siteUrl) {
		await this.addNewDynamicCollection(name);

		await this.configureCollectionWithWebContents();

		await this.page.getByRole('button', {name: 'Save'}).click();

		await waitForAlert(this.page);

		return {
			classPK: await this.getCollectionClassPK(name, siteUrl),
		};
	}

	/**
	 * Adds a dynamic or manual collection with a given name.
	 */
	async addNewCollection(name: string, isDynamic: boolean) {
		await this.page
			.locator('.creation-menu')
			.getByRole('button', {name: 'New'})
			.first()
			.click();

		const collectionType = isDynamic
			? 'Dynamic Collection'
			: 'Manual Collection';

		await this.page.getByRole('menuitem', {name: collectionType}).click();

		await this.page.getByPlaceholder('Title').fill(name);

		await this.page.getByRole('button', {name: 'Save'}).click();

		await waitForAlert(this.page);
	}

	/**
	 * Add a dynamic collection with the given name.
	 */

	async addNewDynamicCollection(name) {
		await this.addNewCollection(name, true);
	}

	/**
	 * Add a manual collection with the given name.
	 */
	async addNewManualCollection(name: string) {
		await this.addNewCollection(name, false);
	}

	/**
	 * Opens a collection from the Collections list for editing.
	 */

	async openCollection(name: string) {
		await this.page.getByRole('link', {name}).click();

		await this.page.waitForLoadState('networkidle');
	}

	/**
	 * On a collection's edit page, adds a personalized variation for the given
	 * segment. The segment picker renders inside a modal iframe.
	 */

	async addPersonalizedVariation(segmentName: string) {
		await this.page
			.getByRole('button', {name: 'Add Personalized Variation'})
			.click();

		await this.page
			.frameLocator('iframe[id$="selectEntity_iframe_"]')
			.getByText(segmentName)
			.click();

		await waitForAlert(this.page);
	}

	/**
	 * On a collection's edit page, deprioritizes the given variation through its
	 * actions menu in the personalized variations panel.
	 */

	async deprioritizeVariation(variationTitle: string) {
		const actionsButton = this.page.getByRole('button', {
			name: `Actions for ${variationTitle}`,
		});

		const deprioritizeButton = this.page.getByRole('button', {
			name: 'Deprioritize',
		});

		// The actions ellipsis sometimes needs a second click to open its
		// dropdown, so only click it while the menu is still closed.

		await expect(async () => {
			if (!(await deprioritizeButton.isVisible())) {
				await actionsButton.click();
			}

			await expect(deprioritizeButton).toBeVisible({timeout: 2000});
		}).toPass({timeout: 20000});

		await deprioritizeButton.click();
	}

	async deleteCollection(name: string) {
		const menuButton = this.page
			.getByRole('row', {name})
			.getByLabel('Show Actions')
			.first();

		await menuButton.scrollIntoViewIfNeeded();

		await menuButton.click();

		await this.page.getByRole('menuitem', {name: 'Delete'}).click();

		await this.page
			.getByRole('button', {
				exact: true,
				name: 'Delete',
			})
			.click();

		await waitForAlert(this.page);

		await this.page.waitForLoadState('networkidle');
	}

	async renameCollection(oldName: string, newName: string) {
		const menuButton = this.page
			.getByRole('row', {name: oldName})
			.getByLabel('Show Actions')
			.first();

		await menuButton.scrollIntoViewIfNeeded();

		await menuButton.click();

		await this.page.getByRole('menuitem', {name: 'Rename'}).click();

		await this.page.getByPlaceholder('Title').fill(newName);

		await this.page.getByRole('button', {name: 'Save'}).click();

		await waitForAlert(this.page);
	}

	/**
	 * On a manual collection's edit page, restricts the collection to multiple
	 * item types by choosing "Select Types" and moving the given types out of
	 * the "In Use" list, then saves.
	 */

	async restrictManualCollectionItemTypes(excludedTypes: string[]) {
		await this.page
			.getByRole('combobox', {name: 'Item Type'})
			.selectOption({label: 'Select Types'});

		const itemTypesBox = this.page.locator('[id$="classNamesBoxes"]');

		const inUseList = itemTypesBox.getByLabel('In Use', {exact: true});

		for (const excludedType of excludedTypes) {
			await inUseList.selectOption({label: excludedType});

			await itemTypesBox
				.getByRole('button', {
					name: /move selected items from in use to available/i,
				})
				.click();
		}

		await this.page.getByRole('button', {name: 'Save'}).click();

		await waitForAlert(this.page);
	}

	/**
	 * On a manual collection's edit page, restricts the collection to a single
	 * item type (and optional subtype), then saves.
	 */

	async configureManualCollectionItemType({
		itemSubtype,
		itemType,
	}: {
		itemSubtype?: string;
		itemType: string;
	}) {
		await this.page
			.getByRole('combobox', {name: 'Item Type'})
			.selectOption({label: itemType});

		if (itemSubtype) {
			const subtypeSelect = this.page
				.locator('.asset-subtype:not(.hide)')
				.getByLabel('Item Subtype');

			await subtypeSelect.waitFor();

			await subtypeSelect.selectOption({label: itemSubtype});
		}

		await this.page.getByRole('button', {name: 'Save'}).click();

		await waitForAlert(this.page);
	}

	/**
	 * On a manual collection's edit page, clicks "Select" to open the asset
	 * entries item selector modal and returns the modal dialog locator.
	 */

	async openSelectItemsModal() {
		await this.page.getByRole('button', {name: 'Select Items'}).click();

		const modal = this.page.getByRole('dialog');

		await modal.waitFor();

		return modal;
	}

	/**
	 * On a manual collection's edit page, opens the asset entries item selector
	 * modal, checks the given assets, and confirms the selection.
	 */

	async selectAssets(assetTitles: string[]) {
		await this.openSelectItemsModal();

		for (const assetTitle of assetTitles) {
			await this.page
				.getByRole('checkbox', {name: `Select ${assetTitle}`})
				.check();
		}

		await this.page
			.locator('.modal-footer')
			.getByRole('button', {exact: true, name: 'Select'})
			.click();
	}

	/**
	 * Configure a dynamic collection for Web Contents.
	 */

	async configureCollectionWithWebContents() {
		await this.page
			.getByLabel('Item Type')
			.selectOption({label: 'Web Content Article'});

		const select = await this.page
			.locator('.asset-subtype:not(.hide)')
			.getByLabel('Item Subtype');

		await select.waitFor();

		await select.selectOption({label: 'Basic Web Content'});
	}

	/**
	 * Gets the collection classPK.
	 */

	async getCollectionClassPK(name, siteUrl) {
		await this.goto(siteUrl);

		const classPK = await this.page
			.locator(`button[data-assetlistentrytitle="${name}"]`)
			.first()
			.evaluate((event) => event.dataset.assetlistentryid);

		return Number(classPK);
	}
}
