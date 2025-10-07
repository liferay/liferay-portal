/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../../fixtures/loginTest';
import {waitForEditor, waitForFDS} from '../../../../../utils/waitFor';
import {ckeditorSamplePageTest} from './../../fixtures/ckeditorSamplePageTest';
import {classicPageTest} from './fixtures/classicPageTest';

export const test = mergeTests(
	apiHelpersTest,
	ckeditorSamplePageTest,
	classicPageTest,
	featureFlagsTest({
		'LPD-11235': {enabled: true},
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest()
);

test.beforeEach(async ({ckeditorSamplePage, page, site}) => {
	await ckeditorSamplePage.createAndGotoSitePage({site});

	await ckeditorSamplePage.selectTab('CKEditor 5');
	await ckeditorSamplePage.selectTab('React');

	await waitForEditor({page});
});

test(
	'Editor configuration is applied',
	{tag: '@LPD-11235'},
	async ({classicPage, page}) => {
		await test.step('Initial data is set', async () => {
			await expect(
				classicPage.editable.getByText('Lorem ipsum dolor sit amet')
			).toBeVisible();
		});

		await test.step('Toolbar contains custom toobar configuration, including added custom and official plugins', async () => {
			const expectedButtons = [
				'Undo',
				'Redo',
				'Bold',
				'Italic',
				'Bookmark',
				'Timestamp',
				'Image',
				'Video',
			];

			const availableButtons =
				await classicPage.toolbar.buttonLabels.allInnerTexts();

			expect(availableButtons).toEqual(expectedButtons);
		});

		await test.step('Toolbar does not contain removed plugin', async () => {
			await expect(
				classicPage.toolbar.buttonLabels.getByLabel('Underline')
			).toBeHidden();
		});

		await test.step('"Timestamp" custom plugin has Clay icon', async () => {
			const timestampButton = classicPage.toolbar.container.getByRole(
				'button',
				{
					name: 'Timestamp',
				}
			);

			await expect(
				timestampButton.locator('svg use[href*="/clay/"]')
			).toBeAttached();
		});

		await test.step('Item selector controls open item selector modal', async () => {
			const imageButton = classicPage.toolbar.container.getByRole(
				'button',
				{
					name: 'Image',
				}
			);

			await imageButton.click();

			await expect(page.getByText('Select Image')).toBeVisible();

			await waitForFDS({empty: true, page});

			const modal = page.locator('.modal');

			const cancelButton = modal.getByRole('button', {
				name: 'Cancel',
			});

			await cancelButton.click();

			await expect(page.getByText('Select Image')).not.toBeAttached();

			const videoButton = classicPage.toolbar.container.getByRole(
				'button',
				{
					name: 'Video',
				}
			);

			await videoButton.click();

			await expect(page.getByText('Select Video')).toBeVisible();

			await cancelButton.click();

			await expect(page.getByText('Select Video')).not.toBeAttached();
		});
	}
);
