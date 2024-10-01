/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import fillAndClickOutside from '../../../utils/fillAndClickOutside';
import {waitForAlert} from '../../../utils/waitForAlert';
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
		this.saveButton = this.page.getByRole('button', {name: 'Save'});
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

	async fillTextField(name: string, content: string) {
		const textField = this.page.getByRole('textbox', {
			name,
		});

		await fillAndClickOutside(this.page, textField, content);
	}

	async save() {
		await this.saveButton.click();

		await waitForAlert(
			this.page,
			`Success:Your request completed successfully.`
		);
	}
}
