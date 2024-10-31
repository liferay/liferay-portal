/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../fixtures/apiHelpersTest';
import {isolatedSiteTest} from '../../fixtures/isolatedSiteTest';
import {loginTest} from '../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../fixtures/pageEditorPagesTest';
import {pageViewModePagesTest} from '../../fixtures/pageViewModePagesTest';
import {productMenuPageTest} from '../../fixtures/productMenuPageTest';
import getRandomString from '../../utils/getRandomString';
import {journalPagesTest} from '../journal-web/fixtures/journalPagesTest';
import isSPAEnabled from './utils/isSPAEnabled';

export const test = mergeTests(
	loginTest(),
	apiHelpersTest,
	isolatedSiteTest,
	journalPagesTest,
	pageEditorPagesTest,
	productMenuPageTest,
	pageViewModePagesTest
);

test(
	'Assert form submit works correctly on Safari browser',
	{
		tag: '@LPD-26285',
	},
	async ({
		apiHelpers,
		journalEditArticlePage,
		page,
		site,
		widgetPagePage,
	}) => {
		const {devices, webkit} = require('playwright');
		const safari = devices['Desktop Safari'];
		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			options: {
				type: 'portlet',
			},
			title: getRandomString(),
		});

		await test.step('Naviagate to root page', async () => {
			await page.goto('/');
		});

		await test.step('Asset SPA is enabled', async () => {
			expect(isSPAEnabled({page})).toBeTruthy();
		});

		await test.step('Add WCD and content with a form including two submit buttons', async () => {
			await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

			await widgetPagePage.addPortlet('Web Content Display');

			await page
				.locator(
					'[id^="_com_liferay_journal_content_web_portlet_JournalContentPortlet_INSTANCE_"]'
				)
				.and(page.getByRole('button'))
				.click();

			await page
				.getByRole('menuitem', {name: 'Basic Web Content'})
				.click();

			const title = getRandomString();

			await journalEditArticlePage.fillTitle(title);

			await page.getByLabel('Source', {exact: true}).click();

			await page.locator('.CodeMirror-scroll').click();

			const content =
				'<form action="" method="post"><input type="submit" value="Button A" /> <input type="submit" value="Button B" /></form><p><span id="capturedFormButtonElement"></span></p><script>Liferay.once("beforeNavigate", function(){buttonValue = Liferay.SPA.__capturedFormButtonElement__.value});Liferay.once("endNavigate", function(){document.getElementById("capturedFormButtonElement").textContent = buttonValue;})</script>';

			await page
				.getByLabel('Content', {exact: true})
				.getByRole('textbox')
				.fill(content);

			await journalEditArticlePage.publishArticle();
		});

		await test.step('Set Safari as the browser and check submit buttons in form', async () => {
			const browser = await webkit.launch();

			const context = await browser.newContext({
				...safari,
			});

			const incognitoPage = await context.newPage();

			await incognitoPage.goto(
				`/web${site.friendlyUrlPath}${layout.friendlyURL}`
			);

			await incognitoPage.getByRole('button', {name: 'Button B'}).click();

			const capturedFormButtonElement = incognitoPage.locator(
				'#capturedFormButtonElement'
			);

			await expect(capturedFormButtonElement).toHaveText('Button B');

			// Dispose context once it's no longer needed.

			await context.close();
		});
	}
);
