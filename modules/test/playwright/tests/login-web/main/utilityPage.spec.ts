/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {liferayConfig} from '../../../liferay.config';
import {getRandomInt} from '../../../utils/getRandomInt';
import performLogin, {performLogout} from '../../../utils/performLogin';
import {utilityPagesPage} from './fixtures/utilityPageTest';

export const test = mergeTests(
	featureFlagsTest({
		'LPD-6378': {enabled: true},
	}),
	utilityPagesPage
);

const getRandomTitle = () => {
	return 'test-up-' + getRandomInt();
};

test('LPD-6869 Render the default "Create Account" utility page if exists', async ({
	page,
	utilityPagesPage,
}) => {
	await performLogin(page, 'test');

	await utilityPagesPage.goto();

	const title = getRandomTitle();

	await utilityPagesPage.add(title, 'Create Account');
	await expect(page.getByText(title)).toBeVisible();
	await utilityPagesPage.markAsDefault(title);

	await performLogout(page);

	await expect(page.getByPlaceholder('Search')).toBeVisible();
	await page.getByRole('button', {name: 'Sign In'}).click();
	await page.getByRole('link', {name: 'Create Account'}).click();
	await expect(page).toHaveTitle(title + ' - Liferay DXP');

	await performLogin(page, 'test');

	await utilityPagesPage.goto();
	await utilityPagesPage.deletePage(title);

	await performLogout(page);
});

test('LPD-6869 Render the original "Create Account" view if no default utility page exists', async ({
	page,
}) => {
	await page.goto(liferayConfig.environment.baseUrl);
	await page.getByRole('button', {name: 'Sign In'}).click();
	await page.getByText('Create Account').click();
	await expect(page).toHaveTitle('Home - Liferay DXP');
});

test('LPD-6870 Render the default "Sign In" utility page if exists', async ({
	loginInstanceSettingsPage,
	page,
	utilityPagesPage,
}) => {
	await performLogin(page, 'test');

	await loginInstanceSettingsPage.goto();
	await loginInstanceSettingsPage.enableLoginPrompt();

	await utilityPagesPage.goto();

	const title = getRandomTitle();

	await utilityPagesPage.add(title, 'Sign In');
	await expect(page.getByText(title)).toBeVisible();
	await utilityPagesPage.markAsDefault(title);

	await performLogout(page);

	await utilityPagesPage.goto();

	await performLogin(page, 'test');

	await utilityPagesPage.goto();
	await utilityPagesPage.deletePage(title);

	await loginInstanceSettingsPage.goto();
	await loginInstanceSettingsPage.resetLoginPrompt();

	await performLogout(page);
});

test('LPD-6870 Render the original "Sign In" view if no default utility page exists', async ({
	loginInstanceSettingsPage,
	page,
	utilityPagesPage,
}) => {
	await performLogin(page, 'test');

	await loginInstanceSettingsPage.goto();
	await loginInstanceSettingsPage.enableLoginPrompt();

	await performLogout(page);

	await utilityPagesPage.goto();

	await expect(page).toHaveTitle('Home - Liferay DXP');
	await expect(page.getByLabel('Sign In')).toBeVisible();

	await performLogin(page, 'test');

	await loginInstanceSettingsPage.goto();
	await loginInstanceSettingsPage.resetLoginPrompt();

	await performLogout(page);
});

test('LPD-6871 Render the default "Forgot Password" utility page if exists', async ({
	page,
	utilityPagesPage,
}) => {
	await performLogin(page, 'test');

	await utilityPagesPage.goto();

	const title = getRandomTitle();

	await utilityPagesPage.add(title, 'Forgot Password');
	await expect(page.getByText(title)).toBeVisible();
	await utilityPagesPage.markAsDefault(title);

	await performLogout(page);

	await expect(page.getByPlaceholder('Search')).toBeVisible();
	await page.getByRole('button', {name: 'Sign In'}).click();
	await page.getByRole('menuitem', {name: 'Forgot Password'}).click();
	await expect(page).toHaveTitle(title + ' - Liferay DXP');

	await performLogin(page, 'test');

	await utilityPagesPage.goto();
	await utilityPagesPage.deletePage(title);

	await performLogout(page);
});

test('LPD-6871 Render the original "Forgot Password" view if no default utility page exists', async ({
	page,
}) => {
	await page.goto(liferayConfig.environment.baseUrl);
	await page.getByRole('button', {name: 'Sign In'}).click();
	await page.getByText('Forgot Password').click();
	await expect(page).toHaveTitle('Home - Liferay DXP');
});
