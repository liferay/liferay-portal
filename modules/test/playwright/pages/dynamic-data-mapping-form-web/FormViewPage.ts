/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';
import path from 'path';

export class FormViewPage {
	readonly languageSelector: Locator;
	readonly page: Page;

	constructor(page: Page) {
		this.languageSelector = page.getByRole('button', {
			name: 'Select a language, current',
		});
		this.page = page;
	}

	async uploadFile(page: Page, dirName: string, filePath: string) {
		const fileChooserPromise = page.waitForEvent('filechooser');

		await page.getByText('Select').click();

		const fileChooser = await fileChooserPromise;

		await fileChooser.setFiles(
			path.join(dirName, '/dependencies', filePath)
		);
	}
}
