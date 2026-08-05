/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page, expect} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';
import {PORTLET_URLS} from '../../../../utils/portletUrls';

export class BlogsPage {
	readonly blogName: (title: string) => Locator;
	readonly deleteAllBlogEntriesButton: Locator;
	readonly moreActionsButton: (title: string) => Locator;
	readonly page: Page;
	readonly permissionsFrameLocator: FrameLocator;
	readonly searchInput: Locator;
	readonly selectAllBlogEntriesCheckBox: Locator;
	readonly successMessage: Locator;

	constructor(page: Page) {
		this.blogName = (title: string) => page.getByText(title);
		this.deleteAllBlogEntriesButton = page.getByRole('button', {
			name: 'Delete',
		});
		this.moreActionsButton = (title: string) =>
			page
				.locator('.card')
				.filter({hasText: title})
				.getByLabel('More actions');
		this.page = page;
		this.permissionsFrameLocator = page.frameLocator(
			'iframe[title="Permissions"]'
		);
		this.searchInput = page.getByPlaceholder('Search');
		this.selectAllBlogEntriesCheckBox = page.getByLabel(
			'Select All Items on the Page'
		);
		this.successMessage = page.getByText('Successful');
	}

	async goto(siteUrl?: Site['friendlyUrlPath']) {
		await this.page.goto(
			`/group${siteUrl || '/guest'}${PORTLET_URLS.blogs}`
		);
	}

	async goToCreateBlogEntry() {
		await this.page.getByRole('link', {name: 'Add Blog Entry'}).click();
	}

	async goToBlogEntryAction(action: string, title: string) {
		await this.moreActionsButton(title).waitFor();

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {
				exact: true,
				name: action,
			}),
			trigger: this.moreActionsButton(title),
		});
	}

	async assertBlogEntryActionAbsent(action: string, title: string) {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {
				exact: true,
				name: 'Delete',
			}),
			trigger: this.moreActionsButton(title),
		});

		await expect(
			this.page.getByRole('menuitem', {exact: true, name: action})
		).toBeHidden();
	}

	async assertBlogEntryActionIcons(
		actionIcons: {action: string; icon: string}[],
		title: string
	) {
		await this.moreActionsButton(title).waitFor();

		await clickAndExpectToBeVisible({
			target: this.page.getByRole('menuitem', {
				exact: true,
				name: actionIcons[0].action,
			}),
			trigger: this.moreActionsButton(title),
		});

		for (const actionIcon of actionIcons) {
			await expect(
				this.page
					.getByRole('menuitem', {
						exact: true,
						name: actionIcon.action,
					})
					.locator(`svg.lexicon-icon-${actionIcon.icon}`)
			).toBeVisible();
		}
	}

	async assertBlogEntryPermissions(
		permissions: {enabled: boolean; locator: string}[],
		title: string
	) {
		await this.goToBlogEntryAction('Permissions', title);

		await this.assertPermissions(permissions);
	}

	async assertPermissions(
		permissions: {enabled: boolean; locator: string}[]
	) {
		await this.permissionsFrameLocator
			.locator(permissions[0].locator)
			.waitFor();

		for (const permission of permissions) {
			const permissionCheckbox = this.permissionsFrameLocator.locator(
				permission.locator
			);

			if (permission.enabled) {
				await expect(permissionCheckbox).toBeChecked();
			}
			else {
				await expect(permissionCheckbox).not.toBeChecked();
			}
		}

		await this.permissionsFrameLocator
			.getByRole('button', {name: 'Cancel'})
			.click();
	}

	async searchEntry(term: string) {
		await this.searchInput.fill(term);
		await this.searchInput.press('Enter');
	}

	async moveEntryToRecycleBin(title: string) {
		const card = this.page.locator('.card').filter({hasText: title});

		await this.goToBlogEntryAction('Delete', title);

		const okButton = this.page
			.getByRole('dialog')
			.getByRole('button', {name: 'OK'});

		try {
			await okButton.click({timeout: 2000});
		}
		catch {

			// This will happen when the recycle bin is disabled.  Let's
			// ignore it so that tests pass in local installations where
			// the recycle bin has been disabled.

		}

		await expect(card).toHaveCount(0);
	}

	async assertEntryPresent(title: string, expected: boolean = true) {
		const card = this.page.locator('.card').filter({hasText: title});

		if (expected) {
			await expect(card.first()).toBeVisible();
		}
		else {
			await expect(card).toHaveCount(0);
		}
	}

	async deleteAllBlogEntries() {
		await this.selectAllBlogEntriesCheckBox.check();
		await this.deleteAllBlogEntriesButton.click();
	}
}
