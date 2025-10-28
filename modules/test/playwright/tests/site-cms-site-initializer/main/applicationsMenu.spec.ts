/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {HomePage} from '../../../pages/portal-web/HomePage';
import getRandomString from '../../../utils/getRandomString';
import performLogin, {
	performLogout,
	userData,
} from '../../../utils/performLogin';
import {cmsPagesTest} from './fixtures/cmsPagesTest';

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
	'Can access to Spaces from the Applications Menu',
	{tag: '@LPD-59033'},
	async ({apiHelpers, page}) => {
		const spaceName = `Space ${getRandomString()}`;

		await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			settings: {
				logoColor: 'outline-3',
				sharingEnabled: true,
			},
			type: 'Space',
		});

		const homePage = new HomePage(page);

		await homePage.openApplicationMenu();

		await page.getByLabel('Applications Menu').waitFor({state: 'visible'});

		await expect(
			page.locator('a span.text-truncate', {
				hasText: 'Default',
			})
		).toBeVisible();

		const spaceLink = page.locator('a span.text-truncate', {
			hasText: spaceName,
		});

		await spaceLink.click();

		await expect(
			page.locator('nav a.breadcrumb-link', {
				hasText: spaceName,
			})
		).toBeVisible();
	}
);

test(
	'Cannot access the Control Menu as an admin or non admin user on the CMS site',
	{tag: '@LPD-68992'},
	async ({apiHelpers, page, spaceSummaryPage}) => {
		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[user.alternateName] = {
			name: user.givenName,
			password: 'test',
			surname: user.familyName,
		};

		const spaceName = 'Default';

		await spaceSummaryPage.goto(spaceName);

		await expect(
			page.getByLabel('Control Menu', {exact: true})
		).not.toBeVisible();

		await spaceSummaryPage.addUserOrUserGroup(user.name, 'users');

		await performLogout(page);

		await performLogin(page, user.alternateName);

		await spaceSummaryPage.goto(spaceName);

		await expect(
			page.getByLabel('Control Menu', {exact: true})
		).not.toBeVisible();
	}
);
