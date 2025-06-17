/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';
import {waitForAlert} from '../../../../utils/waitForAlert';

export class FolderPage {
	readonly page: Page;

	readonly newButton: Locator;
	readonly publishButton: Locator;

	constructor(page: Page) {
		this.page = page;

		this.newButton = page.getByLabel('New');
	}

	async createFolder(name: string) {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {name: 'Folder'}),
			trigger: this.newButton,
		});

		const dialog = this.page.getByRole('dialog', {name: 'New Folder'});

		await dialog.waitFor();

		await dialog.getByLabel('Name').fill(name);

		await dialog.getByRole('button', {name: 'Save'}).click();

		await waitForAlert(
			this.page,
			`Success:${name} was created successfully`
		);
	}

	async clickOption(folderName: string, optionName: string) {
		const card = this.page
			.locator('tr', {hasText: folderName})
			.or(this.page.locator('.card-row', {hasText: folderName}));

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {name: optionName}),
			trigger: card.getByLabel('More actions'),
		});
	}

	async deleteFolder(folderName: string) {
		this.page.on('dialog', (dialog) => dialog.accept());

		await this.clickOption(folderName, 'Delete');
	}
}
