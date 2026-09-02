/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';
import {PORTLET_URLS} from '../../../../utils/portletUrls';
import {getTempDir} from '../../../../utils/temp';
import {DataSetPage} from './DataSetPage';

export class StructuresPage {
	readonly page: Page;
	readonly dataSetFragmentPage: DataSetPage;

	constructor(page: Page) {
		this.page = page;
		this.dataSetFragmentPage = new DataSetPage(page);
	}

	async goto() {
		await expect(async () => {
			await this.page.goto(PORTLET_URLS.cmsStructures);

			await this.page.locator('.fds').waitFor({timeout: 3000});
		}).toPass();
	}

	getItem(filter: string) {
		return this.dataSetFragmentPage.getRow(filter);
	}

	async execItemAction({
		action,
		filter,
		timeout,
	}: {
		action:
			| 'Delete'
			| 'Edit'
			| 'Export as JSON'
			| 'Permissions'
			| 'View Usages';
		filter: string;
		timeout?: number;
	}) {
		await this.dataSetFragmentPage.execItemAction({
			action,
			filter,
			timeout,
		});
	}

	async exportStructureAsJSON(structureLabel: string) {
		await this.goto();

		const downloadPromise = this.page.waitForEvent('download');

		await this.execItemAction({
			action: 'Export as JSON',
			filter: structureLabel,
		});

		const download = await downloadPromise;

		const filePath = `${getTempDir()}/${download.suggestedFilename()}`;

		await download.saveAs(filePath);

		return filePath;
	}

	async importStructureFromJSON(
		filePath: string,
		{override = true}: {override?: boolean} = {}
	) {
		await this.openMenuItem('Import from JSON');

		const dialog = this.page.getByRole('dialog', {
			name: 'Import Content Structures',
		});

		const fileChooserPromise = this.page.waitForEvent('filechooser');

		await dialog.getByRole('button', {name: 'Select File'}).click();

		const fileChooser = await fileChooserPromise;

		await fileChooser.setFiles(filePath);

		await dialog.getByRole('button', {exact: true, name: 'Import'}).click();

		// When a structure with the same external reference code already exists
		// the file-select modal closes and an override warning modal opens;
		// confirm it to proceed.

		if (override) {
			await this.page
				.getByRole('button', {name: 'Select File'})
				.waitFor({state: 'hidden'});

			await this.page
				.getByRole('button', {exact: true, name: 'Import'})
				.click();
		}
	}

	async openMenuItem(action: 'Export' | 'Import' | 'Import from JSON') {
		await this.goto();

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {
				exact: true,
				name: action,
			}),
			trigger: this.page.getByRole('button', {name: 'More Actions'}),
		});
	}
}
