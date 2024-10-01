/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {ViewObjectDefinitionsPage} from '../ViewObjectDefinitionsPage';

export class ViewObjectActionsPage {
	readonly actionsTabItem: Locator;
	readonly addObjectActionButton: Locator;
	readonly frontendDataSetItems: Locator;
	readonly lastExecutionCell: Locator;
	readonly viewObjectDefinitionsPage: ViewObjectDefinitionsPage;

	constructor(page: Page) {
		this.actionsTabItem = page.getByRole('link', {name: 'Actions'});
		this.addObjectActionButton = page.getByLabel('Add Object Action');
		this.frontendDataSetItems = page.locator('div.table-list-title a');
		this.lastExecutionCell = page.locator('.cell-status');
		this.viewObjectDefinitionsPage = new ViewObjectDefinitionsPage(page);
	}

	async goto(objectDefinitionLabel: string) {
		await this.viewObjectDefinitionsPage.goto();

		await this.viewObjectDefinitionsPage.clickEditObjectDefinitionLink(
			objectDefinitionLabel
		);

		await this.actionsTabItem.click();
	}

	async openObjectActionSidePanel() {
		await this.addObjectActionButton.click();
	}
}
