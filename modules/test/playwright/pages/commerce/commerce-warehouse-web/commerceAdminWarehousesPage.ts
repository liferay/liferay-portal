/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page} from '@playwright/test';

import {ApplicationsMenuPage} from '../../product-navigation-applications-menu/ApplicationsMenuPage';
export class CommerceAdminWarehousesPage {
	readonly addButton: Locator;
	readonly applicationsMenuPage: ApplicationsMenuPage;
	readonly page: Page;
	readonly modalFieldName: Locator;
	readonly modalFrameLocator: FrameLocator;
	readonly modalSubmitButton: Locator;

	constructor(page: Page) {
		this.addButton = page
			.getByTestId('management-toolbar')
			.locator('[data-testid="fdsCreationActionButton"]');
		this.applicationsMenuPage = new ApplicationsMenuPage(page);
		this.modalFrameLocator = page.frameLocator('.fds-modal-body iframe');
		this.modalFieldName =
			this.modalFrameLocator.getByLabel('Name Required');
		this.modalSubmitButton = this.modalFrameLocator.getByRole('button', {
			exact: true,
			name: 'Submit',
		});
		this.page = page;
	}

	async goto() {
		await this.applicationsMenuPage.goToCommerceWarehouses();
	}
}
