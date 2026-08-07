/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {EditClientExtensionsPage} from '../../pages/EditClientExtensionsPage';

export class EditIFramePage extends EditClientExtensionsPage {
	readonly friendlyURLMappingInput: Locator;
	readonly urlInput: Locator;

	constructor(page: Page) {
		super(page, 'iframe');

		this.friendlyURLMappingInput = page.locator(
			`[name=_${this.portletName}_friendlyURLMapping]`
		);
		this.urlInput = page.locator(`[name=_${this.portletName}_url]`);
	}

	async fillRequiredFields() {
		await this.nameInput.fill('Test IFrame');
		await this.urlInput.fill('https://www.example.com');
	}
}
