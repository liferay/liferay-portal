/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Page, expect} from '@playwright/test';

export class DepotItemSelectorPage {
	readonly page: Page;

	constructor(page: Page) {
		this.page = page;
	}

	getItemSelector(title: string): FrameLocator {
		return this.page.frameLocator(`iframe[title="${title}"]`);
	}

	async openDepotFolder({
		depotName,
		folderName,
		itemSelector,
	}: {
		depotName: string;
		folderName?: string;
		itemSelector: FrameLocator;
	}) {
		await itemSelector
			.getByRole('link', {exact: true, name: 'Sites and Libraries'})
			.click();

		await itemSelector.getByText('Asset Library', {exact: true}).click();

		await itemSelector.getByText(depotName, {exact: true}).click();

		const breadcrumb = itemSelector.locator('.breadcrumb');

		await expect(
			breadcrumb.getByRole('link', {exact: true, name: depotName})
		).toBeVisible();

		if (folderName) {
			await itemSelector
				.getByRole('link', {exact: true, name: folderName})
				.click();

			await expect(breadcrumb).toContainText(folderName);
		}
	}
}
