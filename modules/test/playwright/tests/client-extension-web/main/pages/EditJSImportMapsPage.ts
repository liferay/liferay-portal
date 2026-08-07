/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {EditClientExtensionsPage} from '../../pages/EditClientExtensionsPage';

export class EditJSImportMapsPage extends EditClientExtensionsPage {
	readonly bareSpecifierInput: Locator;
	readonly javaScriptURLInput: Locator;

	constructor(page: Page) {
		super(page, 'jsImportMapsEntry');

		this.bareSpecifierInput = page.getByRole('textbox', {
			name: 'Bare Specifier',
		});
		this.javaScriptURLInput = page.getByRole('textbox', {
			name: 'JavaScript URL',
		});
	}
}
