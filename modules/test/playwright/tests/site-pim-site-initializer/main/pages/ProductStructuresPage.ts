/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';
import {PORTLET_URLS} from '../../../../utils/portletUrls';

const OBJECT_FOLDER_EXTERNAL_REFERENCE_CODE = 'L_PIM_PRODUCT_TYPES';

export class ProductStructuresPage {
	readonly page: Page;
	readonly publishButton: Locator;

	constructor(page: Page) {
		this.page = page;
		this.publishButton = page
			.locator('.component-tbar')
			.getByText('Publish');
	}

	async addTab(label: string) {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {
				exact: true,
				name: 'Group',
			}),
			trigger: this.page.getByTitle('Add Field').first(),
		});

		const labelInput = this.page.getByLabel('Label');

		await labelInput.fill(label);
		await labelInput.blur();

		await expect(this.getTreeItem(label)).toBeVisible();
	}

	getTreeItem(label: string) {
		return this.page.locator('.treeview-link', {hasText: label});
	}

	async goto() {
		await expect(async () => {
			await this.page.goto(
				`${PORTLET_URLS.cmsStructureBuilder}?objectFolderExternalReferenceCode=${OBJECT_FOLDER_EXTERNAL_REFERENCE_CODE}`,
				{waitUntil: 'networkidle'}
			);

			await expect(this.publishButton).toBeVisible({timeout: 5000});
		}).toPass({timeout: 30000});
	}
}
