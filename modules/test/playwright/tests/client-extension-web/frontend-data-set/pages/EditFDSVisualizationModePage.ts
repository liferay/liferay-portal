/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {EditClientExtensionsPage} from '../../pages/EditClientExtensionsPage';

export class EditFDSVisualizationModePage extends EditClientExtensionsPage {
	readonly iconInput: Locator;
	readonly javaScriptURLInput: Locator;

	constructor(page: Page) {
		super(page, 'fdsVisualizationMode');

		this.iconInput = page.locator(`[name=_${this.portletName}_icon]`);
		this.javaScriptURLInput = page.locator(
			`[name=_${this.portletName}_url]`
		);
	}

	async selectIcon(icon: string) {
		await this.page.getByRole('button', {name: 'Add Icon'}).click();

		const dialog = this.page.getByRole('dialog', {name: 'Select an Icon'});

		await dialog.getByRole('searchbox', {name: 'Search'}).fill(icon);
		await dialog.getByRole('button', {exact: true, name: icon}).click();

		await dialog.waitFor({state: 'hidden'});
	}
}
