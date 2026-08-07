/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page} from '@playwright/test';

/**
 * Navigates like page.goto, surviving a navigation left pending by an earlier
 * step. An action whose success signal renders before its navigation commits,
 * like saving an object layout, leaves that navigation in flight, and when it
 * lands during a later goto the browser reports the goto as aborted or
 * interrupted even though nothing is wrong with the address. Let the pending
 * navigation land and go again: unlike settling for whatever page won, the
 * retry ends on the address the caller asked for.
 */
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
