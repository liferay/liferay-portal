/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {instanceSettingsPagesTest} from '../../../fixtures/instanceSettingsPagesTest';
import {loginTest} from '../../../fixtures/loginTest';
import {usersAndOrganizationsPagesTest} from '../../../fixtures/usersAndOrganizationsPagesTest';
import getRandomString from '../../../utils/getRandomString';
import {
	performLoginViaApi,
	performLogout,
	userData,
} from '../../../utils/performLogin';
import getBasicWebContentStructureId from '../../../utils/structured-content/getBasicWebContentStructureId';
import {waitForAlert} from '../../../utils/waitForAlert';

const test = mergeTests(
	dataApiHelpersTest,
	instanceSettingsPagesTest,
	loginTest(),
	usersAndOrganizationsPagesTest
);

test.afterEach(
	async ({page, termsOfUseInstanceSettingsPage, userLoginPage}) => {
		await performLoginViaApi({page, screenName: 'test'});

		await page.goto('/');

		await userLoginPage.agreeToTermsOfUseIfRequired();

		await termsOfUseInstanceSettingsPage.goto();

		await termsOfUseInstanceSettingsPage.termsOfUseRequiredCheckbox.uncheck();
		await termsOfUseInstanceSettingsPage.groupIdInput.fill('0');
		await termsOfUseInstanceSettingsPage.articleIdInput.fill('');
		await termsOfUseInstanceSettingsPage.saveButton.click();

		await waitForAlert(page);
	}
);

test(
	'Can disable terms of use',
	{tag: '@LPD-81993'},
	async ({
		apiHelpers,
		page,
		termsOfUseInstanceSettingsPage,
		userLoginPage,
	}) => {
		await test.step('Enable terms of use', async () => {
			await userLoginPage.agreeToTermsOfUseIfRequired();

			await termsOfUseInstanceSettingsPage.goto();

			await termsOfUseInstanceSettingsPage.termsOfUseRequiredCheckbox.check();

			const saveResponsePromise = page.waitForResponse(
				(response) =>
					response.url().includes('InstanceSettingsPortlet') &&
					response.request().method() === 'POST'
			);

			await termsOfUseInstanceSettingsPage.saveButton.click();

			await saveResponsePromise;

			await page.goto('/');

			await userLoginPage.agreeToTermsOfUseIfRequired();

			await termsOfUseInstanceSettingsPage.goto();

			await expect(
				termsOfUseInstanceSettingsPage.termsOfUseRequiredCheckbox
			).toBeChecked();
		});

		await test.step('Disable terms of use', async () => {
			await termsOfUseInstanceSettingsPage.termsOfUseRequiredCheckbox.uncheck();
			await termsOfUseInstanceSettingsPage.saveButton.click();

			await waitForAlert(page);

			await expect(
				termsOfUseInstanceSettingsPage.termsOfUseRequiredCheckbox
			).not.toBeChecked();
		});

		await test.step('Verify new user can login without terms of use', async () => {
			await page.goto('/');
			await page.waitForLoadState('networkidle');

			const userAccount =
				await apiHelpers.headlessAdminUser.postUserAccount();

			await performLogout(page);

			await userLoginPage.goto();
			await userLoginPage.emailAddressInput.fill(
				userAccount.emailAddress
			);
			await userLoginPage.passwordInput.fill(userData['test'].password);
			await userLoginPage.signInButton.click();

			await expect(userLoginPage.iAgreeButton).not.toBeVisible();
			await expect(userLoginPage.userProfileMenuButton).toBeVisible({
				timeout: 30000,
			});
		});
	}
);

test(
	'Can specify terms of use web content',
	{tag: '@LPD-81993'},
	async ({
		apiHelpers,
		page,
		termsOfUseInstanceSettingsPage,
		userLoginPage,
	}) => {
		const site = await apiHelpers.headlessAdminSite.postSite({
			name: `Site${getRandomString()}`,
		});

		const temsContent = 'This is a custom terms of use content.';

		const ddmStructureId = await getBasicWebContentStructureId(apiHelpers);

		const webContent =
			await apiHelpers.jsonWebServicesJournal.addWebContent({
				content: temsContent,
				ddmStructureId,
				groupId: Number(site.id),
				serviceContext: {
					addGroupPermissions: true,
					addGuestPermissions: true,
					workflowAction: 1,
				},
				titleMap: {en_US: `ToU WC ${getRandomString()}`},
			});

		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		await termsOfUseInstanceSettingsPage.goto();

		await termsOfUseInstanceSettingsPage.termsOfUseRequiredCheckbox.check();

		await termsOfUseInstanceSettingsPage.groupIdInput.fill(String(site.id));
		await termsOfUseInstanceSettingsPage.articleIdInput.fill(
			String(webContent.articleId)
		);
		await termsOfUseInstanceSettingsPage.saveButton.click();

		await waitForAlert(page);

		await performLogout(page);

		await userLoginPage.goto();
		await userLoginPage.emailAddressInput.fill(user.emailAddress);
		await userLoginPage.passwordInput.fill(userData['test'].password);
		await userLoginPage.signInButton.click();

		await expect(page.getByText(temsContent)).toBeVisible({
			timeout: 10000,
		});

		await userLoginPage.iAgreeButton.click();

		await expect(userLoginPage.userProfileMenuButton).toBeVisible({
			timeout: 20000,
		});
	}
);

test(
	'Reset terms of use consent displays the in-progress alert',
	{tag: '@LPD-81612'},
	async ({page, termsOfUseInstanceSettingsPage}) => {
		await termsOfUseInstanceSettingsPage.goto();

		page.once('dialog', (dialog) => dialog.accept());

		await termsOfUseInstanceSettingsPage.resetConsentButton.click();

		await waitForAlert(
			page,
			'Success:Terms of use consent reset is in progress.'
		);
	}
);
