/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../../fixtures/loginTest';
import getRandomString from '../../../../utils/getRandomString';
import {performUserSwitchViaApi} from '../../../../utils/performLogin';
import {cmsPagesTest} from '../fixtures/cmsPagesTest';
import {registerUserCredentials} from './helpers/roleMembership';

const test = mergeTests(cmsPagesTest, dataApiHelpersTest, loginTest());

test(
	'Clicking the space-invite notification opens the assigned space',
	{tag: '@LPD-85681'},
	async ({apiHelpers, page, spaceSummaryPage}) => {
		const spaceName = `Space ${getRandomString()}`;

		await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			settings: {},
			type: 'Space',
		});

		const user = await apiHelpers.headlessAdminUser.postUserAccount();
		const userFullName = `${user.givenName} ${user.familyName}`;

		registerUserCredentials(user);

		await test.step('Add the user to the space and switch to them', async () => {
			await spaceSummaryPage.goto(spaceName);
			await spaceSummaryPage.addUserOrUserGroup(userFullName, 'users');

			await performUserSwitchViaApi(page, user.alternateName);
		});

		const notificationLink = page.getByRole('link', {
			name: new RegExp(spaceName),
		});

		await test.step('Open Notifications and see the space invitation', async () => {
			await page
				.getByRole('button', {name: `${userFullName} User Profile`})
				.click();

			await page.getByRole('menuitem', {name: 'Notifications'}).click();

			await expect(notificationLink).toBeVisible();
		});

		await test.step('Click the invitation and land on the space page', async () => {
			await notificationLink.click();

			await expect(
				page.getByRole('heading', {exact: true, name: spaceName})
			).toBeVisible();
		});
	}
);
