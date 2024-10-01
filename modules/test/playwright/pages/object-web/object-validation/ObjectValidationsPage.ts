/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page} from '@playwright/test';

import {ViewObjectDefinitionsPage} from '../ViewObjectDefinitionsPage';

export class ObjectValidationsPage {
	readonly activeValitionToggle: Locator;
	readonly addObjectValidationButton: Locator;
	readonly errorMessageInput: Locator;
	readonly iframe: FrameLocator;
	readonly validationTabItem: Locator;
	readonly viewObjectDefinitionsPage: ViewObjectDefinitionsPage;

	constructor(page: Page) {
		this.iframe = page.frameLocator('iframe');
		this.activeValitionToggle = this.iframe.getByLabel('Active Validation');
		this.addObjectValidationButton = page.getByTitle(
			'Add Object Validation'
		);
		this.errorMessageInput = this.iframe.getByPlaceholder(
			'Add an error message.'
		);
		this.validationTabItem = page
			.getByRole('listitem')
			.filter({hasText: 'Validations'});
		this.viewObjectDefinitionsPage = new ViewObjectDefinitionsPage(page);
	}

	async goto(objectDefinitionLabel: string) {
		await this.viewObjectDefinitionsPage.goto();

		await this.viewObjectDefinitionsPage.clickEditObjectDefinitionLink(
			objectDefinitionLabel
		);

		await this.validationTabItem.click();
	}
}
