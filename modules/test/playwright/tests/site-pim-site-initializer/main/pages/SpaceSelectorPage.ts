/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';

export class SpaceSelectorPage {
	readonly dialog: Locator;
	readonly generalTab: Locator;
	readonly page: Page;
	readonly saveButton: Locator;
	readonly spaceSelect: Locator;

	constructor(page: Page) {
		this.dialog = page.getByRole('dialog');
		this.generalTab = page.getByRole('tab', {name: 'General'});
		this.page = page;
		this.saveButton = this.dialog.getByRole('button', {name: 'Save'});
		this.spaceSelect = this.dialog.getByLabel('Space');
	}

	getSpaceOption(space: string) {
		return this.page.getByRole('option', {name: space});
	}

	async selectSpace(space: string) {
		const shown = await Promise.race([
			this.dialog.waitFor({state: 'visible'}).then(() => 'spaceSelector'),
			this.generalTab
				.waitFor({state: 'visible'})
				.then(() => 'contentEditor'),
		]);

		if (shown === 'contentEditor') {
			return;
		}

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.getSpaceOption(space),
			trigger: this.spaceSelect,
		});

		await this.saveButton.click();
	}
}
