/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {waitForAlert} from '../../utils/waitForAlert';
import {ApplicationsMenuPage} from './ApplicationsMenuPage';

export class GogoShellPage {
	readonly applicationsMenuPage: ApplicationsMenuPage;
	readonly commandInput: Locator;
	readonly page: Page;
	readonly executeButton: Locator;

	constructor(page: Page) {
		this.page = page;

		this.applicationsMenuPage = new ApplicationsMenuPage(page);
		this.commandInput = this.page.getByLabel('Command');
		this.executeButton = this.page.getByRole('button', {name: 'Execute'});
	}

	async goto() {
		this.applicationsMenuPage.goToGogoShell();
	}

	async addCommand(command: string) {
		await this.goto();

		await this.commandInput.waitFor();
		await this.commandInput.fill(command);
		await this.executeButton.click();
		await waitForAlert(this.page);
	}
}
