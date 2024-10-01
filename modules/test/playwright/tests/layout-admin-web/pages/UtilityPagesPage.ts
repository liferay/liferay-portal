/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {PageEditorPage} from '../../../pages/layout-content-page-editor-web/PageEditorPage';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import {PORTLET_URLS} from '../../../utils/portletUrls';
import {waitForAlert} from '../../../utils/waitForAlert';

export class UtilityPagesPage {
	readonly page: Page;

	readonly newButton: Locator;
	readonly pageEditorPage: PageEditorPage;
	readonly publishButton: Locator;

	constructor(page: Page) {
		this.page = page;

		this.newButton = page
			.locator('.management-bar')
			.getByRole('button', {name: 'New'});
		this.pageEditorPage = new PageEditorPage(this.page);
		this.publishButton = page.getByLabel('Publish', {exact: true});
	}

	async goto(siteUrl?: Site['friendlyUrlPath']) {
		await this.page.goto(
			`/group${siteUrl || '/guest'}${PORTLET_URLS.utilityPages}`
		);
	}

	async createPage({
		draft = false,
		name,
		type,
	}: {
		draft?: boolean;
		name: string;
		type: string;
	}) {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {exact: true, name: type}),
			trigger: this.newButton,
		});

		// Select master and fill name

		await this.page
			.locator('.card-page-item .lfr-tooltip-scope', {
				has: this.page.getByText('Blank'),
			})
			.click();

		await this.page.getByLabel('Name', {exact: true}).fill(name);

		await this.page.getByRole('button', {name: 'Save'}).click();

		await waitForAlert(
			this.page,
			'Success:The utility page was created successfully.'
		);

		// Publish is draft param is false

		if (!draft) {
			await this.publishPage();
		}
	}

	async changeThumbnail(filePath: string, name: string) {
		const fileChooserPromise = this.page.waitForEvent('filechooser');

		await this.clickOnAction('Change Thumbnail', name);

		const iframe = this.page.frameLocator(
			'iframe[title="Utility Page Thumbnail"]'
		);

		await expect(
			iframe.getByText('Drag & Drop Your Files or Browse to Upload')
		).toBeVisible();

		await iframe.locator('#itemSelectorUploadContainer').click();

		const fileChooser = await fileChooserPromise;

		await fileChooser.setFiles(filePath);

		await iframe.getByRole('button', {exact: true, name: 'Add'}).click();
	}

	async clickOnAction(action: string, title: string) {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {
				exact: true,
				name: action,
			}),
			trigger: this.page
				.locator('div.card-row', {has: this.page.getByTitle(title)})
				.getByRole('button'),
		});
	}

	async deletePage(name: string) {
		await this.clickOnAction('Delete', name);

		await expect(
			this.page.getByText(
				'Are you sure you want to delete the default utility page? All related pages will be deleted.'
			)
		).toBeVisible();

		await this.page.getByRole('button', {name: 'Delete'}).click();

		await waitForAlert(
			this.page,
			'Success:You successfully deleted 1 utility page(s).'
		);
	}

	async goToEdit(pageTitle: string) {
		await this.page.getByLabel(pageTitle).waitFor();

		const href = await this.page
			.locator('div.card-row', {has: this.page.getByLabel(pageTitle)})
			.getByRole('link')
			.getAttribute('href');

		await this.page.goto(href);

		await this.page
			.getByRole('button', {exact: true, name: 'Publish'})
			.waitFor();
	}

	async makeACopy(name: string) {
		this.page.once('dialog', (dialog) => {
			dialog.accept().catch(() => {});
		});

		await this.clickOnAction('Make a Copy', name);

		await waitForAlert(this.page);
	}

	async markAsDefault(name: string) {
		this.page.once('dialog', (dialog) => {
			dialog.accept().catch(() => {});
		});

		await this.clickOnAction('Mark as Default', name);

		await waitForAlert(this.page);
	}

	async previewPage(name: string) {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {name: 'Preview'}),
			trigger: this.page
				.locator('div.card-row', {has: this.page.getByTitle(name)})
				.getByRole('button'),
		});
	}

	async publishPage() {
		await this.publishButton.waitFor();
		await this.publishButton.click();

		await waitForAlert(
			this.page,
			'Success:The utility page was published successfully.'
		);
	}

	async renamePage(newName: string, oldName: string) {
		await this.clickOnAction('Rename', oldName);

		await this.page.getByLabel('Name', {exact: true}).fill(newName);

		await this.page.getByRole('button', {name: 'Save'}).click();

		await waitForAlert(this.page);
	}

	async unmarkAsDefault(name: string) {
		this.page.once('dialog', (dialog) => {
			dialog.accept().catch(() => {});
		});

		await this.clickOnAction('Unmark as Default', name);

		await waitForAlert(this.page);
	}
}
