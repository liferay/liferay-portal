/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page, expect} from '@playwright/test';

import {DataApiHelpers} from '../../helpers/ApiHelpers';
import {clickAndExpectToBeVisible} from '../../utils/clickAndExpectToBeVisible';
import {getRandomInt} from '../../utils/getRandomInt';
import {waitForAlert} from '../../utils/waitForAlert';
import {DataTablePage} from '../account-admin-web/DataTablePage';
import {ApplicationsMenuPage} from '../product-navigation-applications-menu/ApplicationsMenuPage';

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

export class UsersAndOrganizationsPage {
	readonly activateButton: Locator;
	readonly activateUserMenuItem: Locator;
	readonly addOrganizationButton: Locator;
	readonly addUserButton: Locator;
	readonly applicationsMenuPage: ApplicationsMenuPage;
	readonly assignOrganizationRolesIFrame: FrameLocator;
	readonly assignOrganizationRolesMenuItem: Locator;
	readonly assignOrganizationRolesSearchBarButton: Locator;
	readonly assignOrganizationRolesTable: Locator;
	readonly assignOrganizationRolesTableRow: (
		colPosition: number,
		value: string,
		strictEqual?: boolean
	) => Promise<{column: Locator; row: Locator}>;
	readonly assignOrganizationRolesTableRowLink: (
		roleName: string
	) => Promise<Locator>;
	readonly assignOrganizationRolesTableStatus: (
		roleName: string,
		status: string
	) => Promise<Locator>;
	readonly assignOrganizationRolesUserCell: (
		userName: string
	) => Promise<Locator>;
	readonly assignOrganizationRolesUserTable: Locator;
	readonly assignOrganizationRolesUserTableCell: (
		userName: string
	) => Promise<Locator>;
	readonly assignOrganizationRolesUserTableRow: (
		colPosition: number,
		value: string,
		strictEqual?: boolean
	) => Promise<{column: Locator; row: Locator}>;
	readonly assignUsersIFrame: FrameLocator;
	readonly assignUsersMenuItem: Locator;
	readonly assignUsersTable: Locator;
	readonly assignUsersTableRow: (
		colPosition: number,
		value: string,
		strictEqual?: boolean
	) => Promise<{column: Locator; row: Locator}>;
	readonly assignUsersCheckbox: (userName: string) => Promise<Locator>;
	readonly assignUsersDoneButton: Locator;
	readonly clearButton: Locator;
	readonly commentsInput: Locator;
	readonly deactivateButton: Locator;
	readonly deactivateUserMenuItem: Locator;
	readonly deleteButton: Locator;
	readonly deleteOrganizationMenuItem: Locator;
	readonly deletePersonalDataMenuItem: Locator;
	readonly editOrganizationMenuItem: Locator;
	readonly emailAddressInput: Locator;
	readonly exportImportOptionsMenuItem: Locator;
	readonly exportPersonalDataItem: Locator;
	readonly exportUsersOptionsMenuItem: Locator;
	readonly firstNameInput: Locator;
	readonly impersonateUserMenuItem: Locator;
	readonly lastNameInput: Locator;
	readonly manageCustomFieldsOptionsMenuItem: Locator;
	readonly myOrganizationsBreadcrumbLink: (
		organizationName: string
	) => Locator;
	readonly myOrganizationsMenuItem: Locator;
	readonly myOrganizationsTable: Locator;
	readonly myOrganizationsTableRow: (
		colPosition: number,
		value: string,
		strictEqual?: boolean
	) => Promise<{column: Locator; row: Locator}>;
	readonly myOrganizationsTableRowLink: (
		organizationName: string
	) => Promise<Locator>;
	readonly myOrganizationsUserAndOrgsTable: Locator;
	readonly myOrganizationsUserAndOrgsTableRow: (
		colPosition: number,
		value: string,
		strictEqual?: boolean
	) => Promise<{column: Locator; row: Locator}>;
	readonly myOrganizationsUserAndOrgsTableRowLink: (
		organizationName: string
	) => Promise<Locator>;
	readonly noPermissionMessage: Locator;
	readonly noResultsMessage: Locator;
	readonly noUsersMessage: Locator;
	readonly organizationActionsMenu: (
		organizationName: string
	) => Promise<Locator>;
	readonly optionsMenu: Locator;
	readonly organizationChartLink: Locator;
	readonly organizationsLink: Locator;
	readonly organizationsTable: DataTablePage;
	readonly organizationsTableEmptyMessage: Locator;
	readonly organizationUsersTable: Locator;
	readonly organizationUsersTableRow: (
		colPosition: number,
		value: string,
		strictEqual?: boolean
	) => Promise<{column: Locator; row: Locator}>;
	readonly organizationUsersTableRowActions: (
		screenName: string
	) => Promise<Locator>;
	readonly organizationUsersTableRowLink: (
		screenName: string
	) => Promise<Locator>;
	readonly organizationUsersTableRowStatusLink: (
		screenName: string,
		status: string
	) => Promise<Locator>;
	readonly page: Page;
	readonly pageTitle: Locator;
	readonly saveUserButton: Locator;
	readonly screenNameInput: Locator;
	readonly selectAllUsersCheckBox: Locator;
	readonly statusText: (value: string) => Locator;
	readonly tableFilterMenu: Locator;
	readonly tableFilterMenuItem: (option: string) => Locator;
	readonly tableOrderMenu: Locator;
	readonly tableOrderMenuItem: (option: string) => Locator;
	readonly userIdInput: Locator;
	readonly usersCheckbox: (userName: string) => Promise<Locator>;
	readonly usersDataTable: DataTablePage;
	readonly usersSearchBar: Locator;
	readonly usersSearchBarButton: Locator;
	readonly usersTableRow: (
		colPosition: number,
		value: string,
		strictEqual?: boolean
	) => Promise<{column: Locator; row: Locator}>;
	readonly usersTableRowLink: (screenName: string) => Promise<Locator>;
	readonly usersTableRowActions: (screenName: string) => Promise<Locator>;
	readonly usersLink: Locator;
	readonly userPersonalMenuButton: Locator;
	readonly usersTable: Locator;
	readonly usersTableCell: (userName: string) => Locator;
	readonly userPreferencesButton: Locator;
	readonly displaySettingsButton: Locator;
	readonly timeZoneSelect: Locator;
	readonly saveTimeZoneButton: Locator;
	readonly usersAndOrganizationsButton: Locator;

	constructor(page: Page) {
		this.activateButton = page.getByRole('button', {name: 'Activate'});
		this.activateUserMenuItem = page.getByRole('menuitem', {
			name: 'Activate',
		});
		this.addOrganizationButton = page.getByRole('link', {
			name: 'Add Organization',
		});
		this.addUserButton = page.getByRole('link', {name: 'Add User'});
		this.applicationsMenuPage = new ApplicationsMenuPage(page);
		this.assignOrganizationRolesIFrame = page.frameLocator(
			'iframe[title="Assign Organization Roles"]'
		);
		this.assignOrganizationRolesMenuItem = page.getByRole('menuitem', {
			name: 'Assign Organization Roles',
		});
		this.assignOrganizationRolesSearchBarButton =
			this.assignOrganizationRolesIFrame.getByRole('button', {
				name: 'Search for',
			});
		this.assignOrganizationRolesTable =
			this.assignOrganizationRolesIFrame.locator(
				'#_com_liferay_roles_selector_web_portlet_RolesSelectorPortlet_rolesSearchContainer'
			);
		this.assignOrganizationRolesTableRow = async (
			colPosition: number,
			value: string,
			strictEqual: boolean = false
		) => {
			return await searchTableRowByValue(
				this.assignOrganizationRolesTable,
				colPosition,
				value,
				strictEqual
			);
		};
		this.assignOrganizationRolesTableRowLink = async (roleName: string) => {
			const assignOrganizationRolesTableRow =
				await this.assignOrganizationRolesTableRow(0, roleName, true);

			if (
				assignOrganizationRolesTableRow &&
				assignOrganizationRolesTableRow.column
			) {
				return assignOrganizationRolesTableRow.column.getByRole(
					'link',
					{
						name: roleName,
					}
				);
			}

			throw new Error(`Cannot locate role row with name ${roleName}`);
		};
		this.assignOrganizationRolesTableStatus = async (
			roleName: string,
			status: string
		) => {
			const assignOrganizationRolesTableRow =
				await this.assignOrganizationRolesTableRow(0, roleName);

			if (
				assignOrganizationRolesTableRow &&
				assignOrganizationRolesTableRow.row
			) {
				return assignOrganizationRolesTableRow.row.getByText(status);
			}
		};
		this.assignOrganizationRolesUserCell = async (userName: string) => {
			return page.getByRole('cell', {
				exact: true,
				name: userName,
			});
		};
		this.assignOrganizationRolesUserTable =
			this.assignOrganizationRolesIFrame.locator(
				'#_com_liferay_roles_selector_web_portlet_RolesSelectorPortlet_usersSearchContainer'
			);
		this.assignOrganizationRolesUserTableCell = async (
			userName: string
		) => {
			return this.assignOrganizationRolesUserTable.getByRole('cell', {
				exact: true,
				name: userName,
			});
		};
		this.assignOrganizationRolesUserTableRow = async (
			colPosition: number,
			value: string,
			strictEqual: boolean = false
		) => {
			return await searchTableRowByValue(
				this.assignOrganizationRolesUserTable,
				colPosition,
				value,
				strictEqual
			);
		};
		this.assignUsersIFrame = page.frameLocator('iframe[id="modalIframe"]');
		this.assignUsersMenuItem = page.getByRole('menuitem', {
			name: 'Assign Users',
		});
		this.assignUsersTable = this.assignUsersIFrame.locator(
			'#_com_liferay_item_selector_web_portlet_ItemSelectorPortlet_entriesSearchContainer'
		);
		this.assignUsersTableRow = async (
			colPosition: number,
			value: string,
			strictEqual: boolean = false
		) => {
			return await searchTableRowByValue(
				this.assignUsersTable,
				colPosition,
				value,
				strictEqual
			);
		};
		this.assignUsersMenuItem = page.getByRole('menuitem', {
			name: 'Assign Users',
		});
		this.clearButton = page.getByRole('button', {name: 'Clear'});
		this.commentsInput = page.getByLabel('Characters Maximum:');
		this.deactivateButton = page.getByRole('button', {name: 'Deactivate'});
		this.deactivateUserMenuItem = page.getByRole('menuitem', {
			name: 'Deactivate',
		});
		this.deleteButton = page.getByRole('button', {name: 'Delete'});
		this.deleteOrganizationMenuItem = page.getByRole('menuitem', {
			name: 'Delete',
		});
		this.deletePersonalDataMenuItem = page.getByRole('menuitem', {
			name: 'Delete Personal Data',
		});
		this.editOrganizationMenuItem = page.getByRole('menuitem', {
			name: 'Edit',
		});
		this.emailAddressInput = page.getByLabel('Email Address');
		this.exportImportOptionsMenuItem = page.getByRole('menuitem', {
			name: 'Export / Import',
		});
		this.exportUsersOptionsMenuItem = page.getByRole('menuitem', {
			name: 'Export Users',
		});
		this.firstNameInput = page.getByLabel('First Name');
		this.exportPersonalDataItem = page.getByRole('menuitem', {
			name: 'Export Personal Data',
		});
		this.impersonateUserMenuItem = page.getByRole('menuitem', {
			name: 'Impersonate User',
		});
		this.lastNameInput = page.getByLabel('Last Name');
		this.manageCustomFieldsOptionsMenuItem = page.getByRole('menuitem', {
			name: 'Manage Custom Fields',
		});
		this.myOrganizationsBreadcrumbLink = (organizationName: string) => {
			return page.getByRole('link', {
				name: organizationName,
			});
		};
		this.myOrganizationsMenuItem = page.getByRole('menuitem', {
			exact: true,
			name: 'My Organizations',
		});
		this.myOrganizationsTable = page.locator(
			'#_com_liferay_users_admin_web_portlet_MyOrganizationsPortlet_organizationsSearchContainer'
		);
		this.myOrganizationsTableRow = async (
			colPosition: number,
			value: string,
			strictEqual: boolean = false
		) => {
			return await searchTableRowByValue(
				this.myOrganizationsTable,
				colPosition,
				value,
				strictEqual
			);
		};
		this.myOrganizationsTableRowLink = async (organizationName: string) => {
			const myOrganizationsTableRow = await this.myOrganizationsTableRow(
				1,
				organizationName,
				true
			);

			if (myOrganizationsTableRow && myOrganizationsTableRow.column) {
				return myOrganizationsTableRow.column.getByRole('link', {
					name: organizationName,
				});
			}

			throw new Error(
				`Cannot locate organization row with name ${organizationName}`
			);
		};
		this.myOrganizationsUserAndOrgsTable = page.locator(
			'#_com_liferay_users_admin_web_portlet_MyOrganizationsPortlet_organizationUsersSearchContainer'
		);
		this.myOrganizationsUserAndOrgsTableRow = async (
			colPosition: number,
			value: string,
			strictEqual: boolean = false
		) => {
			return await searchTableRowByValue(
				this.myOrganizationsUserAndOrgsTable,
				colPosition,
				value,
				strictEqual
			);
		};
		this.myOrganizationsUserAndOrgsTableRowLink = async (
			organizationName: string
		) => {
			const row = await this.myOrganizationsUserAndOrgsTableRow(
				1,
				organizationName,
				true
			);

			if (row && row.column) {
				return row.column.getByRole('link', {
					name: organizationName,
				});
			}

			throw new Error(
				`Cannot locate organization row with name ${organizationName}`
			);
		};
		this.noPermissionMessage = page.getByText(
			'You do not belong to an organization and are not allowed to view other organizations.'
		);
		this.noResultsMessage = page.getByText('No results were found.', {
			exact: true,
		});
		this.noUsersMessage = page.getByText('No users were found');
		this.optionsMenu = page
			.getByTestId('headerOptions')
			.getByLabel('Options');
		this.organizationChartLink = page.getByRole('link', {
			exact: true,
			name: 'Organization Chart',
		});
		this.organizationsLink = page.getByRole('link', {
			name: 'Organizations',
		});
		this.organizationsTable = new DataTablePage(
			page,
			page
				.locator(
					'#_com_liferay_users_admin_web_portlet_UsersAdminPortlet_organizationsSearchContainer'
				)
				.first()
		);
		this.organizationsTableEmptyMessage = page.getByText(
			'No organizations were found.'
		);
		this.organizationUsersTable = page.locator(
			'[id$="_organizationUsersSearchContainer"]'
		);
		this.organizationUsersTableRow = async (
			colPosition: number,
			value: string,
			strictEqual: boolean = false
		) => {
			return await searchTableRowByValue(
				this.organizationUsersTable,
				colPosition,
				value,
				strictEqual
			);
		};
		this.organizationUsersTableRowActions = async (name: string) => {
			const organizationUsersTableRow =
				await this.organizationUsersTableRow(1, name, true);

			if (organizationUsersTableRow && organizationUsersTableRow.column) {
				return organizationUsersTableRow.row.getByLabel('Show Actions');
			}

			throw new Error(`Cannot locate user row with screenName ${name}`);
		};
		this.organizationUsersTableRowLink = async (screenName: string) => {
			const organizationUsersTableRow =
				await this.organizationUsersTableRow(1, screenName, true);

			if (organizationUsersTableRow && organizationUsersTableRow.column) {
				return organizationUsersTableRow.column.getByRole('link', {
					name: screenName,
				});
			}

			throw new Error(
				`Cannot locate user row with screenName ${screenName}`
			);
		};
		this.organizationUsersTableRowStatusLink = async (
			name: string,
			status: string
		) => {
			const organizationUsersTableRow =
				await this.organizationUsersTableRow(1, name, true);

			if (organizationUsersTableRow && organizationUsersTableRow.row) {
				return organizationUsersTableRow.row.getByRole('link', {
					name: `${status}`,
				});
			}

			throw new Error(`Cannot locate user row with screenName ${name}`);
		};
		this.assignUsersCheckbox = async (userName: string) => {
			const assignUsersTableRow = await this.assignUsersTableRow(
				1,
				userName
			);

			if (assignUsersTableRow && assignUsersTableRow.row) {
				return assignUsersTableRow.row.getByRole('checkbox');
			}
		};
		this.assignUsersDoneButton = page.getByRole('button', {name: 'Done'});
		this.page = page;
		this.pageTitle = page.getByTestId('headerTitle');
		this.saveUserButton = page.getByRole('button', {name: 'Save'});
		this.screenNameInput = page.getByLabel('Screen Name');
		this.usersCheckbox = async (userName: string) => {
			const usersTableRow = await this.usersTableRow(1, userName);

			if (usersTableRow && usersTableRow.row) {
				return usersTableRow.row.getByRole('checkbox');
			}
		};
		this.usersDataTable = new DataTablePage(
			page,
			page.locator(
				'#_com_liferay_users_admin_web_portlet_UsersAdminPortlet_usersSearchContainer'
			)
		);
		this.usersSearchBar = page.getByPlaceholder('Search for');
		this.usersSearchBarButton = page.getByRole('button', {
			name: 'Search for',
		});
		this.usersTableRow = async (
			colPosition: number,
			value: string,
			strictEqual: boolean = false
		) => {
			return await searchTableRowByValue(
				this.usersTable,
				colPosition,
				value,
				strictEqual
			);
		};
		this.statusText = (value) => page.getByText(value, {exact: true});
		this.selectAllUsersCheckBox = page
			.locator('.management-bar')
			.getByLabel('Select All Users on the Page');
		this.tableFilterMenu = page
			.locator('.management-bar')
			.getByLabel('Filter');
		this.tableFilterMenuItem = (option: string) => {
			if (option === 'all') {
				return page
					.locator('.dropdown-menu')
					.getByRole('menuitem', {name: option})
					.first();
			}

			return page.locator('.dropdown-menu').getByRole('menuitem', {
				name: option,
			});
		};
		this.tableOrderMenu = page
			.locator('.management-bar')
			.getByLabel('Order');
		this.tableOrderMenuItem = (option: string) => {
			return page.getByRole('menuitem', {
				name: option,
			});
		};
		this.userIdInput = page.getByLabel('User ID');
		this.usersTableRowLink = async (screenName: string) => {
			const usersTableRow = await this.usersTableRow(2, screenName, true);

			if (usersTableRow && usersTableRow.column) {
				return usersTableRow.column.getByRole('link', {
					name: screenName,
				});
			}

			throw new Error(
				`Cannot locate user row with screenName ${screenName}`
			);
		};
		this.usersTableRowActions = async (screenName: string) => {
			const usersTableRow = await this.usersTableRow(2, screenName, true);

			if (usersTableRow && usersTableRow.column) {
				return usersTableRow.row.getByLabel('Show Actions');
			}

			throw new Error(
				`Cannot locate user row with screenName ${screenName}`
			);
		};
		this.usersLink = page.getByRole('link', {name: 'Users'});
		this.userPersonalMenuButton = page.getByTestId('userPersonalMenu');
		this.usersTable = page.locator(
			'#_com_liferay_users_admin_web_portlet_UsersAdminPortlet_usersSearchContainer'
		);
		this.usersTableCell = (userName: string) => {
			return this.page.getByRole('cell', {
				exact: true,
				name: userName,
			});
		};
		this.userPreferencesButton = page.getByRole('link', {
			name: 'Preferences',
		});
		this.displaySettingsButton = page.getByRole('link', {
			name: 'Display Settings',
		});
		this.timeZoneSelect = page.getByLabel('Time Zone');
		this.saveTimeZoneButton = page.getByRole('button', {name: 'Save'});
	}

	async activateUsers(userNames: string[]) {
		for (const user of userNames) {
			await (await this.usersCheckbox(user)).check();
		}
		await this.activateButton.click();
		await waitForAlert(this.page);
	}

	async createUser(
		apiHelpers: DataApiHelpers,
		userName = `user${getRandomInt()}`,
		comment?: string
	) {
		await this.goto();

		await expect(async () => {
			await this.addUserButton.click();
			await this.screenNameInput.fill(userName);
			await this.emailAddressInput.fill(`${userName}@liferay.com`);
			await this.firstNameInput.fill(userName);
			await this.lastNameInput.fill(userName);

			if (comment) {
				await this.commentsInput.fill(comment);
			}

			await this.saveUserButton.click();

			await waitForAlert(this.page, 'The user was created successfully.');
		}).toPass();

		apiHelpers.data.push({
			id: await this.userIdInput.inputValue(),
			type: 'userAccount',
		});
	}

	async deActivateUsers(userNames: string[]) {
		for (const user of userNames) {
			await (await this.usersCheckbox(user)).check();
		}

		await this.deactivateButton.click();

		await waitForAlert(this.page);
	}

	async deleteUsers(userNames: string[]) {
		for (const userName of userNames) {
			await (await this.usersCheckbox(userName)).check();
		}

		await this.deleteButton.click();

		await waitForAlert(this.page);
	}

	async filterUsers(option: string) {
		await Promise.all([
			clickAndExpectToBeVisible({
				autoClick: true,
				target: this.tableFilterMenuItem(option),
				trigger: this.tableFilterMenu,
			}),
			await expect(this.page.getByText('Search Results')).toBeVisible(),
		]);
	}

	async goto(forceReload?: boolean) {
		await this.applicationsMenuPage.goToUsersAndOrganizations(forceReload);
	}

	async goToOrganizations(forceReload?: boolean) {
		await this.goto(forceReload);
		await Promise.all([
			this.organizationsLink.click(),
			this.page.waitForResponse(
				(resp) =>
					resp.status() === 200 &&
					resp
						.url()
						.includes('screenNavigationCategoryKey=organizations')
			),
		]);
	}

	async goToOrganizationsWithLimitedAccess() {
		await this.applicationsMenuPage.goToUsersAndOrganizationsWithLimitedAccess();
		await Promise.all([
			this.organizationsLink.click(),
			this.page.waitForResponse(
				(resp) =>
					resp.status() === 200 &&
					resp
						.url()
						.includes('screenNavigationCategoryKey=organizations')
			),
		]);
	}

	async goToOrganizationChart(forceReload?: boolean) {
		await this.goto(forceReload);
		await Promise.all([
			this.organizationChartLink.click(),
			this.page.waitForResponse(
				(resp) =>
					resp.status() === 200 &&
					resp
						.url()
						.includes(
							'screenNavigationCategoryKey=commerce-organization'
						)
			),
		]);
	}

	async goToMyOrganizations() {
		await Promise.all([
			this.userPersonalMenuButton.click(),
			this.myOrganizationsMenuItem.click(),
			this.page.waitForResponse(
				(resp) =>
					resp.status() === 200 &&
					resp
						.url()
						.includes(
							'id=com_liferay_users_admin_web_portlet_MyOrganizationsPortlet'
						)
			),
		]);
	}

	async goToUsers(forceReload?: boolean) {
		await this.goto(forceReload);
		await Promise.all([
			this.usersLink.click(),
			this.page.waitForResponse(
				(resp) =>
					resp.status() === 200 &&
					resp.url().includes('screenNavigationCategoryKey=users')
			),
		]);
	}

	async goToUsersWithLimitedAccess() {
		await this.applicationsMenuPage.goToUsersAndOrganizationsWithLimitedAccess();
		await Promise.all([
			this.usersLink.click(),
			this.page.waitForResponse(
				(resp) =>
					resp.status() === 200 &&
					resp.url().includes('screenNavigationCategoryKey=users')
			),
		]);
	}

	async openOptionsMenu() {
		await this.optionsMenu
			.and(this.page.locator('[aria-haspopup]'))
			.click();
	}

	async goToUser(userName: string) {
		await this.page
			.getByRole('link', {exact: true, name: userName})
			.click();
	}
}
