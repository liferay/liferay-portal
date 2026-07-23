/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {loginTest} from '../../../fixtures/loginTest';

const test = mergeTests(loginTest());

test(
	'redirects a signed in user from the guest portal layout to the CMS',
	{tag: '@LPD-96232'},
	async ({page}) => {
		await page.goto('/c/portal/layout');

		await expect(page).toHaveURL(/\/web\/cms(\/|$)/);
	}
);
