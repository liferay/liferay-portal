/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {waitForAlert} from '../../../utils/waitForAlert';
import {UtilityPagesPage} from '../../layout-admin-web/pages/UtilityPagesPage';

export class UtilityPageConfigurationPage {
	readonly page: Page;

	readonly descriptionInput: Locator;
	readonly saveButton: Locator;
	readonly titleInput: Locator;
	readonly utilityPagesPage: UtilityPagesPage;

	constructor(page: Page) {
		this.page = page;

		this.descriptionInput = page.getByPlaceholder('Description');
		this.saveButton = page.getByRole('button', {exact: true, name: 'Save'});
		this.titleInput = page.getByPlaceholder('Title');
		this.utilityPagesPage = new UtilityPagesPage(page);
	}

	async goto(pageTitle: string) {
		await this.utilityPagesPage.clickOnAction('Configure', pageTitle);
	}

	async setUtilityPageConfiguration(
		htmlDescription: string,
		htmlTitle: string,
		pageTitle: string
	) {
		await this.goto(pageTitle);

		await this.descriptionInput.waitFor();

		await this.descriptionInput.fill(htmlDescription);
		await this.titleInput.fill(htmlTitle);

		await this.saveButton.click();

		await waitForAlert(this.page, 'The page was updated successfully.');
	}
}
