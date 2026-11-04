/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {test} from '@playwright/test';

import {LoginScreenName, performLoginViaApi} from '../login';

export interface LoginOptions {
	screenName?: LoginScreenName;
}

export interface Login {
	login: {
		screenName: LoginScreenName;
		sessionId: string;
	};
}

/**
 * This fixture performs a login in the default test page for the user with the given screen name
 * and leaves it in the home page.
 *
 * This fixture needs some pre-cooked users in the test DXP instance. They are created by deploying
 * the `modules/test/playwright-setup` OSGi bundle before Playwright is run.
 *
 * That is automatically done by the CI. In local environments, you need to run
 * `npm run test:setup <name of the test>` before running Playwright to achieve the same results.
 *
 * @param options the screen name to use for performing the login
 *
 * @example
 * export const test = mergeTests(
 *   loginTest('unprivileged'),
 *   ...
 * );
 *
 * test('something', ...);
 */
function loginTest(options: LoginOptions = {}) {
	const fixtureImpl = test.extend<Login>({
		login: [
			async ({page}, use) => {
				const screenName = options.screenName || 'test';

				const cookies = await performLoginViaApi({page, screenName});

				await use({
					screenName,
					sessionId: cookies.find(
						(cookie) => cookie.name === 'JSESSIONID'
					).value,
				});
			},
			{auto: true},
		],
	});

	return fixtureImpl;
}

export {loginTest};
