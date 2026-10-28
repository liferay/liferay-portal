/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page, expect} from '@playwright/test';

import fillAndClickOutside from '../../../../utils/fillAndClickOutside';
import {waitForAlert} from '../../../../utils/waitForAlert';
import {JournalTemplatesPage} from './JournalTemplatesPage';

export class JournalEditTemplatePage {
	readonly page: Page;

	readonly basicInformation: Locator;
	readonly elementsButton: Locator;
	readonly journalTemplatesPage: JournalTemplatesPage;
	readonly nameInput: Locator;
	readonly saveButton: Locator;
	readonly selectStructureButton: Locator;
	readonly selectorWindow: FrameLocator;

	constructor(page: Page) {
		this.page = page;

		this.basicInformation = page.getByRole('button', {
			name: 'Basic Information',
		});
		this.elementsButton = page.getByTitle('Elements', {exact: true});
		this.journalTemplatesPage = new JournalTemplatesPage(page);
		this.nameInput = page.getByRole('textbox', {name: 'Name'});
		this.saveButton = page.getByRole('button', {exact: true, name: 'Save'});
		this.selectStructureButton = page.getByRole('button', {
			name: 'Select Structure',
		});
		this.selectorWindow = page.frameLocator(
			'iframe[title="Select Structure"]'
		);
	}

	async goto(siteUrl?: Site['friendlyUrlPath']) {
		await this.journalTemplatesPage.goto(siteUrl);
		await this.journalTemplatesPage.goToCreateNewTemplate();

		// Do it twice so we decrease flakiness

		await this.journalTemplatesPage.goto(siteUrl);
		await this.journalTemplatesPage.goToCreateNewTemplate();

		await this.basicInformation.waitFor();
	}

	async gotoElements() {
		await this.elementsButton.click();
	}

	async selectTemplateToEdit(title: string) {
		await this.page
			.getByRole('link', {name: title})
			.waitFor({state: 'visible'});
		await this.page.getByRole('link', {name: title}).click();
	}

	async editTemplate(title: string, script: string) {
		await fillAndClickOutside(this.page, this.nameInput, title);

		const scriptEditor = this.page.locator(
			'.ddm_template_editor__CodeMirrorEditor'
		);

		await expect(async () => {
			await scriptEditor.dblclick({timeout: 2000});

			await this.page.keyboard.press('ControlOrMeta+A');
			await this.page.keyboard.press('Backspace');
			await this.page.keyboard.type(script);

			await expect
				.poll(
					async () =>
						(
							await scriptEditor
								.locator('.CodeMirror')
								.evaluate((element: any) =>
									element.CodeMirror.getValue()
								)
						).replace(/\s/g, ''),
					{timeout: 1000}
				)
				.toBe(script.replace(/\s/g, ''));
		}).toPass({timeout: 15000});
	}

	async getDDMTemplateKey() {
		await this.page.getByLabel('Properties').click();

		return this.page.getByLabel('DDM Template Key').inputValue();
	}

	async selectStructure(title: string) {
		await this.selectStructureButton.click();

		await this.selectorWindow
			.getByText(title, {
				exact: true,
			})
			.click();
	}

	async saveTemplate() {
		await expect(async () => {
			await this.saveButton.click();

			await waitForAlert(this.page);
		}).toPass({timeout: 5000});
	}
}
