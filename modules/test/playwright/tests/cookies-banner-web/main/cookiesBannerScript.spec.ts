/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {loginTest} from '../../../fixtures/loginTest';
import {systemSettingsPageTest} from '../../../fixtures/systemSettingsPageTest';
import getRandomString from '../../../utils/getRandomString';
import {waitForAlert} from '../../../utils/waitForAlert';
import {journalPagesTest} from '../../journal-web/main/fixtures/journalPagesTest';

export const test = mergeTests(
	journalPagesTest,
	loginTest(),
	systemSettingsPageTest
);

test(
	'Cookie Banner Script',
	{tag: '@LPD-25701'},
	async ({journalEditArticlePage, page, systemSettingsPage}) => {
		await test.step('Enable Third Party Cookies', async () => {
			await systemSettingsPage.goToSystemSetting(
				'Cookies',
				'Preference Handling'
			);

			const enabledButton = page.getByLabel('Enabled');

			await enabledButton.waitFor({state: 'visible'});

			const isChecked = await enabledButton.isChecked();

			if (!isChecked) {
				await enabledButton.click();
			}

			await expect(enabledButton).toBeChecked();

			const updateButton = page.getByRole('button', {
				name: 'Update',
			});

			const saveButton = page.getByRole('button', {
				name: 'Save',
			});

			if (await saveButton.isVisible()) {
				await saveButton.click();
			}
			else if (await updateButton.isVisible()) {
				await updateButton.click();
			}

			await waitForAlert(page);
		});

		await test.step('Created Web Content with script and check script loads', async () => {
			await page.goto('/');

			await page
				.locator(
					'#p_p_id_com_liferay_cookies_banner_web_portlet_CookiesBannerPortlet_'
				)
				.waitFor({state: 'visible'});

			const acceptAll = page.getByRole('button', {name: 'Accept All'});

			await acceptAll.waitFor({state: 'visible'});

			await acceptAll.click();

			const openProductButton = page.getByLabel('Open Product Menu');

			if (await openProductButton.isVisible()) {
				await openProductButton.click();
			}

			await journalEditArticlePage.goto();

			const randomTitle = getRandomString();

			await journalEditArticlePage.fillTitle(randomTitle);

			const sourceButton = page.getByLabel('Source', {exact: true});

			await sourceButton.click();

			const sourceEditor = page.locator("textarea[autocorrect='off']");

			await sourceEditor.waitFor({state: 'visible'});

			await sourceEditor.fill(
				'<h1 id="test">HTML Example</h1>\n' +
					'\n' +
					'<script type="text/plain" data-third-party-cookie="CONSENT_TYPE_FUNCTIONAL">\n' +
					'      document.getElementById(\'test\').style.backgroundColor = "#ff0000"\n' +
					'</script>'
			);

			await journalEditArticlePage.publishArticle();

			await waitForAlert(
				page,
				`Success:${randomTitle} was created successfully.`
			);

			const webContentPage = page.getByRole('heading', {
				name: 'Web Content',
			});

			await webContentPage.waitFor({state: 'visible'});

			const editWebContentButton = page.locator(
				`button[aria-label="Actions for ${randomTitle}"]`
			);

			await editWebContentButton.waitFor({state: 'visible'});

			await editWebContentButton.click();

			const previewButton = page.getByRole('menuitem', {name: 'Preview'});

			await previewButton.waitFor({state: 'visible'});

			await previewButton.click();

			const htmlFragment = page
				.frameLocator(`iframe[title="${randomTitle}"]`)
				.getByRole('heading', {name: 'HTML Example'});

			await htmlFragment.waitFor({state: 'visible'});

			await expect(htmlFragment).toHaveCSS(
				'background-color',
				'rgb(255, 0, 0)'
			);
		});
	}
);
