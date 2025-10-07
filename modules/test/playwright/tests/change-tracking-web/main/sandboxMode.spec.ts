/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {changeTrackingPagesTest} from '../../../fixtures/changeTrackingPagesTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import getRandomString from '../../../utils/getRandomString';
import {performLoginViaApi, performLogout} from '../../../utils/performLogin';
import {waitForAlert} from '../../../utils/waitForAlert';
import {journalPagesTest} from '../../journal-web/main/fixtures/journalPagesTest';

export const test = mergeTests(
	featureFlagsTest({
		'LPD-20556': {enabled: true},
	}),
	apiHelpersTest,
	changeTrackingPagesTest,
	journalPagesTest
);

test.beforeEach(async ({changeTrackingPage}) => {
	await changeTrackingPage.toggleSandboxConfiguration(true);
});

test.afterEach(async ({changeTrackingPage}) => {
	await changeTrackingPage.toggleSandboxConfiguration(false);
});

test('LPD-53979 Assert warning alert appears when sandbox mode is enabled and Owner role does not have publishing permission', async ({
	changeTrackingPage,
	page,
}) => {
	await changeTrackingPage.goto();

	await page.getByLabel('Options').click();

	await page.getByRole('menuitem', {name: 'Settings'}).click();

	const sandboxOnlyCheckbox = page.getByRole('checkbox', {
		name: 'enable-sandbox-only',
	});

	await expect(sandboxOnlyCheckbox).toBeChecked();

	const warningAlert = page.getByText(
		'Currently, any publication owner can publish the publication'
	);

	await expect(warningAlert).toBeVisible();

	const editPermissionsButton = page.getByRole('button', {
		name: 'Edit Permissions',
	});

	await editPermissionsButton.click();

	await page.waitForTimeout(300);

	const ownerRole = page.locator('tr').filter({hasText: 'Owner'});

	const checkbox = ownerRole
		.locator('td')
		.nth(5)
		.locator('.custom-control > label > .custom-control-input');

	await checkbox.uncheck();

	const saveButton = page.getByRole('button', {name: 'Save'});

	await saveButton.click();

	const closeButton = page
		.locator('.modal')
		.getByLabel('Close', {exact: true});

	await closeButton.click();

	await expect(warningAlert).toBeHidden();

	await changeTrackingPage.toggleSandboxConfiguration(false);

	await expect(warningAlert).toBeHidden();

	await editPermissionsButton.click();

	await checkbox.check();

	await saveButton.click();

	await closeButton.click();

	await expect(warningAlert).toBeHidden();

	await changeTrackingPage.toggleSandboxConfiguration(true);

	await expect(warningAlert).toBeVisible();
});

test('LPD-34602 Add view-only mode for production when using Publications sandbox', async ({
	apiHelpers,
	changeTrackingPage,
	page,
}) => {
	const user = await changeTrackingPage.addUserWithPublicationsUserRole();

	await performLogout(page);

	await performLoginViaApi({page, screenName: user.alternateName});

	const changeTrackingIndicatorButton = page.locator(
		'.change-tracking-indicator-button'
	);

	await changeTrackingIndicatorButton.click();

	const viewInProductionMenuItem = page.getByRole('menuitem', {
		name: 'View On Production',
	});

	await expect(viewInProductionMenuItem).toBeVisible();

	await viewInProductionMenuItem.click();

	const viewingProductionMenuItem = page.getByText('Viewing Production');

	await expect(viewingProductionMenuItem).toBeVisible();

	await viewingProductionMenuItem.click();

	const workOnUserMenuItem = page.getByText('Work on user');

	await expect(workOnUserMenuItem).toBeVisible();

	await workOnUserMenuItem.click();

	await performLogout(page);

	await performLoginViaApi({page, screenName: 'test'});

	await changeTrackingPage.workOnProduction();

	await apiHelpers.headlessAdminUser.deleteUserAccount(Number(user.id));
});

test('LPD-39341 Sandbox mode allows users to work on production without permissions', async ({
	apiHelpers,
	changeTrackingPage,
	journalEditArticlePage,
	page,
}) => {
	const user = await changeTrackingPage.addUserWithPublicationsUserRole();

	await performLogout(page);

	await performLoginViaApi({page, screenName: user.alternateName});

	const sandboxPublication = await page.getByText(user.alternateName + ' - ');

	await expect(sandboxPublication).toBeVisible();

	await journalEditArticlePage.goto();

	const title = getRandomString();

	await journalEditArticlePage.fillTitle(title);

	await journalEditArticlePage.publishArticle();

	await waitForAlert(page, `Success:${title} was created successfully.`);

	await performLogout(page);

	await performLoginViaApi({page, screenName: 'test'});

	await changeTrackingPage.publishSandboxPublication(user.alternateName);

	await performLogout(page);

	await performLoginViaApi({page, screenName: user.alternateName});

	const newSandboxPublication = await page.getByText(
		user.alternateName + ' - '
	);

	await expect(sandboxPublication).not.toBe(newSandboxPublication);

	await expect(newSandboxPublication).toBeVisible();

	await performLogout(page);

	await performLoginViaApi({page, screenName: 'test'});

	await changeTrackingPage.workOnProduction();

	await apiHelpers.headlessAdminUser.deleteUserAccount(Number(user.id));
});
