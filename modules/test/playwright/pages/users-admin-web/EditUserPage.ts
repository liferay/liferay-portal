/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page} from '@playwright/test';

import {searchTableRowByValue} from './UsersAndOrganizationsPage';

export class EditUserPage {
	readonly accountsLink: Locator;
	readonly appsLink: Locator;
	readonly backLink: Locator;
	readonly cancelButton: Locator;
	readonly changeImageButton: Locator;
	readonly clearImageButton: Locator;
	readonly confirmButton: Locator;
	readonly customField: (fieldName: string) => Promise<Locator>;
	readonly doneButton: Locator;
	readonly emailAddressError: Locator;
	readonly emailAddressInput: Locator;
	readonly emailAddressInvalidError: Locator;
	readonly firstNameInput: Locator;
	readonly generateWebDAVPasswordButton: Locator;
	readonly informationLink: Locator;
	readonly lastNameInput: Locator;
	readonly maxFileSizeText: Locator;
	readonly membershipsAccountsRemoveButton: (accountName: string) => Locator;
	readonly membershipsAccountsTableRow: (
		colPosition: number,
		value: string,
		strictEqual?: boolean
	) => Promise<{column: Locator; row: Locator}>;
	readonly membershipsAccountsTable: Locator;
	readonly membershipsLink: Locator;
	readonly membershipsNoAccountsMessage: Locator;
	readonly membershipsUserGroupsTableRow: (
		colPosition: number,
		value: string,
		strictEqual?: boolean
	) => Promise<{column: Locator; row: Locator}>;
	readonly membershipsUserGroupsTable: Locator;
	readonly organizationsLink: Locator;
	readonly organizationsTable: Locator;
	readonly page: Page;
	readonly passwordConfirmationFrame: FrameLocator;
	readonly passwordLink: Locator;
	readonly profileAndDashboardLink: Locator;
	readonly regularRoleCell: (name: string) => Locator;
	readonly regularRoleCellButton: (name: string) => Locator;
	readonly rolesLink: Locator;
	readonly saveButton: Locator;
	readonly screenNameError: Locator;
	readonly screenNameInput: Locator;
	readonly selectAccountsButton: Locator;
	readonly selectOrganizationButton: Locator;
	readonly selectOrganizationRolesButton: Locator;
	readonly selectOrganizationRolesFrame: FrameLocator;
	readonly selectOrganizationRolesFrameCell: (name: string) => Locator;
	readonly selectOrganizationRolesTable: Locator;
	readonly selectOrganizationRolesTableRow: (
		colPosition: number,
		value: string,
		strictEqual?: boolean
	) => Promise<{column: Locator; row: Locator}>;
	readonly selectOrganizationRolesSearchBar: Locator;
	readonly selectOrganizationRolesSearchBarButton: Locator;
	readonly selectOrganizationsTable: Locator;
	readonly selectOrganizationsTableRow: (
		colPosition: number,
		value: string,
		strictEqual?: boolean
	) => Promise<{column: Locator; row: Locator}>;
	readonly selectRegularRolesButton: Locator;
	readonly selectRegularRolesChooseButton: (name: string) => Locator;
	readonly selectRegularRolesFrame: FrameLocator;
	readonly selectRegularRolesSearchInput: Locator;
	readonly selectSiteRolesButton: Locator;
	readonly selectSiteRolesFrame: FrameLocator;
	readonly selectSiteRolesTable: Locator;
	readonly selectSiteRolesTableRow: (
		colPosition: number,
		value: string,
		strictEqual?: boolean
	) => Promise<{column: Locator; row: Locator}>;
	readonly selectSiteRolesSearchBar: Locator;
	readonly selectSiteRolesSearchBarButton: Locator;
	readonly selectSitesTable: Locator;
	readonly selectSitesTableRow: (
		colPosition: number,
		value: string,
		strictEqual?: boolean
	) => Promise<{column: Locator; row: Locator}>;
	readonly selectSitesTableRowButton: (siteName: string) => Promise<Locator>;
	readonly selectTagsButton: Locator;
	readonly selectUserLanguage: Locator;
	readonly tagCheckbox: (tagName: string) => Locator;
	readonly tagInput: (name: string) => Locator;
	readonly tagsFrame: FrameLocator;
	readonly uploadImageSelectImageButton: Locator;
	readonly uploadImageDoneButton: Locator;
	readonly userIDInput: Locator;
	readonly webDAVPasswordLabel: Locator;
	readonly yourPasswordInput: Locator;

	constructor(page: Page) {
		this.accountsLink = page.getByRole('link', {
			exact: true,
			name: 'Accounts',
		});
		this.appsLink = page.getByRole('link', {
			exact: true,
			name: 'Apps',
		});
		this.backLink = page
			.getByRole('link', {exact: true, name: 'Back'})
			.or(page.getByRole('link', {name: 'Return to Full Page'}));
		this.cancelButton = page.getByRole('button', {
			exact: true,
			name: 'Cancel',
		});
		this.changeImageButton = page.getByLabel('Change Image');
		this.clearImageButton = page.getByLabel('Clear Image');
		this.customField = async (fieldName: string) => {
			await page.getByText('Custom Fields').waitFor({timeout: 15 * 1000});

			const customField = await page.getByText(fieldName);

			if (customField.isVisible()) {
				return customField;
			}

			throw new Error(`Cannot locate Custom Field ${fieldName}`);
		};
		this.doneButton = page.getByRole('button', {name: 'Done'});
		this.emailAddressError = page
			.locator(
				'#_com_liferay_account_admin_web_internal_portlet_AccountEntriesAdminPortlet_emailAddressHelper'
			)
			.or(
				page.locator(
					'#_com_liferay_account_admin_web_internal_portlet_AccountEntriesManagementPortlet_emailAddressHelper'
				)
			)
			.or(
				page.locator(
					'#_com_liferay_users_admin_web_portlet_UsersAdminPortlet_emailAddressHelper'
				)
			);
		this.emailAddressInput = page.getByLabel('Email Address');
		this.emailAddressInvalidError = page.getByText(
			'Please enter a valid email address.'
		);
		this.firstNameInput = page.locator('input[id*="Portlet_firstName"]');
		this.generateWebDAVPasswordButton = page.getByTestId(
			'generateWebDAVPasswordButton'
		);
		this.informationLink = page.getByRole('link', {
			exact: true,
			name: 'Information',
		});
		this.lastNameInput = page.getByLabel('Last Name');
		this.maxFileSizeText = page
			.frameLocator('iframe[title="Upload Image"]')
			.getByText('Upload images no larger than 300 KB.');
		this.membershipsAccountsRemoveButton = (accountName) =>
			page.getByLabel(`Remove ${accountName}`);
		this.membershipsAccountsTableRow = async (
			colPosition: number,
			value: string,
			strictEqual: boolean
		) => {
			return await searchTableRowByValue(
				this.membershipsAccountsTable,
				colPosition,
				value,
				strictEqual
			);
		};
		this.membershipsAccountsTable = page.locator(
			'#_com_liferay_users_admin_web_portlet_UsersAdminPortlet_accountEntriesSearchContainer'
		);
		this.membershipsNoAccountsMessage = page.getByText(
			'This user does not belong to any accounts.'
		);
		this.membershipsUserGroupsTableRow = async (
			colPosition: number,
			value: string,
			strictEqual: boolean
		) => {
			return await searchTableRowByValue(
				this.membershipsUserGroupsTable,
				colPosition,
				value,
				strictEqual
			);
		};
		this.membershipsUserGroupsTable = page.locator(
			'#_com_liferay_users_admin_web_portlet_UsersAdminPortlet_userGroupsSearchContainer'
		);
		this.membershipsLink = page.getByRole('link', {
			exact: true,
			name: 'Memberships',
		});
		this.organizationsLink = page.getByRole('link', {
			exact: true,
			name: 'Organizations',
		});
		this.organizationsTable = page.locator(
			'#_com_liferay_users_admin_web_portlet_UsersAdminPortlet_organizationsSearchContainer'
		);
		this.page = page;
		this.passwordConfirmationFrame = page.frameLocator(
			'iframe[title="Confirm Password"]'
		);
		this.passwordLink = page.getByRole('link', {
			exact: true,
			name: 'Password',
		});
		this.profileAndDashboardLink = page.getByRole('link', {
			exact: true,
			name: 'Profile and Dashboard',
		});
		this.regularRoleCell = (name) => page.getByRole('cell', {name});
		this.regularRoleCellButton = (name) =>
			this.regularRoleCell(name).locator('..').getByRole('button');
		this.rolesLink = page.getByRole('link', {
			exact: true,
			name: 'Roles',
		});
		this.saveButton = page.getByRole('button', {name: 'Save'});
		this.screenNameError = page.locator(
			'#_com_liferay_users_admin_web_portlet_UsersAdminPortlet_screenNameHelper'
		);
		this.screenNameInput = page.getByLabel('Screen Name');
		this.selectAccountsButton = page.getByLabel('Select Accounts');
		this.selectOrganizationButton = page.locator(
			'#_com_liferay_users_admin_web_portlet_MyOrganizationsPortlet_selectOrganizationLink'
		);
		this.selectOrganizationRolesButton = page.locator(
			'#_com_liferay_users_admin_web_portlet_UsersAdminPortlet_selectOrganizationRoleLink'
		);
		this.selectOrganizationRolesFrame = page.frameLocator(
			'iframe[title="Select Organization Role"]'
		);
		this.selectOrganizationRolesFrameCell = (name) =>
			this.selectOrganizationRolesFrame.getByRole('cell', {name});
		this.selectOrganizationRolesTable =
			this.selectOrganizationRolesFrame.locator(
				'#_com_liferay_roles_admin_web_portlet_RolesAdminPortlet_organizationsSearchContainer'
			);
		this.selectOrganizationRolesTableRow = async (
			colPosition: number,
			value: string,
			strictEqual: boolean
		) => {
			return await searchTableRowByValue(
				this.selectOrganizationRolesTable,
				colPosition,
				value,
				strictEqual
			);
		};
		this.selectOrganizationRolesSearchBar =
			this.selectOrganizationRolesFrame.getByPlaceholder('Search for');
		this.selectOrganizationRolesSearchBarButton =
			this.selectOrganizationRolesFrame.getByRole('button', {
				name: 'Search for',
			});
		this.selectOrganizationsTable = page
			.frameLocator(
				'#_com_liferay_users_admin_web_portlet_MyOrganizationsPortlet_selectOrganization_iframe_'
			)
			.locator(
				'#_com_liferay_item_selector_web_portlet_ItemSelectorPortlet_entriesSearchContainer'
			);
		this.selectOrganizationsTableRow = async (
			colPosition: number,
			value: string,
			strictEqual: boolean
		) => {
			return await searchTableRowByValue(
				this.selectOrganizationsTable,
				colPosition,
				value,
				strictEqual
			);
		};
		this.selectRegularRolesButton = page.getByLabel('Select Regular Roles');
		this.selectRegularRolesChooseButton = (name) =>
			this.selectRegularRolesFrame.getByLabel(`Choose ${name}`);
		this.selectRegularRolesFrame = page.frameLocator(
			'iframe[title="Select Regular Role"]'
		);
		this.selectRegularRolesSearchInput =
			this.selectRegularRolesFrame.getByPlaceholder('Search for', {
				exact: true,
			});
		this.selectSiteRolesButton = page.locator(
			'#_com_liferay_users_admin_web_portlet_UsersAdminPortlet_selectSiteRoleLink'
		);
		this.selectSiteRolesFrame = page.frameLocator(
			'iframe[title="Select Site Role"]'
		);
		this.selectSiteRolesTable = this.selectSiteRolesFrame.locator(
			'#_com_liferay_roles_admin_web_portlet_RolesAdminPortlet_rolesSearchContainer'
		);
		this.selectSiteRolesTableRow = async (
			colPosition: number,
			value: string,
			strictEqual: boolean
		) => {
			return await searchTableRowByValue(
				this.selectSiteRolesTable,
				colPosition,
				value,
				strictEqual
			);
		};
		this.selectSiteRolesSearchBar =
			this.selectSiteRolesFrame.getByPlaceholder('Search for');
		this.selectSiteRolesSearchBarButton =
			this.selectSiteRolesFrame.getByRole('button', {
				name: 'Search for',
			});
		this.selectSitesTable = page
			.frameLocator(
				'#_com_liferay_users_admin_web_portlet_UsersAdminPortlet_selectSiteRole_iframe_'
			)
			.locator(
				'#_com_liferay_roles_admin_web_portlet_RolesAdminPortlet_groupsSearchContainer'
			);
		this.selectSitesTableRow = async (
			colPosition: number,
			value: string,
			strictEqual: boolean
		) => {
			return await searchTableRowByValue(
				this.selectSitesTable,
				colPosition,
				value,
				strictEqual
			);
		};
		this.selectSitesTableRowButton = async (siteName: string) => {
			const sitesTableRow = await this.selectSitesTableRow(
				0,
				siteName,
				true
			);

			if (sitesTableRow) {
				return sitesTableRow.row.getByRole('button', {
					name: 'Choose',
				});
			}

			throw new Error(`Cannot locate user row with siteName ${siteName}`);
		};
		this.selectTagsButton = page
			.getByLabel('Select Tags')
			.and(page.getByRole('button'));
		this.selectUserLanguage = page.getByLabel('Language');
		this.tagCheckbox = (tagName) => this.tagsFrame.getByLabel(tagName);
		this.tagInput = (name) => page.getByRole('row', {name});
		this.tagsFrame = page.frameLocator(`iframe[title="Tags"]`);
		this.uploadImageSelectImageButton = page
			.frameLocator('iframe[title="Upload Image"]')
			.getByLabel('Select Image');
		this.uploadImageDoneButton = page
			.frameLocator('iframe[title="Upload Image"]')
			.getByRole('button', {name: 'Done'});
		this.userIDInput = page.getByLabel('User ID');
		this.webDAVPasswordLabel = page.locator(
			'#_com_liferay_users_admin_web_portlet_UsersAdminPortlet_webDAVPassword'
		);

		this.confirmButton = this.passwordConfirmationFrame.getByRole(
			'button',
			{name: 'Confirm'}
		);
		this.yourPasswordInput =
			this.passwordConfirmationFrame.getByLabel('Your Password');
	}

	async selectTag(tagNames: Array<string>) {
		for (const tagName of tagNames) {
			await this.tagCheckbox(tagName).check();
		}

		await this.doneButton.click();
	}
}
