/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

export class ConnectorSchedulePage {
	readonly backLink: Locator;
	readonly cronExpressionInput: Locator;
	readonly page: Page;

	constructor(page: Page) {
		this.backLink = page.getByRole('link', {exact: true, name: 'Back'});
		this.cronExpressionInput = page.getByLabel('Cron Expression', {
			exact: true,
		});
		this.page = page;
	}
}
