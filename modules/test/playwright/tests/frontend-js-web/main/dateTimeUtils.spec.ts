/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {samplePageTest} from './fixtures/samplePageTest';

export const test = mergeTests(
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	samplePageTest
);

const linkName = 'Clay Date Picker';

test(
	'Date Picker is using right translations in Basque locale',
	{tag: '@LPD-64999'},
	async ({page, samplePage, site}) => {
		await test.step('Create a content site showing it in Basque and add the taglib sample widget', async () => {
			const locale = 'eu-ES';
			await samplePage.setupSampleWidget({
				locale,
				site,
			});
		});

		await test.step('Select Panel link', async () => {
			await samplePage.selectLink(linkName);

			await expect(
				samplePage.page.getByPlaceholder('YYYY-MM-DD HH:mm')
			).toBeVisible();
		});

		await test.step('Open Date Picker and assert weekdays are shown in Basque', async () => {
			await page.getByRole('button', {name: 'Aukeratu data'}).click();

			const datePickerDialog = page.getByRole('dialog', {
				name: 'Aukeratu data',
			});
			await expect(datePickerDialog).toBeVisible();

			await expect(
				datePickerDialog.getByText('al', {exact: true})
			).toBeVisible();
			await expect(
				datePickerDialog.getByText('ar', {exact: true})
			).toBeVisible();
			await expect(
				datePickerDialog.getByText('az', {exact: true})
			).toBeVisible();
			await expect(
				datePickerDialog.getByText('og', {exact: true})
			).toBeVisible();
			await expect(
				datePickerDialog.getByText('or', {exact: true})
			).toBeVisible();
			await expect(
				datePickerDialog.getByText('lr', {exact: true})
			).toBeVisible();
			await expect(
				datePickerDialog.getByText('ig', {exact: true})
			).toBeVisible();
		});
	}
);
