/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

export class InfoPanelPage {
	readonly page: Page;
	readonly dropdownTab: (tabName: string) => Locator;
	readonly selectTab: (tabName: string) => Locator;

	constructor(page: Page) {
		this.page = page;

		this.dropdownTab = (tabName) => {
			return this.page.getByRole('menuitem', {
				exact: true,
				name: tabName,
			});
		};
		this.selectTab = (tabName) => {
			return this.page.getByRole('tab').filter({hasText: tabName});
		};
	}
}
