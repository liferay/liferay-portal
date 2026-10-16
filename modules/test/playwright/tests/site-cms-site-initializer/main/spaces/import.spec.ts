/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';
import {PORTLET_URLS} from '../../../../utils/portletUrls';
import {cmsPagesTest} from '../fixtures/cmsPagesTest';

const test = mergeTests(cmsPagesTest, dataApiHelpersTest, loginTest());

test(
	'The Import dialog opens from More Actions for an existing Space',
	{tag: '@LPD-85670'},
	async ({apiHelpers, page}) => {
		const space = await apiHelpers.headlessAssetLibrary.createSpace();

		await page.goto(PORTLET_URLS.cmsAllSpaces);

		await page.getByRole('link', {exact: true, name: space.name}).click();

		await page.getByRole('button', {name: 'More Actions'}).waitFor();

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {name: 'Import'}),
			trigger: page.getByRole('button', {name: 'More Actions'}),
		});

		await expect(page.getByRole('heading', {name: 'Import'})).toBeVisible();
	}
);
