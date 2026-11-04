/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Cookie, Page, expect} from '@playwright/test';

import {
	clearAuthToken,
	getHeader,
	readAuthToken,
} from '../core/authToken';
import {performLogin} from '../core/login';
import {faroConfig} from '../tests/osb-faro-web/main/faro.config';

export {
	performLoginViaApi,
	performLogout,
	performUserSwitch,
	performUserSwitchViaApi,
	userData,
} from '../core/login';
export type {LoginScreenName} from '../core/login';

export async function performAnalyticsCloudLoginViaApi(
	page: Page
): Promise<Cookie[]> {
	const loginUrl = faroConfig.environment.baseUrl;

	const params = new URLSearchParams({
		login: faroConfig.user.login,
		password: faroConfig.user.password,
		rememberMe: 'true',
	});

	try {
		await page.goto(loginUrl);

		clearAuthToken(page);

		const url = `${loginUrl}/c/portal/login`;

		await expect
			.poll(async () => {
				const response = await page.request.post(url, {
					data: params.toString(),
					headers: await getHeader(
						page,
						'application/x-www-form-urlencoded'
					),
				});

				return response.status();
			})
			.toBe(200);

		await page.goto(loginUrl);

		await readAuthToken(page);
	}
	catch (error) {
		error.message = `Analytics Cloud login via API failed\n\n${error.message}`;

		throw error;
	}

	return await page.context().cookies();
}

export default performLogin;
