/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect} from '@playwright/test';

export class PageTreePage {
	readonly page: Page;

	constructor(page: Page) {
		this.page = page;
	}

	async open() {
		const pageTree = this.page
			.getByLabel('Product Menu')
			.locator('.treeview');

		if (!(await pageTree.isVisible())) {
			await this.page
				.getByRole('button', {exact: true, name: 'Page Tree'})
				.click();

			await pageTree.waitFor();
		}
	}

	async close() {
		const pageTree = this.page
			.getByLabel('Product Menu')
			.locator('.treeview');

		if (await pageTree.isVisible()) {
			const button = this.page.getByRole('button', {
				name: 'Back to Menu',
			});

			await expect(async () => {
				await button.click();

				await expect(
					this.page.locator("//div[@data-qa-id='productMenuBody']")
				).toBeVisible();

				await expect(
					this.page.locator('.sidebar-body > div')
				).toHaveClass(/panel-group/, {timeout: 2000});
			}).toPass();
		}
	}
}
