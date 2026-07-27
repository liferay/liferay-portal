/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {isolatedSiteTest} from '../../../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../../../fixtures/loginTest';
import {journalPagesTest} from '../../../../../journal-web/main/fixtures/journalPagesTest';

const test = mergeTests(isolatedSiteTest, journalPagesTest, loginTest());

test(
	'Input time tag renders an accessible label on the time input',
	{
		tag: '@LPD-99688',
	},
	async ({journalEditArticlePage, page, site}) => {
		await test.step('Open a new Basic Web Content', async () => {
			await journalEditArticlePage.goto({siteUrl: site.friendlyUrlPath});
		});

		await test.step('Check the date-time picker time inputs expose an aria-label', async () => {
			for (const name of ['expirationDateTime', 'reviewDateTime']) {
				await expect(
					page.locator(
						`#_com_liferay_journal_web_portlet_JournalPortlet_${name}`
					)
				).toHaveAttribute('aria-label', 'Time');
			}
		});
	}
);
