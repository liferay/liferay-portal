/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';
import * as path from 'path';

import {EditClientExtensionsPage} from '../../pages/EditClientExtensionsPage';

export class EditThemeCSSClientExtensionsPage extends EditClientExtensionsPage {
	readonly themeCSSFrontendTokenDefinitionSelectFileButton: Locator;

	constructor(page: Page) {
		super(page, 'themeCSS');

		this.themeCSSFrontendTokenDefinitionSelectFileButton = page
			.getByRole('button', {exact: true, name: 'Select File'})
			.or(page.getByRole('button', {name: 'Replace File'}));
	}

	async uploadFrontendTokenDefinitionFile(dirname: string, fileName: string) {
		const fileChooserPromise = this.page.waitForEvent('filechooser');

		await this.themeCSSFrontendTokenDefinitionSelectFileButton.click();

		const fileChooser = await fileChooserPromise;

		await fileChooser.setFiles(
			path.join(dirname, '/dependencies/' + fileName)
		);
	}
}
