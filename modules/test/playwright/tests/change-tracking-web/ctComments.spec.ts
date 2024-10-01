/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../fixtures/apiHelpersTest';
import {changeTrackingPagesTest} from '../../fixtures/changeTrackingPagesTest';
import {dataApiHelpersTest} from '../../fixtures/dataApiHelpersTest';
import getRandomString from '../../utils/getRandomString';
import performLogin, {performLogout} from '../../utils/performLogin';
import {waitForAlert} from '../../utils/waitForAlert';
import {journalPagesTest} from '../journal-web/fixtures/journalPagesTest';

export const test = mergeTests(
	apiHelpersTest,
	dataApiHelpersTest,
	journalPagesTest,
	changeTrackingPagesTest
);

let user;

test.afterEach(async ({apiHelpers, ctCollection}) => {
	await apiHelpers.headlessChangeTracking.deleteCTCollection(ctCollection.id);

	const role = await apiHelpers.headlessAdminUser.getRoles('Administrator');

	await apiHelpers.headlessAdminUser.deleteRoleUserAccountAssociation(
		role.items[0].id,
		user.id
	);
});

test.beforeEach(async ({apiHelpers, ctCollection}) => {
	await apiHelpers.headlessChangeTracking.checkoutCTCollection('0');

	user = await apiHelpers.headlessAdminUser.getUserAccountByEmailAddress(
		'demo.unprivileged@liferay.com'
	);

	const role =
		await apiHelpers.headlessAdminUser.getRoleByName('Administrator');

	await apiHelpers.headlessAdminUser.assignUserToRole(
		role.externalReferenceCode,
		user.id
	);

	await apiHelpers.headlessChangeTracking.checkoutCTCollection(
		ctCollection.id
	);
});

test('LPD-17130 Only comment owners are allowed to perform actions on the comment', async ({
	changeTrackingPage,
	ctCollection,
	journalEditArticlePage,
	page,
}) => {
	const journalName = getRandomString();
	await journalEditArticlePage.goto();
	await journalEditArticlePage.fillTitle(journalName);
	await page.getByRole('button', {name: 'Publish'}).click();
	await waitForAlert(
		page,
		`Success:${journalName} was created successfully.`
	);

	await changeTrackingPage.goToReviewChanges(ctCollection.name);

	await changeTrackingPage.addComment();

	const dropdownMenu = page.locator('.comment-row button');

	await expect(dropdownMenu).toBeVisible();

	await performLogout(page);

	await performLogin(page, user.alternateName);

	await changeTrackingPage.goToReviewChanges(ctCollection.name);

	await changeTrackingPage.openComments();

	await expect(dropdownMenu).toBeVisible({visible: false});
});
