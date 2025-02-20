/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page} from '@playwright/test';

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
