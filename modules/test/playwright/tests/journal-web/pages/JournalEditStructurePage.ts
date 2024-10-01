/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {waitForAlert} from '../../../utils/waitForAlert';
import {JournalStructuresPage} from './JournalStructuresPage';

export enum FIELD_TYPES {
	DATE = 'Date',
	NUMERIC = 'Numeric',
	TEXT = 'Text',
	SELECT_FROM_LIST = 'Select from List',
}

export class JournalEditStructurePage {
	readonly page: Page;

	readonly journalStructurePage: JournalStructuresPage;
	readonly propertyPlaceholderText: Locator;
	readonly propertiesTab: Locator;
	readonly saveButton: Locator;

	constructor(page: Page) {
		this.page = page;

		this.journalStructurePage = new JournalStructuresPage(page);
		this.propertyPlaceholderText = page.getByLabel('Placeholder Text');
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
		await this.page
			.getByRole('link', {exact: true, name: `${structureName}`})
			.click();

		await this.propertiesTab.waitFor();

		await this.page.locator('body').click();
	}

	async showFieldProperties(name, type?: FIELD_TYPES) {
		await this.page.getByText(`${type || FIELD_TYPES.TEXT}${name}`).click();
	}

	async fillFieldProperty(fieldProperty: Locator, content: string) {
		await fieldProperty.click();
		await fieldProperty.fill(content);
	}

	async save() {
		await this.saveButton.click();

		await waitForAlert(
			this.page,
			`Success:Your request completed successfully.`
		);
	}
}
