/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, test} from '@playwright/test';

import {liferayConfig} from '../../../liferay.config';

test('LPD-32888 Check captcha verification text is cleared after a wrong captcha', async ({
	page,
}) => {
	await page.goto(liferayConfig.environment.baseUrl);
	await page.getByRole('button', {name: 'Sign In'}).click();
	await page.getByText('Forgot Password').click();
	await page.waitForTimeout(1000);
	await page.getByLabel('Email Address').fill('test@liferay.com');
	await page
		.locator(
			'[id="_com_liferay_login_web_portlet_LoginPortlet_captchaText"]'
		)
		.fill('abcd');
	await page.getByRole('button', {name: 'Send new password'}).click();
	await expect(
		page.locator(
			'[id="_com_liferay_login_web_portlet_LoginPortlet_captchaText"]'
		)
	).toBeEmpty();
});
