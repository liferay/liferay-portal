/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../utils/clickAndExpectToBeVisible';
import {waitForPageToBeLoaded} from '../../utils/waitForPageToBeLoaded';

export class LaunchPreviewPage {
	readonly page: Page;

	readonly topbar: Locator;

	constructor(page: Page) {
		this.page = page;

		this.topbar = page.getByRole('navigation', {name: 'Preview'});
	}

	getLaunchPicker(currentName: string): Locator {
		return this.topbar.getByRole('button', {
			exact: true,
			name: currentName,
		});
	}

	async switchLaunch(currentName: string, name: string) {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {exact: true, name}),
			trigger: this.getLaunchPicker(currentName),
		});

		await expect(this.getLaunchPicker(name)).toBeVisible();

		await waitForPageToBeLoaded(this.page);
	}
}
