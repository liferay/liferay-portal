/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, test} from '@playwright/test';

import {CMSLoginPage} from './pages/CMSLoginPage';

test.describe('Standalone CMS login page', () => {
	let cmsLoginPage: CMSLoginPage;

	test.beforeEach(async ({page}) => {
		cmsLoginPage = new CMSLoginPage(page);

		await cmsLoginPage.goto();
	});

	test(
		'shows the CMS welcome message and the sign in form',
		{tag: '@LPD-95488'},
		async () => {
			await expect(cmsLoginPage.welcomeHeading).toBeVisible();

			await expect(cmsLoginPage.emailAddressInput).toBeVisible();
			await expect(cmsLoginPage.passwordInput).toBeVisible();
		}
	);
});
