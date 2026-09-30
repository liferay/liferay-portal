/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {systemSettingsPageTest} from '../../../fixtures/systemSettingsPageTest';
import {WebContentPage} from '../../../pages/journal-web/WebContentPage';
import getRandomString from '../../../utils/getRandomString';
import getBasicWebContentStructureId from '../../../utils/structured-content/getBasicWebContentStructureId';
import {
	clearConsentCookies,
	resetConsentManagerConfiguration,
	updateConsentManagerConfiguration,
} from './utils/consentManagerConfigurationHelper';

export const test = mergeTests(
	dataApiHelpersTest,
	isolatedSiteTest,
	loginTest(),
	systemSettingsPageTest
);

const CONTENT =
	'<h1 id="test">HTML Example</h1>\n' +
	'\n' +
	'<script type="text/plain" data-third-party-cookie="CONSENT_TYPE_FUNCTIONAL">\n' +
	'      document.getElementById(\'test\').style.backgroundColor = "#ff0000"\n' +
	'</script>';

test.afterEach(async ({systemSettingsPage}) => {
	await test.step('Reset Consent Manager Configuration', async () => {
		await resetConsentManagerConfiguration(systemSettingsPage);
	});

	await test.step('Clear Consent Cookies if present', async () => {
		await clearConsentCookies(systemSettingsPage.page);
	});
});

test(
	'Cookie Banner Script',
	{tag: '@LPD-25701'},
	async ({apiHelpers, page, site}) => {
		const title = getRandomString();

		await test.step('Enable Third Party Cookies', async () => {
			await updateConsentManagerConfiguration(page, {
				enabled: true,
				forceReload: true,
			});
		});

		await test.step('Create Web Content with script', async () => {
			await apiHelpers.headlessDelivery.postStructuredContent({
				contentFields: [
					{
						contentFieldValue: {data: CONTENT},
						name: 'content',
					},
				],
				contentStructureId:
					await getBasicWebContentStructureId(apiHelpers),
				datePublished: '2026-01-01T00:00:00Z',
				siteId: site.id,
				title,
			});
		});

		await test.step('Accept all cookies', async () => {
			await page.goto('/');

			await page
				.locator('div[role="dialog"][aria-modal="true"]')
				.waitFor({state: 'visible'});

			const acceptAll = page.getByRole('button', {name: 'Accept All'});

			await acceptAll.waitFor({state: 'visible'});

			await acceptAll.click();
		});

		await test.step('Check script loads in the Web Content preview', async () => {
			const webContentPage = new WebContentPage(page);

			await webContentPage.goto(site.friendlyUrlPath);

			const actionsButton = page.getByRole('button', {
				name: `Actions for ${title}`,
			});

			await actionsButton.waitFor({state: 'visible'});

			await actionsButton.click();

			const previewButton = page.getByRole('menuitem', {name: 'Preview'});

			await previewButton.waitFor({state: 'visible'});

			await previewButton.click();

			const htmlFragment = page
				.frameLocator(`iframe[title="${title}"]`)
				.getByRole('heading', {name: 'HTML Example'});

			await htmlFragment.waitFor({state: 'visible'});

			await expect(htmlFragment).toHaveCSS(
				'background-color',
				'rgb(255, 0, 0)'
			);
		});
	}
);
