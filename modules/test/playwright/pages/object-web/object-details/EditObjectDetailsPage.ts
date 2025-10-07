/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {ViewObjectDefinitionsPage} from '../ViewObjectDefinitionsPage';

export class EditObjectDetailsPage {
	readonly accountRestrictionToggle: Locator;
	readonly detailsTabItem: Locator;
	readonly friendlyURLSeparator: Locator;
	readonly page: Page;
	readonly publishButton: Locator;
	readonly saveButton: Locator;
	readonly viewObjectDefinitionsPage: ViewObjectDefinitionsPage;

	constructor(page: Page) {
		this.accountRestrictionToggle = page.getByLabel(
			'Enable Account Restriction',
			{exact: true}
		);
		this.detailsTabItem = page.getByRole('link', {name: 'Details'});
		this.friendlyURLSeparator = page.getByLabel(
			'Object Entry URL Separator',
			{exact: true}
		);
		this.page = page;
		this.publishButton = page.getByRole('button', {
			exact: true,
			name: 'Publish',
		});
		this.saveButton = page.getByRole('button', {name: 'Save'});
		this.viewObjectDefinitionsPage = new ViewObjectDefinitionsPage(page);
	}

	async enableAccountRestriction(fieldName: string) {
		await this.accountRestrictionToggle.check();

		await this.page.getByText('Select an Option').click();
		await this.page.getByRole('option', {name: fieldName}).click();
	}

	async goto(objectDefinitionLabel: string) {
		await this.viewObjectDefinitionsPage.goto();

		await this.viewObjectDefinitionsPage.clickEditObjectDefinitionLink(
			objectDefinitionLabel
		);
	}

	async goToDetailsTab() {
		await this.detailsTabItem.click();

		await this.page.waitForLoadState('networkidle');
	}

	async saveObjectDefinition() {
		await this.saveButton.click();
	}
}
