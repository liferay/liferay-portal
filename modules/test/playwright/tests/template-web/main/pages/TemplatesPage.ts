/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';
import path from 'path';

import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';
import fillAndClickOutside from '../../../../utils/fillAndClickOutside';
import {PORTLET_URLS} from '../../../../utils/portletUrls';
import {waitForAlert} from '../../../../utils/waitForAlert';

export class TemplatesPage {
	readonly page: Page;

	readonly newButton: Locator;
	readonly saveButton: Locator;
	readonly searchBar: Locator;

	constructor(page: Page) {
		this.page = page;

		this.newButton = page.getByRole('button', {name: 'Add'});
		this.saveButton = page.getByRole('button', {exact: true, name: 'Save'});
		this.searchBar = page.getByLabel('Search for:');
	}

	async goto(siteUrl?: Site['friendlyUrlPath']) {
		await this.page.goto(
			`/group${siteUrl || '/guest'}${PORTLET_URLS.templates}`
		);
	}

	async gotoWidgetTemplates(siteUrl?: Site['friendlyUrlPath']) {
		await this.page.goto(
			`/group${siteUrl || '/guest'}${PORTLET_URLS.widgetTemplates}`
		);
	}

	async clickAction(action: string, title: string) {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {name: action}),
			trigger: this.page
				.locator('tr')
				.filter({hasText: title})
				.locator('button.dropdown-toggle'),
		});
	}

	async copyInformationTemplate(title: string) {
		await this.clickAction('Make a Copy', title);

		await fillAndClickOutside(
			this.page,
			this.page.getByLabel('name'),
			`${title} (Copy)`
		);

		await this.page.getByRole('button', {name: 'Copy'}).click();

		await waitForAlert(this.page);
	}

	async createInformationTemplate({
		itemSubtype,
		itemType,
		name,
	}: {
		itemSubtype?: string;
		itemType: string;
		name: string;
	}) {
		await this.newButton.click();

		await this.page.getByLabel('Name', {exact: true}).fill(name);

		await this.page.getByLabel('Item Type').selectOption({label: itemType});

		if (itemSubtype) {
			await this.page
				.getByLabel('Item Subtype')
				.selectOption({label: itemSubtype});
		}

		await this.page.getByRole('button', {name: 'Save'}).click();

		await waitForAlert(this.page);
	}

	async createWidgetTemplate(name: string, type: string, content?: string) {
		const typeOption = this.page.getByRole('menuitem', {
			name: type,
		});

		const moreButton = this.page.getByRole('button', {name: 'More'});

		await clickAndExpectToBeVisible({
			target: moreButton,
			trigger: this.page.getByRole('button', {name: 'New'}),
		});

		if (await typeOption.isVisible()) {
			await typeOption.click();
		}
		else {
			await clickAndExpectToBeVisible({
				autoClick: true,
				target: typeOption,
				trigger: moreButton,
			});
		}

		// Wait until the editor is loaded

		await this.page.locator('.ddm_template_editor__App').waitFor();

		await fillAndClickOutside(
			this.page,
			this.page.getByPlaceholder('Untitled Template'),
			name
		);

		if (content) {
			const codeMirror = this.page.locator('.CodeMirror-scroll').last();
			await codeMirror.click();

			await this.page.keyboard.press('Control+KeyA');
			await this.page.keyboard.press('Backspace');

			await this.page.keyboard.type(content);
		}

		await this.saveTemplate(name);
	}

	async deleteInformationTemplate(title: string) {
		await this.clickAction('Delete', title);

		await this.page.getByRole('button', {name: 'Delete'}).click();

		await waitForAlert(this.page);
	}

	async editTemplate(name: string) {
		await this.page.getByRole('link', {exact: true, name}).click();
	}

	async editWidgetTemplate(name: string) {
		const link = this.page.getByRole('link', {exact: true, name});

		await expect(async () => {
			await this.searchBar.fill(name, {timeout: 1000});

			await this.searchBar.press('Enter', {timeout: 1000});

			await expect(link).toBeVisible({timeout: 1000});
		}).toPass();

		await clickAndExpectToBeVisible({
			target: this.page.getByPlaceholder('Untitled Template'),
			trigger: link,
		});
	}

	async goToViewUsages(widgetTemplateName: string) {
		await this.page
			.locator('tr')
			.filter({hasText: widgetTemplateName})
			.getByLabel('Show Actions')
			.click();

		await this.page
			.getByRole('menuitem', {exact: true, name: 'View Usages'})
			.click();

		await this.page.waitForURL(/view_widget_templates_usages/);
	}

	async getTemplateKey() {
		await this.page.getByLabel('Properties').click();

		return await this.page.getByLabel('Template Key').getAttribute('value');
	}

	async importInformationTemplate(dirname: string, fileName: string) {
		const fileChooserPromise = this.page.waitForEvent('filechooser');

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page
				.locator('.dropdown-menu')
				.getByRole('menuitem', {name: 'Import Script'}),
			trigger: this.page
				.locator('.control-menu-nav-item')
				.getByLabel('Options', {exact: true}),
		});

		const fileChooser = await fileChooserPromise;

		await fileChooser.setFiles(
			path.join(dirname, '/dependencies/' + fileName)
		);

		await waitForAlert(this.page, `Success:${fileName} Imported`);
	}

	async saveTemplate(name: string) {
		await expect(async () => {
			await this.saveButton.click();

			await waitForAlert(this.page);

			await expect(
				this.page.getByRole('link', {
					exact: true,
					name,
				})
			).toBeAttached();
		}).toPass();
	}
}
