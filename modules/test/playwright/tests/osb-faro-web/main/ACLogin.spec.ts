/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {loginAnalyticsCloudTest} from '../../../fixtures/loginAnalyticsCloudTest';
import {loginTest} from '../../../fixtures/loginTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import {faroConfig} from './faro.config';

const test = mergeTests(loginAnalyticsCloudTest(), loginTest());

test(
	'Can sign out of AC',
	{
		tag: '@LRAC-8534',
	},
	async ({page}) => {

		// Navigate to the AC workspace

		await page.goto(faroConfig.environment.baseUrl);

		// Sign out from the user menu

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {name: 'Sign Out'}),
			trigger: page.locator('.sticker-circle'),
		});

		// Assert that the sign in page is shown

		await expect(page.getByLabel('Email Address')).toBeVisible();
		await expect(page.getByLabel('Password')).toBeVisible();
		await expect(page.getByRole('button', {name: 'Sign In'})).toBeVisible();
	}
);
