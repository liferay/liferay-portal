/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {accountsPagesTest} from '../../../fixtures/accountsPagesTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {MockMockPage} from '../../../pages/smtp-server/MockMockPage';
import {UserLoginPage} from '../../../pages/users-admin-web/UserLoginPage';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import {waitForAlert} from '../../../utils/waitForAlert';
import {AccountManagementPage} from './pages/AccountManagementPage';
import {CreateAccountPage} from './pages/CreateAccountPage';
import {UserSelectorPage} from './pages/UserSelectorPage';

const test = mergeTests(
	accountsPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-62272': {enabled: true},
	}),
	loginTest()
);

let mockMockPage: MockMockPage;

test.beforeEach(async ({page}) => {
	const mockMockTab = await page.context().newPage();

	mockMockPage = new MockMockPage(mockMockTab);

	await mockMockPage.deleteAll();
});

test.afterEach(async () => {
	await mockMockPage.page.close();
});

test.skip(
	'Account management widget on the AI Hub site supports adding an existing user and inviting a new user via email',
	{tag: '@LPD-91103'},
	async ({
		accountUserInvitePage,
		accountUsersPage,
		apiHelpers,
		editAccountPage,
		page,
	}) => {
		const accountManagementPage = new AccountManagementPage(page);
		const userSelectorPage = new UserSelectorPage(page);

		const site = await apiHelpers.headlessAdminSite.postSite({
			name: `AI Hub ${getRandomInt()}`,
			templateKey: 'com.liferay.ai.hub.site.initializer',
			templateType: 'site-initializer',
		});

		const account = await apiHelpers.headlessAdminUser.postAccount({
			name: `Account ${getRandomInt()}`,
			type: 'business',
		});
		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		await accountManagementPage.goto(site.friendlyUrlPath);

		await expect(
			accountManagementPage.accountsTable.searchInput
		).toBeVisible();
		await expect(
			accountManagementPage.accountCell(account.name)
		).toBeVisible();

		await test.step('Verify the default AI Hub account is hidden from the widget', async () => {
			await accountManagementPage.accountsTable.search('Liferay AI Hub');

			await expect(
				accountManagementPage.accountCell('Liferay AI Hub')
			).toBeHidden();

			await accountManagementPage.accountsTable.search('');
		});

		await (
			await accountManagementPage.accountsTable.cellLink(account.name)
		).click();
		await editAccountPage.usersLink.click();

		await test.step('Assign the existing user', async () => {
			await accountUsersPage.usersTable.newButton.click();
			await accountUsersPage.assignUserMenuItem.click();

			await userSelectorPage.assignUsers([user.name]);

			await expect(
				accountUsersPage.usersTable.cell(user.name)
			).toBeVisible();
		});

		const invitedEmail = `invitee${getRandomString()}@liferay.com`;

		await test.step('Invite a new user', async () => {
			await accountUsersPage.usersTable.newButton.click();
			await accountUsersPage.inviteUserMenuItem.click();

			await accountUserInvitePage
				.emailAddressInput(accountUserInvitePage.firstEntry)
				.fill(invitedEmail);
			await accountUserInvitePage
				.emailAddressInput(accountUserInvitePage.firstEntry)
				.press('Enter');

			await accountUserInvitePage.inviteButton.click();

			await waitForAlert(page);
		});

		await test.step('Verify the invitation email landed and exposes a correct registration link', async () => {
			await mockMockPage.page.reload();

			await expect(
				mockMockPage.emailSubjectLink('You have been invited to')
			).toBeVisible({timeout: 10000});

			await mockMockPage
				.emailSubjectLink('You have been invited to')
				.click();

			await expect(
				mockMockPage.emailBodyText(account.name)
			).toBeVisible();

			const registrationLink =
				await mockMockPage.getEmailBodyLinkHref('ticketKey');

			expect(registrationLink).toContain('create-account');
			expect(registrationLink).toContain('ticketKey=');
		});
	}
);

test.skip(
	'A user invited from the AI Hub account management widget can complete registration, sign in, and is listed under the account',
	{tag: '@LPD-91103'},
	async ({
		accountUserInvitePage,
		accountUsersPage,
		apiHelpers,
		editAccountPage,
		page,
	}) => {
		const accountManagementPage = new AccountManagementPage(page);

		const site = await apiHelpers.headlessAdminSite.postSite({
			name: `AI Hub ${getRandomInt()}`,
			templateKey: 'com.liferay.ai.hub.site.initializer',
			templateType: 'site-initializer',
		});

		const account = await apiHelpers.headlessAdminUser.postAccount({
			name: `Account ${getRandomInt()}`,
			type: 'business',
		});

		const invitedEmail = `invitee${getRandomString()}@liferay.com`;

		await test.step('Invite a not-yet-existing user via the widget', async () => {
			await accountManagementPage.goto(site.friendlyUrlPath);

			await (
				await accountManagementPage.accountsTable.cellLink(account.name)
			).click();
			await editAccountPage.usersLink.click();
			await accountUsersPage.usersTable.newButton.click();
			await accountUsersPage.inviteUserMenuItem.click();
			await accountUserInvitePage
				.emailAddressInput(accountUserInvitePage.firstEntry)
				.fill(invitedEmail);
			await accountUserInvitePage
				.emailAddressInput(accountUserInvitePage.firstEntry)
				.press('Enter');
			await accountUserInvitePage.inviteButton.click();

			await waitForAlert(page);
		});

		const registrationLink =
			await test.step('Read the registration link from the invitation email', async () => {
				await mockMockPage.page.reload();

				await expect(
					mockMockPage.emailSubjectLink('You have been invited to')
				).toBeVisible({timeout: 10000});

				await mockMockPage
					.emailSubjectLink('You have been invited to')
					.click();

				const link =
					await mockMockPage.getEmailBodyLinkHref('ticketKey');

				expect(link).toContain('ticketKey=');

				return link;
			});

		const screenName = `invitee${getRandomString()}`.toLowerCase();
		const password = 'test';

		const inviteeContext = await page.context().browser()!.newContext();
		const inviteePage = await inviteeContext.newPage();

		const createAccountPage = new CreateAccountPage(inviteePage);

		await test.step('Follow the invitation link', async () => {
			await inviteePage.goto(registrationLink);
		});

		await test.step('Complete the registration form', async () => {
			await createAccountPage.register({
				password,
				screenName,
			});
		});

		await test.step('Verify the success alert is visible on the home page after registration', async () => {
			await waitForAlert(
				inviteePage,
				'Thank you for creating an account',
				{autoClose: false}
			);
		});

		await test.step('Sign in as the newly registered user via the portal login form', async () => {
			const userLoginPage = new UserLoginPage(inviteePage);

			await expect(async () => {
				await inviteePage.goto('/');

				await userLoginPage.signInNavButton.click({timeout: 2000});

				await userLoginPage.emailAddressInput.fill(invitedEmail);
				await userLoginPage.passwordInput.fill(password);

				await userLoginPage.signInButton.click({timeout: 2000});

				await expect(userLoginPage.userProfileMenuButton).toBeVisible({
					timeout: 5000,
				});
			}).toPass({timeout: 30000});

			await inviteeContext.close();
		});

		await test.step('Verify the invitee is now related to the account', async () => {
			await accountManagementPage.goto(site.friendlyUrlPath);

			await (
				await accountManagementPage.accountsTable.cellLink(account.name)
			).click();

			await editAccountPage.usersLink.click();

			await expect(
				accountUsersPage.usersTable.cell(screenName, false)
			).toBeVisible({timeout: 10000});
		});
	}
);
