/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {rolesPagesTest} from '../../../fixtures/rolesPagesTest';
import {liferayConfig} from '../../../liferay.config';
import getRandomString from '../../../utils/getRandomString';
import performLogin, {
	performLogout,
	userData,
} from '../../../utils/performLogin';
import getPageDefinition from '../../layout-content-page-editor-web/main/utils/getPageDefinition';
import getWidgetDefinition from '../../layout-content-page-editor-web/main/utils/getWidgetDefinition';

export const test = mergeTests(
	dataApiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	loginTest(),
	rolesPagesTest
);

test('LPD-28406 Access to My Account is allowed after disabling access to it through permissions', async ({
	apiHelpers,
	page,
	roleDefinePermissionsPage,
	rolePage,
	rolesPage,
}) => {
	await rolesPage.goto();

	await rolesPage.userLink.click();
	await rolePage.definePermissionsLink.click();

	await roleDefinePermissionsPage.changePermission(
		'Account Settings',
		'Access in Personal Menu All',
		false
	);

	try {
		const site = await apiHelpers.headlessSite.createSite({
			name: getRandomString(),
		});

		apiHelpers.data.push({id: site.id, type: 'site'});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getWidgetDefinition({
					id: getRandomString(),
					widgetName: 'com_liferay_login_web_portlet_LoginPortlet',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

		await expect(page.getByRole('link', {name: 'Test Test'})).toBeVisible();

		const userAccount =
			await apiHelpers.headlessAdminUser.postUserAccount();

		userData[userAccount.alternateName] = {
			name: userAccount.givenName,
			password: 'test',
			surname: userAccount.familyName,
		};

		await performLogout(page);
		await performLogin(page, userAccount.alternateName);

		await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

		await expect(
			page.getByText(`You are signed in as ${userAccount.name}.`, {
				exact: true,
			})
		).toBeVisible();
		await expect(
			page.getByRole('link', {name: userAccount.name})
		).not.toBeVisible();
	}
	finally {
		await performLogout(page);
		await performLogin(page, 'test');

		await rolesPage.goto();

		await rolesPage.userLink.click();
		await rolePage.definePermissionsLink.click();

		await roleDefinePermissionsPage.changePermission(
			'Account Settings',
			'Access in Personal Menu All',
			true
		);
	}
});

test('LPD-55426 Test sign in button is disabled until page is fully loaded', async ({
	context,
	page,
}) => {
	await performLogout(page);

	const cdpSession = await context.newCDPSession(page);
	await cdpSession.send('Network.emulateNetworkConditions', {
		connectionType: 'cellular3g',
		downloadThroughput: (750 * 1024) / 8,
		latency: 70,
		offline: false,
		uploadThroughput: (250 * 1024) / 8,
	});

	await page.goto(liferayConfig.environment.baseUrl + '/c/portal/login', {
		waitUntil: 'commit',
	});
	await expect(page.getByRole('button', {name: 'Sign In'})).toHaveAttribute(
		'disabled'
	);
	await page.waitForLoadState('domcontentloaded');
	await expect(page.getByRole('button', {name: 'Sign In'})).toBeEnabled();

	await page.goto(liferayConfig.environment.baseUrl, {
		waitUntil: 'domcontentloaded',
	});
	await page.getByRole('button', {name: 'Sign In'}).click();
	await expect(page.getByText('Forgot Password')).toBeVisible();
	await expect(
		page.getByRole('button', {name: 'Sign In'}).last()
	).toHaveAttribute('disabled');
	await expect(
		page.getByRole('button', {name: 'Sign In'}).last()
	).toBeEnabled();

	await cdpSession.detach();
});
