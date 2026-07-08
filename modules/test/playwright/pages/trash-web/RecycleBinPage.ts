/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {PORTLET_URLS} from '../../utils/portletUrls';

export class RecycleBinPage {
	readonly page: Page;

	constructor(page: Page) {
		this.page = page;
	}

	async assertEntry(assetName: string, assetType?: string) {
		await expect(this._row(assetName).first()).toBeVisible();

		if (assetType) {
			await expect(
				this._row(assetName).filter({hasText: assetType}).first()
			).toBeVisible();
		}
	}

	async assertEntryAbsent(assetName: string) {
		await expect(this._row(assetName)).toHaveCount(0);
	}

	async assertEntryCount(assetName: string, count: number) {
		await expect(this._row(assetName)).toHaveCount(count);
	}

	async bulkRestore(assetNames: string[]) {
		for (const assetName of assetNames) {
			await this._row(assetName).first().getByRole('checkbox').check();
		}

		await this.page.getByRole('button', {name: 'Restore'}).click();
	}

	async delete(assetName: string) {
		await this._openRowAction(assetName, 'Delete');

		await this.page
			.getByRole('dialog')
			.getByRole('button', {exact: true, name: 'Delete'})
			.click();
	}

	async goto(siteUrl?: Site['friendlyUrlPath']) {
		await this.page.goto(
			`/group${siteUrl || '/guest'}${PORTLET_URLS.recycleBin}`
		);
	}

	async restore(assetName: string) {
		await this._openRowAction(assetName, 'Restore');
	}

	_row(assetName: string): Locator {
		return this.page
			.locator('[data-qa-id="row"]')
			.filter({hasText: assetName});
	}

	async _openRowAction(assetName: string, action: string) {
		const row = this._row(assetName).first();

		const menuItem = this.page
			.locator('.dropdown-menu.show')
			.getByText(action, {exact: true});

		// The row actions toggle is only revealed on hover, so trigger it
		// directly

		await expect(async () => {
			await row
				.locator('.component-action.dropdown-toggle')
				.first()
				.dispatchEvent('click');

			await expect(menuItem).toBeVisible({timeout: 2000});
		}).toPass();

		await menuItem.click();
	}
}
