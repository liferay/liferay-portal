/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../utils/clickAndExpectToBeVisible';
import {PORTLET_URLS} from '../../utils/portletUrls';
import {waitForAlert} from '../../utils/waitForAlert';

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

		// Wait for the restore to complete before returning, otherwise a
		// following navigation aborts the in-flight request

		await waitForAlert(this.page, 'was restored');
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

	async restoreRename(assetName: string, newName: string) {
		await this.restore(assetName);

		// The name collides with an existing entry, so a confirmation form
		// offers to overwrite or to keep both and rename

		await this.page
			.getByLabel(
				'Keep both entries and rename the entry from the Recycle Bin as'
			)
			.check();

		await this.page.locator('[id$="TrashPortlet_newName"]').fill(newName);

		await this.page.getByRole('button', {name: 'Save'}).click();

		// The confirmation form navigates back once the restore completes

		await expect(this.page.getByRole('button', {name: 'Save'})).toHaveCount(
			0
		);
	}

	async search(term: string) {
		const searchbox = this.page.getByRole('searchbox', {
			name: 'Search for:',
		});

		await searchbox.fill(term);

		await searchbox.press('Enter');
	}

	async viewEntry(assetName: string) {
		await this._row(assetName)
			.first()
			.getByRole('link', {name: assetName})
			.click();
	}

	async restoreContentFromFolder(
		folderName: string,
		documentName: string,
		targetFolderName?: string
	) {
		await this._row(folderName)
			.first()
			.getByRole('link', {name: folderName})
			.click();

		const documentRow = this.page
			.getByRole('row')
			.filter({hasText: documentName});

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page
				.locator('.dropdown-menu.show')
				.getByText('Restore', {exact: true}),
			trigger: documentRow.getByRole('button', {name: 'Show Actions'}),
		});

		// The original folder no longer exists, so a dialog prompts for a new
		// restore location

		const selectRestoreFolderFrame =
			'iframe[title="Select Restore Folder"]';

		const selectRestoreFolder = this.page.frameLocator(
			selectRestoreFolderFrame
		);

		if (targetFolderName) {
			await selectRestoreFolder
				.getByRole('row')
				.filter({hasText: targetFolderName})
				.getByRole('button', {name: 'Select'})
				.click();
		}
		else {
			await selectRestoreFolder
				.getByRole('button', {name: 'Select Home'})
				.click();
		}

		// Restoring reloads the page and detaches the dialog once it completes

		await expect(this.page.locator(selectRestoreFolderFrame)).toHaveCount(
			0
		);
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
