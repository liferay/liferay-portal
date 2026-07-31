/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Browser} from '@playwright/test';

// Sends a GET request authenticated with a Basic Authorization header, from a
// fresh browser context with an empty storage state, so the credentials are
// the only authentication in play (no portal session cookie). An absolute URL
// is reduced to its path to keep the request same-origin, and
// redirect: 'manual' keeps an authentication redirect from being followed
// into a 200.

export async function getWithBasicAuth(
	browser: Browser,
	url: string,
	emailAddress: string,
	password: string = 'test'
): Promise<{body: Record<string, unknown> | null; status: number}> {
	const context = await browser.newContext({
		storageState: {cookies: [], origins: []},
	});

	try {
		const page = await context.newPage();

		await page.goto('/');

		const {pathname, search} = new URL(url, page.url());

		return await page.evaluate(
			async ({credentials, path}) => {
				const response = await fetch(path, {
					headers: {
						Authorization: `Basic ${btoa(credentials)}`,
					},
					redirect: 'manual',
				});

				const contentType = response.headers.get('content-type') || '';

				const body = contentType.includes('application/json')
					? await response.json()
					: null;

				return {body, status: response.status};
			},
			{
				credentials: `${emailAddress}:${password}`,
				path: pathname + search,
			}
		);
	}
	finally {
		await context.close();
	}
}
