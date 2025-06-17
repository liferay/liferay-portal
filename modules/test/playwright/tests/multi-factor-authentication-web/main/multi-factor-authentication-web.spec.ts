/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {loginTest} from '../../../fixtures/loginTest';
import {multiFactorAuthenticationPagesTest} from '../../../fixtures/multiFactorAuthenticationPagesTest';
import {remotePageTest} from '../../../fixtures/remotePageTest';
import {systemSettingsPageTest} from '../../../fixtures/systemSettingsPageTest';
import {usersAndOrganizationsPagesTest} from '../../../fixtures/usersAndOrganizationsPagesTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import performLogin, {performLogout} from '../../../utils/performLogin';
import {waitForAlert} from '../../../utils/waitForAlert';

const remotePort = '9080';
const remotePage = remotePageTest(remotePort);

export const test = mergeTests(
	loginTest(),
	multiFactorAuthenticationPagesTest,
	remotePage,
	systemSettingsPageTest,
	usersAndOrganizationsPagesTest
);

test('LPD-56908 verify that only one user notification is sent along the cluster when MFA is disabled system wide', async ({
	multiFactorAuthenticationConfigurationPage,
	page,
	remotePage,
	systemSettingsPage,
}) => {
	await systemSettingsPage.goToSystemSetting(
		'Multi-Factor Authentication',
		'Multi-Factor Authentication System Configuration'
	);

	try {
		await page
			.getByLabel('Disable Multi-Factor Authentication', {exact: true})
			.check();
		await page.getByTestId('submitConfiguration').click();

		await waitForAlert(page);

		await multiFactorAuthenticationConfigurationPage.goto();

		await multiFactorAuthenticationConfigurationPage.enable();

		await page.getByTestId('userPersonalMenu').click();

		await page.getByRole('menuitem', {name: 'Notifications'}).click();
		await expect(
			page.getByRole('link', {
				name: 'Multi-factor authentication',
			})
		).toBeVisible();
		let mfaNotification = page.getByRole('link', {
			name: 'Multi-factor authentication',
		});
		await expect(mfaNotification).toHaveCount(1);

		await remotePage.getByTestId('userPersonalMenu').click();
		await remotePage.getByRole('menuitem', {name: 'Notifications'}).click();
		await expect(
			remotePage.getByRole('link', {
				name: 'Multi-factor authentication',
			})
		).toBeVisible();
		mfaNotification = remotePage.getByRole('link', {
			name: 'Multi-factor authentication',
		});
		await expect(mfaNotification).toHaveCount(1);

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {name: 'Delete'}),
			trigger: page.getByTestId('row').getByLabel('Show Actions'),
		});

		await multiFactorAuthenticationConfigurationPage.goto();

		await multiFactorAuthenticationConfigurationPage.resetConfiguration();
	}
	finally {
		await performLogout(page);

		await performLogin(page, 'test');

		await systemSettingsPage.goToSystemSetting(
			'Multi-Factor Authentication',
			'Multi-Factor Authentication System Configuration'
		);

		await page
			.getByLabel('Disable Multi-Factor Authentication', {exact: true})
			.uncheck();

		await page.getByTestId('submitConfiguration').click();

		await multiFactorAuthenticationConfigurationPage.goto();

		await multiFactorAuthenticationConfigurationPage.resetConfiguration();
	}
});
