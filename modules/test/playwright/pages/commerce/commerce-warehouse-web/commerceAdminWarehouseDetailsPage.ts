/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

export class CommerceAdminWarehouseDetailsPage {
	readonly detailsActiveToggle: Locator;
	readonly page: Page;
	readonly geolocationField: (coordinates: string) => Locator;
	readonly saveButton: Locator;
	readonly warehouseId: Locator;

	constructor(page: Page) {
		this.detailsActiveToggle = page.getByLabel('Active');
		this.geolocationField = (coordinates) => page.getByLabel(coordinates);
		this.page = page;
		this.saveButton = page.getByRole('link', {exact: true, name: 'Save'});
		this.warehouseId = page.locator('span:has-text("ID")+strong');
	}
}
