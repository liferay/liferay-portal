/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page} from '@playwright/test';

import {clickAndExpectToBeHidden} from '../../../utils/clickAndExpectToBeHidden';
import {GlobalMenuPage} from '../../product-navigation-applications-menu/GlobalMenuPage';

export class CommerceAdminCatalogsPage {
	readonly addCatalogsButton: Locator;
	readonly globalMenuPage: GlobalMenuPage;
	readonly catalogActionsButton: (catalogName: string) => Locator;
	readonly catalogId: Locator;
	readonly catalogLink: (name: string) => Locator;
	readonly catalogPermissionsButton: (catalogName: string) => Locator;
	readonly catalogRow: (catalogName: string) => Locator;
	readonly catalogSaveButton: Locator;
	readonly deleteMenuItem: Locator;
	readonly editMenuItem: Locator;
	readonly managementToolbarSearchInput: Locator;
	readonly modalCurrencySelect: Locator;
	readonly modalFieldName: Locator;
	readonly modalFrameLocator: FrameLocator;
	readonly modalLanguageSelect: Locator;
	readonly modalLinkSupplierAutocomplete: Locator;
	readonly modalLinkSupplierDropdownItem: (name: string) => Locator;
	readonly modalSubmitButton: Locator;
	readonly page: Page;
	readonly permissionCheckbox: (
		roleName: string,
		actionLabel: string
	) => Locator;
	readonly permissionsDialog: Locator;
	readonly permissionsFrame: FrameLocator;
	readonly permissionsMenuItem: Locator;
	readonly permissionsSaveButton: Locator;
	readonly permissionsSearchInput: Locator;

	constructor(page: Page) {
		this.addCatalogsButton = page
			.getByTestId('managementToolbar')
			.locator('[data-testid="fdsCreationActionButton"]');
		this.globalMenuPage = new GlobalMenuPage(page);
		this.catalogActionsButton = (catalogName: string) =>
			page.getByRole('button', {
				exact: true,
				name: `${catalogName} Actions`,
			});
		this.catalogId = page.locator('span:has-text("ID")+strong');
		this.catalogLink = (name: string) =>
			page.getByRole('link', {exact: true, name});
		this.catalogPermissionsButton = (catalogName: string) =>
			this.catalogRow(catalogName).getByRole('button', {
				exact: true,
				name: 'Permissions',
			});
		this.catalogRow = (catalogName: string) =>
			page.getByRole('row', {name: catalogName});
		this.catalogSaveButton = page.getByRole('link', {
			exact: true,
			name: 'Save',
		});
		this.deleteMenuItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Delete',
		});
		this.editMenuItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Edit',
		});
		this.managementToolbarSearchInput = page
			.getByTestId('managementToolbar')
			.getByPlaceholder('Search', {exact: true});
		this.modalFrameLocator = page.frameLocator('.fds-modal-body iframe');
		this.modalCurrencySelect = this.modalFrameLocator.locator(
			'select[name="currencyCode"]'
		);
		this.modalFieldName =
			this.modalFrameLocator.getByLabel('Name Required');
		this.modalLanguageSelect = this.modalFrameLocator.locator(
			'select[name="defaultLanguageId"]'
		);
		this.modalLinkSupplierAutocomplete = this.modalFrameLocator
			.locator('#link-account-entry-autocomplete-root input[type="text"]')
			.first();
		this.modalLinkSupplierDropdownItem = (name: string) =>
			this.modalFrameLocator
				.locator('.autocomplete-dropdown-menu')
				.getByText(name, {exact: true});
		this.modalSubmitButton = this.modalFrameLocator.getByRole('button', {
			exact: true,
			name: 'Submit',
		});
		this.page = page;
		this.permissionsDialog = page.getByRole('dialog', {
			name: 'Permissions',
		});
		this.permissionsFrame = page.frameLocator(
			'iframe[title="Permissions"]'
		);
		this.permissionCheckbox = (roleName: string, actionLabel: string) =>
			this.permissionsFrame.getByLabel(
				`Give ${actionLabel} permission to users with the ${roleName} role.`,
				{exact: true}
			);
		this.permissionsMenuItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Permissions',
		});
		this.permissionsSaveButton = this.permissionsFrame.getByRole('button', {
			exact: true,
			name: 'Save',
		});
		this.permissionsSearchInput = this.permissionsFrame.getByPlaceholder(
			'Search for',
			{exact: true}
		);
	}

	async closePermissionsDialog() {
		await clickAndExpectToBeHidden({
			target: this.permissionsDialog,
			trigger: this.permissionsDialog.getByRole('button', {
				exact: true,
				name: 'Close',
			}),
		});
	}

	async goto() {
		await this.globalMenuPage.goToCommerce('Catalogs');
	}

	async search(catalogName: string) {
		await this.managementToolbarSearchInput.fill(catalogName);
		await this.managementToolbarSearchInput.press('Enter');
	}
}
