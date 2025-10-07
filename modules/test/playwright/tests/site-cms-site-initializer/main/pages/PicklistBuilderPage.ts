/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {ApiHelpers} from '../../../../helpers/ApiHelpers';
import {clickAndExpectToBeHidden} from '../../../../utils/clickAndExpectToBeHidden';
import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';
import {PORTLET_URLS} from '../../../../utils/portletUrls';
import {waitForAlert} from '../../../../utils/waitForAlert';

export class PicklistBuilderPage {
	readonly page: Page;

	readonly addButton: Locator;
	readonly ercInput: Locator;
	readonly nameInput: Locator;
	readonly saveButton: Locator;

	constructor(page: Page) {
		this.page = page;

		this.addButton = this.page.getByLabel('Add New').first();
		this.ercInput = this.page.getByLabel('ERC');
		this.nameInput = this.page.getByLabel('Picklist Name');
		this.saveButton = this.page.getByRole('button', {name: 'Save'});
	}

	async addOption(name: string) {
		await clickAndExpectToBeVisible({
			target: this.page.locator('.modal-header').getByText('Add Option'),
			trigger: this.addButton,
		});

		await this.page.locator('.modal-body').getByLabel('Name').fill(name);

		await clickAndExpectToBeHidden({
			target: this.page.locator('.modal-header').getByText('Add Option'),
			trigger: this.page.locator('.modal-footer').getByText('Save'),
		});
	}

	async createPicklist() {
		const apiHelpers = new ApiHelpers(this.page);

		return await apiHelpers.listTypeAdmin.postRandomListTypeDefinition();
	}

	async goto() {
		await this.page.goto(PORTLET_URLS.cmsPicklistBuilder);

		await this.page.getByText('New Picklist').waitFor();
	}

	async deletePicklist(id: number) {
		const apiHelpers = new ApiHelpers(this.page);

		await apiHelpers.listTypeAdmin.deleteListTypeDefinition(id);

		return;
	}

	async getPicklist(name: string) {
		const apiHelpers = new ApiHelpers(this.page);

		const [picklist] =
			await apiHelpers.listTypeAdmin.getFilteredListTypeDefinition(
				'name',
				name
			);

		return picklist;
	}

	async savePicklist() {
		const save = async () => {
			await this.saveButton.click();

			await waitForAlert(this.page, 'successfully', {timeout: 5000});
		};

		const [response] = await Promise.all([
			this.page.waitForResponse(
				(response) => {
					return (
						response.url().includes('list-type-definitions') &&
						response.status() === 200
					);
				},
				{timeout: 5000}
			),
			await save(),
		]);

		return await response.json();
	}
}
