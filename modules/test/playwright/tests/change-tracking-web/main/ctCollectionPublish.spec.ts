/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {applicationsMenuPageTest} from '../../../fixtures/applicationsMenuPageTest';
import {changeTrackingPagesTest} from '../../../fixtures/changeTrackingPagesTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {pagesAdminPagesTest} from '../../../fixtures/pagesAdminPagesTest';
import {productMenuPageTest} from '../../../fixtures/productMenuPageTest';
import getRandomString from '../../../utils/getRandomString';
import {performLoginViaApi, performLogout} from '../../../utils/performLogin';
import getBasicWebContentStructureId from '../../../utils/structured-content/getBasicWebContentStructureId';
import {waitForAlert} from '../../../utils/waitForAlert';
import {blogsPagesTest} from '../../blogs-web/main/fixtures/blogsPagesTest';
import {journalPagesTest} from '../../journal-web/main/fixtures/journalPagesTest';

export const test = mergeTests(
	applicationsMenuPageTest,
	isolatedSiteTest,
	apiHelpersTest,
	blogsPagesTest,
	changeTrackingPagesTest,
	journalPagesTest,
	pagesAdminPagesTest,
	pageEditorPagesTest,
	productMenuPageTest
);

test('LPD-42499 Assert correct message appears in Checking changes page', async ({
	applicationsMenuPage,
	changeTrackingPage,
	ctCollection,
	page,
	pageEditorPage,
	pagesAdminPage,
	productMenuPage,
	site,
}) => {
	await changeTrackingPage.workOnPublication(ctCollection);

	await applicationsMenuPage.goToSite(site.name);

	const layoutTitle = getRandomString();

	await productMenuPage.openProductMenuIfClosed();

	await productMenuPage.goToPages();

	await pagesAdminPage.createNewPage({
		draft: true,
		name: layoutTitle,
		template: 'Blank',
	});

	await pageEditorPage.publishPage();

	await changeTrackingPage.goToReviewChanges(ctCollection.body.name);

	await page.reload();

	await page.getByRole('link', {name: 'Publish'}).click();

	await expect(
		page.getByText(
			'Publishing may overwrite changes made in production after this Publication was created.'
		)
	).toBeVisible();
});

test('Cannot publish empty ctCollection', async ({
	blogsEditBlogEntryPage,
	changeTrackingPage,
	ctCollection,
	page,
}) => {
	await changeTrackingPage.workOnPublication(ctCollection);

	await blogsEditBlogEntryPage.goto();

	const title = getRandomString();
	const content = getRandomString();

	await blogsEditBlogEntryPage.editBlogEntry({
		content,
		publish: true,
		title,
	});

	await changeTrackingPage.workOnProduction();

	await blogsEditBlogEntryPage.goto();

	await blogsEditBlogEntryPage.editBlogEntry({
		content,
		publish: true,
		title,
	});

	await changeTrackingPage.workOnPublication(ctCollection);

	await changeTrackingPage.goToReviewChanges(ctCollection.body.name);

	await page.reload();

	await page.getByRole('link', {name: 'Publish'}).click();

	await expect(
		page.getByText('Publish: ' + ctCollection.body.name)
	).toBeVisible();

	await page
		.locator('li')
		.filter({hasText: 'Test Test added a Blogs Entry'})
		.getByRole('button')
		.click();

	const discardMenuItem = page.getByRole('menuitem', {
		name: 'Discard Change',
	});

	await discardMenuItem.click();

	const discardButton = page.getByRole('button', {name: 'Discard'});

	await discardButton.click();

	await expect(page.getByText('Checking changes')).toBeVisible();

	await expect(page.getByRole('button', {name: 'Publish'})).toBeDisabled();
});

test('Publish Parallel Publications', async ({
	apiHelpers,
	changeTrackingPage,
	ctCollection,
	journalEditArticlePage,
	journalPage,
	page,
	site,
}) => {
	await changeTrackingPage.workOnProduction();

	const folder = await apiHelpers.jsonWebServicesJournal.addFolder({
		groupId: site.id,
	});

	await changeTrackingPage.workOnPublication(ctCollection);

	await journalPage.goto(site.friendlyUrlPath);

	const folderLink = page.getByRole('link', {name: folder.name});

	await folderLink.click();

	const noWebContentWasFoundText = page.getByText(
		'No web content was found.'
	);

	await expect(noWebContentWasFoundText).toBeVisible();

	const newButton = page.getByRole('button', {name: 'New'});

	await newButton.click();

	const basicWebContentMenuItem = page.getByRole('menuitem', {
		name: 'Basic Web Content',
	});

	await basicWebContentMenuItem.click();

	const propertiesTab = page.getByRole('tab', {name: 'Properties'});

	await propertiesTab.waitFor();

	const title1 = getRandomString();

	await journalEditArticlePage.fillTitle(title1);

	await journalEditArticlePage.publishArticle();

	await waitForAlert(page, `Success:${title1} was created successfully.`);

	const ctCollection2 =
		await apiHelpers.headlessChangeTracking.createCTCollection(
			getRandomString()
		);

	await changeTrackingPage.workOnPublication(ctCollection2);

	await journalPage.goto(site.friendlyUrlPath);

	await folderLink.click();

	await expect(noWebContentWasFoundText).toBeVisible();

	await newButton.click();

	await basicWebContentMenuItem.click();

	await propertiesTab.waitFor();

	const title2 = getRandomString();

	await journalEditArticlePage.fillTitle(title2);

	await journalEditArticlePage.publishArticle();

	await waitForAlert(page, `Success:${title2} was created successfully.`);

	await apiHelpers.headlessChangeTracking.publishCTCollection(
		ctCollection.body.id
	);

	await apiHelpers.headlessChangeTracking.publishCTCollection(
		ctCollection2.body.id
	);

	await journalPage.goto(site.friendlyUrlPath);

	await folderLink.click();

	await expect(page.getByRole('link', {name: title1})).toBeVisible();

	await expect(page.getByRole('link', {name: title2})).toBeVisible();
});

test('LPD-33274 Disable Publish button after first click', async ({
	apiHelpers,
	changeTrackingPage,
	ctCollection,
	page,
	site,
}) => {
	await changeTrackingPage.workOnPublication(ctCollection);

	const basicWebContentStructureId =
		await getBasicWebContentStructureId(apiHelpers);

	const title = getRandomString();

	await apiHelpers.jsonWebServicesJournal.addWebContent({
		ddmStructureId: basicWebContentStructureId,
		groupId: site.id,
		titleMap: {en_US: title},
	});

	await changeTrackingPage.goToReviewChanges(ctCollection.body.name);

	await page.getByRole('link', {name: 'Publish'}).click();

	const button = page.locator('button:text("Publish")');

	await button.dblclick();

	await expect(
		page.getByRole('link', {name: ctCollection.body.name})
	).toBeVisible();
});

test('LPD-61155 View Publication history when CTProcess user is deleted', async ({
	apiHelpers,
	changeTrackingPage,
	ctCollection,
	page,
	site,
}) => {
	const user1 = await changeTrackingPage.addUserWithPublicationsUserRole();

	await changeTrackingPage.workOnPublication(ctCollection);

	const basicWebContentStructureId =
		await getBasicWebContentStructureId(apiHelpers);

	const title = getRandomString();

	await apiHelpers.jsonWebServicesJournal.addWebContent({
		ddmStructureId: basicWebContentStructureId,
		groupId: site.id,
		titleMap: {en_US: title},
	});

	await changeTrackingPage.addUserToPublication(
		ctCollection.body.name,
		'Admin',
		user1
	);

	await performLogout(page);

	await performLoginViaApi({page, screenName: user1.alternateName});

	await changeTrackingPage.goToReviewChanges(ctCollection.body.name);

	await page.getByRole('link', {name: 'Publish'}).click();

	await page.locator('button:text("Publish")').click();

	await changeTrackingPage.goToPublicationHistory();

	await expect(
		page.getByRole('link', {name: ctCollection.body.name})
	).toBeVisible();

	await performLogout(page);

	await performLoginViaApi({page, screenName: 'test'});

	await apiHelpers.headlessAdminUser.deleteUserAccount(Number(user1.id));

	await changeTrackingPage.goToPublicationHistory();

	await expect(
		page.getByRole('link', {name: ctCollection.body.name})
	).toBeVisible();
});
