/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';

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

async function assertCopiedFromPanel(
	page: Page,
	panelTitle: string,
	expectedText: string
) {
	const copyButton = page
		.locator('.panel', {hasText: panelTitle})
		.getByRole('button', {name: 'Copy to Clipboard'});

	await copyButton.click();

	await expect(copyButton).toHaveAttribute(
		'data-clipboard-text',
		new RegExp(expectedText)
	);
}

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
			await assertCopiedFromPanel(
				page,
				'Request String',
				'"explain": true'
			);
		});

		await test.step('Copy the response string and assert its contents', async () => {
			await assertCopiedFromPanel(page, 'Response String', '"_shards"');
		});
	});
});
