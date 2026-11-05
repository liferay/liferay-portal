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

test(
	'shows the descriptions of the multi-factor authentication configuration entries',
	{tag: ['@LPS-110309', '@LPS-112805']},
	async ({
		multiFactorAuthenticationConfigurationPage,
		page,
		systemSettingsPage,
	}) => {
		await test.step('MFA system configuration', async () => {
			await systemSettingsPage.goToSystemSetting(
				'Multi-Factor Authentication',
				'Multi-Factor Authentication System Configuration'
			);

			const disableMultiFactorAuthenticationCheckbox = page.getByLabel(
				'Disable Multi-Factor Authentication',
				{exact: true}
			);

			await expect(
				disableMultiFactorAuthenticationCheckbox
			).not.toBeChecked();
			await expect(
				disableMultiFactorAuthenticationCheckbox
			).toHaveAccessibleDescription(
				'Disable multi-factor authentication system-wide.'
			);
		});

		await test.step('Email one-time password configuration', async () => {
			await multiFactorAuthenticationConfigurationPage.goto();

			await expect(
				multiFactorAuthenticationConfigurationPage.enabledCheckBox
			).toHaveAccessibleDescription(
				'Enabling multi-factor authentication implies enabling Email One-Time Password. It is strongly advised that other forms of user authentication that only support a single factor are disabled (e.g. Basic, Digest, WEBDAV).'
			);
			await expect(
				multiFactorAuthenticationConfigurationPage.oneTimePasswordLength
			).toHaveAccessibleDescription(
				'Set the length of a one-time password.'
			);
			await expect(
				multiFactorAuthenticationConfigurationPage.resendEmailTimeout
			).toHaveAccessibleDescription(
				'Set the allowed time in seconds between one-time password requests.'
			);
			await expect(
				page.getByLabel('Template for Email "From" Field')
			).toHaveAccessibleDescription(
				'Set the sender address on the one-time password email.'
			);
			await expect(
				multiFactorAuthenticationConfigurationPage.failedAttemptsAllowed
			).toHaveAccessibleDescription(
				'Set the number of verification attempts allowed before blocking for the retry timeout.'
			);
			await expect(
				multiFactorAuthenticationConfigurationPage.retryTimeout
			).toHaveAccessibleDescription(
				'Set the time in seconds to block requests after reaching the limit for failed attempts allowed.'
			);
		});
	}
);
