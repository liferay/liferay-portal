/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect} from '@playwright/test';

import {
	clearAuthToken,
	getHeader,
	readAuthToken,
} from '../../../../helpers/ApiHelpers';
import {faroConfig} from '../faro.config';

export async function signInToAnalyticsCloud(
	page: Page,
	login: string,
	password: string = faroConfig.user.password
) {

	// Logout any existing session so the new login fully replaces it.

	await page.goto(`${faroConfig.environment.baseUrl}/c/portal/logout`);

	clearAuthToken(page);

	const response = await page.request.post(
		`${faroConfig.environment.baseUrl}/c/portal/login`,
		{
			data: new URLSearchParams({
				login,
				password,
			}).toString(),
			headers: await getHeader(page, 'application/x-www-form-urlencoded'),
		}
	);

	expect(response.status()).toBe(200);

	// Land on the workspace so the SPA picks up the new session.

	await page.goto(faroConfig.environment.baseUrl);

	await expect(
		page.getByRole('textbox', {name: 'Email Address'})
	).toHaveCount(0);

	await readAuthToken(page);
}
