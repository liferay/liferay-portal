/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, test} from '@playwright/test';

import {liferayConfig} from '../../../liferay.config';
import getRandomString from '../../../utils/getRandomString';
import {userData} from '../../../utils/performLogin';

test('LPD-38816 Checking the user has to change the password if the password policy is enabled', async ({
	page,
}) => {
	await page.goto(liferayConfig.environment.baseUrl);

	const screenName = 'test';

	await page.getByRole('button', {name: 'Sign In'}).last().click();

	await page.getByLabel('Email Address').fill(screenName + '@liferay.com');

	await page.getByLabel('Password').fill(userData[screenName].password);

	await page.getByRole('button', {name: 'Sign In'}).last().click();

	await expect(
		page.getByRole('heading', {name: 'Change Password'})
	).toBeVisible({
		timeout: 10 * 1000,
	});
});

test('LPD-39929 Do not ask user to change password when the user set the password', async ({
	page,
}) => {
	await page.goto(liferayConfig.environment.baseUrl);

	await page.getByRole('button', {name: 'Sign In'}).click();

	await page.getByText('Create Account').click();

	await page.getByLabel('Screen Name').fill(getRandomString());

	const email = getRandomString();

	await page.getByLabel('Email Address').fill(email + '@liferay.com');

	await page.getByLabel('First Name').fill(getRandomString());

	await page.getByLabel('Last Name').fill(getRandomString());

	const password = getRandomString();

	await page.getByLabel('Password Required', {exact: true}).fill(password);

	await page.getByLabel('Reenter Password Required').fill(password);

	await page.getByRole('button', {name: 'Save'}).click();

	await expect(
		page.getByText('Thank you for creating an account.')
	).toBeVisible();

	await page.getByLabel('Password').fill(password);

	await page.getByRole('button', {name: 'Sign In'}).click();

	await expect(page.getByText('Welcome to Liferay')).toBeVisible();
});
