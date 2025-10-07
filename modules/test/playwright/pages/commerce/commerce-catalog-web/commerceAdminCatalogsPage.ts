/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page} from '@playwright/test';

import {ApplicationsMenuPage} from '../../product-navigation-applications-menu/ApplicationsMenuPage';
export class CommerceAdminCatalogsPage {
	readonly addCatalogsButton: Locator;
	readonly applicationsMenuPage: ApplicationsMenuPage;
	readonly catalogActionsButton: Locator;
	readonly catalogId: Locator;
	readonly catalogSaveButton: Locator;
	readonly modalFieldName: Locator;
	readonly modalFrameLocator: FrameLocator;
	readonly modalSubmitButton: Locator;
	readonly page: Page;
	readonly permissionsFrame: FrameLocator;
	readonly permissionsMenuItem: Locator;

	constructor(page: Page) {
		this.addCatalogsButton = page
			.getByTestId('managementToolbar')
			.locator('[data-testid="fdsCreationActionButton"]');
		this.applicationsMenuPage = new ApplicationsMenuPage(page);
		this.catalogActionsButton = page.getByRole('button', {
			exact: true,
			name: 'Actions',
		});
		this.catalogId = page.locator('span:has-text("ID")+strong');
		this.catalogSaveButton = page.getByRole('link', {
			exact: true,
			name: 'Save',
		});
		this.modalFrameLocator = page.frameLocator('.fds-modal-body iframe');
		this.modalFieldName =
			this.modalFrameLocator.getByLabel('Name Required');
		this.modalSubmitButton = this.modalFrameLocator.getByRole('button', {
			exact: true,
			name: 'Submit',
		});
		this.page = page;
		this.permissionsFrame = page.frameLocator(
			'iframe[title="Permissions"]'
		);
		this.permissionsMenuItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Permissions',
		});
	}

	async goto() {
		await this.applicationsMenuPage.goToCommerceCatalogs();
	}
}
