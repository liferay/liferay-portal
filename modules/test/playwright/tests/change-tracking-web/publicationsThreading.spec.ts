/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import {createReadStream} from 'fs';
import path from 'path';

import {apiHelpersTest} from '../../fixtures/apiHelpersTest';
import {changeTrackingPagesTest} from '../../fixtures/changeTrackingPagesTest';
import {documentLibraryPagesTest} from '../../fixtures/documentLibraryPages.fixtures';
import {clickAndExpectToBeVisible} from '../../utils/clickAndExpectToBeVisible';
import {getRandomInt} from '../../utils/getRandomInt';
import getRandomString from '../../utils/getRandomString';
import {PORTLET_URLS} from '../../utils/portletUrls';
import {getTempDir} from '../../utils/temp';
import {waitForAlert} from '../../utils/waitForAlert';
import {journalPagesTest} from '../journal-web/fixtures/journalPagesTest';

export const test = mergeTests(
	apiHelpersTest,
	changeTrackingPagesTest,
	documentLibraryPagesTest,
	journalPagesTest
);

test.afterEach(async ({apiHelpers, ctCollection}) => {
	await apiHelpers.headlessChangeTracking.deleteCTCollection(ctCollection.id);
});

test('LPD-33336 Buffered increment runnable processing occurs in the same thread when not in production mode', async ({
	apiHelpers,
	attachmentsPage,
	changeTrackingPage,
	ctCollection,
	documentLibraryPage,
	page,
}) => {
	await changeTrackingPage.workOnProduction();

	await attachmentsPage.goToDocumentsAndMedia();

	const site =
		await apiHelpers.headlessAdminUser.getSiteByFriendlyUrlPath('guest');

	const file1 = await apiHelpers.headlessDelivery.postDocument(
		site.id,
		createReadStream(path.join(__dirname, '/dependencies/attachment.txt'))
	);

	const file2 = await apiHelpers.headlessDelivery.postDocument(
		site.id,
		createReadStream(path.join(__dirname, '/dependencies/attachment.txt'))
	);

	await changeTrackingPage.workOnPublication(ctCollection);

	await documentLibraryPage.selectFileEntry(file1.title);
	await documentLibraryPage.selectFileEntry(file2.title);

	const downloadPromise = page.waitForEvent('download', {timeout: 60000});
	await documentLibraryPage.downloadSelectedFileEntries();
	const download = await downloadPromise;

	const filePath = getTempDir() + download.suggestedFilename();
	await download.saveAs(filePath);
	expect(download.suggestedFilename()).toBeTruthy();

	await changeTrackingPage.workOnProduction();

	await apiHelpers.headlessDelivery.deleteDocument(file1.id);
	await apiHelpers.headlessDelivery.deleteDocument(file2.id);
});

test('LPS-117642 NoSuchTagException throws when adding a web content with tag within a Change List', async ({
	journalPage,
	page,
}) => {
	await page.goto(`/group/guest${PORTLET_URLS.tagsAdmin}`);

	await page.getByRole('link', {name: 'Add Tag'}).click();

	const tagName = getRandomString();
	await page.getByPlaceholder('Name').fill(tagName);
	await page.getByRole('button', {name: 'Save'}).click();
	await waitForAlert(page);

	await journalPage.goto();
	await journalPage.goToCreateArticle();

	await clickAndExpectToBeVisible({
		autoClick: true,
		target: page.getByRole('button', {
			name: 'Categorization',
		}),
		trigger: page.getByTitle('Actions', {exact: true}),
	});

	await journalPage.selectTag(tagName);
	await page.waitForTimeout(1000);

	const articleTitle = 'My Test ' + getRandomInt() + ' Web Content';
	await journalPage.fillArticleData(articleTitle, getRandomString());
	await journalPage.publishArticle();
});
