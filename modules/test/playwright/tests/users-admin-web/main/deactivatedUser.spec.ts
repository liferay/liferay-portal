/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {usersAndOrganizationsPagesTest} from '../../../fixtures/usersAndOrganizationsPagesTest';
import {liferayConfig} from '../../../liferay.config';
import getRandomString from '../../../utils/getRandomString';
import {performLoginViaApi, userData} from '../../../utils/performLogin';
import {sitesAdminPagesTest} from '../../site-admin-web/main/fixtures/sitesAdminPagesTest';

export const test = mergeTests(
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	dataApiHelpersTest,
	loginTest(),
	isolatedSiteTest,
	usersAndOrganizationsPagesTest,
	sitesAdminPagesTest
);

test('Proper page is shown when disabled user on a disabled site tries to refresh on private page', async ({
	apiHelpers,
	browser,
	page,
	site,
	sitesAdminPage,
	usersAndOrganizationsPage,
}) => {
	const user = await apiHelpers.headlessAdminUser.postUserAccount();

	userData[user.alternateName] = {
		name: user.givenName,
		password: 'test',
		surname: user.familyName,
	};

	const layout = await apiHelpers.headlessDelivery.createSitePage({
		pagePermissions: [
			{
				actionKeys: ['VIEW'],
				roleKey: 'Site Member',
			},
		],
		siteId: site.id,
		title: getRandomString(),
	});

	const role =
		await apiHelpers.headlessAdminUser.getRoleByName('Site Member');

	await apiHelpers.headlessAdminUser.assignUserToSite(
		role.id,
		site.id,
		user.id
	);

	const newPage = await browser.newPage({
		baseURL: liferayConfig.environment.baseUrl,
	});

	await performLoginViaApi({page: newPage, screenName: user.alternateName});

	await newPage.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

	page.once('dialog', async (dialog) => await dialog.accept());

	await usersAndOrganizationsPage.goToUsers();

	await usersAndOrganizationsPage.deActivateUsers([user.name]);

	await sitesAdminPage.goto();

	await expect(page.getByRole('link', {name: site.name})).toBeVisible();

	await sitesAdminPage.deactivateSite(site.name);

	await newPage.reload();

	await expect(
		newPage.getByText(`${user.emailAddress} is not active.`)
	).toBeVisible();

	await apiHelpers.headlessAdminUser.deleteUserAccount(Number(user.id));
});
