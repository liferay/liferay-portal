/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page, expect} from '@playwright/test';

import {waitForAlert} from '../../utils/waitForAlert';
import {DataTablePage} from '../account-admin-web/DataTablePage';
import {GlobalMenuPage} from '../product-navigation-applications-menu/GlobalMenuPage';

export const searchTableRowByValue = async function (
	tableLocator: Locator,
	colPosition: number,
	value: string,
	strictEqual: boolean = false
) {
	await tableLocator.elementHandle();

	const rows = await tableLocator.getByRole('row').all();

	for await (const row of rows) {
		const column = row.getByRole('cell').nth(colPosition).first();

		const colValue = (await column.allInnerTexts()).join('');

		if (
			(strictEqual && colValue === value) ||
			(!strictEqual &&
				colValue.toLowerCase().indexOf(value.toLowerCase()) >= 0)
		) {
			return {column, row};
		}
	}

	throw new Error(`Cannot locate table row with value ${value}`);
};

export class UserGroupsPage {
	readonly addUsersIFrame: FrameLocator;
	readonly addUsersIFrameAddButton: Locator;
	readonly addUsersTable: DataTablePage;
	readonly assignMembersMenuItem: Locator;
	readonly backButton: Locator;
	readonly creationMenuNewButton: Locator;
	readonly customField: (fieldName: string) => Promise<Locator>;
	readonly deleteButton: Locator;
	readonly deleteUserGroupWithUsersErrorMessage: Locator;
	readonly editUserGroupMenuItem: Locator;
	readonly exportButton: Locator;
	readonly exportImportFrame: FrameLocator;
	readonly exportImportMenuItem: Locator;
	readonly globalMenuPage: GlobalMenuPage;
	readonly goToDashboardPagesMenuItem: Locator;
	readonly goToProfilePagesMenuItem: Locator;
	readonly managePagesMenuItem: Locator;
	readonly nameInput: Locator;
	readonly newUserButton: Locator;
	readonly newUserGroupButton: Locator;
	readonly noUserGroupsMessage: Locator;
	readonly noUsersMessage: Locator;
	readonly optionsButton: Locator;
	readonly page: Page;
	readonly removeUserMenuItem: Locator;
	readonly saveButton: Locator;
	readonly userGroupPagesPermissionsIFrame: FrameLocator;
	readonly userGroupPagesPermissionsMenuItem: Locator;
	readonly userGroupPagesPermissionsTable: DataTablePage;
	readonly userGroupUsersTable: DataTablePage;
	readonly userGroupsTable: Locator;
	readonly userGroupsTableCell: (value: string, exact?: boolean) => Locator;
	readonly userGroupsTableCheckbox: (screenName: string) => Promise<Locator>;
	readonly userGroupsTableLink: (name: string, exact?: boolean) => Locator;
	readonly userGroupsTableRow: (
		colPosition: number,
		value: string,
		strictEqual?: boolean
	) => Promise<{column: Locator; row: Locator}>;
	readonly userGroupsTableRowActions: (
		screenName: string
	) => Promise<Locator>;
	readonly userGroupsTableRowLink: (
		userGroupName: string
	) => Promise<Locator>;

	constructor(page: Page) {
		this.addUsersIFrame = page.frameLocator(
			'iframe[title^="Add Users to"]'
		);
		this.addUsersIFrameAddButton = page.getByRole('button', {
			exact: true,
			name: 'Add',
		});
		this.addUsersTable = new DataTablePage(
			this.addUsersIFrame,
			this.addUsersIFrame.locator(
				'#_com_liferay_item_selector_web_portlet_ItemSelectorPortlet_entriesSearchContainer'
			)
		);
		this.assignMembersMenuItem = page.getByRole('menuitem', {
			name: 'Assign Members',
		});
		this.backButton = page.getByRole('link', {exact: true, name: 'Back'});
		this.creationMenuNewButton = page
			.getByTestId('creationMenuNewButton')
			.getByText('New');
		this.customField = async (fieldName: string) => {
			await page
				.getByText('Custom Fields', {exact: true})
				.waitFor({timeout: 15 * 1000});

			const customField = await page.getByText(fieldName);

			if (customField.isVisible()) {
				return customField;
			}

			throw new Error(`Cannot locate Custom Field ${fieldName}`);
		};
		this.deleteButton = page.getByRole('button', {name: 'Delete'});
		this.deleteUserGroupWithUsersErrorMessage = page.getByText(
			'You cannot delete user groups that have users.'
		);
		this.editUserGroupMenuItem = page.getByRole('menuitem', {
			name: 'Edit',
		});
		this.exportButton = page
			.frameLocator('iframe[title="Export / Import"]')
			.getByRole('button', {name: 'Export'});
		this.exportImportFrame = page.frameLocator(
			'iframe[title="Export / Import"]'
		);
		this.exportImportMenuItem = page.getByRole('menuitem', {
			name: 'Export / Import',
		});
		this.globalMenuPage = new GlobalMenuPage(page);
		this.goToDashboardPagesMenuItem = page.getByRole('menuitem', {
			name: 'Go to Dashboard Pages',
		});
		this.goToProfilePagesMenuItem = page.getByRole('menuitem', {
			name: 'Go to Profile Pages',
		});
		this.managePagesMenuItem = page.getByRole('menuitem', {
			name: 'Manage Pages',
		});
		this.optionsButton = page.locator(
			'[id*="UserGroupsAdminPortlet"] [title="Options"]'
		);
		this.nameInput = page
			.getByLabel('Name')
			.or(page.getByLabel('New Name'));
		this.newUserButton = page.getByRole('button', {name: 'Add Users'});
		this.newUserGroupButton = page
			.getByRole('button', {name: 'New'})
			.or(page.getByRole('link', {name: 'Add User Group'}));
		this.noUserGroupsMessage = page.getByText('No user groups were found.');
		this.noUsersMessage = page.getByText('No users were found.');
		this.page = page;
		this.removeUserMenuItem = page.getByRole('menuitem', {
			name: 'Remove',
		});
		this.saveButton = page.getByRole('button', {name: 'Save'});
		this.userGroupPagesPermissionsIFrame = page.frameLocator(
			'iframe[title="User Group Pages Permissions"]'
		);
		this.userGroupPagesPermissionsMenuItem = page.getByRole('link', {
			name: 'User Group Pages Permissions',
		});
		this.userGroupPagesPermissionsTable = new DataTablePage(
			this.userGroupPagesPermissionsIFrame,
			this.userGroupPagesPermissionsIFrame.locator(
				'#_com_liferay_portlet_configuration_web_portlet_PortletConfigurationPortlet_rolesSearchContainer'
			)
		);
		this.userGroupUsersTable = new DataTablePage(
			page,
			page.locator(
				'#_com_liferay_user_groups_admin_web_portlet_UserGroupsAdminPortlet_usersSearchContainer'
			)
		);
		this.userGroupsTable = page.locator(
			'#_com_liferay_user_groups_admin_web_portlet_UserGroupsAdminPortlet_userGroupsSearchContainer'
		);
		this.userGroupsTableCell = (value, exact = true) =>
			this.page
				.getByRole('cell', {
					exact,
					name: value,
				})
				.first();
		this.userGroupsTableCheckbox = async (screenName: string) => {
			const userGroupsTableRow = await this.userGroupsTableRow(
				1,
				screenName,
				true
			);

			if (userGroupsTableRow && userGroupsTableRow.column) {
				return userGroupsTableRow.row.getByRole('checkbox');
			}

			throw new Error(
				`Cannot locate user group row with screenName ${screenName}`
			);
		};
		this.userGroupsTableLink = (name, exact = true) =>
			this.page
				.getByRole('link', {
					exact,
					name,
				})
				.first();
		this.userGroupsTableRow = async (
			colPosition: number,
			value: string,
			strictEqual: boolean = false
		) => {
			return await searchTableRowByValue(
				this.userGroupsTable,
				colPosition,
				value,
				strictEqual
			);
		};

		this.userGroupsTableRowActions = async (screenName: string) => {
			const userGroupsTableRow = await this.userGroupsTableRow(
				1,
				screenName,
				true
			);

			if (userGroupsTableRow && userGroupsTableRow.column) {
				return userGroupsTableRow.row.getByRole('button');
			}

			throw new Error(
				`Cannot locate user group row with screenName ${screenName}`
			);
		};
		this.userGroupsTableRowLink = async (userGroupName: string) => {
			const userGroupTableRow = await this.userGroupsTableRow(
				1,
				userGroupName,
				true
			);

			if (userGroupTableRow && userGroupTableRow.column) {
				return userGroupTableRow.column.getByRole('link', {
					name: userGroupName,
				});
			}

			throw new Error(
				`Cannot locate user group row with screenName ${userGroupName}`
			);
		};
	}

	async goto(forceReload?: boolean) {
		if (forceReload) {
			await this.globalMenuPage.goToHome();
		}

		await this.globalMenuPage.goToControlPanel('User Groups');
	}

	async linkSiteTemplate(
		userGroupName: string,
		siteTemplateName: string,
		{propagationEnabled = false}: {propagationEnabled?: boolean} = {}
	) {
		await expect(async () => {
			await (await this.userGroupsTableRowActions(userGroupName)).click();

			await expect(this.editUserGroupMenuItem).toBeVisible();
		}).toPass({timeout: 5000});

		await this.editUserGroupMenuItem.click();

		await this.page
			.locator('select[name$="publicLayoutSetPrototypeId"]')
			.selectOption({label: siteTemplateName});

		await this.page
			.locator('input[name$="publicLayoutSetPrototypeLinkEnabled"]')
			.setChecked(propagationEnabled, {force: true});

		await this.saveButton.click();

		await waitForAlert(this.page);
	}

	async goToWithLimitedAccess() {
		await this.globalMenuPage.goToHome();
		await this.globalMenuPage.goToControlPanel('User Groups');
	}
}
