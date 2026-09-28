/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {loginTest} from '../../../fixtures/loginTest';
import {performLoginViaApi, performLogout} from '../../../utils/performLogin';

export const test = mergeTests(loginTest());

test(
	'Check session expired message is shown when the tab wakes up after the session expired',
	{tag: '@LPD-107298'},
	async ({page}) => {
		await page.clock.install();

		await performLogout(page);
		await performLoginViaApi({page, rememberMe: false, screenName: 'test'});

		await page.waitForFunction(() => (window as any).Liferay.Session);

		const sessionLength = await page.evaluate(
			() => (window as any).Liferay.Session.sessionLength
		);

		await page.clock.fastForward(sessionLength + 1000);

		await expect(
			page.getByText('Due to inactivity, your session has expired')
		).toBeVisible();
		await expect(
			page.getByText('Due to inactivity, your session will expire')
		).toBeHidden();
	}
);
