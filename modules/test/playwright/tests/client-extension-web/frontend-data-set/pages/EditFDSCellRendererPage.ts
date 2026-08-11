/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {EditClientExtensionsPage} from '../../pages/EditClientExtensionsPage';

export class EditFDSCellRendererPage extends EditClientExtensionsPage {
	readonly javaScriptURLInput: Locator;

	constructor(page: Page) {
		super(page, 'fdsCellRenderer');

		this.javaScriptURLInput = page.locator(
			`[name=_${this.portletName}_url]`
		);
	}
}
