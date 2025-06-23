/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../../../fixtures/loginTest';
import {masterPagesPagesTest} from '../../../../../../fixtures/masterPagesPagesTest';
import {pageEditorPagesTest} from '../../../../../../fixtures/pageEditorPagesTest';
import {pageSelectorPagesTest} from '../../../../../../fixtures/pageSelectorPagesTest';
import {pagesAdminPagesTest} from '../../../../../../fixtures/pagesAdminPagesTest';
import {systemSettingsPageTest} from '../../../../../../fixtures/systemSettingsPageTest';
import getRandomString from '../../../../../../utils/getRandomString';
import {waitForAlert} from '../../../../../../utils/waitForAlert';

const test = mergeTests(
	apiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	masterPagesPagesTest,
	pageEditorPagesTest,
	pageSelectorPagesTest,
	pagesAdminPagesTest,
	systemSettingsPageTest
);

test(
	'Check JS injection works correctly',
	{tag: '@LPD-37109'},
	async ({apiHelpers, page, pageEditorPage, site}) => {
		const layout = await apiHelpers.headlessDelivery.createSitePage({
			siteId: site.id,
			title: getRandomString(),
		});

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await pageEditorPage.goToSidebarTab('Page Design Options');

		await page
			.getByTitle('More Page Design Options', {exact: true})
			.click();

		await page.getByRole('tab', {name: 'JavaScript'}).click();

		const sampleJS =
			'window.onload=function(){console.log("customjssnippets")}';

		await page.getByPlaceholder('JavaScript').fill(sampleJS);

		await page.getByRole('button', {name: 'Save'}).click();

		await waitForAlert(page, 'Success:The page was updated successfully.');

		await page.goto(
			`/web${site.friendlyUrlPath}/${layout.friendlyUrlPath}`
		);

		const editButton = page.getByRole('link', {name: 'Edit'});

		await editButton.waitFor({state: 'visible'});

		await editButton.click();

		const publishButton = page.getByRole('button', {name: 'Publish'});

		await publishButton.waitFor({state: 'visible'});

		await publishButton.click();

		await waitForAlert(
			page,
			'Success:The page was published successfully.'
		);

		await page.reload();

		await page.locator('#content').waitFor({state: 'visible'});

		const jsSnippetIIFE = await page.evaluate((sampleJS) => {
			let containsIIFEFunction = false;

			const scriptElements = document.getElementsByTagName('script');

			const injectedSampleJS = `(function(){${sampleJS}})`;

			for (let i = 0; i < scriptElements.length; i++) {
				const scriptText = scriptElements[i].textContent;

				const oneLine = scriptText.replace(/\s+/g, '');

				if (oneLine.includes(injectedSampleJS)) {
					containsIIFEFunction = true;
					break;
				}
			}

			return containsIIFEFunction;
		}, sampleJS);

		expect(jsSnippetIIFE).toEqual(true);
	}
);

test(
	'Check URL does not allow XSS injection',
	{tag: '@LPD-57867'},
	async ({page}) => {
		let capturedLogMessage = '';

		page.on('console', (msg) => {
			if (msg.type() === 'log') {
				capturedLogMessage = msg.text();
			}
		});

		await page.goto(`/?snippet=console.log("${getRandomString()}")`);

		expect(capturedLogMessage).toBe('');
	}
);
