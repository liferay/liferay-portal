/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {PORTLET_URLS} from '../../../../utils/portletUrls';
import {waitForAlert} from '../../../../utils/waitForAlert';

type UserOrUserGroupType = 'groups' | 'users';

export class SpaceSummaryPage {
	readonly page: Page;

	readonly closeButton: Locator;
	readonly userGroupsTab: Locator;
	readonly usersTab: Locator;
	readonly viewAllContentLink: Locator;
	readonly viewAllFilesLink: Locator;
	readonly viewAllMembersLink: Locator;
	readonly viewAllSitesLink: Locator;

	constructor(page: Page) {
		this.page = page;

		this.closeButton = this.page
			.locator('.modal-header')
			.getByLabel('Close', {exact: true});

		this.userGroupsTab = page.getByRole('tab', {name: 'User Groups'});

		this.usersTab = page.getByRole('tab', {name: 'Users'});

		this.viewAllContentLink = this.page.getByRole('link', {
			name: 'View All Content',
		});

		this.viewAllFilesLink = this.page.getByRole('link', {
			name: 'View All Files',
		});

		this.viewAllMembersLink = this.page.getByRole('button', {
			name: 'View All Members',
		});

		this.viewAllSitesLink = this.page.getByRole('button', {
			name: 'View All Sites',
		});
	}

	async goto(spaceName: string) {
		await this.page.goto(PORTLET_URLS.cms);
		await this.page.getByRole('menuitem', {name: spaceName}).click();
		await this.viewAllContentLink.waitFor();
	}

	async addUserOrUserGroup(name: string, type: UserOrUserGroupType) {
		await this.viewAllMembersLink.click();

		this.page.getByRole('dialog').waitFor();
		await this.page
			.getByLabel('Add People to Collaborate', {exact: true})
			.selectOption(type);
		await this.page
			.getByPlaceholder('Enter name or email.', {exact: true})
			.click();
		await this.page.getByRole('option', {name}).click();

		await waitForAlert(
			this.page,
			type.includes('group')
				? `Success:Group ${name} successfully added to space.`
				: `Success:User ${name} successfully added to space.`
		);

		await this.closeButton.click();
	}

	async removeUserOrUserGroup(name: string, type: UserOrUserGroupType) {
		await this.viewAllMembersLink.click();

		this.page.getByRole('dialog').waitFor();
		await this.page
			.getByLabel('Add People to Collaborate', {exact: true})
			.selectOption(type);

		await this.page
			.locator('li')
			.filter({hasText: name})
			.getByLabel('Remove Group')
			.click();

		await waitForAlert(
			this.page,
			type.includes('group')
				? `Success:Group ${name} successfully removed from space.`
				: `Success:User ${name} successfully removed from space.`
		);

		await this.closeButton.click();
	}

	async connectSite(siteName: string) {
		await this.viewAllSitesLink.click();

		this.page.getByRole('dialog').waitFor();
		await this.page.getByLabel('Site', {exact: true}).click();
		await this.page.getByRole('option', {name: siteName}).click();
		await this.page
			.getByRole('button', {exact: true, name: 'Connect'})
			.click();

		this.page.getByLabel('Connected Sites').getByText(siteName).waitFor();

		await this.closeButton.click();
	}
}
