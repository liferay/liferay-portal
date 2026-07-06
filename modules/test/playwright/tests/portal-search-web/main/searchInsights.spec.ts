/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {isolatedLayoutTest} from '../../../fixtures/isolatedLayoutTest';
import {loginTest} from '../../../fixtures/loginTest';
import {searchPageTest} from '../../../fixtures/searchPageTest';

export const test = mergeTests(
	dataApiHelpersTest,
	isolatedLayoutTest({type: 'portlet'}),
	loginTest(),
	searchPageTest
);

test.use({permissions: ['clipboard-read', 'clipboard-write']});

test.describe('Copy to Clipboard', () => {
	test('Copies the request and response strings to the clipboard', async ({
		layout,
		page,
		searchPage,
	}) => {
		await test.step('Add the Search Bar and Search Insights widgets to the page', async () => {
			await page.goto('/web/guest' + layout.friendlyURL);

			await searchPage.addPortlet('Search Bar', 'Search');

			await searchPage.addPortlet('Search Insights', 'Search');
		});

		await test.step('Perform a search to populate the insights panels', async () => {
			await searchPage.searchKeywordInMainContent('test');
		});

		await test.step('Copy the request string and assert its contents', async () => {
			await page
				.locator('.panel', {hasText: 'Request String'})
				.getByRole('button', {name: 'Copy to Clipboard'})
				.click();

			await expect(async () => {
				expect(
					await page.evaluate(() => navigator.clipboard.readText())
				).toContain('"explain": true');
			}).toPass({timeout: 10000});
		});

		await test.step('Copy the response string and assert its contents', async () => {
			await page
				.locator('.panel', {hasText: 'Response String'})
				.getByRole('button', {name: 'Copy to Clipboard'})
				.click();

			await expect(async () => {
				expect(
					await page.evaluate(() => navigator.clipboard.readText())
				).toContain('"_shards"');
			}).toPass({timeout: 10000});
		});
	});
});
