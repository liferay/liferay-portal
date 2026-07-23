/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../fixtures/loginTest';
import getRandomString from '../../../utils/getRandomString';
import performLogin, {
	performLogout,
	userData,
} from '../../../utils/performLogin';
import {cmsPagesTest} from './fixtures/cmsPagesTest';

const test = mergeTests(cmsPagesTest, dataApiHelpersTest, loginTest());

test(
	'Admin section is not visible for non-admin space members',
	{tag: '@LPD-83160'},
	async ({apiHelpers, page, spaceSummaryPage}) => {
		const spaceName = `Space ${getRandomString()}`;
		let space = null;
		let user = null;

		space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			settings: {},
			type: 'Space',
		});

		user = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[user.alternateName] = {
			name: user.givenName,
			password: 'test',
			surname: user.familyName,
		};

		await apiHelpers.jsonWebServicesUser.addGroupUsers(space.siteId, [
			user.id,
		]);

		await performLogout(page);

		await performLogin(page, user.alternateName, 'test');

		await spaceSummaryPage.goto(spaceName);

		await expect(
			page
				.locator('.vertical-navigation-fragment')
				.getByRole('menuitem', {
					name: 'Admin',
				})
		).not.toBeVisible();

		await expect(
			page
				.locator('.vertical-navigation-fragment')
				.getByRole('menuitem', {
					name: 'Contents',
				})
		).toBeVisible();

		await apiHelpers.headlessAdminUser.deleteUserAccount(user.id);
		await apiHelpers.headlessAssetLibrary.deleteAssetLibrary(space.id);
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
