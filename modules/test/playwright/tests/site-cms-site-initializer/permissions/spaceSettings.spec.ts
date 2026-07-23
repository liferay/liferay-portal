/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../fixtures/loginTest';
import {addCMSAdministrator} from '../../../utils/addCMSAdministrator';
import {addSpaceUser} from '../../../utils/addSpaceUser';
import getRandomString from '../../../utils/getRandomString';
import {performUserSwitch} from '../../../utils/performLogin';
import {cmsPagesTest} from './fixtures/cmsPagesTest';

const test = mergeTests(cmsPagesTest, dataApiHelpersTest, loginTest());

test(
	'CMS Administrator can edit the Friendly URL of any space',
	{tag: '@LPD-88344'},
	async ({apiHelpers, page, spaceSummaryPage}) => {
		const spaceName = `Space ${getRandomString()}`;

		await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			type: 'Space',
		});

		const user = await addCMSAdministrator(apiHelpers);

		await performUserSwitch(page, user.alternateName);

		await spaceSummaryPage.goto(spaceName);

		await page.getByRole('button', {name: 'More Actions'}).click();

		await page.getByRole('menuitem', {name: 'Space Settings'}).click();

		await expect(
			page.getByRole('textbox', {name: 'Friendly URL Required'})
		).toBeEditable();
	}
);

test(
	'Space Administrator can edit the Friendly URL of a space they administer',
	{tag: '@LPD-88344'},
	async ({apiHelpers, page, spaceSummaryPage}) => {
		const spaceName = `Space ${getRandomString()}`;

		const space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			type: 'Space',
		});

		const user = await addSpaceUser(
			apiHelpers,
			space.externalReferenceCode,
			'Asset Library Administrator'
		);

		await performUserSwitch(page, user.alternateName);

		await spaceSummaryPage.goto(spaceName);

		await page.getByRole('button', {name: 'More Actions'}).click();

		await page.getByRole('menuitem', {name: 'Space Settings'}).click();

		await expect(
			page.getByRole('textbox', {name: 'Friendly URL Required'})
		).toBeEditable();
	}
);

test(
	'Space Content Reviewer cannot access Space Settings',
	{tag: '@LPD-88344'},
	async ({apiHelpers, page, spaceSummaryPage}) => {
		const spaceName = `Space ${getRandomString()}`;

		const space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			type: 'Space',
		});

		const user = await addSpaceUser(
			apiHelpers,
			space.externalReferenceCode,
			'Asset Library Content Reviewer'
		);

		await performUserSwitch(page, user.alternateName);

		await spaceSummaryPage.goto(spaceName);

		await page.getByRole('button', {name: 'More Actions'}).click();

		await expect(
			page.getByRole('menuitem', {name: 'Space Settings'})
		).toHaveCount(0);
	}
);

test(
	'Space Member cannot access Space Settings',
	{tag: '@LPD-88344'},
	async ({apiHelpers, page, spaceSummaryPage}) => {
		const spaceName = `Space ${getRandomString()}`;

		const space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			type: 'Space',
		});

		const user = await addSpaceUser(
			apiHelpers,
			space.externalReferenceCode,
			'Asset Library Member'
		);

		await performUserSwitch(page, user.alternateName);

		await spaceSummaryPage.goto(spaceName);

		await page.getByRole('button', {name: 'More Actions'}).click();

		await expect(
			page.getByRole('menuitem', {name: 'Space Settings'})
		).toHaveCount(0);
	}
);
