/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../fixtures/apiHelpersTest';
import {changeTrackingPagesTest} from '../../fixtures/changeTrackingPagesTest';
import {documentLibraryPagesTest} from '../../fixtures/documentLibraryPages.fixtures';
import {featureFlagsTest} from '../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../fixtures/isolatedSiteTest';
import {getRandomInt} from '../../utils/getRandomInt';
import getRandomString from '../../utils/getRandomString';
import performLogin, {performLogout, userData} from '../../utils/performLogin';
import {waitForAlert} from '../../utils/waitForAlert';

export const test = mergeTests(
	documentLibraryPagesTest,
	isolatedSiteTest,
	apiHelpersTest,
	featureFlagsTest({
		'LPD-20556': true,
	}),
	changeTrackingPagesTest
);

let title1;
let title2;

test.beforeEach(
	async ({
		changeTrackingPage,
		ctCollection,
		documentLibraryEditFilePage,
		documentLibraryPage,
		page,
		site,
	}) => {
		await changeTrackingPage.workOnProduction();

		await documentLibraryPage.goto(site.friendlyUrlPath);
		await documentLibraryPage.goToCreateNewFile();

		title1 = getRandomString();

		await documentLibraryEditFilePage.publishNewBasicFileEntry(
			title1,
			site.friendlyUrlPath
		);

		await changeTrackingPage.workOnPublication(ctCollection);

		title2 = getRandomString();

		await documentLibraryPage.goToEditFileEntry(title1);

		await documentLibraryEditFilePage.titleSelector.fill(title2);

		await documentLibraryEditFilePage.publishButton.click();

		await waitForAlert(
			page,
			'Success:Your request completed successfully.'
		);
	}
);

test.afterEach(async ({apiHelpers, ctCollection}) => {
	await apiHelpers.headlessChangeTracking.deleteCTCollection(ctCollection.id);
});

test('LPD-25853 Edit in x publication is added in the timeline dropdown actions', async ({
	changeTrackingPage,
	ctCollection,
	documentLibraryPage,
	page,
	site,
}) => {
	await changeTrackingPage.workOnProduction();

	await documentLibraryPage.goto(site.friendlyUrlPath);

	await documentLibraryPage.goToEditFileEntry(title1);

	const timelineButton = page.getByLabel('timeline-button');
	await timelineButton.waitFor();
	await timelineButton.click();

	const timelineActionsButton = page.locator('.publication-timeline button');
	await timelineActionsButton.waitFor();
	await timelineActionsButton.click();

	const editButton = page.getByRole('button', {
		name: `Edit in ${ctCollection.name}`,
	});
	await editButton.waitFor();
	await expect(editButton).toBeVisible();
	await editButton.click();

	await page
		.locator('.change-tracking-indicator-title')
		.filter({hasText: ctCollection.name})
		.waitFor();

	await expect(
		page
			.locator('.change-tracking-indicator-title')
			.filter({hasText: ctCollection.name})
	).toBeVisible();
});

test('LPD-25853 Review Change is added in the timeline dropdown actions', async ({
	apiHelpers,
	ctCollection,
	documentLibraryPage,
	page,
	site,
}) => {
	await documentLibraryPage.goto(site.friendlyUrlPath);

	await documentLibraryPage.goToEditFileEntry(title2);

	const timelineButton = page.getByLabel('timeline-button');
	await timelineButton.waitFor();
	await timelineButton.click();

	const timelineActionsButton = page.locator('.publication-timeline button');
	await timelineActionsButton.waitFor();
	await timelineActionsButton.click();

	const reviewButton = (await apiHelpers.featureFlag.isFeatureFlagEnabled(
		'LPD-20556'
	))
		? page.getByRole('button', {name: 'Review Change'})
		: page.getByRole('button', {name: 'Review Changes'});
	await reviewButton.waitFor();
	await expect(reviewButton).toBeVisible();
	await reviewButton.click();

	await page
		.locator('.publication-name')
		.filter({hasText: ctCollection.name})
		.waitFor();

	await expect(
		page.locator('.publication-name').filter({hasText: ctCollection.name})
	).toBeVisible();

	await expect(page.getByText(title2)).toBeVisible();
});

test('LPD-25853 Discard Change is added in the timeline dropdown actions', async ({
	documentLibraryPage,
	page,
	site,
}) => {
	await documentLibraryPage.goto(site.friendlyUrlPath);

	await documentLibraryPage.goToEditFileEntry(title2);

	const timelineButton = page.getByLabel('timeline-button');
	await timelineButton.waitFor();
	await timelineButton.click();

	const timelineActionsButton = page.locator('.publication-timeline button');
	await timelineActionsButton.waitFor();
	await timelineActionsButton.click();

	const discardButton = page.getByRole('button', {name: 'Discard'});
	await discardButton.waitFor();
	await expect(discardButton).toBeVisible();
	await discardButton.click();

	await page.getByText('Discard Changes').waitFor();

	await expect(page.getByText('Discard Changes')).toBeVisible();
});

test('LPD-25853 Move Change is added in the timeline dropdown actions', async ({
	documentLibraryPage,
	page,
	site,
}) => {
	await documentLibraryPage.goto(site.friendlyUrlPath);

	await documentLibraryPage.goToEditFileEntry(title2);

	const timelineButton = page.getByLabel('timeline-button');
	await timelineButton.waitFor();
	await timelineButton.click();

	const timelineActionsButton = page.locator('.publication-timeline button');
	await timelineActionsButton.waitFor();
	await timelineActionsButton.click();

	const moveButton = page.getByRole('button', {name: 'Move'});
	await moveButton.waitFor();
	await expect(moveButton).toBeVisible();
	await moveButton.click();

	await page.getByText('Move Changes').waitFor();

	await expect(page.getByText('Move Changes')).toBeVisible();
});

test('LPD-25853 Timeline actions are not visible to user without permissions', async ({
	apiHelpers,
	ctCollection,
	documentLibraryPage,
	page,
	site,
}) => {
	const user = await apiHelpers.headlessAdminUser.postUserAccount();

	userData[user.alternateName] = {
		name: user.givenName,
		password: 'test',
		surname: user.familyName,
	};

	const companyId = await page.evaluate(() => {
		return Liferay.ThemeDisplay.getCompanyId();
	});

	const role = await apiHelpers.headlessAdminUser.postRole({
		name: 'role' + getRandomInt(),
		rolePermissions: [
			{
				actionIds: ['VIEW_CONTROL_PANEL'],
				primaryKey: companyId,
				resourceName: '90',
				scope: 1,
			},
			{
				actionIds: ['ACCESS_IN_CONTROL_PANEL', 'VIEW'],
				primaryKey: companyId,
				resourceName:
					'com_liferay_change_tracking_web_portlet_PublicationsPortlet',
				scope: 1,
			},
			{
				actionIds: ['VIEW_SITE_ADMINISTRATION'],
				primaryKey: companyId,
				resourceName: 'com.liferay.portal.kernel.model.Group',
				scope: 1,
			},
			{
				actionIds: ['ACCESS_IN_CONTROL_PANEL', 'VIEW'],
				primaryKey: companyId,
				resourceName:
					'com_liferay_document_library_web_portlet_DLAdminPortlet',
				scope: 1,
			},
			{
				actionIds: ['VIEW'],
				primaryKey: companyId,
				resourceName: 'com.liferay.document.library',
				scope: 1,
			},
		],
	});

	await apiHelpers.headlessAdminUser.assignUserToRole(
		role.externalReferenceCode,
		user.id
	);

	await performLogout(page);

	await performLogin(page, user.alternateName);

	await documentLibraryPage.goto(site.friendlyUrlPath);

	const dlFileEntryLink = page.getByRole('link', {exact: true, name: title1});
	await dlFileEntryLink.waitFor();
	await dlFileEntryLink.click();

	const timelineButton = page.getByLabel('timeline-button');
	await timelineButton.waitFor();
	await timelineButton.click();

	await page.getByText(ctCollection.name).waitFor();

	const timelineActionsButton = page.locator('.publication-timeline button');

	await expect(timelineActionsButton).toBeVisible({visible: false});
});

test('LPD-26155 Conflict warning is visible when content is edited in more than one publication', async ({
	apiHelpers,
	changeTrackingPage,
	documentLibraryEditFilePage,
	documentLibraryPage,
	page,
	site,
}) => {
	const ctCollection2 =
		await apiHelpers.headlessChangeTracking.createCTCollection(
			getRandomString()
		);

	await changeTrackingPage.workOnPublication(ctCollection2);

	await documentLibraryPage.goto(site.friendlyUrlPath);

	const title3 = getRandomString();

	await documentLibraryPage.goToEditFileEntry(title1);

	await documentLibraryEditFilePage.titleSelector.fill(title3);

	await documentLibraryEditFilePage.publishButton.click();

	await waitForAlert(page, 'Success:Your request completed successfully.');

	const timelineButton = page.locator('.change-tracking-timeline-button svg');
	await timelineButton.waitFor();
	await expect(timelineButton).toHaveCSS('color', 'rgb(255, 182, 141)');
	await timelineButton.click();

	const conflictWarning = page.locator(
		'.publication-timeline .alert-warning'
	);
	await conflictWarning.waitFor();
	await expect(conflictWarning).toBeVisible();

	let conflictIcon = page.locator(
		'.publication-timeline .change-tracking-conflict-icon-warning'
	);
	await conflictIcon.first().waitFor();
	await expect(conflictIcon).toHaveCount(2);

	await apiHelpers.featureFlag.updateFeatureFlag('LPD-20556', false);

	// Refresh the page after turning off feature flag

	await documentLibraryPage.goto(site.friendlyUrlPath);

	const dlFileEntryLink = page.getByRole('link', {exact: true, name: title3});
	await dlFileEntryLink.waitFor();
	await dlFileEntryLink.click();

	conflictIcon = page.locator('.change-tracking-conflict-icon-warning');
	await conflictIcon.first().waitFor();
	await expect(conflictIcon).toBeVisible();

	await apiHelpers.headlessChangeTracking.deleteCTCollection(
		ctCollection2.id
	);
});

test('LPD-26155 Production conflict info is visible when new changes have been made to production', async ({
	apiHelpers,
	changeTrackingPage,
	ctCollection,
	documentLibraryEditFilePage,
	documentLibraryPage,
	page,
	site,
}) => {
	await changeTrackingPage.workOnProduction();

	await documentLibraryPage.goto(site.friendlyUrlPath);

	const title3 = getRandomString();

	await documentLibraryPage.goToEditFileEntry(title1);

	await documentLibraryEditFilePage.titleSelector.fill(title3);

	await documentLibraryEditFilePage.publishButton.click();

	await waitForAlert(page, 'Success:Your request completed successfully.');

	await changeTrackingPage.workOnPublication(ctCollection);

	const prodConflictIcon = page.locator(
		'.change-tracking-conflict-icon-danger'
	);
	await prodConflictIcon.waitFor();
	await expect(prodConflictIcon).toBeVisible();
	await prodConflictIcon.click();

	const prodConflictText = page.getByText('Production Conflict');
	await prodConflictText.waitFor();
	await expect(prodConflictText).toBeVisible();

	await apiHelpers.featureFlag.updateFeatureFlag('LPD-20556', false);

	// Refresh the page after turning off feature flag

	await documentLibraryPage.goto(site.friendlyUrlPath);

	const dlFileEntryLink = page.getByRole('link', {exact: true, name: title2});
	await dlFileEntryLink.waitFor();
	await dlFileEntryLink.click();

	await prodConflictIcon.waitFor();
	await expect(prodConflictIcon).toBeVisible();
});

test('LPD-26155 No conflict icon is visible when there are no conflictsn', async ({
	apiHelpers,
	documentLibraryPage,
	page,
	site,
}) => {
	await apiHelpers.featureFlag.updateFeatureFlag('LPD-20556', false);

	await documentLibraryPage.goto(site.friendlyUrlPath);

	const dlFileEntryLink = page.getByRole('link', {exact: true, name: title2});
	await dlFileEntryLink.waitFor();
	await dlFileEntryLink.click();

	const noConflictIcon = page.locator('.change-tracking-conflict-icon');
	await noConflictIcon.waitFor();
	await expect(noConflictIcon).toBeVisible();
});
