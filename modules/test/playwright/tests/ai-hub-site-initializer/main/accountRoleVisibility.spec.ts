/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {accountsPagesTest} from '../../../fixtures/accountsPagesTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {UserLoginPage} from '../../../pages/users-admin-web/UserLoginPage';
import {getRandomInt} from '../../../utils/getRandomInt';
import {AccountManagementPage} from './pages/AccountManagementPage';
import {UserSelectorPage} from './pages/UserSelectorPage';

const test = mergeTests(
	accountsPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-62272': {enabled: true},
	}),
	loginTest()
);

test.skip(
	"AI Hub site filters the navigation menu by the invitee's account roles",
	{tag: '@LPD-90465'},
	async ({
		accountRoleSelectorPage,
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

		await (
			await accountManagementPage.accountsTable.cellLink(account.name)
		).click();
		await editAccountPage.usersLink.click();
		await accountUsersPage.usersTable.newButton.click();
		await accountUsersPage.assignUserMenuItem.click();

		await userSelectorPage.assignUsers([user.name!]);

		await expect(
			accountUsersPage.usersTable.cell(user.name!)
		).toBeVisible();

		const userContext = await page.context().browser()!.newContext();
		const userPage = await userContext.newPage();

		const userLoginPage = new UserLoginPage(userPage);

		await expect(async () => {
			await userPage.goto('/');

			await userLoginPage.signInNavButton.click({timeout: 2000});

			await userLoginPage.emailAddressInput.fill(user.emailAddress!);
			await userLoginPage.passwordInput.fill('test');

			await userLoginPage.signInButton.click({timeout: 2000});

			await expect(userLoginPage.userProfileMenuButton).toBeVisible({
				timeout: 5000,
			});
		}).toPass({timeout: 30000});

		async function assertVisibleMenuPages(
			pageNames: string[],
			hiddenPageNames: string[]
		) {
			await userPage.goto(`/web${site.friendlyUrlPath}/home`);

			const navigation = userPage.locator(
				'nav[aria-label="AI Hub Primary Navigation"]'
			);

			await expect(navigation).toBeVisible({timeout: 10000});

			for (const name of pageNames) {
				await expect(
					navigation.getByRole('menuitem', {exact: true, name})
				).toBeVisible({timeout: 5000});
			}

			for (const name of hiddenPageNames) {
				await expect(
					navigation.getByRole('menuitem', {exact: true, name})
				).toHaveCount(0);
			}
		}

		async function assignAccountRole(roleName: string) {
			await (
				await accountUsersPage.usersTable.rowActions(user.name!)
			).click();

			await accountUsersPage.assignRolesMenuItem.click();

			await accountRoleSelectorPage.selectRoles([roleName]);
		}

		await test.step('Without any account role the user sees only Home', async () => {
			await assertVisibleMenuPages(
				['Home'],
				['Account Management', 'Agent Builder', 'Chatbots']
			);
		});

		await test.step('Account Administrator grants Account Management', async () => {
			await assignAccountRole('Account Administrator');

			await assertVisibleMenuPages(
				['Account Management', 'Home'],
				['Agent Builder', 'Chatbots']
			);
		});

		await test.step('AI Hub Agent Viewer grants Agent Builder', async () => {
			await assignAccountRole('AI Hub Agent Viewer');

			await assertVisibleMenuPages(
				['Account Management', 'Agent Builder', 'Home'],
				['Chatbots']
			);
		});

		await test.step('AI Hub Agent Administrator does not add new visible pages on top of Agent Viewer', async () => {
			await assignAccountRole('AI Hub Agent Administrator');

			await assertVisibleMenuPages(
				['Account Management', 'Agent Builder', 'Home'],
				['Chatbots']
			);
		});

		await test.step('AI Hub Administrator grants every AI Hub navigation page', async () => {
			await assignAccountRole('AI Hub Administrator');

			await assertVisibleMenuPages(
				['Account Management', 'Agent Builder', 'Chatbots', 'Home'],
				[]
			);
		});

		await userContext.close();
	}
);
