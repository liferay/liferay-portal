/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {loginTest} from '../../../fixtures/loginTest';
import getRandomString from '../../../utils/getRandomString';
import {cmsPagesTest} from '../../site-cms-site-initializer/main/fixtures/cmsPagesTest';

const test = mergeTests(apiHelpersTest, cmsPagesTest, loginTest());

test('CMS loads and sections are shown', async ({
	contentsPage,
	homePage,
	page,
}) => {

	// Go to CMS home page

	await homePage.goto();

	// Check main elements are shown

	await expect(page.getByLabel('CMS Control Menu')).toBeVisible();

	await expect(page.getByLabel('CMS Product Menu')).toBeVisible();

	// Create a basic web content and check it works

	await expect(async () => {
		await contentsPage.goto();

		await expect(contentsPage.newButton).toBeVisible({timeout: 2000});
	}).toPass();

	await contentsPage.createContent('Basic Web Content');

	const title = getRandomString();

	await contentsPage.fillData([{label: 'Title', value: title}]);

	await contentsPage.saveContent();

	await contentsPage.goto();

	await expect(page.getByRole('link', {name: title})).toBeVisible();

	// Delete the content

	await contentsPage.deleteContent(title);
});
