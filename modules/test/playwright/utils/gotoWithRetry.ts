/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page} from '@playwright/test';

export async function gotoWithRetry(
	page: Page,
	url: string,
	options?: Parameters<Page['goto']>[1]
) {
	try {
		return await page.goto(url, options);
	}
	catch (error) {
		const message = String(error.message);

		if (
			!message.includes('interrupted by another navigation') &&
			!message.includes('net::ERR_ABORTED')
		) {
			throw error;
		}

		await page.waitForLoadState();

		return await page.goto(url, options);
	}
}
