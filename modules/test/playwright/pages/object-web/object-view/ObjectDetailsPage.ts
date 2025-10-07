/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {clickAndExpectToBeHidden} from '../../../utils/clickAndExpectToBeHidden';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import {ViewObjectDefinitionsPage} from '../ViewObjectDefinitionsPage';

export class ObjectDetailsPage {
	readonly detailsTabItem: Locator;
	readonly saveButton: Locator;
	readonly viewObjectDefinitionsPage: ViewObjectDefinitionsPage;
	readonly page: Page;

	constructor(page: Page) {
		this.page = page;
		this.detailsTabItem = page
			.getByRole('listitem')
			.filter({hasText: 'Details'});
		this.saveButton = page.getByRole('button', {name: 'Save'});
		this.viewObjectDefinitionsPage = new ViewObjectDefinitionsPage(page);
	}

	async goto(objectDefinitionLabel: string) {
		await this.viewObjectDefinitionsPage.goto();

		await this.viewObjectDefinitionsPage.clickEditObjectDefinitionLink(
			objectDefinitionLabel
		);

		await this.detailsTabItem.click();

		// Ensure that the page has loaded

		await this.saveButton.click({trial: true});

		await clickAndExpectToBeVisible({
			target: this.page.getByRole('option', {name: 'en_US'}),
			trigger: this.page
				.locator('[aria-label="Open Localizations"]')
				.first(),
		});

		await clickAndExpectToBeHidden({
			target: this.page.getByRole('option', {name: 'en_US'}),
			trigger: this.page
				.locator('[aria-label="Open Localizations"]')
				.first(),
		});
	}

	async updateConfiguration({
		fieldLabel,
		value,
	}: {
		fieldLabel: string;
		value: boolean;
	}) {
		const field = this.page.getByLabel(fieldLabel, {exact: true});

		if (value) {
			await field.check();
		}
		else {
			await field.uncheck();
		}

		await this.saveButton.click();

		await this.page
			.getByText('The object was saved successfully.')
			.waitFor();

		await this.page.waitForEvent('load');

		if (value) {
			await expect(field).toBeChecked();
		}
		else {
			await expect(field).not.toBeChecked();
		}
	}
}
