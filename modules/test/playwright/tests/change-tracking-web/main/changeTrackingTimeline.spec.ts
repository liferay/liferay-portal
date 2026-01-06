/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {changeTrackingPagesTest} from '../../../fixtures/changeTrackingPagesTest';
import {documentLibraryPagesTest} from '../../../fixtures/documentLibraryPages.fixtures';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {workflowPagesTest} from '../../../fixtures/workflowPagesTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import {
	performLoginViaApi,
	performLogout,
	userData,
} from '../../../utils/performLogin';
import {waitForAlert} from '../../../utils/waitForAlert';
import {journalPagesTest} from '../../journal-web/main/fixtures/journalPagesTest';

export const test = mergeTests(
	apiHelpersTest,
	changeTrackingPagesTest,
    documentLibraryPagesTest,
    isolatedSiteTest,
    journalPagesTest,
    workflowPagesTest
);

const folderTitle1 = getRandomString();
const folderTitle2 = getRandomString();
const title1 = getRandomString();
const title2 = getRandomString();

test.beforeEach(
	async ({
		changeTrackingPage,
		ctCollection,
		documentLibraryEditFilePage,
		documentLibraryEditFolderPage,
		documentLibraryPage,
		page,
		site,
	}) => {
		await documentLibraryPage.goto(site.friendlyUrlPath);
		await documentLibraryPage.goToCreateNewFile();

		await documentLibraryEditFilePage.publishNewBasicFileEntry(
			title1,
			site.friendlyUrlPath
		);

		await documentLibraryPage.goToCreateNewFolder();

		await documentLibraryEditFolderPage.fillTitle(folderTitle1);

		await page.getByRole('button', {name: 'Save'}).click();

		await changeTrackingPage.workOnPublication(ctCollection);

		await documentLibraryPage.goto(site.friendlyUrlPath);
		await documentLibraryPage.goToEditFileEntry(title1);

		await documentLibraryEditFilePage.titleSelector.fill(title2);
		await documentLibraryEditFilePage.publishButton.click();

		await waitForAlert(
			page,
			'Success:Your request completed successfully.'
		);

		await documentLibraryPage.goto(site.friendlyUrlPath);
		await documentLibraryPage.goToEditFolder(folderTitle1);

		await documentLibraryEditFolderPage.fillTitle(folderTitle2);

		await page.getByRole('button', {name: 'Save'}).click();

		await waitForAlert(
			page,
			'Success:Your request completed successfully.'
		);
	}
);

test.afterEach(async ({apiHelpers, ctCollection}) => {
	await apiHelpers.headlessChangeTracking.deleteCTCollection(
		ctCollection.body.id
	);
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
	await documentLibraryPage.goToViewFileEntry(title1);

	const timelineButton = page.getByLabel('timeline-button');
	await timelineButton.waitFor();
	await timelineButton.click();

	const timelineActionsButton = page.locator('.publication-timeline button');
	await timelineActionsButton.waitFor();
	await timelineActionsButton.click();

	const editButton = page.getByRole('button', {
		name: `Edit in ${ctCollection.body.name}`,
	});
	await editButton.waitFor();

	await expect(editButton).toBeVisible();

	await editButton.click();

	await page
		.locator('.change-tracking-indicator-title')
		.filter({hasText: ctCollection.body.name})
		.waitFor();

	await expect(
		page
			.locator('.change-tracking-indicator-title')
			.filter({hasText: ctCollection.body.name})
	).toBeVisible();
});

test('LPD-25853 Review Change is added in the timeline dropdown actions', async ({
	changeTrackingPage,
	ctCollection,
	documentLibraryPage,
	page,
	site,
}) => {
	await changeTrackingPage.workOnPublication(ctCollection);

	await documentLibraryPage.goto(site.friendlyUrlPath);
	await documentLibraryPage.goToEditFileEntry(title2);

	await expect(page.getByText('Upload documents no larger')).toBeVisible();

	const timelineButton = page.getByLabel('timeline-button');
	await timelineButton.click();

	const timelineActionsButton = page.getByLabel('timeline-actions');

	await clickAndExpectToBeVisible({
		autoClick: true,
		target: timelineActionsButton,
		trigger: timelineButton,
	});

	const reviewButton = page.getByRole('button', {name: 'Review Change'});

	await clickAndExpectToBeVisible({
		autoClick: true,
		target: reviewButton,
		trigger: timelineActionsButton,
	});

	await reviewButton.click();

	await page
		.locator('.publication-name')
		.filter({hasText: ctCollection.body.name})
		.waitFor();

	await expect(
		page
			.locator('.publication-name')
			.filter({hasText: ctCollection.body.name})
	).toBeVisible();

	await expect(page.getByText(title2)).toBeVisible();
});

test('LPD-25853 Discard Change is added in the timeline dropdown actions', async ({
	changeTrackingPage,
	ctCollection,
	documentLibraryPage,
	page,
	site,
}) => {
	await changeTrackingPage.workOnPublication(ctCollection);

	await documentLibraryPage.goto(site.friendlyUrlPath);
	await documentLibraryPage.goToEditFileEntry(title2);

	await expect(page.getByText('Upload documents no larger')).toBeVisible();

	const timelineButton = page.getByLabel('timeline-button');
	await timelineButton.click();

	const timelineActionsButton = page.getByLabel('timeline-actions');

	await clickAndExpectToBeVisible({
		autoClick: true,
		target: timelineActionsButton,
		trigger: timelineButton,
	});

	const discardButton = page.getByRole('button', {name: 'Discard'});

	await clickAndExpectToBeVisible({
		autoClick: true,
		target: discardButton,
		trigger: timelineActionsButton,
	});

	await discardButton.click();

	await page.getByText('Discard Changes').waitFor();

	await expect(page.getByText('Discard Changes')).toBeVisible();
});

test('LPD-25853 Move Change is added in the timeline dropdown actions', async ({
	changeTrackingPage,
	ctCollection,
	documentLibraryPage,
	page,
	site,
}) => {
	await changeTrackingPage.workOnPublication(ctCollection);

	await documentLibraryPage.goto(site.friendlyUrlPath);
	await documentLibraryPage.goToEditFileEntry(title2);

	await expect(page.getByText('Upload documents no larger')).toBeVisible();

	const timelineButton = page.getByLabel('timeline-button');
	await timelineButton.click();

	const timelineActionsButton = page.getByLabel('timeline-actions');

	await clickAndExpectToBeVisible({
		autoClick: true,
		target: timelineActionsButton,
		trigger: timelineButton,
	});

	const moveButton = page.getByRole('button', {name: 'Move'});

	await clickAndExpectToBeVisible({
		autoClick: true,
		target: moveButton,
		trigger: timelineActionsButton,
	});

	await moveButton.click();

	const moveChangesHeader = page
		.getByTestId('headerTitle')
		.filter({hasText: 'Move Changes'});

	await moveChangesHeader.waitFor();

	await expect(moveChangesHeader).toBeVisible();
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

	await performLoginViaApi({page, screenName: user.alternateName});

	await documentLibraryPage.goto(site.friendlyUrlPath);

	const dlFileEntryLink = page.getByRole('link', {exact: true, name: title1});
	await dlFileEntryLink.waitFor();
	await dlFileEntryLink.click();

	await expect(page.getByText('No Preview Available')).toBeVisible();

	const timelineButton = page.getByLabel('timeline-button');
	await timelineButton.waitFor();
	await timelineButton.click();

	await page.getByText(ctCollection.body.name).waitFor();

	const timelineActionsButton = page.locator('.publication-timeline button');

	await expect(timelineActionsButton).toBeVisible({visible: false});

	await performLogout(page);

	await performLoginViaApi({page, screenName: 'test'});
});

test('LPD-26155 Conflict warning is visible when content is edited in more than one publication', async ({
	apiHelpers,
	changeTrackingPage,
	documentLibraryEditFolderPage,
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

	const folderTitle3 = getRandomString();

	await documentLibraryPage.goToEditFolder(folderTitle1);

	await documentLibraryEditFolderPage.fillTitle(folderTitle3);

	await page.getByRole('button', {name: 'Save'}).click();

	await waitForAlert(page, 'Success:Your request completed successfully.');

	await documentLibraryPage.goToEditFolder(folderTitle3);

	const timelineButton = page.locator('.change-tracking-timeline-button svg');
	await timelineButton.waitFor();

	await expect(timelineButton).toHaveCSS('color', 'rgb(255, 182, 141)');

	await timelineButton.click();

	const conflictWarning = page.locator(
		'.publication-timeline .alert-warning'
	);
	await conflictWarning.waitFor();

	await expect(conflictWarning).toBeVisible();

	const conflictIcon = page.locator(
		'.publication-timeline .change-tracking-conflict-icon-warning'
	);
	await conflictIcon.first().waitFor();

	await expect(conflictIcon).toHaveCount(2);
});

test('LPD-26155 Production conflict info is visible when new changes have been made to production', async ({
	changeTrackingPage,
	ctCollection,
	documentLibraryEditFolderPage,
	documentLibraryPage,
	page,
	site,
}) => {
	await changeTrackingPage.workOnProduction();

	await documentLibraryPage.goto(site.friendlyUrlPath);

	const folderTitle3 = getRandomString();

	await documentLibraryPage.goToEditFolder(folderTitle1);

	await documentLibraryEditFolderPage.fillTitle(folderTitle3);

	await page.getByRole('button', {name: 'Save'}).click();

	await waitForAlert(page, 'Success:Your request completed successfully.');

	await changeTrackingPage.workOnPublication(ctCollection);

	await documentLibraryPage.goToEditFolder(folderTitle2);

	const prodConflictIcon = page.locator(
		'.change-tracking-conflict-icon-danger'
	);
	await prodConflictIcon.waitFor();

	await expect(prodConflictIcon).toBeVisible();

	await prodConflictIcon.hover();

	const prodConflictText = page.getByText('Production Conflict');
	await prodConflictText.waitFor();

	await expect(prodConflictText).toBeVisible();
});

test('LPD-37842 Timeline icon is yellow for cross-publication edits.', async ({
	apiHelpers,
	changeTrackingPage,
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
	await documentLibraryPage.goToEditFolder(folderTitle1);

	const timelineButton = page.locator('.change-tracking-timeline-button svg');
	await timelineButton.waitFor();

	await expect(timelineButton).toHaveCSS('color', 'rgb(255, 182, 141)');

	await apiHelpers.headlessChangeTracking.deleteCTCollection(
		ctCollection2.body.id
	);
});

test('LPD-39412 Assert publication timeline history is enabled for structures', async ({
	journalStructuresPage,
	page,
	site,
}) => {
	await journalStructuresPage.goto(site.friendlyUrlPath);

	await page.getByRole('link', {name: 'Add'}).click();

	await page
		.getByLabel('Press enter to add Text field.')
		.dragTo(page.getByText('Drag fields from the sidebar'));
	await page.getByPlaceholder('Untitled Structure').pressSequentially(title1);

	await page.getByRole('button', {name: 'Save'}).click();

	await page.waitForTimeout(500);

	await journalStructuresPage.goto(site.friendlyUrlPath);

	const timelineButton = page.getByLabel('timeline-button');
	await timelineButton.waitFor();
	await timelineButton.click();

	const timelineActionsButton = page.locator('.publication-timeline button');

	await expect(timelineActionsButton).toBeVisible();

	await page.getByText('Description').click();

	await page.getByRole('link', {name: title1}).click();

	await page.waitForTimeout(500);

	await timelineButton.click();

	await expect(timelineActionsButton).toBeVisible();
});

test('LPD-39412 Assert publication timeline history is enabled for templates', async ({
	journalEditTemplatePage,
	page,
	site,
}) => {
	await journalEditTemplatePage.goto(site.friendlyUrlPath);

	await page
		.getByPlaceholder('Untitled Template')
		.pressSequentially(title2, {delay: 50});

	await page
		.getByRole('button', {exact: true, name: 'Save and Continue'})
		.click();

	await page.waitForTimeout(500);

	const timelineButton = page.getByLabel('timeline-button');
	await timelineButton.waitFor();
	await timelineButton.click();

	const timelineActionsButton = page.locator('.publication-timeline button');

	await timelineActionsButton.waitFor();
	await expect(timelineActionsButton).toBeVisible();

	await journalEditTemplatePage.goto(site.friendlyUrlPath);

	await page.waitForTimeout(500);

	await timelineButton.waitFor();
	await timelineButton.click();

	await expect(timelineActionsButton).toBeVisible();
});

test('LPD-73283 Conflict warning is visible when content is edited in more than one publication', async ({
apiHelpers,
    changeTrackingPage,
    ctCollection,
    journalEditArticlePage,
    journalPage,
    page,
    site,
    workflowPage,
}) => {
    await changeTrackingPage.workOnProduction();

    let journalArticleTitle1= getRandomString();

    await journalEditArticlePage.goto();

    await journalEditArticlePage.fillTitle(journalArticleTitle1);

    await journalEditArticlePage.publishArticle();

    await waitForAlert(page, `Success:${journalArticleTitle1} was created successfully.`);

    const incompleteCTCollection =
        await apiHelpers.headlessChangeTracking.createCTCollection(
            getRandomString()
        );

    await changeTrackingPage.workOnPublication(incompleteCTCollection);

    await workflowPage.goto();
    await workflowPage.changeWorkflow(
        'Web Content Article',
        'Single Approver'
    );

    await journalPage.goto();

    await journalEditArticlePage.editArticle(journalArticleTitle1);

    await journalEditArticlePage.fillTitle(getRandomString());
    
    await page.getByRole('button', { name: 'Submit for Workflow' }).click();

    await changeTrackingPage.workOnPublication(ctCollection);

    await journalPage.goto();

    await journalEditArticlePage.editArticle(journalArticleTitle1);

    let journalArticleTitle2 = getRandomString();

    await journalEditArticlePage.fillTitle(journalArticleTitle2);

    await journalEditArticlePage.publishArticle();

    await waitForAlert(page, `Success:${journalArticleTitle1} was updated successfully.`);

});