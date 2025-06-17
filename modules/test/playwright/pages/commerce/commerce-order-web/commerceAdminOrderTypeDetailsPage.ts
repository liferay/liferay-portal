/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

export class CommerceAdminOrderTypeDetailsPage {
	readonly activeToggle: Locator;
	readonly headerDetailsTitle: Locator;
	readonly orderTypeId: Locator;
	readonly publishLink: Locator;

	constructor(page: Page) {
		this.activeToggle = page.getByLabel('Active', {exact: true});
		this.headerDetailsTitle = page.getByTestId('headerDetailsTitle');
		this.orderTypeId = page
			.getByText('ID')
			.locator('..')
			.locator('.header-info-value');
		this.publishLink = page.getByRole('link', {name: 'Publish'});
	}
}
