/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {ApiHelpers} from '../../helpers/ApiHelpers';
import getRandomString from '../../utils/getRandomString';
import {userData} from '../../utils/performLogin';
import {PORTLET_URLS} from '../../utils/portletUrls';
import {waitForAlert} from '../../utils/waitForAlert';
import {InstanceSettingsPage} from '../configuration-admin-web/InstanceSettingsPage';

export class ChangeTrackingPage {
	readonly frontendDataSetEntries: Locator;
	readonly instanceSettingsPage: InstanceSettingsPage;
	readonly page: Page;
	readonly reviewChangesButton: Locator;
	readonly tabsContainer: Locator;

	constructor(page: Page) {
		this.frontendDataSetEntries = page.locator(
			'[data-testid="visualization-mode-table"]'
		);
		this.instanceSettingsPage = new InstanceSettingsPage(page);
		this.page = page;
		this.reviewChangesButton = page.getByRole('menuitem', {
			name: 'Review Changes',
		});
		this.tabsContainer = page.locator('nav.navbar');
	}

	async addComment(comment?: string) {
		await this.openComments();

		const commentTextBox = this.page.getByRole('textbox', {
			name: 'Comment',
		});

		if (!comment) {
			comment = getRandomString();
		}

		await commentTextBox.fill(comment);

		await this.page.getByRole('button', {name: 'Reply'}).waitFor();

		await this.page.getByRole('button', {name: 'Reply'}).click();

		await expect(this.page.getByText('1 Comment')).toBeVisible();
	}

	async addUserToPublication(ctCollectionName, role, user) {
		await this.goToReviewChanges(ctCollectionName);

		await this.page.getByLabel('View Collaborators').click();

		await this.page.getByLabel('can view').click();

		await this.page.getByRole('menuitem', {name: role}).click();

		await this.page
			.getByPlaceholder('Enter name or email address.')
			.fill(user.emailAddress);

		await this.page.getByRole('option', {name: user.name}).click();

		await this.page.getByLabel('Submit').click();

		await waitForAlert(
			this.page,
			'Success:Users were invited successfully.'
		);
	}

	async addUserWithPublicationsUserRole() {
		const apiHelpers = new ApiHelpers(this.page);

		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[user.alternateName] = {
			name: user.givenName,
			password: 'test',
			surname: user.familyName,
		};

		const role =
			await apiHelpers.headlessAdminUser.getRoleByName(
				'Publications User'
			);

		await apiHelpers.headlessAdminUser.assignUserToRole(
			role.externalReferenceCode,
			user.id
		);

		const site =
			await apiHelpers.headlessAdminUser.getSiteByFriendlyUrlPath(
				'guest'
			);

		const siteAdminRole =
			await apiHelpers.headlessAdminUser.getRoleByName(
				'Site Administrator'
			);

		await apiHelpers.headlessAdminUser.assignUserToSite(
			siteAdminRole.id,
			site.id,
			user.id
		);

		return user;
	}

	async assertPublicationCommentsCRUDPermissions() {
		const comment = getRandomString();

		await this.addComment(comment);

		const editedComment = getRandomString();

		await this.editComment(comment, editedComment);

		await this.deleteComment(editedComment);
	}

	async deleteComment(comment) {
		const commentsDiv = this.page.locator('div.publications-comments');

		if (!commentsDiv) {
			await this.openComments();
		}

		await this.page
			.locator('div.comment-row')
			.filter({hasText: comment})
			.locator('button.dropdown-toggle')
			.click();

		await this.page.getByRole('menuitem', {name: 'Delete'}).click();

		await this.page.getByRole('button', {name: 'Delete'}).click();

		await expect(this.page.getByText(comment)).toBeHidden();
	}

	async editComment(comment, content) {
		const commentsDiv = this.page.locator('div.publications-comments');

		if (!commentsDiv) {
			await this.openComments();
		}

		await this.page
			.locator('div.comment-row')
			.filter({hasText: comment})
			.locator('button.dropdown-toggle')
			.click();

		await this.page.getByRole('menuitem', {name: 'Edit'}).click();

		await this.page.getByText(comment).fill(content);

		await this.page.getByRole('button', {name: 'Save'}).click();

		await expect(this.page.getByText(content)).toBeVisible();
	}

	async goto() {
		await this.page.goto(`/group/guest${PORTLET_URLS.publications}`);
	}

	async goToPublicationHistory() {
		await this.goto();

		await this.selectTab('History');
	}

	async goToReviewChanges(title: string) {
		await this.goto();

		await this.page
			.locator('#fnsd___table-id div')
			.filter({hasText: title})
			.first()
			.waitFor();

		await this.page.getByRole('link', {exact: true, name: title}).click();

		await this.page
			.locator(
				'#_com_liferay_change_tracking_web_portlet_PublicationsPortlet_controlMenu'
			)
			.filter({hasText: 'Review Changes'})
			.waitFor();
	}

	async goToReviewChangesHistory(title: string) {
		await this.goto();

		await this.page
			.locator('li[data-nav-item-index="2"] a span')
			.filter({hasText: 'History'})
			.first()
			.click();

		await this.page
			.locator('#fnsd___table-id div')
			.filter({hasText: title})
			.first()
			.waitFor();

		await this.page.getByRole('link', {exact: true, name: title}).click();

		await this.page
			.locator(
				'#_com_liferay_change_tracking_web_portlet_PublicationsPortlet_controlMenu'
			)
			.filter({hasText: 'Review Changes'})
			.waitFor();
	}

	async workOnProduction() {
		const apiHelpers = new ApiHelpers(this.page);

		await apiHelpers.headlessChangeTracking.checkoutCTCollection('0');

		await this.page.reload();
	}

	async workOnPublication(ctCollection) {
		const apiHelpers = new ApiHelpers(this.page);

		await apiHelpers.headlessChangeTracking.checkoutCTCollection(
			ctCollection.id
		);

		await this.page.reload();
	}

	async reviewChange(title: string) {
		await this.page.getByRole('link', {name: title}).first().click();

		await this.page.locator('h2').filter({hasText: title}).waitFor();
	}

	async selectTab(tabLabel: string) {
		const tabLink = this.tabsContainer.locator('a', {
			hasText: tabLabel,
		});

		await tabLink.click();

		await tabLink.and(this.page.locator('.active')).waitFor();
	}

	async openComments() {
		const commentsIcon = this.page.getByLabel('Comments');

		await commentsIcon.click();
	}

	async toggleShowAllDataConfiguration(check: boolean) {
		await this.instanceSettingsPage.goToInstanceSetting(
			'Publications',
			'Publications View Changes'
		);

		await this.page
			.getByLabel('Show all data when reviewing')
			.setChecked(check);

		await this.instanceSettingsPage.saveButton.click();

		await waitForAlert(
			this.page,
			`Success:Your request completed successfully.`
		);
	}

	async toggleSandboxConfiguration(check: boolean) {
		await this.instanceSettingsPage.goToInstanceSetting(
			'Publications',
			'Publications Settings'
		);

		await expect(this.page.getByText('Sandbox Enabled')).toBeVisible();

		const checkBox = this.page.getByLabel('Sandbox Enabled');

		if (check) {
			await checkBox.setChecked(true);

			await this.instanceSettingsPage.saveButton.click();

			await waitForAlert(
				this.page,
				`Success:Your request completed successfully.`
			);
		}
		else {
			await checkBox.setChecked(false);

			await this.instanceSettingsPage.saveButton.click();

			await waitForAlert(
				this.page,
				`Success:Your request completed successfully.`
			);
		}
	}

	async viewDisplayTab(tabLabel: string, {isHidden} = {isHidden: false}) {
		const tab = this.page.locator('nav.navbar');

		if (isHidden) {
			await expect(
				tab.locator('a', {
					hasText: tabLabel,
				})
			).toBeHidden();
		}
		else {
			await expect(
				tab.locator('a', {
					hasText: tabLabel,
				})
			).toBeVisible();
		}
	}
}
