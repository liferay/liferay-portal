/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {PORTLET_URLS} from '../../utils/portletUrls';

export class BundleBlacklistPage {
	readonly blacklistBundleSymbolicInput: Locator;
	readonly page: Page;
	readonly updateButton: Locator;

	constructor(page: Page) {
		this.blacklistBundleSymbolicInput = page.getByRole('textbox', {
			name: 'Blacklist Bundle Symbolic',
		});
		this.page = page;
		this.updateButton = page.getByRole('button', {name: 'Update'});
	}

	async goto() {
		this.page.goto(PORTLET_URLS.systemBundleBlacklist);
	}
}
