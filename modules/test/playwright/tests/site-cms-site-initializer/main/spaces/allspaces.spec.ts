/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../../fixtures/loginTest';
import getRandomString from '../../../../utils/getRandomString';
import {PORTLET_URLS} from '../../../../utils/portletUrls';
import {waitForAlert} from '../../../../utils/waitForAlert';
import {cmsPagesTest} from '../fixtures/cmsPagesTest';

const test = mergeTests(
	cmsPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-17564': {enabled: true},
		'LPS-179669': {enabled: true},
	}),
	loginTest()
);

test(
	'When a space is deleted, it is not visible in the left Spaces panel',
	{tag: '@LPD-63691'},
	async ({apiHelpers, page}) => {
		const spaceName = getRandomString();

		await test.step('Create a new space', async () => {
			await apiHelpers.headlessAssetLibrary.createAssetLibrary({
				name: spaceName,
				settings: {},
				type: 'Space',
			});
		});

		await test.step('Check the space is visible in All Spaces and left panel', async () => {
			await page.goto(PORTLET_URLS.cmsAllSpaces);

			expect(page.getByRole('menuitem', {name: spaceName})).toBeVisible();
			expect(page.getByRole('link', {name: spaceName})).toBeVisible();
		});

		await test.step('delete space from All Spaces', async () => {
			await page.getByRole('cell', {name: 'Actions'}).nth(2).click();
			await page.getByRole('menuitem', {name: 'Delete'}).click();
			await page.getByRole('button', {name: 'Delete'}).click();

			await waitForAlert(
				page,
				`Success:${spaceName} was successfully deleted.`
			);
		});

		await test.step('Check the space is not visible in All Spaces nor in the left Spaces panel', async () => {
			expect(
				page.getByRole('menuitem', {name: spaceName})
			).not.toBeVisible();
			expect(page.getByRole('link', {name: spaceName})).not.toBeVisible();
		});
	}
);
