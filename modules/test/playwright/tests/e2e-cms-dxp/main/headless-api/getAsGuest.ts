/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Browser} from '@playwright/test';

// Sends a GET request as an anonymous client (no credentials). The request runs
// inside a fresh browser context with an empty storage state, from the portal
// origin so relative paths resolve. redirect: 'manual' keeps an authentication
// redirect from being silently followed into a 200.

export async function getAsGuest(
	browser: Browser,
	path: string
): Promise<{body: Record<string, unknown> | null; status: number}> {
	const context = await browser.newContext({
		storageState: {cookies: [], origins: []},
	});

	try {
		const page = await context.newPage();

		await page.goto('/');

		return await page.evaluate(async (url) => {
			const response = await fetch(url, {redirect: 'manual'});

			const contentType = response.headers.get('content-type') || '';

			// Only parse a JSON body; a file download or an error page is not
			// JSON, and the caller relies on the status alone in that case.

			const body = contentType.includes('application/json')
				? await response.json()
				: null;

			return {body, status: response.status};
		}, path);
	}
	finally {
		await context.close();
	}
}
