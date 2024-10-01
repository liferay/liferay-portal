/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {isolatedLayoutTest} from '../../fixtures/isolatedLayoutTest';
import {loginTest} from '../../fixtures/loginTest';
import {systemSettingsPageTest} from '../../fixtures/systemSettingsPageTest';
import {waitForAlert} from '../../utils/waitForAlert';

export const test = mergeTests(
	isolatedLayoutTest(),
	loginTest(),
	systemSettingsPageTest
);

test('@LPD-25701 Cookie Banner Script', async ({
	layout,
	page,
	systemSettingsPage,
}) => {
	await test.step('Go to page and click edit', async () => {
		await page.goto(layout.friendlyURL);

		const editButton = page.getByRole('link', {name: 'Edit'});

		await editButton.waitFor({state: 'visible'});
		await editButton.click();
	});

	await test.step('Drag and drop html to page', async () => {
		const searchFragment = page.getByLabel('Search Fragments and Widgets');

		await searchFragment.waitFor({state: 'visible'});
		await searchFragment.click();
		await searchFragment.fill('html');

		const htmlItem = page.getByRole('menuitem', {
			name: 'HTML Add HTML Mark HTML as Favorite',
		});

		await htmlItem.waitFor({state: 'visible'});

		await htmlItem.dragTo(page.locator('#page-editor div').nth(1));
	});

	await test.step('Add script to html and save page', async () => {
		const htmlExample = page.getByText('HTML Example');

		await htmlExample.waitFor({state: 'visible'});
		await htmlExample.click();
		await htmlExample.click();
		await htmlExample.click();

		const htmlSection = page.locator('.CodeMirror-scroll');

		await htmlSection.waitFor({state: 'visible'});
		await htmlSection.click();

		const textarea = page.locator('textarea');

		await textarea.press('Control+a');
		await textarea.press('Backspace');
		await textarea.fill(
			'<h1 id="test">HTML Example</h1>\n' +
				'\n' +
				'<script type="text/plain" data-third-party-cookie="CONSENT_TYPE_FUNCTIONAL">\n' +
				'      document.getElementById(\'test\').style.backgroundColor = "#ff0000"\n' +
				'</script>'
		);

		await page
			.getByRole('button', {
				name: 'Save',
			})
			.click();

		await page.getByLabel('Publish', {exact: true}).click();

		await waitForAlert(
			page,
			`Success:The page was published successfully.`
		);
	});

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

	await test.step('Accept Cookies', async () => {
		await page.goto(layout.friendlyURL);

		await page.getByRole('heading', {name: 'HTML Example'}).waitFor();

		await page
			.locator(
				'#p_p_id_com_liferay_cookies_banner_web_portlet_CookiesBannerPortlet_'
			)
			.waitFor({state: 'visible'});

		const acceptAll = page.getByRole('button', {name: 'Accept All'});

		await acceptAll.waitFor({state: 'visible'});

		await acceptAll.click();
	});

	await test.step('Check if script changed html', async () => {
		await page.reload();

		const htmlFragment = page.getByRole('heading', {
			name: 'HTML Example',
		});

		await htmlFragment.waitFor();

		await expect(htmlFragment).toHaveCSS(
			'background-color',
			'rgb(255, 0, 0)'
		);
	});
});
