/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page} from '@playwright/test';

import {zipFolder} from '../../../../utils/zip';

export class ExportImportFramePage {
	readonly page: Page;

	constructor(page: Page) {
		this.page = page;
	}

	async importLARFile(folderPath: string) {
		let iframeElement = await this.page.locator('iframe').elementHandle();
		let frame = await iframeElement.contentFrame();
		await frame.waitForLoadState();

		const exportImportFrame = this.page.frameLocator(
			'iframe[title="Export \\/ Import"]'
		);

		await exportImportFrame.getByRole('link', {name: 'Import'}).click();

		const fileChooserPromise = this.page.waitForEvent('filechooser');

		await exportImportFrame
			.getByRole('button', {name: 'Select File'})
			.click();

		const fileChooser = await fileChooserPromise;

		await fileChooser.setFiles(await zipFolder(folderPath));

		await exportImportFrame.getByRole('button', {name: 'Continue'}).click();
		await exportImportFrame.getByRole('button', {name: 'Import'}).click();

		iframeElement = await this.page.locator('iframe').elementHandle();
		frame = await iframeElement.contentFrame();
		await frame.waitForLoadState();

		await frame.waitForSelector(
			'[data-qa-id=row]:nth-of-type(1) .background-task-status-successful',
			{state: 'visible'}
		);
	}

	async close() {
		await this.page.getByLabel('Close', {exact: true}).click();
	}
}
