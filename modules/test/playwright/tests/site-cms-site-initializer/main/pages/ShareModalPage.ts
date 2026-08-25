/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';

export class ShareModalPage {
	readonly collaboratorInput: Locator;
	readonly inviteExternalUserOption: Locator;
	readonly page: Page;
	readonly submitButton: Locator;

	constructor(page: Page) {
		this.collaboratorInput = page.getByPlaceholder(
			'Enter name, email, or groups'
		);
		this.inviteExternalUserOption = page.getByText('Invite external user', {
			exact: false,
		});
		this.page = page;
		this.submitButton = page
			.getByRole('dialog')
			.getByRole('button', {exact: true, name: 'Share'});
	}

	collaborator(name: string) {
		return this.page.getByRole('listitem').filter({hasText: name});
	}

	getHeader(title: string) {
		return this.page.getByText(`Share "${title}"`);
	}

	async removeAccess(name: string) {
		const collaborator = this.collaborator(name);

		// The dropdown menu renders outside the collaborator list item, so the
		// menu item is scoped to the page and not to the collaborator.

		const removeAccessItem = this.page.getByRole('menuitem', {
			exact: true,
			name: 'Remove Access',
		});

		await clickAndExpectToBeVisible({
			target: removeAccessItem,
			trigger: collaborator.getByLabel('More Options'),
		});

		await removeAccessItem.click();
	}

	async submit() {
		await this.submitButton.click();
	}

	async typeInCollaboratorInput(value: string) {
		await this.collaboratorInput.click();

		await this.collaboratorInput.fill(value);
	}
}
