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
import {productMenuPageTest} from '../../../fixtures/productMenuPageTest';
import {usersAndOrganizationsPagesTest} from '../../../fixtures/usersAndOrganizationsPagesTest';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import performLogin, {
	performLoginViaApi,
	performLogout,
	userData,
} from '../../../utils/performLogin';
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
	productMenuPageTest,
	usersAndOrganizationsPagesTest
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
