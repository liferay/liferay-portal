/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

export class InfoPanelPage {
	readonly page: Page;
	readonly dropdownVersionAction: (versionName: string) => Locator;
	readonly dropdownVersionActionMenuItem: (action: string) => Locator;
	readonly metadataRow: (label: string | RegExp) => Locator;
	readonly metadataValue: (label: string | RegExp) => Locator;
	readonly selectTab: (tabName: string) => Locator;

	constructor(page: Page) {
		this.page = page;

		this.dropdownVersionAction = (versionName) => {
			return this.page
				.locator('li')
				.filter({
					hasText: versionName,
				})
				.getByLabel('Actions');
		};
		this.dropdownVersionActionMenuItem = (action) => {
			return this.page.getByRole('menuitem', {
				name: action,
			});
		};
		this.metadataRow = (label) =>
			this.page.locator('.asset-metadata-section').filter({
				has: this.page.locator('p.font-weight-bold').filter({
					hasText: label,
				}),
			});
		this.metadataValue = (label) =>
			this.metadataRow(label).locator('p').nth(1);
		this.selectTab = (tabName) => {
			return this.page.getByRole('tab').filter({hasText: tabName});
		};
	}

	async expectMetadata(label: string | RegExp, value: string | RegExp) {
		await expect(this.metadataValue(label)).toHaveText(value);
	}

	async expectMetadataHidden(label: string | RegExp) {
		await expect(this.metadataRow(label)).toBeHidden();
	}
}
