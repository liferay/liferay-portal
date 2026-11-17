/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

export class TranslationManagerPage {
	readonly adminLocalizedInputContainer: Locator;
	readonly adminEnglishLocalizedInputContainer: Locator;
	readonly adminTriggerButton: Locator;
	readonly localizedInput: Locator;
	readonly page: Page;
	readonly englishTriggerButton: Locator;
	readonly triggerButton: Locator;
	readonly manageButton: Locator;
	readonly manageMenuItem: Locator;
	readonly dialog: Locator;
	readonly catalanChoice: Locator;
	readonly cancelButton: Locator;
	readonly doneButton: Locator;
	readonly addButton: Locator;
	readonly searchInput: Locator;
	readonly germanChoice: Locator;
	readonly catalanRow: Locator;
	readonly englishRow: Locator;
	readonly frenchRow: Locator;

	constructor(page: Page) {
		this.page = page;
		this.adminLocalizedInputContainer = page
			.locator('.input-localized.input-localized-input')
			.first();
		this.adminEnglishLocalizedInputContainer = page
			.getByText('Admin English (United States)')
			.first()
			.locator('.input-localized.input-localized-input')
			.first();
		this.adminTriggerButton = page
			.locator('.col', {
				has: page.getByRole('heading', {exact: true, name: 'Admin'}),
			})
			.getByTitle('Select a Language');
		this.localizedInput =
			this.adminLocalizedInputContainer.getByRole('textbox');
		this.triggerButton =
			this.adminLocalizedInputContainer.getByRole('button');
		this.englishTriggerButton =
			this.adminEnglishLocalizedInputContainer.getByRole('button');
		this.manageButton = page.getByRole('button', {
			name: 'Manage Translations',
		});
		this.manageMenuItem = page.getByRole('menuitem', {
			name: 'Manage Translations',
		});
		this.dialog = page.getByRole('dialog', {
			name: 'Manage Translations',
		});
		this.catalanChoice = page.getByRole('menuitem', {
			name: 'Not translated into Catalan.',
		});
		this.cancelButton = this.dialog.getByRole('button', {name: 'Cancel'});
		this.doneButton = this.dialog.getByRole('button', {name: 'Done'});
		this.addButton = page.getByLabel('Add', {
			exact: true,
		});
		this.searchInput = page.getByPlaceholder('Search');
		this.germanChoice = page.getByRole('menuitem', {
			name: 'Not translated into German.',
		});
		this.catalanRow = this.dialog.getByRole('row', {
			name: 'Catalan (Spain)',
		});
		this.englishRow = this.dialog.getByRole('row', {
			name: 'English (United States)',
		});
		this.frenchRow = this.dialog.getByRole('row', {
			name: 'French (France)',
		});
	}
}
