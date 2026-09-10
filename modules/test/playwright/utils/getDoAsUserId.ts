/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page} from '@playwright/test';

export async function getDoAsUserId(page: Page): Promise<string> {
	await page.waitForURL(/doAsUserId=/);

	const doAsUserId = new URL(page.url()).searchParams.get('doAsUserId');

	if (!doAsUserId) {
		throw new Error(`Unable to locate "doAsUserId" in ${page.url()}`);
	}

	return doAsUserId;
}
