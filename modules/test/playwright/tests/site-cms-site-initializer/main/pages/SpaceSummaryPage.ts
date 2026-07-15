/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';
import {PORTLET_URLS} from '../../../../utils/portletUrls';
import {waitForAlert} from '../../../../utils/waitForAlert';

type UserOrUserGroupType = 'groups' | 'users';

export class SpaceSummaryPage {
	readonly page: Page;

	readonly addContentButton: Locator;
	readonly addFileButton: Locator;
	readonly addMembersButton: Locator;
	readonly closeButton: Locator;
	readonly galleryPreview: Locator;
	readonly userGroupsTab: Locator;
	readonly usersTab: Locator;
	readonly viewAllContentLink: Locator;
	readonly viewAllFilesLink: Locator;
	readonly viewAllMembersLink: Locator;
	readonly viewAllSitesLink: Locator;

	constructor(page: Page) {
		this.page = page;

		this.addContentButton = page.getByRole('button', {name: `Add Content`});

		this.addFileButton = page.getByRole('button', {name: `Add Files`});

		this.addMembersButton = page.getByRole('button', {
			exact: true,
			name: 'Add Members',
		});

		this.closeButton = this.page
			.locator('.modal-header')
			.getByLabel('Close', {exact: true});

		this.galleryPreview = page.locator('.fds-gallery-view__preview');

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
		await expect(async () => {
			await this.page.goto(PORTLET_URLS.cms);

			await this.page
				.getByRole('menuitem', {name: spaceName})
				.click({timeout: 3000});

			await this.page
				.getByRole('heading', {exact: true, name: spaceName})
				.waitFor({timeout: 3000});
		}).toPass();
	}

	async openMembersDialog() {
		await clickAndExpectToBeVisible({
			target: this.page.getByRole('dialog'),
			trigger: this.viewAllMembersLink,
		});
	}

	async addRoleToSpaceMember(roleName: string, userName: string) {
		await this.openMembersDialog();

		const userRow = this.page
			.getByRole('listitem')
			.filter({hasText: userName});

		await userRow.waitFor();

		const triggerText = userRow.locator('.permission-select-trigger-text');

		await expect(triggerText).toHaveText(/\S+/);

		await userRow
			.locator('button:has(.permission-select-trigger-text)')
			.click();

		await this.page
			.getByRole('checkbox', {
				exact: true,
				name: roleName,
			})
			.check();

		await expect(triggerText).toContainText(roleName);

		await waitForAlert(this.page, 'role was successfully updated.', {
			autoClose: false,
		});

		await this.closeButton.click();

		await this.page.getByRole('dialog').waitFor({state: 'detached'});
	}

	async addUserOrUserGroup(name: string, type: UserOrUserGroupType) {
		await this.openMembersDialog();

		const dialog = this.page.getByRole('dialog');

		await dialog
			.getByLabel('Add People to Collaborate', {exact: true})
			.selectOption(type);

		const input = dialog.getByPlaceholder('Enter name or email.', {
			exact: true,
		});

		await input.waitFor({state: 'visible'});
		await input.click();
		await input.fill(name);

		await this.page.getByRole('option', {name}).click();

		await waitForAlert(
			this.page,
			type.includes('group')
				? `Success:Group ${name} successfully added to space.`
				: `Success:User ${name} successfully added to space.`,
			{autoClose: false}
		);

		await this.closeButton.click();

		await this.page.getByRole('dialog').waitFor({state: 'detached'});
	}

	async removeUserOrUserGroup(name: string, type: UserOrUserGroupType) {
		await this.openMembersDialog();

		await this.page
			.getByLabel('Add People to Collaborate', {exact: true})
			.selectOption(type);

		await this.page
			.locator('li')
			.filter({hasText: name})
			.getByLabel(type.includes('group') ? 'Remove Group' : 'Remove User')
			.click();

		await waitForAlert(
			this.page,
			type.includes('group')
				? `Success:Group ${name} successfully removed from space.`
				: `Success:User ${name} successfully removed from space.`,
			{autoClose: false}
		);

		await this.closeButton.click();
	}

	async createContentFolder(name: string) {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {name: 'Folder'}),
			trigger: this.addContentButton,
		});

		const dialog = this.page.getByRole('dialog', {name: 'New Folder'});

		await dialog.waitFor();

		await dialog.getByLabel('Name').fill(name);

		await dialog.getByRole('button', {name: 'Save'}).click();

		await this.page.getByRole('link', {name}).waitFor();
	}

	async createFileFolder(name: string) {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {name: 'Folder'}),
			trigger: this.addFileButton,
		});

		const dialog = this.page.getByRole('dialog', {name: 'New Folder'});

		await dialog.waitFor();

		await dialog.getByLabel('Name').fill(name);

		await dialog.getByRole('button', {name: 'Save'}).click();

		await this.page.getByRole('link', {name}).waitFor();
	}

	async connectSite(siteName: string) {
		await this.openConnectSitesDialog();

		await this.page.getByLabel('Sites', {exact: true}).click();

		await this.page
			.getByRole('option', {exact: true, name: 'Sites'})
			.click();

		await this.page
			.getByPlaceholder('Select a Site', {exact: true})
			.click();
		await this.page.getByRole('option', {name: siteName}).click();
		await this.page
			.getByRole('button', {exact: true, name: 'Connect'})
			.click();

		await this.page
			.getByLabel('Connected Sites')
			.getByText(siteName)
			.waitFor();

		await this.closeButton.click();
	}

	async connectSiteTemplate(siteTemplateName: string) {
		await this.openConnectSitesDialog();

		await this.page.getByLabel('Sites', {exact: true}).click();

		await this.page
			.getByRole('option', {exact: true, name: 'Site Templates'})
			.click();

		await this.page
			.getByPlaceholder('Select a Site Template', {exact: true})
			.click();
		await this.page.getByRole('option', {name: siteTemplateName}).click();
		await this.page
			.getByRole('button', {exact: true, name: 'Connect'})
			.click();

		await this.page
			.getByLabel('Connected Sites')
			.getByText(`${siteTemplateName} (Site Template)`, {exact: true})
			.waitFor();

		await waitForAlert(
			this.page,
			`Success:Site template ${siteTemplateName} was successfully connected to the space.`,
			{autoClose: false}
		);

		await this.closeButton.click();

		await this.page.getByRole('dialog').waitFor({state: 'detached'});
	}

	async disconnectSiteFromModal({
		isSiteTemplate = false,
		siteName,
	}: {
		isSiteTemplate?: boolean;
		siteName: string;
	}) {
		const label = isSiteTemplate ? `${siteName} (Site Template)` : siteName;

		await this.page
			.getByLabel('Connected Sites')
			.getByRole('listitem')
			.filter({
				has: this.page.getByText(label, {
					exact: true,
				}),
			})
			.getByRole('button', {name: /Actions/i})
			.click();

		await this.page.getByRole('menuitem', {name: 'Disconnect'}).click();

		await waitForAlert(
			this.page,
			`Success:${
				isSiteTemplate ? `Site template` : `Site`
			} ${siteName} was successfully disconnected from the space.`,
			{autoClose: false}
		);
	}

	async openConnectedSitesModal() {
		await this.viewAllSitesLink.click();

		await this.page.getByRole('dialog').waitFor();
	}

	private async openConnectSitesDialog() {
		await this.page
			.getByRole('button', {name: 'Connect Sites'})
			.or(this.page.getByRole('button', {name: 'View All Sites'}))
			.click();

		await this.page.getByRole('dialog').waitFor();
	}
}
