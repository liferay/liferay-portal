/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {openFieldset} from '../../../../utils/openFieldset';
import {waitForAlert} from '../../../../utils/waitForAlert';
import {JournalStructuresPage} from './JournalStructuresPage';

export class JournalEditStructureDefaultValuesPage {
	readonly page: Page;

	readonly journalStructurePage: JournalStructuresPage;
	readonly propertiesTab: Locator;
	readonly saveButton: Locator;

	constructor(page: Page) {
		this.page = page;

		this.journalStructurePage = new JournalStructuresPage(page);
		this.propertiesTab = page.getByRole('tab', {name: 'Properties'});
		this.saveButton = page.getByRole('button', {exact: true, name: 'Save'});
	}

	async goto({
		siteUrl,
		structureName,
	}: {
		siteUrl?: Site['friendlyUrlPath'];
		structureName?: string;
	} = {}) {
		await this.journalStructurePage.goto(siteUrl);
		await this.journalStructurePage.goToJournalStructureAction(
			'Edit Default Values',
			structureName
		);

		await this.propertiesTab.waitFor();

		await this.page.locator('body').click();
	}

	getRichTextField(name: string): Locator {
		return this.page
			.getByText(`${name} Rich Text Editor`)
			.getByRole('textbox');
	}

	async fillRichTextField(name: string, content: string) {
		const richTextField = this.getRichTextField(name);

		await expect(async () => {
			await richTextField.fill(content, {timeout: 2000});

			await expect(richTextField).toContainText(content, {
				timeout: 2000,
			});
		}).toPass();
	}

	async fillTextField(name: string, content: string) {
		const textField = this.page.getByRole('textbox', {
			name,
		});

		// The Fields panel can load collapsed, leaving its inputs present but
		// not actionable, so re-expand it before filling.

		await expect(async () => {
			await openFieldset(this.page, 'Fields');

			await textField.fill(content, {timeout: 2000});
		}).toPass();
	}

	async save() {
		await this.saveButton.click();

		await waitForAlert(
			this.page,
			`Success:Your request completed successfully.`
		);
	}
}
