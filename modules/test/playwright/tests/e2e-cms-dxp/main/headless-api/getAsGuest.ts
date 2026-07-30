/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Browser} from '@playwright/test';

// Sends a GET request as an anonymous client, from a fresh browser context with
// an empty storage state. An absolute URL is reduced to its path to keep the
// request same-origin, and redirect: 'manual' keeps an authentication redirect
// from being followed into a 200.

export async function getAsGuest(
	browser: Browser,
	url: string
): Promise<{body: Record<string, unknown> | null; status: number}> {
	const context = await browser.newContext({
		storageState: {cookies: [], origins: []},
	});

	try {
		const page = await context.newPage();

		await page.goto('/');

		const {pathname, search} = new URL(url, page.url());

		return await page.evaluate(async (path) => {
			const response = await fetch(path, {redirect: 'manual'});

			const contentType = response.headers.get('content-type') || '';

			// Only parse a JSON body; a file download or an error page is not
			// JSON, and the caller relies on the status alone in that case.

			const body = contentType.includes('application/json')
				? await response.json()
				: null;

			return {body, status: response.status};
		}, pathname + search);
	}
	finally {
		await context.close();
	}
}
