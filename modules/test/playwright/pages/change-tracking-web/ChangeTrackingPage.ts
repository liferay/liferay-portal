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

type CTCollection = {body: any; response?: Response};

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

	async assertStatus(status: string, title: string) {
		await this.goToPublicationHistory();

		await this.page
			.locator('.fds tbody tr')
			.filter({
				has: this.page.getByText(title),
			})
			.filter({
				has: this.page.getByText(status, {exact: true}),
			})
			.waitFor();

		await this.page
			.locator('.fds tbody tr')
			.filter({
				has: this.page.getByText(title),
			})
			.filter({
				has: this.page.getByText(status, {exact: true}),
			})
			.isVisible();
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

	async enablePublications(check: boolean) {
		await this.goToPublicationsViaApplicationMenu();

		if (
			await this.page
				.getByTestId('headerTitle')
				.filter({hasText: 'Publications'})
				.isVisible()
		) {
			await this.page.getByLabel('Options').click();

			await this.page.getByRole('menuitem', {name: 'Settings'}).click();

			await expect(
				this.page.getByText('Enable Publications')
			).toBeVisible();
		}

		const checkBox = this.page.getByRole('checkbox', {
			name: 'Enable Publications',
		});

		if (check) {
			await checkBox.setChecked(true);

			await expect(
				this.page.getByText('Allow Unapproved Changes')
			).toBeVisible();

			await this.goto();
		}
		else {
			await checkBox.setChecked(false);

			await expect(
				this.page.getByText('Allow Unapproved Changes')
			).not.toBeVisible();
		}
	}

	async goto(languageCode?: string) {
		const languageUrlPath = languageCode ? `/${languageCode}` : '';

		await this.page.goto(
			`${languageUrlPath}/group/guest${PORTLET_URLS.publications}`
		);

		const changeTrackingIndicatorButton = this.page.locator(
			'.change-tracking-indicator-button'
		);

		if (!(await changeTrackingIndicatorButton.isVisible())) {
			await this.enablePublications(true);
		}
	}

	async goToPublicationsViaApplicationMenu() {
		await this.page.getByLabel('Open Applications MenuCtrl+Alt+A').click();

		await this.page.getByRole('menuitem', {name: 'Publications'}).click();

		const enablePublications = this.page.getByText('Enable Publications');

		const publicationsHeader = this.page
			.getByTestId('headerTitle')
			.filter({hasText: 'Publications'});

		await expect(enablePublications.or(publicationsHeader)).toBeVisible();
	}

	async goToPublicationHistory() {
		await this.goto();

		await this.selectTab('History');
	}

	async gotoPublicationsPermissions() {
		await this.goto();

		await this.page.getByLabel('Options').click();

		await this.page.getByRole('menuitem', {name: 'Settings'}).click();

		await expect(
			this.page.getByRole('heading', {name: 'Permissions'})
		).toBeVisible();

		await expect(this.page.getByRole('alert')).toBeVisible();

		await this.page.getByRole('button', {name: 'Edit Permissions'}).click();
	}

	async goToReviewChanges(title: string, languageCode?: string) {
		if (languageCode) {
			await this.goto(languageCode);
		}
		else {
			await this.goto();
		}

		await this.page
			.locator('#fnsd___table-id  .table-list-title')
			.filter({hasText: title})
			.first()
			.waitFor();

		await this.page.getByRole('link', {exact: true, name: title}).click();

		if (!languageCode) {
			await this.page
				.locator(
					'#_com_liferay_change_tracking_web_portlet_PublicationsPortlet_controlMenu'
				)
				.filter({hasText: 'Review Changes'})
				.waitFor();
		}
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

	async goToReviewChangesScheduled(title: string) {
		await this.goto();

		await this.selectTab('Scheduled');

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

	async switchLanguage(language: string) {
		await this.page.getByLabel('show-available-locales').click();

		await this.page
			.getByRole('menuitem', {
				name: `${language} Translated`,
			})
			.click();
	}

	async workOnProduction() {
		const apiHelpers = new ApiHelpers(this.page);

		await apiHelpers.headlessChangeTracking.checkoutCTCollection(0);

		await this.page.reload();
	}

	async workOnPublication(ctCollection: CTCollection) {
		const apiHelpers = new ApiHelpers(this.page);

		await apiHelpers.headlessChangeTracking.checkoutCTCollection(
			ctCollection.body.id
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

	async publishSandboxPublication(title: string) {
		await this.goto();

		await this.page.getByRole('link', {name: title}).first().click();

		await this.page.getByRole('link', {name: 'Publish'}).waitFor();

		await this.page.getByRole('link', {name: 'Publish'}).click();

		await expect(
			this.page.getByText('No unresolved conflicts, ready to publish.')
		).toBeVisible();

		await this.page.getByRole('button', {name: 'Publish'}).click();
	}

	async toggleSandboxConfiguration(check: boolean) {
		await this.goto();

		await this.page.getByLabel('Options').click();

		await this.page.getByRole('menuitem', {name: 'Settings'}).click();

		await expect(this.page.getByText('Enable Publications')).toBeVisible();

		const checkBox = this.page.getByRole('checkbox', {
			name: 'enable-sandbox-only',
		});

		const publicationsEnabled = this.page.getByRole('checkbox', {
			name: 'Enable Publications',
		});

		if (check) {
			await checkBox.setChecked(true);

			await expect(publicationsEnabled).toBeChecked();

			await expect(checkBox).toBeChecked();
		}
		else {
			await checkBox.setChecked(false);

			await expect(publicationsEnabled).toBeChecked();

			await expect(checkBox).not.toBeChecked();
		}
	}

	async viewChanges({
		changed,
		click,
		isVisible,
		site,
		title,
		type,
	}: {
		changed?: string;
		click?: boolean;
		isVisible?: boolean;
		site?: string;
		title: string;
		type?: string;
	}) {
		let fdsRow = this.page.locator('.fds tbody tr').filter({
			has: this.page.getByText(title),
		});

		if (changed) {
			fdsRow = fdsRow.filter({
				has: this.page.getByRole('cell', {name: changed}),
			});
		}

		if (site) {
			fdsRow = fdsRow.filter({
				has: this.page.getByRole('cell', {name: site}),
			});
		}
		if (type) {
			fdsRow = fdsRow.filter({
				has: this.page.getByRole('cell', {name: type}),
			});
		}

		if (isVisible === true) {
			await expect(fdsRow).toBeVisible();
		}
		else if (isVisible === false) {
			await expect(fdsRow).toBeHidden();
		}

		if (click === true) {
			await fdsRow.getByRole('link', {name: title}).first().click();
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
