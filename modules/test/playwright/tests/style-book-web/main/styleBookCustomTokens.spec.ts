/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {styleBookPageTest} from '../../../fixtures/styleBookPageTest';
import getRandomString from '../../../utils/getRandomString';

const CATEGORY_NAME = 'Color System';

const test = mergeTests(isolatedSiteTest, loginTest(), styleBookPageTest);

test.beforeEach(async ({site, styleBooksPage}) => {
	await styleBooksPage.goto(site.friendlyUrlPath);

	await styleBooksPage.create(getRandomString());
});

test(
	'Creates a custom frontend token and sees it rendered in the sidebar',
	{tag: '@LPD-83061'},
	async ({page, styleBooksPage}) => {
		test.slow();

		const customTokenBadge = page.getByRole('img', {
			name: 'Style Book Custom Token',
		});

		await test.step('Assert there are no custom tokens in the style book editor', async () => {
			await styleBooksPage.selectTokenCategory(CATEGORY_NAME);

			await expect(customTokenBadge).toBeHidden();
		});

		const defaultValue = getRandomString();
		const tokenName = getRandomString();
		const tokenInput = page.getByRole('textbox', {
			exact: true,
			name: tokenName,
		});

		await test.step('Create a custom token', async () => {
			await styleBooksPage.createCustomToken({
				tokenName,
				value: defaultValue,
			});

			await expect(customTokenBadge).toBeVisible();
			await expect(tokenInput).toBeVisible();
			await expect(tokenInput).toHaveValue(defaultValue);
		});

		await test.step("Edit a theme token and assert the custom token's value does not change", async () => {
			await styleBooksPage.updateTokenInputColor(
				'Brand Color 1',
				'#FF0000',
				'Brand Colors'
			);

			await styleBooksPage.waitForAutoSave();

			await styleBooksPage.reloadEditor();

			await styleBooksPage.selectTokenCategory(CATEGORY_NAME);

			await expect(customTokenBadge).toBeVisible();
			await expect(tokenInput).toHaveValue(defaultValue);
		});

		await test.step("Edit the custom token's value and verify it persists", async () => {
			const editedValue = getRandomString();

			await styleBooksPage.updateTokenInput(tokenName, editedValue);

			await styleBooksPage.waitForAutoSave();

			await styleBooksPage.reloadEditor();

			await styleBooksPage.selectTokenCategory(CATEGORY_NAME);

			await expect(customTokenBadge).toBeVisible();
			await expect(tokenInput).toHaveValue(editedValue);
		});
	}
);

test(
	'Creates a custom frontend token in a new token set',
	{tag: '@LPD-83061'},
	async ({page, styleBooksPage}) => {
		const tokenSetName = getRandomString();

		const tokenSet = page.getByRole('button', {
			exact: true,
			name: tokenSetName,
		});

		await test.step('Assert the new custom token set does not exist yet', async () => {
			await styleBooksPage.selectTokenCategory(CATEGORY_NAME);

			await expect(tokenSet).toBeHidden();
		});

		const tokenName = getRandomString();
		const value = getRandomString();

		await test.step('Create a custom token in a new token set', async () => {
			await styleBooksPage.createCustomToken({
				tokenName,
				tokenSetName,
				value,
			});

			await expect(tokenSet).toBeVisible();
		});

		await test.step('Assert the new token renders inside the new token set', async () => {
			await styleBooksPage.reloadEditor();

			await styleBooksPage.selectTokenCategory(CATEGORY_NAME);

			const tokenInput = page.getByRole('textbox', {
				exact: true,
				name: tokenName,
			});

			await tokenSet.click();

			await expect(tokenInput).toHaveValue(value);
		});
	}
);
