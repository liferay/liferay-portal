/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {captchaConfigPageTest} from '../../../fixtures/captchaConfigPageTest';
import {loginTest} from '../../../fixtures/loginTest';
import {liferayConfig} from '../../../liferay.config';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import {performLoginViaApi, performLogout} from '../../../utils/performLogin';

export const test = mergeTests(
	apiHelpersTest,
	captchaConfigPageTest,
	loginTest()
);

test.afterEach('Reset Captcha configuration', async ({captchaConfigPage}) => {
	await captchaConfigPage.goTo();

	await captchaConfigPage.resetCaptchaConfiguration();
});

test('LPD-52234: Check if you can change languages in the update password page', async ({
	apiHelpers,
	page,
}) => {
	const userAccount = await apiHelpers.headlessAdminUser.postUserAccount();

	try {
		await performLogout(page);

		await page.goto(liferayConfig.environment.baseUrl);
		await page.getByRole('button', {name: 'Sign In'}).click();
		await page.getByText('Forgot Password').click();
		await page.getByRole('button', {name: 'Send New Password'}).waitFor();
		await page.getByLabel('Email Address').fill(userAccount.emailAddress);
		await page.getByRole('button', {name: 'Send New Password'}).click();

		await page.waitForLoadState('domcontentloaded');
		await performLoginViaApi({page, screenName: 'test'});

		const ticket =
			await apiHelpers.headlessAdminUser.getUserAccountPasswordResetTicket(
				userAccount.id
			);

		await performLogout(page);

		await page.goto(
			`${liferayConfig.environment.baseUrl}/c/portal/update_password?ticketId=${ticket.id}&ticketKey=${ticket.key}`
		);

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {name: 'português-Brasil'}),
			trigger: page.getByTitle('Select a Language', {exact: true}),
		});

		await page.getByRole('heading', {name: 'Alterar senha'}).waitFor();

		await expect(
			page.getByText(
				'Este formato de link não é mais reconhecido. Solicite um novo link.'
			)
		).not.toBeVisible();
	}
	finally {
		await page.goto('/en');

		await performLoginViaApi({page, screenName: 'test'});

		await apiHelpers.headlessAdminUser.deleteUserAccount(
			Number(userAccount.id)
		);
	}
});
