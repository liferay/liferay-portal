/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect} from '@playwright/test';

export default async function isSPAEnabled({page}: {page: Page}) {
	await expect(page.locator('.lfr-spa-loading')).not.toBeInViewport();

	const liferaySPAOutput = await page.evaluate(() => Liferay.SPA);

	return liferaySPAOutput !== undefined;
}
