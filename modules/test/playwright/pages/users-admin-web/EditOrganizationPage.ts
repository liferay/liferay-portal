/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../utils/clickAndExpectToBeVisible';
import {UsersAndOrganizationsPage} from './UsersAndOrganizationsPage';

export class EditOrganizationPage {
	readonly categoryGridCell: (categoryName: string) => Locator;
	readonly categoryOption: (categoryName: string) => Locator;
	readonly categoryInput: (vocabularyName: string) => Locator;
	readonly createSiteToggle: Locator;
	readonly commentsInput: Locator;
	readonly contactLink: Locator;
	readonly countrySelect: Locator;
	readonly editOrgLaborIconMenu: Locator;
	readonly headerTitle: Locator;
	readonly manageSiteLink: Locator;
	readonly nameInput: Locator;
	readonly openingHoursLink: Locator;
	readonly organizationEditMenuItem: Locator;
	readonly organizationSiteLink: Locator;
	readonly organizationSiteSaveButton: Locator;
	readonly orgLaborListTypeSelectedValue: Locator;
	readonly page: Page;
	readonly regionSelect: Locator;
	readonly saveButton: Locator;
	readonly typeLabel: Locator;
	readonly usersAndOrganizationsPage: UsersAndOrganizationsPage;

	constructor(page: Page) {
		this.categoryGridCell = (categoryName: string) =>
			page.getByRole('gridcell', {exact: true, name: categoryName});
		this.categoryOption = (categoryName: string) =>
			page.getByRole('option', {name: categoryName});
		this.categoryInput = (vocabularyName: string) =>
			page.getByLabel(vocabularyName, {exact: true});
		this.createSiteToggle = page.getByLabel('Create Site');
		this.commentsInput = page.locator(
			'#_com_liferay_users_admin_web_portlet_UsersAdminPortlet_comments'
		);
		this.contactLink = page.getByRole('link', {name: 'Contact'});
		this.countrySelect = page.locator(
			'#_com_liferay_users_admin_web_portlet_UsersAdminPortlet_countryId'
		);
		this.editOrgLaborIconMenu = page.getByTestId('editOrgLaborIconMenu');
		this.headerTitle = page.getByTestId('headerTitle');
		this.manageSiteLink = page.getByRole('link', {name: 'Manage Site'});
		this.nameInput = page.locator(
			'#_com_liferay_users_admin_web_portlet_UsersAdminPortlet_name'
		);
		this.openingHoursLink = page.getByRole('link', {name: 'Opening Hours'});
		this.organizationEditMenuItem = page.getByRole('menuitem', {
			name: 'Edit',
		});
		this.organizationSiteLink = page.getByRole('link', {
			name: 'Organization Site',
		});
		this.organizationSiteSaveButton = page.getByRole('button', {
			name: 'Save',
		});
		this.orgLaborListTypeSelectedValue = page
			.locator(
				'#_com_liferay_users_admin_web_portlet_UsersAdminPortlet_orgLaborListTypeId'
			)
			.locator('option[selected=""]');
		this.page = page;
		this.regionSelect = page.locator(
			'#_com_liferay_users_admin_web_portlet_UsersAdminPortlet_regionId'
		);
		this.saveButton = page.getByRole('button', {name: 'Save'});
		this.typeLabel = page.getByLabel('Type Label');
		this.usersAndOrganizationsPage = new UsersAndOrganizationsPage(page);
	}

	async gotoOrganizationEditOpeningHoursTab(organizationName: string) {
		await (
			await this.usersAndOrganizationsPage.organizationsTable.rowActions(
				organizationName
			)
		).click();
		await this.organizationEditMenuItem.click();
		await this.contactLink.click();
		await this.openingHoursLink.click();
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.organizationEditMenuItem,
			trigger: this.editOrgLaborIconMenu,
		});
	}
}
