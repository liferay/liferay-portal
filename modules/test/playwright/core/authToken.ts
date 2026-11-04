/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {BrowserContext, Page} from '@playwright/test';

export type ContentType =
	| 'application/json'
	| 'application/x-www-form-urlencoded';

const authTokens = new WeakMap<BrowserContext, string>();

export function clearAuthToken(page: Page) {
	authTokens.delete(page.context());
}

export async function getCSRFTokenHeader(page: Page) {
	let authToken = authTokens.get(page.context());

	if (authToken === undefined) {
		authToken = await readAuthToken(page);
	}

	return {
		'x-csrf-token': authToken,
	};
}

export async function getHeader(
	page: Page,
	contentType: ContentType = 'application/json'
) {
	return {
		'Content-Type': contentType,
		...(await getCSRFTokenHeader(page)),
	};
}

export async function readAuthToken(page: Page) {
	const authToken = await page.evaluate(() => Liferay.authToken);

	authTokens.set(page.context(), authToken);

	return authToken;
}
