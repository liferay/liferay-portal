/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import {samplePageTest} from '../../frontend-taglib/main/fixtures/samplePageTest';
import {TabName} from '../../frontend-taglib/main/pages/SamplePage';

export const test = mergeTests(
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	samplePageTest
);

test.beforeEach(async ({samplePage}) => {
	await samplePage.selectTab(TabName.INPUT_LOCALIZED);
});

test(
	'Input localized id and label match',
	{
		tag: '@LPD-42768',
	},
	async ({page}) => {
		await test.step('Check id and label match', async () => {
			await page.locator('.form-control.language-value').waitFor();

			const labelFor = await page
				.getByText('Sample label')
				.getAttribute('for');

			const inputId = await page
				.locator(
					'input[id^="_com_liferay_frontend_taglib_sample_web_portlet_SamplePortlet_INSTANCE_"]'
				)
				.first()
				.getAttribute('id');

			await expect(labelFor).toBe(inputId);
		});
	}
);

test(
	'Input localized works although id includes some non alphanumeric characters',
	{
		tag: '@LPD-56164',
	},
	async ({page}) => {
		await test.step('Check input localized is AUI compatible', async () => {
			const labelFor = await page
				.getByText('Sample label')
				.getAttribute('for');

			await expect(labelFor).toContain('inputLocalizedId_21_');
		});

		await test.step('Check input localized is working by changing language', async () => {
			const languageButton = page.getByRole('button', {
				name: 'Current translation is',
			});

			const languageMenu = page.locator('.lfr-icon-menu-open');

			await clickAndExpectToBeVisible({
				autoClick: false,
				target: languageMenu,
				trigger: languageButton,
			});

			await page.getByRole('menuitem').getByText('ar-SA').click();

			await expect(languageButton).toContainText('ar-SA');
		});
	}
);
