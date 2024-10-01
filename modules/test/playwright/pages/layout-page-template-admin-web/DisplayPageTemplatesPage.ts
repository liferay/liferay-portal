/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../utils/clickAndExpectToBeVisible';
import {PORTLET_URLS} from '../../utils/portletUrls';
import {waitForAlert} from '../../utils/waitForAlert';

export class DisplayPageTemplatesPage {
	readonly page: Page;

	readonly newButton: Locator;
	readonly publishButton: Locator;

	constructor(page: Page) {
		this.page = page;

		this.newButton = page.getByText('New', {exact: true});
		this.publishButton = page.getByLabel('Publish', {exact: true});
	}

	async goto(siteUrl?: Site['friendlyUrlPath']) {
		await this.page.goto(
			`/group${siteUrl || '/guest'}${PORTLET_URLS.displayPageTemplates}`
		);
	}

	async clickMoreActions(name: string, actionName: string) {
		await this.page
			.locator('.card-page-item')
			.filter({hasText: name})
			.getByLabel('More actions')
			.click();

		await this.page
			.getByRole('menuitem', {
				exact: true,
				name: actionName,
			})
			.click();
	}

	async deleteTemplate(name: string) {
		await this.clickMoreActions(name, 'Delete');

		await this.page.getByRole('button', {name: 'Delete'}).click();

		await waitForAlert(
			this.page,
			'Success:You successfully deleted 1 display page template(s).'
		);
	}

	async deleteAllDisplayPageTemplates() {
		await this.page
			.getByLabel('Select All Items on the Page')
			.setChecked(true);

		await this.page.getByRole('button', {name: 'Delete'}).click();

		await this.page
			.getByLabel('Delete Entries- Loading')
			.getByRole('button', {name: 'Delete'})
			.click();
	}

	async editTemplate(name: string) {
		await this.clickMoreActions(name, 'Edit');

		await this.page
			.getByText('Select a Page Element', {exact: true})
			.waitFor();
	}

	async viewUsages(name: string) {
		await this.clickMoreActions(name, 'View Usages');

		await clickAndExpectToBeVisible({
			target: this.page.getByRole('row').getByRole('checkbox').first(),

			trigger: this.page.getByRole('menuitem', {
				exact: true,
				name: 'View Usages',
			}),
		});
	}

	async mapConfiguration({
		field,
		mappingField,
	}: {
		field: string;
		mappingField: string;
	}) {
		await this.page
			.locator('.form-group')
			.filter({has: this.page.getByLabel(field, {exact: true})})
			.getByTitle('Map', {exact: true})
			.click();

		await this.page
			.getByLabel('Field', {exact: true})
			.selectOption(mappingField);

		await this.page
			.locator('.dpt-mapping-panel')
			.getByRole('button')
			.click();

		await this.page
			.getByRole('button', {exact: true, name: 'Save'})
			.click();

		await waitForAlert(
			this.page,
			'Success:The page was updated successfully.'
		);
	}

	async markAsDefault(name: string) {
		this.page.once('dialog', (dialog) => {
			dialog.accept().catch(() => {});
		});

		await this.clickMoreActions(name, 'Mark as Default');

		await waitForAlert(this.page);
	}

	async renameTemplate(newName: string, oldName: string) {
		await this.clickMoreActions(oldName, 'Rename');

		await this.page.getByLabel('Name', {exact: true}).fill(newName);

		await this.page.getByRole('button', {name: 'Save'}).click();

		await waitForAlert(this.page);
	}

	async createFolder(name: string) {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {name: 'Folder'}),
			trigger: this.newButton,
		});

		await this.page.locator('.modal-body').waitFor();

		await this.page.getByLabel('Name').fill(name);

		await this.page.getByRole('button', {name: 'Create'}).click();

		await waitForAlert(this.page);
	}

	async createTemplate({
		contentSubtype,
		contentType,
		folderName,
		name,
	}: {
		contentSubtype?: string;
		contentType: string;
		folderName?: string;
		name: string;
	}) {
		if (folderName) {
			await this.page.getByRole('link', {name: folderName}).click();

			await this.page.getByText(folderName, {exact: true}).waitFor();

			await clickAndExpectToBeVisible({
				autoClick: false,
				target: this.page.getByRole('menuitem', {
					name: 'Display Page Template',
				}),
				trigger: this.newButton,
			});

			await this.page
				.getByRole('menuitem', {
					name: 'Display Page Template',
				})
				.click();
		}
		else {
			await clickAndExpectToBeVisible({
				target: this.page.getByRole('button', {name: 'Blank'}),
				timeout: 3000,
				trigger: this.newButton,
			});
		}

		await this.page.getByRole('button', {name: 'Blank'}).click();
		await this.page.getByLabel('Name', {exact: true}).fill(name);

		await this.page
			.getByLabel('Content Type')
			.selectOption({label: contentType});

		if (contentSubtype) {
			await this.page
				.getByLabel('Subtype')
				.selectOption({label: contentSubtype});
		}

		await this.page.getByRole('button', {name: 'Save'}).click();

		await waitForAlert(
			this.page,
			'Success:The display page template was created successfully.'
		);

		await this.publishTemplate();
	}

	async publishTemplate() {
		await this.publishButton.waitFor();
		await this.publishButton.click();

		await waitForAlert(
			this.page,
			'Success:The display page template was published successfully.'
		);
	}
}
