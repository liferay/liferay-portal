/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../utils/clickAndExpectToBeVisible';
import {waitForAlert} from '../../utils/waitForAlert';

const MOVED_TO_RECYCLE_BIN = (name: string) =>
	`The element ${name} was moved to the Recycle Bin.`;

export class WebContentPage {
	readonly page: Page;

	constructor(page: Page) {
		this.page = page;
	}

	async assertEntryAbsent(title: string) {
		await expect(this._row(title)).toHaveCount(0);
	}

	async assertEntryPresent(title: string) {
		await expect(this._row(title).first()).toBeVisible();
	}

	async goto(siteUrl?: Site['friendlyUrlPath']) {
		await this.page.goto(
			`/group${siteUrl || '/guest'}/~/control_panel/manage?p_p_id=com_liferay_journal_web_portlet_JournalPortlet`
		);
	}

	async gotoRecycleBinEntryViaSuccessMessage() {
		await this.page
			.locator('[id$="recycleBinAlert"]')
			.getByRole('link', {name: 'Recycle Bin'})
			.click();
	}

	async moveFolderToRecycleBin(name: string) {
		const row = this.page
			.locator('[data-qa-id="row"][data-folder="true"]')
			.filter({hasText: name})
			.first();

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page
				.locator('.dropdown-menu:visible')
				.getByText('Delete', {exact: true}),
			trigger: row.getByRole('button', {name: 'Show Actions'}),
		});

		await waitForAlert(this.page, MOVED_TO_RECYCLE_BIN(name));
	}

	async moveToRecycleBin(title: string, {autoClose = true} = {}) {
		await this._openRowAction(title, 'Delete');

		await waitForAlert(this.page, MOVED_TO_RECYCLE_BIN(title), {autoClose});
	}

	async moveToRecycleBinViaDeleteIcon(title: string) {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('button', {
				exact: true,
				name: 'Delete',
			}),
			trigger: this.page.getByLabel('Select All Items on the Page'),
		});

		await waitForAlert(this.page, MOVED_TO_RECYCLE_BIN(title));
	}

	async undoMoveToRecycleBin() {
		await this.page
			.locator('[id$="recycleBinAlert"]')
			.getByRole('button', {name: 'Undo'})
			.click();
	}

	_row(title: string): Locator {
		return this.page.locator('[data-qa-id="row"]').filter({hasText: title});
	}

	async _openRowAction(title: string, action: string) {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page
				.locator('.dropdown-menu:visible')
				.getByText(action, {exact: true}),
			trigger: this.page.getByRole('button', {
				name: `Actions for ${title}`,
			}),
		});
	}
}
