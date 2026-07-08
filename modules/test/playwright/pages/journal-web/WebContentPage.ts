/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../utils/clickAndExpectToBeVisible';
import {waitForAlert} from '../../utils/waitForAlert';

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

	async moveToRecycleBin(title: string) {
		await this._openRowAction(title, 'Delete');

		await waitForAlert(
			this.page,
			`The element ${title} was moved to the Recycle Bin.`
		);
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
