/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page} from '@playwright/test';

import {ApplicationsMenuPage} from '../product-navigation-applications-menu/ApplicationsMenuPage';

export class UsersAdminItemSelectorPage {
	readonly applicationsMenuPage: ApplicationsMenuPage;
	readonly assignUsersUserGroupsFrame: (
		userGroupName: string
	) => FrameLocator;
	readonly assignUsersUserGroupsFrameSearchBar: (
		userGroupName: string
	) => Locator;
	readonly assignUsersUserGroupsFrameSearchButton: (
		userGroupName: string
	) => Locator;
	readonly assignUsersUserGroupsFrameTableRow: (
		name: string,
		userGroupName: string
	) => Locator;
	readonly clientCredentialUserNameTextbox: Locator;
	readonly creationMenuNewButton: Locator;
	readonly page: Page;
	readonly selectUserButton: Locator;
	readonly usersFrame: FrameLocator;
	readonly usersFrameSearchButton: Locator;
	readonly usersFrameTableRow: (name: string) => Locator;

	constructor(page: Page) {
		this.applicationsMenuPage = new ApplicationsMenuPage(page);
		this.assignUsersUserGroupsFrame = (userGroupName) =>
			page.frameLocator(`iframe[title="Add Users to ${userGroupName}"]`);
		this.assignUsersUserGroupsFrameSearchBar = (userGroupName) =>
			this.assignUsersUserGroupsFrame(userGroupName).getByPlaceholder(
				'Search for'
			);
		this.assignUsersUserGroupsFrameSearchButton = (userGroupName) =>
			this.assignUsersUserGroupsFrame(userGroupName).getByRole('button', {
				name: 'search',
			});
		this.assignUsersUserGroupsFrameTableRow = (name, userGroupName) => {
			return this.assignUsersUserGroupsFrame(userGroupName).getByRole(
				'cell',
				{exact: true, name}
			);
		};
		this.clientCredentialUserNameTextbox = page.getByRole('textbox', {
			name: 'client-credential-user-name',
		});
		this.creationMenuNewButton = page
			.getByTestId('creationMenuNewButton')
			.and(page.locator('a:visible'));
		this.page = page;
		this.selectUserButton = page.getByTestId('selectUserButton');
		this.usersFrame = page.frameLocator('iframe[title="Users"]');
		this.usersFrameSearchButton = this.usersFrame.getByRole('button', {
			name: 'search',
		});
		this.usersFrameTableRow = (name: string) => {
			return this.usersFrame.getByRole('cell', {name});
		};
	}

	async goToOauth2Administration() {
		await this.applicationsMenuPage.goToOauth2Administration();
	}
}
