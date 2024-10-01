/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../fixtures/featureFlagsTest';
import {loginTest} from '../../fixtures/loginTest';
import {portalDefaultPermissionsPagesTest} from '../../fixtures/portalDefaultPermissionsPagesTest';
import getRandomString from '../../utils/getRandomString';
import {waitForAlert} from '../../utils/waitForAlert';

export const test = mergeTests(
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-21265': true,
		'LPS-178052': true,
	}),
	loginTest(),
	portalDefaultPermissionsPagesTest
);

const setupInstanceDefaultPermissions = async ({
	defaultPermissionsConfigurationPage,
	page,
}) => {
	await defaultPermissionsConfigurationPage.goto();

	await expect(
		defaultPermissionsConfigurationPage.portalDefaultPermissionsSearchContainer
	).toBeVisible();
	await page.waitForTimeout(500);

	await defaultPermissionsConfigurationPage.editPageButton.click();

	await defaultPermissionsConfigurationPage.frameSaveButton.waitFor({
		state: 'attached',
	});
	await page.waitForTimeout(300);

	await defaultPermissionsConfigurationPage.analyticsAdministratorUpdateDiscussionCheckbox.setChecked(
		true
	);
	await defaultPermissionsConfigurationPage.powerUserUpdateDiscussionCheckbox.setChecked(
		true
	);

	await defaultPermissionsConfigurationPage.frameSaveButton.click();

	await defaultPermissionsConfigurationPage.frameSaveButton.waitFor({
		state: 'detached',
	});

	await waitForAlert(page);
};

const setupSiteDefaultPermissions = async ({
	defaultPermissionsSiteConfigurationPage,
	page,
	site,
}) => {
	await defaultPermissionsSiteConfigurationPage.goto(site.name);

	await expect(
		defaultPermissionsSiteConfigurationPage.portalDefaultPermissionsSearchContainer
	).toBeVisible();
	await page.waitForTimeout(500);

	await defaultPermissionsSiteConfigurationPage.actionsPageButton.click();
	await defaultPermissionsSiteConfigurationPage.editPageButton.click();

	await defaultPermissionsSiteConfigurationPage.frameSaveButton.waitFor({
		state: 'attached',
	});
	await page.waitForTimeout(300);

	await defaultPermissionsSiteConfigurationPage.analyticsAdministratorUpdateDiscussionCheckbox.setChecked(
		false
	);

	await defaultPermissionsSiteConfigurationPage.frameSaveButton.click();

	await defaultPermissionsSiteConfigurationPage.frameSaveButton.waitFor({
		state: 'detached',
	});

	await waitForAlert(page);
};

test('LPD-21645 Set up the default permissions for pages', async ({
	defaultPermissionsConfigurationPage,
	page,
}) => {
	await setupInstanceDefaultPermissions({
		defaultPermissionsConfigurationPage,
		page,
	});

	await defaultPermissionsConfigurationPage.editPageButton.click();

	await defaultPermissionsConfigurationPage.frameSaveButton.waitFor({
		state: 'attached',
	});

	await expect(
		defaultPermissionsConfigurationPage.analyticsAdministratorUpdateDiscussionCheckbox
	).toBeChecked();

	await expect(
		defaultPermissionsConfigurationPage.powerUserUpdateDiscussionCheckbox
	).toBeChecked();

	await defaultPermissionsConfigurationPage.analyticsAdministratorUpdateDiscussionCheckbox.setChecked(
		false
	);
	await defaultPermissionsConfigurationPage.powerUserUpdateDiscussionCheckbox.setChecked(
		false
	);

	await defaultPermissionsConfigurationPage.frameSaveButton.click();

	await defaultPermissionsConfigurationPage.frameSaveButton.waitFor({
		state: 'detached',
	});

	await waitForAlert(page);
});

test('LPD-22038 Set up the default site permissions for pages', async ({
	apiHelpers,
	defaultPermissionsConfigurationPage,
	defaultPermissionsSiteConfigurationPage,
	page,
}) => {
	await setupInstanceDefaultPermissions({
		defaultPermissionsConfigurationPage,
		page,
	});

	const site = await apiHelpers.headlessSite.createSite({
		name: getRandomString(),
	});

	apiHelpers.data.push({id: site.id, type: 'site'});

	await setupSiteDefaultPermissions({
		defaultPermissionsSiteConfigurationPage,
		page,
		site,
	});

	await defaultPermissionsSiteConfigurationPage.actionsPageButton.click();
	await defaultPermissionsSiteConfigurationPage.editPageButton.click();

	await defaultPermissionsSiteConfigurationPage.frameSaveButton.waitFor({
		state: 'attached',
	});

	await expect(
		defaultPermissionsSiteConfigurationPage.analyticsAdministratorUpdateDiscussionCheckbox
	).not.toBeChecked();
	await expect(
		defaultPermissionsSiteConfigurationPage.powerUserUpdateDiscussionCheckbox
	).toBeChecked();

	await defaultPermissionsConfigurationPage.frameSaveButton.click();

	await defaultPermissionsConfigurationPage.frameSaveButton.waitFor({
		state: 'detached',
	});

	await waitForAlert(page);

	const dialogPromise = page.waitForEvent('dialog').then(async (dialog) => {
		await dialog.accept();

		return dialog.message();
	});

	await defaultPermissionsSiteConfigurationPage.actionsPageButton.click();
	await defaultPermissionsSiteConfigurationPage.resetPageButton.click();

	await dialogPromise;

	await waitForAlert(page);

	await defaultPermissionsSiteConfigurationPage.actionsPageButton.click();
	await defaultPermissionsSiteConfigurationPage.editPageButton.click();

	await defaultPermissionsSiteConfigurationPage.frameSaveButton.waitFor({
		state: 'attached',
	});

	await expect(
		defaultPermissionsSiteConfigurationPage.analyticsAdministratorUpdateDiscussionCheckbox
	).toBeChecked();
	await expect(
		defaultPermissionsSiteConfigurationPage.powerUserUpdateDiscussionCheckbox
	).toBeChecked();
});

test('LPD-22040 Check default permissions for pages', async ({
	apiHelpers,
	defaultPermissionsConfigurationPage,
	defaultPermissionsSiteConfigurationPage,
	page,
}) => {
	await setupInstanceDefaultPermissions({
		defaultPermissionsConfigurationPage,
		page,
	});

	const site = await apiHelpers.headlessSite.createSite({
		name: getRandomString(),
	});

	apiHelpers.data.push({id: site.id, type: 'site'});

	let layout = await apiHelpers.headlessDelivery.createSitePage({
		siteId: site.id,
		title: getRandomString(),
	});

	// @ts-ignore

	layout.pagePermissions.forEach((pagePermission) => {
		if (
			['Analytics Administrator', 'Owner', 'Power User'].indexOf(
				pagePermission.roleKey
			) >= 0
		) {
			expect(
				pagePermission.actionKeys.indexOf('UPDATE_DISCUSSION')
			).toBeGreaterThanOrEqual(0);
		}
		else {
			expect(
				pagePermission.actionKeys.indexOf('UPDATE_DISCUSSION')
			).toBeLessThan(0);
		}
	});

	await setupSiteDefaultPermissions({
		defaultPermissionsSiteConfigurationPage,
		page,
		site,
	});

	layout = await apiHelpers.headlessDelivery.createSitePage({
		siteId: site.id,
		title: getRandomString(),
	});

	// @ts-ignore

	layout.pagePermissions.forEach((pagePermission) => {
		if (['Owner', 'Power User'].indexOf(pagePermission.roleKey) >= 0) {
			expect(
				pagePermission.actionKeys.indexOf('UPDATE_DISCUSSION')
			).toBeGreaterThanOrEqual(0);
		}
		else {
			expect(
				pagePermission.actionKeys.indexOf('UPDATE_DISCUSSION')
			).toBeLessThan(0);
		}
	});
});
