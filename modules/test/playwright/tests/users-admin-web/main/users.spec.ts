/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {accountSettingsPagesTest} from '../../../fixtures/accountSettingsPagesTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {messageBoardsPagesTest} from '../../../fixtures/messageBoardsTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {productMenuPageTest} from '../../../fixtures/productMenuPageTest';
import {usersAndOrganizationsPagesTest} from '../../../fixtures/usersAndOrganizationsPagesTest';
import {workflowPagesTest} from '../../../fixtures/workflowPagesTest';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import {nextPage} from '../../../utils/pagination';
import performLogin, {
	performLoginViaApi,
	performLogout,
	userData,
} from '../../../utils/performLogin';
import {waitForAlert} from '../../../utils/waitForAlert';
import getPageDefinition from '../../layout-content-page-editor-web/main/utils/getPageDefinition';
import getWidgetDefinition from '../../layout-content-page-editor-web/main/utils/getWidgetDefinition';

export const test = mergeTests(
	accountSettingsPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	messageBoardsPagesTest,
	pageEditorPagesTest,
	productMenuPageTest,
	usersAndOrganizationsPagesTest,
	workflowPagesTest
);

test(
	'Cannot create user with invalid email address',
	{tag: '@LPD-57000'},
	async ({editUserPage, usersAndOrganizationsPage}) => {
		const testEmailAddress = async (
			emailAddress: string,
			emailAddressVariation: string
		) => {
			await editUserPage.emailAddressInput.fill(emailAddress);
			await editUserPage.saveButton.click();

			if (emailAddressVariation === 'empty') {
				await expect(editUserPage.emailAddressError).toHaveText(
					'The Email Address field is required.'
				);
			}
			else if (emailAddressVariation === 'root') {
				await editUserPage.screenNameInput.fill(getRandomString());
				await editUserPage.firstNameInput.fill(getRandomString());
				await editUserPage.lastNameInput.fill(getRandomString());
				await editUserPage.saveButton.click();

				await expect(
					editUserPage.emailAddressInvalidError
				).toBeVisible();
			}
			else if (emailAddressVariation === 'valid') {
				await expect(editUserPage.emailAddressError).not.toBeVisible();
			}
			else {
				await expect(editUserPage.emailAddressError).toHaveText(
					'Please enter a valid email address.'
				);
			}
		};

		await usersAndOrganizationsPage.goToUsers();

		await usersAndOrganizationsPage.addUserButton.click();

		await testEmailAddress('', 'empty');
		await testEmailAddress('newuser`@liferay.com', 'valid');
		await testEmailAddress('newuser\\@liferay.com', 'invalid');
		await testEmailAddress('newuser^@liferay.com', 'valid');
		await testEmailAddress('newuser(@liferay.com', 'invalid');
		await testEmailAddress('newuser{@liferay.com', 'valid');
		await testEmailAddress('newuser)@liferay.com', 'invalid');
		await testEmailAddress('newuser}@liferay.com', 'valid');
		await testEmailAddress('newuser[@liferay.com', 'invalid');
		await testEmailAddress('newuser?@liferay.com', 'valid');
		await testEmailAddress('newuser]@liferay.com', 'invalid');
		await testEmailAddress('newuser&@liferay.com', 'valid');
		await testEmailAddress('newuser:@liferay.com', 'invalid');
		await testEmailAddress("newuser'@liferay.com", 'valid');
		await testEmailAddress('newuser–@liferay.com', 'invalid');
		await testEmailAddress('newuser=@liferay.com', 'valid');
		await testEmailAddress('newuser>@liferay.com', 'invalid');
		await testEmailAddress('newuser-@liferay.com', 'valid');
		await testEmailAddress('newuser.@liferay.com', 'invalid');
		await testEmailAddress('newuser/@liferay.com', 'valid');
		await testEmailAddress('newuser;@liferay.com', 'invalid');
		await testEmailAddress('newuser_@liferay.com', 'valid');
		await testEmailAddress('newuser@@liferay.com', 'invalid');
		await testEmailAddress('newuser%@liferay.com', 'valid');
		await testEmailAddress('root@liferay.com', 'root');
	}
);

test(
	'User with apostrophe in email can login and use the portal',
	{tag: ['@LPD-57000', '@LPS-102425']},
	async ({
		accountSettingsPage,
		apiHelpers,
		messageBoardsEditThreadPage,
		messageBoardsPage,
		messageBoardsWidgetPage,
		page,
		productMenuPage,
		site,
		userAssociatedDataBlogPage,
		userAssociatedDataMessageBoardPage,
		userAssociatedDataMessageBoardWidgetPage,
	}) => {
		const name = "user'name";

		const userAccountWithApostrophe =
			await apiHelpers.headlessAdminUser.postUserAccount({
				emailAddress: `${name}@liferay.com`,
			});

		userData[name] = {
			name: userAccountWithApostrophe.givenName,
			password: 'test',
			surname: userAccountWithApostrophe.familyName,
		};

		const messageBoardPage =
			await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([
					getWidgetDefinition({
						id: getRandomString(),
						widgetName:
							'com_liferay_message_boards_web_portlet_MBPortlet',
					}),
				]),
				siteId: site.id,
				title: 'page' + getRandomInt(),
			});

		await messageBoardsWidgetPage.addMessageBoardsPortlet(site);

		await page.goto(`/web/${site.name}${messageBoardPage.friendlyUrlPath}`);

		await expect(async () => {
			await userAssociatedDataMessageBoardWidgetPage.actionsButton.click();
			await userAssociatedDataMessageBoardWidgetPage.permissionsMenuItem.click(
				{timeout: 500}
			);
		}).toPass();

		await userAssociatedDataMessageBoardPage.setPermissions([
			'#user_ACTION_ADD_MESSAGE',
		]);

		await performLogout(page);
		await performLogin(page, name);

		await accountSettingsPage.goToAccountSettings();
		await accountSettingsPage.organizationsMenuItem.click();

		await expect(
			accountSettingsPage.optionalHeading('Organizations')
		).toBeVisible();

		await accountSettingsPage.membershipsMenuItem.click();

		await expect(
			accountSettingsPage.optionalHeading('Memberships')
		).toBeVisible();

		await accountSettingsPage.rolesMenuItem.click();

		await expect(
			accountSettingsPage.optionalHeading('Roles')
		).toBeVisible();

		await accountSettingsPage.passwordMenuItem.click();

		await expect(
			accountSettingsPage.optionalHeading('Password')
		).toBeVisible();

		await page.goto(`/web/${site.name}${messageBoardPage.friendlyUrlPath}`);

		await messageBoardsPage.newThreadButton.click();

		await messageBoardsEditThreadPage.publishNewBasicThread(
			'Thread Subject created as User',
			'Thread Body created as User'
		);

		await performLogout(page);
		await performLoginViaApi({page, screenName: 'test'});

		const adminRole =
			await apiHelpers.headlessAdminUser.getRoleByName('Administrator');

		await apiHelpers.headlessAdminUser.postRoleByExternalReferenceCodeUserAccountAssociation(
			adminRole.externalReferenceCode,
			userAccountWithApostrophe.id
		);

		await performLogout(page);
		await performLogin(page, name);

		await page.goto(`/web/${site.name}${messageBoardPage.friendlyUrlPath}`);

		await messageBoardsPage.newThreadButton.click();

		await messageBoardsEditThreadPage.publishNewBasicThread(
			'Thread Subject created as Admin',
			'Thread Body created as Admin'
		);

		await expect(
			userAssociatedDataMessageBoardWidgetPage.threadSubjectLink(
				'Thread Subject created as Admin'
			)
		).toBeVisible();

		await productMenuPage.goToBlogs();

		await userAssociatedDataBlogPage.newButton.click();
		await userAssociatedDataBlogPage.blogTitleInput.fill(
			'Blog Title created as Admin' + getRandomInt()
		);
		await userAssociatedDataBlogPage.blogContentInput.click();
		await userAssociatedDataBlogPage.blogContentInput.fill(
			'Blog Content created as Admin' + getRandomInt()
		);
		await userAssociatedDataBlogPage.publishButton.click();

		await expect(
			userAssociatedDataBlogPage.blogTitleLink(
				'Blog Title created as Admin'
			)
		).toBeVisible();
	}
);

test(
	'Cannot create user with invalid screen name',
	{tag: '@LPD-57460'},
	async ({editUserPage, usersAndOrganizationsPage}) => {
		const testScreenName = async (
			screenName: string,
			screenNameVariation: string
		) => {
			await editUserPage.screenNameInput.fill(screenName);
			await editUserPage.saveButton.click();

			if (screenNameVariation === 'empty') {
				await expect(editUserPage.screenNameError).toHaveText(
					'The Screen Name field is required.'
				);
			}
			else if (screenNameVariation === 'valid') {
				await expect(editUserPage.screenNameError).not.toBeVisible();
			}
			else {
				await expect(editUserPage.screenNameError).toContainText(
					'The screen name cannot be an email address or a reserved word'
				);

				await editUserPage.screenNameInput.fill('validScreenName');
			}
		};

		await usersAndOrganizationsPage.goToUsers();

		await usersAndOrganizationsPage.addUserButton.click();

		await testScreenName('', 'empty');
		await testScreenName('newuser`', 'invalid');
		await testScreenName('newuser\\', 'invalid');
		await testScreenName('newuser^', 'invalid');
		await testScreenName('newuser(', 'invalid');
		await testScreenName('newuser{', 'invalid');
		await testScreenName('newuser)', 'invalid');
		await testScreenName('newuser}', 'invalid');
		await testScreenName('newuser[', 'invalid');
		await testScreenName('newuser?', 'invalid');
		await testScreenName('newuser]', 'invalid');
		await testScreenName('newuser&', 'invalid');
		await testScreenName('newuser:', 'invalid');
		await testScreenName("newuser'", 'invalid');
		await testScreenName('newuser–', 'invalid');
		await testScreenName('newuser=', 'invalid');
		await testScreenName('newuser>', 'invalid');
		await testScreenName('newuser/', 'invalid');
		await testScreenName('newuser;', 'invalid');
		await testScreenName('newuser@', 'invalid');
		await testScreenName('newuser%', 'invalid');
		await testScreenName('newuser$', 'invalid');
		await testScreenName('newuser#', 'invalid');
		await testScreenName('newuser!', 'invalid');
		await testScreenName('newuser-', 'valid');
		await testScreenName('newuser.', 'valid');
		await testScreenName('newuser_', 'valid');
		await testScreenName('0123456789', 'valid');
	}
);

test(
	'Adding special characters to user name',
	{tag: '@LPD-57460'},
	async ({apiHelpers, editUserPage, page, usersAndOrganizationsPage}) => {
		const name = '<username>';

		const userAccount = await apiHelpers.headlessAdminUser.postUserAccount({
			givenName: name,
		});

		userData[userAccount.alternateName] = {
			name: userAccount.givenName,
			password: 'test',
			surname: userAccount.familyName,
		};

		const adminRole =
			await apiHelpers.headlessAdminUser.getRoleByName('Administrator');

		await apiHelpers.headlessAdminUser.postRoleByExternalReferenceCodeUserAccountAssociation(
			adminRole.externalReferenceCode,
			userAccount.id
		);

		await usersAndOrganizationsPage.goToUsers(false);

		await expect(
			usersAndOrganizationsPage.usersTableCell(
				name + ' ' + userAccount.familyName
			)
		).toBeVisible();

		await usersAndOrganizationsPage.goToUser(
			name + ' ' + userAccount.familyName
		);

		await expect(editUserPage.firstNameInput).toHaveValue(name);

		await performLogout(page);
		await performLoginViaApi({page, screenName: userAccount.alternateName});

		await page.goto(`/web/${userAccount.alternateName}`);

		await expect(
			page.getByText(name + ' ' + userAccount.familyName)
		).toHaveCount(3);
	}
);

test(
	'Can add new users',
	{tag: '@LPD-57819'},
	async ({apiHelpers, usersAndOrganizationsPage}) => {
		const userNames: string[] = [];

		for (let i = 0; i < 3; i++) {
			const userName = `user${getRandomInt()}`;

			await usersAndOrganizationsPage.createUser(apiHelpers, userName);

			userNames.push(userName);
		}

		await usersAndOrganizationsPage.goto();

		for (const userName of userNames) {
			await expect(
				usersAndOrganizationsPage.usersTableCell(userName)
			).toBeVisible();
		}
	}
);

test(
	'Can filter users by unassociated users',
	{tag: '@LPD-57819'},
	async ({apiHelpers, usersAndOrganizationsPage}) => {
		const account = await apiHelpers.headlessAdminUser.postAccount();
		const organization =
			await apiHelpers.headlessAdminUser.postOrganization();
		const user1 = await apiHelpers.headlessAdminUser.postUserAccount();
		const user2 = await apiHelpers.headlessAdminUser.postUserAccount();
		const user3 = await apiHelpers.headlessAdminUser.postUserAccount();

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account.id,
			[user1.emailAddress]
		);
		await apiHelpers.headlessAdminUser.assignUserToOrganizationByEmailAddress(
			organization.id,
			user2.emailAddress
		);

		await usersAndOrganizationsPage.goto();

		await usersAndOrganizationsPage.filterUsers('Unassociated Users');

		await expect(
			usersAndOrganizationsPage.usersTableCell(user3.alternateName)
		).toBeVisible();
		await expect(
			usersAndOrganizationsPage.usersTableCell('test')
		).toBeVisible();
		await expect(
			usersAndOrganizationsPage.usersTableCell(user1.alternateName)
		).not.toBeVisible();
		await expect(
			usersAndOrganizationsPage.usersTableCell(user2.alternateName)
		).not.toBeVisible();
	}
);

test(
	'Can assign user group to user',
	{tag: '@LPD-57819'},
	async ({apiHelpers, editUserPage, page, usersAndOrganizationsPage}) => {
		const user = await apiHelpers.headlessAdminUser.postUserAccount();
		const userGroup = await apiHelpers.headlessAdminUser.postUserGroup();

		await usersAndOrganizationsPage.goto();

		await (
			await usersAndOrganizationsPage.usersTableRowLink(
				user.alternateName
			)
		).click();

		await editUserPage.membershipsLink.click();

		await expect(editUserPage.membershipsNoUserGroupsMessage).toBeVisible();

		await editUserPage.selectUserGroupsButton.click();
		await editUserPage.selectUserGroupTable.changeView('table');
		await editUserPage.selectUserGroupTable.cell(userGroup.name).click();

		await expect(
			(
				await editUserPage.membershipsUserGroupsTableRow(
					0,
					userGroup.name,
					true
				)
			).row
		).toBeVisible();

		await editUserPage.saveButton.click();

		await waitForAlert(page);

		await usersAndOrganizationsPage.goto();

		await expect(
			usersAndOrganizationsPage.usersTableCell(userGroup.name)
		).toBeVisible();
	}
);

test(
	'Can receive new user notification when single approver workflow is applied to user',
	{tag: ['@LPD-57819', '@LPS-200153']},
	async ({apiHelpers, configurationTabPage, notificationsPage, page}) => {
		await configurationTabPage.goTo();

		await nextPage(page);

		await configurationTabPage.assignWorkflowToAssetType(
			'Single Approver',
			'User'
		);

		try {
			await apiHelpers.headlessAdminUser.postUserAccount();

			await expect(async () => {
				await notificationsPage.goto();

				await expect(
					notificationsPage.workflowReviewMessage('User')
				).toBeVisible();

				await notificationsPage.selectAllItemsCheckbox.check();
				await notificationsPage.deleteButton.click();

				await waitForAlert(
					page,
					'Notifications were deleted successfully.'
				);
			}).toPass();
		}
		finally {
			await configurationTabPage.goTo();

			await nextPage(page);

			await configurationTabPage.unassignWorkflowFromAssetType('User');
		}
	}
);

test(
	'Assert relevance sort option displays when searching',
	{tag: ['@LPD-57819', '@LPS-130750']},
	async ({apiHelpers, page, usersAndOrganizationsPage}) => {
		await apiHelpers.headlessAdminUser.postUserAccount();
		await apiHelpers.headlessAdminUser.postUserAccount();

		await usersAndOrganizationsPage.goto();

		await usersAndOrganizationsPage.tableOrderMenu.click();

		await expect(
			usersAndOrganizationsPage.tableOrderMenuItem('Relevance')
		).not.toBeVisible();

		await usersAndOrganizationsPage.usersSearchBar.fill('User');
		await usersAndOrganizationsPage.usersSearchBarButton.click();

		await expect(page.getByText('Search Results')).toBeVisible();

		await usersAndOrganizationsPage.tableOrderMenu.click();

		await expect(
			usersAndOrganizationsPage.tableOrderMenuItem('Relevance')
		).toBeVisible();
	}
);

test(
	'Can add a new user after publishing a content page',
	{tag: ['@LPD-57819', '@LPS-95340']},
	async ({apiHelpers, pageEditorPage, site, usersAndOrganizationsPage}) => {
		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getWidgetDefinition({
					id: getRandomString(),
					widgetName:
						'com_liferay_asset_publisher_web_portlet_AssetPublisherPortlet',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await pageEditorPage.publishPage();

		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		await usersAndOrganizationsPage.goto();

		await expect(
			usersAndOrganizationsPage.usersTableCell(user.alternateName)
		).toBeVisible();
	}
);
