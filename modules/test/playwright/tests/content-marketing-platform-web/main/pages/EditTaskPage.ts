/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

export class EditTaskPage {
	readonly descriptionInput: Locator;
	readonly descriptionToolbar: Locator;
	readonly page: Page;
	readonly saveButton: Locator;
	readonly titleInput: Locator;

	constructor(page: Page) {
		this.descriptionInput = page.getByRole('textbox', {
			name: /^Description/,
		});
		this.descriptionToolbar = page.getByRole('toolbar', {
			name: 'Editor toolbar',
		});
		this.page = page;
		this.saveButton = page.getByRole('button', {
			name: 'Save',
		});
		this.titleInput = page.getByPlaceholder('Untitled Task');
	}
}
