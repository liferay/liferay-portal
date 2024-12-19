/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, expect, mergeTests} from '@playwright/test';

import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {samplePageTest} from '../../fixtures/samplePageTest';

export const test = mergeTests(
	featureFlagsTest({
		'LPS-178052': true,
	}),
	isolatedSiteTest,
	loginTest(),
	samplePageTest
);

const linkName = 'Logo Selector';

test(
	'Logo selector changes do not affect to every selector in the page',
	{tag: '@LPD-39308'},
	async ({page, site, samplePage}) => {

		await test.step('Create a content site and the taglib sample widget', async () => {
			await samplePage.setupSampleWidget({
				site,
			});
		});

		await test.step('Select Panel link', async () => {
			await samplePage.selectLink(linkName);
		});

		await test.step('Open modal to change first logo selector and fire change event', async () => {
            await page.getByLabel('Change First Logo').click();

            await page.evaluate(() => {
                Liferay.fire('changeLogo', {tempImageFileName: 'New Logo Name'})
            });

            await page.frameLocator('iframe[title="Upload First Logo"]').getByRole('button', { name: 'Done' }).click();
               
		});

		await test.step('Check second logo selector has not been changed', async () => {
            const secondInput = page
                .locator('div')
                .filter({ hasText: /^Second Logo$/ })
                .locator('[id^="_com_liferay_frontend_taglib_sample_web_portlet_SamplePortlet_INSTANCE_"]');
            
            await expect(secondInput).toHaveValue('Default');
		});
	}
);
