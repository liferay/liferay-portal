/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

export type TSharingPermission = 'Comment' | 'Update' | 'View';

export class SharedContentViewerPage {
	readonly actionsButton: Locator;
	readonly addCommentField: Locator;
	readonly page: Page;

	constructor(page: Page) {
		this.actionsButton = page.getByRole('button', {
			exact: true,
			name: 'Actions',
		});
		this.addCommentField = page.locator('.add-comment');
		this.page = page;
	}

	async assertSharingPermission(
		title: string,
		permission: TSharingPermission
	) {
		await expect(
			this.page.getByRole('heading', {name: title})
		).toBeVisible();

		// Only Comment and Update grant a comment box.

		if (permission === 'View') {
			await expect(this.addCommentField).toBeHidden();
		}
		else {
			await expect(this.addCommentField).toBeVisible();
		}

		// Only Update exposes the Edit action. The viewer omits the Actions
		// menu entirely for a non-editable blog entry, so open it only when it
		// is present.

		const editMenuItem = this.page.getByRole('menuitem', {
			exact: true,
			name: 'Edit',
		});

		if (permission === 'Update') {
			await this.actionsButton.click();

			await expect(editMenuItem).toBeVisible();

			await this.page.keyboard.press('Escape');
		}
		else if (await this.actionsButton.isVisible()) {
			await this.actionsButton.click();

			await expect(editMenuItem).toBeHidden();

			await this.page.keyboard.press('Escape');
		}
		else {
			await expect(editMenuItem).toBeHidden();
		}
	}
}
