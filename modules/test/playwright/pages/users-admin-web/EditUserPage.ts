/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page, expect} from '@playwright/test';

import {waitForAlert} from '../../utils/waitForAlert';
import {DataTablePage} from '../account-admin-web/DataTablePage';
import {searchTableRowByValue} from './UsersAndOrganizationsPage';

export class EditUserPage {
	readonly accountsLink: Locator;
	readonly addAdditionalEmailAddressesButton: Locator;
	readonly addAddressButton: Locator;
	readonly addAddressCityInput: Locator;
	readonly addAddressCountrySelect: Locator;
	readonly addAddressPostalCodeInput: Locator;
	readonly addAddressRegionSelect: Locator;
	readonly addAddressStreet1Input: Locator;
	readonly additionalEmailAddressInput: Locator;
	readonly additionalEmailAddressesTable: Locator;
	readonly additionalEmailAddressesTablePrimaryText: (
		emailAddress: string
	) => Promise<Locator>;
	readonly additionalEmailAddressesTableRow: (
		colPosition: number,
		value: string,
		strictEqual?: boolean
	) => Promise<{column: Locator; row: Locator}>;
	readonly additionalEmailAddressesTableRowActions: (
		emailAddress: string
	) => Promise<Locator>;
	readonly addPhoneNumbersButton: Locator;
	readonly addPhoneNumbersInput: Locator;
	readonly addressActions: (street: string) => Locator;
	readonly addressesLink: Locator;
	readonly addressPrimaryText: (streetName: string) => Locator;
	readonly alertsAndAnnouncementsDeliveryTable: DataTablePage;
	readonly appsLink: Locator;
	readonly backLink: Locator;
	readonly birthdayInput: Locator;
	readonly cancelButton: Locator;
	readonly categoryGridCell: (categoryName: string) => Locator;
	readonly categoryInput: (vocabularyName: string) => Locator;
	readonly categoryOption: (categoryName: string) => Locator;
	readonly changeImageButton: Locator;
	readonly clearImageButton: Locator;
	readonly confirmButton: Locator;
	readonly contactInformationLink: Locator;
	readonly contactLink: Locator;
	readonly customField: (fieldName: string) => Promise<Locator>;
	readonly displaySettingsLink: Locator;
	readonly doneButton: Locator;
	readonly editMenuItem: Locator;
	readonly emailAddressError: Locator;
	readonly emailAddressInput: Locator;
	readonly emailAddressInvalidError: Locator;
	readonly firstNameInput: Locator;
	readonly generateWebDAVPasswordButton: Locator;
	readonly greetingInput: Locator;
	readonly jabberInput: Locator;
	readonly informationLink: Locator;
	readonly lastNameInput: Locator;
	readonly makePrimaryCheckbox: Locator;
	readonly makePrimaryMenuItem: Locator;
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
	readonly membershipsNoUserGroupsMessage: Locator;
	readonly membershipsSiteTableRow: (
		colPosition: number,
		value: string,
		strictEqual?: boolean
	) => Promise<{column: Locator; row: Locator}>;
	readonly membershipsSiteTable: Locator;
	readonly membershipsUserGroupsTableRow: (
		colPosition: number,
		value: string,
		strictEqual?: boolean
	) => Promise<{column: Locator; row: Locator}>;
	readonly membershipsUserGroupsTable: Locator;
	readonly middleNameInput: Locator;
	readonly myOrganizationsSelectOrganizationButton: Locator;
	readonly myOrganizationsSelectOrganizationsTable: Locator;
	readonly myOrganizationsSelectOrganizationsTableRow: (
		colPosition: number,
		value: string,
		strictEqual?: boolean
	) => Promise<{column: Locator; row: Locator}>;
	readonly organizationRolesTable: DataTablePage;
	readonly organizationsLink: Locator;
	readonly organizationsTable: Locator;
	readonly organizationsTableRemoveButton: (
		organizationName: string
	) => Locator;
	readonly page: Page;
	readonly passwordConfirmationFrame: FrameLocator;
	readonly passwordInput: Locator;
	readonly passwordReenterInput: Locator;
	readonly passwordLink: Locator;
	readonly phoneNumbersTable: Locator;
	readonly phoneNumbersTablePrimaryText: (
		phoneNumber: string
	) => Promise<Locator>;
	readonly phoneNumbersTableRow: (
		colPosition: number,
		value: string,
		strictEqual?: boolean
	) => Promise<{column: Locator; row: Locator}>;
	readonly phoneNumbersTableRowActions: (
		phoneNumber: string
	) => Promise<Locator>;
	readonly preferencesLink: Locator;
	readonly prefixInput: Locator;
	readonly profileAndDashboardLink: Locator;
	readonly regularRoleCell: (name: string) => Locator;
	readonly regularRoleCellButton: (name: string) => Locator;
	readonly regularRolesTable: DataTablePage;
	readonly removeMenuItem: Locator;
	readonly rolesLink: Locator;
	readonly saveButton: Locator;
	readonly screenNameError: Locator;
	readonly screenNameInput: Locator;
	readonly selectAccountsButton: Locator;
	readonly selectOrganizationRolesButton: Locator;
	readonly selectOrganizationRolesChooseButton: (
		name: string
	) => Promise<Locator>;
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
	readonly selectOrganizationsAddButton: Locator;
	readonly selectOrganizationsButton: Locator;
	readonly selectOrganizationsFrame: FrameLocator;
	readonly selectOrganizationsTable: DataTablePage;

	readonly selectRegularRolesButton: Locator;
	readonly selectRegularRolesChooseButton: (name: string) => Locator;
	readonly selectRegularRolesFrame: FrameLocator;
	readonly selectRegularRolesFrameCloseButton: Locator;
	readonly selectRegularRolesSearchInput: Locator;
	readonly selectRegularRolesTable: DataTablePage;
	readonly selectSiteButton: Locator;
	readonly selectSiteFrame: FrameLocator;
	readonly selectSiteFrameSiteLink: (name: string) => Locator;
	readonly selectSiteSearchBar: Locator;
	readonly selectSiteSearchBarButton: Locator;
	readonly selectSiteRolesButton: Locator;
	readonly selectSiteRolesChooseButton: (name: string) => Promise<Locator>;
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
	readonly selectUserGroupIFrame: FrameLocator;
	readonly selectUserGroupTable: DataTablePage;
	readonly selectUserGroupsButton: Locator;
	readonly selectUserLanguage: Locator;
	readonly siteRolesTable: DataTablePage;
	readonly skypeInput: Locator;
	readonly suffixInput: Locator;
	readonly tagCheckbox: (tagName: string) => Locator;
	readonly tagInput: (name: string) => Locator;
	readonly tagsFrame: FrameLocator;
	readonly timeZoneInput: Locator;
	readonly uploadImageSelectImageButton: Locator;
	readonly uploadImageDoneButton: Locator;
	readonly userIDInput: Locator;
	readonly webDAVPasswordLabel: Locator;
	readonly websitesTableRow: (
		colPosition: number,
		value: string,
		strictEqual?: boolean
	) => Promise<{column: Locator; row: Locator}>;
	readonly websitesTable: Locator;
	readonly yourPasswordInput: Locator;

	constructor(page: Page) {
		this.accountsLink = page.getByRole('link', {
			exact: true,
			name: 'Accounts',
		});
		this.addAdditionalEmailAddressesButton = page.getByLabel(
			'Add Additional Email Addresses'
		);
		this.addAddressButton = page.getByLabel('Add Addresses');
		this.addAddressCityInput = page.getByLabel('City Required');
		this.addAddressCountrySelect = page.getByLabel('Country Required');
		this.addAddressPostalCodeInput = page.getByLabel(
			'Postal Code Required'
		);
		this.addAddressRegionSelect = page.getByLabel('Region Required');
		this.addAddressStreet1Input = page.getByLabel('Street 1 Required');
		this.additionalEmailAddressInput = page.getByLabel('Address Required');
		this.additionalEmailAddressesTable = page.locator(
			'#_com_liferay_users_admin_web_portlet_UsersAdminPortlet_emailAddressesSearchContainer'
		);
		this.additionalEmailAddressesTablePrimaryText = async (
			emailAddress: string
		) => {
			const additionalEmailAddressTableRow =
				await this.additionalEmailAddressesTableRow(
					0,
					emailAddress,
					true
				);

			if (
				additionalEmailAddressTableRow &&
				additionalEmailAddressTableRow.column
			) {
				return additionalEmailAddressTableRow.row.getByText('Primary');
			}
		};
		this.additionalEmailAddressesTableRow = async (
			colPosition: number,
			value: string,
			strictEqual: boolean
		) => {
			return await searchTableRowByValue(
				this.additionalEmailAddressesTable,
				colPosition,
				value,
				strictEqual
			);
		};
		this.additionalEmailAddressesTableRowActions = async (
			emailAddress: string
		) => {
			const additionalEmailAddressTableRow =
				await this.additionalEmailAddressesTableRow(
					0,
					emailAddress,
					true
				);

			if (
				additionalEmailAddressTableRow &&
				additionalEmailAddressTableRow.column
			) {
				return additionalEmailAddressTableRow.row.getByLabel(
					'Edit Email Address'
				);
			}
		};
		this.addPhoneNumbersButton = page.getByLabel('Add Phone Numbers');
		this.addPhoneNumbersInput = page.getByLabel('Number Required');
		this.addressActions = (street: string) => {
			return page
				.locator('li')
				.filter({hasText: `${street}`})
				.getByLabel('Edit Address');
		};
		this.addressesLink = page.getByRole('link', {
			exact: true,
			name: 'Addresses',
		});
		this.addressPrimaryText = (streetName: string) => {
			return page
				.locator('li')
				.filter({hasText: streetName})
				.filter({hasText: 'Primary'});
		};
		this.alertsAndAnnouncementsDeliveryTable = new DataTablePage(
			page,
			page.locator(
				'#_com_liferay_users_admin_web_portlet_UsersAdminPortlet_announcementsDeliveriesSearchContainer'
			)
		);
		this.appsLink = page.getByRole('link', {
			exact: true,
			name: 'Apps',
		});
		this.birthdayInput = page.locator(
			'#_com_liferay_users_admin_web_portlet_UsersAdminPortlet_birthday'
		);
		this.backLink = page
			.getByRole('link', {exact: true, name: 'Back'})
			.or(page.getByRole('link', {name: 'Return to Full Page'}));
		this.cancelButton = page.getByRole('button', {
			exact: true,
			name: 'Cancel',
		});
		this.categoryGridCell = (categoryName: string) =>
			page.getByRole('gridcell', {exact: true, name: categoryName});
		this.categoryInput = (vocabularyName: string) =>
			page.getByLabel(vocabularyName, {exact: true});
		this.categoryOption = (categoryName: string) =>
			page.getByRole('option', {name: categoryName});
		this.changeImageButton = page.getByLabel('Change Image');
		this.clearImageButton = page.getByLabel('Clear Image');
		this.contactLink = page.getByRole('link', {
			exact: true,
			name: 'Contact',
		});
		this.contactInformationLink = page.getByRole('link', {
			exact: true,
			name: 'Contact Information',
		});
		this.customField = async (fieldName: string) => {
			await page.getByText('Custom Fields').waitFor({timeout: 15 * 1000});

			const customField = await page.getByText(fieldName);

			if (customField.isVisible()) {
				return customField;
			}

			throw new Error(`Cannot locate Custom Field ${fieldName}`);
		};
		this.displaySettingsLink = page.getByRole('link', {
			exact: true,
			name: 'Display Settings',
		});
		this.doneButton = page.getByRole('button', {name: 'Done'});
		this.editMenuItem = page.getByRole('menuitem', {
			name: 'Edit',
		});
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
		this.greetingInput = page.getByLabel('Greeting');
		this.informationLink = page.getByRole('link', {
			exact: true,
			name: 'Information',
		});
		this.jabberInput = page.getByLabel('Jabber');
		this.lastNameInput = page.getByLabel('Last Name');
		this.makePrimaryCheckbox = page.getByLabel('Make Primary');
		this.makePrimaryMenuItem = page.getByRole('menuitem', {
			name: 'Make Primary',
		});
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
		this.membershipsNoUserGroupsMessage = page.getByText(
			'This user does not belong to a user group.'
		);
		this.membershipsSiteTableRow = async (
			colPosition: number,
			value: string,
			strictEqual: boolean
		) => {
			return await searchTableRowByValue(
				this.membershipsSiteTable,
				colPosition,
				value,
				strictEqual
			);
		};
		this.membershipsSiteTable = page.locator(
			'#_com_liferay_users_admin_web_portlet_UsersAdminPortlet_groupsSearchContainer'
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
		this.middleNameInput = page.getByLabel('Middle Name');
		this.myOrganizationsSelectOrganizationButton = page.locator(
			'#_com_liferay_users_admin_web_portlet_MyOrganizationsPortlet_selectOrganizationLink'
		);
		this.myOrganizationsSelectOrganizationsTable = page
			.frameLocator(
				'#_com_liferay_users_admin_web_portlet_MyOrganizationsPortlet_selectOrganization_iframe_'
			)
			.locator(
				'#_com_liferay_item_selector_web_portlet_ItemSelectorPortlet_entriesSearchContainer'
			);
		this.myOrganizationsSelectOrganizationsTableRow = async (
			colPosition: number,
			value: string,
			strictEqual: boolean
		) => {
			return await searchTableRowByValue(
				this.myOrganizationsSelectOrganizationsTable,
				colPosition,
				value,
				strictEqual
			);
		};
		this.organizationRolesTable = new DataTablePage(
			page,
			page.locator(
				'#_com_liferay_users_admin_web_portlet_UsersAdminPortlet_organizationRolesSearchContainer'
			)
		);
		this.organizationsLink = page.getByRole('link', {
			exact: true,
			name: 'Organizations',
		});
		this.organizationsTable = page.locator(
			'#_com_liferay_users_admin_web_portlet_UsersAdminPortlet_organizationsSearchContainer'
		);
		this.organizationsTableRemoveButton = (organizationName) =>
			page.getByLabel(`Remove ${organizationName}`);
		this.page = page;
		this.passwordConfirmationFrame = page.frameLocator(
			'iframe[title="Confirm Password"]'
		);
		this.passwordInput = page.locator('input[id*="Portlet_password1"]');
		this.passwordReenterInput = page.locator(
			'input[id*="Portlet_password2"]'
		);
		this.passwordLink = page.getByRole('link', {
			exact: true,
			name: 'Password',
		});
		this.phoneNumbersTable = page.locator(
			'#_com_liferay_users_admin_web_portlet_UsersAdminPortlet_phonesSearchContainer'
		);
		this.phoneNumbersTablePrimaryText = async (phoneNumber: string) => {
			const tableRow = await this.phoneNumbersTableRow(
				0,
				phoneNumber,
				true
			);

			if (tableRow && tableRow.column) {
				return tableRow.row.getByText('Primary');
			}
		};
		this.phoneNumbersTableRow = async (
			colPosition: number,
			value: string,
			strictEqual: boolean
		) => {
			return await searchTableRowByValue(
				this.phoneNumbersTable,
				colPosition,
				value,
				strictEqual
			);
		};
		this.phoneNumbersTableRowActions = async (phoneNumber: string) => {
			const tableRow = await this.phoneNumbersTableRow(
				0,
				phoneNumber,
				true
			);

			if (tableRow && tableRow.column) {
				return tableRow.row.getByLabel('Edit Phone Number');
			}
		};
		this.preferencesLink = page.getByRole('link', {
			exact: true,
			name: 'Preferences',
		});
		this.prefixInput = page.getByLabel('Prefix');
		this.profileAndDashboardLink = page.getByRole('link', {
			exact: true,
			name: 'Profile and Dashboard',
		});
		this.regularRoleCell = (name) => page.getByRole('cell', {name});
		this.regularRoleCellButton = (name) =>
			this.regularRoleCell(name).locator('..').getByRole('button');
		this.regularRolesTable = new DataTablePage(
			page,
			page.locator(
				'#_com_liferay_users_admin_web_portlet_UsersAdminPortlet_rolesSearchContainer'
			)
		);
		this.removeMenuItem = page.getByRole('menuitem', {
			name: 'Remove',
		});
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
		this.selectOrganizationRolesButton = page.locator(
			'#_com_liferay_users_admin_web_portlet_UsersAdminPortlet_selectOrganizationRoleLink'
		);
		this.selectOrganizationRolesChooseButton = async (name: string) => {
			const selectOrganizationRolesTableRow =
				await this.selectOrganizationRolesTableRow(0, name);

			if (
				selectOrganizationRolesTableRow &&
				selectOrganizationRolesTableRow.row
			) {
				return selectOrganizationRolesTableRow.row.getByRole('button', {
					name: 'Choose',
				});
			}
		};
		this.selectOrganizationRolesFrame = page.frameLocator(
			'iframe[title="Select Organization Role"]'
		);
		this.selectOrganizationRolesFrameCell = (name) =>
			this.selectOrganizationRolesFrame.getByRole('cell', {name});
		this.selectOrganizationRolesTable = this.selectOrganizationRolesFrame
			.locator(
				'#_com_liferay_roles_admin_web_portlet_RolesAdminPortlet_organizationsSearchContainer'
			)
			.or(
				this.selectOrganizationRolesFrame.locator(
					'#_com_liferay_roles_admin_web_portlet_RolesAdminPortlet_rolesSearchContainer'
				)
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
		this.selectOrganizationsAddButton = page.getByRole('button', {
			name: 'Add',
		});
		this.selectOrganizationsButton = page.getByLabel(
			'Select Organizations'
		);
		this.selectOrganizationsFrame = page.frameLocator(
			'iframe[title="Select Organization"]'
		);

		this.selectOrganizationsTable = new DataTablePage(
			this.selectOrganizationsFrame,
			this.selectOrganizationsFrame.locator(
				'#_com_liferay_item_selector_web_portlet_ItemSelectorPortlet_entriesSearchContainer'
			)
		);
		this.selectRegularRolesButton = page.getByLabel('Select Regular Roles');
		this.selectRegularRolesChooseButton = (name) =>
			this.selectRegularRolesFrame.getByLabel(`Choose ${name}`);
		this.selectRegularRolesFrame = page.frameLocator(
			'iframe[title="Select Regular Role"]'
		);
		this.selectRegularRolesFrameCloseButton = page.getByLabel('Close');
		this.selectRegularRolesSearchInput =
			this.selectRegularRolesFrame.getByPlaceholder('Search for', {
				exact: true,
			});
		this.selectRegularRolesTable = new DataTablePage(
			this.selectRegularRolesFrame,
			this.selectRegularRolesFrame.locator(
				'#_com_liferay_roles_admin_web_portlet_RolesAdminPortlet_rolesSearchContainer'
			)
		);
		this.selectSiteButton = page.locator(
			'#_com_liferay_users_admin_web_portlet_UsersAdminPortlet_selectSiteLink'
		);
		this.selectSiteFrame = page.frameLocator('iframe[title="Select Site"]');
		this.selectSiteFrameSiteLink = (name) =>
			this.selectSiteFrame.getByRole('link', {
				name,
			});
		this.selectSiteSearchBar =
			this.selectSiteFrame.getByPlaceholder('Search for');
		this.selectSiteSearchBarButton = this.selectSiteFrame.getByRole(
			'button',
			{
				name: 'Search for',
			}
		);
		this.selectSiteRolesButton = page.locator(
			'#_com_liferay_users_admin_web_portlet_UsersAdminPortlet_selectSiteRoleLink'
		);
		this.selectSiteRolesChooseButton = async (name: string) => {
			const selectSiterolesTableRow = await this.selectSiteRolesTableRow(
				0,
				name
			);

			if (selectSiterolesTableRow && selectSiterolesTableRow.row) {
				return selectSiterolesTableRow.row.getByRole('button', {
					name: 'Choose',
				});
			}
		};
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
		this.selectUserGroupIFrame = page.frameLocator(
			'iframe[title="Select User Group"]'
		);
		this.selectUserGroupTable = new DataTablePage(
			this.selectUserGroupIFrame,
			this.selectUserGroupIFrame.locator(
				'#_com_liferay_item_selector_web_portlet_ItemSelectorPortlet_entriesSearchContainer'
			)
		);
		this.selectUserGroupsButton = page.getByLabel('Select User Groups');
		this.selectUserLanguage = page.getByLabel('Language');
		this.siteRolesTable = new DataTablePage(
			page,
			page.locator(
				'#_com_liferay_users_admin_web_portlet_UsersAdminPortlet_siteRolesSearchContainer'
			)
		);
		this.skypeInput = page.getByLabel('Skype');
		this.suffixInput = page.getByLabel('Suffix');
		this.tagCheckbox = (tagName) => this.tagsFrame.getByLabel(tagName);
		this.tagInput = (name) => page.getByRole('row', {name});
		this.tagsFrame = page.frameLocator(`iframe[title="Tags"]`);
		this.timeZoneInput = page.getByLabel('Time Zone');
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
		this.websitesTableRow = async (
			colPosition: number,
			value: string,
			strictEqual: boolean
		) => {
			return await searchTableRowByValue(
				this.websitesTable,
				colPosition,
				value,
				strictEqual
			);
		};
		this.websitesTable = page.locator(
			'#_com_liferay_users_admin_web_portlet_UsersAdminPortlet_websitesSearchContainer'
		);

		this.confirmButton = this.passwordConfirmationFrame.getByRole(
			'button',
			{name: 'Confirm'}
		);
		this.yourPasswordInput =
			this.passwordConfirmationFrame.getByLabel('Your Password');
	}

	async addNewAddress(makePrimary: boolean, streetName: string) {
		await expect(async () => {
			await this.addAddressButton.click();
			if (makePrimary) {
				await this.makePrimaryCheckbox.check();
			}
			await this.addAddressStreet1Input.fill(streetName);
			await this.addAddressCityInput.fill('Miami');
			await this.addAddressCountrySelect.selectOption({
				label: 'United States',
			});
			await this.addAddressRegionSelect.selectOption({label: 'Florida'});
			await this.addAddressPostalCodeInput.fill('33101');
			await this.saveButton.click();
		}).toPass();
	}
	async addNewPhoneNumber(makePrimary: boolean, phoneNumber: string) {
		await expect(async () => {
			await this.addPhoneNumbersButton.click();
			if (makePrimary) {
				await this.makePrimaryCheckbox.check();
			}
			await this.addPhoneNumbersInput.fill(phoneNumber);
			await this.saveButton.click();
		}).toPass();
		await waitForAlert(this.page);
	}

	async selectTag(tagNames: Array<string>) {
		for (const tagName of tagNames) {
			await this.tagCheckbox(tagName).check();
		}

		await this.doneButton.click();
	}
}
